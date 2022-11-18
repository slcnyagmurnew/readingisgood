package com.example.readingisgood.dao;

import com.example.readingisgood.model.Book;
import com.example.readingisgood.model.ERole;
import com.example.readingisgood.model.Role;
import com.example.readingisgood.model.User;
import com.example.readingisgood.payload.LoginRequest;
import com.example.readingisgood.payload.SignUpRequest;
import com.example.readingisgood.repository.RoleRepository;
import com.example.readingisgood.repository.UserRepository;
import com.example.readingisgood.security.jwt.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class CustomerDao {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenUtil tokenUtil;

    /**
     * Sign up operation for users and admin
     * @param signUpRequest: get from sign up api (in customer controller)
     * @return: success message if registration succeeded otherwise error message
     */
    public void signup(@NotNull SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) { // check if username exists in db
            throw new DuplicateKeyException("Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) { // check if email exists in db
            throw new DuplicateKeyException("Email is already in use!");
        }
        // create new user without roles
        User user = new User(signUpRequest.getUsername(), passwordEncoder.encode(signUpRequest.getPassword()), signUpRequest.getEmail(), null);

        Set<String> strRoles = signUpRequest.getRoles(); // get roles from request body
        Set<Role> roles = new HashSet<>();

        // create user (if roles is null) with user role (default)
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER) // check if role exists in db
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if ("admin".equals(role)) {
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN) // check if role exists in db
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER) // check if role exists in db
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                }
            });
        }

        user.setUserRoles(roles);
        userRepository.insert(user); // new document for user repository
    }

    /**
     * Login operation for users and admin
     * @param loginRequest: get from login api (in customer controller)
     * @return: token if authentication not fails otherwise error
     */
    public String login(@NotNull LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenUtil.generateJwtToken(authentication);
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

    public Optional<User> get(String value) {
        return userRepository.findByUsername(value);
    }
}
