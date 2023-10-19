package com.valentiasoft.backapislackconnector.po

enum UserRoles {
    DEVOPS('devops'),
    BACKEND('backend'),
    FRONTEND('frontend'),
    UNKNOWN('unknown')

    private final String role

    private UserRoles(String role){
        this.role = role
    }

    static UserRoles getRole(String role){
        return values().find {enumRole ->
            enumRole.toString().equalsIgnoreCase(role)
        } ?: UNKNOWN
    }
}
