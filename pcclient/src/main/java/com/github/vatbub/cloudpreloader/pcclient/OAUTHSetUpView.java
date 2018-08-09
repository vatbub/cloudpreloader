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


import com.teamdev.jxbrowser.chromium.events.*;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
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

    @FXML
    private ProgressBar statusBar;
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
        assert statusBar != null : "fx:id=\"statusBar\" was not injected: check your FXML file 'OAUTHSetUpView.fxml'.";

        registerWebviewHooks();
        initialized = true;
        navigate();
    }

    private void registerWebviewHooks() {
        webView.getBrowser().addStatusListener(statusEvent -> System.out.println("Status event: " + statusEvent.getText()));

        webView.getBrowser().addLoadListener(new LoadListener() {
            @Override
            public void onStartLoadingFrame(StartLoadingEvent startLoadingEvent) {
                System.out.println("onStartLoadingFrame: ");
                urlTextBox.setText(webView.getBrowser().getURL());
            }

            @Override
            public void onProvisionalLoadingFrame(ProvisionalLoadingEvent provisionalLoadingEvent) {
                System.out.println("onProvisionalLoadingFrame: ");
            }

            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent finishLoadingEvent) {
                System.out.println("onFinishLoadingFrame: ");
                String location = webView.getBrowser().getURL();
                System.out.println(location);
                if (location.startsWith(getRedirectURL().toExternalForm())) {
                    Map<String, String> queryMap = getQueryMap(location.split("#")[1]);

                    String accessToken = null;
                    String authenticationToken = null;
                    String userId = null;

                    for (Map.Entry<String, String> entry : queryMap.entrySet()) {
                        switch (entry.getKey()) {
                            case "access_token":
                                accessToken = entry.getValue();
                                break;
                            case "authentication_token":
                                authenticationToken = entry.getValue();
                                break;
                            case "user_id":
                                userId = entry.getValue();
                                break;
                        }
                    }

                    Platform.runLater(() ->getStage().hide());
                    onResultRunnable.onResult(accessToken, authenticationToken, userId);

                }
            }

            @Override
            public void onFailLoadingFrame(FailLoadingEvent failLoadingEvent) {
                System.out.println("onFailLoadingFrame: ");
            }

            @Override
            public void onDocumentLoadedInFrame(FrameLoadEvent frameLoadEvent) {
                System.out.println("onDocumentLoadedInFrame: ");
            }

            @Override
            public void onDocumentLoadedInMainFrame(LoadEvent loadEvent) {
                System.out.println("onDocumentLoadedInMainFrame: ");
            }
        });

        /*webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(Worker.State.SUCCEEDED))
                return;

            try {
                String location = webView.getEngine().getLocation();
                if (location.startsWith(getRedirectURL().toExternalForm())) {
                    URL locationURI = new URL(location);
                    Map<String, String> queryMap = getQueryMap(locationURI.getQuery());

                    String accessToken = null;
                    String authenticationToken = null;
                    String userId = null;

                    for (Map.Entry<String, String> entry : queryMap.entrySet()) {
                        switch (entry.getKey()) {
                            case "access_token":
                                accessToken = entry.getValue();
                                break;
                            case "authentication_token":
                                authenticationToken = entry.getValue();
                                break;
                            case "user_id":
                                userId = entry.getValue();
                                break;
                        }
                    }

                    getStage().hide();
                    onResultRunnable.onResult(accessToken, authenticationToken, userId);
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }); */

        // webView.getEngine().getLoadWorker().progressProperty().addListener((observable, oldValue, newValue) -> statusBar.setProgress(newValue.doubleValue()));
        // webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> urlTextBox.setText(newValue));
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
        void onResult(String accessToken, String authenticationToken, String userId);
    }
}
