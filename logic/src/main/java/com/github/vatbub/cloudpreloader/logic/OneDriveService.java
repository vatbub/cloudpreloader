package com.github.vatbub.cloudpreloader.logic;

import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.net.URL;

public class OneDriveService extends Service {
    @Override
    public void sendFile(URL url, Credentials credentials, Runnable onFinished) throws IOException {
        throw new NotImplementedException("Sending not implemented");
    }

    @Override
    public Class<? extends Credentials> getDefaultCredentialsClass() {
        return SimpleApiKeyCredentials.class;
    }
}
