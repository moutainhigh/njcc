$(function(){

    //状态标识：true-已存在生效的规则，false-不存在已生效的规则
    window.registerRuleStatusFlag = false;

    //列表分页显示数据
    var url = '/activity/actRegisterRuleController/list';
    CommonUtils.page(url,null,null,null,'RegisterRuleFn');

    //生效
    $('#validRegisterRule').unbind('click.validRegisterRule').bind('click.validRegisterRule',function(){
        var selectBox = $('#dataBody tr td :checkbox:checked');
        if(selectBox.length!=1){
            layer.alert('请选择您要编辑的数据！', {icon: 0});
            return;
        }
        var rpid = $(selectBox).attr('rpid');
        var tdRuleStatus = $('input:checkbox:checked').parent().parent().find("td[name='sdStatus']").html();
        if (tdRuleStatus=="有效"){
            layer.alert("此注册规则已生效，无需修改", {icon: 0});
            return;
        }
        if (window.registerRuleStatusFlag){
            layer.alert("已存在生效的规则，请先失效后操作", {icon: 0});
        }else{
            ajaxReq(fhpt_ctx+'/activity/actRegisterRuleController/editStatus',{'id':rpid,'status':'1'},'此注册规则已有效！');
        }
    });

    //失效
    $('#unvalidRegisterRule').unbind('click.unvalidRegisterRule').bind('click.unvalidRegisterRule',function(){
        var selectBox = $('#dataBody tr td :checkbox:checked');
        if(selectBox.length!=1){
            layer.alert('请选择您要编辑的数据！', {icon: 0});
            return;
        }
        var rpid = $(selectBox).attr('rpid');
        var tdRuleStatus = $('input:checkbox:checked').parent().parent().find("td[name='sdStatus']").html();
        if (tdRuleStatus=="无效"){
            layer.alert("此注册规则已无效，无需修改", {icon: 0});
        }else{
            ajaxReq(fhpt_ctx+'/activity/actRegisterRuleController/editStatus',{'id':rpid,'status':'0'},'此注册规则已无效！');
        }
    });

    //新建
    $("#createRegisterRule").on('click', function(){
        var url = fhpt_ctx+"/activity/actRegisterRuleController/goAddRule";
        window.location.href = url;
    });


});

function RegisterRuleFn(res){
    window.registerRuleStatusFlag = false;
    $("#dataBody").html("");
    var rows = res.rows;
    $.each(rows,function(i,n){
        var trObj=$("<tr></tr>");
        var tdCheck=$("<td></td>").append($('<input type="checkbox" name="check" onclick="selectcheck(this);"/>').attr({'rpid': n.id}));
        var tdNo=$("<td></td>").html(i+1);
        var tdStartTime = $("<td></td>").html(n.startTime);
        var tdEndTime = $("<td></td>").html(n.endTime);
        var tdDrawCount = $("<td></td>").html(n.drawCount);
        var tdStatus = $("<td name='sdStatus'></td>").html(formatValid(n.status));
        $("#dataBody").append(trObj.append(tdCheck).append(tdNo).append(tdStartTime).append(tdEndTime).append(tdDrawCount).append(tdStatus));
    });

};


function ajaxReq(ajaxurl,param,successMsg){
    $.ajax({
        type: "POST",
        url: ajaxurl,
        data: param,
        dataType:"json",
        async:false,
        success: function(res){ // 服务器返回的 json 结果
            if(res.count ==1){
                if(successMsg){
                    layer.alert(successMsg, {icon: 6});
                }
            }else{
                if(successMsg)layer.alert("状态修改异常", {icon: 5});
            }
            CommonUtils.page('/activity/actRegisterRuleController/list',null,null,null,'RegisterRuleFn');
        }
    });



};

function formatValid(value){
    if (value == null || value == '') {
        return '';
    }else {
        if(value=='0'){
            value='无效';
        }
        if(value=='1'){
            value='有效';
            window.registerRuleStatusFlag = true;
        }
    }
    return value;
}

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