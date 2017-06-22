<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

	<%@ include file="/taglibs.jsp"%>
	<script src="${res}/businessJS/activity/actGoodsTypeModel.js${sourceVer}" type="text/javascript"></script>
	<style type="text/css">
		.tabsty th{text-align: center;}
	</style>
<div class="modal-header">
	<button type="button" class="close goodsTypeModelClose" ><span aria-hidden="true"><img src="${res}/img/del_but.png"/></span><span class="sr-only">Close</span></button>
	<h4 class="modal-title" id="myModalLabel">物品类型管理</h4>
</div>
<div class="modal-body">
	<div class="panel panel-default">
		<%--<div class="panel-heading"><h5>内容管理<small> > 频道管理</small></h5></div>--%>
		<div class="panel-body">
			<form class="form-horizontal" role="form" id="queryGoodsTypeForm" onsubmit="return false;">
				<div class="form-group formStyle">
					<div class="col-md-12" style="margin-bottom: 10px;">
						<label class="col-md-2">物品类型编号:</label>
						<input class="col-md-2" id="goodsTypeCode" name="goodsTypeCode" type="text" value="" />
						<label class="col-md-2">物品类型名称:</label>
						<input class="col-md-2" id="goodsTypeName" name="goodsTypeName" type="text" value="" />
						<div class="col-md-4 text-right">
							<button id="queryGoodsTypeBtn" type="button" class="btn btn-success btn-sm" style="margin-right:15px;">查 询</button>
							<button id="clearBtn" type="button" class="btn btn-info btn-sm" style="margin-right:15px;">重 置</button>
						</div>
						<%--<label class="col-md-2">是否生效:</label>
						<div class="col-md-2">
							<select id="valid" name="valid" class="form-control" style="height: 35px;">
								<option value="">全部</option>
								<option value="0">无效</option>
								<option value="1">有效</option>
							</select>
						</div>--%>
					</div>
				</div>
			</form>
				<div class="form-group formStyle">
					<div class="col-md-12 button15" style="margin-top: 10px;">
						<button id="createGoodsType" class="btn btn-info btn-sm"><i class="glyphicon glyphicon-plus"></i> 新 建</button>
						<button id="editGoodsType" class="btn btn-warning btn-sm"><i class="glyphicon glyphicon-info-sign"></i> 修 改</button>
						<%--<button id="unvalidGoodsType" class="btn btn-danger btn-sm"><i class="glyphicon glyphicon-trash"></i> 失 效</button>
						<button id="validGoodsType" class="btn btn-success btn-sm"><i class="glyphicon glyphicon-trash"></i> 生 效</button>--%>
					</div>
				</div>
		</div>
	</div>

	<div class="tabsty">
		<table id="tableId" class="table table-striped table-bordered table-hover text-center">
			<thead>
			<tr style="background-color: #F6F5F4;">
				<th><input type="checkbox" name="checkAll" value="" disabled/></th>
				<th>物品类型编号</th>
				<th>物品类型名称</th>
				<%--<th>是否生效</th>--%>
			</tr>
			</thead>
			<tbody id="goodsTypeBody"></tbody>
		</table>
		<div class="pageList">
			<div class="col-sm-2 text-left"></div>
			<div id="gsType" class="col-sm-10"></div>
		</div>
		<%--<div id="a" class="pageList"></div>--%>
	</div>
</div>
<div class="modal-footer">
	<button type="button" class="btn btn-primary goodsTypeModelClose">关闭</button>
</div>
<!--编辑弹出层-->
<div class="modal" id="goodsTypeModify" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close goodsTypeCloseX" ><span aria-hidden="true"><img src="${res}/img/del_but.png"/></span><span class="sr-only">Close</span></button>
				<h4 class="modal-title" id="storeHouseTypeModifyTitle">物品类型</h4>
			</div>
				<div class="modal-body">
					<form id="goodsTypeModifyForm" class="form-horizontal" role="form" onsubmit="return false;">
						<input name="id" type="hidden" />
						<div class="form-group">
							<label class="col-md-3  control-label"><font color="red">*</font> 物品类型编码：</label>
							<div class="col-md-5"><input type="text" name="goodsTypeCode" class="form-control" required/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-3  control-label"><font color="red">*</font> 物品类型名称：</label>
							<div class="col-md-5"><input type="text" name="goodsTypeName" class="form-control" required/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-3 control-label"></label>
							<div class="col-md-9">
								<button id="goodsTypeModifyBtn"  class="btn btn-success" style="margin-right: 15px;">确 认</button>
								<button type="button" name="" class="btn btn-danger goodsTypeCloseX">关 闭</button>
							</div>
						</div>
					</form>
				</div>
		</div>
	</div>
</div>