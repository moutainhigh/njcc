$(function(){

    //列表分页显示数据
    CommonUtils.page(url, 1, originalData, null, 'callbackRes');

    $("#Search").click(function() {
        queryForm();
    });
    function queryForm(){
        var dataObj = {};
        $.each($('#queryDrawAwardForm').serializeArray(), function() {
            dataObj[this.name] = this.value;
            var drawBegin=$("#drawBegin").val();
            if(drawBegin)drawBegin += ' 00:00:00';
            var drawEnd=$("#drawEnd").val();
            if(drawEnd)drawEnd +=  ' 23:59:59';
            dataObj['drawBegin'] = drawBegin;
            dataObj['drawEnd'] = drawEnd;
        });
        CommonUtils.page(url, 1, dataObj,null,'callbackRes');
    };
    $("#clearbtn").click(function() {
        $("#queryDrawAwardForm :input").val('');
        $("#queryDrawAwardForm :selected").val('');
    });

    //释放库存
    $("#tbody").on('click','.release',function(){
        ajaxReq(fhpt_ctx+'/activity/actDrawAwardController/release',{'id':$(this).attr('drawId')},'此库存已释放！');
    });
    function ajaxReq(ajaxurl,param,successMsg){
        $.ajax({
            type: "POST",
            url: ajaxurl,
            data: param,
            dataType:"json",
            async:false,
            success: function(res){ // 服务器返回的 json 结果
                if(res.count ==1){
                    if(successMsg)layer.alert(successMsg, {icon: 6});
                    //$('#redPacketModal').modal('hide');
                    queryForm();
                }else{
                    if(successMsg)layer.alert(res.errorMsg, {icon: 5});
                }
            }
        });
    };

    // 查看订单
    $("#tbody").on('click','.view',function(){
        var url = fhpt_ctx+"/activity/actDrawAwardController/view?id="+$(this).attr('drawId');
        window.location.href = url;
    });

    queryAwardTypeSelect();
    //加载奖品类型下拉框
    function queryAwardTypeSelect(){
        var cbSelect = $('.goodsTypeCode');
        cbSelect.empty();
        cbSelect.append($('<option></option>').attr({'value':''}).text('请选择'));
        $.ajax({
            type: "POST",
            url: fhpt_ctx+'/activity/actGoodsTypeController/list',
            data: {'valid':'1',
                'page': 1,
                'rows' :1000},
            dataType:"json",
            async:false,
            success: function(res){ // 服务器返回的 json 结果
                $.each(res.rows,function(){
                    var soption = $('<option></option>').attr({'value':this.goodsTypeCode}).text(this.goodsTypeName);
                    cbSelect.append(soption);
                });
            }
        });
    }
})

var url = '/activity/actDrawAwardController/list';
var wheelId = getUrlParam("wheelId");
//var originalData = '{"wheelId":"' + wheelId + '"}';
var originalData = {};
originalData['wheelId'] = wheelId;

//获取Url参数
function getUrlParam(paramName) {
    var reParam = new RegExp('(?:[\?&]|&)' + paramName + '=([^&]+)', 'i');
    var match = window.location.search.match(reParam);
    return ( match && match.length > 1 ) ? match[1] : null;
}

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
    /*var data = {
        page: curr,
        rows: pageSize,
        wheelId: wheelId,  //wheelId是最原始带过来的参数
        drawBegin: o.drawBegin,
        drawEnd: o.drawEnd
    }*/
    dataObj['page']=curr;
    dataObj['rows']=pageSize;
    dataObj['wheelId']=wheelId;
    return dataObj;
}

function callbackRes(res){
    $("#tbody").html("");
    var rows = res.rows;
    $.each(rows,function(i,n){
        var trObj=$("<tr></tr>");
        var tdAge=$("<td></td>")
            .html(i+1);
        var tdAwardName = $("<td></td>")
            .html(n.awardName);
        var tdType = $("<td></td>")
            .html(n.goodsTypeName);
        var tdloginName = $("<td></td>")
            .html(n.loginName);
        var tdDrawDate = $("<td></td>")
            .html(formatTime(n.drawDate));
        var tdStatus = $("<td></td>")
            .html(formatStatus(n.status));
        var html = '';
        if (n.status == "2" || n.status == "4") {
            html += '<a href="javascript:;" class="text-primary view" drawId = '+n.id+'>查看订单</a>&nbsp;';
        }else if (n.status == "1") {
            /*html += '<a href="javascript:;" class="text-primary release" drawId = '+n.id+'>释放库存</a>&nbsp;';*/
        }

        var tdOperate=$("<td></td>")
            .html(html);
        $("#tbody").append(trObj.append(tdAge).append(tdAwardName).append(tdType).append(tdloginName).append(tdDrawDate)
            .append(tdStatus).append(tdOperate));


    })
};

function formatTime(value){
    if (value == null || value == '') {
        return '';
    }
    if(value.length==14){
        //20160618163611
        return value.substr(0,4)+'-'+value.substr(4,2)+'-'+value.substr(6,2)+' '+value.substr(8,2)+':'+value.substr(10,2)+':'+value.substr(12,2);
    }else if(value.length==8){
        return value.substr(0,4)+'-'+value.substr(4,2)+'-'+value.substr(4,2);
    }
    return value;
}

function formatStatus(value){
    if (value =='1'){
        return "未兑奖";
    }else if(value == '2'){
        return "未发货"
    }else if(value == '3'){
        return "已兑奖"
    }else if(value == '4'){
        return "已发货"
    }else if(value == '5'){
        return "已释放"
    }
    return value;
}

/*function doSave(){
    if($("#mailCompanyCode").val()==''||$("#mailTrackNum").val()==''||$("#confirm").val()==''){
        return;
    }

    var data = {'id':$('#mailInfoId').val(),'status':4,'mailCompanyCode':$('#mailCompanyCode').val(),'mailTrackNum':$('#mailTrackNum').val()};
    var formUrl =fhpt_ctx+'/activity/actDrawAwardController/confirm';

    $.ajax({
        type: "POST",
        url: formUrl,
        data: data,
        dataType:"json",
        asnyc:false,
        success: function(data){ // 服务器返回的 json 结果
            if (data && !data.errorMsg) {
                window.location.href=fhpt_ctx+'/activity/actDrawAwardController/manage';
            }else{
                layer.msg(data.errorMsg, {time:2000});
            }
        }
    });
    return ;
}*/
