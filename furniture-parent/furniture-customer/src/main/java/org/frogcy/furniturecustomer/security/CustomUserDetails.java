package org.frogcy.furniturecustomer.security;

import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.Role;
import org.frogcy.furniturecommon.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }
    public CustomUserDetails(Customer customer) {
       this.customer = customer;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = customer.getRoles();
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for(Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return customer.getPassword();
    }

    @Override
    public String getUsername() {
        return customer.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return customer.isEnabled() && customer.isVerified();
    }
}
