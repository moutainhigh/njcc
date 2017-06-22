$(function(){

    queryWheelConfig();
    function queryWheelConfig(){
        var cbSelect = $('.configCode');
        var editRptId = $('#eventWheelModifyForm select[name="configCode"]').val();
        cbSelect.empty();
        cbSelect.append($('<option></option>').attr({'value':''}).text('请选择'));
        $.ajax({
            type: "POST",
            url: fhpt_ctx+'/activity/actWheelConfigController/list',
            data: {'status':'1',
                'page': 1,
                'rows' :1000},
            dataType:"json",
            async:false,
            success: function(res){ // 服务器返回的 json 结果
                $.each(res.rows,function(){
                    var soption = $('<option></option>').attr({'value':this.configCode}).text(this.configName);
                    cbSelect.append(soption);
                });
            }
        });
    }
});

function doSave() {
    if($('input:radio[name="configCode"]:checked').val() == null){
        layer.msg("请选择选择大转盘！！！",{time:2000});
        return ;
    }

    var configCode = $('input:radio[name="configCode"]:checked').val();
    ajaxReq(fhpt_ctx+'/activity/actEventWheelController/configSave',{'id':$('#id').val(),'eventConfigCode':configCode},'基础设置完成！');

    function ajaxReq(ajaxurl,param,successMsg){
        $.ajax({
            type: "POST",
            url: ajaxurl,
            data: param,
            dataType:"json",
            async:false,
            success: function(res){ // 服务器返回的 json 结果
                if(res.count ==1){
                    if(successMsg) {
                        layer.confirm(successMsg, {btn: ['确定']},function () {
                            layer.closeAll('dialog');
                            window.location.href=fhpt_ctx+'/activity/actEventWheelController/config?id='+$('#id').val();
                        });
                    }
                }else{
                    layer.alert(res.errorMsg, {icon: 6});
                }
            }
        });
    };
}

