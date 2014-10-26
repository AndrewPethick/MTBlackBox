package application;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TextField;

public class Layer {

	public TextField t = new TextField(""+-1);
	public TextField r = new TextField(""+-1);
	
	
	public Layer(Double t, Double r) {
		this.t.setText(""+t);
		this.r.setText(""+r);
	}

}
