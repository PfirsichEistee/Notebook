package app;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TextBox {
	// ATTRIBUTES //
	
	public String text;
	public float x, y;
	public float width;
	public float fontHeight;
	
	// "Work"-variables
	private boolean fat = false;
	private String color = "#ff0000";
	
	/* String info:
	Will NOT draw the following pattern: [/xxx\\]   (the 'xxx' can represent one or more letters OR a colorcode #RRGGBB)
	Whenever the user enters a character, make sure he cannot type "[/" or "\\]"
	[/f\\] = fat
	[/n\\] = normal
	[/#RRGGBB\\] = colorcode
	 */
	
	
	// CONSTRUCTOR //
	
	public TextBox(String pText, float px, float py, float pWidth, float pFontHeight) {
		text = pText;
		x = px;
		y = py;
		width = pWidth;
		fontHeight = pFontHeight;
	}
	
	
	// -> "Main"-Methods
	
	public void draw(GraphicsContext gc, float pixelPerCm) {
		gc.setStroke(Color.BLACK);
		gc.setFill(Color.BLACK);
		gc.setFont(Font.font(pixelPerCm * fontHeight));
		
		int row = 1;
		String rowStr = getTextRow(row);
		
		while (rowStr != null) {
			drawRow(gc, rowStr, (float)(x * pixelPerCm), (float)(y * pixelPerCm + gc.getFont().getSize() * row), pixelPerCm);
			
			row++;
			rowStr = getTextRow(row);
		}
		
		
		// Reset attributes
		fat = false;
	}
	
	
	private void drawRow(GraphicsContext gc, String str, float px, float py, float pixelPerCm) {
		int patternStart = str.indexOf("[/");
		String ph = str;
		if (patternStart != -1) {
			ph = str.substring(0, patternStart);
		}
		
		gc.fillText(ph, px, py);
		if (fat) {
			gc.strokeText(ph, px, py);
		}
		
		if (patternStart != -1) {
			int patternEnd = str.indexOf("\\]");
			
			if (patternEnd != -1) {
				String command = str.substring(patternStart + 2, patternEnd);
				
				if (command.equals("f")) {
					fat = true;
				} else if (command.equals("n")) {
					fat = false;
				}
				
				drawRow(gc, str.substring(patternEnd + 2), px + getStringWidth(ph) * pixelPerCm, py, pixelPerCm);
			}
		}
	}
	
	
	
	// -> Patterned Strings
	
	// -> -> public
	public String getCharAt(String str, int index) {
		int offset = 0;
		
		int start = -1;
		int end = -1;
		
		do {
			start = str.indexOf("[/", start + 1);
			end = str.indexOf("\\]", end + 1) + 1;
			
			if (start != -1 && (index + offset) > start) {
				offset += (end - start);
			} else if ((index + offset) == start) {
				return str.substring(start, end + 1);
			}
		} while (start != -1);
		
		return str.substring(index + offset, index + offset + 1);
	}
	public String getCharAt(int index) {
		return getCharAt(text, index);
	}
	
	
	public int getStringLength(String str) {
		int offset = 0;
		
		int start = -1;
		int end = -1;
		
		do {
			start = str.indexOf("[/", start + 1);
			end = str.indexOf("\\]", end + 1) + 1;
			
			if (start != -1) {
				offset += (end - start);
			}
		} while (start != -1);
		
		return (str.length() - offset);
	}
	
	
	public float getStringWidth(String str, int startIndex, int endIndex) { // endIndex excluded
		str = getUnpatternedString(str);
		if (endIndex < str.length()) {
			str = str.substring(startIndex, endIndex);
		} else {
			str = str.substring(startIndex);
		}
		
		
		Text txt = new Text(str);
		txt.setFont(Font.font(fontHeight));
		
		return (float)txt.getLayoutBounds().getWidth();
	}
	public float getStringWidth(String str) {
		return getStringWidth(str, 0, 999999999);
	}
	
	
	public float getTextWidth() {
		return getStringWidth(text, 0, 999999999);
	}
	public float getTextHeight() {
		return getTextRowCount() * fontHeight;
	}
	

	public String getTextRow(int pRow) {
		return CMath.getStringRow(text, pRow);
	}
	
	
	public int getTextRowCount() {
		int rows = 1;
		
		int ph = 0;
		while (text.indexOf("\n", ph + 1) != -1) {
			ph = text.indexOf("\n", ph + 1);
			rows++;
		}
		
		return rows;
	}
	
	
	public String getUnpatternedString(String ph) {
		int phIndex = 0;
		while (phIndex != -1) {
			phIndex = ph.indexOf("[/");
			int phIndexEnd = ph.indexOf("\\]");
			
			if (phIndex != -1 && phIndexEnd != -1) {
				ph = ph.substring(0, phIndex) + "\u0000" + ph.substring(phIndexEnd + 2); // \u0000 is an empty character
			}
		}
		
		return ph;
	}
	
	
	
	
	// TRASH
	
	/*public void removeCharAt(int index) {
		System.out.println("remove index " + index);
		if (index >= 2 && text.indexOf("\\]", index - 2) == (index - 2)) {
			System.out.println("pattern on left");
			
			int start = text.indexOf("[/");
			
			while (start != -1) {
				if (text.indexOf("\\]", start) == (index - 2)) {
					text = text.substring(0, start) + text.substring(index + 1);
					break;
				}
				
				start = text.indexOf("[/", start + 1);
			}
			
			return;
		}
		
		text = text.substring(0, index) + text.substring(index + 1);
	}*/
}
