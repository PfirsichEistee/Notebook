package app;

import javafx.scene.canvas.GraphicsContext;

public abstract class Tool {
	protected static Viewport viewport;
	protected static GraphicsContext gc;
	
	
	// All coordinates in cm
	public abstract void clickMouse(int button, float mouseX, float mouseY);
	public abstract void dragMouse(int button, float mouseX, float mouseY);
	public abstract void moveMouse(float mouseX, float mouseY);
	public abstract void pressMouse(int button, float mouseX, float mouseY);
	public abstract void releaseMouse(int button, float mouseX, float mouseY);
	public abstract void startTool(); // Start using this tool
	public abstract void quitTool(); // Quit using this tool
	
	public void draw() {
		// Override this if something needs to be drawn
		// Use methods below to draw something (coordinates in cm)
	}
	
	protected void drawRect(float x, float y, float w, float h) {
		float ph = viewport.getPixelPerCm();
		
		gc.fillRect(x * ph, y * ph, w * ph, h * ph);
		gc.strokeRect(x * ph, y * ph, w * ph, h * ph);
	}
	protected void drawCircle(float x, float y, float r) {
		float ph = viewport.getPixelPerCm();
		gc.fillOval(x * ph - r * ph, y * ph - r * ph, r * ph * 2, r * ph * 2);
		gc.strokeOval(x * ph - r * ph, y * ph - r * ph, r * ph * 2, r * ph * 2);
	}
	protected void drawLine(float x1, float y1, float x2, float y2) {
		float ph = viewport.getPixelPerCm();
		gc.strokeLine(x1 * ph, y1 * ph, x2 * ph, y2 * ph);
	}
	
	
	public static void setViewport(Viewport pViewport) {
		viewport = pViewport;
		gc = viewport.getGraphicsContext2D();
	}
}
