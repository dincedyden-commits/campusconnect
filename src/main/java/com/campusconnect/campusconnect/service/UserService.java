package com.campusconnect.campusconnect.service;

import com.campusconnect.campusconnect.dto.RegisterRequest;
import com.campusconnect.campusconnect.dto.UserResponse;
import com.campusconnect.campusconnect.entity.User;
import com.campusconnect.campusconnect.exception.*;
import com.campusconnect.campusconnect.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.regex.Pattern;

@Service
public class UserService {

    /**
     * Protected platform identity — nobody can register these as a
     * username. Extend this set (or move it to an admin-controlled table)
     * as the reserved-name list grows.
     */
    private static final Set<String> RESERVED_USERNAMES = Set.of(
            "campusconnect", "campusconnectofficial", "campusconnectadmin",
            "admin", "administrator", "support", "official"
    );

    private static final Set<String> SYSTEM_USERNAMES = Set.of(
            "campusconnect", "campusconnectadmin"
    );

    public static boolean containsReservedCampusConnectTerm(String value) {
        if (value == null) return false;
        String normalized = value.toLowerCase().replaceAll("[^a-z0-9]", "");
        return normalized.contains("campusconnect");
    }

    public static boolean isSystemUsername(String username) {
        return username != null && SYSTEM_USERNAMES.contains(username.toLowerCase());
    }

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL = Pattern.compile(".*[^a-zA-Z0-9].*");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest req) {
        String username = req.getUsername().trim();
        String email = req.getEmail().trim().toLowerCase();

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("Username must be 3-20 characters: letters, numbers, underscores only");
        }
        if (containsReservedCampusConnectTerm(username) || RESERVED_USERNAMES.contains(username.toLowerCase())) {
            throw new ReservedUsernameException(username);
        }
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateUsernameException(username);
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException(email);
        }
        validatePasswordStrength(req.getPassword());

        User user = new User();
        user.setUsername(username);
        user.setFullName(req.getFullName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setUniversity(req.getUniversity());
        user.setBio(req.getBio());
        user.setCourse(req.getCourse());
        user.setYearOfStudy(req.getYearOfStudy());

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    public java.util.List<UserResponse> search(String q) { return userRepository.findTop20ByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(q.trim(), q.trim()).stream().map(UserResponse::from).toList(); }

    public UserResponse getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return UserResponse.from(user);
    }

    public boolean isUsernameAvailable(String username) {
        if (username == null || !USERNAME_PATTERN.matcher(username.trim()).matches()) return false;
        if (containsReservedCampusConnectTerm(username.trim()) || RESERVED_USERNAMES.contains(username.trim().toLowerCase())) return false;
        return !userRepository.existsByUsernameIgnoreCase(username.trim());
    }

    public boolean isEmailAvailable(String email) {
        if (email == null || email.isBlank()) return false;
        return !userRepository.existsByEmailIgnoreCase(email.trim());
    }

    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new WeakPasswordException("Password must be at least 8 characters");
        }
        if (!DIGIT.matcher(password).matches()) {
            throw new WeakPasswordException("Password must include at least one number");
        }
        if (!SPECIAL.matcher(password).matches()) {
            throw new WeakPasswordException("Password must include at least one symbol");
        }
    }
}
