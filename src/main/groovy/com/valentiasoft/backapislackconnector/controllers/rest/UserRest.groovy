package com.valentiasoft.backapislackconnector.controllers.rest

import com.valentiasoft.backapislackconnector.entities.UserEntity
import com.valentiasoft.backapislackconnector.services.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@Tag(name = 'user', description = 'User manager')
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping('/api/v1/auth')
class UserRest {
    private final UserService userService

    @Autowired
    UserRest(UserService userService){
        this.userService = userService
    }

    @Operation(
        summary = 'Retrieve all users',
        description = 'Retrieve all users within slack database'
    )
    @ApiResponses(value = [
        @ApiResponse(responseCode = "200", content = [ @Content(mediaType = "application/json", schema = @Schema(implementation = UserEntity)) ])
    ])
    @GetMapping('users')
    Page<UserEntity> getUsers(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "50") int size
    ){
        Pageable pageable = PageRequest.of(page, size)
        return userService.getUsers(pageable)
    }

    @Operation(
        summary = 'Retrieve a user',
        description = 'Retrieve an specific user using his username'
    )
    @ApiResponses(value = [
        @ApiResponse(responseCode = "200", content = [ @Content(mediaType = "application/json", schema = @Schema(implementation = UserEntity)) ]),
        @ApiResponse(responseCode = "400", content = [ @Content(mediaType = "text/html", schema = @Schema(type='string', example = 'Username {username} not found'))])
    ])
    @GetMapping('users/{username}')
    <T> T getUser(@PathVariable String username){
        UserEntity userEntity = userService.getUser(username)
        if(userEntity){
            return userEntity
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userService.getErrorSearchingUser(username))
        }
    }

    @Operation(
        summary = 'Create a user',
        description = 'Create a user if parameters pass restrictions'
    )
    @ApiResponses(value = [
        @ApiResponse(responseCode = "200", content = [ @Content(mediaType = "application/json", schema = @Schema(implementation = UserEntity))]),
        @ApiResponse(responseCode = "400", content = [ @Content(mediaType = "text/html", schema = @Schema(type='string', example = '¡Something goes wrong creating user!'))])
    ])
    @PostMapping('users/create')
    <T> T createUser(@RequestBody UserEntity user) {
        String isValidUser = userService.isValidUser(user)

        if(!isValidUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(isValidUser)
        } else {
            return userService.createUser(user)
        }
    }

    @Operation(
        summary = 'Delete a user',
        description = 'Delete a user using his username'
    )
    @ApiResponses(value = [
        @ApiResponse(responseCode = "200", content = [ @Content(mediaType = "text/html", schema = @Schema(type='string', example = 'Username {id} deleted'))]),
        @ApiResponse(responseCode = "404", content = [ @Content(mediaType = "text/html", schema = @Schema(type='string', example = 'Username {username} not found'))]),
        @ApiResponse(responseCode = "500", content = [ @Content(mediaType = "text/html", schema = @Schema(type='string', example = 'Internal error: <ERROR>'))])
    ])
    @DeleteMapping('users/{username}/delete')
    String deleteUser(@PathVariable String username){
        return userService.deleteUser(username)
    }

    @Operation(
        summary = 'Advanced user search',
        description = 'Messages can be found using [username, role, email] as filter'
    )
    @ApiResponses(value = [
        @ApiResponse(responseCode = "200", content =  [ @Content(mediaType = "application/json", schema = @Schema(implementation = UserEntity)) ])
    ])
    @GetMapping('users/search')
    Page<UserEntity> searchUser(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "50") int size,
        @RequestParam(name = "username", required = false) String username,
        @RequestParam(name = "role", required = false) String role,
        @RequestParam(name = "email", required = false) String email
    ){
        Pageable pageable = PageRequest.of(page, size)
        return userService.searchUser(username, role, email, pageable)
    }
}
