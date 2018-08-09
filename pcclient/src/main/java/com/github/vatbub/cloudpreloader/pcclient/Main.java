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


import com.github.vatbub.common.core.Common;
import com.github.vatbub.common.core.logging.FOKLogger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
    private static Main instance;
    private GUI controllerInstance;

    public static void main(String[] args) {
        Common.getInstance().setAppName("com.github.vatbub.cloudpreloader");
        for (String arg : args) {
            if (arg.startsWith("locale=")) {
                FOKLogger.info(Main.class.getName(), "setting the language...");
                Locale.setDefault(new Locale(arg.split("=")[1]));
            }
        }

        launch(args);
    }

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        GUI.setBundle(ResourceBundle.getBundle("com.github.vatbub.cloudpreloader.pcclient.GUI"));

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GUI.fxml"), GUI.getBundle());
        Parent root = fxmlLoader.load();
        controllerInstance = fxmlLoader.getController();

        Scene scene = new Scene(root);
        // scene.getStylesheets().add(getClass().getResource("MainWindow.css").toExternalForm());

        primaryStage.setTitle(GUI.getBundle().getString("windowTitle"));

        primaryStage.setMinWidth(scene.getRoot().minWidth(0) + 70);
        primaryStage.setMinHeight(scene.getRoot().minHeight(0) + 70);

        primaryStage.setScene(scene);

        // Set Icon
        // primaryStage.getIcons().add(new Image(MainWindow.class.getResourceAsStream("icon.png")));

        primaryStage.show();
    }

    public GUI getControllerInstance() {
        return controllerInstance;
    }
}
