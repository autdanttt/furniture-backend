package org.frogcy.furnitureadmin.user;


import org.frogcy.furniturecommon.entity.Role;
import org.frogcy.furniturecommon.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;
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

    @Autowired
    private RoleRepository roleRepository;


    @Test
    public void testCreateUser() {
        Role role = entityManager.find(Role.class, 1);

        User user = new User();
        user.setEmail("admin@shop.com");
        user.setPassword("$2a$12$0J4wiyUcAJrgU1HScl5iO.xaGuMEGqLcA46iB8mGBZoQV89Kg.Shi");
        user.setFirstName("Super");
        user.setLastName("Admin");
        user.setAvatarUrl("https://res.cloudinary.com/dm8tfyppk/image/upload/v1751335909/avatar/7e2fb45c-ad62-4327-8aff-c0c02d8c8154.jpg");
        user.setEnabled(true);
        user.setRoles(Set.of(role));
        user.setCreatedAt(new Date());
        User saved = userRepository.save(user);

        assertThat(saved.getEmail()).isEqualTo("admin@admin.com");
    }

    @Test
    public void testCreateMultipleUsersWithDifferentRolesUsingBuilder() {
        // --- Giai đoạn 1: Lấy tất cả các role cần thiết ---
        Role adminRole = roleRepository.findById(1).get();
        Role productManagerRole = roleRepository.findById(2).get();
        Role inventoryManagerRole = roleRepository.findById(3).get();
        Role orderManagerRole = roleRepository.findById(4).get();
        Role shipperRole = roleRepository.findById(5).get();
        Role assistantRole = roleRepository.findById(6).get();

        String encodedPassword = "$2a$12$0J4wiyUcAJrgU1HScl5iO.xaGuMEGqLcA46iB8mGBZoQV89Kg.Shi"; // Mật khẩu đã mã hóa

        // --- Giai đoạn 2: Tạo các User bằng Builder ---
//
//        User admin = User.builder()
//                .email("admin@shop.com")
//                .password(encodedPassword)
//                .firstName("Super")
//                .lastName("Admin")
//                .avatarUrl("https://res.cloudinary.com/dm8tfyppk/image/upload/v1751335909/avatar/7e2fb45c-ad62-4327-8aff-c0c02d8c8154.jpg")
//                .enabled(true)
//                .roles(Set.of(adminRole))
//                .build();
//        admin.setCreatedAt(new Date());

        User productManager = User.builder()
                .email("product.manager@shop.com")
                .password(encodedPassword)
                .firstName("Product")
                .lastName("Manager")
                .avatarUrl("https://res.cloudinary.com/dm8tfyppk/image/upload/v1751335909/avatar/7e2fb45c-ad62-4327-8aff-c0c02d8c8154.jpg")
                .enabled(true)
                .roles(Set.of(productManagerRole))
                .build();
        productManager.setCreatedAt(new Date());
        productManager.setCreatedById(1);

        User inventoryManager = User.builder()
                .email("inventory.manager@shop.com")
                .password(encodedPassword)
                .firstName("Inventory")
                .lastName("Manager")
                .avatarUrl("https://res.cloudinary.com/dm8tfyppk/image/upload/v1751335909/avatar/7e2fb45c-ad62-4327-8aff-c0c02d8c8154.jpg")
                .enabled(true)
                .roles(Set.of(inventoryManagerRole))
                .build();
        inventoryManager.setCreatedAt(new Date());
        inventoryManager.setCreatedById(1);

        User orderManager = User.builder()
                .email("order.manager@shop.com")
                .password(encodedPassword)
                .firstName("Order")
                .lastName("Manager")
                .avatarUrl("https://res.cloudinary.com/dm8tfyppk/image/upload/v1751335909/avatar/7e2fb45c-ad62-4327-8aff-c0c02d8c8154.jpg")
                .enabled(true)
                .roles(Set.of(orderManagerRole))
                .build();
        orderManager.setCreatedAt(new Date());
        orderManager.setCreatedById(1);

        User shipper = User.builder()
                .email("shipper@shop.com")
                .password(encodedPassword)
                .firstName("Super")
                .lastName("Shipper")
                .avatarUrl("https://res.cloudinary.com/dm8tfyppk/image/upload/v1751335909/avatar/7e2fb45c-ad62-4327-8aff-c0c02d8c8154.jpg")
                .enabled(true)
                .roles(Set.of(shipperRole))
                .build();
        shipper.setCreatedAt(new Date());
        shipper.setCreatedById(1);

        User assistant = User.builder()
                .email("assistant@shop.com")
                .password(encodedPassword)
                .firstName("Helpful")
                .lastName("Assistant")
                .avatarUrl("https://res.cloudinary.com/dm8tfyppk/image/upload/v1751335909/avatar/7e2fb45c-ad62-4327-8aff-c0c02d8c8154.jpg")
                .enabled(true)
                .roles(Set.of(assistantRole))
                .build();
        assistant.setCreatedAt(new Date());
        assistant.setCreatedById(1);

        // --- Giai đoạn 3: Lưu tất cả User vào DB ---
        userRepository.saveAll(List.of(productManager, inventoryManager, orderManager, shipper, assistant));

        // --- Giai đoạn 4: Kiểm tra kết quả ---
        long userCount = userRepository.count();
        assertThat(userCount).isGreaterThanOrEqualTo(6); // Kiểm tra xem có ít nhất 5 user đã được tạo

        User foundAssistant = userRepository.findByEmail("assistant@shop.com").get();
        assertThat(foundAssistant.getFirstName()).isEqualTo("Helpful");
        assertThat(foundAssistant.getRoles()).contains(assistantRole);
    }


}
