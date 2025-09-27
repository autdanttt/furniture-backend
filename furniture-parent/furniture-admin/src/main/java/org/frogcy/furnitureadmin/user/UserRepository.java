package org.frogcy.furnitureadmin.user;

import org.frogcy.furniturecommon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("""
        SELECT u FROM User u
        WHERE u.email = :email
        AND u.deleted = false
""")
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email= ?1 AND u.deleted = false")
    public User getUserByEmail(String email);
}
