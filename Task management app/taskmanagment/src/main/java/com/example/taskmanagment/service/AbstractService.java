package com.example.taskmanagment.service;

import com.example.taskmanagment.model.entities.User;
import com.example.taskmanagment.model.exceptions.NotFoundException;
import com.example.taskmanagment.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractService {
    @Autowired
    protected ModelMapper mapper;
    @Autowired
    protected UserRepository userRepository;
    public User findUserById(long id){
        return  userRepository.findUserById(id).orElseThrow(() -> new NotFoundException("There is no such user."));
    }
}
