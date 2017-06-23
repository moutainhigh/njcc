package com.yixuninfo.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yixuninfo.common.utils.StringEscapeEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping({"/baseController"})
public class BaseController
{
  @InitBinder
  public void initBinder(ServletRequestDataBinder binder)
  {
    binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));

    binder.registerCustomEditor(String.class, new StringEscapeEditor(true, false));
  }

  @RequestMapping({"/{folder}/{jspName}"})
  public String redirectJsp(@PathVariable String folder, @PathVariable String jspName)
  {
    return "/" + folder + "/" + jspName;
  }

  public void writeJson(Object object)
  {
    try
    {
      HttpServletResponse response = ((ServletWebRequest)RequestContextHolder.getRequestAttributes()).getResponse();

      String json = JSON.toJSONStringWithDateFormat(object, "yyyy-MM-dd HH:mm:ss", new SerializerFeature[0]);

      response.setContentType("text/html;charset=utf-8");
      response.getWriter().write(json);
      response.getWriter().flush();
      response.getWriter().close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}