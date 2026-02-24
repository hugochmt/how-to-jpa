package com.example.how_to_jpa;

import java.util.List;

public record UserDto(Long id, String name, List<Long> todoId) {
  public UserDto(User user) {
    this(user.getId(), user.getName(), user.getTodos().stream().map(Todo::getId).toList());
  }
}
