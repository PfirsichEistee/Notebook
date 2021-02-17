package app;

import javafx.scene.paint.Color;

public class Line {
	public float strength;
	public Color color;
	public float[] points;
	
	
	public Line(float pStrength, Color pColor, float[] pPoints) {
		strength = pStrength;
		color = pColor;
		points = pPoints;
	}
}
