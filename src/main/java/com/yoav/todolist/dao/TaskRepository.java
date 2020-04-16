package com.yoav.todolist.dao;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer>, ITaskDao {

    @Override
    default void add(Task task, Account account) {
        task.setAccount(account);
        saveAndFlush(task);
    }

    @Override
    default Optional<Task> getById(int id) {
        return findById(id);
    }

    @Override
    default List<Task> getAll() {
        return findAll();
    }

    @Query(value = "DELETE FROM tasks WHERE tasks.account_id = ?1", nativeQuery = true)
    @Transactional
    @Modifying
    @Override
    void deleteAllByAccountId(int id);

}