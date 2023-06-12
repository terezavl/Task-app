package com.example.taskmanagment;

import static com.example.taskmanagment.Util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;

import com.example.taskmanagment.model.DTOs.*;
import com.example.taskmanagment.model.entities.User;
import com.example.taskmanagment.model.exceptions.BadRequestException;
import com.example.taskmanagment.model.exceptions.NotFoundException;
import com.example.taskmanagment.model.exceptions.UnauthorizedException;
import com.example.taskmanagment.model.repositories.UserRepository;
import com.example.taskmanagment.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder encoder;
    @Mock
    private ModelMapper mapper;
    @InjectMocks
    private UserService userService;


    @Test
    public void registerUserSuccessful() {
        RegisterDTO registerData = new RegisterDTO(NAME,EMAIL, PASS, PASS);
        User user = getUser();
        UserWithoutPassDTO expectedUser = getUserWithoutPass();

        when(userRepository.existsByEmail(registerData.getEmail())).thenReturn(false);
        when(mapper.map(registerData, User.class)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mapper.map(user, UserWithoutPassDTO.class)).thenReturn(expectedUser);

        UserWithoutPassDTO result = userService.register(registerData);

        assertEquals(expectedUser, result);
    }

    @Test
    public void registerUserPasswordMismatch() {
        RegisterDTO registerData = new RegisterDTO(NAME,EMAIL,PASS,"wrongPass");

        assertThrows(BadRequestException.class, () -> {
            userService.register(registerData);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    public void registerUserExistingEmail() {
        RegisterDTO registerData = new RegisterDTO(NAME,EMAIL,PASS,PASS);

        when(userRepository.existsByEmail(registerData.getEmail())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            userService.register(registerData);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    public void testEditUserSuccessful() {
        EditUserDTO editUserDTO = new EditUserDTO ("newName", "newemail@example.com", "newPassword1*", "newPassword1*");
        User user = Util.getUser();
        UserWithoutPassDTO expectedUser = new UserWithoutPassDTO(USER_ID,"newName", "newemail@example.com");

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(editUserDTO.getEmail())).thenReturn(false);
        when(encoder.encode(editUserDTO.getPassword())).thenReturn("encodedPassword");
        when(mapper.map(user, UserWithoutPassDTO.class)).thenReturn(expectedUser);

        UserWithoutPassDTO result = userService.editUser(USER_ID, editUserDTO);

        assertEquals(expectedUser, result);

        verify(userRepository, times(1)).findUserById(USER_ID);
        verify(userRepository, times(1)).existsByEmail(editUserDTO.getEmail());
        verify(userRepository, times(1)).save(user);
        verify(encoder, times(1)).encode(editUserDTO.getPassword());
        verifyNoMoreInteractions(userRepository, encoder);
    }
    @Test
    public void testEditUserPasswordSuccessful() {
        EditUserDTO editUserDTO = new EditUserDTO(null, null, "newPassword1*", "newPassword1*");
        User user = Util.getUser();
        UserWithoutPassDTO expectedUser = new UserWithoutPassDTO(user.getId(),user.getUserName(), user.getEmail());

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
        when(encoder.encode(editUserDTO.getPassword())).thenReturn("encodedPassword");
        when(mapper.map(user, UserWithoutPassDTO.class)).thenReturn(expectedUser);

        UserWithoutPassDTO result = userService.editUser(USER_ID, editUserDTO);

        assertEquals(expectedUser, result);

        verify(userRepository, times(1)).findUserById(USER_ID);
        verify(encoder, times(1)).encode(editUserDTO.getPassword());
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository, encoder);
    }

    @Test
    public void testEditUserPasswordMismatch() {
        EditUserDTO editUserDTO = new EditUserDTO();
        editUserDTO.setPassword("newPassword");
        editUserDTO.setConfirmPassword("differentPassword");

        User user = Util.getUser();

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userService.editUser(USER_ID, editUserDTO));

        assertEquals("Password mismatch.", exception.getMessage());

        verify(userRepository, never()).save(any());
        verify(userRepository, times(1)).findUserById(USER_ID);
        verifyNoMoreInteractions(userRepository, encoder);
    }
    @Test
    public void testEditUserEmail() {
        EditUserDTO editUserDTO = new EditUserDTO();
        editUserDTO.setEmail("new-email@example.com");
        User user = Util.getUser();
        UserWithoutPassDTO expectedUser = new UserWithoutPassDTO(user.getId(),user.getUserName(), editUserDTO.getEmail());

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(editUserDTO.getEmail())).thenReturn(false);
        when(mapper.map(user, UserWithoutPassDTO.class)).thenReturn(expectedUser);

        UserWithoutPassDTO result = userService.editUser(USER_ID, editUserDTO);

        assertEquals(expectedUser, result);

        verify(userRepository, times(1)).findUserById(USER_ID);
        verify(userRepository, times(1)).existsByEmail(editUserDTO.getEmail());
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository, encoder);
    }

    @Test
    public void testEditUserEmailExists() {
        EditUserDTO editUserDTO = new EditUserDTO();
        editUserDTO.setEmail("existing-email@example.com");

        User existingUser = Util.getUser();

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(editUserDTO.getEmail())).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userService.editUser(USER_ID, editUserDTO));

        assertEquals("User with this email already exists.", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    public void loginUserSuccessful() {
        LoginDTO loginData = getLoginDTO();
        User user = getUser();
        UserWithoutPassDTO expectedUser = getUserWithoutPass();

        when(userRepository.findByEmail(loginData.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches(loginData.getPassword(), user.getPassword())).thenReturn(true);
        when(mapper.map(user, UserWithoutPassDTO.class)).thenReturn(expectedUser);

        UserWithoutPassDTO result = userService.login(loginData);

        assertEquals(expectedUser, result);
    }

    @Test
    public void loginUserNotFound() {
        LoginDTO loginData = getLoginDTO();

        when(userRepository.findByEmail(loginData.getEmail())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.login(loginData);
        });
        assertEquals("Wrong credentials.",exception.getMessage());
    }

    @Test
    public void deleteUserSuccessful() {
        UserPassDTO passDTO = new UserPassDTO(PASS);
        User user = getUser();

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
        when(encoder.matches(passDTO.getPassword(), user.getPassword())).thenReturn(true);
        assertDoesNotThrow(() -> userService.checkEncodedPassword(passDTO.getPassword(), user.getPassword()));

        userService.deleteAccount(USER_ID, passDTO);

        verify(userRepository, times(1)).findUserById(USER_ID);
        verify(userRepository, times(1)).deleteUserById(USER_ID);
    }

    @Test
    public void deleteUserUnsuccessful() {
        UserPassDTO passDTO = new UserPassDTO("wrong pass");
        User user = getUser();

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
        when(encoder.matches(passDTO.getPassword(), user.getPassword())).thenReturn(false);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> userService.deleteAccount(USER_ID, passDTO));
        assertEquals("Wrong password.", exception.getMessage());

        verify(userRepository, never()).deleteUserById(USER_ID);
    }
}

