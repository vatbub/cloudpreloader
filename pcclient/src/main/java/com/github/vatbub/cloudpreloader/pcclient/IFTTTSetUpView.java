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


import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IFTTTSetUpView {

    private static final String makerSettingsURL = "https://ifttt.com/services/maker_webhooks/settings";
    private static final String afterLoginURL = "https://ifttt.com/discover";
    // TODO Make it case insensitive
    private static final Pattern makerKeyPattern = Pattern.compile("<DD>https:\\/\\/maker\\.ifttt\\.com\\/use\\/.*<\\/DD>");
    private static final String makerKeyPatternPrefix = "<DD>https://maker.ifttt.com/use/";
    private static final String makerKeyPatternSufffix = "</DD>";
    private Stage stage;
    private IFTTTOnResultRunnable iftttOnResultRunnable;
    @FXML
    private WebView iftttWebView;
    @FXML
    private Label statusText;
    @FXML
    private ProgressBar statusBar;

    public static void show(IFTTTOnResultRunnable onResultRunnable) throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("com.github.vatbub.cloudpreloader.pcclient.IFTTTSetUpView");

        FXMLLoader fxmlLoader = new FXMLLoader(IFTTTSetUpView.class.getResource("IFTTTSetUpView.fxml"), bundle);
        Parent root = fxmlLoader.load();
        IFTTTSetUpView controller = fxmlLoader.getController();
        controller.setStage(new Stage());
        controller.iftttOnResultRunnable = onResultRunnable;

        Scene scene = new Scene(root);
        // scene.getStylesheets().add(getClass().getResource("MainWindow.css").toExternalForm());

        controller.getStage().setTitle(GUI.getBundle().getString("windowTitle"));

        controller.getStage().setMinWidth(scene.getRoot().minWidth(0) + 70);
        controller.getStage().setMinHeight(scene.getRoot().minHeight(0) + 70);

        controller.getStage().setScene(scene);

        // Set Icon
        // primaryStage.getIcons().add(new Image(MainWindow.class.getResourceAsStream("icon.png")));

        controller.getStage().show();
    }

    @FXML
    void initialize() {
        registerWebviewHooks();
        iftttWebView.getEngine().load(makerSettingsURL);
    }

    private void registerWebviewHooks() {
        iftttWebView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(Worker.State.SUCCEEDED))
                return;

            if (iftttWebView.getEngine().getLocation().equals(makerSettingsURL)) {
                String html = getHTML(iftttWebView.getEngine().getDocument());
                Matcher matcher = makerKeyPattern.matcher(html);
                if (!matcher.find())
                    throw new IllegalStateException("Api key not found");
                String searchResult = matcher.group();
                searchResult = searchResult.replace(makerKeyPatternPrefix, "").replace(makerKeyPatternSufffix, "");
                getStage().hide();
                iftttOnResultRunnable.onResult(searchResult);
            } else if (iftttWebView.getEngine().getLocation().equals(afterLoginURL))
                iftttWebView.getEngine().load(makerSettingsURL);
        });

        iftttWebView.getEngine().getLoadWorker().progressProperty().addListener((observable, oldValue, newValue) -> statusBar.setProgress(newValue.doubleValue()));
    }

    private String getHTML(Document doc) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Stage getStage() {
        return stage;
    }

    private void setStage(Stage stage) {
        this.stage = stage;
    }

    public interface IFTTTOnResultRunnable {
        void onResult(String makerApiKey);
    }
}
