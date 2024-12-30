package com.project;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static final DoubleProperty windowWidth = new SimpleDoubleProperty(600);
    public static double windowHeight = 400;

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/assets/layout.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("IATI Chat");
        stage.setWidth(windowWidth.get());
        stage.setHeight(windowHeight);
        stage.show();
        
        stage.widthProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                windowWidth.set(stage.getWidth());;
                windowHeight = stage.getHeight();
            }
            
        });

        // Afegeix una icona només si no és un Mac
        if (!System.getProperty("os.name").contains("Mac")) {
            Image icon = new Image("file:icons/icon.png");
            stage.getIcons().add(icon);
        }

         stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
