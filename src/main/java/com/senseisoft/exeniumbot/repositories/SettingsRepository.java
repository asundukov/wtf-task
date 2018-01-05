package com.senseisoft.exeniumbot.repositories;

import com.senseisoft.exeniumbot.entities.Settings;
import org.springframework.data.repository.CrudRepository;

public interface SettingsRepository extends CrudRepository<Settings, Long> {
}
