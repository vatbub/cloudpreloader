package com.github.vatbub.cloudpreloader.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OAuthCredentials implements Credentials {
    private static final String SERIALIZATION_PAIR_DELIMITER = "&";
    private static final String SERIALIZATION_KEY_VALUE_DELIMITER = "=";
    private String accessToken;
    private String userId;
    private Map<String, String> otherParameters;

    public OAuthCredentials() {
    }

    public OAuthCredentials(Map<String, String> parameters) {
        readFromParameters(parameters);
    }

    private void readFromParameters(Map<String, String> parameters) {
        setOtherParameters(new HashMap<>());
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            switch (entry.getKey()) {
                case "access_token":
                    setAccessToken(entry.getValue());
                    break;
                case "account_id":
                case "user_id":
                    setUserId(entry.getValue());
                    break;
                default:
                    getOtherParameters().put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public String serialize() {
        Map<String, String> parameters = new HashMap<>(otherParameters.size() + 2);
        parameters.put("access_token", getAccessToken());
        parameters.put("user_id", getUserId());
        parameters.putAll(getOtherParameters());

        List<String> keyValuePairs = new ArrayList<>(parameters.size());

        for (Map.Entry<String, String> entry : parameters.entrySet())
            keyValuePairs.add(entry.getKey() + SERIALIZATION_KEY_VALUE_DELIMITER + entry.getValue());

        return String.join(SERIALIZATION_PAIR_DELIMITER, keyValuePairs);
    }

    @Override
    public void deserialize(String input) {
        String[] keyValuePairs = input.split(SERIALIZATION_PAIR_DELIMITER);
        Map<String, String> parameters = new HashMap<>(keyValuePairs.length);

        for (String keyValuePair : keyValuePairs) {
            String[] splitRes = keyValuePair.split(SERIALIZATION_KEY_VALUE_DELIMITER);
            parameters.put(splitRes[0], splitRes[1]);
        }

        readFromParameters(parameters);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, String> getOtherParameters() {
        return otherParameters;
    }

    public void setOtherParameters(Map<String, String> otherParameters) {
        this.otherParameters = otherParameters;
    }
}
