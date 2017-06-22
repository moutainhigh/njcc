package com.ncvas.controller.base;

import com.ncvas.util.HttpUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyEditorSupport;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 基础Controller 包含类型转换器
 * @author quyixia
 *
 */
public class AbstractController {
	
	protected transient final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected static <T> T readRequest2Object(HttpServletRequest request, Class<T> classOfT) throws Exception{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		InputStream inputStream = request.getInputStream();
		//创建一个Buffer字符串
		byte[] buffer = new byte[1024];
		//每次读取的字符串长度，如果为-1，代表全部读取完毕
		int len = 0;
		//使用一个输入流从buffer里把数据读取出来
		while( (len = inputStream.read(buffer)) != -1 ){
			//用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
			outStream.write(buffer, 0, len);
		}
		String queryString = new String(outStream.toByteArray(), "UTF-8");
		Map<String, String> objectParamMap = HttpUtils.getUrlParams(queryString);
		T instace = classOfT.newInstance();
		BeanUtils.populate(instace, objectParamMap);
		return instace;
	}
	
	/**
	 * 日期类型转换器
	 * @param dataBinder 数据Binder
	 */
	@InitBinder
	public void initDateBinder(WebDataBinder dataBinder) {
		//日期转换
		dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
			public void setAsText(String value) {
				try {
					if(value != null && !value.isEmpty()) {
						setValue(new SimpleDateFormat("yyyy-MM-dd").parse(value));
					} else {
						setValue(null);
					}
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			}

			public String getAsText() {
				if(getValue() != null) {
					return new SimpleDateFormat("yyyy-MM-dd").format((Date)getValue());
				} else {
					return null;
				}
			}
		});
		//字符串去空格
		dataBinder.registerCustomEditor(String.class, new PropertyEditorSupport() {
			public void setAsText(String value) {
				if(value != null) {
					setValue(value.trim());
				} else {
					setValue(null);
				}
			}
			public String getAsText() {
				return (String)getValue();
			}
		});
	}

}