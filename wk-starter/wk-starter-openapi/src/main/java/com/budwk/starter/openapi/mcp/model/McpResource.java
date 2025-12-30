package com.budwk.starter.openapi.mcp.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * MCP Resource Definition Model
 * 
 * @author wizzer@qq.com
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class McpResource {
    private String uri;
    private String name;
    private String description;
    private String mimeType;

    public McpResource() {
    }

    public McpResource(String uri, String name, String description, String mimeType) {
        this.uri = uri;
        this.name = name;
        this.description = description;
        this.mimeType = mimeType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
