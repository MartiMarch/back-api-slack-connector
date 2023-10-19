package com.valentiasoft.backapislackconnector.controllers.rest

import com.valentiasoft.backapislackconnector.entities.SlackMessageEntity
import com.valentiasoft.backapislackconnector.services.SlackService
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
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
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

@Tag(name = 'slack', description = 'Slack connector')
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping('/api/v1/slack')
class SlackRest {
    private final SlackService slackService

    @Autowired
    SlackRest(SlackService slackService) {
        this.slackService = slackService
    }

    @Operation(
        summary = 'Slack messages connector',
        description = 'Let send messages to slack, it will be stored in DB'
    )
    @ApiResponses(value = [
        @ApiResponse(responseCode = "200", content = [ @Content(mediaType = "application/json", schema = @Schema(implementation = SlackMessageEntity))]),
        @ApiResponse(responseCode = "400", content = [ @Content(mediaType = "text/html", schema = @Schema(type='string', example = '¡Something goes wrong sending message!'))])
    ])
    @PostMapping('send')
    <T> T sendMsg(@RequestBody SlackMessageEntity slackMessageEntity){
        if(slackService.isValidMsg(slackMessageEntity)){
            return slackService.sendMessage(slackMessageEntity)
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(slackService.getErrorSendingMsg())
        }
    }

    @Operation(
        summary = 'Retrieve all messages',
        description = 'Retrieve all Slack message within slack database'
    )
    @ApiResponses(value = [
        @ApiResponse(responseCode = "200", content = [ @Content(mediaType = "application/json", schema = @Schema(implementation = SlackMessageEntity)) ])
    ])
    @GetMapping('messages')
    Page<SlackMessageEntity> getMessages(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "50") int size
    ){
        Pageable pageable = PageRequest.of(page, size)
        return slackService.getMessages(pageable)
    }

    @Operation(
        summary = 'Retrieve a message',
        description = 'Retrieve an specific message using id'
    )
    @ApiResponses(value = [
        @ApiResponse(responseCode = "200", content = [ @Content(mediaType = "application/json", schema = @Schema(implementation = SlackMessageEntity))]),
        @ApiResponse(responseCode = "400", content = [ @Content(mediaType = "text/html", schema = @Schema(type='string', example = 'Slack message with id {id} not found'))])
    ])
    @GetMapping('messages/{id}')
    <T> T getMessage(@PathVariable String id){
        SlackMessageEntity msg = slackService.getMessage(id)
        if(msg){
            return msg
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(slackService.getErrorSearchingMsg(id))
        }
    }

    @Operation(
        summary = 'Delete a message',
        description = 'Delete a message using id linked to it'
    )
    @ApiResponses(value = [
        @ApiResponse(responseCode = "200", content = [ @Content(mediaType = "text/html", schema = @Schema(type='string', example = 'Slack message with id {id} deleted'))]),
        @ApiResponse(responseCode = "404", content = [ @Content(mediaType = "text/html", schema = @Schema(type='string', example = 'Slack message with id {id} not found'))]),
        @ApiResponse(responseCode = "500", content = [ @Content(mediaType = "text/html", schema = @Schema(type='string', example = 'Internal error: <ERROR>'))])
    ])
    @DeleteMapping('messages/{id}/delete')
    String deleteMsg(@PathVariable String id) {
        return slackService.deleteMessage(id)
    }

    @Operation(
        summary = 'Advanced message search',
        description = 'Messages can be found using [type, date, ip] as filter'
    )
    @ApiResponses(value = [
        @ApiResponse(responseCode = "200", content =  [ @Content(mediaType = "application/json", schema = @Schema(implementation = SlackMessageEntity)) ])
    ])
    @GetMapping('messages/search')
    Page<SlackMessageEntity> searchMessage(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "50") int size,
        @RequestParam(name = "type", required = false) String type,
        @RequestParam(name = "date", required = false) String date,
        @RequestParam(name = "ip", required = false) String ip
    ){
        Pageable pageable = PageRequest.of(page, size)
        return slackService.searchMessage(type, date, ip, pageable)
    }
}
