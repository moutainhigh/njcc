$(function(){
    if(displayParam==0){
        $("#displayParam").removeAttr();
        $("#displayParam").attr("style","display:block");
    }else {
        $("#displayParam").removeAttr();
        $("#displayParam").attr("style","display:none");
    }

    $('.replenishSubmitBtn').unbind('click.replenishSubmitBtn').bind('click.replenishSubmitBtn',function(){
       var replenishAwardCount = $(".replenishAwardCount").val();
        // var storeCount = $("#storeHouseTbody .tdStoreCount").attr("count");
        // console.log(storeCount);
        if(replenishAwardCount===""||replenishAwardCount===null){
            layer.msg("补充库存数量不能为空", {time:2000});
            $(".replenishAwardCount").focus();
            return;
        }else if(!CommonUtils.MathCheck.INTEGER(replenishAwardCount)){
            layer.msg('补充库存数量应为整数',{time:2000});
            $(".replenishAwardCount").focus();
            return;
        }
        var tdId = $("#storeHouseTbody .tdId").attr("tdId");
        var tdActAwardId = $("#storeHouseTbody .actAwardId").attr("tdActAwardId");
        var divId = $("#storeHouseTbody .actAwardId").attr("divId");
        var url = fhpt_ctx+"/activity/actStoreHouseController/getActStoreHouse";
        ajaxReq(url,{"id":tdId},function (data) {
            if (data && data.count==1) {
                var n = data.actStoreHouse;
                if(n.storeCount<=replenishAwardCount){
                    layer.msg("库存不足，请填写小于"+n.storeCount+"的数量！", {time:2000});
                }else {
                    layer.confirm('确定补充库存吗？确定后直接补充！', {btn: ['确定','取消']}, //按钮
                        function(){
                            var dataJson = {"actStoreHouseId":tdId,"actAwardId":tdActAwardId,"storeCountAlarm":n.storeCountAlarm,"storeCount":n.storeCount,"replenishAwardCount":replenishAwardCount,"inform":n.inform};
                            ajaxReq(fhpt_ctx+"/activity/actAwardController/updateActAwardAndActStoreHouse",dataJson,function (data) {
                                if(data&&data.count==1){
                                    $("#storeHouseManageModel").modal("toggle");
                                    var input = " input[name='awardCount']";
                                    $("#"+divId+input).val(data.awardCount);
                                    layer.msg('已更新库存',{time: 2000});
                                }else {
                                    layer.msg(data.errorMsg, {time:2000});
                                }
                            });
                        },
                        function(){});
                }
            }else{
                layer.msg(data.errorMsg, {time:2000});
            }
        });
    });


    $('.actWardBtn').unbind('click.actWardBtn').bind('click.actWardBtn',saveActAward);

    $('.actSortBtn').unbind('click.actSortBtn').bind('click.actSortBtn',function(){
        var size = $("#actAwardSortDiv").find('.form-horizontal').size();
        var list_map = new Array;
        var url = fhpt_ctx+"/activity/actAwardController/updateActAwardList";
        for(var i =0;i<size;i++){

                var select =' select option:selected';
                var awardId = $("#sortDiv"+i+select).val();//选择奖品
                var sortIndex = " input[name='sortIndex']";
                var sort = $("#sortDiv"+i+sortIndex).val();//排序


            var data ={"id":awardId,"awardSort":sort};

            list_map.push(data);
        }
        $.ajax({
            type: "POST",
            url:url,
            data:JSON.stringify(list_map),
            contentType: "application/json; charset=utf-8",
            dataType:"json",
            success: function(data){ // 服务器返回的 json 结果
                if (data && data.count==1) {
                    layer.alert("更新奖品排序成功！", {icon: 6});
                    //layer.msg(data.successMsg, {time:2000});
                    //window.location.href=fhpt_ctx+'/activity/actEventWheelController/manage';
                }else {
                    layer.msg(data.errorMsg, {time:2000});
                }
            },
            error: function(data){ // 服务器返回的 json 结果
                layer.msg(data.errorMsg, {time:2000});
            }
        });
        //获取
        /*if(list_map != null && list_map.length > 0){
         for ( var i = 0; i < list_map.length; i++) {
         alert(list_map[i].id);
         }
         }*/
    });

    $('.lastWinningProbability').unbind('input click').bind('input click',function() {
        var size = $("#actAwardDiv").find('.form-horizontal').size();
        var winningProbabilityCount = 0;
        var inputWinningProbability = " input[name='winningProbability']";
        for(var i =0;i<size;i++){
            if(i!=(size-1)){
                var winningProbability=$("#div"+i+inputWinningProbability).val();//中奖概率
                winningProbabilityCount=parseInt(winningProbability)+ parseInt(winningProbabilityCount);
            }
        }
        if(parseInt(winningProbabilityCount)>100){
            layer.msg("中奖概率不能超过100", {time:2000});
        }else {
            $("#div"+(size-1)+inputWinningProbability).val(100-winningProbabilityCount);//最后的中奖概率
        }
    });

    $(":radio").click(function(){
        if($(this).attr("name").startsWith("exchangeType")){
            var index = $(this).attr("name").replace("exchangeType","");
            if($(this).val()==="2"){
                $("#offlineStoreRadio"+index).attr("style","display:none");
                $("#exchangeAddress"+index).attr("style","display:none");
            }else if($(this).val()==="1"){
                $("#offlineStoreRadio"+index).attr("style","display:block");
                $("#exchangeAddress"+index).attr("style","display:block");
            }
            emptyVal(index);
        }else if($(this).attr("name").startsWith("offlineStoreType")){
            var index = $(this).attr("name").replace("offlineStoreType","");
            if($(this).val()==="2"){
                $("#offlineStoreRadio"+index).find(".pwdVerification").attr("style","display:block");
            }else if($(this).val()==="1"){
                $("#offlineStoreRadio"+index).find(".pwdVerification").val("");//口令核销清空
                $("#offlineStoreRadio"+index).find(".pwdVerification").attr("style","display:none");
            }
        };
    });

    //保存选择的地址
    $('.exchangeAddressModify').unbind('click.exchangeAddressModify').bind('click.exchangeAddressModify',function(){
        var checks = $("input[name=check]").is(":checked");
        var divId = $("#divId").val();
        var checkedIds= [];
        if(checks){
            /*var addressNodes = $("#"+divId).find(".exchangeAddress .storeAddress");
            // console.log(addressNodes);
            $.each(addressNodes,function (i,n) {
                $("#"+divId).find(".storeAddress").remove();
            });*/
            $("#"+divId).find(".exchangeAddress").empty();
            $.each($("input[name=check]:checked"),function (i,n) {
                $("#"+divId).find(".exchangeAddress").append("<div class='storeAddress'>"+$(n).attr("storeAddress")+"</div>");
                checkedIds.push($(n).attr("stmId"));
            })
            $("#exchangeAddressManageModel").modal('toggle');
            $("#"+divId).find(":input[name=exchangeAddress]").val(checkedIds);
        }else {
            layer.msg("请选择最少一个地址!!！", {time:2000});
        }
    });

    //加载兑换地址弹出层
    $('.exchangeAddressModelClose').unbind('click.exchangeAddressModelClose').bind('click.exchangeAddressModelClose',function(){
        $("#exchangeAddressManageModel").modal('toggle');
    });

    $("#queryExchangeAddressBtn").click(function () {
        queryExchangeAddressForm();
    });

    function queryExchangeAddressForm(){
        $("#selAll").prop("checked", false);
        var dataObj = {};
        $.each($('#queryExchangeAddressForm').serializeArray(), function() {
            dataObj[this.name] = this.value;
        });
        ajaxStoreList(dataObj);
    }

    $("#queryExchangeAddressForm #clearbtn").click(function(){
        $("#storeAddress").val('');
        $("#selAll").prop("checked", false);
        $("input[name=check]").prop("checked", false);
    });

});

function saveActAward() {
    $(this).unbind('click', saveActAward);
    var size = $("#actAwardDiv").find('.form-horizontal').size();
    var list_map = new Array;
    var url = fhpt_ctx+"/activity/actAwardController/addActAwardList";
    var winningProbabilityCount = 0;
    for(var i =0;i<size;i++){
        var inputId = " input[name='id']";
        var id=$("#div"+i+inputId).val();//id
        var inputAwardName = " input[name='awardName']";
        var awardName=$("#div"+i+inputAwardName).val();//奖品名称
        var inputWinningProbability = " input[name='winningProbability']";
        var winningProbability=$("#div"+i+inputWinningProbability).val()/100;//中奖概率
        var inputiImgUrl= " input[name='imgUrl']";
        var imgUrl=$("#div"+i+inputiImgUrl).val();//图片

        winningProbabilityCount=parseInt($("#div"+i+inputWinningProbability).val())+ parseInt(winningProbabilityCount);
        if(parseInt(winningProbabilityCount)>100){
            layer.msg("中奖概率不能超过100", {time:2000});
            $(this).bind('click', saveActAward);
            return;
        }

        if(awardName===""||awardName===null){
            layer.msg("奖品名称不能为空", {time:2000});
            $("#div"+i+inputAwardName).focus();
            $(this).bind('click', saveActAward);
            return;
        }else if(awardName.length>6){
            layer.msg("奖品名称长度不能大于6", {time:2000});
            $("#div"+i+inputAwardName).focus();
            $(this).bind('click', saveActAward);
            return;
        }
        if(winningProbability===""||winningProbability===null){
            layer.msg("中奖概率不能为空", {time:2000});
            $("#div"+i+inputWinningProbability).focus();
            $(this).bind('click', saveActAward);
            return;
        }

        if(!CommonUtils.MathCheck.INTEGER($("#div"+i+inputWinningProbability).val())){
            layer.msg("中奖概率填写有误，请重填0到100以内的正整数百分比",{time:2000});
            $("#div"+i+inputWinningProbability).focus();
            $(this).bind('click', saveActAward);
            return;
        };

        if(i!=(size-1)){
            var select =' select option:selected';
            var storeHouseId = $("#div"+i+select).val();//选择奖品
            var inputAwardCount= " input[name='awardCount']";
            var awardCount=$("#div"+i+inputAwardCount).val();//奖品数量
            var inputAwardCountAlarm= " input[name='awardCountAlarm']";
            var awardCountAlarm=$("#div"+i+inputAwardCountAlarm).val();//转盘奖品库存警告
            var inputInform= " input[name='inform']";
            var inform=$("#div"+i+inputInform).val().trim();//警报通知人电话号码

            if(storeHouseId===""||storeHouseId===null){
                layer.msg("选择奖品不能为空", {time:2000});
                $("#div"+i+select).focus();
                $(this).bind('click', saveActAward);
                return;
            }
            if(awardCount===""||awardCount===null){
                layer.msg("奖品数量不能为空", {time:2000});
                $("#div"+i+inputAwardCount).focus();
                $(this).bind('click', saveActAward);
                return;
            }else if(!CommonUtils.MathCheck.INTEGER(awardCount)){
                layer.msg('奖品数量应为整数',{time:2000});
                $("#div"+i+inputAwardCount).focus();
                $(this).bind('click', saveActAward);
                return;
            }
            if(awardCountAlarm===""||awardCountAlarm===null){
                layer.msg("转盘奖品库存警告数量不能为空", {time:2000});
                $("#div"+i+inputAwardCountAlarm).focus();
                $(this).bind('click', saveActAward);
                return;
            }else if(!CommonUtils.MathCheck.INTEGER(awardCountAlarm)){
                layer.msg('转盘奖品库存警告数量应为整数',{time:2000});
                $("#div"+i+inputAwardCountAlarm).focus();
                $(this).bind('click', saveActAward);
                return;
            }

            if(parseInt(awardCountAlarm) > parseInt(awardCount)){
                layer.msg('转盘奖品库存警告数量不能多于奖品数量',{time:2000});
                $("#div"+i+inputAwardCountAlarm).focus();
                $(this).bind('click', saveActAward);
                return;
            }

            if(inform===""||inform===null){
                layer.msg("请填写警报通知人电话号码",{time:2000});
                $("#div"+i+inputInform).focus();
                $(this).bind('click', saveActAward);
                return;
            } else if(inform.length == 11 ){//先判断手机号长度， 如果长度是11， 则判断是一个电话号码， 其他的长度， 算多个号码， 先转成数组
                if(!CommonUtils.CheckPhone.CHECK_MOBILE(inform)){
                    layer.msg("手机号码填写有误，请重填",{time:2000});
                    $("#div"+i+inputInform).focus();
                    $(this).bind('click', saveActAward);
                    return;
                };
            }else if(inform.length < 11){
                layer.msg("手机号码格式有误，请重填",{time:2000});
                $("#div"+i+inputInform).focus();
                $(this).bind('click', saveActAward);
                return;
            }else if(inform.length > 11){
                var indexOf = inform.indexOf(",");
                inform = inform.replace(new RegExp('，','gm'),',');
                if(indexOf != -1){
                    var informArr=inform.split(",");
                    var arrayObj = new Array();
                    for(var b=0;b<informArr.length;b++){
                        if(!CommonUtils.CheckPhone.CHECK_MOBILE(informArr[b].trim())){
                            layer.msg("该手机号码填写有误，请重填"+informArr[b].trim(),{time:2000});
                            $("#div"+i+inputInform).focus();
                            $(this).bind('click', saveActAward);
                            return;
                        };
                        arrayObj.push(informArr[b].trim());
                    }
                    inform = arrayObj.toString();
                }else {
                    layer.msg("手机号码填写有误，请重填",{time:2000});
                    $("#div"+i+inputInform).focus();
                    $(this).bind('click', saveActAward);
                    return;
                }
            }

            var exchangeAddress = $("#div"+i).find(":input[name=exchangeAddress]").val();
            /*if(exchangeAddress===""||exchangeAddress===null){
             layer.msg("兑换地址不能为空", {time:2000});
             $("#div"+i).find(".exchangeAddressManageBtn").focus();
             return;
             }*/
        }

        if(imgUrl===""||imgUrl===null){
            layer.msg("图片不能为空", {time:2000});
            var imgSel = " .imgSel";
            $("#div"+i+imgSel).focus();
            $(this).bind('click', saveActAward);
            return;
        }

        var belongId=$("#belongId").val();//belongId
        var inputAwardSort = " input[name='awardSort']";
        var inputIsNoAward = " input[name='isNoAward']";
        var awardSort=$("#div"+i+inputAwardSort).val();//排序
        var isNoAward=$("#div"+i+inputIsNoAward).val();//是否中奖
        if(id===""||id ===null){
            var data ={"isNoAward":isNoAward,"belongId":belongId,"awardSort":awardSort,"awardName":awardName,"winningProbability":winningProbability,"imgUrl":imgUrl,"storeHouseId":storeHouseId,"awardCount":awardCount,"awardCountAlarm":awardCountAlarm,"inform":inform};
        }else {
            var data ={"isNoAward":isNoAward,"belongId":belongId,"id":id,"awardName":awardName,"winningProbability":winningProbability,"imgUrl":imgUrl,"storeHouseId":storeHouseId,"awardCount":awardCount,"awardCountAlarm":awardCountAlarm,"inform":inform};
            url = fhpt_ctx+"/activity/actAwardController/updateActAwardList";
        }

        // exchangeType; //兑奖方式类型 1为线下门店兑换(包括类型，地址)  2为线上邮寄
        // offlineStoreType; //线下门店兑换类型 1为自助兑换  2为口令核销(包括口令核销码)
        // pwdVerification; //当线下门店兑换类型为2时，口令核销
        /*var exchangeType =$("#div"+i).find(":input[name='exchangeType"+i+"']:checked").val();
         if(exchangeType==="1"){
         data.exchangeType=exchangeType;
         var offlineStoreType =$("#div"+i).find(":input[name='offlineStoreType"+i+"']:checked").val();
         data.offlineStoreType=offlineStoreType;
         if(offlineStoreType==="2"){
         var pwdVerification =$("#div"+i).find(".pwdVerification").val();
         data.pwdVerification=pwdVerification;
         }
         data.exchangeAddress=exchangeAddress;
         }else if(exchangeType==="2"){
         data.exchangeType=exchangeType;
         }*/
        //清除了，所以直接传递参数更新
        var exchangeType =$("#div"+i).find(":input[name='exchangeType"+i+"']:checked").val();
        var offlineStoreType =$("#div"+i).find(":input[name='offlineStoreType"+i+"']:checked").val();
        var pwdVerification =$("#div"+i).find(".pwdVerification").val();

        data.exchangeType=exchangeType;
        if(exchangeType==="1"){
            if(offlineStoreType=='2' && (pwdVerification == null || pwdVerification == '')){
                layer.msg("选择口令核销时，核销码不能为空！",{time:2000});
                $("#div"+i).find(".pwdVerification").focus();
                $(this).bind('click', saveActAward);
                return;
            }
            data.offlineStoreType=offlineStoreType;
        }else {
            data.offlineStoreType="";
        }

        if(exchangeType==="1"){
            if(exchangeAddress == null || exchangeAddress == ''){
                layer.msg("选择线下门店兑换时，兑换地址不能为空！",{time:2000});
                $("#div"+i).find(".exchangeAddressManageBtn").focus();
                $(this).bind('click', saveActAward);
                return;
            }
            data.offlineStoreType=offlineStoreType;
        }
        data.pwdVerification=pwdVerification;
        data.exchangeAddress=exchangeAddress;
        list_map.push(data);
    }
    $.ajax({
        type: "POST",
        url:url,
        data:JSON.stringify(list_map),
        contentType: "application/json; charset=utf-8",
        dataType:"json",
        success: function(data){ // 服务器返回的 json 结果
            if (data && data.count==1) {
                layer.confirm(data.successMsg, {btn: ['确定']},function () {
                    var size = $("#actAwardDiv").find('.form-horizontal').size();
                    if(data.actIds!=null){
                        //新增成功时,把id值赋予每个div
                        for(var i =0;i<size;i++){
                            var inputId = " input[name='id']";
                            $("#div"+i+inputId).val(data.actIds[i]);//id
                        }
                    }
                    //新增、编辑成功时奖品排序名称更改
                    // var actAwards = $("#sort").find(":input[name='actAwards']").val();

                    for(var i =0;i<size;i++){
                        $("#sortDiv"+i).find(".awardSortType option").remove();
                        var actAwardsId =$("#sortDiv"+i).find(":input[name='actAwardsId']").val();
                        if(typeof(actAwardsId)=="undefined"){
                            $.each(data.actJson,function(index,item) {
                                if(index==i){
                                    $("#sortDiv"+i).append("<input type='hidden' name='actAwardsId' value='"+item.id+"'/>");
                                    $("#sortDiv"+i).find(".awardSortType").append("<option value='"+item.id+"' selected='selected'>"+item.awardName+"</option>");
                                }else {
                                    $("#sortDiv"+i).find(".awardSortType").append("<option value='"+item.id+"'>"+item.awardName+"</option>");
                                }
                            });
                        }else {
                            $.each(data.actJson,function(index,item) {
                                if(actAwardsId==item.id){
                                    $("#sortDiv"+i).find(".awardSortType").append("<option value='"+item.id+"' selected='selected'>"+item.awardName+"</option>");
                                }else {
                                    $("#sortDiv"+i).find(".awardSortType").append("<option value='"+item.id+"'>"+item.awardName+"</option>");
                                }
                            });
                        }
                    }

                    $('.actWardBtn').bind('click', saveActAward);
                    layer.closeAll('dialog');
                });
                // layer.alert(data.successMsg, {icon: 6});
                //layer.msg(data.successMsg, {time:2000});
                //window.location.href=fhpt_ctx+'/activity/actEventWheelController/manage';
            }else {
                layer.msg(data.errorMsg, {time:2000});
                $('.actWardBtn').bind('click', saveActAward);
            }
        },
        error: function(data){ // 服务器返回的 json 结果
            layer.msg(data.errorMsg, {time:2000});
            $('.actWardBtn').bind('click', saveActAward);
        }
    });
    //获取
    /*if(list_map != null && list_map.length > 0){
     for ( var i = 0; i < list_map.length; i++) {
     alert(list_map[i].id);
     }
     }*/
}

function emptyVal(index) {
    $("#offlineStoreRadio"+index).find(".pwdVerification").val("");//口令核销清空
    $("#offlineStoreRadio"+index).find(":input[name='offlineStoreType"+index+"']").removeAttr("checked");//去除线下门店兑换类型 选中
    $("#offlineStoreRadio"+index).find(":input[name='offlineStoreType"+index+"']:first").prop('checked', true);//线下门店兑换类型第一个选中
    $("#offlineStoreRadio"+index).find(".pwdVerification").attr("style","display:none");//隐藏口令核销input
    $("#exchangeAddress"+index).find(":input[name=exchangeAddress]").val("");//兑换地址清空
    $("#exchangeAddress"+index).find(".exchangeAddress").empty();//兑换预览地址清空
}


//全选、全不选
function doSelectAll(){
    $("input[name=check]").prop("checked", $("#selAll").is(":checked"));
}

function exchangeAddressOpt(divId) {
    $("#storeAddress").val('');
    $("input[name=check]").prop("checked", false);
    $("#selAll").prop("checked", false);
    $("#divId").attr("value",divId);
    // $("input[name=check]").is(":checked");
    ajaxStoreList();
}

function ajaxStoreList(dataObj) {
    ajaxReq(fhpt_ctx+'/njcc/storeController/list',returnData(dataObj),function (data) {
        if (data && data.errorMsg==null) {
            $("#exchangeAddressBody").html("");

            var rows = data.rows;
            $.each(rows,function(i,n){
                var trObj=$("<tr></tr>");
                var tdCheck=$("<td></td>").append($('<input type="checkbox" name="check"/>').attr({'stmId': n.id,'storeAddress':n.storeAddress}));
                var tdStoreAddress = $("<td></td>").html(n.storeAddress);
                $("#exchangeAddressBody").append(trObj.append(tdCheck).append(tdStoreAddress));
            });
            $('#exchangeAddressList').prev("div").text('共'+data.total+'条记录').attr("style","font-size: 14px");
            $("#exchangeAddressManageModel").modal('show');
        }else{
            layer.msg(data.errorMsg, {time:2000});
        }
    });
}

function returnData(dataObj){
    if(dataObj == null || dataObj == '')
    {
        var data ={
            page: 1,
            rows :10000
        }
        return data;
    };
    dataObj['page']=1;
    dataObj['rows']=1000;
    return dataObj;
}

function gradeChange(selectDivId,index){
    var selected =' select option:selected';
    var selectActAward = $("#"+selectDivId+selected).val();//选择奖品
    //当前选中的这个下拉框的旧值
    var oldVal=$("#"+selectDivId).find(":input[name='actAwardsId']").val();
    // console.info(selectActAward+'---'+selectDivId+'--old:'+oldVal);
    //获取已经存在的已经根据这个值选中的option
    var matchSelect = $('select option:selected[value='+selectActAward+']').parent().parent();
    $.each(matchSelect,function(index,item){
        // console.info($(item).html()+'---'+$(item).attr("id"));
        var tmpId = $(item).attr("id");
        if(tmpId!=selectDivId){
            //开始置换,把旧的值赋值被置换的
            $(item).find('select').val(oldVal);
            $(item).find(":input[name='actAwardsId']").val(oldVal);
            //自己本身的隐藏赋值被选中的
            $('#'+selectDivId).find(":input[name='actAwardsId']").val(selectActAward);
        }
    });
}

function storeHouseTypeChange(selectDivId,index){
    var storeHouseId= $("#"+selectDivId).find(":input[name=storeHouseId]").val();
    var actAwardId= $("#"+selectDivId).find(":input[name=storeHouseId]").attr("actAwardId");
    // console.log(storeHouseId);
    // console.log(actAwardId);
    if(storeHouseId!=""){
        // var selected =' select option:selected';
        // var selectActAward = $("#"+selectDivId+selected).val();//选择奖品
        // console.log(selectActAward);
        var awardCount= $("#"+selectDivId).find(":input[name=awardCount]").val();
        // console.log(awardCount);
        if(awardCount!="0"){
            var url =fhpt_ctx+"/activity/actStoreHouseController/updateStoreCount";
            ajaxReq(url,{"id":storeHouseId,"actAwardId":actAwardId},function (data) {
                if (data && data.count==2) {
                    $("#"+selectDivId).find(":input[name=awardCount]").val("0");
                    layer.alert('更改库存数量成功!,奖品数量已清零，请选择补充库存按钮补充库存！', {icon: 0});
                }else {
                    layer.msg(data.errorMsg, {time:2000});
                }
            });
        }
    }
}

function replenishStoreHouse(divId) {
    var select =' select option:selected';
    var selectId = $("#"+divId+select).val();
    var input = " input[name='id']";
    var actAwardId = $("#"+divId+input).val();
    var url =fhpt_ctx+"/activity/actStoreHouseController/getActStoreHouse";
    ajaxReq(url,{"id":selectId},function (data) {
        if (data && data.count==1) {
            $("#storeHouseTbody").empty();
            $(".replenishAwardCount").val("");
            var n = data.actStoreHouse;
            var trObj=$("<tr></tr>");
            var tdNo=$("<td></td>")
                .html(1);
            var tdGoodsName=$("<td></td>")
                .html(n.goodsName);
            var tdGoodsTypeName=$("<td></td>")
                .html(n.goodsBelongTypeName);
            var tdStoreCount=$("<td class='tdStoreCount' count='"+n.storeCount+"'></td>")
                .html(n.storeCount);
            var tdId=$("<td class='tdId' tdId='"+n.id+"'></td>")
                .html(n.id).attr("style","display:none");
            var tdActAwardId=$("<td class='actAwardId' tdActAwardId='"+actAwardId+"' divId='"+divId+"'></td>")
                .html(actAwardId).attr("style","display:none");
            $("#storeHouseTbody").append(trObj.append(tdNo).append(tdGoodsName).append(tdGoodsTypeName).append(tdStoreCount).append(tdId).append(tdActAwardId));
            $("#storeHouseManageModel").modal("toggle");
        }else{
            layer.msg(data.errorMsg, {time:2000});
        }
    });
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

function upload(divId){
    var index = divId.replace("div","");
    var fileId  = 'imgSel'+index;
    var file = $("#"+divId).find(".imgSel").val();
    if(file==""){
        layer.alert('请选择文件！！', {icon: 0});
    }else if(!/\.(gif|jpg|jpeg|png|bmp|GIF|JPG|PNG|BMP)$/.test(file)){
        layer.alert('图片类型必须是.gif,jpeg,jpg,png,bmp中的一种', {icon: 0});
        $("#imgSel").val("");
    }else if(!checkImgPX(divId,"320","250")){
        layer.alert('图的尺寸应该是320*250', {icon: 0});
        $("#imgSel").val("");
    }else{
        $.ajaxFileUpload({
            url : fhpt_ctx+"/njcc/fileUpload/image?type=actAward",   //submit to UploadFileServlet
            secureuri : false,
            fileElementId : fileId,
            dataType : 'json', //or json xml whatever you like~
            success : function(data, status) {
                layer.msg("成功上传图片！", {time:2000});
                $("#"+divId).find(".imgUrl").val(data.imageGroup.sourceUrl);
                $("#"+divId).find(".imghead").attr("src",imagePathStr + data.imageGroup.sourceUrl).attr("width","320").attr("height","250");
            },
            error : function(data, status, e) {
                alert(data+"       "+status+"            "+e);
            }
        });
    }
}

/*
 * 判断图片大小
 *
 * @param divID
 *          当前div的ID
 * @param width
 *          需要符合的宽
 * @param height
 *          需要符合的高
 * @return true-符合要求,false-不符合
 */
function checkImgPX(divId, width, height) {
    var imgwidth=$("#"+divId).find(".imgWidthHeight").attr("imgWidth");
    var imgheight=$("#"+divId).find(".imgWidthHeight").attr("imgHeight");
    // console.log("imgwidth=="+imgwidth+"imgheight=="+imgheight);

    if(imgwidth != width || imgheight != height) {
        return false;
    }
    return true;
}

//图片上传预览
function previewImage(file,preview,imghead) {
    var MAXWIDTH  = 320;
    var MAXHEIGHT = 250;
    var div = document.getElementById(preview);
    if (file.files && file.files[0]) {
        div.innerHTML ='<img class="imghead" id="'+imghead+'">';
        var img = document.getElementById(imghead);
        img.onload = function(){
            var width  = img.width;
            var height = img.height;
            div.innerHTML+='<input class="imgWidthHeight" imgWidth="'+width+'" imgHeight="'+height+'" hidden>';
            var rect = clacImgZoomParam(MAXWIDTH, MAXHEIGHT, img.offsetWidth, img.offsetHeight);
            img.width  =  rect.width;
            img.height =  rect.height;
            //img.style.marginLeft = rect.left+'px';
            img.style.marginTop = rect.top+'px';
        }
        var reader = new FileReader();
        reader.onload = function(evt){img.src = evt.target.result;	//预览图片地址
        };
        reader.readAsDataURL(file.files[0]);
        layer.msg("请点击上传按钮上传图片！", {time:2000});
    }else {	//兼容IE
        var sFilter='filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale,src="';
        file.select();
        var src = document.selection.createRange().text;
        div.innerHTML ='<img id="'+imghead+'">';
        var img = document.getElementById(imghead);
        img.filters.item('DXImageTransform.Microsoft.AlphaImageLoader').src = src;
        var rect = clacImgZoomParam(MAXWIDTH, MAXHEIGHT, img.offsetWidth, img.offsetHeight);
        status =('rect:'+rect.top+','+rect.left+','+rect.width+','+rect.height);
        div.innerHTML = "<div id='"+divhead+"'class='imghead' style='width:"+rect.width+"px;height:"+rect.height+"px;margin-top:"+rect.top+"px;"+sFilter+src+"\"'></div>";
    }
}
function clacImgZoomParam( maxWidth, maxHeight, width, height ){
    var param = {top:0, left:0, width:width, height:height};
    if( width>maxWidth || height>maxHeight ) {
        rateWidth = width / maxWidth;
        rateHeight = height / maxHeight;
        if( rateWidth > rateHeight ){
            param.width =  maxWidth;
            param.height = Math.round(height / rateWidth);
        }else{
            param.width = Math.round(width / rateHeight);
            param.height = maxHeight;
        }
    }

    param.left = Math.round((maxWidth - param.width) / 2);
    param.top = Math.round((maxHeight - param.height) / 2);
    return param;
};