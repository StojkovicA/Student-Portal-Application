package com.backend.domain.token;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends ListCrudRepository<Token, Long> {

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM tokens WHERE token LIKE :token")
    void deleteAllByToken(String token);
}
