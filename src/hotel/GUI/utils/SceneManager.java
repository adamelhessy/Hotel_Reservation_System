package hotel.GUI.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager 
{
    private static Stage primaryStage;

    // Call this once when the app starts
    public static void setPrimaryStage(Stage stage) 
    {
        primaryStage = stage;
    }

    //everyone will use this to navigate to switch screens
    public static void navigate(String fxmlFileName)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/hotel/GUI/screens/" + fxmlFileName));
            Parent root = loader.load();
            //3AZABBBB HELP
            Scene scene = new Scene(root, 1280, 780);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
        }
        catch (Exception e)
        {
            System.err.println("Failed to load screen: " + fxmlFileName);
            e.printStackTrace();
        }
    }

}
