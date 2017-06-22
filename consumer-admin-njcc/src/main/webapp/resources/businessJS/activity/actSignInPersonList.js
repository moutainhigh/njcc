$(function () {
    //重置
    $("#clearbtn").on('click', function () {
        $("#queryForm :input").val('');
        $("#queryForm :selected").val('');
    });

    //查询
    $("#searchBtn").on('click', function () {
        var signInBegin = $("#signInBegin").val();
        if (signInBegin) {
            signInBegin += ' 00:00:00';
        }
        var signInEnd = $("#signInEnd").val();
        if (signInEnd) {
            signInEnd += ' 23:59:59';
        }
        var dataObj = '{"signInBegin":"' + signInBegin + '","signInEnd":"' + signInEnd + '"}';
        CommonUtils.page(url, null, dataObj, null, 'userSignInFn');
    });

    //列表分页显示数据
    CommonUtils.page(url, null, originalData, null, 'userSignInFn');

    //导出
    $("#reportBtn").on('click', function () {
        var signInBegin=$("#signInBegin").val();
        var signInEnd=$("#signInEnd").val();
        if(!signInBegin || !signInEnd){
            layer.alert('导出功能必须选择开始结束时间', {icon: 0});
            return;
        }
        $("#searchBtn").click();
        if(signInBegin){
            signInBegin += ' 00:00:00';
        }
        if(signInEnd){
            signInEnd +=  ' 23:59:59';
        }
        var data ={
            page: 0,
            rows :pageSize,
            loginName: loginName,  //loginName是最原始带过来的参数
            signInBegin:signInBegin,
            signInEnd:signInEnd
        };
        var body = $(document.body),
            form = $("<form method='post'></form>"),
            input;
        form.attr("action",fhpt_ctx+"/activity/actUserSignInController/report");
        form.attr("method","post");
        $.each(data,function(key,value){
            input = $("<input type='hidden'>");
            input.attr({"name":key});
            input.val(value);
            form.append(input);
        });
        form.appendTo(document.body);
        form.submit();
        document.body.removeChild(form[0]);
    });

});

var url = '/activity/actUserSignInController/list';
var loginName = getUrlParam("loginName");
var originalData = '{"loginName":"' + loginName + '"}';

function userSignInFn(res) {
    $("#tbody").html("");
    var rows = res.rows;
    $.each(rows, function (i, n) {
        var trObj = $("<tr></tr>");
        var tdNo = $("<td></td>").html(i + 1);
        var tdLoginName = $("<td></td>").html(n.loginName);
        var tdRealName = $("<td></td>").html(n.realName);
        var tdMobile = $("<td></td>").html(n.mobile);
        var tdGender = $("<td></td>").html(n.gender);
        //var tdCreated = $("<td></td>").html(CommonUtils.formatDatebox(n.created));
        var signInTime = $("<td></td>").html(n.signInTime);
        $("#tbody").append(trObj.append(tdNo).append(tdLoginName).append(tdRealName).append(tdMobile).append(tdGender).append(signInTime));
    });

};

//获取Url参数
function getUrlParam(paramName) {
    var reParam = new RegExp('(?:[\?&]|&)' + paramName + '=([^&]+)', 'i');
    var match = window.location.search.match(reParam);
    return ( match && match.length > 1 ) ? match[1] : null;
}

function returnDate(curr, dataObj) {
    if (dataObj == null || dataObj == '') {
        var data = {
            page: curr,
            rows: pageSize
        }
        return data;
    }
    ;
    var o = eval('(' + dataObj + ')');
    var data = {
        page: curr,
        rows: pageSize,
        loginName: loginName,  //loginName是最原始带过来的参数
        signInBegin: o.signInBegin,
        signInEnd: o.signInEnd
    }
    return data;
}