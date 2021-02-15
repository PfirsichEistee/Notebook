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
}
