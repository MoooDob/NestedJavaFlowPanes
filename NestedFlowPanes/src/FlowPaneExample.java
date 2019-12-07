import java.util.ArrayList;
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
	
   int maxNestingDepth = 3;
   int maxNumberOfPanes = 30;
   int maxNumberOfChilds = 3;
   
   int seed = 1534;	   
   Random randomizer; 
	   
   @Override 
   public void start(Stage stage) {
	   
	  randomizer = new Random(seed);
	   
	  Pane flowPane = createTree(0);	  
      
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

   }
private Pane createTree( 
		int level
		) {
	
	int numOfChilds = randomizer.nextInt(this.maxNumberOfChilds);
	Pane[] childPanes;
	
	if (level < maxNestingDepth) {
		
		childPanes = new Pane[numOfChilds];
				
		for (int idx = 0; idx < numOfChilds; idx++) {
			childPanes[idx] = createTree(level++);
		}
		
	} else childPanes = new Pane[0];
	
	Pane pane = createBlock(childPanes);
	   	   
	return pane;
}


private FlowPane createBlock(Pane ... subPanes) {
       
	   float transformFactor = 1.0f;
       
	   double maxItemHeight = 0;
	   int totalArea = 0;
	   
	   // add subPanes to list
	   ArrayList<Pane> panes = new ArrayList<Pane>();
	   for (Pane p : subPanes) {
		   panes.add(p);
		   if (p == null) {
			   totalArea = totalArea;
		   }
		   maxItemHeight = Math.max(maxItemHeight, p.getHeight());
		   totalArea += p.getHeight() * p.getWidth();
	   }
//	   Collections.addAll(items, subPanes);
	   
	   int numOfPanes = randomizer.nextInt(this.maxNumberOfPanes);

	   for(int idx=0; idx<numOfPanes; idx++) {
		   double paneHeight = randomizer.nextInt(100);
		   double paneWidth = randomizer.nextInt(70);
		   maxItemHeight = Math.max(maxItemHeight, paneHeight);
		   totalArea += paneHeight * paneWidth;
		   Pane newPane = new Pane();
		   panes.add(newPane);
		   Label newLabel = new Label("" + (idx + 1));
		   
		   // centering Text in Pane: https://stackoverflow.com/questions/36854031/how-to-center-a-label-on-a-pane-in-javafx
		   newLabel.layoutXProperty().bind(newPane.widthProperty().subtract(newLabel.widthProperty()).divide(2));
		   newLabel.layoutYProperty().bind(newPane.heightProperty().subtract(newLabel.heightProperty()).divide(2));
		   
		   newPane.getChildren().add(newLabel);
		   newPane.setPrefSize(paneWidth, paneHeight);
		   newPane.setStyle("-fx-background-color: rgba(" + randomizer.nextInt(255) + ", " + randomizer.nextInt(255) + ", " + randomizer.nextInt(255) + ", 0.5); -fx-background-radius: 10;");
	   }
	   
	   // height of squared total area 
	   int maxHeight = (int) (Math.sqrt(totalArea) * transformFactor); 
      
      //Creating a Flow Pane 
      FlowPane flowPane = new FlowPane(Orientation.VERTICAL);    
       
      //Setting the horizontal and vertical gap between the nodes 
      flowPane.setHgap(0);
      flowPane.setVgap(0);
      
      //Setting the 
      flowPane.setPrefWrapLength(Math.max(maxHeight, maxItemHeight) + 6 /*Insets*/);
      
      flowPane.autosize();
          
      //flowPane.setStyle("-fx-background-color: rgba(" + randomizer.nextInt(255) + ", " + randomizer.nextInt(255) + ", " + randomizer.nextInt(255) + ", " + randomizer.nextFloat() + "); -fx-background-radius: 10;");
      flowPane.setStyle("-fx-background-color: rgba(" + 255 + ", " + 255 + ", " + 255 + ", " + 0 + "); -fx-background-radius: 10; -fx-border-color: black");
           
      //Retrieving the observable list of the flow Pane 
      ObservableList<Node> list = flowPane.getChildren(); 
      
      //Adding all the nodes to the flow pane and add margins 
      for(Pane p : panes) {
    	  list.add(p); 
    	  FlowPane.setMargin(p, new Insets(3, 3, 3, 3));
	   }
      
	return flowPane;
} 
   public static void main(String args[]){
      launch(args); 
   } 
}