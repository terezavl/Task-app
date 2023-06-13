package com.example.taskmanagment;

import com.example.taskmanagment.model.DTOs.LoginDTO;
import com.example.taskmanagment.model.DTOs.RegisterDTO;
import com.example.taskmanagment.model.DTOs.UserWithoutPassDTO;
import com.example.taskmanagment.model.entities.Task;
import com.example.taskmanagment.model.entities.User;

import java.util.Collections;

public abstract class Util {
    public static final String NAME = "John";
    public static final String EMAIL = "email@examp.com";
    public static final String PASS = "StrongPass*8";
    public static final String TITLE = "Title";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final long USER_ID = 1;
    public static final long TASK_ID = 1;

    public static LoginDTO getLoginDTO(){
        return new LoginDTO(EMAIL, PASS);
    }
    public static UserWithoutPassDTO getUserWithoutPass(){
        return new UserWithoutPassDTO(USER_ID, NAME, EMAIL);
    }
    public static User getUser (){
        return new User(USER_ID,NAME,EMAIL, PASS, Collections.emptySet());
    }
    public static Task getTask(){
        Task task = new Task();
        task.setTitle(TITLE);
        task.setDescription(DESCRIPTION);
        task.setIsFinished(false);
        task.setId(TASK_ID);
        task.setPriority(1);
        return task;
    }

}
