package api;

import javafx.scene.control.Alert;

public class Message {
    /**
     * Empty Constructor
     */
    public Message(){ }

    public void showMessage(String title, String subject, String body, Alert.AlertType alertType){
             Alert alert = new Alert(alertType);
             alert.setTitle(title);
             alert.setHeaderText(subject);
             alert.setContentText(body);
             alert.showAndWait();
    }
}
