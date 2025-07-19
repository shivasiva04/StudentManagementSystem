//package com.shivasiva.student.management.system.security;
//
//import com.shivasiva.student.management.system.model.User;
//import com.shivasiva.student.management.system.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.*;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByUsername(username.trim())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        if (!user.isVerified()) {
//            throw new UsernameNotFoundException("User not verified");
//        }
//
//        // Ensure role is prefixed with ROLE_ as required by Spring Security
//        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase());
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPassword(),
//                List.of(authority)
//        );
//    }
//}


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

        // Detect if input is email
        if (input.contains("@")) {
            user = userRepository.findByEmail(input.trim())
                    .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
        } else {
            user = userRepository.findByUsername(input.trim())
                    .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        }

        if (!user.isVerified()) {
            throw new UsernameNotFoundException("User not verified");
        }

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),  // always use username internally
                user.getPassword(),
                List.of(authority)
        );
    }
}
