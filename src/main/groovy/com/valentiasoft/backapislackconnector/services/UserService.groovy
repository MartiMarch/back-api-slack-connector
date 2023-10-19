package com.valentiasoft.backapislackconnector.services

import com.valentiasoft.backapislackconnector.entities.UserEntity
import com.valentiasoft.backapislackconnector.po.UserRoles
import com.valentiasoft.backapislackconnector.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.GetMapping

import java.util.regex.Pattern

@Service
class UserService implements UserDetailsService  {
    private static final Pattern PATTERN_SPECIAL_CHARACTERS = Pattern.compile('[!@#$%&*()_+=|<>?{}\\[\\]~-]')
    private static final Pattern PATTERN_DIGITS_CHARACTERS = Pattern.compile('[0-9]')
    private static final Pattern PATTERN_CAPITAL_LETTERS  = Pattern.compile('[A-Z]')
    private static final Pattern PATTERN_LOWERCASE_LETTERS = Pattern.compile('[a-z]')
    private static final Pattern PATTERN_EMAIL = Pattern.compile('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$')
    private BCryptPasswordEncoder encryptor = new BCryptPasswordEncoder()

    @Autowired
    private UserRepository userRepository

    @Autowired
    private MongoTemplate mongoTemplate

    @Autowired
    UserService(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    UserEntity getUser(String username){
        return userRepository.findById(username).orElse(null)
    }

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = getUser(username)

        if(userEntity == null){
            throw new UsernameNotFoundException(username)
        }

        return User
                .withUsername(userEntity.getUsername())
                .password(userEntity.getPassword())
                .roles(userEntity.getRole())
                //.metaPropertyValues.add(new PropertyValue()
                .build()
    }

    Page<UserEntity> getUsers(Pageable pageable){
        return userRepository.findAll(pageable)
    }

    String getErrorSearchingUser(String username){
        return "Username ${username} not found"
    }

    UserEntity createUser(UserEntity user){
        String salt = BCrypt.gensalt()
        String hashedPass = BCrypt.hashpw(user.getPassword(), salt)

        user.setSalt(salt)
        user.setPassword(hashedPass)

        return userRepository.save(user)
    }

    String isValidUser(UserEntity user){
        if(!isValidPassword(user.getPassword())){
            return '''|¡Something goes wrong creating user!
                      |Invalid password, it must match the nex requirements:
                      | - More than 7 characters length
                      | - Can't be empty or null
                      | - Must contains capital letters
                      | - Must contains lowercase letters
                      | - Must contains numbers
                      | - Must contains special characters
                      |'''.stripMargin()
        }

        if(!isValidUsername(user.getUsername())){
            return '''|¡Something goes wrong creating user!
                      |Username length must contains 5 characters as minimum
                      |'''.stripMargin()
        }
        if(!isValidRole(user.getRole())){
            return """|¡Something goes wrong creating user!
                      |Unknown role, it must be one of the next values: ${UserRoles.values()}
                      |""".stripMargin()
        }
        if(!isValidEmail(user.getEmail())){
            return '''|¡Something goes wrong creating user!
                      |Invalid email
                      |'''.stripMargin()
        }

        return ''
    }

    private static boolean isValidPassword(String password){
        return password.size() >= 8 &&
               !password.isEmpty() &&
               password != null &&
               password.find(PATTERN_SPECIAL_CHARACTERS) &&
               password.find(PATTERN_DIGITS_CHARACTERS) &&
               password.find(PATTERN_LOWERCASE_LETTERS) &&
               password.find(PATTERN_CAPITAL_LETTERS)
    }

    private static boolean isValidUsername(String username){
        return username != null &&
               !username.isEmpty() &&
               username.size() >= 5
    }

    private static boolean isValidRole(String role){
        return UserRoles.getRole(role) != UserRoles.UNKNOWN
    }

    private static boolean isValidEmail(String email){
        return email != null &&
               !email.isEmpty() &&
               email.find(PATTERN_EMAIL)
    }

    String deleteUser(String username){
        Optional<UserEntity> optionalUser = userRepository.findById(username)
        if(optionalUser.isPresent()){
            try{
                UserEntity userEntity = optionalUser.get()
                userRepository.delete(userEntity)
                return "Username'${username}' deleted"
            } catch(Exception err){
                return """|Internal error:
                          |${err.message}""".stripMargin()
            }
        } else {
            return "Username '${username}' not found"
        }
    }

    Page<UserEntity> searchUser(String username, String role, String email, Pageable pageable){
        Query query = new Query()
        query.with(pageable)

        if(username != null){
            query.addCriteria(Criteria.where('username').is(username))
        }
        if(role != null){
            query.addCriteria(Criteria.where('role').is(role))
        }
        if(email != null){
            query.addCriteria(Criteria.where('email').is(email))
        }

        return mongoTemplate.find(query, UserEntity.class) as Page<UserEntity>
    }

    boolean checkPaswordHash(UserEntity user, String passToCheck){
        String hashedPass = BCrypt.hashpw(passToCheck, user.getSalt())
        return hashedPass == user.getPassword()
    }
}
