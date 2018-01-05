package com.senseisoft.exeniumbot.repositories;

import com.senseisoft.exeniumbot.entities.Language;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;


public interface LanguageRepository extends CrudRepository<Language, Long> {
    Optional<Language> findByCode(String code);
}
