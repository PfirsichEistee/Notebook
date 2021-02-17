package app;

import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

public class Viewport extends Canvas {
	// ATTRIBUTES //
	
	// Editor Variables
	private GUI_Controller gui;
	private GraphicsContext gc;
	
	private float mouseX, mouseY;
	
	private float zoom;
	private float positionX, positionY; // in cm; upper-left corner
	private float rectW, rectH; // in cm; bounding box of canvas
	
	private Tool currentTool;
	
	private boolean showGrid;
	
	
	// Current Data (Data is public, so Tools can modify it)
	public ArrayList<Line> lineList;
	public ArrayList<String> textboxList;
	
	
	// CONSTRUCTOR //
	
	public Viewport(double pWidth, double pHeight, GUI_Controller pGui) {
		super(pWidth, pHeight);
		gui = pGui;
		
		gc = getGraphicsContext2D();
		gc.setLineCap(StrokeLineCap.ROUND);
		
		mouseX = -20;
		mouseY = -20;
		
		zoom = 35;
		positionX = -0.5f;
		positionY = -0.5f;
		rectW = 0;
		rectH = 0;
		
		showGrid = true;
		
		
		
		// Prepare other classes
		Tool.viewport = this;
		
		
		// Prepare Data
		lineList = new ArrayList<Line>();
		
		
		// TESTING HERE
		currentTool = new Tool_Pen();
		
		
		// Lastly..
		initGuiEvents();
	}
	
	
	// METHODS //
	
	public void draw() {
		float pixelPerCm = getPixelPerCm();
		
		
		gc.setFill(Color.DARKGRAY);
		gc.fillRect(0, 0, getWidth(), getHeight());
		
		gc.save();
		gc.translate(-positionX * pixelPerCm, -positionY * pixelPerCm);
		
		// Draw Background
		gc.setFill(Color.GRAY);
		gc.fillRect(-pixelPerCm / 4, -pixelPerCm / 4, getWidth() - (-positionX * pixelPerCm) + pixelPerCm, getHeight() - (-positionY * pixelPerCm) + pixelPerCm);
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, getWidth() - (-positionX * pixelPerCm), getHeight() - (-positionY * pixelPerCm));
		if (showGrid) {
			gc.setStroke(new Color(0.2f, 0.2f, 1, 0.4f));
			
			// Top-left corner
			float rootX = (positionX * pixelPerCm) - ((positionX * pixelPerCm) % pixelPerCm);
			float rootY = (positionY * pixelPerCm) - ((positionY * pixelPerCm) % pixelPerCm);
			
			for (int x = 0; x <= (Math.floor(getWidth() / pixelPerCm) + 1); x++) {
				gc.strokeLine(rootX + x * pixelPerCm, rootY, rootX + x * pixelPerCm, rootY + getHeight() + pixelPerCm);
			}
			for (int y = 0; y <= (Math.floor(getHeight() / pixelPerCm) + 1); y++) {
				gc.strokeLine(rootX, rootY + y * pixelPerCm, rootX + getWidth() + pixelPerCm, rootY + y * pixelPerCm);
			}
		}
		
		// Draw Data
		for (int i = 0; i < lineList.size(); i++) {
			drawLine(lineList.get(i));
		}
		
		// Draw Tool
		if (currentTool != null) {
			currentTool.draw();
		}
		
		gc.restore();
	}
	
	public void resize(double pWidth, double pHeight) {
		setWidth(pWidth);
		setHeight(pHeight);
		
		draw();
	}
	
	private void drawLine(Line line) {
		float pixelPerCm = getPixelPerCm();
		
		gc.setLineWidth(pixelPerCm * line.strength);
		gc.setStroke(line.color);
		
		if (line.points.length > 0) {
			float prevX = line.points[0];
			float prevY = line.points[1];
			
			for (int i = 0; i < line.points.length; i += 2) {
				gc.strokeLine(prevX * pixelPerCm, prevY * pixelPerCm, line.points[i] * pixelPerCm, line.points[i + 1] * pixelPerCm);
				
				prevX = line.points[i];
				prevY = line.points[i + 1];
			}
		}
	}
	
	// Events
	public void onMouseClicked(int button, float px, float py) {
		
	}
	public void onMousePressed(int button, float px, float py) {
		if (currentTool != null) {
			currentTool.pressMouse(button, getMouseViewX(), getMouseViewY());
		}
	}
	public void onMouseReleased(int button, float px, float py) {
		if (currentTool != null) {
			currentTool.releaseMouse(button, getMouseViewX(), getMouseViewY());
		}
	}
	public void onMouseDragged(int button, float px, float py) {
		if (button == 2) { // Middle
			float deltaX = px - mouseX;
			float deltaY = py - mouseY;
			
			positionX = CMath.clamp(positionX - (deltaX / getPixelPerCm()), -0.5f, rectW - 0.5f);
			positionY = CMath.clamp(positionY - (deltaY / getPixelPerCm()), -0.5f, rectH - 0.5f);
			
			draw();
		} else {
			// Call Tool Method
			if (currentTool != null) {
				currentTool.dragMouse(button, getMouseViewX(), getMouseViewY());
			}
		}
		
		mouseX = px;
		mouseY = py;
	}
	public void onMouseScrolled(int dir) {
		float phMouseX = getMouseViewX();
		float phMouseY = getMouseViewY();
		
		zoom = CMath.clamp(zoom - dir * 3, 3, 100);
		
		float deltaX = phMouseX - getMouseViewX();
		float deltaY = phMouseY - getMouseViewY();

		positionX = CMath.clamp(positionX + deltaX, -0.5f, rectW - 0.5f);
		positionY = CMath.clamp(positionY + deltaY, -0.5f, rectH - 0.5f);
		
		draw();
	}
	
	// Getters & Setters
	public float getMouseViewX() {
		return (mouseX / getPixelPerCm()) + positionX;
	}
	public float getMouseViewY() {
		return (mouseY / getPixelPerCm()) + positionY;
	}
	public float getPixelPerCm() {
		return (float)getWidth() / zoom;
	}
	public float getRectW() {
		return rectW;
	}
	public float getRectH() {
		return rectH;
	}
	
	public void setMouse(float px, float py) {
		mouseX = px;
		mouseY = py;
	}
	public void setRectW(float pw) {
		rectW = pw;
	}
	public void setRectH(float ph) {
		rectH = ph;
	}
	
	// Gui Events
	public void initGuiEvents() {
		gui.penStrength.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (currentTool != null && currentTool.getClass().isAssignableFrom(Tool_Pen.class)) {
					((Tool_Pen)currentTool).setStrength((float)gui.penStrength.getValue());
				}
			}
		});
		
		gui.penColor.valueProperty().addListener(new ChangeListener<Color>() {
			@Override
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
				if (currentTool != null && currentTool.getClass().isAssignableFrom(Tool_Pen.class)) {
					((Tool_Pen)currentTool).setColor(gui.penColor.getValue());
				}
			}
		});
	}
}






