<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--
<img src="${pageContext.request.contextPath}/style/images/blue_face/bluefaces_35.png" alt="资源未找到" />
<div>错误代码：404</div>
<div>错误描述：资源未找到</div><script type="text/javascript" charset="utf-8">try{parent.$.messager.progress('close');}catch(e){}</script>--%>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/taglibs.jsp"%>
    <meta charset="utf-8" />
    <title>页面不存在--智汇APP系统</title>
    <meta name="viewport" content="width=device-width,user-scalable=no">
    <script src="${res}/js/jquery-1.11.3.min.js"></script>
    <link rel="stylesheet" type="text/css" href="${res}/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="${res}/css/adminPage.css"/>
</head>
<body>
<div class="panel panel-default panelHeight main_bj">
    <div class="panel-heading"><h5>Error：404<small> > 页面不存在</small></h5></div>
    <div class="panel-body">
        <div class="sty404">
            <img src="${res}/img/404.png" />
            <h3>抱歉，您访问的页面不存在！</h3>
            <button class="btn btn-warning" onclick="goBack()">返回上一步</button>
        </div>
    </div>
</div>
</body>
<script>
    function goBack(){window.history.back()}
</script>
</html>
