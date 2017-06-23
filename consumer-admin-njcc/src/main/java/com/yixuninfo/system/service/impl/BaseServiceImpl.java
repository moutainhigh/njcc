package com.yixuninfo.system.service.impl;

import com.yixuninfo.system.dto.PageHelperDto;

public class BaseServiceImpl
{
  public String orderHql(PageHelperDto ph)
  {
    String orderString = "";
    if ((ph.getSort() != null) && (ph.getOrder() != null)) {
      orderString = getOrderHql(ph.getSort(), ph.getOrder());
    }
    return orderString;
  }

  public String orderHql(String hql, String sort, String order) {
    if ((sort != null) && (order != null)) {
      hql = hql + getOrderHql(sort, order);
    }
    return hql;
  }

  public static String getOrderHql(String sort, String order) {
    String hql = "";
    if ((sort != null) && (order != null)) {
      if ((sort.contains(",")) && (order.contains(","))) {
        hql = hql + " order by ";
        String[] sorts = sort.split(",");
        String[] orders = order.split(",");
        for (int i = 0; i < sorts.length; i++) {
          hql = hql + sorts[i] + " " + orders[i] + ",";
        }
        hql = hql.substring(0, hql.length() - 1);
      } else {
        hql = hql + " order by " + sort + " " + order;
      }
    }
    return hql;
  }
}