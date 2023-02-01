package com.paulgougassian.service;

import com.paulgougassian.entity.Company;
import com.paulgougassian.entity.Customer;
import com.paulgougassian.repository.CompanyRepository;
import com.paulgougassian.repository.CustomerRepository;
import com.paulgougassian.service.ex.IllegalUserTypeException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static com.paulgougassian.security.UserType.COMPANY;
import static com.paulgougassian.security.UserType.CUSTOMER;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final String TYPE = "type";

    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final HttpServletRequest httpRequest;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String type = httpRequest.getParameter(TYPE);
        Assert.hasLength(type, "parameter: %s can't be empty!".formatted(TYPE));

        if (type.equalsIgnoreCase(COMPANY.name())) {
            return loadCompanyByUsername(email);
        }

        if (type.equalsIgnoreCase(CUSTOMER.name())) {
            return loadCustomerByUsername(email);
        }

        throw new IllegalUserTypeException("User-type: %s can't be used to login".formatted(type));
    }

    private UserDetails loadCompanyByUsername(String email) {
        Company company = companyRepository.findByEmail(email)
                                           .orElseThrow(() -> new UsernameNotFoundException(
                                                   "Unable to find company with email: %s".formatted(
                                                           email)));

        return new User(company.getUuid().toString(),
                        passwordEncoder.encode(company.getPassword()),
                        List.of(new SimpleGrantedAuthority(COMPANY.name())));

    }

    private UserDetails loadCustomerByUsername(String email) {
        Customer customer = customerRepository.findByEmail(email)
                                              .orElseThrow(() -> new UsernameNotFoundException(
                                                      "Unable to find customer with email: %s".formatted(
                                                              email)));

        return new User(customer.getUuid().toString(),
                        passwordEncoder.encode(customer.getPassword()),
                        List.of(new SimpleGrantedAuthority(CUSTOMER.name())));
    }
}
