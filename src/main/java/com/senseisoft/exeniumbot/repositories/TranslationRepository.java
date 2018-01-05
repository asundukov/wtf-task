package com.senseisoft.exeniumbot.repositories;

import com.senseisoft.exeniumbot.entities.Language;
import com.senseisoft.exeniumbot.entities.Translation;
import java.util.List;
import org.springframework.data.repository.CrudRepository;


public interface TranslationRepository extends CrudRepository<Translation, Long> {
    List<Translation> findByLanguage(Language language);
}
