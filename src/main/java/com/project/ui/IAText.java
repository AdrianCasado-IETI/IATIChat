package com.project.ui;

import com.project.Main;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class IAText extends HBox {

    private final TextFlow textFlow;

    public IAText() {
        this.setStyle("-fx-background-color:rgb(219, 219, 219); -fx-padding: 10; -fx-spacing: 10; -fx-border-radius: 10; -fx-background-radius: 10;");
        this.setAlignment(Pos.CENTER_LEFT);
    
        this.setMaxWidth(Main.windowWidth.doubleValue() * 0.8);
        this.setPrefWidth(Main.windowWidth.doubleValue() * 0.8);
    
        Main.windowWidth.addListener((observable, oldValue, newValue) -> {
            this.setMaxWidth(newValue.doubleValue() * 0.8);
            this.setPrefWidth(newValue.doubleValue() * 0.8);
        });
    
        this.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                this.setMaxWidth(Main.windowWidth.doubleValue() * 0.8);
                this.setPrefWidth(Main.windowWidth.doubleValue() * 0.8);
            }
        });
    
        ImageView botImage = new ImageView(new Image(getClass().getResourceAsStream("/assets/images/botImage.png")));
        botImage.setFitWidth(40);
        botImage.setFitHeight(40);
        botImage.setPreserveRatio(true);
    
        Text textLabel = new Text("Loading...");
        textLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        textLabel.setFill(Color.DARKGRAY);
    
        textFlow = new TextFlow(textLabel);
        textFlow.setStyle("-fx-padding: 5;");
    
        this.getChildren().addAll(botImage, textFlow);
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
