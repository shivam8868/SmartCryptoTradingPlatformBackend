package com.tradingplatform.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tradingplatform.modal.User;
import com.tradingplatform.repository.UserRepository;

// this service is for loading user details for authentication and authorization purposes in Spring Security. It implements the UserDetailsService interface, which is a core interface in Spring Security for retrieving user-related data. The loadUserByUsername method is overridden to fetch user details based on the provided username (in this case, email) and return a UserDetails object that Spring Security can use for authentication and authorization decisions.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: ");  // or +username
        }
        List<GrantedAuthority> authorityList = new ArrayList<>();

        
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorityList);
    }


}
