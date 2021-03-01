package app;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class GUI_Controller {
	@FXML
    private BorderPane borderPaneA;
    @FXML
    private BorderPane borderPaneB;
    @FXML
    private Pane mainPane;
    
    @FXML
    public ToggleButton toolPen, toolHighlighter, toolEraser, toolText, toolEquation, toolDiagram;
    @FXML
    public ColorPicker penColor;
    @FXML
    public Slider penStrength;
    
    
    private Parent root;
    private Viewport viewport;
    
    
    public void initialize() {
    	// Gui
    	penStrength.setMin(0.01);
    	penStrength.setMax(0.2);
    	penStrength.setValue(0.1);
    	penColor.setValue(Color.BLACK);
    	
    	// Viewport
    	viewport = new Viewport(mainPane.getWidth(), mainPane.getHeight(), this);
    	
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
				viewport.onMouseMoved((float)event.getX(), (float)event.getY());
			}
    	});
    	
    	// Mouse Press/Release
    	mainPane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				viewport.requestFocus();
				viewport.onMousePressed(getMouseButtonID(event), (float)event.getX(), (float)event.getY());
			}
    	});
    	mainPane.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				viewport.onMouseReleased(getMouseButtonID(event), (float)event.getX(), (float)event.getY());
			}
    	});
    	mainPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				viewport.onMouseClicked(getMouseButtonID(event), (float)event.getX(), (float)event.getY());
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
    
    public void setRoot(Parent pRoot) {
    	root = pRoot;
    }
    public Parent getRoot() {
    	return root;
    }
}




