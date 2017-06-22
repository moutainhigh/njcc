$(function(){
    var url = '/activity/actGoodsTypeController/list';
    $('.goodsTypeModelClose').unbind('click.goodsTypeModelClose').bind('click.goodsTypeModelClose',function(){
        $("#goodsTypeManageModel").modal('toggle');
    });
    $('#createGoodsType').unbind('click.createGoodsType').bind('click.createGoodsType',function(){
        $('#storeHouseTypeModifyTitle').text('新增物品类型');
        $('#goodsTypeModifyBtn').attr({'mtype':'add'});
        $('#goodsTypeModifyForm input').val('');
        $("#goodsTypeModify").modal('toggle');
    });
    $('#editGoodsType').unbind('click.editGoodsType').bind('click.editGoodsType',function(){
        $('#storeHouseTypeModifyTitle').text('修改物品类型');
        $('#goodsTypeModifyBtn').attr({'mtype':'edit'});
        var selectBox = $('#goodsTypeBody tr td :checkbox:checked');
        if(selectBox.length!=1){
            layer.alert('请选择您要编辑的数据！', {icon: 0});
            return;
        }
        var selectData = selectBox.parent().parent();
        var ctid = $(selectBox).attr('ctid');
        var goodsTypeCode = selectData.find('td:eq(1)').text();
        var goodsTypeName = selectData.find('td:eq(2)').text();
        $('#goodsTypeModifyForm input[name="id"]').val(ctid);
        $('#goodsTypeModifyForm input[name="goodsTypeCode"]').val(goodsTypeCode);
        $('#goodsTypeModifyForm input[name="goodsTypeName"]').val(goodsTypeName);
        $("#goodsTypeModify").modal('toggle');
    });
    $('.goodsTypeCloseX').unbind('click.goodsTypeCloseX').bind('click.goodsTypeCloseX',function(){
        $("#goodsTypeModify").modal('toggle');
    });

    /*$('#unvalidGoodsType').unbind('click.unvalidGoodsType').bind('click.unvalidGoodsType',function(){
        var selectBox = $('#goodsTypeBody tr td :checkbox:checked');
        if(selectBox.length!=1){
            layer.alert('请选择您要编辑的数据！', {icon: 0});
            return;
        }
        var ctid = $(selectBox).attr('ctid');
        ajaxReq(fhpt_ctx+'/activity/actGoodsTypeController/edit',{'id':ctid,'valid':'0'},'此物品类型已失效！');
    });

    $('#validGoodsType').unbind('click.validGoodsType').bind('click.validGoodsType',function(){
        var selectBox = $('#goodsTypeBody tr td :checkbox:checked');
        if(selectBox.length!=1){
            layer.alert('请选择您要编辑的数据！', {icon: 0});
            return;
        }
        var ctid = $(selectBox).attr('ctid');
        ajaxReq(fhpt_ctx+'/activity/actGoodsTypeController/edit',{'id':ctid,'valid':'1'},'此物品类型已生效！');
    });*/
    $('#goodsTypeModifyBtn').unbind('click.goodsTypeModifyBtn').bind('click.goodsTypeModifyBtn',function(){
        var mtype = $(this).attr('mtype');
        var data=$('#goodsTypeModifyForm').serialize();
        var rptid = $('#goodsTypeModifyForm input[name="id"]').val();
        //这里判断一下非空
        var submitcheck = true;
        $.each($('#goodsTypeModifyForm').serializeArray(), function() {
            if(this.name != 'id' && this.value==''){
                submitcheck=false;return false;
            }
        });
        if(!submitcheck)return;
        var ajaxUrl = '',msg='';
        if(mtype=='add'){
            ajaxUrl=fhpt_ctx+'/activity/actGoodsTypeController/add';
            msg='添加物品类型成功！';
        }
        if(mtype=='edit'){
            ajaxUrl=fhpt_ctx+'/activity/actGoodsTypeController/modify';
            msg='修改物品类型成功！';
        }
        //先检查频道code不能重复
        var goodsTypeCode={'page': 1,
            'rows' :1,
            'goodsTypeCode':$('#goodsTypeModifyForm input[name="goodsTypeCode"]').val()};
        $.ajax({
            type: "POST",
            url: fhpt_ctx+url,
            data: goodsTypeCode,
            dataType:"json",
            async:false,
            success: function(res){ // 服务器返回的 json 结果
                if(res.rows.length >0) {
                    var obj = res.rows[0];
                    if (obj.id != rptid) {
                        layer.alert('物品类型code已存在，请勿重复', {icon: 6});
                        return;
                    }
                }
                ajaxReq(ajaxUrl, data, msg);
            }
        });
        return;
    });
    CommonUtils.page(url,null,null,'gsType','pointGoodsTypeFn');
    $("#queryGoodsTypeBtn").click(function () {
        queryGoodsTypeForm();
    });
    function queryGoodsTypeForm(){
        var dataObj = {};
        $.each($('#queryGoodsTypeForm').serializeArray(), function() {
            dataObj[this.name] = this.value;
        });
        CommonUtils.page(url, 1, dataObj,'gsType','pointGoodsTypeFn');
    }
    function ajaxReq(ajaxurl,param,successMsg){
        $.ajax({
            type: "POST",
            url: ajaxurl,
            data: param,
            dataType:"json",
            async:false,
            success: function(res){ // 服务器返回的 json 结果
                if(res.count ==1){
                    //layer.alert(successMsg, {icon: 6});
                    $('#goodsTypeModify').modal('hide');
                    queryGoodsTypeForm();
                }else{
                    layer.alert(res.errorMsg, {icon: 5});
                }
            }
        });
    };
    $("#clearBtn").click(function(){
        $('#queryGoodsTypeForm input[name="goodsTypeCode"]').val('');
        $('#queryGoodsTypeForm input[name="goodsTypeName"]').val('');
    });
});

function returnDate(curr,dataObj){
    if(dataObj == null || dataObj == '')
    {
        var data ={
            page: curr,
            rows :pageSize
        }
        return data;
    };
    //var o = eval('(' + dataObj + ')');
    dataObj['page']=curr;
    dataObj['rows']=pageSize;
    return dataObj;
}

function pointGoodsTypeFn(res){
    $("#goodsTypeBody").html("");
    var rows = res.rows;
    $.each(rows,function(i,n){
        var trObj=$("<tr></tr>");
        var tdCheck=$("<td></td>").append($('<input type="checkbox" name="check" onclick="selectcheck(this);"/>').attr({'ctid': n.id}));
        var tdGoodsTypeCode = $("<td></td>").html(n.goodsTypeCode);
        var tdGoodsTypeName = $("<td></td>").html(n.goodsTypeName);
        //var tdValid = $("<td></td>").html(formatValid(n.valid));
        $("#goodsTypeBody").append(trObj.append(tdCheck).append(tdGoodsTypeCode).append(tdGoodsTypeName)/*.append(tdValid)*/);
    })
};

function selectcheck(obj){
    var checks=document.getElementsByName("check");
    if(obj.checked)
    {
        for(var i=0;i<checks.length;i++){
            checks[i].checked = false;
        }
        obj.checked = true;
    }else
    {
        for(var i=0;i<checks.length;i++){
            checks[i].checked = false;
        }
    }
}

function formatValid(value){
    if (value == null || value == '') {
        return '';
    }else {
        if(value=='0')value='无效';
        if(value=='1')value='有效';
    }
    return value;
}