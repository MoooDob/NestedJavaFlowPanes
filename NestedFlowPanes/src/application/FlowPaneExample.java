package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

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
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class FlowPaneExample extends Application {

	float transformFactor = 1.0f;

	int seed = 1534;
	Random randomizer;
	
	int gap = 3;
	
	boolean showFiles = false;
	boolean showBorder = false;
	boolean usePadding = false;

	@Override
	public void start(Stage stage) {

		// init randomizer
		randomizer = new Random(seed);

		// mother of all flow panes
		final Pane flowPane; 

		// ask for directory

		DirectoryChooser dc = new DirectoryChooser();
		File selectedDirectory = dc.showDialog(stage);

		if (selectedDirectory == null) {
			flowPane = null;
			System.out.println("No directory selected. Terminated.");
		} else {
			// mother of all flow panes
			flowPane = createSubTree(selectedDirectory);
		}

		// ScrollPane
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(flowPane);
		scrollPane.setPannable(true);
		
		// Create operator
		AnimatedZoomOperator zoomOperator = new AnimatedZoomOperator();

		if (flowPane != null) {
			// Listen to scroll events (similarly you could listen to a button click, slider, ...)
			flowPane.setOnScroll(new EventHandler<ScrollEvent>() {
			    @Override
			    public void handle(ScrollEvent event) {
			        double zoomFactor = 1.5;
			        if (event.getDeltaY() <= 0) {
			            // zoom out
			            zoomFactor = 1 / zoomFactor;
			        }
			        zoomOperator.zoom(flowPane, zoomFactor, event.getSceneX(), event.getSceneY());
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
		
	}

	private Pane createSubTree(File directory) {
		
		// Creating a Flow Pane
		FlowPane flowPane = new FlowPane(Orientation.VERTICAL);
		
		double maxPaneHeight = 0;
		int totalArea = 0;
		int totalNodesHeight = 0;

		ArrayList<Node> nodes = new ArrayList<Node>();
		
		// Add label
		Label newLabel = new Label(directory.getName());
		newLabel.setFont(new Font(8.0f));
		//newLabel.setBackground(new Background(new BackgroundFill(Color.CORNFLOWERBLUE, CornerRadii.EMPTY, Insets.EMPTY)));

		nodes.add(newLabel);		
		//flowPane.getChildren().add(newLabel);
		
				
		int numSubDirs = 0;
		String[] subFilesAndDirectories = directory.list();		

		for (String fileOrDirectoryName : subFilesAndDirectories) {
			
			File fileOrDirectory = new File(directory, fileOrDirectoryName);
			
			if (fileOrDirectory.isDirectory()) {
				nodes.add(createSubTree(fileOrDirectory));
				numSubDirs++;
			} else {
				if (showFiles) {
					nodes.add(createFilePane(fileOrDirectory));
				}
			}
		}
			
		// Setting the horizontal and vertical gap between the nodes
		flowPane.setHgap(gap);
		flowPane.setVgap(gap);
		
		if (usePadding) {flowPane.setPadding(new Insets(gap,gap,gap,gap));}

		//flowPane.autosize();
		
		// Alignments
		flowPane.setAlignment(Pos.TOP_LEFT); 
		flowPane.setColumnHalignment(HPos.LEFT); 
		flowPane.setRowValignment(VPos.TOP); 

		// flowPane.setStyle("-fx-background-color: rgba(" + randomizer.nextInt(255) +
		// ", " + randomizer.nextInt(255) + ", " + randomizer.nextInt(255) + ", " +
		// randomizer.nextFloat() + "); -fx-background-radius: 10;");
		flowPane.setStyle("-fx-background-color: rgba(" + 255 + ", " + 255 + ", " + 255 + ", " + 0
				+ "); -fx-background-radius: 10; " + (showBorder ? "-fx-border-color: gray" : "")
		);


		// Retrieving the observable list of the flow Pane
		ObservableList<Node> list = flowPane.getChildren();


		
		// Adding all the nodes to the flow pane and add margins
		for (Node node : nodes) {

			list.add(node);
			
			double currentHeight = 
					(node instanceof FlowPane ? ((FlowPane)node).getPrefWrapLength() + 
												(showBorder ? 2 : 0) /*top + bottom border*/ + 
												(usePadding ? 2 * gap : 0) /*padding*/ 
					: (node instanceof Pane ? ((Pane)node).getPrefHeight() : 12 /*label*/)
			);
			maxPaneHeight = Math.max(maxPaneHeight, currentHeight);
			totalNodesHeight += currentHeight;
			// totalArea += p.getPrefHeight() * p.getPrefWidth();
//			System.out.println((p instanceof FlowPane ? "Folder" : "File") + ": " + ((Label)p.getChildren().get(0)).getText() + 
//					" p.PrefHeight: " + p.getPrefHeight() + " p.PrefWidth: " + p.getPrefWidth() + 
//					" p.Height: " + p.getHeight() + " p.Width: " + p.getWidth() +
//					(p instanceof FlowPane ? " p.PrefWrap: " + ((FlowPane)p).getPrefWrapLength() : "") +
//					" totalHeight: " + totalPanesHeight);
		}
			
		// height of squared total area
		int maxHeight = (int) (Math.sqrt(totalArea) * transformFactor);
		
		// Setting the
		// flowPane.setPrefWrapLength(Math.max(maxHeight, maxItemHeight) + 6 /*gaps*/);
		flowPane.setPrefWrapLength(
				totalNodesHeight + 
				(nodes.size() - 1) * gap /*gaps, including label*/ //+ 
//				(usePadding ? 2 * gap : 0) /*padding*/
		); 				

			
		return flowPane;
	}

	private Pane createFilePane(File file) {

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


//		double paneHeight = lineCtr;
//		double paneWidth = maxLineLength;
		double paneHeight = 12;
		double paneWidth = 50;
		
		Pane newPane = new Pane();
		newPane.setPrefSize(paneWidth, paneHeight);
		newPane.setMaxSize(paneWidth, paneHeight);
		newPane.setStyle("-fx-background-color: rgba(" + randomizer.nextInt(255) + ", " + randomizer.nextInt(255)
		+ ", " + randomizer.nextInt(255) + ", 0.5); -fx-background-radius: 10;");

		// add label
		// Label newLabel = new Label(file.getName() + "\n" + (int)paneHeight + "x" + (int)paneWidth);
		Label newLabel = new Label(file.getName() +  (int)paneHeight + "x" + (int)paneWidth);
		newLabel.setTextAlignment(TextAlignment.CENTER);
		newLabel.setFont(new Font(8.0f));

		// centering label in pane:
		// https://stackoverflow.com/questions/36854031/how-to-center-a-label-on-a-pane-in-javafx
		newLabel.layoutXProperty().bind(newPane.widthProperty().subtract(newLabel.widthProperty()).divide(2));
		newLabel.layoutYProperty().bind(newPane.heightProperty().subtract(newLabel.heightProperty()).divide(2));

		newPane.getChildren().add(newLabel);

		return newPane;
	}

	public static void main(String args[]) {
		launch(args);
	}
}