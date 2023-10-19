package com.valentiasoft.backapislackconnector.services

import com.valentiasoft.backapislackconnector.entities.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LoginService {

    @Autowired
    private UserService userService

    @Autowired
    private JwtService jwtService

    String generateJwtToken(String username, String password){
        UserEntity userEntity = userService.getUser(username)
        if(userEntity != null && userService.checkPaswordHash(userEntity, password)){
            return jwtService.createToken(userEntity)
        }
        return null
    }

    String getLoginError(){
        return 'Failed log-in, user and/or invalid user/password'
    }
}
