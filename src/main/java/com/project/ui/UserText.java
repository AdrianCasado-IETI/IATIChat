package com.project.ui;

import java.io.File;
import java.net.URL;

import com.project.Main;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class UserText extends VBox {

    private final TextFlow textFlow;

    public UserText(String value) {
        this.setStyle("-fx-background-color:rgb(255, 255, 255); -fx-padding: 10; -fx-spacing: 10; -fx-border-radius: 10; -fx-background-radius: 10;");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setMaxWidth(Main.windowWidth.doubleValue()*0.8);
        this.setPrefWidth(Main.windowWidth.doubleValue()*0.8);

        Main.windowWidth.addListener((observable, oldValue, newValue) -> {
            this.setMaxWidth(newValue.doubleValue()*0.8);
            this.setPrefWidth(newValue.doubleValue()*0.8);
        });

        Text textLabel = new Text(value);
        textLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        textLabel.setFill(Color.rgb(73, 73, 73));

        textFlow = new TextFlow(textLabel);
        textFlow.setStyle("-fx-padding: 5;");

        this.getChildren().addAll(textFlow);
    }

    public UserText(String value, File imageFile) {
        this.setStyle("-fx-background-color:rgb(255, 255, 255); -fx-padding: 10; -fx-spacing: 10; -fx-border-radius: 10; -fx-background-radius: 10;");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setMaxWidth(Main.windowWidth.doubleValue()*0.8);
        this.setPrefWidth(Main.windowWidth.doubleValue()*0.8);
        Main.windowWidth.addListener((observable, oldValue, newValue) -> {
            this.setMaxWidth(newValue.doubleValue()*0.8);
            this.setPrefWidth(newValue.doubleValue()*0.8);
        });

        Text textLabel = new Text(value);
        textLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        textLabel.setFill(Color.rgb(73, 73, 73));

        textFlow = new TextFlow(textLabel);
        textFlow.setStyle("-fx-padding: 5;");

        ImageView image = new ImageView();
        image.setFitWidth(200);
        image.setPreserveRatio(true);
        image.setSmooth(true);
        String url = imageFile.toURI().toString();
        image.setImage(new Image(url));

        this.getChildren().addAll(image,textFlow);
    }

    public void setText(String text) {
        Text newText = new Text(text);
        newText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        newText.setFill(Color.rgb(63, 63, 63));
        textFlow.getChildren().clear();
        textFlow.getChildren().add(newText);
    }

    public String getText() {
        if (textFlow.getChildren().isEmpty()) {
            return "";
        }
        return ((Text) textFlow.getChildren().get(0)).getText();
    }
}
