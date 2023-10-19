package com.valentiasoft.backapislackconnector.controllers.rest

import com.valentiasoft.backapislackconnector.entities.LoginEntity
import com.valentiasoft.backapislackconnector.entities.SlackMessageEntity
import com.valentiasoft.backapislackconnector.services.LoginService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = 'login', description = 'Login manager')
@RestController
@RequestMapping('/api/v1/auth')
class LoginRest {

    @Autowired
    private LoginService loginService

    @Operation(
        summary = 'Get JWT token',
        description = 'If user and password are correct it will return JWT token'
    )
    @ApiResponses(value = [
        @ApiResponse(responseCode = "200", content = [ @Content(mediaType = "application/json", schema = @Schema(implementation = LoginEntity))]),
        @ApiResponse(responseCode = "401", content = [ @Content(mediaType = "text/html", schema = @Schema(type='string', example = 'Failed log-in, user and/or invalid user/password'))])
    ])
    @PostMapping('login')
    String login(@RequestBody LoginEntity loginEntity){
        String token = loginService.generateJwtToken(loginEntity.getUsername(), loginEntity.getPassword())
        if(token){
            return token
        }
        return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).body(loginService.getLoginError())
    }
}
