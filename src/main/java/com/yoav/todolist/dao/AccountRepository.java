package com.yoav.todolist.dao;

import com.yoav.todolist.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>, IAccountDao {

    @Override
    default void add(Account account) {
        saveAndFlush(account);
    }

    @Query(value = "SELECT CASE WHEN COUNT(*) = 1 THEN true ELSE false END FROM Account WHERE username = ?1 AND password = ?2")
    @Override
    boolean isExistByUsernameAndPassword(String username, String password);

    @Query(value = "SELECT CASE WHEN COUNT(*) = 1 THEN true ELSE false END FROM Account WHERE username = ?1")
    @Override
    boolean isExistByUsername(String username);

    @Override
    @Query(value = "SELECT * FROM accounts WHERE accounts.username = ?1", nativeQuery = true)
    @Transactional
    Optional<Account> findByUsername(String username);

}
