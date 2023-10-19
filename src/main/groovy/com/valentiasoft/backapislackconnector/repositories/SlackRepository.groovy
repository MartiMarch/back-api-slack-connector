package com.valentiasoft.backapislackconnector.repositories

import com.valentiasoft.backapislackconnector.entities.SlackMessageEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.data.domain.Page

@Repository
interface SlackRepository extends MongoRepository<SlackMessageEntity, String>{
    Page<SlackMessageEntity> findAll(Pageable pageable)
}