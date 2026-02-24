---
theme: default
layout: intro
backgroundSize: 90%
fonts:
  sans: 'Inter'
  mono: 'JetBrains Mono'
---

# **SpringBoot - Tips JPA**

---
layout: image-right
image: resources/jpa.png
backgroundSize: contain
---

## **Qu'est-ce que Spring Data JPA ?**

Spring Data JPA est une abstraction au-dessus de JPA (Java Persistence API) qui simplifie l'accès aux données.

**Avantages clés :**

- Réduction du code boilerplate
- Requêtes dérivées des noms de méthodes
- Support natif des transactions
- Intégration transparente avec Spring Boot

---
layout: center
---

# **Quiz Time !**

---

## **Question #1**

```markdown
Table users :

| id | name       |
|----|------------|
| 1  | 'John Doe' |
```

Quel est le nom en base après l'appel de cette méthode ?

```java
public void updateUserName() {
  var user = userRepository.findById(1L).orElseThrow();
  user.setName("Toto");
}
```

- `John Doe`
- `Toto`

---

## **Question #2**

Combien d'appels en base ?

```java
@Entity
public class User {
  //...
  @OneToMany(mappedBy = "user") // fetch = FetchType.LAZY
  private List<Todo> todos;
}

@Transactional
public void getUsersTodos() {
  var users = userRepository.findAll();
  for (User user : users) {
    user.getTodos().size();
  }
}
```

- 1
- 2
- N+1 (1 + nombre d'utilisateurs)

---

## **Question #3**

Quelle différence entre ces deux approches ?

```java
@Transactional
public void updateTodoUser() {
  User user = userRepository.findById(1L);
  var todo = new Todo();
  todo.setUser(user);
  this.todoRepository.save(todo);
}

@Transactional
public void updateTodoUser() {
  User user = userRepository.getReferenceById(1L);
  var todo = new Todo();
  todo.setUser(user);
  this.todoRepository.save(todo);
}
```

- Aucune différence
- L'approche 2 fait 1 requête SQL en moins
- L'approche 1 est plus rapide

---

## **Repository pattern**

```java
// CrudRepository - opérations CRUD basiques
public interface UserRepository extends CrudRepository<User, Long> {
}

// JpaRepository - plus complet (flush, batch operations)
public interface UserRepository extends JpaRepository<User, Long> {
}

// PagingAndSortingRepository - pagination et tri
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
}
```

Utiliser `JpaRepository` par défaut, c'est le plus complet.

---

## **Derived query**

```java
public interface UserRepository extends JpaRepository<User, Long> {

  // Génère automatiquement : SELECT * FROM user WHERE email = ?
  User findByEmail(String email);

  // WHERE age > ? AND active = ?
  List<User> findByAgeGreaterThanAndActiveTrue(int age);

  // WHERE last_name LIKE ? ORDER BY first_name
  List<User> findByLastNameContainingOrderByFirstName(String lastName);
}
```

⚠️ Dans les cas où les noms peuvent devenir très longs : privilégier `@Query`.

```java
Long countAllByStatusInAndPatientReimbursementReimbursementTypeInAndInvoiceNumberIsNotNullAndInvoiceIsNullAndShippingDateBefore(
        List<OrderStatusEnum> statuses,
        List<ReimbursementTypeEnum> reimbursementTypes,
        OffsetDateTime offsetDateTime);
```

---

## **@Query - Requêtes personnalisées**

### JPQL vs SQL Natif

```java
// JPQL - recommandé (indépendant de la BDD)
@Query("SELECT u FROM User u WHERE u.status = :status")
List<User> findByStatus(@Param("status") String status);

// SQL Natif - pour des cas spécifiques
@Query(value = "SELECT * FROM users WHERE created_at > NOW() - INTERVAL 7 DAY", nativeQuery = true)
List<User> findRecentUsers();
```

---

## **Gestion des transactions**

- Rollback automatique si exception
- La transaction la plus "haute" défini le début et la fin de la transaction
- Nécessaire pour déclencher des appels en base dynamiques

```java

@Service
public class UserService {

  @Transactional
  public void createUserWithOrders(User user, List<Order> orders) {
    userRepository.save(user);
    orderRepository.saveAll(orders);
  }

  @Transactional(readOnly = true)
  public User getUser(Long id) {
    return userRepository.findById(id).orElseThrow();
  }
}
```

`readOnly = true` optimise les lectures et évite les flush inutiles.

---

## **Le Problème N+1 - Le piège le plus fréquent !**

❌ MAUVAIS - Génère N+1 requêtes

```java

@OneToMany(mappedBy = "user") // Lazy
private List<Order> orders;

List<User> users = userRepository.findAll();
for(
User user :users){
        user.

getOrders().

size(); // Requête SQL pour chaque user !
}
```

✅ BON - Une seule requête avec JOIN FETCH

```java

@Query("SELECT u FROM User u LEFT JOIN FETCH u.orders")
List<User> findAllWithOrders();
```

ℹ️ Activez `spring.jpa.show-sql=true` en dev pour détecter ces problèmes.

---

## **Projections**

### Réduire la quantité de données chargées

```java
// Interface-based projection
public interface UserSummary {
  String getName();

  String getEmail();
}

List<UserSummary> findByActiveTrue();

// Class-based projection (DTO)
@Query("SELECT new com.example.UserDTO(u.id, u.name) FROM User u")
List<UserDTO> findAllDTOs();

// Dynamic projection
<T> List<T> findByEmail(String email, Class<T> type);
```

---

## **Utilisation de `getReferenceById`**

- Renvoie un objet proxy n'ayant que l'id d'initialisé.
- Pas d'appel en base tant qu'aucun des champs n'est requeté
- Utile pour assigner un objet dans une relation basée sur l'id

```java
Todo todo = new Todo();
todo.setUser(userRepository.getReferenceById(userId));
todoRepository.save(todo);
```

---

## **Auditing**

```java

@EntityListeners(AuditingEntityListener.class)
@Entity
public class User {

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  @CreatedBy
  private String createdBy;

  @LastModifiedBy
  private String lastModifiedBy;
}
```

---

## **Resources**

- Blog Vlad Mihalcea : https://vladmihalcea.com/
- Doc SpringBoot : https://spring.io/projects/spring-data-jpa
- Javadoc hibernate
- Performance oriented Spring Data JPA & Hibernate : https://www.youtube.com/watch?v=exqfB1WaqIw