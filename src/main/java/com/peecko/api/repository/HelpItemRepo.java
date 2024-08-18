package com.peecko.api.repository;

import com.peecko.api.domain.HelpItem;
import com.peecko.api.domain.enumeration.Lang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HelpItemRepo extends JpaRepository<HelpItem, Long> {

    List<HelpItem> findByLang(Lang lang);

}
