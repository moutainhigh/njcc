$(function() {
    var url = '/activity/actEventWheelController/list';
    CommonUtils.page(url,null,null,null,'eventWheelFn');

    //转盘类型
    $('.wheelConfigManageBtn').unbind('click.wheelConfigManageBtn').bind('click.wheelConfigManageBtn',function(){
        $('#wheelConfigManageModel .modal-content').empty();
        $('#wheelConfigManageModel').removeData('bs.modal').modal({
            remote: fhpt_ctx+"/activity/actWheelConfigController/wheelConfigModel"
        });

    });

    /*$('#wheelConfigManageModel').on('hide.bs.modal', function () {
        queryeventWheelTypeSelect();
        setTimeout(function(){
            $('#eventWheelDocumentBody').attr({'class':'modal-open'});
        },500);
    });
    function queryeventWheelTypeSelect(){
        var cbSelect = $('.eventWheelType');
        var editRptId = $('#eventWheelModifyForm select[name="eventWheelType"]').val();
        cbSelect.empty();
        cbSelect.append($('<option></option>').attr({'value':''}).text('请选择'));
        $.ajax({
            type: "POST",
            url: fhpt_ctx+'/activity/actWheelConfigController/list',
            data: {'valid':'1',
                'page': 1,
                'rows' :1000},
            dataType:"json",
            async:false,
            success: function(res){ // 服务器返回的 json 结果
                $.each(res.rows,function(){
                    var soption = $('<option></option>').attr({'value':this.redPacketTypeCode}).text(this.redPacketTypeName);
                    cbSelect.append(soption);
                });
                $('#eventWheelModifyForm select[name="eventWheelType"]').val(editRptId)
            }
        });
    }
*/
    //编辑
    $("#wheelBody").on('click','.edit',function(){
        var url = fhpt_ctx+"/activity/actEventWheelController/modify?id="+$(this).attr('wheelId');
        window.location.href = url;
    });

    //创建
    $("#createWheel").click(function(){
        //加前置判断，已有有效活动则不让新增
        ajaxReq(fhpt_ctx+'/activity/actEventWheelController/addIndex',{},'此活动大转盘已无效！');
        function ajaxReq(ajaxurl,param,successMsg){
            $.ajax({
                type: "POST",
                url: ajaxurl,
                data: param,
                dataType:"json",
                async:false,
                success: function(res){ // 服务器返回的 json 结果
                    if(res.count ==1){
                        window.location.href=fhpt_ctx+"/activity/actEventWheelController/modify";
                    }else{
                        if(successMsg)layer.alert(res.errorMsg, {icon: 5});
                    }
                }
            });
        };
    });

    //失效
    $('#unvalidWheel').unbind('click.unvalidWheel').bind('click.unvalidWheel',function(){
        var selectBox = $('#wheelBody tr td :checkbox:checked');
        if(selectBox.length!=1){
            layer.alert('请选择您要编辑的数据！', {icon: 0});
            return;
        }
        var rpid = $(selectBox).attr('rpid');
        ajaxReq(fhpt_ctx+'/activity/actEventWheelController/checkStatus',{'id':rpid,'status':'0'},'此活动大转盘已无效！');
    });

    //生效
    $('#validWheel').unbind('click.validWheel').bind('click.validWheel',function(){
        var selectBox = $('#wheelBody tr td :checkbox:checked');
        if(selectBox.length!=1){
            layer.alert('请选择您要编辑的数据！', {icon: 0});
            return;
        }
        var rpid = $(selectBox).attr('rpid');
        ajaxReq(fhpt_ctx+'/activity/actEventWheelController/checkStatus',{'id':rpid,'status':'1'},'此活动大转盘已有效！');
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
                    queryWheelForm();
                }else{
                    if(successMsg)layer.alert(res.errorMsg, {icon: 5});
                }
            }
        });
    };

    //配置
    $("#wheelBody").on('click','.config',function(){
        var url = fhpt_ctx+"/activity/actEventWheelController/config?id="+$(this).attr('wheelId');
        window.location.href = url;
    });

    //详情
    $("#wheelBody").on('click','.view',function(){
        var url = fhpt_ctx+"/activity/actEventWheelController/view?id="+$(this).attr('wheelId');
        window.location.href = url;
    });

    //兑奖
    $("#wheelBody").on('click','.draw',function(){
        var url = fhpt_ctx+"/activity/actDrawAwardController/drawAwardList?wheelId="+$(this).attr('wheelId');
        window.location.href = url;
    });

    $("#queryWheelBtn").click(function () {
        queryWheelForm();
    });
    function queryWheelForm(){
        var dataObj = {};
        $.each($('#queryWheelForm').serializeArray(), function() {
            dataObj[this.name] = this.value;
        });
        console.log(dataObj);
        CommonUtils.page(url, 1, dataObj,null,'eventWheelFn');
    }

    $("#clearbtn").click(function(){
        $('#queryWheelForm input').val('');
        $('#queryWheelForm select').val('');
    });
});

function eventWheelFn(res){

    $("#wheelBody").html("");
    var rows = res.rows;
    $.each(rows,function(i,n){
        var trObj=$("<tr></tr>");
        var tdCheck=$("<td></td>").append($('<input type="checkbox" name="check" onclick="selectcheck(this);"/>').attr({'rpid': n.id}));
        var tdNo=$("<td></td>").html(i+1);
        var tdEventName = $("<td></td>").html(n.eventName);
        var tdEventBegin = $("<td></td>").html(n.eventBegin);
        var tdEventEnd = $("<td></td>").html(n.eventEnd);
        var tdLastUpdate = $("<td></td>").html(n.lastUpdate);
        var tdOperateName = $("<td></td>").html(n.operateName);
        var tdStatus = $("<td></td>").html(formatValid(n.status));
        /*var tdOperate=$("<td></td>").append('<span><a href="javascript:;" id="edit" class="text-primary margin_rg15">编辑</a></span> ' +
            '<span><a href="javascript:;" id="config" class="text-danger">配置</a></span>');
*/
        var html = '';
        html += '<a href="javascript:;" class="text-primary edit" wheelId = '+n.id+'>编辑</a>&nbsp;';
        html += '<a href="javascript:;" class="text-primary config" wheelId = '+n.id+'>配置</a>&nbsp;';
        html += '<a href="javascript:;" class="text-primary view" wheelId = '+n.id+'>查看详情</a>&nbsp;';
        html += '<a href="javascript:;" class="text-primary draw" wheelId = '+n.id+'>兑奖</a>&nbsp;';

        var tdOperate=$("<td></td>")
            .html(html);
        $("#wheelBody").append(trObj.append(tdCheck).append(tdNo).append(tdEventName).append(tdEventBegin).append(tdEventEnd).append(tdLastUpdate).append(tdOperateName).append(tdStatus).append(tdOperate));
    });

};

function formatValid(value){
    if (value == null || value == '') {
        return '';
    }else {
        if(value=='0')value='无效';
        if(value=='1')value='有效';
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