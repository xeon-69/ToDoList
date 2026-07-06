package com.xeon.todolist.repository;

import com.xeon.todolist.entity.Tasks;
import com.xeon.todolist.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface TaskRepository extends JpaRepository<Tasks, Long> {

    @Query("SELECT t FROM Tasks t JOIN t.user u WHERE u.username = :username")
    Page<Tasks> findTasksByUsername (@Param("username") String username, Pageable pageable);

    boolean existsByTitleAndUser(String title, Users user);
}
