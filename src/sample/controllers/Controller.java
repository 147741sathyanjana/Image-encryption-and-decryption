package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import sample.Functions;

import java.io.IOException;

public class Controller {
    @FXML
    private Hyperlink linkFooter;
    @FXML
    private Label lblHeading;

    @FXML
    public void initialize() {
        lblHeading.setText("Image Encryption/Descryption\nUsing AES Algorithm");
        linkFooter.setText("Designed & Developed by Group 12\nv1.0.0");
    }

    @FXML
    private void handleEncrypt(ActionEvent actionEvent) throws IOException {
        Functions.getInstance().switchScene("encrypt_dialog", lblHeading);
    }

    @FXML
    private void handleDecrypt(ActionEvent actionEvent) {
        Functions.getInstance().switchScene("decrypt_dialog", lblHeading);
    }
}
