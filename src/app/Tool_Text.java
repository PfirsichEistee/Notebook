package app;

import javafx.scene.paint.Color;

public class Tool_Text extends Tool {
	private TextBox hoveringTextBox;
	private TextBox selectedTextBox;
	private boolean isDragging;
	private float dragX, dragY;
	
	private Integer[] selChar;
	private boolean[] selCharRight;
	
	
	public Tool_Text() {
		isDragging = false;
		dragX = 0;
		dragY = 0;
		
		selChar = new Integer[2];
		selCharRight = new boolean[2];
		selCharRight[0] = false;
		selCharRight[1] = false;
	}
	
	
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
			updateSelectedChar(1, mouseX, mouseY);
			
			viewport.draw();
		}
	}
	
	@Override
	public void moveMouse(float mouseX, float mouseY) {
		hoveringTextBox = null;
		for (int i = 0; i < viewport.textboxList.size(); i++) {
			TextBox tb = viewport.textboxList.get(i);
			
			if (CMath.isPointInRect(mouseX, mouseY, tb.x - 0.25f, tb.y - 0.75f, tb.getTextWidth() + 0.5f, tb.getTextHeight() + 1.25f)) {
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
						updateSelectedChar(0, mouseX, mouseY);
						selChar[1] = null;
						
						viewport.draw();						
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
			if (selectedTextBox != null && button == 0 && CMath.isPointInRect(mouseX, mouseY, selectedTextBox.x, selectedTextBox.y, selectedTextBox.getTextWidth() + 0.5f, selectedTextBox.getTextHeight() + 0.5f)) {
				updateSelectedChar(1, mouseX, mouseY);
				
				if (selChar[1].equals(selChar[0])) {
					selChar[1] = null;
				}
				
				viewport.draw();
			}
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
		
		// Selection
		if (selectedTextBox != null) {
			String rawText = selectedTextBox.getUnpatternedString();
			
			gc.setFill(new Color(1, 0.4, 0, 0.7));
			gc.setStroke(new Color(0, 0, 0, 0));
			
			
			Float[] selPosX = new Float[2];
			Float[] selPosY = new Float[2];
			Float[] selSize = new Float[2];
			
			for (int i = 0; i < 2; i++) {
				if (selChar[i] != null) {
					int cRow = 1;
					Integer startRow = null;
					do {
						startRow = CMath.getStringRowStart(rawText, cRow);
						Integer endRow = CMath.getStringRowEnd(rawText, cRow);
						
						if (startRow != null && startRow <= selChar[i] && endRow >= selChar[i]) {
							// Character is in row "cRow"
							String ph = rawText.substring(startRow, selChar[i]);
							
							selPosX[i] = selectedTextBox.getStringWidth(ph);
							selPosY[i] = (cRow - 1) * selectedTextBox.fontHeight;
							selSize[i] = selectedTextBox.getStringWidth(String.valueOf(rawText.charAt(selChar[i]))); // single char width
							
							//drawRect(selectedTextBox.x + phWidth, selectedTextBox.y + (cRow - 1) * selectedTextBox.fontHeight, charWidth, selectedTextBox.fontHeight);
							
							
							break;
						} else {
							// Not in this row
							cRow++;
						}
					} while (startRow != null);
				}
			}
			
			if (selChar[1] != null && selChar[0] == null) {
				selPosX[0] = selPosX[1];
				selPosY[0] = selPosY[1];
				selSize[0] = selSize[1];
				
				selPosX[1] = null;
				selPosY[1] = null;
				selSize[1] = null;
			}
			
			if (selChar[0] != null && selChar[1] != null && selChar[1] < selChar[0]) {
				float ph1 = selPosX[0];
				float ph2 = selPosY[0];
				float ph3 = selSize[0];
				
				selPosX[0] = selPosX[1];
				selPosY[0] = selPosY[1];
				selSize[0] = selSize[1];
				
				selPosX[1] = ph1;
				selPosY[1] = ph2;
				selSize[1] = ph3;
			}
			
			if (selPosX[0] != null && selPosX[1] != null && selPosX[0].equals(selPosX[1]) && selPosY[0].equals(selPosY[1])) {
				selPosX[1] = null;
				selPosY[1] = null;
				selSize[1] = null;
			}
			
			
			if (selPosX[0] != null) {
				if (selPosX[1] != null) {
					// Multi-char-selection
					if (selPosY[0].equals(selPosY[1])) {
						// Same row
						drawRect(selPosX[0] + selectedTextBox.x, selPosY[0] + selectedTextBox.y, selPosX[1] - selPosX[0] + selSize[1], selectedTextBox.fontHeight);
					} else {
						drawRect(selPosX[0] + selectedTextBox.x, selPosY[0] + selectedTextBox.y, selectedTextBox.getTextWidth() - selPosX[0], selectedTextBox.fontHeight);
						
						drawRect(selectedTextBox.x, selPosY[1] + selectedTextBox.y, selPosX[1] + selSize[1], selectedTextBox.fontHeight);
						
						drawRect(selectedTextBox.x, selPosY[0] + selectedTextBox.y + selectedTextBox.fontHeight, selectedTextBox.getTextWidth(), selPosY[1] - selPosY[0] - selectedTextBox.fontHeight);
					}
				} else {
					// Single-char-selection
					if (selCharRight[0] && selChar[0] != null || selCharRight[1] && selChar[1] != null) {
						drawRect(selPosX[0] + selectedTextBox.x + selSize[0], selPosY[0] + selectedTextBox.y, 0.1f, selectedTextBox.fontHeight);
					} else {
						drawRect(selPosX[0] + selectedTextBox.x, selPosY[0] + selectedTextBox.y, selSize[0], selectedTextBox.fontHeight);
					}
				}
			}
		}
	}
	
	private void updateSelectedChar(int index, float mouseX, float mouseY) {
		selChar[index] = null;
		
		if (selectedTextBox != null) {
			mouseX = CMath.clamp(mouseX, selectedTextBox.x, 999999999);
			mouseY = CMath.clamp(mouseY, selectedTextBox.y, 999999999);
			
			String rawText = selectedTextBox.getUnpatternedString();
			
			int selRow = (int)Math.ceil((mouseY - selectedTextBox.y) / selectedTextBox.fontHeight);
			
			do {
				Integer rowStart = CMath.getStringRowStart(rawText, selRow);
				
				if (rowStart != null) {
					// Its this row
					String row = CMath.getStringRow(rawText, selRow);
					
					for (int i = (row.length() - 1); i >= 0; i--) {
						if ((mouseX - selectedTextBox.x) >= selectedTextBox.getStringWidth(row.substring(0, i))) {
							selChar[index] = rowStart + i;
							
							selCharRight[index] = false;
							if (i == (row.length() - 1) && (mouseX - selectedTextBox.x) >= selectedTextBox.getStringWidth(row.substring(0, i + 1))) {
								selCharRight[index] = true;
							}
							
							break;
						}
					}
					
					
					break;
				} else {
					selRow--;
				}
			} while (selRow > 1);
			
		}
	}
}
