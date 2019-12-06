import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
         
public class FlowPaneExample extends Application { 
   @Override 
   public void start(Stage stage) {      
	   
	   
	   FlowPane subflowPane = createBlock(5, 120, new Color(67, 35, 85), 1233);
	   FlowPane flowPane = createBlock(10, 300, new Color(103, 201, 167), 1231, subflowPane);

      
      // ScrollPane
      ScrollPane scrollPane = new ScrollPane();
      scrollPane.setContent(flowPane);
      scrollPane.setPannable(true);
        
      //Creating a scene object 
      Scene scene = new Scene(scrollPane, 550, 200);  
      
      // add stylesheet to scene
      scene.getStylesheets().add("styles.css");
      
      //Setting title to the Stage 
      stage.setTitle("Flow Pane Example"); 
         
      //Adding scene to the stage 
      stage.setScene(scene); 
         
      //Displaying the contents of the stage 
      stage.show(); 

//      System.out.println("flowPane.Width: " + flowPane.getWidth());

   }
private FlowPane createBlock(int numItems, 
		int maxHeight, 
		Color backgroundColor, 
		int randomseed,
		Pane ... subPanes
		) {
	    
       Random random = new Random(randomseed);
       
	   double maxItemHeight = 0;
	   
	   ArrayList<Pane> items = new ArrayList<Pane>();
	   Collections.addAll(items, subPanes);

	   for(int idx=0; idx<=numItems; idx++) {
		   double height = random.nextInt(100);
		   double width = random.nextInt(70);
		   maxItemHeight = Math.max(maxItemHeight, height);
		   Pane newPane = new Pane();
		   items.add(newPane);
		   Label newLabel = new Label("" + (idx + 1));
		   
		   // centering Text in Pane: https://stackoverflow.com/questions/36854031/how-to-center-a-label-on-a-pane-in-javafx
		   newLabel.layoutXProperty().bind(newPane.widthProperty().subtract(newLabel.widthProperty()).divide(2));
		   newLabel.layoutYProperty().bind(newPane.heightProperty().subtract(newLabel.heightProperty()).divide(2));
		   
		   newPane.getChildren().add(newLabel);
		   newPane.setPrefSize(width, height);
		   newPane.setStyle("-fx-background-color: rgba(" + random.nextInt(255)+ ", " + random.nextInt(255) + ", " + random.nextInt(255) + ", 0.5); -fx-background-radius: 10;");
	   }
      
      //Creating a Flow Pane 
      FlowPane flowPane = new FlowPane(Orientation.VERTICAL);    
       
      //Setting the horizontal gap between the nodes 
      flowPane.setHgap(0);
      flowPane.setVgap(0);
      
//     flowPane.setMaxHeight(Math.max(maxHeight, maxItemHeight) + 6 /*Insets*/);
//      flowPane.setMinHeight(Math.max(maxHeight, maxItemHeight) + 6 /*Insets*/);
//      flowPane.setPrefHeight(Math.max(maxHeight, maxItemHeight) + 6 /*Insets*/);
      
      flowPane.setPrefWrapLength(Math.max(maxHeight, maxItemHeight) + 6 /*Insets*/);
      
      flowPane.autosize();
      
//      System.out.println("flowPane.Width: " + flowPane.getWidth());
      
      flowPane.setStyle("-fx-background-color: rgba(" + backgroundColor.getRed() + ", " + backgroundColor.getGreen() + ", " + backgroundColor.getBlue() + ", " + backgroundColor.getAlpha() + "); -fx-background-radius: 10;");
           
      //Retrieving the observable list of the flow Pane 
      ObservableList<Node> list = flowPane.getChildren(); 
      
      //Adding all the nodes to the flow pane and add margins 
      for(int idx=0; idx<numItems; idx++) {
    	  list.add(items.get(idx)); 
    	  FlowPane.setMargin(items.get(idx), new Insets(3, 3, 3, 3));
	   }
      
//      System.out.println("flowPane.Width: " + flowPane.getWidth());
	return flowPane;
} 
   public static void main(String args[]){
      launch(args); 
   } 
}