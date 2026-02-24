package com.example.how_to_jpa;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {

  private final TodoRepository todoRepository;
  private final UserRepository userRepository;

  @Transactional
  public void updateUserName() {
    var user = userRepository.findById(1L).orElseThrow();
    user.setName("Toto");
    userRepository.save(user);
    this.updateTodoUser2();
    user.setAge(30);
  }

  @Transactional
  public void getUsersTodos() {
    var users = userRepository.findAll();
    for (User user : users) {
      user.getTodos().size();
    }
  }

  @Transactional()
  public void updateTodoUser() {
    var todo = new Todo();
    User user = userRepository.findById(1L).orElseThrow();
    todo.setUser(user);
    this.todoRepository.save(todo);
  }

  @Transactional
  public void updateTodoUser2() {
    var todo = new Todo();
    User user = userRepository.getReferenceById(154L);
    user.getAge();
    todo.setUser(user);
    this.todoRepository.save(todo);
  }

  public List<Todo> createList(Todo todo1, Todo todo2) {
    List<Todo> list = new ArrayList<>();
    list.add(todo1);
    list.add(todo2);

    return list;
  }
}
