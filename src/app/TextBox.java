package app;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TextBox {
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
	
	
	public TextBox(String pText, float px, float py, float pWidth, float pFontHeight) {
		text = pText;
		x = px;
		y = py;
		width = pWidth;
		fontHeight = pFontHeight;
	}
	
	
	public void draw(GraphicsContext gc, float pixelPerCm) {
		gc.setStroke(Color.BLACK);
		gc.setFill(Color.BLACK);
		gc.setFont(Font.font(pixelPerCm * fontHeight));
		
		int row = 1;
		String rowStr = getTextRow(row);
		
		while (rowStr != null) {
			drawRow(gc, rowStr, (float)(x * pixelPerCm), (float)(y * pixelPerCm + gc.getFont().getSize() * row));
			
			row++;
			rowStr = getTextRow(row);
		}
		
		
		// Reset attributes
		fat = false;
	}
	
	
	private void drawRow(GraphicsContext gc, String str, float px, float py) {
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
				
				drawRow(gc, str.substring(patternEnd + 2), px + getStringWidth(ph, gc.getFont()), py);
			}
		}
	}
	
	
	
	private float getStringWidth(String str, Font font) {
		Text txt = new Text(str);
		txt.setFont(font);
		
		return (float)txt.getLayoutBounds().getWidth();
	}
	
	
	public float getTextWidth() {
		Text txt = new Text(getUnpatternedString(text));
		txt.setFont(Font.font(fontHeight));
		
		return (float)txt.getLayoutBounds().getWidth(); // width in cm!
	}
	public float getTextHeight() {
		int rows = 1;
		
		int ph = 0;
		while (text.indexOf("\n", ph + 1) != -1) {
			ph = text.indexOf("\n", ph + 1);
			rows++;
		}
		
		return rows * fontHeight;
	}
	public String getTextRow(int pRow) {
		return CMath.getStringRow(text, pRow);
	}
	public float getStringWidth(String str) {
		// ..in cm
		
		Text txt = new Text(getUnpatternedString(str));
		txt.setFont(Font.font(fontHeight));
		
		return (float)txt.getLayoutBounds().getWidth();
	}
	public int getStringRealIndex(String strWithPattern, int strIndex) {
		// strIndex is the index of getStringWithoutPattern(strWithPattern)
		
		String str = getUnpatternedString(strWithPattern);
		
		int offset = 0;
		
		for (int i = 0; i < strIndex; i++) {
			while (strWithPattern.charAt(i + offset) != str.charAt(i)) {
				offset++;
			}
		}
		
		return (strIndex + offset);
	}
	public int getStringRealIndex(int strIndex) {
		return getStringRealIndex(text, strIndex);
	}
	public String getUnpatternedString(String ph) {
		int phIndex = 0;
		while (phIndex != -1) {
			phIndex = ph.indexOf("[/");
			
			if (phIndex != -1 && ph.indexOf("\\]") != -1) {
				ph = ph.substring(0, phIndex) + ph.substring(ph.indexOf("\\]") + 2);
			}
		}
		
		return ph;
	}
	public String getUnpatternedString() {
		return getUnpatternedString(text);
	}
}
