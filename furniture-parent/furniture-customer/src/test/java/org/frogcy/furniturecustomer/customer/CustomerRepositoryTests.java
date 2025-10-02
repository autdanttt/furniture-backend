package org.frogcy.furniturecustomer.customer;

import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.Gender;
import org.frogcy.furniturecommon.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTests {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired private TestEntityManager entityManager;
    @Test
    public void testCreateCustomer() {
        Role role = entityManager.find(Role.class, 7);

        Customer customer = new Customer();
        customer.setEmail("user2@gmail.com");
        customer.setPassword("$2a$12$0J4wiyUcAJrgU1HScl5iO.xaGuMEGqLcA46iB8mGBZoQV89Kg.Shi");
        customer.setFirstName("Mr");
        customer.setLastName("User2");
        customer.setAvatarUrl("https://res.cloudinary.com/dm8tfyppk/image/upload/v1751335909/avatar/7e2fb45c-ad62-4327-8aff-c0c02d8c8154.jpg");
        customer.setEnabled(true);
        customer.setGender(Gender.MALE);
        customer.setCreatedAt(new Date());
        customer.setRoles(Set.of(role));
        customer.setCreatedAt(new Date());
        customer.setPhoneNumber("0356459052");
        customer.setVerified(true);
        Customer saved = customerRepository.save(customer);

        assertThat(saved.getEmail()).isEqualTo("user2@gmail.com");
    }
}
