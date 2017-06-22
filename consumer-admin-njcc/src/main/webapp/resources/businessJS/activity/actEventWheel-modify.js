$(function(){

});

function doSave(){
    if($("#eventName").val()==''){
        layer.alert('活动名称不能为空', {icon: 0});
        $("#eventName").focus();
        return;
    }
    if($("#eventBegin").val()==''){
        layer.alert('大转盘的开始时间不能为空', {icon: 0});
        return;
    }
    if($("#eventEnd").val()==''){
        layer.alert('大转盘的结束时间不能为空', {icon: 0});
        return;
    }
    if($("#eventDesc").val()==''){
        layer.alert('活动规则不能为空', {icon: 0});
        $("#eventDesc").focus();
        return;
    }

    if(!CommonUtils.compareDate($("#eventBegin").val(),$("#eventEnd").val())){
        layer.alert('大转盘的开始时间必须小于结束时间！', {icon: 0});
        return;
    }

    var data = $('#resultForm').serialize();

    if($("#id").val()==""){
        var formUrl =fhpt_ctx+"/activity/actEventWheelController/create";
    }else{
        var formUrl =fhpt_ctx+"/activity/actEventWheelController/update";
    }

    $.ajax({
        type: "POST",
        url: formUrl,
        data: data,
        dataType:"json",
        asnyc:false,
        success: function(data){ // 服务器返回的 json 结果
            if (data && !data.errorMsg) {
                window.location.href=fhpt_ctx+'/activity/actEventWheelController/manage';
            }else{
                layer.msg(data.errorMsg, {time:2000});
            }
        }
    });
    return;
}
