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
	
	public static boolean isPointInRect(float px, float py, float rx, float ry, float rw, float rh) {
		if (px >= rx && px <= (rx + rw) && py >= ry && py <= (ry + rh)) {
			return true;
		}
		return false;
	}
	
	public static String getStringRow(String str, int row) {
		int startIndex = 0;
		for (int i = 1; i <= row; i++) {
			int endIndex = str.indexOf("\n", startIndex);
			
			if (i == row) {
				if (endIndex != -1) {
					return str.substring(startIndex, endIndex);
				} else {
					return str.substring(startIndex);
				}
			}
			
			if (endIndex == -1) {
				return null;
			}
			
			startIndex = endIndex + 1;
		}
		
		return null;
	}
	
	public static Integer getStringRowStart(String str, int row) {
		int startIndex = 0;
		for (int i = 1; i <= row; i++) {
			int endIndex = str.indexOf("\n", startIndex);
			
			if (i == row) {
				return startIndex;
			}
			
			if (endIndex == -1) {
				return null;
			}
			
			startIndex = endIndex + 1;
		}
		
		return null;
	}
	
	public static Integer getStringRowEnd(String str, int row) {
		int startIndex = 0;
		for (int i = 1; i <= row; i++) {
			int endIndex = str.indexOf("\n", startIndex);
			
			if (i == row) {
				if (endIndex != -1) {
					return endIndex;
				} else {
					return (str.length() - 1);
				}
			}
			
			if (endIndex == -1) {
				return null;
			}
			
			startIndex = endIndex + 1;
		}
		
		return null;
	}
	
	public static int getStringRowCount(String str) {
		int count = 1;
		Integer index = null;
		
		do {
			index = str.indexOf("\n", index);
			
			if (index != null) {
				index++;
				count++;
			}
		} while (index != null);
		
		return count;
	}
}
