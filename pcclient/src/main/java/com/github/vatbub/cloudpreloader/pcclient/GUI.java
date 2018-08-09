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
    private TextField enterFilename;
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
        Service.getInstance(implementation).sendFile(new URL(enterUrl.getText()), enterFilename.getText(), credentials, () -> System.out.println("Finished!"));
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
                OAUTHSetUpView.show(new URL("https://login.microsoftonline.com/common/oauth2/v2.0/authorize"), "05e2a1c2-fedc-431e-81b5-4da5676e5961", new URL("https://login.live.com/oauth20_desktop.srf"), scopes, (credentials) -> {
                    System.out.println("accessToken = " + credentials.getAccessToken());
                    System.out.println("authenticationToken = " + credentials.getOtherParameters().get("authentication_token"));
                    System.out.println("userId = " + credentials.getUserId());
                    CredentialsManager.getInstance().saveCredentials(implementation, credentials);
                });
                break;
            case Dropbox:
                OAUTHSetUpView.show(new URL("https://www.dropbox.com/oauth2/authorize"), "gog090k20yty565", new URL("https://fredplus10.me/oauthredirect"), (credentials) -> CredentialsManager.getInstance().saveCredentials(implementation, credentials));
                break;
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
            setup.setDisable(false);
        });
    }
}
