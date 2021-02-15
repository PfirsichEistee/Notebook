package app;

import java.util.ArrayList;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Viewport extends Canvas {
	// ATTRIBUTES //
	
	// Editor Variables
	private GraphicsContext gc;
	
	private float mouseX, mouseY;
	
	private float zoom;
	private float positionX, positionY; // in cm; upper-left corner
	private float rectW, rectH; // in cm; bounding box of canvas
	
	private Tool currentTool;
	
	
	// Current Data (Data is public, so Tools can modify it)
	public ArrayList<Line> lineList;
	public ArrayList<String> textboxList;
	
	
	// CONSTRUCTOR //
	
	public Viewport(double pWidth, double pHeight) {
		super(pWidth, pHeight);
		
		gc = getGraphicsContext2D();
		
		mouseX = -20;
		mouseY = -20;
		
		zoom = 35;
		positionX = -0.5f;
		positionY = -0.5f;
		rectW = 0;
		rectH = 0;
		
		
		
		// Prepare other classes
		Tool.viewport = this;
		
		
		// Prepare Data
		lineList = new ArrayList<Line>();
		
		
		// TESTING HERE
		currentTool = new Tool_Pen();
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
		
		if (line.points.length > 2) {
			float prevX = line.points[0];
			float prevY = line.points[1];
			
			for (int i = 0; i < line.points.length; i += 2) {
				drawLineRounded(prevX * pixelPerCm, prevY * pixelPerCm, line.points[i] * pixelPerCm, line.points[i + 1] * pixelPerCm);
				
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
	
	// Custom Drawing
	public void drawLineRounded(float x1, float y1, float x2, float y2) {
		gc.strokeLine(x1, y1, x2, y2);
	}
}






