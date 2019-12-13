package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.imaging.color.ColorCieLab;
import org.apache.commons.imaging.color.ColorCieLch;
import org.apache.commons.imaging.color.ColorConversions;
import org.apache.commons.io.FilenameUtils;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

public class FlowPaneExample extends Application {

	float transformFactor = 1.0f;

	int seed = 1524;
	Random randomizer;

	int gap = 3;

	boolean showFiles = true;
	boolean showRandomDirectoryBackgroundColor = false;
	boolean showBorder = true;
	boolean usePadding = true;
	boolean showFilenames = true;
	int maxDirLevels = 100;

	// Files with this extension will be shown, null or empty array => all files 
	final String[] fileExtensionFilter = {}; //{"java"}; // {"java", "cpp", "h"} // null /*=> all*/

	// files with this extension will shown using their dimension (max line length x lines),
	// other files will be shown using an equal sized rounded rectangle
	// null or empty array => show all files with dimensions
	final String[] dimensionDisplayExtensionFilter = {}; // {"java"}

	// files with this file name will be explicitly shown using their dimension 
	// (max line length x lines)
	final String[] dimensionDisplayFilenameFilter = {}; // {"readme.md"}


	// **********************

	// init the tooltip
	Tooltip tooltip = new Tooltip("No Tooltip");
	
	// Array for all files
	ArrayList<String> allFiles = new ArrayList<String>();

	// Array for all files
	HashMap<String, ColorCieLab> fileColors = new HashMap<String,ColorCieLab>(); 

	
	// Color space
	final double L_MIN = 10;
	final double L_MAX = 90;
	
	final int A_MIN = -128;
	final int A_MAX =  127;
	final int B_MIN = -128;
	final int B_MAX =  127;
	
	public final class ImmutableTriple<L, M, R> {

	    public final L left;
	    public final M middle;
	    public final R right;

	    public <L, M, R> ImmutableTriple<L, M, R> of(final L left, final M middle, final R right) {
	        return new ImmutableTriple<L, M, R>(left, middle, right);
	    }

	    public ImmutableTriple(final L left, final M middle, final R right) {
	        super();
	        this.left = left;
	        this.middle = middle;
	        this.right = right;
	    }

	    //-----------------------------------------------------------------------
	    public L getLeft() {
	        return left;
	    }

	    public M getMiddle() {
	        return middle;
	    }

	    public R getRight() {
	        return right;
	    }
	}

	
	@Override
	public void start(Stage stage) {

		// init randomizer
		randomizer = new Random(seed);

		// mother of all flow panes
		final Pane root; // = new FlowPane(); 

		// Prepare tooltip
		tooltip.setConsumeAutoHidingEvents(true);
		tooltip.setTextAlignment(TextAlignment.LEFT);



		// ask for directory

		DirectoryChooser dc = new DirectoryChooser();
		File selectedDirectory = dc.showDialog(stage);

		if (selectedDirectory == null) {
			root = null;
			System.out.println("No directory selected. Terminated.");
		} else {
						
			// collect all files of the folder structure and sort
			int totalNumOfLevels = visitDirectory(selectedDirectory, 0, hashAB(A_MIN,B_MIN), hashAB(A_MAX,B_MAX));			
			Collections.sort(allFiles);
			
			// create flowpane for displaying the file panes
			FlowPane flowPane = new FlowPane(Orientation.VERTICAL);

			// Setting the horizontal and vertical gap between the nodes
			flowPane.setHgap(gap);
			flowPane.setVgap(gap);

			// Padding
			if (usePadding) {flowPane.setPadding(new Insets(gap,gap,gap,gap));}
			
			// Retrieving the observable list of the flow Pane
			ObservableList<Node> list = flowPane.getChildren();
			
			// Alignments
			flowPane.setAlignment(Pos.TOP_LEFT); 
			flowPane.setColumnHalignment(HPos.LEFT); 
			flowPane.setRowValignment(VPos.TOP); 
			
			flowPane.setStyle("-fx-background-color: rgba(" + 255 + ", " + 255 + ", " + 255 + ", " + 0.5f
					+ "); -fx-background-radius: 10; " + (showBorder ? "-fx-border-color: blue; -fx-border-style: dotted;" : "")
			);
			
			
			// Calculate the RGB colors 
			HashMap<String, Color> RGBColors = new HashMap<String, Color>();
			for (String fullfilename : fileColors.keySet()) {
				ColorCieLab LabColor = fileColors.get(fullfilename);
				double L = L_MIN + (L_MAX - L_MIN)  * LabColor.L /*contains the level*/ / totalNumOfLevels;
				Color parentDirectoryColor = convertLabToFXColor(new ColorCieLab(L, LabColor.a, LabColor.b));
				RGBColors.put(fullfilename, parentDirectoryColor);
			}
			
			
			double maxPaneHeight = 0;
			double totalArea = 0;
			double totalPanesHeight = 0;
			
			// visit all files and folders
			for (int i = 0; i < allFiles.size(); i++) {
				
				File file = new File(allFiles.get(i));				
				Color parentDirectoryColor = RGBColors.get(file.getAbsolutePath());
				
				ImmutableTriple<Pane,Double,Double> result = createFilePane(file, parentDirectoryColor);
				Pane pane = result.getLeft();
				double paneWidth = result.getMiddle();
				double paneHeight = result.getRight();

				list.add(pane);

				double currentHeight = paneHeight;
				maxPaneHeight = Math.max(maxPaneHeight, currentHeight);
				totalPanesHeight += currentHeight;
				totalArea += paneHeight * paneWidth;
				//			System.out.println((p instanceof FlowPane ? "Folder" : "File") + ": " + ((Label)p.getChildren().get(0)).getText() + 
				//					" p.PrefHeight: " + p.getPrefHeight() + " p.PrefWidth: " + p.getPrefWidth() + 
				//					" p.Height: " + p.getHeight() + " p.Width: " + p.getWidth() +
				//					(p instanceof FlowPane ? " p.PrefWrap: " + ((FlowPane)p).getPrefWrapLength() : "") +
				//					" totalHeight: " + totalPanesHeight);
			}

			// height of squared total area
			double areaHeight = Math.sqrt(totalArea) * transformFactor;
			double prefHeight = Math.max(areaHeight, maxPaneHeight);

			prefHeight = Math.min(totalPanesHeight, areaHeight);

			// Setting preferred heights
			flowPane.setPrefWrapLength(
					prefHeight
					+ (allFiles.size() - 1) * gap /*gaps*/
					// + 2 /*border*/
					); 	

			//bindTooltip(flowPane,tooltip);
			
			root = flowPane;


			// ScrollPane
			ScrollPane scrollPane = new ScrollPane();
			scrollPane.setContent(root);
			scrollPane.setPannable(true);

			// Create operator
			AnimatedZoomOperator zoomOperator = new AnimatedZoomOperator();

			if (root != null) {
				// Listen to scroll events (similarly you could listen to a button click, slider, ...)
				root.setOnScroll(new EventHandler<ScrollEvent>() {
					@Override
					public void handle(ScrollEvent event) {
						double zoomFactor = 1.5;
						if (event.getDeltaY() <= 0) {
							// zoom out
							zoomFactor = 1 / zoomFactor;
						}
						zoomOperator.zoom(root, zoomFactor, event.getSceneX(), event.getSceneY());
					}
				});
			}

			// Creating a scene object
			Scene scene = new Scene(scrollPane, 700, 800);

			// add stylesheet to scene
			scene.getStylesheets().add("styles.css");

			// Setting title to the Stage
			stage.setTitle("Directory structure of " + selectedDirectory.getAbsolutePath());

			// Adding scene to the stage
			stage.setScene(scene);

			// Displaying the contents of the stage
			stage.show();	

			FileWriter csvWriter;
			try {
				csvWriter = new FileWriter("areas.csv");
				csvWriter.append(String.format("\"name\",\"total px²\",\"used px²\",\"rel\"\n"));
				calcTreeArea(csvWriter, root);
				csvWriter.flush();
				csvWriter.close();
			} catch (IOException e) {
				System.out.println("Problem while writing to areas.csv");
			} 

		}
	}



	private Color convertLabToFXColor(ColorCieLab color) {				
		java.awt.Color colorRGB_awt = new java.awt.Color(ColorConversions.convertXYZtoRGB(ColorConversions.convertCIELabtoXYZ(color)));
		Color fxColor = new Color((double)colorRGB_awt.getRed()/256, (double)colorRGB_awt.getGreen()/256, (double)colorRGB_awt.getBlue()/256, (double)colorRGB_awt.getAlpha()/256);
//		System.out.println(" Lab: " + color.toString() + 
//				" RGB: " + fxColor.toString() +
//				" R " + ((double)colorRGB_awt.getRed()/256) + 
//		        " G " + ((double)colorRGB_awt.getRed()/256) + 
//				" B " + ((double)colorRGB_awt.getRed()/256) + 
//				" A " + ((double)colorRGB_awt.getAlpha()/256)
//		);
		return fxColor;
	}

	
	
	private Pair<Double, Double> calcTreeArea(FileWriter csvWriter, Pane root) throws IOException {
		String name = "root";
		double totalArea = 0;
		double totalUsedArea = 0;
		for (Node node : root.getChildren()) {
			String filename = ((Label)((Pane)node).getChildren().get(0)).getText();
			double pHeight = ((Pane)node).getHeight();
			double pWidth = ((Pane)node).getWidth();
			double area = pHeight * pWidth ; 
			csvWriter.append(String.format(Locale.US, "%s,%1.0f,%1.0f,%1.2f\n", filename , area, area, 1.0));
			totalUsedArea += area;
		}
		totalArea += ((Pane)root).getHeight() * ((Pane)root).getWidth();
		csvWriter.append(String.format(Locale.US, "%s,%1.0f,%1.0f,%1.2f\n", name , totalArea, totalUsedArea, totalArea/totalUsedArea));
		return new Pair<Double, Double>(totalArea, totalUsedArea);
	}

	
	
	
	private int visitDirectory(File directory, int level, double min_ab, double max_ab) {

		int numOfLevels = level;
//		System.out.println(directory.getAbsolutePath() + 
//				" L: " + level + 
//				" min_ab: " + min_ab + 
//				" max_ab: " + max_ab
//		);		
		// Filter
		String[] childFilesAndDirectories = directory.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				File file = new File(dir, name);
				if (fileExtensionFilter == null || fileExtensionFilter.length == 0 ) {
					// No Filter defined
					return true;
				} else {
					// check if file extension is in the list of allowed extensions
					return file.isDirectory() || Arrays.stream(fileExtensionFilter).anyMatch(FilenameUtils.getExtension(name.toLowerCase())::equals);
				}
			};
		});	
		
		// determine the number of child directories
		String[] childDirectories = directory.list(new FilenameFilter() {
			  @Override
			  public boolean accept(File current, String name) {
			    return new File(current, name).isDirectory();
			  }
			});
		int numOfChildDirectories = childDirectories.length;
				
		// Color calculation
		double delta_ab = max_ab - min_ab;
		double part_ab = delta_ab / numOfChildDirectories;
		double directory_ab = (max_ab + min_ab) / 2; // center of current a,b color range
		Pair<Integer, Integer> AB = unhashAB((int)directory_ab);
		int a = AB.getKey();
		int b = AB.getValue();
		ColorCieLab directoryColor = new ColorCieLab(level, a, b); 
		
//		System.out.println(" delta: " + delta_ab + 
//				" num: " + numOfChildDirectories + 
//				" part_ab: " + part_ab + 
//				" dirab:" + directory_ab
//		);
		
		int childDirectoriesCounter = 0;
		for (String FilesAndDirectories : childFilesAndDirectories) {

			
			File fileOrDirectory = new File(directory, FilesAndDirectories);

			if (fileOrDirectory.isDirectory()) {
				double current_min_ab = min_ab + childDirectoriesCounter * part_ab;
				double current_max_ab = min_ab + childDirectoriesCounter * part_ab + part_ab;
				
//				System.out.println(" " + fileOrDirectory.getAbsolutePath() + 
//						" current_min_ab: " + current_min_ab + 
//						" current_max_ab: " + current_max_ab
//				);

				numOfLevels = Math.max(numOfLevels, visitDirectory(fileOrDirectory, level + 1, current_min_ab, current_max_ab));
				childDirectoriesCounter++;
			} else {
				if (showFiles) {
					String s = fileOrDirectory.getAbsolutePath();
					allFiles.add(fileOrDirectory.getAbsolutePath());
					fileColors.put(s, directoryColor);
				}
			}
		}
		
		return numOfLevels;
		
	}
	
	

	private ImmutableTriple<Pane, Double, Double> createFilePane(File file, Color directoryColor) {

		// analyse file to get height and width
		int lineCtr = 0;
		int maxLineLength = 0;
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				maxLineLength = Math.max(maxLineLength, line.length());
				lineCtr++;
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		double paneHeight = 0;;
		double paneWidth = 0;;
		if (Arrays.stream(dimensionDisplayFilenameFilter).anyMatch(file.getName().toLowerCase()::equals)
				|| dimensionDisplayExtensionFilter == null 
				|| dimensionDisplayExtensionFilter.length == 0 
				|| Arrays.stream(dimensionDisplayExtensionFilter).anyMatch(FilenameUtils.getExtension(file.getName().toLowerCase())::equals)
				) {
			// extension is in dimensionDisplayExtensionFilter
			paneHeight = lineCtr;
			paneWidth = maxLineLength;        	
		} else {
			// extension is not in dimensionDisplayExtensionFilter
			paneHeight = 12;
			paneWidth = 50;        
		}

		
		Pane newPane = new Pane();
		newPane.setPrefSize(paneWidth, paneHeight);
		newPane.setMaxSize(paneWidth, paneHeight);
		newPane.setBackground(new Background(new BackgroundFill(directoryColor, CornerRadii.EMPTY, Insets.EMPTY)));
//		newPane.setStyle("-fx-background-color: rgba(" + randomizer.nextInt(255) + ", " + randomizer.nextInt(255) +  ", " + randomizer.nextInt(255) + ", 0.5); -fx-background-radius: 10;");
		newPane.setStyle("-fx-border-color: blue; -fx-border-style: dotted;");
		// add label

		// Label newLabel = new Label(file.getName() + "\n" + (int)paneHeight + "x" + (int)paneWidth);
		Label newLabel = new Label(file.getName() + " " + (int)paneHeight + "x" + (int)paneWidth);
		newLabel.setTextAlignment(TextAlignment.CENTER);
		newLabel.setFont(new Font(8.0f));
		newLabel.setVisible(showFilenames);

		// centering label in pane:
		// https://stackoverflow.com/questions/36854031/how-to-center-a-label-on-a-pane-in-javafx
		newLabel.layoutXProperty().bind(newPane.widthProperty().subtract(newLabel.widthProperty()).divide(2));
		newLabel.layoutYProperty().bind(newPane.heightProperty().subtract(newLabel.heightProperty()).divide(2));

		newPane.getChildren().add(newLabel);

		bindTooltip(newPane, tooltip);
		bindTooltip(newLabel, tooltip);

		return new ImmutableTriple<Pane,Double,Double>(newPane, paneWidth, paneHeight);
	}

	public static void main(String args[]) {
		launch(args);
	}

	public static void bindTooltip(final Node node, final Tooltip tooltip){
		node.setOnMouseMoved(new EventHandler<MouseEvent>(){
			@Override  
			public void handle(MouseEvent event) {
				// +15 moves the tooltip 15 pixels below the mouse cursor;
				// if you don't change the y coordinate of the tooltip, you
				// will see constant screen flicker
				tooltip.setText(
						node instanceof Label ? ((Label)node).getText() :
							 ((Label)((Pane)node).getChildren().get(0)).getText() 
				);		    	
				tooltip.show(node, event.getScreenX() + 1 , event.getScreenY() - 30);
				//		         System.out.println(node.getClass().getName() + " " + tooltip.getText() + " MouseMove");
				event.consume();
			}
		});  
		node.setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event){
				tooltip.hide();
				//		         System.out.println(node.getClass().getName() + " " + tooltip.getText() + " MouseExit");
				event.consume();
			}
		});
	}
	
	
	private static int hashAB(int a, int b) {
        return a * 256 + b;
	}
	
	
	private static Pair<Integer,Integer> unhashAB(int hash) {
		int a = (int) hash / 256;
		int b = hash - a * 256;      
        return new Pair<Integer,Integer>(a,b);
	}
}

