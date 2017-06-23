package com.yixuninfo.system.model;

import com.yixuninfo.system.listener.EntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@EntityListeners({EntityListener.class})
@MappedSuperclass
public abstract class BaseEntity
  implements Serializable
{
  private static final long serialVersionUID = -67188388306700736L;
  public static final String ID_PROPERTY_NAME = "id";
  public static final String CREATE_DATE_PROPERTY_NAME = "createDate";
  public static final String MODIFY_DATE_PROPERTY_NAME = "modifyDate";
  private String sn;
  private Date createDate;
  private Date modifyDate;

  @Column(name="SN")
  @Id
  public String getSn()
  {
    return this.sn;
  }

  public void setSn(String sn) {
    this.sn = sn;
  }

  @Column(nullable=true, updatable=false)
  public Date getCreateDate()
  {
    return this.createDate;
  }

  public void setCreateDate(Date createDate)
  {
    this.createDate = createDate;
  }

  @Column(nullable=true)
  public Date getModifyDate()
  {
    return this.modifyDate;
  }

  public void setModifyDate(Date modifyDate)
  {
    this.modifyDate = modifyDate;
  }

  public boolean equals(Object obj)
  {
    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (!BaseEntity.class.isAssignableFrom(obj.getClass())) {
      return false;
    }
    BaseEntity other = (BaseEntity)obj;
    return getSn() != null ? getSn().equals(other.getSn()) : false;
  }

  public int hashCode()
  {
    int hashCode = 17;
    hashCode += (null == getSn() ? 0 : getSn().hashCode() * 31);
    return hashCode;
  }
}