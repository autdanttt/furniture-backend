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

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.DATE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTests {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired private TestEntityManager entityManager;
    private static final Random random = new Random();
    @Test
    public void testCreateCustomer() {
        Role role = entityManager.find(Role.class, 7);

        Customer customer = new Customer();

        customer.setPassword("$2a$12$0J4wiyUcAJrgU1HScl5iO.xaGuMEGqLcA46iB8mGBZoQV89Kg.Shi");
        customer.setFirstName(HO.get(random.nextInt(HO.size())) + " " +
                TEN_DEM.get(random.nextInt(TEN_DEM.size())));
        customer.setLastName(TEN.get(random.nextInt(TEN.size())));
        customer.setEmail("tuandat.email001@gmail.com");
        customer.setAvatarUrl("https://res.cloudinary.com/dm8tfyppk/image/upload/v1751335909/avatar/7e2fb45c-ad62-4327-8aff-c0c02d8c8154.jpg");
        customer.setEnabled(true);
        customer.setGender(Gender.MALE);

        Instant fakeInstant = Instant.parse("2025-09-21T04:02:00Z");

        ZoneId vnZone = ZoneId.of("Asia/Ho_Chi_Minh");

        Clock fixedClock = Clock.fixed(fakeInstant, vnZone);

        Date fakeDate = Date.from(Instant.now(fixedClock));
        customer.setCreatedAt(fakeDate);
        customer.setRoles(Set.of(role));
        customer.setPhoneNumber(randomPhone());
        customer.setVerified(true);
        Customer saved = customerRepository.save(customer);

        assertThat(saved.getEmail()).isNotEqualTo(null);
    }
    private static String randomEmail(String firstName, String lastName) {
        String base = (lastName + "." + firstName)
                .toLowerCase()
                .replaceAll("[^a-z]", ""); // bỏ dấu, bỏ ký tự lạ
        int num = 100 + random.nextInt(900);
        String[] domains = {"gmail.com", "yahoo.com", "outlook.com", "icloud.com"};
        return base + num + "@" + domains[random.nextInt(domains.length)];
    }
    private static final String[] VIETNAM_PREFIXES = {
            "032", "033", "034", "035", "036", "037", "038", "039", // Viettel
            "070", "079", "077", "076", "078",                     // MobiFone
            "081", "082", "083", "084", "085",                     // VinaPhone
            "056", "058",                                           // Vietnamobile
            "059"                                                   // Gmobile
    };
    private static final List<String> HO = List.of("Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Võ", "Đặng");
    private static final List<String> TEN_DEM = List.of("Văn", "Thị", "Hữu", "Ngọc", "Minh", "Tuấn", "Thanh", "Anh");
    private static final List<String> TEN = List.of("An", "Bình", "Châu", "Dũng", "Hà", "Hằng", "Lan", "Linh", "Nam", "Phong", "Tú", "Trang");

    private static final String[] PREFIXES = {"032","033","034","035","036","037","038","039","070","079","077","076","078","081","082","083","084","085"};

    public static String randomName() {
        return HO.get(random.nextInt(HO.size())) + " " +
                TEN_DEM.get(random.nextInt(TEN_DEM.size())) + " " +
                TEN.get(random.nextInt(TEN.size()));
    }
    public static String randomPhone() {
        String prefix = PREFIXES[random.nextInt(PREFIXES.length)];
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < 7; i++) sb.append(random.nextInt(10));
        return sb.toString();
    }
}
