package com.github.vatbub.cloudpreloader.logic;

import java.io.IOException;
import java.net.URL;

public abstract class Service {
    public static Service getInstance(KnownImplementations implementation) {
        switch (implementation) {
            case IFTTT:
                return new IFTTTService();
            default:
                throw new IllegalArgumentException();
        }
    }

    public abstract void sendFile(URL url, Credentials credentials, Runnable onFinished) throws IOException;

    public abstract Class<? extends Credentials> getDefaultCredentialsClass();

    public enum KnownImplementations {
        IFTTT
    }
}
