package com.example.demo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;

/**
 * Base64编码服务类
 * 
 */
public final class Base64 {

	private static byte base64Alphabet[];
	private static byte lookUpBase64Alphabet[];

	public Base64() {
	}

	static {
		base64Alphabet = new byte[255];
		lookUpBase64Alphabet = new byte[64];
		int i;
		for (i = 0; i < 255; i++)
			base64Alphabet[i] = -1;

		for (i = 90; i >= 65; i--)
			base64Alphabet[i] = (byte) (i - 65);

		for (i = 122; i >= 97; i--)
			base64Alphabet[i] = (byte) ((i - 97) + 26);

		for (i = 57; i >= 48; i--)
			base64Alphabet[i] = (byte) ((i - 48) + 52);

		base64Alphabet[43] = 62;
		base64Alphabet[47] = 63;
		for (i = 0; i <= 25; i++)
			lookUpBase64Alphabet[i] = (byte) (65 + i);

		i = 26;
		for (int j = 0; i <= 51; j++) {
			lookUpBase64Alphabet[i] = (byte) (97 + j);
			i++;
		}

		i = 52;
		for (int j = 0; i <= 61; j++) {
			lookUpBase64Alphabet[i] = (byte) (48 + j);
			i++;
		}

		lookUpBase64Alphabet[62] = 43;
		lookUpBase64Alphabet[63] = 47;
	}

	public static boolean isBase64(String isValidString) {
		return isArrayByteBase64(isValidString.getBytes());
	}

	public static boolean isBase64(byte octect) {
		return octect == 61 || base64Alphabet[octect] != -1;
	}

	public static boolean isArrayByteBase64(byte arrayOctect[]) {
		int length = arrayOctect.length;
		if (length == 0)
			return true;
		for (int i = 0; i < length; i++)
			if (!isBase64(arrayOctect[i]))
				return false;

		return true;
	}
	
	/**
	 * encode byte to base65
	 * @param binaryData
	 * @return
	 */
	public static byte[] encode(byte binaryData[]) {
		int lengthDataBits = binaryData.length * 8;
		int fewerThan24bits = lengthDataBits % 24;
		int numberTriplets = lengthDataBits / 24;
		byte encodedData[] = null;
		if (fewerThan24bits != 0)
			encodedData = new byte[(numberTriplets + 1) * 4];
		else
			encodedData = new byte[numberTriplets * 4];
		byte k = 0;
		byte l = 0;
		byte b1 = 0;
		byte b2 = 0;
		byte b3 = 0;
		int encodedIndex = 0;
		int dataIndex = 0;
		int i = 0;
		for (i = 0; i < numberTriplets; i++) {
			dataIndex = i * 3;
			b1 = binaryData[dataIndex];
			b2 = binaryData[dataIndex + 1];
			b3 = binaryData[dataIndex + 2];
			l = (byte) (b2 & 15);
			k = (byte) (b1 & 3);
			encodedIndex = i * 4;
			byte val1 = (b1 & -128) != 0 ? (byte) (b1 >> 2 ^ 192)
					: (byte) (b1 >> 2);
			byte val2 = (b2 & -128) != 0 ? (byte) (b2 >> 4 ^ 240)
					: (byte) (b2 >> 4);
			byte val3 = (b3 & -128) != 0 ? (byte) (b3 >> 6 ^ 252)
					: (byte) (b3 >> 6);
			encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
			encodedData[encodedIndex + 1] = lookUpBase64Alphabet[val2 | k << 4];
			encodedData[encodedIndex + 2] = lookUpBase64Alphabet[l << 2 | val3];
			encodedData[encodedIndex + 3] = lookUpBase64Alphabet[b3 & 63];
		}

		dataIndex = i * 3;
		encodedIndex = i * 4;
		if (fewerThan24bits == 8) {
			b1 = binaryData[dataIndex];
			k = (byte) (b1 & 3);
			byte val1 = (b1 & -128) != 0 ? (byte) (b1 >> 2 ^ 192)
					: (byte) (b1 >> 2);
			encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
			encodedData[encodedIndex + 1] = lookUpBase64Alphabet[k << 4];
			encodedData[encodedIndex + 2] = 61;
			encodedData[encodedIndex + 3] = 61;
		} else if (fewerThan24bits == 16) {
			b1 = binaryData[dataIndex];
			b2 = binaryData[dataIndex + 1];
			l = (byte) (b2 & 15);
			k = (byte) (b1 & 3);
			byte val1 = (b1 & -128) != 0 ? (byte) (b1 >> 2 ^ 192)
					: (byte) (b1 >> 2);
			byte val2 = (b2 & -128) != 0 ? (byte) (b2 >> 4 ^ 240)
					: (byte) (b2 >> 4);
			encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
			encodedData[encodedIndex + 1] = lookUpBase64Alphabet[val2 | k << 4];
			encodedData[encodedIndex + 2] = lookUpBase64Alphabet[l << 2];
			encodedData[encodedIndex + 3] = 61;
		}
		return encodedData;
	}

	/**
	 * decode base64 to byte
	 * @param base64Data
	 * @return
	 */
	public static byte[] decode(byte base64Data[]) {
		if (base64Data.length == 0)
			return new byte[0];
		int numberQuadruple = base64Data.length / 4;
		byte decodedData[] = null;
		byte b1 = 0;
		byte b2 = 0;
		byte b3 = 0;
		byte b4 = 0;
		byte marker0 = 0;
		byte marker1 = 0;
		int encodedIndex = 0;
		int dataIndex = 0;
		int lastData;
		for (lastData = base64Data.length; base64Data[lastData - 1] == 61;)
			if (--lastData == 0)
				return new byte[0];

		decodedData = new byte[lastData - numberQuadruple];
		for (int i = 0; i < numberQuadruple; i++) {
			dataIndex = i * 4;
			marker0 = base64Data[dataIndex + 2];
			marker1 = base64Data[dataIndex + 3];
			b1 = base64Alphabet[base64Data[dataIndex]];
			b2 = base64Alphabet[base64Data[dataIndex + 1]];
			if (marker0 != 61 && marker1 != 61) {
				b3 = base64Alphabet[marker0];
				b4 = base64Alphabet[marker1];
				decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
				decodedData[encodedIndex + 1] = (byte) ((b2 & 15) << 4 | b3 >> 2 & 15);
				decodedData[encodedIndex + 2] = (byte) (b3 << 6 | b4);
			} else if (marker0 == 61)
				decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
			else if (marker1 == 61) {
				b3 = base64Alphabet[marker0];
				decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
				decodedData[encodedIndex + 1] = (byte) ((b2 & 15) << 4 | b3 >> 2 & 15);
			}
			encodedIndex += 3;
		}

		return decodedData;
	}

	/**
	 * encode input stream to base64 of output stream
	 * @param inputStream
	 * @param outputStream
	 * @return
	 * @throws IOException
	 */
	public static long encode(InputStream inputStream, OutputStream outputStream)
			throws IOException {
		long totalSize = 0L;
		byte binaryData[] = new byte[3072];
		int PUSHBACK_SIZE = 3;
		boolean last = false;
		PushbackInputStream pushbackInputStream = new PushbackInputStream(
				inputStream, PUSHBACK_SIZE);
		label0: do {
			int size;
			int encodeSize;
			do {
				if ((size = pushbackInputStream.read(binaryData)) <= -1)
					break label0;
				encodeSize = size;
			} while (size == 0);
			if (size < 3) {
				last = true;
			} else {
				int numRemainder = size % 3;
				if (numRemainder != 0) {
					encodeSize = (size / 3) * 3;
					pushbackInputStream.unread(binaryData, encodeSize,
							numRemainder);
				}
			}
			byte cloneData[] = new byte[encodeSize];
			System.arraycopy(binaryData, 0, cloneData, 0, encodeSize);
			byte encodeData[] = encode(cloneData);
			outputStream.write(encodeData);
			totalSize += encodeData.length;
		} while (!last);
		return totalSize;
	}

	/**
	 * decode input stream to output stream of base64
	 * @param inputStream
	 * @param outputStream
	 * @return
	 * @throws IOException
	 */
	public static long decode(InputStream inputStream, OutputStream outputStream)
			throws IOException {
		long totalSize = 0L;
		byte base64Data[] = new byte[3072];
		int PUSHBACK_SIZE = 4;
		boolean last = false;
		PushbackInputStream pushbackInputStream = new PushbackInputStream(
				inputStream, PUSHBACK_SIZE);
		label0: do {
			int size;
			int decodeSize;
			do {
				if ((size = inputStream.read(base64Data)) <= -1)
					break label0;
				decodeSize = size;
			} while (size == 0);
			if (size < 4) {
				last = true;
			} else {
				int numRemainder = size % 4;
				if (numRemainder != 0) {
					decodeSize = (size / 4) * 4;
					pushbackInputStream.unread(base64Data, decodeSize,
							numRemainder);
				}
			}
			byte cloneData[] = new byte[decodeSize];
			System.arraycopy(base64Data, 0, cloneData, 0, decodeSize);
			byte decodeData[] = decode(cloneData);
			outputStream.write(decodeData);
			totalSize += decodeData.length;
		} while (!last);
		return totalSize;
	}

}
