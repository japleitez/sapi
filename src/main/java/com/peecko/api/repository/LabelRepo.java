package com.peecko.api.repository;

import com.peecko.api.domain.Label;
import com.peecko.api.domain.enumeration.Lang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LabelRepo extends JpaRepository<Label, Long> {

    Optional<Label> findByCodeAndLang(String code, Lang lang);

}
