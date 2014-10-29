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
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MTApplication extends SplitPane {

	public final LineChart<Number,Number> chartRxy;
	public final LineChart<Number,Number> chartPhasexy;

	public final LineChart<Number,Number> chartRyx;
	public final LineChart<Number,Number> chartPhaseyx;
	
//	ListView<Layer> layers = new ListView<Layer>();
	
	XYChart.Series seriesrxy = new XYChart.Series();
	XYChart.Series seriespxy = new XYChart.Series();
	XYChart.Series seriesryx = new XYChart.Series();
	XYChart.Series seriespyx = new XYChart.Series();
	
	
	XYChart.Series seriesrxyfwd = new XYChart.Series();
	XYChart.Series seriespxyfwd = new XYChart.Series();
	XYChart.Series seriesryxfwd = new XYChart.Series();
	XYChart.Series seriespyxfwd = new XYChart.Series();
	
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
     
	TextField fileField = new TextField("resources\\TestData.txt");
	Button loadButton = new Button("Load Data");
     
	LayerEntry layering = new LayerEntry(this);
	TabPane tabpane= new TabPane();
	private ArrayList<Double> freqs;
	private Stage stage;
	public MTApplication(Stage s) {	
		this.stage = s;
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
		
	

//		tabpane.getTabs().add(createTab("WELCOME", createWelcomePane()));
		tabpane.getTabs().add(createTab("Load Data", createDataPane()));
//		p.getTabs().add(createTab("View Data", ));
		tabpane.getTabs().add(createTab("Set Up Model", createModelPane()));
//		tabpane.getTabs().add(createTab("Set up Inversion", createInversionSetupPane()));
		tabpane.getTabs().add(createTab("Gravity", createGravityModel()));
		tabpane.setPrefWidth(700);
		getItems().addAll(tabpane,createViewPane());
//		setDividerPositions(0.5f);
	}

	private Node createGravityModel() {

		 final NumberAxis xAxis1 = new NumberAxis();
		 final NumberAxis yAxis1 = new NumberAxis();
		 xAxis1.setLabel("Distance (m)"); 
		 yAxis1.setLabel("GAL (ms^-2)"); 
		 final NumberAxis xAxis2 = new NumberAxis();
		 final NumberAxis yAxis2 = new NumberAxis();
		 xAxis2.setLabel("Distance (m)"); 
		 yAxis2.setLabel("GAL (ms^-2)"); 
		 final NumberAxis xAxis3 = new NumberAxis();
		 final NumberAxis yAxis3 = new NumberAxis();
		 xAxis3.setLabel("Distance (m)"); 
		 yAxis3.setLabel("GAL (ms^-2)"); 
		 final NumberAxis xAxis4 = new NumberAxis();
		 final NumberAxis yAxis4 = new NumberAxis();
		 xAxis4.setLabel("Distance (m)"); 
		 yAxis4.setLabel("GAL (ms^-2)"); 
		LineChart<Number,Number> chart1 = new LineChart<Number,Number>(xAxis1,yAxis1);
		LineChart<Number,Number> chart2 = new LineChart<Number,Number>(xAxis2,yAxis2);
		LineChart<Number,Number> chart3 = new LineChart<Number,Number>(xAxis3,yAxis3);
		LineChart<Number,Number> chart4 = new LineChart<Number,Number>(xAxis4,yAxis4);
		XYChart.Series seriesRaw = new XYChart.Series();
		XYChart.Series seriesInv1 = new XYChart.Series();
		XYChart.Series seriesInv2 = new XYChart.Series();
		XYChart.Series seriesInv3 = new XYChart.Series();
		
		seriesRaw.setName("Raw");
		seriesInv1.setName("Inv IRLSQ");
		seriesInv2.setName("Inv LSQ");
		seriesInv3.setName("Inv WLSQ");
		for(ArrayList<Double> p : 	invGrav()) {
			seriesRaw.getData().add(new XYChart.Data<Number, Number>(p.get(0), new Double(p.get(1))));
			seriesInv1.getData().add(new XYChart.Data<Number, Number>(p.get(0), new Double(p.get(2))));
			seriesInv2.getData().add(new XYChart.Data<Number, Number>(p.get(0), new Double(p.get(3))));
			seriesInv3.getData().add(new XYChart.Data<Number, Number>(p.get(0), new Double(p.get(4))));
		}
		chart1.getData().add(seriesRaw);
		chart2.getData().add(seriesInv1);
		chart3.getData().add(seriesInv2);
		chart4.getData().add(seriesInv3);
		VBox b = new VBox(chart1, chart2, chart3,chart4);
		return b;
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
		Image i = new Image(MTApplication.class.getResource("Folder.png").toExternalForm());

		Button openButton = new Button("",new ImageView(i));
	
		HBox b = new HBox(fileField,openButton,loadButton);
		p.setTop(b);
		loadButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				loadData();
			}

			
		});
		openButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				FileChooser c = new FileChooser();
				FileChooser fileChooser = new FileChooser();
		        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MT Text File", "*.txt");
		        fileChooser.getExtensionFilters().add(extFilter);
		        File file = fileChooser.showSaveDialog(stage);
		        
		        if(file != null){
		        	fileField.textProperty().set(file.getAbsolutePath());
		        }
			}
		});
		return p;
	}
	private void loadData() {
//		tabpane.getSelectionModel().select(1); //open up data view
		File f = new File(fileField.getText());
		ArrayList<ArrayList<Double>> data = importData(f);
		chartRxy.getData().remove(seriesrxy);
	   	chartRyx.getData().remove(seriesryx);
		 chartPhasexy.getData().remove(seriespxy);
		 chartPhaseyx.getData().remove(seriespyx);
		 
		 seriesrxy.setName("Apparent Resistivity XY Data");
		 seriesryx.setName("Phase XY Data");
		 seriespxy.setName("Apparent Resistivity YX Data");
		 seriespyx.setName("Phase YX Data");
		 
	   	ArrayList<Double> freqs = getColumn(data,0);
	   	ArrayList<Double> rhoxy = getColumn(data,1);
	   	ArrayList<Double> phasexy = getColumn(data,2);
	   	ArrayList<Double> rhoyx = getColumn(data,3);
	   	ArrayList<Double> phaseyx = getColumn(data,4);		
	   	this.freqs = freqs;
	   	
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
	boolean fwdInit = false;
	public ArrayList<ArrayList<Double>> invGrav() {
		GravityInverse inv = new GravityInverse();
		inv.soln();
		double [] gu = inv.getData().getArray();
		double [] distances = inv.getPosition().getArray();
		double [] inversionDIRLS = inv.getDIRLS().getArray();
		double [] inversionLSQ = inv.getLSQ().getArray();
		double [] inversionWLSQ = inv.getWLSQ().getArray();
		ArrayList<ArrayList<Double>> values = new ArrayList<ArrayList<Double>>();
		for(int i = 0 ; i < gu.length ; i++) {
			ArrayList<Double> l = new ArrayList<Double>();
		
			l.add(distances[i]);
			l.add(gu[i]);
			l.add(inversionDIRLS[i]);
			l.add(inversionLSQ[i]);
			l.add(inversionWLSQ[i]);
			values.add(l);
		}
		return values;
	}
	
	public void fwd() {
	
		if(!fwdInit) {
		chartRxy.getData().remove(seriesrxyfwd);
	   	chartRyx.getData().remove(seriesryxfwd);
		 chartPhasexy.getData().remove(seriespxyfwd);
		 chartPhaseyx.getData().remove(seriespyxfwd);
		 seriesrxyfwd.setName("Apparent Resistivity XY Synthetic");
		 seriespxyfwd.setName("Phase XY Synthetic");
		 seriesryxfwd.setName("Apparent Resistivity YX Synthetic");
		 seriespyxfwd.setName("Phase YX Synthetic");
		}
//		if(seriespxyfwd.getData().size() == )
		 seriesrxyfwd.getData().removeAll(seriesrxyfwd.getData());
		 seriesryxfwd.getData().removeAll(seriesryxfwd.getData());
		 seriespxyfwd.getData().removeAll(seriespxyfwd.getData());
		 seriespyxfwd.getData().removeAll(seriespyxfwd.getData());
		 ArrayList<ArrayList<Double>> layering = getLayering();
		 ArrayList<Double> freqs = this.freqs;
		 if(freqs == null) freqs = getDefaultFrequencies();
		 ArrayList<Double> res = getColumn(layering, 0);
		 ArrayList<Double> thick = getColumn(layering, 1);
		 double [] sig = new double [res.size()];
		 double [] d = new double [res.size()-1];
		 double [] w = new double [freqs.size()];
		 for(int i = 0 ; i < res.size() ; i++) {
			 sig[i] = 1/res.get(i);
			 if(i != res.size() - 1) d[i] = thick.get(i);
		 }
		 for(int i = 0 ; i < freqs.size() ; i++) {
			 w[i] = freqs.get(i) * 2 * Math.PI;
		 }
		 MTForward fwd = new MTForward(sig, d, w);	
	     fwd.calcPaPhi();
	     double[] phi = fwd.getPhi();
	     double[] pa = fwd.getPa();

//	        System.out.println("PA");
	        for(int i=0; i<pa.length; ++i){
		   		seriesrxyfwd.getData().add(new XYChart.Data(Math.log10(freqs.get(i)), Math.log10(pa[i])));
		   		seriespxyfwd.getData().add(new XYChart.Data(Math.log10(freqs.get(i)), 360*(phi[i]/(2*Math.PI))));
		   		seriesryxfwd.getData().add(new XYChart.Data(Math.log10(freqs.get(i)), Math.log10(pa[i])));
		   		seriespyxfwd.getData().add(new XYChart.Data(Math.log10(freqs.get(i)), 360*(phi[i]/(2*Math.PI))));
	        }

//	        System.out.println("PHI");
	        for(int i=0; i<phi.length; ++i){
//	            System.out.println(phi[i]);
	        }
	        if(!fwdInit) {
		chartRxy.getData().add(seriesrxyfwd);
	   	chartRyx.getData().add(seriesryxfwd);
		 chartPhasexy.getData().add(seriespxyfwd);
		 chartPhaseyx.getData().add(seriespyxfwd);
	        }
	        fwdInit = true;
	        }

	private ArrayList<Double> getDefaultFrequencies() {
		double minLogFreq = -3;
		double maxLogFreq = 3;
		double df = 0.1;
		ArrayList<Double> frequencies = new ArrayList<Double>();
		for(double f = minLogFreq ; f < maxLogFreq ; f += df) {
			frequencies.add(Math.pow(10, f));
		}
		return frequencies;
	}


}
