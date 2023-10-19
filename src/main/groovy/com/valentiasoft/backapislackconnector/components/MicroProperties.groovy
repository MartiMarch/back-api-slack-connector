package com.valentiasoft.backapislackconnector.components

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.boot.context.properties.ConfigurationProperties

@Component
@Configurable
@ConfigurationProperties(prefix='micro')
class MicroProperties {
    private String name
    private String version
    private String team

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    String getVersion() {
        return version
    }

    void setVersion(String version) {
        this.version = version
    }

    String getTeam() {
        return team
    }

    void setTeam(String team) {
        this.team = team
    }
}
