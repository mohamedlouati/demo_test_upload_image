package com.example.demo_test_upload_image.repo;

import com.example.demo_test_upload_image.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}

