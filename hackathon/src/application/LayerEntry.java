package application;


import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import javax.imageio.ImageIO;



public class LayerEntry extends SplitPane{
	
	private MTApplication app;
	public ArrayList<Layer> layering = new ArrayList<Layer>();
	VBox layeringView = new VBox();
	BorderPane layerEdit = new BorderPane();
	
	public final LineChart<Number,Number> chart;
	XYChart.Series series = new XYChart.Series();
	 final NumberAxis xAxis = new NumberAxis(-3,3,0.5);
     final NumberAxis yAxis = new NumberAxis();
     
	public LayerEntry(MTApplication app) {
		this.app = app;
		setOrientation(Orientation.VERTICAL);
	
		setupLayerEntry();
		chart= new LineChart<Number, Number>(xAxis, yAxis);
		chart.getData().add(series);
		getItems().add(layerEdit);
		getItems().add(chart);
		
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

		layerEdit.setTop(b);
		layerEdit.setCenter(new ScrollPane(layeringView));
		add.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				addNewLayer();
				
			}

			
		});
//		Button forward = new Button("FORWARD COMPUTE");
//		setBottom(forward);
		
//		forward.setOnAction(new EventHandler<ActionEvent>() {
//			
//			@Override
//			public void handle(ActionEvent event) {
//				app.fwd();
//			}
//		});
	}
	
	private void addNewLayer() {
		// TODO Auto-generated method stub
		Double t = Double.valueOf(thickness.getText());
		Double r = Double.valueOf(res.getText());
		Layer l = new Layer(this,t,r);
		layering.add(l);
		Button remove = new Button("Remove");
		TextField res = new TextField();
		Slider sr = new Slider(-3, 3, 1);
		Slider st = new Slider(1, 1000, 1);
		st.setValue(t);
		sr.setValue(r);
		VBox resB = new VBox();
		VBox thickB = new VBox();
		resB.getChildren().add(l.r);
		resB.getChildren().add(sr);
		thickB.getChildren().add(l.t);
		thickB.getChildren().add(st);
		
		HBox b = new HBox(new Label("Res"),resB,new Label("Thick"),thickB,remove);
		VBox v = new VBox();
		v.getChildren().add(b);
		layeringView.getChildren().add(v);

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
				updateGraph();
			}
		});
		st.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				l.t.setText("" + newValue.doubleValue());
				app.fwd();
				updateGraph();
			}
		});		
		remove.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				layering.remove(l);
				layeringView.getChildren().remove(v);
				updateGraph();
				updateGraph();
			}
		});
		updateGraph();
	}

	public void updateAll() {
		if(layering.size() == 0) addNewLayer(); //stops modelling null earths
		app.fwd();
		updateGraph();
	}

	private void updateGraph() {
		ObservableList<XYChart.Data<Number,Number>> data = series.getData();
//		xAxis.lowerBoundProperty().set(-3);
//		xAxis.upperBoundProperty().set(3);
		ArrayList<ArrayList<Double>> layering = app.getLayering();
		if(data.size() == (2 * layering.size() + 1)) {
			//update
			double depth = 0;
			double lastRes = -1;
			int i = 0;
			for(ArrayList<Double> values : layering) {
				double res = values.get(0);
				lastRes = res;
				double thick = -values.get(1);
				XYChart.Data<Number,Number> val = (XYChart.Data<Number,Number>) series.getData().get(i++);
				val.setXValue(Math.log10(res));
				val.setYValue(new Double(depth));
				depth += thick;
				val = (XYChart.Data<Number,Number>) series.getData().get(i++);
				val.setXValue(Math.log10(res));
				val.setYValue(new Double(depth));
			}
			XYChart.Data<Number,Number> val = (XYChart.Data<Number,Number>) series.getData().get(i++);
			val.setXValue(Math.log10(lastRes));
			val.setYValue(new Double(depth));
		
		} else {
			series.getData().removeAll(data);
			double depth = 0;
			double lastRes = -1;
			
			for(ArrayList<Double> values : layering) {
				double res = values.get(0);
				lastRes = res;
				double thick = -values.get(1);
				series.getData().add(new XYChart.Data(Math.log10(res),new Double(depth)));
				depth += thick;
				series.getData().add(new XYChart.Data(Math.log10(res),new Double(depth)));
			}
			series.getData().add(new XYChart.Data(Math.log10(lastRes),new Double(depth-100)));
		}
			
		series.setName("Geo-electrical Resistivity");
		chart.setTitle("Geo-electrical Model");
		xAxis.setLabel("Log10 Resistivity (Ohm m)");
		yAxis.setLabel("Elevation (m)");
		chart.setAnimated(false);
		
	}

	public void save() {
		FileChooser c = new FileChooser();
		String title = "Save Earth File";
		c.setTitle(title);
		String path = "./";
		if (path != null) {
			c.setInitialDirectory(new File(path));
		}
		c.getExtensionFilters().add(new ExtensionFilter("Earth Output", new ArrayList<String>(Arrays.asList("*.earth"))));
			
		File l = c.showSaveDialog(null);
		if(l == null) {
			
		} else {
			if(!l.getPath().toLowerCase().endsWith(".earth")) {
				l = new File(l.getPath() + ".earth");
			}
			if(l.exists()) {
				l.delete();
			}
			try {
				
				BufferedWriter out = new BufferedWriter(new FileWriter(l));
				out.write("depth,resistivity,thickness");
				out.newLine();
				double depth = 0;
				for(Layer layer : layering) {
					out.write(depth + "," + layer.r.getText() + "," + layer.t.getText());
					out.newLine();
					depth -= Double.valueOf(layer.t.getText());
				}
				out.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	public void load() {
		FileChooser c = new FileChooser();
		String title = "Load Earth File";
		c.setTitle(title);
		String path = "./";
		if (path != null) {
			c.setInitialDirectory(new File(path));
		}
		c.getExtensionFilters().add(new ExtensionFilter("Earth Output", new ArrayList<String>(Arrays.asList("*.earth"))));
			
		File l = c.showOpenDialog(null);
		layering.clear();
		
		if(l == null) {
			
		} else {
			if(l.exists()) {
				while(layeringView.getChildren().size() > 1) {
					//remove All layers
					layeringView.getChildren().remove(1);			
				}
				try {
					BufferedReader in = new BufferedReader(new FileReader(l));
					String line = "";
					in.readLine(); //skip header
					while((line = in.readLine()) != null) {
						String [] split = line.split(",");
						String depth = split[0];
						String res = split[1];
						String thick = split[2];
						this.res.setText(res);
						this.thickness.setText(thick);
						addNewLayer();
					}
					updateAll();
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
			
			
		}
	}
	public static String getCompactDate() {
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		return dateFormat.format(date);
	}
	public void exportPNG() {
		 WritableImage image = chart.snapshot(new SnapshotParameters(), null);

		    File file = new File("earth_"+getCompactDate()+".png");

		    try {
		        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
		        Desktop.getDesktop().open(file);  
		    } catch (IOException e) {
		       e.printStackTrace();		       
		    }
	}
	public void exportCSV() {
	
		    File file = new File("earth_"+getCompactDate()+".csv");

		    try {
				
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				double depth = 0;
				out.write("depth,resistivity,thickness");
				out.newLine();
				for(Layer layer : layering) {
					out.write(depth + "," + layer.r.getText() + "," + layer.t.getText());
					out.newLine();
					depth -= Double.valueOf(layer.t.getText());
				}
				out.close();
				Desktop.getDesktop().open(file);  
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}
