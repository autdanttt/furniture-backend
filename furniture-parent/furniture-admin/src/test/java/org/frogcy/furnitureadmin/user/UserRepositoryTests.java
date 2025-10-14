package org.frogcy.furnitureadmin.user;


import org.frogcy.furniturecommon.entity.Role;
import org.frogcy.furniturecommon.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

//    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired private TestEntityManager entityManager;


    @Test
    public void testCreateUser() {
        Role role = entityManager.find(Role.class, 1);

        User user = new User();
        user.setEmail("admin@admin.com");
        user.setPassword("$2a$12$0J4wiyUcAJrgU1HScl5iO.xaGuMEGqLcA46iB8mGBZoQV89Kg.Shi");
        user.setFirstName("Mr");
        user.setLastName("Admin");
        user.setAvatarUrl("https://res.cloudinary.com/dm8tfyppk/image/upload/v1751335909/avatar/7e2fb45c-ad62-4327-8aff-c0c02d8c8154.jpg");
        user.setEnabled(true);
        user.setRoles(Set.of(role));
        user.setCreatedAt(new Date());
        User saved = userRepository.save(user);

        assertThat(saved.getEmail()).isEqualTo("admin@admin.com");
    }

}
