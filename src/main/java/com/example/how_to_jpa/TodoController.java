package com.example.how_to_jpa;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TodoController {

  private final TodoService todoService;
  private final TodoRepository todoRepository;
  private final UserRepository userRepository;

  @GetMapping("/todos")
  List<TodoDto> getTodos() {
    return todoRepository.findAll().stream().map(TodoDto::new).toList();
  }

  @GetMapping("/users")
  List<UserDto> getUsers() {
    return userRepository.findAll().stream().map(UserDto::new).toList();
  }

  @GetMapping("1")
  void endpoint1() {
    this.todoService.updateUserName();
  }

  @GetMapping("2")
  void endpoint2() {
    this.todoService.getUsersTodos();
  }

  @GetMapping("3")
  void endpoint3() {
    this.todoService.updateTodoUser();
  }

  @GetMapping("3.2")
  void endpoint3_2() {
    this.todoService.updateTodoUser2();
  }


}
