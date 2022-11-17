package com.example.readingisgood.security.services;

import com.example.readingisgood.model.User;
import com.example.readingisgood.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository; // get mongo repository of user
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // search user with username in user db and throw exception if username does not found
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                "User not found with username " + username
        ));
        return UserDetailsImpl.build(user); // get user with details (roles, username eg.)
    }
}
