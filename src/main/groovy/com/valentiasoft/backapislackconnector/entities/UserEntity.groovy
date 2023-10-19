package com.valentiasoft.backapislackconnector.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document("users")
class UserEntity {

    @Id
    @Indexed(unique = true)
    private String username
    private String password
    private String salt
    private String role
    private String email

    UserEntity(){}

    String getUsername() {
        return username
    }

    void setUsername(String username) {
        this.username = username
    }

    String getPassword() {
        return password
    }

    void setPassword(String password) {
        this.password = password
    }

    String getEmail() {
        return email
    }

    void setEmail(String email) {
        this.email = email
    }

    String getRole() {
        return role
    }

    void setRole(String role) {
        this.role = role
    }

    String getSalt() {
        return salt
    }

    void setSalt(String salt) {
        this.salt = salt
    }
}
