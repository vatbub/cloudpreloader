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


import com.github.vatbub.cloudpreloader.logic.OAuthCredentials;
import com.teamdev.jxbrowser.chromium.events.*;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class OAUTHSetUpView {
    private Stage stage;
    private boolean initialized;
    private URL authorizationURL;
    private URL redirectURL;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField urlTextBox;

    @FXML
    private BrowserView webView;

    private OnResultRunnable onResultRunnable;

    public static void show(URL baseURL, String clientId, URL redirectURL, OnResultRunnable onResultRunnable) throws IOException {
        show(baseURL, clientId, redirectURL, null, onResultRunnable);
    }

    public static void show(URL baseURL, String clientId, URL redirectURL, List<String> scopes, OnResultRunnable onResultRunnable) throws IOException {
        StringBuilder finalURL = new StringBuilder(baseURL.toExternalForm())
                .append("?client_id=")
                .append(clientId)
                .append("&response_type=token&redirect_uri=")
                .append(redirectURL.toExternalForm());

        if (scopes != null && scopes.size() > 0)
            finalURL.append("&scope=")
                    .append(String.join(" ", scopes));

        show(new URL(finalURL.toString()), redirectURL, onResultRunnable);
    }

    public static void show(URL authorizationURL, URL redirectURL, OnResultRunnable onResultRunnable) throws IOException {
        // ResourceBundle bundle = ResourceBundle.getBundle("com.github.vatbub.cloudpreloader.pcclient.IFTTTSetUpView");

        FXMLLoader fxmlLoader = new FXMLLoader(IFTTTSetUpView.class.getResource("OAUTHSetUpView.fxml"));//, bundle);
        Parent root = fxmlLoader.load();
        OAUTHSetUpView controller = fxmlLoader.getController();
        controller.setStage(new Stage());
        controller.onResultRunnable = onResultRunnable;

        Scene scene = new Scene(root);
        // scene.getStylesheets().add(getClass().getResource("MainWindow.css").toExternalForm());

        controller.getStage().setTitle(GUI.getBundle().getString("windowTitle"));

        controller.getStage().setMinWidth(scene.getRoot().minWidth(0) + 70);
        controller.getStage().setMinHeight(scene.getRoot().minHeight(0) + 70);

        controller.getStage().setScene(scene);

        controller.setAuthorizationURL(authorizationURL);
        controller.setRedirectURL(redirectURL);

        // Set Icon
        // primaryStage.getIcons().add(new Image(MainWindow.class.getResourceAsStream("icon.png")));

        controller.getStage().setOnCloseRequest((event) -> {
            if (Environment.isWindows() || Environment.isWindows64())
                new Thread(() -> controller.webView.getBrowser().dispose()).start();
            else
                controller.webView.getBrowser().dispose();
        });

        controller.getStage().show();
    }

    private static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    @FXML
    void initialize() {
        assert urlTextBox != null : "fx:id=\"urlTextBox\" was not injected: check your FXML file 'OAUTHSetUpView.fxml'.";
        assert webView != null : "fx:id=\"webView\" was not injected: check your FXML file 'OAUTHSetUpView.fxml'.";

        registerWebviewHooks();
        initialized = true;
        navigate();
    }

    private void registerWebviewHooks() {
        webView.getBrowser().addStatusListener(statusEvent -> System.out.println("Status event: " + statusEvent.getText()));

        webView.getBrowser().addLoadListener(new LoadListener() {
            @Override
            public void onStartLoadingFrame(StartLoadingEvent startLoadingEvent) {
                urlTextBox.setText(webView.getBrowser().getURL());
            }

            @Override
            public void onProvisionalLoadingFrame(ProvisionalLoadingEvent provisionalLoadingEvent) {
                urlTextBox.setText(webView.getBrowser().getURL());
            }

            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent finishLoadingEvent) {
                String location = webView.getBrowser().getURL();
                urlTextBox.setText(location);
                if (location.startsWith(getRedirectURL().toExternalForm())) {
                    Map<String, String> queryMap = getQueryMap(location.split("#")[1]);

                    OAuthCredentials credentials = new OAuthCredentials(queryMap);

                    Platform.runLater(() -> getStage().hide());
                    getOnResultRunnable().onResult(credentials);
                }
            }

            @Override
            public void onFailLoadingFrame(FailLoadingEvent failLoadingEvent) {
                urlTextBox.setText(webView.getBrowser().getURL());
            }

            @Override
            public void onDocumentLoadedInFrame(FrameLoadEvent frameLoadEvent) {
                urlTextBox.setText(webView.getBrowser().getURL());
            }

            @Override
            public void onDocumentLoadedInMainFrame(LoadEvent loadEvent) {
                urlTextBox.setText(webView.getBrowser().getURL());
            }
        });
    }

    public Stage getStage() {
        return stage;
    }

    private void setStage(Stage stage) {
        this.stage = stage;
    }

    public OnResultRunnable getOnResultRunnable() {
        return onResultRunnable;
    }

    private void navigate() {
        if (!initialized || getAuthorizationURL() == null)
            return;

        webView.getBrowser().loadURL(getAuthorizationURL().toString());
    }

    public URL getAuthorizationURL() {
        return authorizationURL;
    }

    public void setAuthorizationURL(URL authorizationURL) {
        this.authorizationURL = authorizationURL;
        navigate();
    }

    public URL getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(URL redirectURL) {
        this.redirectURL = redirectURL;
    }

    public interface OnResultRunnable {
        void onResult(OAuthCredentials credentials);
    }
}
