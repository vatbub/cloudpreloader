package com.github.vatbub.cloudpreloader.logic;

public class SimpleApiKeyCredentials implements Credentials {
    private String apiKey;

    public SimpleApiKeyCredentials() {
        this(null);
    }

    public SimpleApiKeyCredentials(String apiKey) {
        setApiKey(apiKey);
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String serialize() {
        return getApiKey();
    }

    @Override
    public void deserialize(String input) {
        setApiKey(input);
    }
}
