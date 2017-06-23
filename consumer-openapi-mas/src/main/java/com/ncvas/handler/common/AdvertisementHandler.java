package com.ncvas.handler.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ncvas.base.entity.Page;
import com.ncvas.common.entity.*;
import com.ncvas.common.service.AdvertisementService;
import com.ncvas.common.service.ChannelService;
import com.ncvas.common.service.ChannelTypeService;
import com.ncvas.common.utils.StringUtil;
import com.ncvas.controller.base.ApiMessageException;
import com.ncvas.entity.HttpServletRequestVO;
import com.ncvas.entity.ResponeResultListVO;
import com.ncvas.entity.ResponeVO;
import com.ncvas.handler.AbstractApiHandler;
import com.ncvas.handler.ApiHandlerCallback;
import com.ncvas.payment.enums.ReqBizTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
/**
 * @author linlin.
 * 智汇App轮播图查询接口
 */
@Service
public class AdvertisementHandler extends AbstractApiHandler implements HttpServletRequestVO {
	@Resource(name = "advertisementService")
	private AdvertisementService advertisementService ;
	@Autowired
	private ChannelService channelService;

	@Autowired
	private ChannelTypeService channelTypeService;

	private HttpServletRequest request;
	@Value("#{configProperties['requestKey']}")
	private String requestKey;

	@Override
	public ResponeVO doHandler(final Object params) {
		final AdvertisementParams sparams = (AdvertisementParams) params;
		return super.buildCallback(new ApiHandlerCallback() {
			@Override
			public ResponeVO callback() throws Exception {
/*				int beginNum = PageUtil.begin(sparams.getPageNumber(), sparams.getPageSize());
				int endNum = PageUtil.end(sparams.getPageNumber(), sparams.getPageSize());*/
//				不用前台参数 直接全部查出来
				int beginNum = 0;
				int endNum = 1000;
				Map<String, Object> resultList = new LinkedHashMap<String, Object>();

				if(!StringUtil.isEmpty(sparams.getType())&&sparams.getType().equals("2")){
					AdvertisementDTO dto = new AdvertisementDTO();
					dto.setValid("1");
					dto.addDesc("CREATED");
					dto.setOrders(dto.getOrders());
					dto.setType("2");
					dto.setDifferType("1");
					dto.setReqBizType(StringUtil.isEmpty(sparams.getReqBizType())? ReqBizTypeEnum.REQ_BIZ_TYPE_MAS.getCode():sparams.getReqBizType());
					List<Map<String, String>> rows= getRow(dto,beginNum,endNum);
					if(rows!=null&&rows.size()>0)resultList.put("greetRows",rows);

					List<Map<String, Object>> channelRows = getChannelMap(sparams.getReqBizType());
					if(channelRows!=null&&channelRows.size()>0)resultList.put("channelRows",channelRows);

					return new ResponeResultListVO(resultList);
				}else {
					List<Map<String, String>> rows = new ArrayList<Map<String,String>>();
					AdvertisementDTO dto = new AdvertisementDTO();
					dto.setValid("1");
					dto.addAsc("CREATED");
					dto.setOrders("TYPE DESC,CREATED DESC");
					dto.setDifferType("1");
					dto.setIsAdv("2");
					dto.setReqBizType(StringUtil.isEmpty(sparams.getReqBizType())? ReqBizTypeEnum.REQ_BIZ_TYPE_MAS.getCode():sparams.getReqBizType());
					Page<Advertisement> datas = advertisementService.getPageModel(dto, beginNum, endNum);

					if(datas.getRows()!=null&&datas.getRows().size()>0) {
						for (Advertisement advertisement : datas.getRows()) {
							rows.add(getAdMap(advertisement));
						}
					}
					Map<String, Object> results = new LinkedHashMap<String, Object>();
					results.put("rows",rows);
					return new ResponeResultListVO(results);
				}
			}
		});
	}

	private Map<String,String> getAdMap(Advertisement advertisement){
		Map<String, String> row = new LinkedHashMap<String, String>();
		row.put("title", advertisement.getTitle());
		row.put("type", advertisement.getType());
		row.put("pic",resetImageReqestUrl(advertisement.getPic()));
		row.put("remark", advertisement.getRemark()==null?"":advertisement.getRemark());
		String erminalCode =(String) request.getParameter("erminalCode");
		if(!StringUtil.isEmpty(erminalCode)){
			JSONObject jsonObject = JSON.parseObject(erminalCode);
//			JSONObject jsonObject = JSON.parseObject(DESUtil.decode(erminalCode,requestKey));
			String os = String.valueOf(jsonObject.get("os"));
			if("1".equals(os)){
				row.put("link",advertisement.getLink()==null?"":advertisement.getLink());
			}else {
				row.put("link", advertisement.getIoslink()==null?"":advertisement.getIoslink());
			}
		}
		row.put("second", advertisement.getSecond()); //秒数，用于APP欢迎图使用
		row.put("reqBizType", advertisement.getReqBizType()); //来源
		return row;
	}

	private List<Map<String, String>> getRow(AdvertisementDTO dto,int beginNum,int endNum){
		Page<Advertisement> datas = advertisementService.getPageModel(dto, beginNum, endNum);
		List<Map<String, String>> rows = new ArrayList<Map<String,String>>();

		if(datas.getRows()!=null&&datas.getRows().size()>0){
			for (Advertisement advertisement : datas.getRows()) {
				rows.add(getAdMap(advertisement));
			}
		}
		return rows;
	}

	private Map<String,String> getClMap(Channel channel){
		Map<String, String> row = new LinkedHashMap<String, String>();
		row.put("channelName", channel.getChannelName());
		row.put("channelCode", channel.getChannelCode());
		row.put("channelImg", resetImageReqestUrl(channel.getChannelImg()));
		//row.put("channelOrder", String.valueOf(channel.getChannelOrder()));
		row.put("channelBelong", channel.getChannelBelong());
		row.put("channelBelongName",channel.getChannelBelongName());
		row.put("valid",channel.getValid());
		row.put("value",channel.getValue());//存放跳转webview地址，一般为空
		String reqSourceType = ReqBizTypeEnum.REQ_BIZ_TYPE_MAS.getCode();
		if (channel.getReqSourceType() != null){
			if (channel.getReqSourceType()=="1"){
				reqSourceType = ReqBizTypeEnum.REQ_BIZ_TYPE_NJCC.getCode();
			}else if(channel.getReqSourceType()=="2"){
				reqSourceType = ReqBizTypeEnum.REQ_BIZ_TYPE_HISS.getCode();
			}else if (channel.getReqSourceType()=="3"){
				reqSourceType = ReqBizTypeEnum.REQ_BIZ_TYPE_MAS.getCode();
			}
		}
		row.put("reqSourceType",reqSourceType);//请求来源
		return row;
	}

	private List<Map<String,Object>> getChannelMap(String reqBizType){
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		ChannelDTO cd = new ChannelDTO();
		cd.setValid("1");
		cd.setOrders("t.CHANNEL_BELONG ASC,t.CHANNEL_ORDER ASC");
		cd.setReqSourceType(StringUtil.isEmpty(reqBizType)? ReqBizTypeEnum.REQ_BIZ_TYPE_MAS.getCode():reqBizType);
		List<Channel> channels = channelService.doQuery(cd, 0, Integer.MAX_VALUE);
		List<Map<String, String>> rows = new LinkedList<Map<String, String>>();
		for (Channel channel:channels) {
			String cb = channel.getChannelBelong();
			Map<String, String> row = getClMap(channel);
			if(resultMap.get(cb)==null){
				rows = new LinkedList<Map<String, String>>();
				rows.add(row);
				resultMap.put(cb,rows);
			}else {
				rows.add(row);
				resultMap.put(cb,rows);
			}
		}
		List<Map<String, Object>> resultList = new LinkedList<Map<String, Object>>();
		resultList.add(resultMap);
		return resultList;
	}

	private List<Map<String,Object>> getChannelList(String reqBizType){
		//获取所属频道
		ChannelTypeDTO channelTypeDTO = new ChannelTypeDTO();
		channelTypeDTO.setValid("1");
		channelTypeDTO.addAsc("t.CHANNEL_TYPE_CODE");
		channelTypeDTO.setOrders(channelTypeDTO.getOrders());
		List<ChannelType> channelTypes = channelTypeService.doQuery(channelTypeDTO, 0, Integer.MAX_VALUE);
		//根据所属频道获取所属频道的频道信息
		ChannelDTO cd = new ChannelDTO();
		cd.setValid("1");
		cd.setOrders("t.CHANNEL_BELONG ASC,t.CHANNEL_ORDER ASC");
		cd.setReqSourceType(StringUtil.isEmpty(reqBizType)? ReqBizTypeEnum.REQ_BIZ_TYPE_MAS.getCode():reqBizType);

		List<Map<String, Object>> resultList = new LinkedList<Map<String, Object>>();
		for (ChannelType ct:channelTypes) {
			cd.setChannelBelong(ct.getChannelTypeCode());
			List<Channel> channels = channelService.doQuery(cd, 0, Integer.MAX_VALUE);
			List<Map<String, String>> rows = new LinkedList<Map<String, String>>();
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
			if(channels!=null&&channels.size()>0){
				for (Channel channel : channels) {
					Map<String, String> row = getClMap(channel);
					rows.add(row);
					resultMap.put(channel.getChannelBelong(),rows);
				}
				resultList.add(resultMap);
			}
		}
		return resultList;
	}

	@Override
	public void checkRequestParams(Object params) throws ApiMessageException {
		AdvertisementParams sparams = (AdvertisementParams) params;
	}

	@Override
	public Class<?> getRequestParams() {
		return AdvertisementParams.class;
	}

	@Override
	public void setHttpServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public static class AdvertisementParams {
		private int pageNumber;
		private int pageSize;
		private String type;
		private String reqBizType;//请求来源1为智汇APP2为医疗APP

		public int getPageNumber() {
			return pageNumber;
		}
		public void setPageNumber(int pageNumber) {
			this.pageNumber = pageNumber;
		}
		public int getPageSize() {
			return pageSize;
		}
		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}

		public String getReqBizType() {
			return reqBizType;
		}

		public void setReqBizType(String reqBizType) {
			this.reqBizType = reqBizType;
		}

		@Override
		public String toString() {
			return "AdvertisementParams{" +
					"pageNumber=" + pageNumber +
					", pageSize=" + pageSize +
					", type='" + type + '\'' +
					", reqBizType='" + reqBizType + '\'' +
					'}';
		}
	}
}
