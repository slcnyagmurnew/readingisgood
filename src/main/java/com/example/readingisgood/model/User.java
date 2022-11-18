package com.example.readingisgood.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Document(collection = "user")
public class User {

    @Id
    private String id;

    private String username;

    private String password;

    private String email;

    private Set<Role> userRoles;

    public User(String username, String password, String email, Set<Role> userRoles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.userRoles = userRoles;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Role> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<Role> userRoles) {
        this.userRoles = userRoles;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", userRoles=" + userRoles +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User user = (User) obj;
        return Objects.equals(this.username, user.username) && Objects.equals(this.password, user.password)
                && Objects.equals(this.email, user.email) && Objects.equals(this.userRoles, user.userRoles);
    }
}

