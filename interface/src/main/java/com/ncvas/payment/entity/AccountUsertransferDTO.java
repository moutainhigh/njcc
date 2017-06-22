package com.ncvas.payment.entity;

import com.ncvas.base.entity.ValueObjectDTO;
import com.ncvas.common.utils.StringUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * @author lc_xin.
 * @date 2016年12月19日
 */
public class AccountUsertransferDTO extends AccountUsertransfer implements ValueObjectDTO {

	private List<String> reqBizTypeList;
	private String creationtime; //页面传过来的创建时间
	private String creationendtime;//页面传过来的创建时间的范围值

	public List<String> getReqBizTypeList() {
		return reqBizTypeList;
	}

	public void setReqBizTypeList(List<String> reqBizTypeList) {
		this.reqBizTypeList = reqBizTypeList;
	}

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
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 4210716717356824646L;
	
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
