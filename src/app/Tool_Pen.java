package app;

import java.util.ArrayList;

import javafx.scene.paint.Color;

public class Tool_Pen extends Tool {
	private ArrayList<Float> line;
	private float strength;
	private Color color;
	
	private float lastDragX, lastDragY;
	
	
	public Tool_Pen() {
		line = new ArrayList<Float>();
		strength = 0.05f;
		color = Color.BLACK;
		
		lastDragX = -999;
		lastDragY = -999;
	}
	
	
	@Override
	public void clickMouse(int button, float mouseX, float mouseY) {
		
	}
	@Override
	public void dragMouse(int button, float mouseX, float mouseY) {
		float deltaX = mouseX - lastDragX;
		float deltaY = mouseY - lastDragY;
		
		if (Math.sqrt(deltaX * deltaX + deltaY * deltaY) > 0.005) {
			lastDragX = mouseX;
			lastDragY = mouseY;
			
			mouseX = CMath.clamp(mouseX, 0, 999999999);
			mouseY = CMath.clamp(mouseY, 0, 999999999);
			
			line.add(mouseX);
			line.add(mouseY);
			
			viewport.draw();
		}
	}
	@Override
	public void pressMouse(int button, float mouseX, float mouseY) {
		if (button == 0) {
			dragMouse(button, mouseX, mouseY);
		}
	}
	@Override
	public void releaseMouse(int button, float mouseX, float mouseY) {
		if (line.size() > 0) {
			// Add New Line To Data
			float[] floatArray = new float[line.size()];
			
			for (int i = 0; i < line.size(); i++) {
				floatArray[i] = line.get(i);
			}
			
			Line newLine = new Line(strength, color, floatArray);
			
			viewport.lineList.add(newLine);
			
			line.clear();
			
			// Check If New Line Extended Rectangle
			float phX = floatArray[0];
			float phY = floatArray[1];
			
			for (int i = 0; i < floatArray.length; i += 2) {
				if (floatArray[i] > phX) {
					phX = floatArray[i];
				}
				if (floatArray[i + 1] > phY) {
					phY = floatArray[i + 1];
				}
			}
			
			if (viewport.getRectW() < phX) {
				viewport.setRectW(phX);
			}
			if (viewport.getRectH() < phY) {
				viewport.setRectH(phY);
			}
			
			// Redraw
			viewport.draw();
		}
	}
	@Override
	public void startTool() {
		
	}
	@Override
	public void quitTool() {
		
	}
	
	@Override
	public void draw() {
		if (line.size() > 0) {
			viewport.getGraphicsContext2D().setStroke(color);
			viewport.getGraphicsContext2D().setLineWidth(viewport.getPixelPerCm() * strength);
			
			float prevX = line.get(0);
			float prevY = line.get(1);
			
			for (int i = 0; i < line.size(); i += 2) {
				drawLine(prevX, prevY, line.get(i), line.get(i + 1));
				
				prevX = line.get(i);
				prevY = line.get(i + 1);
			}
		}
	}
	
	
	public void setStrength(float pStrength) {
		strength = pStrength;
	}
	public void setColor(Color pColor) {
		color = pColor;
	}
}
