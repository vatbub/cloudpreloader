package com.github.vatbub.cloudpreloader.pcclient;

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
    void setupOnAction(ActionEvent event) throws IOException {
        Service.KnownImplementations implementation = servicePicker.getSelectionModel().getSelectedItem();
        switch(implementation){
            case IFTTT:
                IFTTTSetUpView.show((makerApiKey -> {
                    CredentialsManager.getInstance().saveCredentials(implementation, new SimpleApiKeyCredentials(makerApiKey));
                    send.setDisable(false);
                }));
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
        });
    }
}
