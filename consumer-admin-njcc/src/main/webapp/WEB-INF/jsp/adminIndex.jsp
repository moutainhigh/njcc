<%@ page import="com.yixuninfo.common.utils.ConfigUtil" %>
<%@ page import="com.yixuninfo.system.dto.SessionInfoDto" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/taglibs.jsp"%>
	<jsp:include page="layout/header.jsp"/>
	<script src="${res}/js/menu.js${sourceVer}"></script>
</head>
<body>

<div class="container-fluid headerBg">
	<div class="col-sm-2 logo"><img src="${res}/img/login_logo.png"/></div>
	<div class="col-sm-6 hidePlane"><i class="glyphicon glyphicon-th-list hidePlaneLift"></i></div>
	<div class="col-sm-4 topRight">
		<ul class="list-inline outSys">
			<li>${sessionScope.sessionInfo.name}，欢迎你！</li>
<%--			<li id="selectTab">切换平台
				<div class="shopPanel">
					<ul class="list-inline">
						<li><a href="javascript:;"><img src="${res}/img/shoppingMall_01.png"/><span>智汇商城</span></a></li>
						<li><a href="javascript:;"><img src="${res}/img/shoppingMall_02.png"/><span>智汇金服</span></a></li>
						<li><a href="javascript:;"><img src="${res}/img/shoppingMall_03.png"/><span>售卡商城</span></a></li>
						<li><a href="javascript:;"><img src="${res}/img/shoppingMall_04.png"/><span>积分商城</span></a></li>
					</ul>
				</div>
			</li>--%>
			<li id="loginOut">退出登录</li>
		</ul>
	</div>
</div>

<div class="container-fluid article">
	<!--左边栏菜单部分-->
	<div class="leftSidebar">
		<dl class="leftSidebar_dl"></dl>
	</div>
	<!--右边栏内容部分-->
	<div class="rightSidebar">
		<!--内容栏-->
		<div class="containerPane" id="containerPane">
			<iframe id="iframe" name="frameUrl" src="" scrolling="yes" frameborder="0"></iframe>
		</div>
		<div class="footer">@Copyright 2016 . All Rights Reserved.</div>
	</div>
</div>

<!--超时弹出登录框-->
<script src="${res}/js/easydialog.js" type="text/javascript" charset="utf-8"></script>
<div class="loginDialog" id="testBox">
	<!--<div id="Dialogclose"></div>-->
	<div id="loginDia"></div>
</div>
</body>
</html>