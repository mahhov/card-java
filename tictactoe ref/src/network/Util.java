package network;

public class Util {

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static byte[] intToByte(int n) {
		return new byte[] { (byte) (n), (byte) (n >> 8), (byte) (n >> 16),
				(byte) (n >> 24) };
	}

	public static int byteToInt(byte[] b) {
		return unsignByte(b[0]) | (unsignByte(b[1]) << 8)
				| (unsignByte(b[2]) << 16) | (unsignByte(b[3]) << 24);
	}

	public static int unsignByte(byte b) {
		if (b >= 0)
			return b;
		return (int) 256 + b;
	}
}
