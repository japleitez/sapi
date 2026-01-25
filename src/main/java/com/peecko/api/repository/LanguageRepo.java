package com.peecko.api.repository;

import com.peecko.api.domain.Language;
import com.peecko.api.domain.enumeration.Lang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LanguageRepo extends JpaRepository<Language, String> {

    List<Language> findByActiveTrueOrderByName();

    Language findLanguageByCode(String code);

}
