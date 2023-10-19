package com.valentiasoft.backapislackconnector.components

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.boot.context.properties.ConfigurationProperties

@Component
@Configurable
@ConfigurationProperties(prefix='slack')
class SlackProperties {
    private String webhook

    String getWebhook() {
        return webhook
    }

    void setWebhook(String webhook) {
        this.webhook = webhook
    }
}
