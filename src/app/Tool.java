package app;


public abstract class Tool {
	public static Viewport viewport;
	
	
	// All coordinates in cm
	public abstract void clickMouse(int button, float mouseX, float mouseY);
	public abstract void dragMouse(int button, float mouseX, float mouseY);
	public abstract void pressMouse(int button, float mouseX, float mouseY);
	public abstract void releaseMouse(int button, float mouseX, float mouseY);
	public abstract void startTool(); // Start using this tool
	public abstract void quitTool(); // Quit using this tool
	
	public void draw() {
		// Override this if something needs to be drawn
		// Use methods below to draw something (coordinates in cm)
	}
	
	protected void drawRect() {
		
	}
	protected void drawCircle() {
		
	}
	protected void drawLine(float x1, float y1, float x2, float y2) {
		float ph = viewport.getPixelPerCm();
		viewport.getGraphicsContext2D().strokeLine(x1 * ph, y1 * ph, x2 * ph, y2 * ph);
	}
}
