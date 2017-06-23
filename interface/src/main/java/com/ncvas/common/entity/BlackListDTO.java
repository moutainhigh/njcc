package com.ncvas.common.entity;

import com.ncvas.base.entity.ValueObjectDTO;
import java.util.LinkedList;
import java.util.List;

/**
 * @author lc_xin.
 * @date 2016/9/28
 */
public class BlackListDTO extends BlackList implements ValueObjectDTO {



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
