//package com.shivasiva.student.management.system.service;
//
//import com.shivasiva.student.management.system.model.User;
//import com.shivasiva.student.management.system.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Optional;
//import java.util.Random;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private EmailService emailService;
//
//    private HashMap<String, String> otpMap = new HashMap<>();
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    public boolean registerUser(User user) {
//        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
//            return false;
//        }
//
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setVerified(false);
//
//        String otp = generateOtp();
//        otpMap.put(user.getUsername(), otp);
//        emailService.sendOtp(user.getEmail(), otp);
//
//        userRepository.save(user);
//        return true;
//    }
//
//
//    public boolean verifyOtp(String username, String otp) {
//        Optional<User> optionalUser = userRepository.findByUsername(username);
//        String storedOtp = otpMap.get(username);
//
//        if (optionalUser.isPresent() && storedOtp != null && storedOtp.equals(otp)) {
//            User user = optionalUser.get();
//            user.setVerified(true);
//            userRepository.save(user);
//            otpMap.remove(username);
//            System.out.println("OTP verified for: " + username);
//            return true;
//        }
//
//        System.out.println("OTP verification failed for: " + username);
//        return false;
//    }
//
//    public User login(String username, String password) {
//        Optional<User> optionalUser = userRepository.findByUsername(username.trim());
//
//        if (optionalUser.isPresent()) {
//            User user = optionalUser.get();
//
//            System.out.println("Login attempt for: " + username);
//            System.out.println("Stored password: " + user.getPassword());
//            System.out.println("Input password: " + password);
//            System.out.println("Is verified: " + user.isVerified());
//
//            if (user.getPassword().equals(password.trim()) && user.isVerified()) {
//                System.out.println("Login successful!");
//                return user;
//            } else {
//                System.out.println("Login failed: Incorrect password or not verified.");
//            }
//        } else {
//            System.out.println("Login failed: User not found.");
//        }
//
//        return null;
//    }
//
//    private String generateOtp() {
//        return String.format("%06d", new Random().nextInt(999999));
//    }
//}


package com.shivasiva.student.management.system.service;

import com.shivasiva.student.management.system.model.User;
import com.shivasiva.student.management.system.repository.UserRepository;
import com.shivasiva.student.management.system.model.Staff;
import com.shivasiva.student.management.system.repository.StaffRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private HashMap<String, String> otpMap = new HashMap<>();

    public boolean registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return false; // Username already exists
        }

        // Role-based logic: only allow staff signup if email is already added by admin
        if ("STAFF".equalsIgnoreCase(user.getRole())) {
            Optional<Staff> staffOpt = staffRepository.findByEmail(user.getEmail());
            if (staffOpt.isEmpty()) {
                System.out.println("Staff email not found in pre-approved list");
                return false;
            }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(false);

        // Send OTP
        String otp = generateOtp();
        otpMap.put(user.getUsername(), otp);
        emailService.sendOtp(user.getEmail(), otp);

        userRepository.save(user);
        return true;
    }

    public boolean verifyOtp(String username, String otp) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        String storedOtp = otpMap.get(username);
        if (optionalUser.isPresent() && storedOtp != null && storedOtp.equals(otp)) {
            User user = optionalUser.get();
            user.setVerified(true);
            userRepository.save(user);
            otpMap.remove(username);
            return true;
        }
        return false;
    }

    public User login(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username.trim());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(password.trim()) && user.isVerified()) {
                return user;
            }
        }
        return null;
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
