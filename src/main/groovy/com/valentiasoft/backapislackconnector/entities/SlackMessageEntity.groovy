package com.valentiasoft.backapislackconnector.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document("messages")
class SlackMessageEntity {

    @Id
    @Indexed(unique = true)
    private String id
    private String message
    private String ip
    private String type
    private String date

    SlackMessageEntity(){}

    SlackMessageEntity(String message, String type) {
        super()
        this.message = message
        this.type = type
    }

    String getMessage() {
        return message
    }

    void setMessage(String message) {
        this.message = message
    }

    String getIp() {
        return ip
    }

    void setIp(String ip) {
        this.ip = ip
    }

    String getType() {
        return type
    }

    void setType(String type) {
        this.type = type
    }

    String getDate() {
        return date
    }

    void setDate(String date) {
        this.date = date
    }

    String getId() {
        return id
    }

    void setId(String id) {
        this.id = id
    }
}
