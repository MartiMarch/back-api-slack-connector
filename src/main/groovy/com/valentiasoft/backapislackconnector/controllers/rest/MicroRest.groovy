package com.valentiasoft.backapislackconnector.controllers.rest

import com.valentiasoft.backapislackconnector.components.MicroProperties
import com.valentiasoft.backapislackconnector.services.MicroService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity

@Tag(name = 'micro', description = 'Micro metadata')
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping('/api/v1/micro')
class MicroRest {
    private final String HEALTH_CHECK_STATUS = 'Microservice running'
    private final MicroService microService

    @Autowired
    public MicroRest(MicroService microService) {
        this.microService = microService;
    }

    @Operation(
        summary = 'Health check',
        description = 'Used to check if service is running'
    )
    @ApiResponses(value = [
        @ApiResponse(responseCode = "200", content = [ @Content(mediaType = "text/html", schema = @Schema(type = "string", example = "Microservice running")) ])
    ])
    @GetMapping('/health')
    String getMicroHealth(){
        return HEALTH_CHECK_STATUS
    }

    @Operation(
        summary = 'Micro metadata',
        description = 'Give some metadata of microservice'
    )
    @ApiResponses(value = [
        @ApiResponse(responseCode = "200", content = [ @Content(mediaType = "application/json", schema = @Schema(implementation = MicroProperties)) ])
    ])
    @GetMapping('/info')
    ResponseEntity<MicroProperties> getMicroInfo(){
        return ResponseEntity.ok(microService.getMicroEntity())
    }
}
