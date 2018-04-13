package com.chinac.doc.sdk.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = SwaggerDocProperties.Swagger_Doc_PREFIX)
public class SwaggerDocProperties {
    public static final String Swagger_Doc_PREFIX = "swagger";

    private String basePackage;
    private String description;
    private String author;
    private boolean remoteEnable = true;

    private boolean selfhostEnable;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isSelfhostEnable() {
        return selfhostEnable;
    }

    public void setSelfhostEnable(boolean selfhostEnable) {
        this.selfhostEnable = selfhostEnable;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public boolean isRemoteEnable() {
        return remoteEnable;
    }

    public void setRemoteEnable(boolean remoteEnable) {
        this.remoteEnable = remoteEnable;
    }
}
