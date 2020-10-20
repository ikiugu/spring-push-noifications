package com.ikiugu.notifications.repositories;

import com.ikiugu.notifications.models.User;
import com.ikiugu.notifications.models.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * created by alfred.ikiugu on 20-Oct-20
 */

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {
    Weather findByUser(User user);
}
