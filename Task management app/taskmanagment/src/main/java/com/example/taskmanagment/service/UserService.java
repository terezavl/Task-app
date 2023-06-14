package com.example.taskmanagment.service;

import com.example.taskmanagment.model.DTOs.*;
import com.example.taskmanagment.model.entities.User;
import com.example.taskmanagment.model.exceptions.BadRequestException;
import com.example.taskmanagment.model.exceptions.UnauthorizedException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService extends AbstractService{
    @Autowired
    private BCryptPasswordEncoder encoder;
    public UserWithoutPassDTO register(final RegisterDTO registerData) {
        checkPasswordMatch(registerData.getPassword(), registerData.getConfirmPassword());
        if(userRepository.existsByEmail(registerData.getEmail())){
            throw new BadRequestException("User with this email already exists.");
        }
        final User user=mapper.map(registerData,User.class);
        user.setPassword(encoder.encode(registerData.getPassword()));
        userRepository.save(user);
        return mapper.map(user, UserWithoutPassDTO.class);
    }

    public UserWithoutPassDTO login(final LoginDTO loginData) {
        final Optional<User> opt1= userRepository.findByEmail(loginData.getEmail());
        if(opt1.isEmpty()){
            throw new UnauthorizedException("Wrong credentials.");
        }
        final User user= opt1.get();
        checkEncodedPassword(loginData.getPassword(), user.getPassword());
        return mapper.map(user, UserWithoutPassDTO.class);
    }

    public UserWithoutPassDTO editUser(final long userId, final EditUserDTO editData) {
        final User user = findUserById(userId);
        final String email = editData.getEmail();
        if(email != null){
            if(userRepository.existsByEmail(email)){
                throw new BadRequestException("User with this email already exists.");
            }
            user.setEmail(email);
        }
        if(editData.getUserName() != null){
            user.setUserName(editData.getUserName());
        }
        if(editData.getPassword()!= null){
            checkPasswordMatch(editData.getPassword(), editData.getConfirmPassword());
            user.setPassword(encoder.encode(editData.getPassword()));
        }
        userRepository.save(user);
        return mapper.map(user, UserWithoutPassDTO.class);
    }
    @Transactional
    public void deleteAccount(final long loggedId, UserPassDTO passDTO) {
        final User user = findUserById(loggedId);
        checkEncodedPassword(passDTO.getPassword(), user.getPassword());
        userRepository.deleteUserById(loggedId);
    }

    public void checkEncodedPassword(String rawPass, String encodedPass){
        if(!encoder.matches(rawPass, encodedPass)){
            throw new UnauthorizedException("Wrong credentials.");
        }
    }
    private void checkPasswordMatch(String pass, String confirmPass){
        if(!pass.equals(confirmPass)){
            throw new BadRequestException("Password mismatch.");
        }
    }
}
