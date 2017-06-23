<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>网银跳转</title>
  <meta content="text/html; charset=UTF-8" http-equiv="Content-Type" />
  <meta name="viewport" content="initial-scale=1, maximum-scale=1">
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="apple-mobile-web-app-status-bar-style" content="black">
  <link href="${pageContext.request.contextPath}/resources/webview/css/style1.css" rel="stylesheet" type="text/css">
</head>
<body>
<div class="wrap" >
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tbody>
    <tr>
      <th scope="col" class="pt60 pb40"><img src="${pageContext.request.contextPath}/resources/webview/images/pic_fail@2x.png"  /></th>
    </tr>
    <tr>
      <td class="c_111 fs32 tacenter pb40">跳转失败</td>
    </tr>
    <tr>
      <td class="pb40">
        <p>失败原因：</p>
        <p>
          ${errMsg}</p>
      </td>
    </tr>
<%--    <tr>
      <td><button type="button" class="button">确认</button></td>
    </tr>--%>
    </tbody>
  </table>
</div>
</body>
</html>