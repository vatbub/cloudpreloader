package com.github.vatbub.cloudpreloader.logic;

public interface Credentials {
    String serialize();
    void deserialize(String input);
}
