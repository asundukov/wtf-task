package com.senseisoft.exeniumbot.repositories;

import com.senseisoft.exeniumbot.entities.Stat;
import com.senseisoft.exeniumbot.entities.UserData;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;


public interface StatRepository extends CrudRepository<Stat, Long> {
    Optional<Stat> findByUserDataAndStatId(UserData userData, String statId);
    List<Stat> findByUserDataOrderByCreationTimeDesc(UserData userData);
}
