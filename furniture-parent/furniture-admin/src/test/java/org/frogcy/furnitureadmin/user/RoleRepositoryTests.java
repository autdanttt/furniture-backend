package org.frogcy.furnitureadmin.user;

import org.frogcy.furniturecommon.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testCreateFirstRole(){
    }
//    @Test
//    public void testCreateUserRole() {
//        Role customer = Role.builder().name("ROLE_CUSTOMER").description("Default role for customers with basic access to the system.").build();
//        roleRepository.save(customer);
//    }
    @Test
    public void testCreateMultipleRoles(){
        Role admin = Role.builder().name("ROLE_ADMIN").description("Manages entire system.").build();
        Role productManager = Role.builder().name("ROLE_PRODUCT_MANAGER").description("Updates product info, pricing.").build();
        Role inventoryManager = Role.builder().name("ROLE_INVENTORY_MANAGER").description("Manages stock, warehouse.").build();
        Role orderManager = Role.builder().name("ROLE_ORDER_MANAGER").description("Processes and tracks customer orders.").build();
        Role shipper = Role.builder()
                .name("ROLE_SHIPPER")
                .description("Delivers orders to customers.")
                .build();
        Role assistant = Role.builder().name("ROLE_ASSISTANT").description("Supports customers, handles issues.").build();
        Role customer = Role.builder().name("ROLE_CUSTOMER").description("Default role for customers with basic access to the system.").build();
        roleRepository.saveAll(List.of(admin, productManager, inventoryManager, orderManager, shipper, assistant, customer));
    }
}
