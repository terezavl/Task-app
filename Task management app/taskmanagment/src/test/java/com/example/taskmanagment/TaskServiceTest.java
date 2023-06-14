package com.example.taskmanagment;

import static com.example.taskmanagment.Util.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

import com.example.taskmanagment.model.DTOs.*;
import com.example.taskmanagment.model.entities.Task;
import com.example.taskmanagment.model.entities.User;
import com.example.taskmanagment.model.exceptions.NotFoundException;
import com.example.taskmanagment.model.exceptions.UnauthorizedException;
import com.example.taskmanagment.model.repositories.TaskRepository;
import com.example.taskmanagment.model.repositories.UserRepository;
import com.example.taskmanagment.service.TaskService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper mapper;
    @InjectMocks
    private TaskService taskService;

    @Test
    public void createTaskSuccessful() {
        CreateTaskDTO createData = new CreateTaskDTO(TITLE,DESCRIPTION,1);
        User user = Util.getUser();
        Task task = Util.getTask();
        task.setUser(user);

        TaskInfoDTO expected = new TaskInfoDTO(TASK_ID, new UserWithoutPassDTO(user.getId(),user.getUserName(),user.getEmail()),
                task.getTitle(), task.getDescription(), task.getIsFinished(), task.getPriority());

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(mapper.map(createData, Task.class)).thenReturn(task);
        when(mapper.map(task, TaskInfoDTO.class)).thenReturn(expected);

        TaskInfoDTO result = taskService.createTask(USER_ID, createData);

        assertEquals(expected, result);
    }
    @Test
    public void createTaskUnsuccessful() {
        CreateTaskDTO createData = new CreateTaskDTO(TITLE,DESCRIPTION,1);

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            taskService.createTask(USER_ID, createData);
        });
        verify(taskRepository, never()).save(any());
    }

    @Test
    public void editTaskSuccessful() {
        EditTaskDTO editData = new EditTaskDTO(TITLE, DESCRIPTION, false, 2);
        User user = Util.getUser();
        Task task = Util.getTask();
        task.setUser(user);
        TaskInfoDTO expectedTask = new TaskInfoDTO();
        expectedTask.setTitle(editData.getTitle());
        expectedTask.setDescription(editData.getDescription());
        expectedTask.setIsFinished(editData.getIsFinished());
        expectedTask.setPriority(editData.getPriority());
        expectedTask.setUser(getUserWithoutPass());

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
        when(taskRepository.findTaskById(TASK_ID)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(mapper.map(task, TaskInfoDTO.class)).thenReturn(expectedTask);

        TaskInfoDTO result = taskService.editTask(1, 1, editData);

        assertEquals(expectedTask, result);
    }
    @Test
    public void testEditTaskUnsuccessful() {
        long taskId = 2;
        EditTaskDTO editData = new EditTaskDTO(TITLE, DESCRIPTION, false,2);

        User user = Util.getUser();

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
        when(taskRepository.findTaskById(taskId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            taskService.editTask(USER_ID, taskId, editData);
        });
        assertEquals("There is no such task.", exception.getMessage());
        verify(taskRepository, Mockito.never()).save(any());
    }

    @Test
    public void getUserUnfinishedTasksSuccessful() {
        int page = 1;
        int size = 10;
        TaskWithoutOwnerDTO taskWithoutOwnerDTO = new TaskWithoutOwnerDTO(2, TITLE, DESCRIPTION,1);
        List<Task> tasks = new ArrayList<>();
        User user = Util.getUser();
        Task task = Util.getTask();
        task.setUser(user);
        tasks.add(task);
        Page<Task> taskPage = new PageImpl<>(tasks);
        Pageable pageable = PageRequest.of(page,size, Sort.by("priority"));

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
        when(taskRepository.findAllByUser(USER_ID, pageable, 0)).thenReturn(taskPage);
        when(mapper.map(task, TaskWithoutOwnerDTO.class)).thenReturn(taskWithoutOwnerDTO);

        Page<TaskWithoutOwnerDTO> result = taskService.getUserTasks(USER_ID, page, size);

        assertEquals(1, result.getTotalElements());
        assertEquals(taskWithoutOwnerDTO, result.getContent().get(0));
    }
    @Test
    public void getTaskByIdSuccessful() {
        User user = Util.getUser();
        Task task = Util.getTask();
        task.setUser(user);
        TaskInfoDTO expected = new TaskInfoDTO();
        expected.setId(TASK_ID);
        expected.setTitle(TITLE);
        expected.setDescription(DESCRIPTION);
        expected.setIsFinished(false);
        expected.setPriority(1);

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
        when(taskRepository.findTaskById(TASK_ID)).thenReturn(Optional.of(task));
        when(mapper.map(task, TaskInfoDTO.class)).thenReturn(expected);

        TaskInfoDTO result = taskService.getTaskById(TASK_ID, USER_ID);

        assertEquals(expected, result);
    }
    @Test
    public void getTaskByIdUnSuccessful() {
        User user = Util.getUser();
        user.setId(5);
        Task task = Util.getTask();
        task.setUser(Util.getUser());

        when(userRepository.findUserById(5)).thenReturn(Optional.of(user));
        when(taskRepository.findTaskById(TASK_ID)).thenReturn(Optional.of(task));

        assertThrows(UnauthorizedException.class, () -> taskService.getTaskById( task.getId(), user.getId()));

        verify(taskRepository, Mockito.never()).save(task);
    }

    @Test
    public void deleteTaskSuccessful() {
        User user = Util.getUser();
        Task task = Util.getTask();
        task.setUser(user);

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
        when(taskRepository.findTaskById(TASK_ID)).thenReturn(Optional.of(task));

        assertDoesNotThrow(() -> taskService.deleteTask(USER_ID, TASK_ID));

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    public void deleteTaskUnsuccessful() {
        User user = Util.getUser();
        user.setId(5);
        Task task = Util.getTask();
        task.setUser(Util.getUser());

        when(userRepository.findUserById(5)).thenReturn(Optional.of(user));
        when(taskRepository.findTaskById(TASK_ID)).thenReturn(Optional.of(task));

        assertThrows(UnauthorizedException.class, () -> taskService.deleteTask(user.getId(), task.getId()));

        verify(taskRepository, Mockito.never()).delete(task);
    }

    @Test
    public void getUnfinishedTasksSuccessful(){
        User user = Util.getUser();
        List<Task> tasks = Arrays.asList(
                new Task(1, "Task 1", "Description 1", false,1, user),
                new Task(2, "Task 2", "Description 2", false,2, user)
        );

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
        when(taskRepository.findTasksByUser(USER_ID, 0)).thenReturn(tasks);

        InputStream expected = new ByteArrayInputStream("TaskId;Title;Description;Priority;IsFinished\n1;Task 1;Description 1;1;false\n2;Task 2;Description 2;2;false\n".getBytes());

        InputStream result = taskService.exportUnfinishedTasks(USER_ID);

        assertNotNull(result);
        Assertions.assertThat(result).hasSameContentAs(expected);

        IOUtils.closeQuietly(expected);
        IOUtils.closeQuietly(result);
    }

    @Test
    public void testExportNonExistingUser() {

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.exportUnfinishedTasks(USER_ID));

        verify(userRepository, times(1)).findUserById(USER_ID);
        verifyNoInteractions(taskRepository);
    }

    @Test
    public void testExportNoUnfinishedTasks() {
        User user = Util.getUser();

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
        when(taskRepository.findTasksByUser(USER_ID, 0)).thenReturn(Collections.emptyList());

        InputStream expectedInputStream = new ByteArrayInputStream("TaskId;Title;Description;Priority;IsFinished\n".getBytes());

        InputStream result = taskService.exportUnfinishedTasks(USER_ID);

        Assertions.assertThat(result).hasSameContentAs(expectedInputStream);

        verify(userRepository, times(1)).findUserById(USER_ID);
        verify(taskRepository, times(1)).findTasksByUser(USER_ID, 0);

        IOUtils.closeQuietly(expectedInputStream);
        IOUtils.closeQuietly(result);
    }

}

