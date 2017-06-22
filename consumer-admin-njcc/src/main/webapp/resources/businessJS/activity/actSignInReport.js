$(function(){
    //列表分页显示数据
    var url = '/activity/ActUserSigninReportController/list';
    CommonUtils.page(url,null,null,null,'userSignInReportFn');

    //查看
    $("#UserSignInReportBody").on('click','.personList',function(){
        var url = fhpt_ctx+"/activity/actUserSignInController/personList?loginName="+$(this).attr('loginName');
        window.location.href = url;
    });

    $("#queryUserSignInBtn").on('click',function(){
        queryUserSign();
    });

    //查询
    function queryUserSign(){
        var dataObj = {};
        $.each($('#queryUserSignInForm').serializeArray(), function() {
            dataObj[this.name] = this.value;
        });
        CommonUtils.page(url, 1, dataObj,null,'userSignInReportFn');
    }


});

function userSignInReportFn(res){
    $("#UserSignInReportBody").html("");
    var rows = res.rows;
    $.each(rows,function(i,n){
        var trObj=$("<tr></tr>");
        var tdNo=$("<td></td>").html(i+1);
        var tdLoginName = $("<td></td>").html(n.loginName);
        var tdRealName = $("<td></td>").html(n.realName);
        var tdMobile = $("<td></td>").html(n.mobile);
        var tdGender = $("<td></td>").html(n.gender);
        var tdLastSignin = $("<td></td>").html(n.lastSignin);
        var tdSigninCount = $("<td></td>").html(n.signinCount);
        var html = '';
        html += '<a href="javascript:;" class="text-primary personList" loginName = '+n.loginName+'>查看</a>&nbsp;';
        var tdOperate=$("<td></td>").html(html);
        $("#UserSignInReportBody").append(trObj.append(tdNo).append(tdLoginName).append(tdRealName).append(tdMobile).append(tdGender).append(tdLastSignin).append(tdSigninCount).append(tdOperate));
    });

};

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