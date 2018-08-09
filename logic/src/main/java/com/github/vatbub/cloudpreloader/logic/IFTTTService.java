package com.github.vatbub.cloudpreloader.logic;

import com.github.vatbub.common.internet.Internet;

import java.io.IOException;
import java.net.URL;

public class IFTTTService extends Service {
    @Override
    public void sendFile(URL url, Credentials credentials, Runnable onFinished) throws IOException {
        if (!(credentials instanceof SimpleApiKeyCredentials))
            throw new IllegalArgumentException("credentials must be of type SimpleApiKeyCredentials");
        String res = Internet.sendEventToIFTTTMakerChannel(((SimpleApiKeyCredentials) credentials).getApiKey(), "cloudpreloader.uploadfile", url.toExternalForm());
        System.out.println(res);
    }

    @Override
    public Class<? extends Credentials> getDefaultCredentialsClass() {
        return SimpleApiKeyCredentials.class;
    }
}
