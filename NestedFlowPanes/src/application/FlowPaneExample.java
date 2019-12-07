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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class FlowPaneExample extends Application {

	float transformFactor = 1.0f;

	int seed = 1534;
	Random randomizer;

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

		// Creating a scene object
		Scene scene = new Scene(scrollPane, 700, 200);

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
		

		double maxItemHeight = 0;
		int totalArea = 0;

		ArrayList<Pane> panes = new ArrayList<Pane>();
		
		String[] subFilesAndDirectories = directory.list();

		for (String fileOrDirectoryName : subFilesAndDirectories) {
			
			File fileOrDirectory = new File(directory, fileOrDirectoryName);
			
			if (fileOrDirectory.isDirectory()) {
				panes.add(createSubTree(fileOrDirectory));
			} else {
				panes.add(createFilePane(fileOrDirectory));
			}
		}
			
		// Setting the horizontal and vertical gap between the nodes
		flowPane.setHgap(3);
		flowPane.setVgap(3);

		flowPane.autosize();
		
		// Alignments
		flowPane.setAlignment(Pos.CENTER); 
		flowPane.setColumnHalignment(HPos.CENTER); 
		flowPane.setRowValignment(VPos.CENTER); 

		// flowPane.setStyle("-fx-background-color: rgba(" + randomizer.nextInt(255) +
		// ", " + randomizer.nextInt(255) + ", " + randomizer.nextInt(255) + ", " +
		// randomizer.nextFloat() + "); -fx-background-radius: 10;");
		flowPane.setStyle("-fx-background-color: rgba(" + 255 + ", " + 255 + ", " + 255 + ", " + 0
				+ "); -fx-background-radius: 10; -fx-border-color: black");

		// Retrieving the observable list of the flow Pane
		ObservableList<Node> list = flowPane.getChildren();

		// Adding all the nodes to the flow pane and add margins
		for (Pane p : panes) {
			list.add(p);
			//FlowPane.setMargin(p, new Insets(3, 3, 3, 3));
			maxItemHeight = Math.max(maxItemHeight, p.getHeight());
			totalArea += p.getHeight() * p.getWidth();
		}
		
		// height of squared total area
		int maxHeight = (int) (Math.sqrt(totalArea) * transformFactor);
		
		// Setting the
		flowPane.setPrefWrapLength(Math.max(maxHeight, maxItemHeight) + 6 /* Insets */);
			
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


		double paneHeight = lineCtr;
		double paneWidth = maxLineLength;
		
		Pane newPane = new Pane();
		newPane.setPrefSize(paneWidth, paneHeight);
		newPane.setMaxSize(paneWidth, paneHeight);
		newPane.setStyle("-fx-background-color: rgba(" + randomizer.nextInt(255) + ", " + randomizer.nextInt(255)
		+ ", " + randomizer.nextInt(255) + ", 0.5); -fx-background-radius: 10;");

		// Add label
		Label newLabel = new Label(file.getName() + "\n" + (int)paneHeight + "x" + (int)paneWidth);
		newLabel.setFont(new Font(8.0f));

		// centering Text in Pane:
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