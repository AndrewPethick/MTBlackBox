package application;
	
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			BorderPane welcomePane = new BorderPane();
			Button welcomeButton = new Button("",new WelcomeScreen());
			welcomePane.setCenter(welcomeButton);
			root.setCenter(welcomeButton);
			welcomeButton.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					root.setCenter(new MTApplication());
				}
			});
			root.setStyle("-fx-background-color: #ffffff;");
			welcomePane.setStyle("-fx-background-color: #5A1C8B;");
			Scene scene = new Scene(root,1280,768);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("eMTeeBlackBox Developed by " + " Josh Poirier" +" Colton Kohnke" + " Katerina Gonzales" + " Elijah Thomas" + " Andrew Pethick 2014");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
