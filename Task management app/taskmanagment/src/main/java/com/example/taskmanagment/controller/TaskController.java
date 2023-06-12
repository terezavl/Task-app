package com.example.taskmanagment.controller;

import com.example.taskmanagment.model.DTOs.CreateTaskDTO;
import com.example.taskmanagment.model.DTOs.EditTaskDTO;
import com.example.taskmanagment.model.DTOs.TaskInfoDTO;
import com.example.taskmanagment.model.DTOs.TaskWithoutOwnerDTO;
import com.example.taskmanagment.service.TaskService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.time.LocalDateTime;

@RestController
public class TaskController extends AbstractController{
    @Autowired
    private TaskService taskService;

    @PostMapping("/tasks")
    public TaskInfoDTO createTask(@Valid @RequestBody final CreateTaskDTO createData, final HttpSession s){
        final long userId=getLoggedId(s);
        return taskService.createTask(userId, createData);
    }
    @PutMapping ("/tasks/{id}")
    public TaskInfoDTO editTask(@Valid @RequestBody EditTaskDTO editData, @PathVariable("id")final long taskId, final HttpSession s){
        final long userId=getLoggedId(s);
        return taskService.editTask(userId,taskId, editData);
    }
    @GetMapping("/users/tasks")
    public Page<TaskWithoutOwnerDTO> getUserTasks( @RequestParam (defaultValue = "0") final int page,
                                                   @RequestParam (defaultValue = "10") final int size, final HttpSession s){
        final long userId=getLoggedId(s);
        return taskService.getUserTasks(userId, page, size);
    }
    @GetMapping("/tasks/{id}")
    public TaskInfoDTO getTaskById(@PathVariable ("id") final long taskId, final HttpSession s){
        //todo final
        final long userId = getLoggedId(s);
        return taskService.getTaskById(taskId, userId);
    }
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable ("id") final int taskId,  final HttpSession s){
        final long userId = getLoggedId(s);
        taskService.deleteTask(userId, taskId);
        return ResponseEntity.ok("Task deleted successfully.");
    }

    @GetMapping("/users/tasks/csv")
    public ResponseEntity<StreamingResponseBody> exportUnfinishedTasksCsv(final HttpSession session) {
        final long userId = getLoggedId(session);
        InputStream csvStream = taskService.exportUnfinishedTasks(userId);
        StreamingResponseBody responseBody = FileUtil.streamingResponse(csvStream);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/csv");
        headers.add("Content-Disposition", "attachment; filename=unfinished_tasks_"+ LocalDateTime.now()+".csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseBody);
    }

}
