//package com.shivasiva.student.management.system.repository;
//
//import com.shivasiva.student.management.system.model.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface UserRepository extends JpaRepository<User, Long> {
//    Optional<User> findByUsername(String username);
//
//}


package com.shivasiva.student.management.system.repository;

import com.shivasiva.student.management.system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
