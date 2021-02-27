package app;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Tool_Text extends Tool {
	private TextBox hoveringTextBox;
	private TextBox selectedTextBox;
	private boolean isDragging;
	private float dragX, dragY;
	
	private Integer[] selChar;
	private boolean[] selCharRight;
	
	
	public Tool_Text(Parent root) {
		isDragging = false;
		dragX = 0;
		dragY = 0;
		
		selChar = new Integer[2];
		selCharRight = new boolean[2];
		selCharRight[0] = false;
		selCharRight[1] = false;
		
		root.setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				typeKey(event);
			}
		});
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
			
			if (selectedTextBox != null && selectedTextBox != hoveringTextBox) {
				selChar[0] = null;
				selChar[1] = null;
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
						selCharRight[1] = selCharRight[0];
						
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
				
				if (selChar[0] != null && selChar[1] != null && selChar[1].equals(selChar[0])) {
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
			int rowCount = CMath.getStringRowCount(rawText);
			
			
			float[] xSelPos = new float[2];
			float[] ySelPos = new float[2];
			float[] selSize = new float[2];
			
			
			gc.setFill(new Color(1, 0.4, 0, 0.7));
			gc.setStroke(new Color(0, 0, 0, 0));
			
			
			for (int selIndex = 0; selIndex < 2; selIndex++) {
				if (selChar[selIndex] != null) {
					// find positions
					for (int i = 1; i <= rowCount; i++) {
						int rowStart = CMath.getStringRowStart(rawText, i);
						int rowEnd = CMath.getStringRowEnd(rawText, i);
						
						if (selChar[selIndex] >= rowStart && selChar[selIndex] <= rowEnd) {
							xSelPos[selIndex] = selectedTextBox.getStringWidth(rawText.substring(rowStart, selChar[selIndex] + 1));
							ySelPos[selIndex] = (i - 1) * selectedTextBox.fontHeight;
							selSize[selIndex] = selectedTextBox.getStringWidth(String.valueOf(rawText.charAt(selChar[selIndex])));
						}
					}
				}
			}
			
			// Draw Selection
			if (selChar[0] != null || selChar[1] != null) {
				if (selChar[0] == null) {
					selChar[0] = selChar[1];
					xSelPos[0] = xSelPos[1];
					ySelPos[0] = ySelPos[1];
					selSize[0] = selSize[1];
				} else if (selChar[1] == null) {
					selChar[1] = selChar[0];
					xSelPos[1] = xSelPos[0];
					ySelPos[1] = ySelPos[0];
					selSize[1] = selSize[0];
				}
				
				
				int selMode = getSelectionMode();
				
				if (selMode == 0) {
					// Normal Editing (single line)
					drawRect(selectedTextBox.x + xSelPos[0] - (!selCharRight[0] ? selSize[0] : 0), selectedTextBox.y + ySelPos[0], 0.05f, selectedTextBox.fontHeight);
				} else if (selMode == 1) {
					// Single Row Selection
					float phx1 = selectedTextBox.x + xSelPos[0] - (!selCharRight[0] ? selSize[0] : 0);
					float phx2 = selectedTextBox.x + xSelPos[1] - (!selCharRight[1] ? selSize[1] : 0);
					
					if (phx2 < phx1) {
						float phx3 = phx1;
						phx1 = phx2;
						phx2 = phx3;
					}
					
					drawRect(phx1, selectedTextBox.y + ySelPos[0], phx2 - phx1, selectedTextBox.fontHeight);
				} else {
					// Multiple Row Selection
					int lower = (selChar[0] < selChar[1] ? 0 : 1);
					int higher = 1 - lower;
					
					float phx1 = xSelPos[lower] - (!selCharRight[lower] ? selSize[lower] : 0);
					float phx2 = xSelPos[higher] - (!selCharRight[higher] ? selSize[higher] : 0);
					
					drawRect(selectedTextBox.x + phx1, selectedTextBox.y + ySelPos[lower], selectedTextBox.getTextWidth() - phx1, selectedTextBox.fontHeight);
					drawRect(selectedTextBox.x, selectedTextBox.y + ySelPos[higher], phx2, selectedTextBox.fontHeight);
					
					drawRect(selectedTextBox.x, selectedTextBox.y + ySelPos[lower] + selectedTextBox.fontHeight, selectedTextBox.getTextWidth(), ySelPos[higher] - ySelPos[lower] - selectedTextBox.fontHeight);
				}
			}
		}
	}
	
	private void typeKey(KeyEvent event) {
		if (selectedTextBox != null && (selChar[0] != null || selChar[1] != null)) {
			int index = selectedTextBox.getStringRealIndex(selChar[0]);
			
			String txt = selectedTextBox.text;
			
			if (selCharRight[0]) {
				txt = txt.substring(0, index + 1) + event.getCharacter() + txt.substring(index + 1);
			} else {
				txt = txt.substring(0, index) + event.getCharacter() + txt.substring(index);
			}
			
			selectedTextBox.text = txt;
			selChar[0]++;
			selChar[1] = null;
			
			viewport.draw();
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
					
					float mx = CMath.clamp(mouseX - selectedTextBox.x, 0, selectedTextBox.getStringWidth(row));
					for (int i = (row.length() - 1); i >= 0; i--) {
						float subWidth = selectedTextBox.getStringWidth(row.substring(0, i));
						if (mx >= subWidth) {
							mx -= subWidth;
							subWidth = selectedTextBox.getStringWidth(String.valueOf(row.charAt(i)));
							
							selCharRight[index] = (mx > (subWidth / 2) ? true : false);
							selChar[index] = rowStart + i;
							
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
	
	private int getSelectionMode() {
		int[] selRow = new int[2];
		
		String rawTxt = selectedTextBox.getUnpatternedString();
		int rows = CMath.getStringRowCount(rawTxt);
		
		for (int i = 0; i < 2; i++) {
			for (int r = 1; r <= rows; r++) {
				int start = CMath.getStringRowStart(rawTxt, r);
				int end = CMath.getStringRowEnd(rawTxt, r);
				
				if (selChar[i] >= start && selChar[i] <= end) {
					selRow[i] = r;
					break;
				}
			}
		}
		
		if (selRow[0] == selRow[1]) {
			if (selChar[0] == selChar[1] && selCharRight[0] == selCharRight[1]
					|| selChar[1] == (selChar[0] + 1) && selCharRight[0] && !selCharRight[1]
					|| selChar[0] == (selChar[1] + 1) && selCharRight[1] && !selCharRight[0]) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 2;
		}
	}
}
