package com.peecko.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LabelRepo extends JpaRepository {

    List<LabelRepo> findByNameAndLang();

}
