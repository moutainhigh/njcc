package com.ncvas;

import com.ncvas.util.DESUtil;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ApiControllerTest {

//	private static final String url = "http://183.6.133.211:17007/hiss-openapi/api/ncvas.html";
	private static final String url = "http://localhost:9097/njcc-openapi/api/njcc.html";
	private static String requestKey = "aomenshoujiaxianshangduchangshangxiankaiyele";

	public static void main(String[] args) throws Exception {
		login();
	}
	
	public static void login() throws Exception{
		Map<String, Object> dataMsg = new HashMap<String, Object>();
		dataMsg.put("type", "2");
		dataMsg.put("pageNumber", "1");
		dataMsg.put("pageSize", "10");
		dataMsg.put("reqBizType", "1");
		long startTime = System.currentTimeMillis();
//		System.out.println(sendPost(url, "50028", null, dataMsg));
		System.out.println("明文=====>>" + DESUtil.decode(sendPostDes(url, "50028", null, dataMsg), requestKey));
		long endTime = System.currentTimeMillis();
		System.out.println(endTime-startTime);
	}

	private static String sendPostDes(String url, String serCode, String token, Map<String, Object> dataMsg) {
		InputStream inputStream = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("serCode=" + serCode);
			buffer.append("&dataMsg=" + JSONObject.fromObject(dataMsg).toString());
			buffer.append("&token=" + token);
			buffer.append("&ak=" + "38bd06870728ffdb3504546d3c4a1ea1");
			buffer.append("&erminalCode=" + "{\"os\":\"1\",\"devid\":\"359535052495068|1|19|20150702153923\",\"itime\":\"20150720142314\",\"version\":\"4\"}");

			StringBuffer sb = new StringBuffer();
			sb.append("serCode=" + DESUtil.encode(serCode,requestKey));
			sb.append("&dataMsg=" + DESUtil.encode(JSONObject.fromObject(dataMsg).toString(),requestKey));
			sb.append("&token=" + token);
			sb.append("&ak=" + "38bd06870728ffdb3504546d3c4a1ea1");
			sb.append("&erminalCode=" + DESUtil.encode("{\"os\":\"1\",\"devid\":\"359535052495068|1|19|20150702153923\",\"itime\":\"20150720142314\",\"version\":\"4\"}",requestKey));
			sb.append("&sign=" + DigestUtils.md5Hex(buffer.toString()));

			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setDefaultUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("appVersion", "2.0.0");
			//connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
			connection.getOutputStream().write(sb.toString().getBytes("utf-8"));

			String sign = connection.getHeaderField("sign");
			System.out.println("sign=====>>"+sign);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			inputStream = connection.getInputStream();
			byte[] byteBuffer = new byte[1024];
			int readLength = 0;
			while ((readLength = inputStream.read(byteBuffer)) != -1) {
				baos.write(byteBuffer, 0, readLength);
			}
			System.out.println("密文=====>>"+baos);

			return new String(baos.toByteArray(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private static String sendPost(String url, String serCode, String token, Map<String, Object> dataMsg) {
		InputStream inputStream = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("serCode=" + serCode);
			buffer.append("&dataMsg=" + JSONObject.fromObject(dataMsg).toString());
			buffer.append("&token=" + token);
			buffer.append("&ak=" + "38bd06870728ffdb3504546d3c4a1ea1");
			buffer.append("&erminalCode=" + "{\"os\":\"1\",\"devid\":\"359535052495068|1|19|20150702153923\",\"itime\":\"20150720142314\",\"version\":\"4\"}");
			buffer.append("&sign=" + DigestUtils.md5Hex(buffer.toString()));

			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setDefaultUseCaches(false);
			connection.setRequestMethod("POST");
			//connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
			connection.getOutputStream().write(buffer.toString().getBytes("utf-8"));

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			inputStream = connection.getInputStream();
			byte[] byteBuffer = new byte[1024];
			int readLength = 0;
			while ((readLength = inputStream.read(byteBuffer)) != -1) {
				baos.write(byteBuffer, 0, readLength);
			}

			return new String(baos.toByteArray(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public void testRedis(){
		Set sentinels = new HashSet();
		sentinels.add(new HostAndPort("172.16.0.89", 26379).toString());
		JedisSentinelPool sentinelPool = new JedisSentinelPool("mymaster", sentinels);
		System.out.println("Current master: " + sentinelPool.getCurrentHostMaster().toString());
		Jedis master = sentinelPool.getResource();
		master.set("a","testasdfasdfasdf");
		sentinelPool.returnResource(master);
		Jedis master2 = sentinelPool.getResource();
		String value = master2.get("a");
		System.out.println("username: " + value);
		master2.close();
		sentinelPool.destroy();
	}
}
