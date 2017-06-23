$(function(){

    var listUrl = "/njcc/advertisementController/list";
    CommonUtils.page(listUrl);

    //编辑
    $("#tbody").on('click','.margin_rg15',function(){
        var _this = $(this);
        _this = $(this).parents("tr");
        var tdRow = _this.find("td").eq(8).text();
        var rowObj = eval('(' + tdRow + ')');
        window.location.href=fhpt_ctx+"/njcc/advertisementController/modify?id="+rowObj.id;
    });

    //删除
    $("#tbody").on('click','.text-danger',function(){
        var _this = $(this);
        _this = $(this).parents("tr");
        var tdRow = _this.find("td").eq(8).text();
        var rowObj = eval('(' + tdRow + ')');
        rowObj.valid=0;
        rowObj.created=CommonUtils.formatDatebox(rowObj.created);
        layer.confirm('确定删除数据吗？', {btn: ['确定','取消']}, //按钮
            function(){
                $.ajax({
                    url:fhpt_ctx+"/njcc/advertisementController/edit",
                    data:rowObj,
                    type:"POST",
                    dataType:"json",
                    success: function(res){
                        if(res&&res.errorMsg){
                            layer.msg(res.errorMsg,{time: 2000});
                        }else{
                            CommonUtils.page(listUrl);
                            layer.msg('已删除',{time: 2000});
                        }
                    }
                });
                /*_this.parents("tr").remove();
                layer.msg('已删除',{time: 1000});
                tabTrId();*/
            },
            function(){});
    });

    $("#Search").click(function() {
        var title=$("#title").val();
        var type=$("#type").val();
        var creationtime=$("#daySelect").val();
        var reqBizTypeVo=$("#reqBizTypeVo").val();
        //var valid=$("#valid").val();
        var dataObj = '{"title":"'+title+'","type":"'+type+'","creationtime":"'+creationtime+'","reqBizTypeVo":"'+reqBizTypeVo+'"}';
        CommonUtils.page(listUrl,1,dataObj);
    });

    $("#add").click(function(){
        window.location.href=fhpt_ctx+"/njcc/advertisementController/modify";
    });

    $("#clearbtn").click(function() {
        $("#title").val('');
        $("#type").val('');
        $("#daySelect").val('');
        $("#reqBizTypeVo").val('');
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
    var o = eval('(' + dataObj + ')');
    var data ={
        page: curr,
        rows :pageSize,
        title:o.title,
        type:o.type,
        creationtime:o.creationtime,
        reqBizTypeVo:o.reqBizTypeVo
    }
    return data;
}

function callbackRes(res){
    $("#tbody").html("");
    var rows = res.rows;
    $.each(rows,function(i,n){
        var trObj=$("<tr></tr>");
        var tdNo=$("<td></td>")
            .html(i+1);
        var tdTitle=$("<td></td>")
            .html(n.title);
        var tdType=$("<td></td>")
            .html(formatType(n.type));
        var tdCreated=$("<td></td>")
            .html(CommonUtils.formatDatebox(n.created));
        /*var tdValid=$("<td></td>")
            .html(formatValid(n.valid));*/
        var tdRow=$("<td></td>")
            .html(JSON.stringify(n)).attr("style","display:none");
        var tdStartTime=$("<td></td>")
            .html(CommonUtils.formatDatebox(n.starttime));
        var tdEndTime=$("<td></td>")
            .html(CommonUtils.formatDatebox(n.endtime));
        var tdReqBizType=$("<td></td>")
            .html(formatReqBizType(n.reqBizType));
        var tdOperate=$("<td></td>").append('<span><a href="javascript:;" id="audit" class="text-primary margin_rg15">编辑</a></span> <!--<span><a href="javascript:;" class="text-danger">删除</a></span>-->');
        $("#tbody").append(trObj.append(tdNo).append(tdTitle).append(tdType)
            .append(tdCreated).append(tdStartTime).append(tdEndTime).append(tdReqBizType)/*.append(tdValid)*/
        );
        if(n.valid == '1') {
            trObj.append(tdOperate).append(tdRow);
       }else {
            var td =$("<td></td>");
            trObj.append(td).append(tdRow);
        }
    })
};


/*//删除行序号自动修改
function tabTrId(){
    var firstTd = $("#tableId tbody tr");
    firstTd.each(function(i){
        $(this).children().first().html(i+1);
    })
};*/

function formatType(value){
    if (value == '0') {
        return '首页轮播图';
    } else if (value == '1') {
        return '卡片轮播图';
    } else if(value=='2'){
        return 'APP欢迎页';
    }
    return value;
}

function formatReqBizType(value){
    if (value == '1') {
        return '智汇APP';
    } else if(value=='2'){
        return '医疗APP';
    }
    return value;
}

function formatValid(value){
    if (value == '0') {
        return '失效';
    } else if (value == '1') {
        return '有效';
    }
    return value;
}
