package com.example.readingisgood.unit;

import com.example.readingisgood.dao.CustomerDao;
import com.example.readingisgood.model.ERole;
import com.example.readingisgood.model.Role;
import com.example.readingisgood.model.User;
import com.example.readingisgood.payload.LoginRequest;
import com.example.readingisgood.payload.SignUpRequest;
import com.example.readingisgood.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
public class CustomerDaoTest {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    public void before() {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ERole.ROLE_USER));
        User user = new User("testUser", passwordEncoder.encode("testUser"), "testUser@example.com", roles);
        userRepository.insert(user);
    }

    @Test
    void signUpTest() {
        Set<String> roles = new HashSet<>();
        Set<Role> role = new HashSet<>();
        roles.add("user");
        role.add(new Role(ERole.ROLE_USER));
        User user = new User("new", "new", "new@example.com", role);
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail(user.getEmail());
        signUpRequest.setPassword(user.getPassword());
        signUpRequest.setUsername(user.getUsername());
        signUpRequest.setRoles(roles);

        customerDao.signup(signUpRequest);
        customerDao.get(user.getUsername()).ifPresent(actualUser -> assertThat(actualUser.getUsername()).isEqualTo(user.getUsername()));
    }

    @Test
    void loginTest() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("testUser");
        loginRequest.setUsername("testUser");
        customerDao.login(loginRequest);

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertThat(userDetails.getUsername()).isEqualTo(loginRequest.getUsername());
    }

    @Test
    void getTest() {
        Set<Role> role = new HashSet<>();
        role.add(new Role(ERole.ROLE_USER));
        User user = new User("testUser", "testUser", "testUser@example.com", role);
        customerDao.get(user.getUsername()).ifPresent(actualUser -> assertThat(actualUser.getUsername()).isEqualTo(user.getUsername()));
    }

    @AfterEach
    public void clean() {
        customerDao.deleteAll();
        SecurityContextHolder.clearContext();
    }
}
