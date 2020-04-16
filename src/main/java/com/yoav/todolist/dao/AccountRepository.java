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

    @Query(value = "SELECT * FROM accounts WHERE accounts.username = ?1 AND accounts.password = ?2", nativeQuery = true)
    @Transactional
    Optional<Account> isExistByUsernameAndPasswordQuery(String username, String password);

    @Query(value = "SELECT * FROM accounts WHERE accounts.username = ?1", nativeQuery = true)
    @Transactional
    Optional<Account> isExistByUsernameQuery(String username);

    @Override
    @Query(value = "SELECT * FROM accounts WHERE accounts.username = ?1", nativeQuery = true)
    @Transactional
    Optional<Account> findByUsername(String username);

    @Override
    default Account update(Account account) {
        return save(account);
    }

    @Override
    default boolean isExistByUsernameAndPassword(String username, String password) {
        Optional<Account> account = isExistByUsernameAndPasswordQuery(username, password);
        return account.isPresent();
    }

    @Override
    default boolean isExistByUsername(String username) {
        Optional<Account> account = isExistByUsernameQuery(username);
        return account.isPresent();
    }

}
