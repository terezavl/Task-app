package com.example.taskmanagment;

import com.example.taskmanagment.controller.UserController;
import com.example.taskmanagment.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRegister() throws Exception {
        String registerDataJson = "{\"userName\":\"testuser\",\"email\":\"example1@email.com\",\"password\":\"strongPass*1\",\"confirmPassword\":\"strongPass*1\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerDataJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").value("testuser"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist());
    }

    @Test
    public void testLogin() throws Exception {
        String loginDataJson = "{\"email\":\"example@mail.com\",\"password\":\"strongPass*1\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDataJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber());
    }

    @Test
    public void testLoginInvalidCredentials() throws Exception {
        String loginDataJson = "{\"email\":\"example@mail.com\",\"password\":\"wrongPassword\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDataJson))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
    @Test
    public void testLoginEmptyCredentials() throws Exception {
        String loginDataJson = "{\"email\":\"\",\"password\":\"\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDataJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
