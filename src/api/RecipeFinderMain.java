package api;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Dustin Summers
 * Date: 11/06/2017
 * Description:
 * Main class for Recipe Finder
 */

public class RecipeFinderMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("recipeChooser.fxml"));
        primaryStage.setTitle("Restaurant Inventory");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
