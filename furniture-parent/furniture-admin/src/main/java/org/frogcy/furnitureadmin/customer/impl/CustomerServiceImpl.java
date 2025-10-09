package org.frogcy.furnitureadmin.customer.impl;

import org.frogcy.furnitureadmin.customer.CustomerNotFoundException;
import org.frogcy.furnitureadmin.customer.CustomerRepository;
import org.frogcy.furnitureadmin.customer.CustomerService;
import org.frogcy.furnitureadmin.customer.dto.CustomerMapper;
import org.frogcy.furnitureadmin.customer.dto.CustomerResponseDTO;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public PageResponseDTO<CustomerResponseDTO> getCustomers(int page, int size, String sortField, String sortDir, String keyword) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Customer> customerPage = customerRepository.search(keyword, pageable);
        List<CustomerResponseDTO> customers = customerPage.getContent()
                .stream().map(customer -> {
                    CustomerResponseDTO customerDTO = customerMapper.toDTO(customer);
                    return customerDTO;
                }).toList();
        return new PageResponseDTO<>(
                customers,
                customerPage.getNumber(),
                customerPage.getSize(),
                customerPage.getTotalElements(),
                customerPage.getTotalPages()
        );
    }

    @Override
    public void changeEnabled(Integer customerId, boolean enabled) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with id: " + customerId)
        );
        customer.setEnabled(enabled);
        customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(Integer id, Integer customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with id: " + customerId)
        );
        customer.setDeleted(true);
        customer.setDeletedAt(new Date());
        customer.setDeletedById(id);
        customerRepository.save(customer);
    }
}
