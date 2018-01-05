package com.senseisoft.exeniumbot.repositories;

import com.senseisoft.exeniumbot.entities.UserData;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserDataRepository extends CrudRepository<UserData, Long> {
    Optional<UserData> findByTelegramId(String telegramId);
    UserData findByTelegramUsername(String telegramUsername);
    UserData findByExeniumId(String exeniumId);
}
