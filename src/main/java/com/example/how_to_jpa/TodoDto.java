package com.example.how_to_jpa;

public record TodoDto(Long id, String description, String username) {

  public TodoDto(Todo todo) {
    this(todo.getId(), todo.getDescription(), todo.getUser().getName());
  }
}
