package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import sample.Functions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.awt.Desktop;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptController {
    @FXML
    private VBox vBoxEncrypt;
    @FXML
    private Button btnDownload;
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnEncrypt;
    @FXML
    private ImageView selectedImage;
    @FXML
    private Label lblNotice;
    @FXML
    private TextField keyField;

    private File fileToEncrypt;
    private CipherInputStream cipt;

    @FXML
    public void initialize() {
        vBoxEncrypt.setVisible(false);
        vBoxEncrypt.setManaged(false);

        keyField.setDisable(true);
        btnEncrypt.setDisable(true);
        btnDownload.setDisable(true);
    }

    @FXML
    private void handleSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        FileChooser.ExtensionFilter filterExtensions = new FileChooser.ExtensionFilter("Image Files (.jpg, .png)",
                "*.jpg", "*.png");
        fileChooser.getExtensionFilters().addAll(filterExtensions);
        fileToEncrypt = fileChooser.showOpenDialog(null);

        if (fileToEncrypt != null) {
            Image image = new Image(fileToEncrypt.toURI().toString(), 400, 400,
                    true,
                    true);
            lblNotice.setVisible(false);
            lblNotice.setManaged(false);

            keyField.setDisable(false);
            btnEncrypt.setDisable(false);

            selectedImage.setImage(image);
            selectedImage.getScene().getWindow().sizeToScene();
        }
    }

    @FXML
    private void handleBack(ActionEvent actionEvent) throws IOException {
        Functions.getInstance().switchScene("index", lblNotice);
    }

    private CipherInputStream encryptImage(SecretKey key) {
        CipherInputStream cipt = null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipt = new CipherInputStream(
                    new FileInputStream(fileToEncrypt), cipher);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipt;
    }

    @FXML
    private void handleEncrypt(ActionEvent actionEvent) {
        if (keyField.getText().isEmpty()) {
            keyField.requestFocus();
        } else {
            vBoxEncrypt.setManaged(true);
            vBoxEncrypt.setVisible(true);
            vBoxEncrypt.getScene().getWindow().sizeToScene();

            lblStatus.setText("Encrypting...");
            btnDownload.setDisable(true);

            try {
                byte[] key = keyField.getText().getBytes("UTF-8");
                MessageDigest sha = MessageDigest.getInstance("SHA-1");
                key = sha.digest(key);
                key = Arrays.copyOf(key, 16);
                SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
                cipt = encryptImage(secretKeySpec);
                if (cipt != null) {
                    keyField.setDisable(true);
                    btnEncrypt.setDisable(true);
                    btnDownload.setDisable(false);

                    lblStatus.setText("Image encrypted succesfully,\nplease proceed to download the file.");
                    lblStatus.getScene().getWindow().sizeToScene();
                } else {
                    keyField.setText("");
                    keyField.setDisable(false);
                    btnEncrypt.setDisable(false);
                    btnDownload.setDisable(false);
                    lblStatus.setText("Encryption failed, please try again.");
                    lblStatus.getScene().getWindow().sizeToScene();
                }
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                keyField.setText("");
                keyField.setDisable(false);
                btnEncrypt.setDisable(false);
                btnDownload.setDisable(false);
                lblStatus.setText("Encryption failed, please try again.");
                lblStatus.getScene().getWindow().sizeToScene();
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDownload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save encrypted file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Format",
                "*.jpg", "*.png"));
        fileChooser.setInitialFileName(fileToEncrypt.getName().replaceFirst("[.][^.]+$", ""));
        File savedFile = fileChooser.showSaveDialog(null);

        if (savedFile != null && fileToEncrypt != null) {
            try {

                FileOutputStream fileip = new FileOutputStream(
                        new File(savedFile.toPath().toString()));
                int i;
                while ((i = cipt.read()) != -1) {
                    fileip.write(i);
                }

                lblStatus.setText("Encrypted image saved successfully.");
                lblStatus.getScene().getWindow().sizeToScene();

                fileip.close();
                Desktop.getDesktop().open(new File(
                        savedFile.getAbsolutePath().substring(0,
                                savedFile.getAbsolutePath().lastIndexOf(File.separator))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
