package application;

import javafx.scene.layout.BorderPane;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene; 
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage; 


public class WelcomeScreen extends BorderPane {
	public WelcomeScreen() {
	
//	BorderPane borderPane = new BorderPane();
		
	//public String name = "geophysWiz.png";
	Image i = new Image(WelcomeScreen.class.getResource("geophysWiz1.png").toExternalForm());
	//Image i = new Image("geophysWiz1.png");
	
	ImageView view = new ImageView(i);	
    view.setImage(i);
	
	//view.setFitWidth(10000);
	view.setPreserveRatio(true);
	view.prefWidth(500);
//	view.prefWidth(500);
//	view.prefHeight(0.4f);
//	view.setFitHeight(getMaxHeight());
	
	this.setCenter(view);
		
	}
}
