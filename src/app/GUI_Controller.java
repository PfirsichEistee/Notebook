package app;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class GUI_Controller {
	@FXML
    private BorderPane borderPaneA;
    @FXML
    private BorderPane borderPaneB;
    @FXML
    private Pane mainPane;
    
    
    private Viewport viewport;
    
    
    public void initialize() {
    	viewport = new Viewport(mainPane.getWidth(), mainPane.getHeight());
    	
    	mainPane.getChildren().add(viewport);
    	
    	// Events
    	
    	// Size
    	mainPane.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				viewport.resize(mainPane.getWidth(), mainPane.getHeight());
			}
    	});
    	mainPane.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				viewport.resize(mainPane.getWidth(), mainPane.getHeight());
			}
    	});
    	
    	// Mouse Moved
    	mainPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				viewport.setMouse((float)event.getX(), (float)event.getY());
			}
    	});
    	
    	// Mouse Press/Release
    	mainPane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				viewport.onMousePressed(getMouseButtonID(event), (float)event.getX(), (float)event.getY());
			}
    	});
    	mainPane.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				viewport.onMouseReleased(getMouseButtonID(event), (float)event.getX(), (float)event.getY());
			}
    	});
    	
    	// Mouse Drag
    	mainPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				viewport.onMouseDragged(getMouseButtonID(event), (float)event.getX(), (float)event.getY());
			}
    	});
    	
    	// Mouse Scroll
    	mainPane.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				int dir = 0;
				if (event.getDeltaY() != 0) {
					dir = (event.getDeltaY() > 0 ? 1 : -1);
				}
				
				viewport.onMouseScrolled(dir);
			}
    	});
    	
    	
    }
    
    
    public int getMouseButtonID(MouseEvent event) {
    	if (event.getButton() == MouseButton.PRIMARY) {
    		return 0;
    	} else if (event.getButton() == MouseButton.SECONDARY) {
    		return 1;
    	}
    	return 2;
    }
}



