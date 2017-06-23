$(function(){

    var listUrl = "/njcc/blackListController/list";
    CommonUtils.page(listUrl);

    $("#Search").click(function() {
        queryForm();
    });

    function queryForm(){
        var dataObj = {};
        $.each($('#blackListForm').serializeArray(), function() {
            dataObj[this.name] = this.value;
        });
        CommonUtils.page(listUrl, 1, dataObj);
    }

    $("#add").click(function(){
        $(".modal-title").html("新建用户");
        $("#mymodal").modal("toggle");
        $("#addFrom")[0].reset();
        $('#addFrom input').val('');
        $("#addFrom").attr("action",fhpt_ctx+"/njcc/blackListController/add");
    });

    //编辑
    $("#tbody").on('click','.margin_rg15',function(){
        var _this = $(this);
        _this = $(this).parents("tr");
        var id = _this.find("td").eq(6).text();
        var aliascode = _this.find("td").eq(1).text();
        var mobile = _this.find("td").eq(2).text();
        var loginid = _this.find("td").eq(3).text();
        $('#addFrom input[name="id"]').val(id);
        $('#addFrom input[name="aliascode"]').val(aliascode);
        $('#addFrom input[name="mobile"]').val(mobile);
        $('#addFrom input[name="loginid"]').val(loginid);

        $.ajax({
            type: "POST",
            url:fhpt_ctx+"/njcc/blackListController/deleteRedis",
            data: $('#addFrom').serialize(),
            dataType:"json",
            success: function(data){ // 服务器返回的 json 结果
                if (data && data.count==1) {
                    $(".modal-title").html("编辑用户");
                    $("#mymodal").modal("toggle");
                    $("#addFrom").attr("action",fhpt_ctx+"/njcc/blackListController/edit");
                }else{
                    layer.msg(data.msg, {time:2000});
                }
            }
        });
    });

    //添加、编辑按钮提交
    $("#submit").click(function(){
        if($('#addFrom input[name="aliascode"]').val()==''){
            return;
        }
        if($('#addFrom input[name="mobile"]').val()==''){
            return;
        }
        if($('#addFrom input[name="loginid"]').val()==''){
            return;
        }

        var url = $("#addFrom").attr("action");
        var dataJson =  $('#addFrom').serialize();

        if($('#addFrom input[name="id"]').val()!=''){
            save(url,dataJson);
        }else{
            var dataObj = {};
            $.each($('#addFrom').serializeArray(), function() {
                dataObj[this.name] = this.value;
            });
            dataObj['page']=1;
            dataObj['rows']=1;

            $.ajax({
                type: "POST",
                url: fhpt_ctx+"/njcc/blackListController/list",
                data: dataObj,
                dataType:"json",
                success: function(data){ // 服务器返回的 json 结果
                    if (data && data.rows.length>0) {
                        layer.msg("该黑名单信息已经存在！", {time:2000});
                    }else{
                        save(url,dataJson);
                    }
                }
            });
        }
    });

    //删除
    $("#tbody").on('click','.text-danger',function(){
        var _this = $(this);
        _this = $(this).parents("tr");
        var id = _this.find("td").eq(6).text();
        var aliascode = _this.find("td").eq(1).text();
        var mobile = _this.find("td").eq(2).text();
        var loginid = _this.find("td").eq(3).text();
        layer.confirm('确定删除数据吗？', {btn: ['确定','取消']}, //按钮
            function(){
                $.ajax({
                    url:fhpt_ctx+"/njcc/blackListController/del",
                    data:{id:id},
                    type:"POST",
                    dataType:"json",
                    success: function(res){
                        if(res&&res.errorMsg){
                            layer.msg(res.errorMsg,{time: 2000});
                        }else{
                            $.ajax({
                                type: "POST",
                                url:fhpt_ctx+"/njcc/blackListController/deleteRedis",
                                data: {aliascode:aliascode,mobile:mobile,loginid:loginid},
                                dataType:"json",
                                success: function(data){ // 服务器返回的 json 结果
                                    if (data && data.count==1) {
                                        CommonUtils.page(listUrl);
                                        layer.msg('已删除',{time: 2000});
                                    }else{
                                        layer.msg(data.msg, {time:2000});
                                    }
                                }
                            });
                        }
                    }
                });
            },
            function(){});
    });

    $("#clearbtn").click(function() {
        $("#aliascode").val('');
        $("#mobile").val('');
        $("#loginid").val('');
    });
});

function  save(url,data){
    $.ajax({
        type: "POST",
        url:url ,
        data:data,
        dataType:"json",
        success: function(data){ // 服务器返回的 json 结果
            if (data && data.count==1) {
                window.location.href=fhpt_ctx+'/njcc/blackListController/manage';
            }else{
                layer.msg(data.msg, {time:2000});
            }
        }
    });
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
    dataObj['page']=curr;
    dataObj['rows']=pageSize;
    return dataObj;
}

function callbackRes(res){
    $("#tbody").html("");
    var rows = res.rows;
    $.each(rows,function(i,n){
        var trObj=$("<tr></tr>");
        var tdNo=$("<td></td>")
            .html(i+1);
        var tdAliascode=$("<td></td>")
            .html(n.aliascode);
        var tdMobile=$("<td></td>")
            .html(n.mobile);
        var tdLoginid=$("<td></td>")
            .html(n.loginid);
        var tdCreated=$("<td></td>")
            .html(CommonUtils.formatDatebox(n.created));
        var tdId=$("<td></td>")
            .html(n.id).attr("style","display:none");
        var tdOperate=$("<td></td>").append('<span><a href="javascript:;" id="audit" class="text-primary margin_rg15">编辑</a></span> <span><a href="javascript:;" class="text-danger">删除</a></span>');
        $("#tbody").append(trObj.append(tdNo).append(tdAliascode).append(tdMobile).append(tdLoginid)
            .append(tdCreated).append(tdOperate).append(tdId)
        );
    })
};