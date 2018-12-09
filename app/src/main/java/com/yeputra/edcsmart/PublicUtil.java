package com.yeputra.edcsmart;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;

/**
 * 一些常用的公用方法
 * 
 */
@SuppressLint("DefaultLocale")
public class PublicUtil {

	/**
	 * 进度条Dialog
	 * 
	 * @param context
	 * @param title
	 * @param text
	 * @return
	 */
	public static ProgressDialog getDialog(Context context, String title,
			String text) {
		ProgressDialog mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setTitle(title);
		mProgressDialog.setMessage(text);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		return mProgressDialog;
	}

	/**
	 * 密码输入
	 */
	public static void showPassMode(final EditText et) {
		et.setTransformationMethod(PasswordTransformationMethod.getInstance());
	}

	/**
	 * 发送handler消息
	 * 
	 * @param handler
	 * @param msg
	 */
	public static void sendHandler(Handler handler, int msg) {
		handler.sendMessage(handler.obtainMessage(msg));
	}

	// 数组转字符串、以空格间隔
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			if (i == src.length - 1) {
				stringBuilder.append(hv);
			} else {
				stringBuilder.append(hv + " ");
			}
		}
		return stringBuilder.toString();
	}

	// 十六进制字符串转byte数组
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 将单个byte字节转为String
	 * 
	 * @param b
	 * @return
	 */
	public static String byteToString(byte b) {
		byte high, low;
		byte maskHigh = (byte) 0xf0;
		byte maskLow = 0x0f;
		high = (byte) ((b & maskHigh) >> 4);
		low = (byte) (b & maskLow);
		StringBuffer buf = new StringBuffer();
		buf.append(findHex(high));
		buf.append(findHex(low));
		return buf.toString();
	}

	private static char findHex(byte b) {
		int t = new Byte(b).intValue();
		t = t < 0 ? t + 16 : t;
		if ((0 <= t) && (t <= 9)) {
			return (char) (t + '0');
		}
		return (char) (t - 10 + 'A');
	}
}
