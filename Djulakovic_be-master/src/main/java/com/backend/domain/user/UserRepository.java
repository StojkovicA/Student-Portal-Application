package com.backend.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ListCrudRepository<User, Long> {

    @Query(value = "SELECT * " +
            "FROM users " +
            "WHERE email LIKE :email", nativeQuery = true)
    User findByEmail(String email);

    @Query(value = "SELECT * " +
            "FROM users " +
            "WHERE (CONCAT(firstName, ' ', lastName) LIKE :quickSearch OR indeks LIKE :quickSearch)",
            countQuery = "SELECT COUNT(*) " +
                    "FROM users " +
                    "WHERE (CONCAT(firstName, ' ', lastName) LIKE :quickSearch OR indeks LIKE :quickSearch)", nativeQuery = true)
    Page<User> findAllDistinctByQuickSearch(String quickSearch, Pageable pageRequest);


    @Modifying
    @Query(nativeQuery = true, value = "UPDATE users SET firebase_token = :firebaseToken WHERE id = :userId")
    void updateFirebaseToken(Long userId, String firebaseToken);
}
