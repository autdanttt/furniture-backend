package org.frogcy.furnitureadmin.customer;

import org.frogcy.furnitureadmin.customer.dto.CustomerResponseDTO;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;

public interface CustomerService {
    PageResponseDTO<CustomerResponseDTO> getCustomers(int page, int size, String sortField, String sortDir, String keyword);

    void changeEnabled(Integer customerId, boolean enabled);

    void deleteCustomer(Integer id, Integer customerId);
}
