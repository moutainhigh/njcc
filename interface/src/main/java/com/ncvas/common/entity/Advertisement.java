package com.ncvas.common.entity;

import com.ncvas.base.entity.ValueObject;

/**
 * @author lc_xin.
 * @date 2016年6月16日
 * 广告管理
 */
public class Advertisement extends ValueObject {

	/**
	 * 
	 */ 
	private static final long serialVersionUID = -2683780422928511773L;
	
	private String title ; 
	private String type  ; 
	private String pic   ; 
	private String link  ; 
	private String remark;
	private String valid ; //是否有效 0为无效  1为有效
	private String starttime ; //开始时间
	private String endtime ; //结束时间
	private String ioslink ; //ios链接
	private String second; //秒数，用于APP欢迎图使用
	private String reqBizType;//请求来源1和null为智汇APP2为医疗APP

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getIoslink() {
		return ioslink;
	}

	public void setIoslink(String ioslink) {
		this.ioslink = ioslink;
	}

	public String getSecond() {
		return second;
	}

	public void setSecond(String second) {
		this.second = second;
	}

	public String getReqBizType() {
		return reqBizType;
	}

	public void setReqBizType(String reqBizType) {
		this.reqBizType = reqBizType;
	}
}
