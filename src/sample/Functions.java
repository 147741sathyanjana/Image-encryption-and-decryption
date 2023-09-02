package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Functions {
    private static final Functions instance = new Functions();

    public static Functions getInstance() {
        return instance;
    }

    private Functions() {
    }

    public void switchScene(String fxml, Label lbl) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("fxml/" + fxml + ".fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);

            switch (fxml) {
                case "index":
                    stage.setTitle("Image Encryption Using AES Algorithm");
                    stage.setScene(new Scene(root, 500, 500));
                    break;
                case "encrypt_dialog":
                    stage.setTitle("Encrypt Image");
                    stage.onCloseRequestProperty().setValue(e -> switchScene("index", lbl));
                    stage.setScene(new Scene(root));
                    break;
                case "decrypt_dialog":
                    stage.setTitle("Decrypt Image");
                    stage.onCloseRequestProperty().setValue(e -> switchScene("index", lbl));
                    stage.setScene(new Scene(root));
                    break;
            }
            stage.setResizable(false);
            stage.show();
            lbl.getScene().getWindow().hide();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
