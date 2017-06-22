$(function() {

    // 初始化
    if($.trim("${ruleResult.signInType}")=="2"){
        // 累计
        ljSet();
    }else{
        // 连续，其他情况默认连续
        lxSet();
    }

    $('#lxRadio').on('click', function(){
        lxSet();
    });

    $('#ljRadio').on('click', function(){
        ljSet();
    });


    // 失效
    $('#unvalidRule').on('click', function(){
        var status = "0"; // 失效状态码 0
        var url;
        if($("#ruleId").val()!=null && $("#ruleId").val()!=""){
            //修改
            url = fhpt_ctx+"/activity/actSignInRuleController/edit";
        }else{
            //新增
            url = fhpt_ctx+"/activity/actSignInRuleController/add";
        }
        updateOrAdd(url,status);
    });

    //生效
    $('#validRule').on('click', function(){
        var status = "1"; // 生效状态码 1
        var url;
        if($("#ruleId").val()!=null && $("#ruleId").val()!=""){
            //修改
            url = fhpt_ctx+"/activity/actSignInRuleController/edit";
        }else{
            //新增
            url = fhpt_ctx+"/activity/actSignInRuleController/add";
        }
        updateOrAdd(url,status);
    });


});

function updateOrAdd(url,status){
    //先进行校验
    if(checkFormat()){
        var data= $('#resultForm').serialize();
        var awardType = "1"; //因为奖励类型只有一个，暂时写静态数据
        data = data + "&status="+ status+"&awardType="+awardType;
        //alert(data);
        $.ajax({
            type: "POST",
            url: url,
            data: data,
            dataType:"json",
            asnyc:false,
            success: function(data){ // 服务器返回的 json 结果
                if (data && !data.errorMsg) {
                    if(status == "1"){
                        layer.msg("操作成功，并已经设置为生效", {time:2000});
                    }else{
                        layer.msg("操作成功，并已经设置为失效", {time:2000});
                    }
                }else{
                    layer.msg(data.errorMsg, {time:2000});
                }
            }
        });
        setTimeout('window.location.reload()',1500);//重新加载页面，主要是为了让id回显
    };

}

//校验格式
function checkFormat(){
    if($("input[name='signInName']").val()=='' || $("input[name='dayPreNeed']").val()=='' || $("input[name='drawCount']").val()=='' ){
        layer.alert("带星标号行不允许为空！", {time:2000});
        return false;
    }
    return true;
}

//连续签到规则
function lxSet(){
    $("#ljDiv").hide();
    $("#lxDiv").show();

    $("#lxDayPreNeed").attr("name","dayPreNeed");
    $("#lxDrawCount").attr("name","drawCount");
    $("#lxRadio").attr("name","signInType");
    $("#lxRadio").attr("checked","true");

    $("#ljDayPreNeed").removeAttr("name");
    $("#ljDrawCount").removeAttr("name");
    $("#ljRadio").removeAttr("name");
    $("#ljRadio").removeAttr("checked");
}

//累计签到规则
function  ljSet(){
    $("#lxDiv").hide();
    $("#ljDiv").show();

    $("#ljDayPreNeed").attr("name","dayPreNeed");
    $("#ljDrawCount").attr("name","drawCount");
    $("#ljRadio").attr("name","signInType");
    $("#ljRadio").attr("checked","true");

    $("#lxDayPreNeed").removeAttr("name");
    $("#lxDrawCount").removeAttr("name");
    $("#lxRadio").removeAttr("name");
    $("#lxRadio").removeAttr("checked");
}