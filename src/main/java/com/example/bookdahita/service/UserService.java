package com.example.bookdahita.service;

import com.example.bookdahita.models.User;

public interface UserService {
    User findByUserName(String userName);
    void registerNewUser(User user) throws Exception;
    void updateUser(User user) throws Exception;
}