package com.api.freelancer.service;

import com.api.freelancer.model.Users;
import com.api.freelancer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public List<Users> findAll() {
        return userRepository.findAll();
    }

    public Users save(Users users) {
        return userRepository.save(users);
    }
}
