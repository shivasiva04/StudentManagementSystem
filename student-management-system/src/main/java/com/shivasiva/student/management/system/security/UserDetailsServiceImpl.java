package com.shivasiva.student.management.system.security;

import com.shivasiva.student.management.system.model.User;
import com.shivasiva.student.management.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        User user;

        // Login using either email or username
        if (input.contains("@")) {
            user = userRepository.findByEmail(input.trim())
                    .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));
        } else {
            user = userRepository.findByUsername(input.trim())
                    .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
        }

        if (!user.isVerified()) {
            throw new UsernameNotFoundException("User account not verified");
        }

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), // Uses username internally
                user.getPassword(),
                List.of(authority)
        );
    }

}

