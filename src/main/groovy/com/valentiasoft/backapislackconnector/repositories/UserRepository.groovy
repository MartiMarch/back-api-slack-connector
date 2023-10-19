package com.valentiasoft.backapislackconnector.repositories

import com.valentiasoft.backapislackconnector.entities.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository extends MongoRepository<UserEntity, String>{
    Page<UserEntity> findAll(Pageable pageable)
}