package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sun.rmi.runtime.Log;

public class MTApplication extends BorderPane {

	public final LineChart<Number,Number> chartRxy;
	public final LineChart<Number,Number> chartPhasexy;

	public final LineChart<Number,Number> chartRyx;
	public final LineChart<Number,Number> chartPhaseyx;
	
//	ListView<Layer> layers = new ListView<Layer>();
	
	XYChart.Series seriesrxy = new XYChart.Series();
	XYChart.Series seriespxy = new XYChart.Series();
	XYChart.Series seriesryx = new XYChart.Series();
	XYChart.Series seriespyx = new XYChart.Series();
	
	 final NumberAxis xrxyAxis = new NumberAxis();
	 final NumberAxis xryxAxis = new NumberAxis();
	 final NumberAxis xpxyAxis = new NumberAxis();
	 final NumberAxis xpyxAxis = new NumberAxis();
	 
     final NumberAxis yRxyAxis = new NumberAxis();
     final NumberAxis yRyxAxis = new NumberAxis();
     final NumberAxis yPhasexyAxis = new NumberAxis();
     final NumberAxis yPhaseyxAxis = new NumberAxis();
     
     
     
     public final static String LOG_FREQ = "Log Frequency";
     public final static String LOG_RES = "Log Resistivity (Ohm m)";
     public final static String RAD = "Phase (rad)";
     
		TextField fileField = new TextField("resources\\TestData");
		Button loadButton = new Button("Load Data");
     
	LayerEntry layering = new LayerEntry(this);
	TabPane tabpane= new TabPane();
	public MTApplication() {	
		
		xrxyAxis.setLabel(LOG_FREQ); 
		xryxAxis.setLabel(LOG_FREQ); 
		xpxyAxis.setLabel(LOG_FREQ); 
		xpyxAxis.setLabel(LOG_FREQ); 
		
		yRxyAxis.setLabel(LOG_RES);
		yRyxAxis.setLabel(LOG_RES);
		yPhasexyAxis.setLabel(RAD);
		yPhaseyxAxis.setLabel(RAD);
		
		 chartRxy = new LineChart<Number,Number>(xrxyAxis,yRxyAxis);
		chartRyx = new LineChart<Number,Number>(xryxAxis,yRyxAxis);
		chartPhasexy = new LineChart<Number,Number>(xpxyAxis,yPhasexyAxis);
		chartPhaseyx = new LineChart<Number,Number>(xpyxAxis,yPhaseyxAxis);
		
	

		tabpane.getTabs().add(createTab("WELCOME", createWelcomePane()));
		tabpane.getTabs().add(createTab("Load Data", createDataPane()));
//		p.getTabs().add(createTab("View Data", ));
		tabpane.getTabs().add(createTab("Set Up Model", createModelPane()));
		tabpane.getTabs().add(createTab("Set up Inversion", createInversionSetupPane()));
		tabpane.getTabs().add(createTab("Invert", createInversionPane()));
		
		setLeft(tabpane);
		setCenter(createViewPane());
//		setDividerPositions(0.5f);
	}

	private Node createWelcomePane() {
		return new WelcomeScreen();
	}

	private Node createInversionPane() {
		return null;
	}

	private Node createInversionSetupPane() {
		VBox b = new VBox();

		Button button = new Button("print");
		button.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				System.out.println("PRINT");
				for(ArrayList<Double> l : getLayering()) {
					for (Double d : l) {
						System.out.print(d + "\t");
					}
					System.out.println();
				}
				
			}
		});
		b.getChildren().add(button);
		return b;
	}

	private Node createModelPane() {
		BorderPane b = new BorderPane();
		b.setCenter(layering);
		return b;
	}
	
	public ArrayList<ArrayList<Double>> getLayering() {
		ArrayList<ArrayList<Double>> layering = new ArrayList<ArrayList<Double>>();

		for(Layer l : this.layering.layering) {
			ArrayList<Double> values = new ArrayList<Double>();
			double res = Double.valueOf(l.r.getText());
			double thick = Double.valueOf(l.t.getText());
			values.add(res);
			values.add(thick);
			layering.add(values);
		}
		
		return layering;
	}

	private Node createViewPane() {
		VBox b = new VBox();
		b.getChildren().add(chartRxy);
		b.getChildren().add(chartPhasexy);
		b.getChildren().add(chartRyx);
		b.getChildren().add(chartPhaseyx);
		return b;
	}

	private Tab createTab(String string, Node content) {
		Tab t = new Tab(string);
		t.setContent(content);
		t.setClosable(false);
		return t;
	}

	private Node createDataPane() {

		
		BorderPane p = new BorderPane();
		HBox b = new HBox(fileField,loadButton);
		p.setTop(b);
		loadButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				loadData();
			}

			
		});
		return p;
	}
	private void loadData() {
		tabpane.getSelectionModel().select(1); //open up data view
		File f = new File(fileField.getText());
		ArrayList<ArrayList<Double>> data = importData(f);
	  
	   	ArrayList<Double> freqs = getColumn(data,0);
	   	ArrayList<Double> rhoxy = getColumn(data,1);
	   	ArrayList<Double> phasexy = getColumn(data,2);
	   	ArrayList<Double> rhoyx = getColumn(data,3);
	   	ArrayList<Double> phaseyx = getColumn(data,4);		
	   	
	   	for(int i = 0 ; i < freqs.size() ; i++) {
	   		seriesrxy.getData().add(new XYChart.Data(Math.log10(freqs.get(i)), Math.log10(rhoxy.get(i))));
	   		seriesryx.getData().add(new XYChart.Data(Math.log10(freqs.get(i)), Math.log10(rhoyx.get(i))));
	   		seriespxy.getData().add(new XYChart.Data(Math.log10(freqs.get(i)), (phasexy.get(i))));
	   		seriespyx.getData().add(new XYChart.Data(Math.log10(freqs.get(i)), (phaseyx.get(i))));
	   	}
	   	chartRxy.getData().add(seriesrxy);
	   	chartRyx.getData().add(seriesryx);
		 chartPhasexy.getData().add(seriespxy);
		 chartPhaseyx.getData().add(seriespyx);
	}
	private ArrayList<Double> getColumn(ArrayList<ArrayList<Double>> data, int i) {
		ArrayList<Double> column = new ArrayList<Double>();
		for(ArrayList<Double> d : data) {
			column.add(d.get(i));
		}
	
		return column;
		
	}
	private ArrayList<ArrayList<Double>> importData(File f) {
		ArrayList<ArrayList<Double>> frequencies = new ArrayList<ArrayList<Double>>();
		
	  	try {
	   		String line = "";
			BufferedReader in = new BufferedReader(new FileReader(f));
			in.readLine();//skip header	
			while((line = in.readLine()) != null) {
				ArrayList<Double> frequencyEntry = new ArrayList<Double>();
				

				String [] split = line.split("\t");
				for(String s : split) {
					frequencyEntry.add((Double.valueOf(s)));
				}
				 frequencies.add(frequencyEntry);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	  	Collections.sort(frequencies,new Comparator<ArrayList<Double>>() {

			@Override
			public int compare(ArrayList<Double> lhs, ArrayList<Double> rhs) {
				Double a = lhs.get(0);
				Double b = rhs.get(0);
				return a.compareTo(b);
			}
		});
	   	return frequencies;
	}


}
