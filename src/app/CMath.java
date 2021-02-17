package app;

public final class CMath {
	public static float clamp(float num, float mini, float maxi) {
		if (num < mini) {
			return mini;
		} else if (num > maxi) {
			return maxi;
		}
		return num;
	}
	
	public static float dist(float x1, float y1, float x2, float y2) {
		x1 = x2 - x1;
		y1 = y2 - y1;
		
		return (float)Math.sqrt(x1 * x1 + y1 * y1);
	}
}
