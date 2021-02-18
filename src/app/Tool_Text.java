package app;

import javafx.scene.paint.Color;

public class Tool_Text extends Tool {
	private TextBox selectedTextBox;
	
	
	public Tool_Text() {
		
	}
	
	
	@Override
	public void clickMouse(int button, float mouseX, float mouseY) {
		
	}
	
	@Override
	public void dragMouse(int button, float mouseX, float mouseY) {
		
	}
	
	@Override
	public void moveMouse(float mouseX, float mouseY) {
		selectedTextBox = null;
		for (int i = 0; i < viewport.textboxList.size(); i++) {
			TextBox tb = viewport.textboxList.get(i);
			
			if (CMath.isPointInRect(mouseX, mouseY, tb.x, tb.y, tb.getTextWidth(), tb.getTextHeight())) {
				selectedTextBox = tb;
				break;
			}
		}
		
		viewport.draw();
	}
	
	@Override
	public void pressMouse(int button, float mouseX, float mouseY) {
		
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
		gc.setLineWidth(1.5f);
		gc.setFill(Color.TRANSPARENT);
		gc.setStroke(Color.GRAY);
		if (selectedTextBox != null) {
			drawRect(selectedTextBox.x - 0.25f, selectedTextBox.y - 0.25f, selectedTextBox.getTextWidth() + 0.5f, selectedTextBox.getTextHeight() + 0.5f);
		}
	}
}
