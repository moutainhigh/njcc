/**   
* @Title: AvroHttpServer.java 
* @Package com.cyber.rpc 
* @Description: TODO(用一句话描述该文件做什么) 
* @author cssuger@163.com   
* @date 2016年6月5日 下午5:45:32 
* @version V1.0   
*/
package com.ncvas.net.rpc;

import java.util.Map;

import com.ncvas.net.Server;
import com.ncvas.net.protocol.ProtocolUtil;
import com.ncvas.util.PropertiesUtil;
import org.apache.avro.Protocol;
import org.apache.avro.Protocol.Message;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.ipc.HttpServer;
import org.apache.avro.ipc.generic.GenericResponder;
import org.apache.avro.util.Utf8;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/** 
 * @ClassName: AvroHttpServer 
 * @Description: avro http server服务端 
 * @author cssuger@163.com 
 * @date 2016年6月5日 下午5:45:32 
 *  
 */
public class AvroHttpServer extends GenericResponder implements Server {

	private int port;
	
	private String host ;
	
  
	

	
	private Map<String,String> map = new PropertiesUtil("conf/resource.properties").getReadMap();
	
	public AvroHttpServer(Protocol local) {
		super(local);
		host = map.get("avro_host");
		port = Integer.parseInt(map.get("avro_port"));
	}

	@Override
	public Object respond(Message arg0, Object arg1) throws Exception {
		if(arg1 instanceof GenericRecord){
			GenericRecord record = (GenericRecord)arg1;
			GenericRecord resmg =  (GenericRecord)record.get("logentity");
			Object ob = resmg.get("body");
			Utf8 resMg = null;
			//业务处理
			if(null != ob){
				resMg = (Utf8)ob;
				//logger.info("获取数据:{}"+resMg.toString());
				System.out.println(resMg.toString());
				
			}
			resmg.put("body", "消息已经接受到");
			return resmg;
		}
		return null;
	}
	
	
	
	public void start(){
		try {
			HttpServer server = new HttpServer(this, port);
			new ServiceRegistry(map.get("zkconnection")).register(map.get("avro_host")+":"+Integer.parseInt(map.get("avro_port")));
			server.start();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        try {
            configurator.doConfigure(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/logback.xml"));
       } catch (JoranException e) {
            e.printStackTrace();
        }
		new AvroHttpServer(ProtocolUtil.parserSchema()).start();
	}

	


}
