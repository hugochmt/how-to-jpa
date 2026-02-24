package com.example.how_to_jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Query("FROM User user LEFT JOIN FETCH user.todos")
  List<User> findAllUsers();
}
