package com.shivasiva.student.management.system.service;
import com.shivasiva.student.management.system.model.User;
import com.shivasiva.student.management.system.model.Staff;
import com.shivasiva.student.management.system.repository.UserRepository;
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
    private StaffRepository staffRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final HashMap<String, String> otpMap = new HashMap<>();
    private final HashMap<String, User> pendingUsers = new HashMap<>();

    public boolean registerUser(User user) {
        String email = user.getEmail().trim();

        // STAFF signup validation
        if ("STAFF".equalsIgnoreCase(user.getRole())) {
            Optional<Staff> staffOpt = staffRepository.findByEmailIgnoreCase(email);

            if (staffOpt.isEmpty()) {
                System.out.println("❌ Staff not found: " + email);
                return false;
            }

            Staff staff = staffOpt.get();

            if (staff.isRegistered()) {
                System.out.println("❌ Staff already registered: " + email);
                return false;
            }

            // Prevent already verified users from signing up again
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                if (existingUser.get().isVerified()) {
                    System.out.println("❌ Email already registered as verified user: " + email);
                    return false;
                }
                userRepository.delete(existingUser.get()); // remove stale unverified user
            }
        } else {
            // Non-staff: Block if already used
            if (userRepository.findByEmail(email).isPresent()) {
                System.out.println("❌ Email already used: " + email);
                return false;
            }
        }

        // Save new user (temporarily unverified)
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(false);

        String otp = generateOtp();
        otpMap.put(user.getUsername(), otp);
        pendingUsers.put(user.getUsername(), user);

        emailService.sendOtp(email, otp);
        return true;
    }



    public boolean verifyOtp(String username, String otp) {
        String storedOtp = otpMap.get(username);

        if (storedOtp != null && storedOtp.equals(otp)) {
            User user = pendingUsers.get(username);
            if (user == null) return false;

            user.setVerified(true);
            userRepository.save(user);

            // If staff, mark registered
            if ("STAFF".equalsIgnoreCase(user.getRole())) {
                Optional<Staff> staffOpt = staffRepository.findByEmailIgnoreCase(user.getEmail());
                staffOpt.ifPresent(staff -> {
                    staff.setRegistered(true);
                    staff.setPassword(user.getPassword()); // Save encoded password
                    staffRepository.save(staff);
                });
            }

            // Clean up
            otpMap.remove(username);
            pendingUsers.remove(username);
            return true;
        }

        return false;
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public User login(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username.trim());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(password, user.getPassword()) && user.isVerified()) {
                return user;
            }
        }
        return null;
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email.trim()).isPresent();
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username.trim()).isPresent();
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username.trim());
    }


    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email.trim());
    }

    public void deleteUnverifiedUser(String username) {
        otpMap.remove(username);
        pendingUsers.remove(username);
        userRepository.findByUsername(username).ifPresent(user -> {
            if (!user.isVerified()) {
                userRepository.delete(user);
            }
        });
    }
}




