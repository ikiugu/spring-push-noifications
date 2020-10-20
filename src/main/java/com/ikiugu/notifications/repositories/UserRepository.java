package com.ikiugu.notifications.repositories;

import com.ikiugu.notifications.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * created by alfred.ikiugu on 20-Oct-20
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);
}
