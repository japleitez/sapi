package com.peecko.api.repository;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.enumeration.Lang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApsUserRepo extends JpaRepository<ApsUser, Long> {

    Optional<ApsUser> findByUsername(String username);

    boolean existsByUsername(String username);

    @Modifying
    @Query("UPDATE ApsUser a SET a.license = :license WHERE a.username = :username")
    void setLicense(@Param("username") String username, @Param("license") String license);

    @Modifying
    @Query("UPDATE ApsUser a SET a.active = :active WHERE a.username = :username")
    void setActive(@Param("username") String username, @Param("active") Boolean active);

    @Modifying
    @Query("UPDATE ApsUser a SET a.lang = :lang, a.updated = CURRENT_TIMESTAMP WHERE a.username = :username")
    void setLanguage(@Param("username") String username, @Param("lang") Lang lang);

    @Modifying
    @Query("UPDATE ApsUser a SET a.password = :password WHERE a.username = :username")
    void setPassword(@Param("username") String username, @Param("password") String password);


    @Modifying
    @Query("UPDATE ApsUser a SET a.name = :name WHERE a.username = :username")
    void setName(@Param("username") String username, @Param("name") String name);
}
