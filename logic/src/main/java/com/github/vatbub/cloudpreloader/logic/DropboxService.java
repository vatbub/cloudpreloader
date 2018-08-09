package com.github.vatbub.cloudpreloader.logic;

import java.io.IOException;
import java.net.URL;

public class DropboxService extends Service {
    @Override
    public void sendFile(URL url, Credentials credentials, Runnable onFinished) throws IOException {

    }

    // gog090k20yty565
    // cloudPreloaderDropboxSecret

    @Override
    public Class<? extends Credentials> getDefaultCredentialsClass() {
        return null;
    }
}
