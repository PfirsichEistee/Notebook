package app;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Tool_Eraser extends Tool {
	
	
	public Tool_Eraser() {
		
	}
	
	@Override
	public void clickMouse(int button, float mouseX, float mouseY) {
		
	}
	@Override
	public void dragMouse(int button, float mouseX, float mouseY) {
		if (button == 0) {
			ArrayList<Line> affectedLines = new ArrayList<Line>();
			float radius = viewport.getPenStrength() * 10;
			
			for (int i = (viewport.lineList.size() - 1); i >= 0; i--) {
				Line phLine = viewport.lineList.get(i);
				for (int k = 0; k < phLine.points.length; k += 2) {
					if (CMath.dist(phLine.points[k], phLine.points[k + 1], mouseX, mouseY) <= radius) {
						affectedLines.add(phLine);
						viewport.lineList.remove(i);
						break;
					}
				}
			}
			
			for (int i = 0; i < affectedLines.size(); i++) {
				// newly added lines will be inspected as well
				Line pLine = affectedLines.get(i);
				int lastLength = pLine.points.length;
				do {
					lastLength = pLine.points.length;
					
					Line newLine = removeNearestPointTo(pLine, radius, mouseX, mouseY);
					if (newLine != null) {
						affectedLines.add(newLine);
					}
				} while (pLine.points != null && lastLength != pLine.points.length);
			}
			
			
			// Finally, re-add all lines to data
			for (int i = 0; i < affectedLines.size(); i++) {
				if (affectedLines.get(i).points != null && affectedLines.get(i).points.length > 0) {
					viewport.lineList.add(affectedLines.get(i));
				}
			}
		}
		
		viewport.updateRectSize();
		//viewport.updatePosition();  -> Would cause accidental erasing because camera moves itself WHILE erasing
		
		viewport.draw();
	}
	@Override
	public void moveMouse(float mouseX, float mouseY) {
		viewport.draw();
	}
	@Override
	public void pressMouse(int button, float mouseX, float mouseY) {
		if (button == 0) {
			dragMouse(button, mouseX, mouseY);
		}
	}
	@Override
	public void releaseMouse(int button, float mouseX, float mouseY) {
		
	}
	@Override
	public void startTool() {
		
	}
	@Override
	public void quitTool() {
		
	}
	
	@Override
	public void draw() {
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);
		gc.setFill(new Color(1, 1, 1, 0.6));
		drawCircle(viewport.getMouseViewX(), viewport.getMouseViewY(), viewport.getPenStrength() * 10);
	}
	
	
	private Line removeNearestPointTo(Line pLine, float maxRadius, float px, float py) {
		// Returns a new line, if the passed one was "split" (or null if not)
		// Check if points-Array is empty afterwards!
		
		int nearestIndex = 0;
		for (int i = 2; i < pLine.points.length; i += 2) {
			if (CMath.dist(px, py, pLine.points[i], pLine.points[i + 1]) < CMath.dist(px, py, pLine.points[nearestIndex], pLine.points[nearestIndex + 1])) {
				nearestIndex = i;
			}
		}
		
		if (CMath.dist(px, py, pLine.points[nearestIndex], pLine.points[nearestIndex + 1]) <= maxRadius) {
			if (nearestIndex == 0 || nearestIndex == (pLine.points.length - 2)) { // (last index is a y-Coordinate, so "length - 2")
				// Either start or end will be removed
				if (pLine.points.length == 2) {
					pLine.points = null;
				} else {
					int k = 0;
					float[] newArray = new float[pLine.points.length - 2];
					for (int i = 0; i < pLine.points.length; i += 2) {
						if (i == nearestIndex) {
							continue;
						}
						
						newArray[k] = pLine.points[i];
						newArray[k + 1] = pLine.points[i + 1];
						
						k += 2;
					}
					
					pLine.points = newArray;
				}
				
				return null;
			} else {
				// Split Line
				float[] leftArray = new float[nearestIndex];
				float[] rightArray = new float[pLine.points.length - leftArray.length - 2];
				
				int k = 0;
				boolean left = true;
				for (int i = 0; i < pLine.points.length; i += 2) {
					if (i == nearestIndex) {
						k = 0;
						left = false;
						continue;
					}
					
					if (left) {
						leftArray[k] = pLine.points[i];
						leftArray[k + 1] = pLine.points[i + 1];
					} else {
						rightArray[k] = pLine.points[i];
						rightArray[k + 1] = pLine.points[i + 1];
					}
					
					k += 2;
				}
				
				pLine.points = rightArray;
				
				return new Line(pLine.strength, pLine.color, leftArray);
			}
		}
		
		return null;
	}
}





