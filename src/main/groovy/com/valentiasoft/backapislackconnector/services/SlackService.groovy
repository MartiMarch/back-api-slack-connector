package com.valentiasoft.backapislackconnector.services

import com.valentiasoft.backapislackconnector.components.SlackProperties
import com.valentiasoft.backapislackconnector.controllers.Curl
import com.valentiasoft.backapislackconnector.entities.SlackMessageEntity
import com.valentiasoft.backapislackconnector.po.MessageType
import com.valentiasoft.backapislackconnector.repositories.SlackRepository
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class SlackService {
    private final static String DATE_ZONE = 'Europe/Madrid'
    private final static String DATE_FORMAT = 'dd-MM-yyyy'

    private final SlackRepository slackRepository
    private final SlackProperties slackProperties
    private final Curl curl = new Curl()

    @Autowired
    private HttpServletRequest request

    @Autowired
    private MongoTemplate mongoTemplate

    @Autowired
    SlackService(SlackRepository slackRepository, SlackProperties slackProperties) {
        this.slackRepository = slackRepository
        this.slackProperties = slackProperties
    }

    Page<SlackMessageEntity> getMessages(Pageable pageable){
        return slackRepository.findAll(pageable)
    }

    SlackMessageEntity getMessage(String id){
        return slackRepository.findById(id).orElse(null)
    }

    String getErrorSearchingMsg(String id){
        return "Messagw with id '${id}' not found"
    }

    SlackMessageEntity sendMessage(SlackMessageEntity slackMessageEntity){
        String webhookEndpoint = slackProperties.getWebhook()
        Map flags = [
            H: "'Content-type: application/json'",
            d: "'{\"text\":\"${slackMessageEntity.message}\"}'"
        ]
        curl.call(webhookEndpoint, 'POST',flags)
        slackMessageEntity.setDate(getCurrentDate())
        slackMessageEntity.setIp(request.getRemoteAddr())
        slackMessageEntity.setType(slackMessageEntity.getType().toLowerCase())
        return slackRepository.save(slackMessageEntity)
    }

    boolean isValidMsg(SlackMessageEntity slackMessageEntity){
        return isKnownType(slackMessageEntity.type)
    }

    boolean isKnownType(String type){
        return MessageType.UNKNOWN != MessageType.getMsgType(type)
    }

    String getErrorSendingMsg(){
        return """
            |¡Something goes wrong sending message!
            |Check the next things:
            | - Type of message must be ${MessageType.values()}
            |""".stripMargin()
    }

    private String getCurrentDate(){
        ZoneId madridZone = ZoneId.of(DATE_ZONE)
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT)
        LocalDateTime dateTime = ZonedDateTime.now(madridZone).toLocalDateTime()
        return dateTime.format(dateFormat)
    }

    String deleteMessage(String id){
        Optional<SlackMessageEntity> optionalMsg = slackRepository.findById(id)
        if(optionalMsg.isPresent()) {
            try {
                SlackMessageEntity msg = optionalMsg.get()
                slackRepository.delete(msg)
                return "Slack message with id '${id}' deleted"
            } catch(Exception err) {
                return """|Internal error:
                          |${err.message}""".stripMargin()
            }
        } else {
            return "Slack message with id '${id}' not found"
        }
    }

    Page<SlackMessageEntity> searchMessage(String type, String date, String ip, Pageable pageable){
        Query query = new Query()
        query.with(pageable)

        if(type != null){
            query.addCriteria(Criteria.where('type').is(type))
        }
        if(date != null){
            query.addCriteria(Criteria.where('date').is(date))
        }
        if(ip != null){
            query.addCriteria(Criteria.where('ip').is(ip))
        }

        return mongoTemplate.find(query, SlackMessageEntity.class) as Page<SlackMessageEntity>
    }
}
