package application;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

public class Layer {

	public TextField t = new TextField(""+-1);
	public TextField r = new TextField(""+-1);
	private LayerEntry e;
	
	
	public Layer(LayerEntry e, Double t, Double r) {
		this.t.setText(""+t);
		this.r.setText(""+r);
		this.e = e;
		this.t.setOnAction(getEventHandler());
		this.r.setOnAction(getEventHandler());
	}


	private EventHandler<ActionEvent> getEventHandler() {
		return new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				e.updateAll();
			}
		};
	}




}
