package com.ncvas.net.rpc;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import com.ncvas.net.Client;
import com.ncvas.net.protocol.ProtocolUtil;
import com.ncvas.exception.WSException;
import com.ncvas.util.Constants;
import com.ncvas.util.PropertiesUtil;
import org.apache.avro.Protocol;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.ipc.HttpTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.generic.GenericRequestor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * @ClassName: Client 
 * @Description: rpc http形式客户端
 * @author A18ccms a18ccms_gmail_com 
 * @date 2014年8月1日 下午1:31:48 
 *  
 */
public class AvroRpcHttpClient implements Client {
	
	private final Logger logger1 = LoggerFactory.getLogger(getClass());
	
	private Protocol protocol = null;
	private Map<String,String> map = new PropertiesUtil("conf/resource.properties").getReadMap();
	//private ServiceDiscovery servicediscovery = new ServiceDiscovery(map.get("zkconnection"));
	
    private String host = null;
    private int port = 0;
 
    public AvroRpcHttpClient(Protocol protocol) {
        this.protocol = protocol;
       // this.host = map.get("avro_host");
        //this.port = Integer.parseInt(map.get("avro_port"));
        String ipAndHost = discover();
		// this.host = map.get("avro_host");
		// this.port = Integer.parseInt(map.get("avro_port"));
		this.host = ipAndHost.split(":")[0];
		this.port = Integer.parseInt(ipAndHost.split(":")[1]);
    }
    /**
	 * 
	 * @Title: discover 
	 * @Description: 服务发现
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	private String discover() {
		ServiceDiscovery servicediscovery = new ServiceDiscovery(map.get("zkconnection"));
		String ipAndHosts = servicediscovery.discover();
		servicediscovery.stop();
		return ipAndHosts;
	}
	
 
    private void colse(Transceiver t){
		try {
			if (t.isConnected()) {
				t.close();
			}
		} catch (Exception ex) {
		}
    	
    }
 
    public static void main(String[] args) throws Exception {
    	String resmg = "根据新华社稿件，6月3日，习近平主席在人民大会堂同柬埔寨国王西哈莫尼举行会谈。双方一致同意，巩固睦邻友好，深化互利合作，推动中柬全面战略合作伙伴关系不断向前发展，给两国人民带来更多福祉。习近平指出：“中柬是情同手足的好邻居，也是肝胆相照的好朋友。”这么高的定位，是从哪里来的？不妨从习主席的话语中找找答案：西哈努克太皇同中国老一辈领导人共同缔造并精心培育的中柬友谊日益深入人心，成为两国人民的宝贵财富。国王陛下继承太皇陛下对华友好事业，长期致力于弘扬中柬传统友谊，我们对此予以高度评价。中方珍视同柬埔寨王室的特殊友谊，高度重视发展对柬埔寨关系，支持柬方走符合本国国情的发展道路。习主席提到的“特殊友谊”，也可以从历史中找到源头。1955年4月，周恩来总理与西哈努克在万隆会议上结识，从此掀开中柬友好关系的历史性篇章。";
    	long start = System.currentTimeMillis();
    	for(int i = 0;i<20000;i++){
    		System.out.println("-------"+i);
    		AvroRpcHttpClient	client = new AvroRpcHttpClient(ProtocolUtil.parserSchema());
    		
        	client.sendLogger(resmg);
    	}
    	long end = System.currentTimeMillis();
    	System.out.println(end -start);
    	
    	//new Client(Utils.getProtocol(), "127.0.0.1", 10056).run();
    }

	
	public void sendLogger(final String logger) {
		GenericRecord requestData = new GenericData.Record(protocol.getType("LogEntity"));
		requestData.put("body", logger);
		GenericRecord request = new GenericData.Record(protocol.getMessages().get("sendLogger").getRequest());
		request.put("logentity", requestData);
		// 客户端连接服务器
		Transceiver t = null;
		try {
		  t = this.connection();
		  GenericRequestor requestor = new GenericRequestor(protocol, t);
		  Object object = requestor.request("sendLogger", request);
		  if(object instanceof GenericData.Record){
			 Record response = (GenericData.Record)object;
			 logger1.info("接受服务器端消息为:{}",response.get("body"));
		  }
		} catch (Exception e) {
			e.printStackTrace();
			throw new WSException(Constants.ERROR_STATE,"服务器异常!");
		} finally {
			colse(t);
		}
		
	}





	



	public Transceiver connection() {
		Transceiver t = null;
		try {
			t = new HttpTransceiver(new URL("http://"+host+":"+port));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WSException(Constants.ERROR_STATE,"服务器异常!");
		}
		return t;
	}
}
