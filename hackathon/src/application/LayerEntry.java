package application;


import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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
		b.getChildren().add(new Label("Res"));


		b.getChildren().add(res);
		b.getChildren().add(new Label("Thick"));
		b.getChildren().add(thickness);
		b.getChildren().add(add);
		Slider sr = new Slider(-3, 3, 1);
		Slider st = new Slider(1, 10000, 1);
		
		this.setTop(b);
		add.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				addNewLayer();
				
			}

			
		});
		Button forward = new Button("FORWARD COMPUTE");
		setBottom(forward);
		
		forward.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				app.fwd();
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
		Slider sr = new Slider(-3, 3, 1);
		Slider st = new Slider(1, 10000, 1);
		VBox resB = new VBox();
		VBox thickB = new VBox();
		resB.getChildren().add(l.r);
		resB.getChildren().add(sr);
		thickB.getChildren().add(l.t);
		thickB.getChildren().add(st);
		st.setValue(100);
		HBox b = new HBox(new Label("Res"),resB,new Label("Thick"),thickB,remove);
		VBox v = new VBox();
		v.getChildren().add(b);
		layeringView.getChildren().add(v);
		
		
		
//		HBox sliders = new HBox();
//		sliders.getChildren().add(new Label("Res"));
//		sliders.getChildren().add(new Label("Thick"));
//		sliders.getChildren().add(sr);
//		sliders.getChildren().add(st);
//		v.getChildren().add(sliders);

//		l.r.textProperty().addListener(new ChangeListener<String>() {
//
//			@Override
//			public void changed(ObservableValue<? extends String> observable,
//					String oldValue, String newValue) {
//				double res = Double.valueOf(newValue);
//				sr.setValue(res);
//			}
//		});
//		l.t.textProperty().addListener(new ChangeListener<String>() {
//
//			@Override
//			public void changed(ObservableValue<? extends String> observable,
//					String oldValue, String newValue) {
//				double thick = Double.valueOf(newValue);
//				st.setValue(thick);
//			}
//		});
		add.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				addNewLayer();
				
			}

			
		});
		sr.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				l.r.setText("" + Math.pow(10,newValue.doubleValue()));
				app.fwd();
			}
		});
		st.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				l.t.setText("" + newValue.doubleValue());
				app.fwd();
			}
		});		
		remove.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				layering.remove(l);
				layeringView.getChildren().remove(b);
			}
		});
		
	}
}
