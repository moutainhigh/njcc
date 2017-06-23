package com.ncvas.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * AES 是一种可逆加密算法，对用户的敏感信息加密处理 对原始数据进行AES加密后，在进行Base64编码转化；
 */
public class AESOperator {

	/*
	 * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
	 */
	private String sKey = "haoduSmk20150906";
	private String ivParameter = "haoduSmkappkeyiv";
	private static AESOperator instance = null;

	private AESOperator() {

	}

	public static AESOperator getInstance() {
		if (instance == null)
			instance = new AESOperator();
		return instance;
	}
	
public static String Encrypt(String encData ,String secretKey,String vector) throws Exception {
		
		if(secretKey == null) {
			return null;
		}
		if(secretKey.length() != 16) {
			return null;
		}
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] raw = secretKey.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		IvParameterSpec iv = new IvParameterSpec(vector.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		byte[] encrypted = cipher.doFinal(encData.getBytes("utf-8"));
		return new BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码。
	}


	// 加密
	public String encrypt(String sSrc) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] raw = sKey.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
		return new BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码。
	}

	// 解密
	public String decrypt(String sSrc) throws Exception {
		try {
			byte[] raw = sKey.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);// 先用base64解密
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original, "utf-8");
			return originalString;
		} catch (Exception ex) {
			return null;
		}
	}
	
	public  static String decrypt(String sSrc,String key,String ivs) throws Exception {
		try {
			byte[] raw = key.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(ivs.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);// 先用base64解密
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original, "utf-8");
			return originalString;
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static String encodeBytes(byte[] bytes) {
		StringBuffer strBuf = new StringBuffer();

		for (int i = 0; i < bytes.length; i++) {
			strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
			strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
		}

		return strBuf.toString();
	}

	public static void main(String[] args) throws Exception {

//		// 需要加密的字串
//		String cSrc = "{\"MessageHeader\":{\"commandId\":\"medicalRechargeTransferForApp\",\"sourcecode\":\"smkApp\",\"timestamp\":\"2014-05-09 10:19:25\",\"messageLength\":\"20\",\"integrationId\":\"smkApp201508290438174210\"},\"MessageBody\":{\"reqInfo\":{\"license\":\"123123123123\",\"msgName\":\"medicalRechargeTransferForApp\",\"req_device\":\"2\",\"req_language\":\"1\",\"req_time\":\"2014-05-09 10:19:25\" },\"aliascode\":\"0000990164061404\",\"password\":\"111111\",\"psgncode\":\"0000190172665758\",\"orderAmt\":\"0.01\"}}";
//		long lStart = System.currentTimeMillis();
 // 	String enString = URLEncoder.encode(AESOperator.getInstance().encrypt("4265"));
//		System.out.println("加密后的字串是：" + enString);
//
//		long lUseTime = System.currentTimeMillis() - lStart;
//		System.out.println("加密耗时：" + lUseTime + "毫秒");
//		// 解密
//		String esrc = "uvxAv+YtRuIsUieTbwSVsBRUT8QUikBns9QL6DQtpLDDZW+2dhTZkOVKbny+UWXmfZK4h5aNfOJwy4ojDz9wL6bKoR2oBtWpSdBNJMRxMtQVjbdO7LLJG5B7BtKlIXM3NZ2trNbw8/JJV8O9Fbh4U+egGjt40Yh4TPJHI+SUGXvQH0BsqKWTuk1qquHs6jhxAp2Xt+O/QREo/XTYVD0eH9XImUn9d1de0jJD/9q0eJVGGjCcFI+MzGxfr+QnqxHVG+HwIvpQCNmkw747EExU2pAq01zDey/VYd8MLJKETMd9nTdnFZlYCFF6n9JDsOF4";
//		lStart = System.currentTimeMillis();
//		String DeString = AESOperator.getInstance().decrypt("IWxiF1uB82O8DrIRHJWMOA==");
//		System.out.println("解密后的字串是：" + DeString);
//		lUseTime = System.currentTimeMillis() - lStart;
//		System.out.println("解密耗时：" + lUseTime + "毫秒");
//		long nanoTime= System.currentTimeMillis();
//		String transseq = nanoTime+"";
//		System.out.println("111==="+transseq);
//		System.out.println("2221==="+System.nanoTime());
//		transseq = transseq.substring(0,8);
//		System.out.println(transseq);
//		System.out.println(new SimpleDateFormat("MMddmmss").format(new Date()));
	}

}
