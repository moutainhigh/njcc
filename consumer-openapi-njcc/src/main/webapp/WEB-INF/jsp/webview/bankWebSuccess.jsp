<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>网银跳转</title>
  <meta content="text/html; charset=UTF-8" http-equiv="Content-Type" />
  <meta name="viewport" content="initial-scale=1, maximum-scale=1">
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="apple-mobile-web-app-status-bar-style" content="black">
</head>
<body>
<div class="wrap" style="height: 100%">
  <table width="100%" border="0" cellspacing="0" cellpadding="0" style="vertical-align: middle;height: 100%" >
    <tbody>
    <tr>
      <th scope="col" class="pt60 pb40">
        网银跳转中，请稍后！<br/>
        <img src="${pageContext.request.contextPath}/resources/webview/images/submitProcess.gif"  />
      </th>
    </tr>
    </tbody>
  </table>
</div>
  <div style="display: none">
    <form id="bankWebForm" action="${result.webViewRes['channelItemUrl']}" method="${result.webViewRes['submitMethod']}" >
      <c:forEach items="${result.webViewRes['parameters']}" var="v">
        <%--<c:if test="${v.value !=null && v.value != ''}">--%>
          <input id="${v.key}" name="${v.key}" value="${v.value}" />
        <%--</c:if>--%>
      </c:forEach>
    </form>
  </div>
</body>
<script type="text/javascript">
var form = document.getElementById('bankWebForm').submit();
</script>
</html>