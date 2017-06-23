package com.ncvas.common.entity;

import com.ncvas.base.entity.ValueObjectDTO;
import com.ncvas.common.utils.StringUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * @author lc_xin.
 * @date 2016年6月16日
 */
public class AdvertisementDTO extends Advertisement implements ValueObjectDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5437231855780427280L;
	
	private String creationtime; //页面传过来的创建时间
	private String creationendtime;//页面传过来的创建时间的范围值
	private String differType; //区分接口或者后台的sql语句
	private String isAdv;		//轮播图的
	private String reqBizTypeVo;//页面传过来的广告所属来源
	
	public String getCreationtime() {
		return creationtime;
	}

	public void setCreationtime(String creationtime) {
		this.creationtime = creationtime;
	}

	public String getCreationendtime() {
		if(!StringUtil.isEmpty(this.getCreationtime())) {
			return this.getCreationtime() + " 23:59:59";
		}
		return  creationendtime;
	}

	public void setCreationendtime(String creationendtime) {
		this.creationendtime = creationendtime;
	}

	public String getDifferType() {
		return differType;
	}

	public void setDifferType(String differType) {
		this.differType = differType;
	}

	public String getIsAdv() {
		return isAdv;
	}

	public void setIsAdv(String isAdv) {
		this.isAdv = isAdv;
	}

	public String getReqBizTypeVo() {
		return reqBizTypeVo;
	}

	public void setReqBizTypeVo(String reqBizTypeVo) {
		this.reqBizTypeVo = reqBizTypeVo;
	}

	/**
	 *需要正序字段名
	 */
	private List<String> asc;
	/**
	 *需要逆序字段名
	 */
	private List<String> desc;
	/**
	 *排序SQL
	 */
	private String orders;

	/**
	 *  添加需要正序字段名
	 */
	public void addAsc(String col){
		if(asc==null){
			asc = new LinkedList<String>();
		}
		asc.add(col);
	}

	/**
	 *  清空需要正序字段名
	 */
	public void cleanAsc(){
		asc = null;
	}

	/**
	 *  添加需要逆序字段名
	 */
	public void addDesc(String col){
		if(desc==null){
			desc = new LinkedList<String>();
		}
		desc.add(col);
	}

	/**
	 *  清空需要逆序字段名
	 */
	public void cleanDesc(){
		desc = null;
	}

	/**
	 *  如果排序SQL为空根据需要正逆序的字段名拼接排序SQL
	 */
	public String  getOrders() {
		StringBuilder orderStr = null;
		StringBuilder ascStr;
		StringBuilder descStr;
		if(orders==null){
			if(asc!=null){
				ascStr = new StringBuilder();
				orderStr = new StringBuilder();
				String pex = "";
				for(String a : asc){
					ascStr.append(pex+a);
					pex = ",";
				}
				orderStr.append(ascStr.toString()+" ASC");
			}
			if(desc!=null){
				descStr = new StringBuilder();
				String pex = "";
				for(String d : desc){
					descStr.append(pex+d);
					pex = ",";
				}
				if(orderStr==null){
					orderStr = new StringBuilder();
					orderStr.append(descStr.toString()+" DESC");
				}else{
					orderStr.append(","+descStr.toString()+" DESC");
				}
			}
			orders = orderStr!=null?orderStr.toString():null;
		}

		return orders;
	}

	/**
	 *  设置排序SQL
	 */
	public void setOrders(String orders) {
		this.orders = orders;
	}
}
