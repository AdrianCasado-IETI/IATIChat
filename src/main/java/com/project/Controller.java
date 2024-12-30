package com.project;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONObject;

import com.project.ui.IAText;
import com.project.ui.UserText;

public class Controller implements Initializable {

    File selectedImage;

    @FXML
    private Button buttonCallStream, buttonCallComplete, buttonBreak;

    @FXML
    private VBox scrollContent;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField input;

    @FXML
    private ImageView image;

    @FXML 
    private VBox imageContainer;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private AtomicBoolean isCancelled = new AtomicBoolean(false);
    private InputStream currentInputStream;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean isFirst = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setButtonsIdle();
        imageContainer.setVisible(false);
        imageContainer.setManaged(false);
        scrollContent.setAlignment(Pos.CENTER);
        scrollContent.setSpacing(10);
    }

    @FXML
    private void callStream(ActionEvent event) {

        String inputValue = input.getText();
        if(inputValue.equals("") || inputValue == null) {
            return;
        }
        UserText newUserText;
        if(selectedImage == null) {
            newUserText = new UserText(inputValue);
        }else {
            newUserText = new UserText(inputValue, selectedImage);
        }

        scrollContent.getChildren().add(newUserText);
        
        input.setText("");
        setButtonsRunning();
        isCancelled.set(false);

        String encodedImage = null;

        if(selectedImage != null) {
            encodedImage = encodeImageToBase64(selectedImage);
        }
        deleteFile(null);


        JSONObject requestObject;
        if ( encodedImage != null ) {
            requestObject = new JSONObject("{\"model\": \"llava-phi3\", \"prompt\": \""+inputValue+"\", \"stream\": true, \"images\": [\""+encodedImage+"\"];}");
        } else {
            requestObject = new JSONObject("{\"model\": \"llama3.2:1b\", \"prompt\": \""+inputValue+"\", \"stream\": true}");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/api/generate"))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(requestObject.toString()))
                .build();
        
        IAText newIAResponse = new IAText();
        VBox.setVgrow(newIAResponse, Priority.ALWAYS);
        newIAResponse.setMaxWidth(500);
        newIAResponse.setPrefWidth(500);
        scrollContent.getChildren().add(newIAResponse);

        scrollContent.setFillWidth(true);

        isFirst = true;
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(response -> {
                    currentInputStream = response.body();
                    
                    executorService.submit(() -> {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(currentInputStream))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (isCancelled.get()) {
                                    System.out.println("Stream cancelled");
                                    break;
                                }
                                JSONObject jsonResponse = new JSONObject(line);
                                String responseText = jsonResponse.getString("response");
                                if (isFirst) {
                                    Platform.runLater(() -> newIAResponse.setText(responseText));
                                    isFirst = false;
                                } else {
                                    Platform.runLater(() -> newIAResponse.setText(newIAResponse.getText() + responseText));
                                    Platform.runLater(() -> scrollPane.setVvalue(1.0));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Platform.runLater(() -> {
                                newIAResponse.setText("Error during streaming.");
                                setButtonsIdle();
                            });
                        } finally {
                            try {
                                if (currentInputStream != null) {
                                    System.out.println("Cancelling InputStream in finally");
                                    currentInputStream.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Platform.runLater(this::setButtonsIdle);
                        }
                    });
                    return response;
                })
                .exceptionally(e -> {
                    if (!isCancelled.get()) {
                        e.printStackTrace();
                    }
                    Platform.runLater(this::setButtonsIdle);
                    return null;
                });
    }

    @FXML
    private void chooseFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Selecciona una imatge");

        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image", "*.png"),
            new FileChooser.ExtensionFilter("Image", "*.jpg"),
            new FileChooser.ExtensionFilter("Image", "*.jpeg"),
            new FileChooser.ExtensionFilter("Image", "*.svg")
        );

        selectedImage = chooser.showOpenDialog(scrollContent.getScene().getWindow());

        if(selectedImage != null) {
            try{
                String url = selectedImage.toURI().toString();
                System.out.println(url);
                image.setImage(new Image(url));
                imageContainer.setVisible(true);
                imageContainer.setManaged(true);
            }catch(Exception e) {
                System.out.println("error");
            }
            
            
        }
    }

    @FXML
    private void deleteFile(ActionEvent event) {
        selectedImage = null;
        imageContainer.setVisible(false);
        imageContainer.setManaged(false);
    }

    private void setButtonsRunning() {
        buttonCallStream.setDisable(true);
    }

    private void setButtonsIdle() {
        buttonCallStream.setDisable(false);
    }

    private String encodeImageToBase64 (File image) {
        try {
            byte[] imageBytes = Files.readAllBytes(image.toPath());
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch ( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }
}
