$(function(){
    var reqType = $("#reqType").val();
    var url;
    //请求类型如果为3， 则是医疗app
    if(reqType == "3"){
        url = "/njcc3/accountBindNjccCardController/listForHiss";
        //$("#typeForReq").hide();
    }else {
        url = "/njcc3/accountBindNjccCardController/list";
    }
    //var url = "/njcc3/accountBindNjccCardController/list";
    CommonUtils.page(url);
    $("#Search").click(function() {
        var cardCategory=$("#cardCategory").val();
        var aliasCode=$("#aliasCode").val();
        var bindingStatus=$("#bindingStatus").val();
        var reqBizType=$("#reqBizType").val();
        var dataObj = '{"cardCategory":"'+cardCategory+'","aliasCode":"'+aliasCode+'","bindingStatus":"'+bindingStatus+'", "reqBizType":"'+reqBizType+'"}';
        CommonUtils.page(url,1,dataObj);
    });

    $("#clearbtn").click(function() {
        $("#cardCategory").val('');
        $("#aliasCode").val('');
        $("#bindingStatus").val('');
        $("#reqBizType").val('');
    });
})

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
        cardCategory:o.cardCategory,
        aliasCode:o.aliasCode,
        bindingStatus:o.bindingStatus,
        reqBizType:o.reqBizType
    }
    return data;
}

function callbackRes(res){
    $("#tbody").html("");
    var rows = res.rows;
    $.each(rows,function(i,n){
        var trObj=$("<tr></tr>");
        var tdAge=$("<td></td>")
            .html(i+1);
        var tdCardcategory = $("<td></td>")
            .html(formatCardCatalog(n.cardCategory));
        var tdAliascode = $("<td></td>")
            .html(n.aliasCode);
        var tdLoginid = $("<td></td>")
            .html(n.loginName);
        var tdBinDingStatus = $("<td></td>")
            .html(formatBinDingStatus(n.bindingStatus));
        var reqType = $("#reqType").val();
        $("#tbody").append(trObj.append(tdAge).append(tdCardcategory).append(tdAliascode).append(tdLoginid)
            .append(tdBinDingStatus));
        if(reqType != '3') {
            var tdReqBizType = $("<td></td>")
                .html(formatReqType(n.reqBizType));
            trObj.append(tdReqBizType);
        }
    })
};

function formatBinDingStatus(value){
    if (value ==0){
        return "已绑定";
    }else if (value == 1){
        return "已解绑";
    }
    return value;
}

function formatCardCatalog(value){
    if (value =='01'){
        return "市民卡A卡";
    }else if (value == '02'){
        return "市民卡B卡";
    }else if (value == '03'){
        return "金陵通卡、紫金卡";
    }else if (value == '04'){
        return "市民卡C卡";
    }else if (value == '05'){
        return "旅游年卡";
    }else if (value == '06'){
        return "助老卡";
    }
    return value;
}

function formatReqType(value){
    if(value=='1' || value == 2 || value == null) {value='智汇app';}
    if(value=='3'){value='医疗app';}
    return value;
}