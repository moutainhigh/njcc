package com.yixuninfo.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class BeanUtil
{
  public static String apply(Class<?> fBean, String fVarName, Class<?> tBean, String tVarName)
  {
    StringBuilder sb = new StringBuilder();
    StringBuilder sbless = new StringBuilder();
    Field[] tfields = tBean.getDeclaredFields();
    for (Field field : tfields) {
      String fieldName = field.getName();
      if ((!"serialVersionUID".equals(fieldName)) && (!field.isAccessible()))
      {
        String getMethodName = methodName(fieldName, "get");
        Method getMethod = null;
        try {
          getMethod = fBean.getMethod(getMethodName, new Class[0]);
        } catch (Exception e) {
          System.out.println(new StringBuilder().append("no method named ").append(getMethodName).toString());
        }
        if (getMethod != null) {
          String valueStr = "";
          valueStr = new StringBuilder().append(fVarName).append(".").append(getMethodName).append("()").toString();
          sb.append(new StringBuilder().append(tVarName).append(methodName(fieldName, ".set")).append("(").append(valueStr).append(");\n").toString());
        } else {
          sbless.append(new StringBuilder().append(tVarName).append(methodName(fieldName, ".set")).append("();\n").toString());
        }
      }
    }
    return new StringBuilder().append(sb.toString()).append(sbless.toString()).toString();
  }

  public static void printSetRandom(Class<?> tBean, String tVarName) {
    StringBuilder sb = new StringBuilder();
    Field[] tfields = tBean.getDeclaredFields();
    for (Field field : tfields) {
      String fieldName = field.getName();
      if ((!"serialVersionUID".equals(fieldName)) && (!field.isAccessible()))
      {
        if (("errorCode".equals(fieldName)) || (field.isAccessible())) {
          sb.append(new StringBuilder().append(tVarName).append(methodName(fieldName, ".set")).append("(\"\");\n").toString());
        }
        if (("errorMsg".equals(fieldName)) || (field.isAccessible())) {
          sb.append(new StringBuilder().append(tVarName).append(methodName(fieldName, ".set")).append("(\"\");\n").toString());
        }
        String valueStr = new StringBuilder().append("\"").append((int)(Math.random() * 100000.0D)).append("\"").toString();
        sb.append(new StringBuilder().append(tVarName).append(methodName(fieldName, ".set")).append("(").append(valueStr).append(");\n").toString());
      }
    }
    System.out.println(sb.toString());
  }

  public static String methodName(String fieldName, String pre)
  {
    return new StringBuilder().append(pre).append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1, fieldName.length())).toString();
  }

  public static void extendsStr(Class[] beans) {
    if (beans != null) {
      Map<String,Field> fields = new HashMap();
      for (int i = 0; i < beans.length; i++) {
        Field[] tfields = beans[i].getDeclaredFields();
        for (Field field : tfields) {
          String fieldName = field.getName();
          if ((!"serialVersionUID".equals(fieldName)) && (!field.isAccessible()))
          {
            if (!fields.containsKey(new StringBuilder().append(fieldName).append(field.getType()).toString()))
              fields.put(new StringBuilder().append(fieldName).append(field.getClass()).toString(), field);
          }
        }
      }
      for (Entry entry : fields.entrySet()) {
        Field field = (Field)entry.getValue();
        System.out.println(new StringBuilder().append("private ").append(field.getType().getSimpleName()).append(" ").append(field.getName()).append(";").toString());
      }
    }
  }

  public static <T> T convert(Object obj, Class<T> clazz)
  {
    Object result = null;
    try {
      result = Class.forName(clazz.getName()).newInstance();

      Class clazz1 = clazz;
      Class objClazz = obj.getClass();
      do {
        for (Field field : clazz1.getDeclaredFields())
          try {
            if ((Modifier.isFinal(field.getModifiers())) || (!Modifier.isStatic(field.getModifiers())))
            {
              Field f1 = objClazz.getDeclaredField(field.getName());
              if ((f1 != null) && (field.getType().equals(f1.getType()))) {
                String methodName = f1.getName();
                String first = methodName.substring(0, 1).toUpperCase();
                String rest = methodName.substring(1, methodName.length());
                methodName = new StringBuilder().append("set").append(first).append(rest).toString();
                if (Modifier.isPrivate(field.getModifiers())) {
                  f1.setAccessible(true);
                }
                Method method = clazz1.getMethod(methodName, new Class[] { field.getType() });
                if (method != null)
                  method.invoke(result, new Object[] { f1.get(obj) });
              }
            }
          }
          catch (SecurityException e) {
            e.printStackTrace();
          } catch (NoSuchFieldException e) {
            e.printStackTrace();
          } catch (IllegalArgumentException e) {
            e.printStackTrace();
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (NoSuchMethodException e) {
            e.printStackTrace();
          } catch (InvocationTargetException e) {
            e.printStackTrace();
          }
      }
      while ((clazz1 = clazz1.getSuperclass()) != null);
    }
    catch (InstantiationException e1) {
      e1.printStackTrace();
    } catch (IllegalAccessException e1) {
      e1.printStackTrace();
    } catch (ClassNotFoundException e1) {
      e1.printStackTrace();
    }
    return (T)result;
  }

  public static void main(String[] args)
  {
  }
}