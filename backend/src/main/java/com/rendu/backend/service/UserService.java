package com.rendu.backend.service;

import com.rendu.backend.models.User;

import java.util.List;

public interface UserService {
    User createUser(User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    void deleteUser(Long id);

    User logIn(String email, String password);
}
