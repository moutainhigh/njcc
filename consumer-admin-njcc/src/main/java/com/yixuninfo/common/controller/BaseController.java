package com.yixuninfo.common.controller;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseController
{
  @InitBinder
  public void initDateBinder(WebDataBinder dataBinder)
  {
    dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
      public void setAsText(String value) {
        try {
          if ((value != null) && (!value.isEmpty()))
            setValue(new SimpleDateFormat("yyyy-MM-dd").parse(value));
          else
            setValue(null);
        }
        catch (ParseException e) {
          e.printStackTrace();
        }
      }

      public String getAsText() {
        if (getValue() != null) {
          return new SimpleDateFormat("yyyy-MM-dd").format((Date)getValue());
        }
        return null;
      }
    });
    dataBinder.registerCustomEditor(String.class, new PropertyEditorSupport() {
      public void setAsText(String value) {
        if (value != null)
          setValue(value.trim());
        else
          setValue(null);
      }

      public String getAsText() {
        return (String)getValue();
      }
    });
  }
}