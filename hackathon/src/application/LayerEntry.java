package application;


import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LayerEntry extends BorderPane{
	
	private MTApplication app;
	public ArrayList<Layer> layering = new ArrayList<Layer>();
	VBox layeringView = new VBox();
	
	public LayerEntry(MTApplication app) {
		this.app = app;
		setCenter(layeringView);
		setupLayerEntry();
		
	}

	TextField thickness = new TextField("100");
	TextField res = new TextField("1");
	Button add = new Button("Add Layer");
	private void setupLayerEntry() {
		HBox b = new HBox();
		b.getChildren().add(thickness);
		b.getChildren().add(res);
		b.getChildren().add(add);
		this.setTop(b);
		add.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				addNewLayer();
				
			}

			
		});
		
	}
	
	private void addNewLayer() {
		// TODO Auto-generated method stub
		Double t = Double.valueOf(thickness.getText());

		Double r = Double.valueOf(res.getText());
		Layer l = new Layer(t,r);
		layering.add(l);
		Button remove = new Button("Remove");
		TextField res = new TextField();
		HBox b = new HBox(new Label("Res"),l.r,new Label("Thick"),l.t,remove);
		layeringView.getChildren().add(b);
		remove.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				layering.remove(l);
				layeringView.getChildren().remove(b);
			}
		});
		
	}
}
