package com.example.demo_test_upload_image.service;

import com.example.demo_test_upload_image.model.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    List<User> getAllUser();
}
