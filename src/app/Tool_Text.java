package app;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Tool_Text extends Tool {
	// ATTRIBUTES //
	
	private TextBox hoveringTextBox;
	private TextBox selectedTextBox;
	private boolean isDragging;
	private float dragX, dragY;
	
	private Integer selectedPosition;
	/*
	 * Its the letter-index
	 * -> left side of the letter
	 * 
	 * You cannot select \n
	 * -> remove it by hitting backspace on the first letter in the following line
	 * -> (you actually can select its index, but you cannot go "outside" to edit it)
	 * 
	 * max. value = text.length()
	 * -> need to be able to remove last character as well
	 */
	
	
	// CONSTRUCTOR //
	
	public Tool_Text(Parent root) {
		isDragging = false;
		dragX = 0;
		dragY = 0;
		
		
		root.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				//typeKey(event);
			}
		});
	}
	
	
	// METHODS //
	
	// -> Abstract Methods
	
	@Override
	public void clickMouse(int button, float mouseX, float mouseY) {
		
	}
	
	@Override
	public void dragMouse(int button, float mouseX, float mouseY) {
		if (isDragging && selectedTextBox != null && (button == 0 || button == 1)) {
			selectedTextBox.x = CMath.clamp(mouseX - dragX, 0.25f, 999999999);
			selectedTextBox.y = CMath.clamp(mouseY - dragY, 0.75f, 999999999);
			
			if (button == 1) {
				selectedTextBox.x = Math.round(selectedTextBox.x);
				selectedTextBox.y = Math.round(selectedTextBox.y);
			}
			
			viewport.draw();
		}
		
		if (!isDragging && selectedTextBox != null && button == 0 && CMath.isPointInRect(mouseX, mouseY, selectedTextBox.x, selectedTextBox.y, selectedTextBox.getTextWidth() + 0.5f, selectedTextBox.getTextHeight() + 0.5f)) {
			//updateSelectedChar(1, mouseX, mouseY);
			
			viewport.draw();
		}
	}
	
	@Override
	public void moveMouse(float mouseX, float mouseY) {
		hoveringTextBox = null;
		for (int i = 0; i < viewport.textboxList.size(); i++) {
			TextBox tb = viewport.textboxList.get(i);
			
			if (CMath.isPointInRect(mouseX, mouseY, tb.x - 0.25f, tb.y - 0.75f, tb.getTextWidth() + 0.5f, tb.getTextHeight() + 1f)) {
				hoveringTextBox = tb;
				break;
			}
		}
		
		viewport.draw();
	}
	
	@Override
	public void pressMouse(int button, float mouseX, float mouseY) {
		if (button == 0 || button == 1) {
			if (button == 0 && hoveringTextBox == null) {
				hoveringTextBox = new TextBox("(...)", mouseX, mouseY, 7.5f, 0.5f);
				viewport.textboxList.add(hoveringTextBox);
				
				selectedTextBox = hoveringTextBox;
				
				viewport.updateRectSize();
				viewport.draw();
				return;
			}
			
			selectedTextBox = hoveringTextBox;
			
			
			if (selectedTextBox != null) {
				if (CMath.isPointInRect(mouseX, mouseY, selectedTextBox.x - 0.25f, selectedTextBox.y - 0.75f, selectedTextBox.getTextWidth() + 0.5f, 0.5f)) {
					// Cursor is on title-bar
					isDragging = true;
					
					dragX = mouseX - selectedTextBox.x;
					dragY = mouseY - selectedTextBox.y;
				} else {
					// Cursor is in content-box
					if (button == 0) {
						getSelectedPosition(0, mouseX, mouseY);
						//System.out.println(selectedPosition);
					}
				}
			}
		}
		
		viewport.draw();
	}
	
	@Override
	public void releaseMouse(int button, float mouseX, float mouseY) {
		if (isDragging) {
			isDragging = false;
			
			viewport.updateRectSize();
		} else {
			
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
		TextBox tb = selectedTextBox;
		for (int i = 0; i < 2; i++) {
			if (tb != null) {
				// Window
				gc.setLineWidth(1.5f);
				gc.setFill(Color.TRANSPARENT);
				gc.setStroke(Color.GRAY);
				drawRect(tb.x - 0.25f, tb.y - 0.25f, tb.getTextWidth() + 0.5f, tb.getTextHeight() + 0.5f);
				
				gc.setFill(new Color(0.5, 0.5, 0.5, 0.4));
				drawRect(tb.x - 0.25f, tb.y - 0.75f, tb.getTextWidth() + 0.5f, 0.5f);
			}
			
			tb = hoveringTextBox;
		}
		
		if (selectedTextBox != null && selectedPosition != null) {
			drawSelection();
		}
	}
	
	
	// -> Drawing
	
	private void drawSelection() {
		// Get row start index
		String str = selectedTextBox.getUnpatternedString(selectedTextBox.text);
		int rowStart = 0;
		int rowEnd = str.indexOf('\n', rowStart);
		
		int row = 1;
		
		while (selectedPosition > rowEnd && rowEnd != -1) {
			rowStart = rowEnd + 1;
			rowEnd = str.indexOf('\n', rowStart);
			
			row++;
		}
		
		
		// Get row width
		float rowWidth = selectedTextBox.getStringWidth(selectedTextBox.text, rowStart, selectedPosition);
		
		// Draw line
		gc.setStroke(Color.BLACK);
		float yPos = selectedTextBox.y + (row - 1) * selectedTextBox.fontHeight;
		drawLine(selectedTextBox.x + rowWidth, yPos, selectedTextBox.x + rowWidth, yPos + selectedTextBox.fontHeight);
	}
	
	
	// -> Useful Methods
	
	private Integer getSelectedPosition(int index, float mouseX, float mouseY) {
		if (selectedTextBox != null) {
			// Make mouse position local
			mouseX -= selectedTextBox.x;
			mouseY -= selectedTextBox.y;
			
			// Fix the values
			mouseY = CMath.clamp(mouseY, 0, 999999999);
			
			// Get selected row
			int selRow = (int)(Math.floor(mouseY / selectedTextBox.fontHeight) + 1);
			int maxRows = selectedTextBox.getTextRowCount();
			if (selRow > maxRows) {
				selRow = maxRows;
			}
			
			// Get selected row start
			String text = selectedTextBox.getUnpatternedString(selectedTextBox.text);
			int rowStart = CMath.getStringRowStart(text, selRow);
			
			// Get selected position
			String row = CMath.getStringRow(text, selRow);
			int rowLength = selectedTextBox.getStringLength(row);
			float lastWidth = selectedTextBox.getStringWidth(row);
			selectedPosition = 0;
			boolean outsideOfEnd = false;
			for (int i = rowLength; i >= 0; i--) {
				float newWidth = selectedTextBox.getStringWidth(row, 0, i);
				
				if (mouseX >= newWidth) {
					if (i == row.length()) {
						selectedPosition = i;
						
						outsideOfEnd = true;
						
						break;
					} else {
						float ph = ((lastWidth - newWidth) / 2) + newWidth;
						
						if (mouseX < ph) {
							selectedPosition = i;
						} else {
							selectedPosition = i + 1;
						}
						
						break;
					}
				}
				
				lastWidth = newWidth;
			}
			
			selectedPosition += rowStart;
			
			if (selRow == maxRows && outsideOfEnd) {
				selectedPosition++;
			}
		}
		
		return null;
	}
}
