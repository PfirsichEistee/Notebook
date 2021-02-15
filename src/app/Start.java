package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Start extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
		Parent root = (Parent)loader.load();
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Notebook");
		
		primaryStage.setWidth(800);
		primaryStage.setHeight(600);
		primaryStage.setMinWidth(800);
		primaryStage.setMinHeight(600);
		
		
		primaryStage.show();
	}
}
