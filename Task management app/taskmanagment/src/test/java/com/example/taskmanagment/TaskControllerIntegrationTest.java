package com.example.taskmanagment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateTask() throws Exception {
        // Login first
        String loginDataJson = "{\"email\":\"example@mail.com\",\"password\":\"strongPass*1\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDataJson))
                .andReturn();

        // Get the session from the previous login request
        MockHttpSession session = (MockHttpSession) mockMvc.perform(MockMvcRequestBuilders.get("/users/session"))
                .andReturn().getRequest().getSession();

        // Set the required session attribute
        session.setAttribute("LOGGED_ID", 2L);

        // Create a task
        String createTaskJson = "{\"title\":\"Task 1\",\"description\":\"Task description\",\"isFinished\":false}";
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTaskJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Task 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Task description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isFinished").value(false));
    }
}

