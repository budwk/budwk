package com.budwk.starter.openapi.mcp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

/**
 * MCP Prompt Definition Model
 * 
 * @author wizzer@qq.com
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class McpPrompt {
    private String name;
    private String description;
    private List<PromptArgument> arguments;

    public McpPrompt() {
    }

    public McpPrompt(String name, String description, List<PromptArgument> arguments) {
        this.name = name;
        this.description = description;
        this.arguments = arguments;
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

    public List<PromptArgument> getArguments() {
        return arguments;
    }

    public void setArguments(List<PromptArgument> arguments) {
        this.arguments = arguments;
    }

    /**
     * Prompt Argument Definition
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PromptArgument {
        private String name;
        private String description;
        private boolean required;

        public PromptArgument() {
        }

        public PromptArgument(String name, String description, boolean required) {
            this.name = name;
            this.description = description;
            this.required = required;
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

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }
    }
}
