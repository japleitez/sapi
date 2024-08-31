package com.peecko.api.repository;

import com.peecko.api.domain.TodayVideo;
import com.peecko.api.domain.enumeration.Lang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodayVideoRepo extends JpaRepository<TodayVideo, Long> {

    TodayVideo findFirstByLanguageOrderByReleaseDateDesc(Lang lang);

}
