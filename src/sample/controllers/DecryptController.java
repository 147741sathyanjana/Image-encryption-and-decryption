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

import java.io.ByteArrayInputStream;
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

public class DecryptController {
    @FXML
    private VBox vBoxDecrypt;
    @FXML
    private Button btnDownload;
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnDecrypt;
    @FXML
    private ImageView decryptedImage;
    @FXML
    private Label lblNotice;
    @FXML
    private TextField keyField;

    private File fileToDecrypt;
    private CipherInputStream cipt;
    private byte[] decryptedImageInBytes;

    @FXML
    public void initialize() {
        vBoxDecrypt.setVisible(false);
        vBoxDecrypt.setManaged(false);

        keyField.setDisable(true);
        btnDecrypt.setDisable(true);
        btnDownload.setDisable(true);
    }

    @FXML
    private void handleSelectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Encrypted Image");
        FileChooser.ExtensionFilter filterExtensions = new FileChooser.ExtensionFilter("Image Files (.jpg, .png)",
                "*.jpg", "*.png");
        fileChooser.getExtensionFilters().addAll(filterExtensions);
        fileToDecrypt = fileChooser.showOpenDialog(null);

        if (fileToDecrypt != null) {
            lblNotice.setText("Encrypted image file: " + fileToDecrypt.getName());

            keyField.setDisable(false);
            btnDecrypt.setDisable(false);
        }
    }

    @FXML
    private void handleBack(ActionEvent actionEvent) throws IOException {
        Functions.getInstance().switchScene("index", lblNotice);
    }

    private CipherInputStream decryptImage(SecretKey key) {
        CipherInputStream cipt = null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            cipt = new CipherInputStream(
                    new FileInputStream(fileToDecrypt), cipher);
            decryptedImageInBytes = cipt.readAllBytes();
        } catch (Exception e) {
            if (e.getMessage().contains("BadPaddingException"))
                cipt = null;
        }
        return cipt;
    }

    @FXML
    private void handleDecrypt(ActionEvent actionEvent) {
        if (keyField.getText().isEmpty()) {
            keyField.requestFocus();
        } else {
            vBoxDecrypt.setManaged(true);
            vBoxDecrypt.setVisible(true);
            vBoxDecrypt.getScene().getWindow().sizeToScene();

            lblStatus.setText("Decrypting...");
            btnDownload.setDisable(true);

            try {
                byte[] key = keyField.getText().getBytes("UTF-8");
                MessageDigest sha = MessageDigest.getInstance("SHA-1");
                key = sha.digest(key);
                key = Arrays.copyOf(key, 16);
                SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
                cipt = decryptImage(secretKeySpec);

                if (cipt != null) {
                    keyField.setDisable(true);
                    btnDecrypt.setDisable(true);
                    btnDownload.setDisable(false);

                    lblNotice.setVisible(false);
                    lblNotice.setManaged(false);

                    lblStatus.setText("Image decrypted succesfully,\nplease proceed to download the file.");
                    Image image = new Image(new ByteArrayInputStream(
                            decryptedImageInBytes), 400, 400,
                            true,
                            true);
                    decryptedImage.setImage(image);
                    decryptedImage.getScene().getWindow().sizeToScene();
                } else {
                    cipt = null;
                    keyField.setDisable(false);
                    btnDecrypt.setDisable(false);
                    btnDownload.setDisable(true);

                    lblStatus.setText("Image could not be decrypted,\nplease check your secret key.");
                    lblStatus.getScene().getWindow().sizeToScene();
                }
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDownload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save decrypted file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Format",
                "*.jpg", "*.png"));
        fileChooser.setInitialFileName(fileToDecrypt.getName().replaceFirst("[.][^.]+$", ""));
        File savedFile = fileChooser.showSaveDialog(null);
        if (savedFile != null && fileToDecrypt != null) {
            try {
                FileOutputStream fileop = new FileOutputStream(
                        new File(savedFile.toPath().toString()));

                for (byte b : decryptedImageInBytes) {
                    fileop.write(b);
                }
                fileop.close();

                lblStatus.setText("Decrypted image saved successfully.");
                lblStatus.getScene().getWindow().sizeToScene();

                Desktop.getDesktop().open(new File(savedFile.getAbsolutePath().substring(0,
                        savedFile.getAbsolutePath().lastIndexOf(File.separator))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
