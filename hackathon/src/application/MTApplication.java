package application;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MTApplication extends BorderPane {

	public final LineChart<Number, Number> chartResistivities;
	public final LineChart<Number, Number> chartPhases;

	/*
	 * Data
	 */
	XYChart.Series seriesrxy = new XYChart.Series();
	XYChart.Series seriesryx = new XYChart.Series();
	XYChart.Series seriespxy = new XYChart.Series();
	XYChart.Series seriespyx = new XYChart.Series();

	XYChart.Series seriesResistivitiesfwd = new XYChart.Series();
	XYChart.Series seriesPhasesfwd = new XYChart.Series();

	final NumberAxis xAxisResistivities = new NumberAxis();
	final NumberAxis xAxisPhases = new NumberAxis();

	final NumberAxis yAxisResistivities = new NumberAxis();
	final NumberAxis yAxisPhases = new NumberAxis();

	public final static String LOG_FREQ = "Log Frequency";
	public final static String LOG_RES = "Log Resistivity (Ohm m)";
	public final static String RAD = "Phase (degrees)";

	TextField fileField = new TextField("resources\\TestData.txt");
	Button loadButton = new Button("Load Data");
	Button clearButton = new Button("Clear Data");
	
	LayerEntry layering = new LayerEntry(this);
	TabPane tabpane = new TabPane();
	private ArrayList<Double> freqs;
	private Stage stage;
	SplitPane sp = new SplitPane();
	public MTApplication(Stage s) {
		this.stage = s;
		xAxisResistivities.setLabel(LOG_FREQ);
		xAxisPhases.setLabel(LOG_FREQ);
		yAxisResistivities.setLabel(LOG_RES);
		yAxisPhases.setLabel(RAD);

		chartResistivities = new LineChart<Number, Number>(xAxisResistivities,yAxisResistivities);
		chartPhases = new LineChart<Number, Number>(xAxisPhases, yAxisPhases);

		// tabpane.getTabs().add(createTab("WELCOME", createWelcomePane()));
		tabpane.getTabs().add(createTab("Data", createDataPane(),"72_DATA_NET"));
		// p.getTabs().add(createTab("View Data", ));
		tabpane.getTabs().add(createTab("Geo-Electrical Model", createModelPane(),"72_EARTH1D"));
		// tabpane.getTabs().add(createTab("Set up Inversion",
		// createInversionSetupPane()));
		// tabpane.getTabs().add(createTab("Gravity", createGravityModel()));
		tabpane.setPrefWidth(700);
		
		sp.getItems().addAll(tabpane, createViewPane());
		this.setCenter(sp);
		this.setTop(getMenuBar());
	}



	private Node getMenuBar() {
		MenuBar b = new MenuBar();
		Menu file = new Menu("File");
		Menu save = new Menu("Save");
		MenuItem saveEarth = new MenuItem("Save Earth");
		Menu load = new Menu("Load");
		MenuItem loadEarth = new MenuItem("Load Earth");
		
		b.getMenus().add(file);
		file.getItems().add(save);
		file.getItems().add(load);
		save.getItems().add(saveEarth);
		load.getItems().add(loadEarth);
		
		Menu export = new Menu("Export");
		MenuItem exportDataCSV = new MenuItem("Export Data (CSV)");
		MenuItem exportDataPNG = new MenuItem("Export Data (PNG)");
		MenuItem exportEarthCSV = new MenuItem("Export Earth (CSV)");
		MenuItem exportEarthPNG = new MenuItem("Export Earth (PNG)");
		b.getMenus().add(export);
		export.getItems().add(exportDataCSV);
		export.getItems().add(exportDataPNG);
		export.getItems().add(exportEarthCSV);
		export.getItems().add(exportEarthPNG);
		initLoadEarth(loadEarth);
		initSaveEarth(saveEarth);
		initExportDataCSV(exportDataCSV);
		initExportDataPNG(exportDataPNG);
		initExportEarthCSV(exportEarthCSV);
		initExportEarthPNG(exportEarthPNG);
		return b;
	}


	private void initExportEarthPNG(MenuItem exportEarthPNG) {
		exportEarthPNG.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				layering.exportPNG();
			}
		});
	}



	private void initExportEarthCSV(MenuItem exportEarthCSV) {
		exportEarthCSV.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				layering.exportCSV();
			}
		});
	}



	private void initExportDataPNG(MenuItem exportDataPNG) {
		exportDataPNG.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				SplitPane p = ((SplitPane) sp.getItems().get(1));
				WritableImage image = p.snapshot(new SnapshotParameters(), null);

			    File file = new File("data_"+LayerEntry.getCompactDate()+".png");

			    try {
			        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
			        Desktop.getDesktop().open(file);  
			    } catch (IOException e) {
			       e.printStackTrace();		       
			    }
			}
		});
	}



	private void initExportDataCSV(MenuItem exportDataCSV) {
		exportDataCSV.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				  File file = new File("data"+LayerEntry.getCompactDate()+".csv");

				    try {
						
						BufferedWriter out = new BufferedWriter(new FileWriter(file));
						double depth = 0;
						out.write("Frequency,Rho_xy,phi_xy,rho_yx,phi_yx,RhoSynth,PhiSynth");
						out.newLine();
						layering.updateAll();
//						XYChart.Series seriesrxy = new XYChart.Series();
//						XYChart.Series seriesryx = new XYChart.Series();
//						XYChart.Series seriespxy = new XYChart.Series();
//						XYChart.Series seriespyx = new XYChart.Series();
//
//						XYChart.Series seriesResistivitiesfwd = new XYChart.Series();
//						XYChart.Series seriesPhasesfwd = new XYChart.Series();
//						seriesrxy.getData().add(new XYChart.Data(Math.log10(freqs.get(i)), Math.log10(rhoxy.get(i))));
//						seriesryx.getData().add(new XYChart.Data(Math.log10(freqs.get(i)), Math.log10(rhoyx.get(i))));
//						seriespxy.getData().add(new XYChart.Data(Math.log10(freqs.get(i)), (phasexy.get(i))));
//						seriespyx.getData().add(new XYChart.Data(Math.log10(freqs.get(i)), (phaseyx.get(i))));
						int N = seriesrxy.getData().size();
						for(int i = 0 ; i < N ; i++) {
							String freq = "" + Math.pow(10,(Double) (((XYChart.Data) seriesrxy.getData().get(i)).getXValue()));
							String rxys = "" + Math.pow(10,(Double) (((XYChart.Data) seriesrxy.getData().get(i)).getYValue()));
							String ryxs = "" + Math.pow(10,(Double) (((XYChart.Data) seriesryx.getData().get(i)).getYValue()));
							String pxys = "" + (Double) (((XYChart.Data) seriespxy.getData().get(i)).getYValue());
							String pyxs = "" + (Double) (((XYChart.Data) seriespyx.getData().get(i)).getYValue());
							String af = (seriesResistivitiesfwd.getData().size() == 0) ? "-" : "" + (Math.pow(10,(Double) (((XYChart.Data) seriesResistivitiesfwd.getData().get(i)).getYValue())));
							String pf = (seriesPhasesfwd.getData().size() == 0) ? "-" : "" + (Double) (((XYChart.Data) seriesPhasesfwd.getData().get(i)).getYValue());
							out.write(freq + "," + rxys + "," + ryxs + "," + pxys + "," + pyxs + "," + af + "," + pf);
							out.newLine();
						}
						
						
						out.close();
						Desktop.getDesktop().open(file);  
					} catch (IOException e) {
						e.printStackTrace();
					}
				
				
			}
		});
	}



	private void initLoadEarth(MenuItem loadEarth) {
		loadEarth.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				layering.load();
			}
		});
	}
	private void initSaveEarth(MenuItem saveEarth) {
		saveEarth.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				layering.save();
			}
		});
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
				for (ArrayList<Double> l : getLayering()) {
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

		for (Layer l : this.layering.layering) {
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
		SplitPane vs = new SplitPane();
		vs.setOrientation(Orientation.VERTICAL);
		vs.getItems().add(chartResistivities);
		vs.getItems().add(chartPhases);

		return vs;
	}

	private Tab createTab(String string, Node content, String icon) {
		Tab t = new Tab(string);
		t.setContent(content);
		t.setClosable(false);
		t.setGraphic(Icons.createIcon(icon + ".png", 40));
		return t;
	}

	private Node createDataPane() {

		BorderPane p = new BorderPane();
		Image i = new Image(MTApplication.class.getResource("Folder.png")
				.toExternalForm());

		Button openButton = new Button("", new ImageView(i));
		
		HBox b = new HBox(fileField, openButton, loadButton);
		VBox vb = new VBox(b,clearButton);
		p.setTop(vb);
		loadButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				loadData();
			}

		});
		clearButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				clearData();
			}

		
		});
		openButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				FileChooser c = new FileChooser();
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MT Text File", "*.txt");
				fileChooser.getExtensionFilters().add(extFilter);
				if(!new File("./resources/").exists()) {
					fileChooser.setInitialDirectory(new File("./"));	
				} else{
					fileChooser.setInitialDirectory(new File("./resources/"));
				}
					
				

				File file = fileChooser.showOpenDialog(stage);

				if (file != null) {
					fileField.textProperty().set(file.getAbsolutePath());
				}
			}
		});
		return p;
	}
	private void clearData() {
		chartResistivities.getData().remove(seriesrxy);
		chartResistivities.getData().remove(seriesryx);
		chartPhases.getData().remove(seriespxy);
		chartPhases.getData().remove(seriespyx);
		layering.updateAll();

	}
	private void loadData() {
		// tabpane.getSelectionModel().select(1); //open up data view
		File f = new File(fileField.getText());
		ArrayList<ArrayList<Double>> data = importData(f);
		chartResistivities.getData().remove(seriesrxy);
		chartResistivities.getData().remove(seriesryx);
		chartPhases.getData().remove(seriespxy);
		chartPhases.getData().remove(seriespyx);

		seriesrxy = new XYChart.Series();
		seriesryx = new XYChart.Series();
		seriespxy = new XYChart.Series();
		seriespyx = new XYChart.Series();

		seriesrxy.setName("Apparent Resistivity XY Data");
		seriesryx.setName("Apparent Resistivity YX Data");
		seriespxy.setName("Phase YX Data");
		seriespyx.setName("Phase YX Data");

		ArrayList<Double> freqs = getColumn(data, 0);
		ArrayList<Double> rhoxy = getColumn(data, 1);
		ArrayList<Double> phasexy = getColumn(data, 2);
		ArrayList<Double> rhoyx = getColumn(data, 3);
		ArrayList<Double> phaseyx = getColumn(data, 4);
		this.freqs = freqs;

		for (int i = 0; i < freqs.size(); i++) {
			seriesrxy.getData().add(new XYChart.Data(Math.log10(freqs.get(i)), Math.log10(rhoxy.get(i))));
			seriesryx.getData().add(new XYChart.Data(Math.log10(freqs.get(i)), Math.log10(rhoyx.get(i))));
			seriespxy.getData().add(new XYChart.Data(Math.log10(freqs.get(i)), (phasexy.get(i))));
			seriespyx.getData().add(new XYChart.Data(Math.log10(freqs.get(i)), (phaseyx.get(i))));
		}
		chartResistivities.getData().add(seriesrxy);
		chartResistivities.getData().add(seriesryx);
		chartPhases.getData().add(seriespxy);
		chartPhases.getData().add(seriespyx);
		layering.updateAll();
	}

	private ArrayList<Double> getColumn(ArrayList<ArrayList<Double>> data, int i) {
		ArrayList<Double> column = new ArrayList<Double>();
		for (ArrayList<Double> d : data) {
			column.add(d.get(i));
		}

		return column;

	}

	private ArrayList<ArrayList<Double>> importData(File f) {
		ArrayList<ArrayList<Double>> frequencies = new ArrayList<ArrayList<Double>>();

		try {
			String line = "";
			BufferedReader in = new BufferedReader(new FileReader(f));
			in.readLine();// skip header
			while ((line = in.readLine()) != null) {
				ArrayList<Double> frequencyEntry = new ArrayList<Double>();

				String[] split = line.split("\t");
				for (String s : split) {
					frequencyEntry.add((Double.valueOf(s)));
				}
				frequencies.add(frequencyEntry);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.sort(frequencies, new Comparator<ArrayList<Double>>() {

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
		double[] gu = inv.getData().getArray();
		double[] distances = inv.getPosition().getArray();
		double[] inversionDIRLS = inv.getDIRLS().getArray();
		double[] inversionLSQ = inv.getLSQ().getArray();
		double[] inversionWLSQ = inv.getWLSQ().getArray();
		ArrayList<ArrayList<Double>> values = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < gu.length; i++) {
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

		if (!fwdInit) {
			chartResistivities.getData().remove(seriesResistivitiesfwd);

			// chartRyx.setAnimated(false);
			chartResistivities.setAnimated(false);
			chartPhases.getData().remove(seriesPhasesfwd);

			chartPhases.setAnimated(false);
			seriesResistivitiesfwd.setName("Apparent Resistivity 1D Synthetic");
			seriesPhasesfwd.setName("Phase 1D Synthetic");

		}
		// if(seriespxyfwd.getData().size() == )
		seriesResistivitiesfwd.getData().removeAll(seriesResistivitiesfwd.getData());
		seriesPhasesfwd.getData().removeAll(seriesPhasesfwd.getData());

		ArrayList<ArrayList<Double>> layering = getLayering();
		ArrayList<Double> freqs = this.freqs;
		if (freqs == null)
			freqs = getDefaultFrequencies();
		ArrayList<Double> res = getColumn(layering, 0);
		ArrayList<Double> thick = getColumn(layering, 1);
		double[] sig = new double[res.size()];
		double[] d = new double[res.size() - 1];
		double[] w = new double[freqs.size()];
		for (int i = 0; i < res.size(); i++) {
			sig[i] = 1 / res.get(i);
			if (i != res.size() - 1)
				d[i] = thick.get(i);
		}
		for (int i = 0; i < freqs.size(); i++) {
			w[i] = freqs.get(i) * 2 * Math.PI;
		}
		MTForward fwd = new MTForward(sig, d, w);
		fwd.calcPaPhi();
		double[] phi = fwd.getPhi();
		double[] pa = fwd.getPa();
		
		for (int i = 0; i < pa.length; ++i) {
			seriesResistivitiesfwd.getData().add(new XYChart.Data(Math.log10(freqs.get(i)), Math.log10(pa[i])));
			seriesPhasesfwd.getData().add(new XYChart.Data(Math.log10(freqs.get(i)),360 * (phi[i] / (2 * Math.PI))));
		}

		if (!fwdInit) {
			chartResistivities.getData().add(seriesResistivitiesfwd);
			chartPhases.getData().add(seriesPhasesfwd);
		}
		fwdInit = true;
	}

	private ArrayList<Double> getDefaultFrequencies() {
		double minLogFreq = -3;
		double maxLogFreq = 3;
		double df = 0.1;
		ArrayList<Double> frequencies = new ArrayList<Double>();
		for (double f = minLogFreq; f < maxLogFreq; f += df) {
			frequencies.add(Math.pow(10, f));
		}
		return frequencies;
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
		LineChart<Number, Number> chart1 = new LineChart<Number, Number>(xAxis1, yAxis1);
		LineChart<Number, Number> chart2 = new LineChart<Number, Number>(xAxis2, yAxis2);
		LineChart<Number, Number> chart3 = new LineChart<Number, Number>(xAxis3, yAxis3);
		LineChart<Number, Number> chart4 = new LineChart<Number, Number>(xAxis4, yAxis4);
		XYChart.Series seriesRaw = new XYChart.Series();
		XYChart.Series seriesInv1 = new XYChart.Series();
		XYChart.Series seriesInv2 = new XYChart.Series();
		XYChart.Series seriesInv3 = new XYChart.Series();

		seriesRaw.setName("Raw");
		seriesInv1.setName("Inv IRLSQ");
		seriesInv2.setName("Inv LSQ");
		seriesInv3.setName("Inv WLSQ");
		for (ArrayList<Double> p : invGrav()) {
			seriesRaw.getData().add(new XYChart.Data<Number, Number>(p.get(0), new Double(p.get(1))));
			seriesInv1.getData().add(new XYChart.Data<Number, Number>(p.get(0), new Double(p.get(2))));
			seriesInv2.getData().add(new XYChart.Data<Number, Number>(p.get(0), new Double(p.get(3))));
			seriesInv3.getData().add(new XYChart.Data<Number, Number>(p.get(0), new Double(p.get(4))));
		}
		chart1.getData().add(seriesRaw);
		chart2.getData().add(seriesInv1);
		chart3.getData().add(seriesInv2);
		chart4.getData().add(seriesInv3);
		VBox b = new VBox(chart1, chart2, chart3	, chart4);
		return b;
	}
}
