package com.example.taskmanagment.service;

import com.example.taskmanagment.model.DTOs.CreateTaskDTO;
import com.example.taskmanagment.model.DTOs.EditTaskDTO;
import com.example.taskmanagment.model.DTOs.TaskInfoDTO;
import com.example.taskmanagment.model.DTOs.TaskWithoutOwnerDTO;
import com.example.taskmanagment.model.entities.Task;
import com.example.taskmanagment.model.entities.User;
import com.example.taskmanagment.model.exceptions.NotFoundException;
import com.example.taskmanagment.model.exceptions.UnauthorizedException;
import com.example.taskmanagment.model.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class TaskService extends AbstractService{
    @Autowired
    private TaskRepository taskRepository;
    public TaskInfoDTO createTask(final long userId, final CreateTaskDTO createData) {
        final User user=findUserById(userId);
        final Task task = mapper.map(createData,Task.class);
        if (createData.getPriority() != null ) {
            task.setPriority(createData.getPriority());
        }
        task.setUser(user);
        task.setIsFinished(false);
        taskRepository.save(task);
        return mapper.map(task, TaskInfoDTO.class);
    }

    public TaskInfoDTO editTask(final long userId, final long taskId, final EditTaskDTO editData) {
        findUserById(userId);
        final Task task=findTaskById(taskId);
        checkTaskOwner(userId, task.getUser().getId());
        if (editData.getTitle() != null) {
            task.setTitle(editData.getTitle());
        }
        if (editData.getDescription() != null ) {
            task.setDescription(editData.getDescription());
        }
        if (editData.getIsFinished() != null ) {
            task.setIsFinished(editData.getIsFinished());
        }
        if (editData.getPriority() != null ) {
            task.setPriority(editData.getPriority());
        }
        taskRepository.save(task);
        return mapper.map(task, TaskInfoDTO.class);
    }

    public Page<TaskWithoutOwnerDTO> getUserTasks(final long id, final int page, final int size) {
        findUserById(id);
        final Pageable pageable = PageRequest.of(page,size, Sort.by("priority"));
        return taskRepository.findAllByUser(id, pageable,0)
                .map(task -> mapper.map(task, TaskWithoutOwnerDTO.class));
    }

    public TaskInfoDTO getTaskById(final long taskId, final long userId) {
        findUserById(userId);
        final Task task = findTaskById(taskId);
        checkTaskOwner(userId, task.getUser().getId());
        return mapper.map(task, TaskInfoDTO.class);
    }

    private Task findTaskById(final long id){
        return taskRepository.findTaskById(id).orElseThrow(() -> new NotFoundException("There is no such task."));
    }
    public void checkTaskOwner(final long userId, final long taskUserId){
        if(taskUserId != userId){
            throw new UnauthorizedException("You are not the owner of this task.");
        }
    }

    public void deleteTask(final long userId, final long taskId) {
        findUserById(userId);
        final Task task = findTaskById(taskId);
        checkTaskOwner(userId, task.getUser().getId());
        taskRepository.delete(task);
    }
    public InputStream exportUnfinishedTasks(final long userId) {
        findUserById(userId);
        final List<Task> tasks = taskRepository.findTasksByUser(userId, 0);
        tasks.sort((t1, t2) -> t1.getPriority() - t2.getPriority());
        final ByteArrayOutputStream csvContent = FileUtil.writeTasksCSV(tasks);
        return new ByteArrayInputStream(csvContent.toByteArray());
    }
}
