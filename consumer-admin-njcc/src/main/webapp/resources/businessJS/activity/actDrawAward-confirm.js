$(function(){

})
function doSave(){

    if ($("input[type='checkbox']").is(':checked')) {
        var datas = {'id':$('#id').val(),'actDrawAwardId':$('#actDrawAwardId').val(),'mailCompanyCode':$('#mailCompanyCode').val(),
            'receiveName':$('#receiveName').val(),'receiveMobile':$('#receiveMobile').val(),'mailTrackNum':$('#mailTrackNum').val()
            ,'receiveAddress':$('#receiveAddress').val()};
        var formUrl =fhpt_ctx+'/activity/actDrawAwardController/confirm';
        layer.confirm('一经点击确认发货按钮将不能更改兑奖状态！', {btn: ['确认发货','取消']},
            function() {
                ajaxReq(formUrl,datas,function (data) {
                    if(data&&data.count==1){
                        var url = fhpt_ctx + "/activity/actDrawAwardController/drawAwardList?wheelId=" + $('#wheelId').val();
                        window.location.href = url;
                    }else {
                        layer.msg(data.errorMsg, {time:2000});
                    }
                });
            },
            function(){});
    }
}

function ajaxReq(url,dataJson,callBack) {
    $.ajax({
        type: "POST",
        url:url,
        data:dataJson,
        dataType:"json",
        success: function(data){ // 服务器返回的 json 结果
            callBack(data);
        },
        error: function(data){ // 服务器返回的 json 结果
            layer.msg(data.errorMsg, {time:2000});
        }
    });
}
