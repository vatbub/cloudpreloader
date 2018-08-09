package com.github.vatbub.cloudpreloader.pcclient;

/*-
 * #%L
 * cloudpreloader.pcclient
 * %%
 * Copyright (C) 2016 - 2018 Frederik Kammel
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.github.vatbub.cloudpreloader.logic.Credentials;
import com.github.vatbub.cloudpreloader.logic.CredentialsManager;
import com.github.vatbub.cloudpreloader.logic.Service;
import com.github.vatbub.cloudpreloader.logic.SimpleApiKeyCredentials;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GUI {
    private static ResourceBundle bundle;
    @FXML
    private Button setup;
    @FXML
    private ChoiceBox<Service.KnownImplementations> servicePicker;
    @FXML
    private TextField enterUrl;
    @FXML
    private Button send;
    @FXML
    private CheckBox allowInBackground;

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static void setBundle(ResourceBundle bundle) {
        GUI.bundle = bundle;
    }

    @FXML
    void sendOnAction(ActionEvent event) throws IOException {
        Service.KnownImplementations implementation = servicePicker.getSelectionModel().getSelectedItem();
        Credentials credentials = CredentialsManager.getInstance().getCredentials(implementation);
        Service.getInstance(implementation).sendFile(new URL(enterUrl.getText()), credentials, () -> System.out.println("Finished!"));
    }

    @FXML
    void setupOnAction(ActionEvent event) throws Exception {
        Service.KnownImplementations implementation = servicePicker.getSelectionModel().getSelectedItem();
        switch (implementation) {
            case IFTTT:
                IFTTTSetUpView.show((makerApiKey -> {
                    CredentialsManager.getInstance().saveCredentials(implementation, new SimpleApiKeyCredentials(makerApiKey));
                    send.setDisable(false);
                }));
                break;
            case OneDrive:
                List<String> scopes = new ArrayList<>();
                scopes.add("User.Read");
                OAUTHSetUpView.show(new URL("https://login.microsoftonline.com/common/oauth2/v2.0/authorize"), "05e2a1c2-fedc-431e-81b5-4da5676e5961", new URL("https://login.live.com/oauth20_desktop.srf"), scopes, (accessToken, authenticationToken, userId) -> {
                    System.out.println("accessToken = " + accessToken);
                    System.out.println("authenticationToken = " + authenticationToken);
                    System.out.println("userId = " + userId);
                });
            case Dropbox:
                /*Client wakeLauncherClient = new Client(new URL("https://awsec2wakelauncher.herokuapp.com"));
                Client.IpInfo ipInfo = wakeLauncherClient.launchAndWaitForInstance("i-0e8df6301f6179842");
                String dropboxSecret;
                int counter = 0;
                while (true) {
                    try {
                        dropboxSecret = APIKeyClient.getApiKey(ipInfo.getInstanceIp(), "cloudPreloaderDropboxSecret");
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                        counter++;
                        if (counter >= 10)
                            throw e;
                    }
                }
                DbxAppInfo appInfo = new DbxAppInfo("gog090k20yty565", dropboxSecret);
                DbxRequestConfig requestConfig = new DbxRequestConfig("cloudPreloaderDesktop");
                DbxWebAuth webAuth = new DbxWebAuth(requestConfig, appInfo);
                DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder()
                        .withNoRedirect()
                        .build();

                String authorizeUrl = webAuth.authorize(webAuthRequest);*/
                OAUTHSetUpView.show(new URL("https://www.dropbox.com/oauth2/authorize"), "gog090k20yty565", new URL("https://fredplus10.me/oauthredirect"), (accessToken, authenticationToken, userId) -> {
                    System.out.println("accessToken = " + accessToken);
                    System.out.println("authenticationToken = " + authenticationToken);
                    System.out.println("userId = " + userId);
                });
        }
    }

    @FXML
    void allowInBackgroundOnAction(ActionEvent event) {

    }

    @FXML
    void initialize() {
        servicePicker.getItems().addAll(Service.KnownImplementations.values());

        servicePicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            send.setDisable(!CredentialsManager.getInstance().hasCredentials(newValue));
        });
    }
}
