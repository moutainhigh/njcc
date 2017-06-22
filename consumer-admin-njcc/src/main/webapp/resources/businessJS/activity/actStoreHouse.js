$(function(){
    var url = '/activity/actStoreHouseController/list';
    /*$('.storeHouseTypeManageBtn').unbind('click.storeHouseTypeManageBtn').bind('click.storeHouseTypeManageBtn',function(){
        $('#storeHouseTypeManageModel .modal-content').empty();
        $('#storeHouseTypeManageModel').removeData('bs.modal').modal({
            remote: fhpt_ctx+"/activity/actStoreHouseTypeController/storeHouseTypeModel"
        });
    });*/
    $('.goodsTypeManageBtn').unbind('click.goodsTypeManageBtn').bind('click.goodsTypeManageBtn',function(){
            $('#goodsTypeManageModel .modal-content').empty();
            $('#goodsTypeManageModel').removeData('bs.modal').modal({
                remote: fhpt_ctx+"/activity/actGoodsTypeController/actGoodsTypeModel"
            });
        });

    /**新增库存*/
    $('#createStore').unbind('click.createStore').bind('click.createStore',function(){
        window.location.href=fhpt_ctx+"/activity/actStoreHouseController/inputStore";
    });

    /**修改库存*/
    $('#editStore').unbind('click.editStore').bind('click.editStore',function(){
        var selectBox = $('#storeHouseBody tr td :checkbox:checked');
        if(selectBox.length!=1){
            layer.alert('请选择您要编辑的数据！', {icon: 0});
            return;
        }
        var ctid = $(selectBox).attr('ctid');
        window.location.href=fhpt_ctx+"/activity/actStoreHouseController/inputStore?id="+ctid;
    });

    function queryGoodsTypeSelect(){
        var cbSelect = $('.goodsType');
        cbSelect.empty();
        cbSelect.append($('<option></option>').attr({'value':''}).text('请选择'));
        $.ajax({
            type: "POST",
            url: fhpt_ctx+'/activity/actGoodsTypeController/list',
            data: {'valid':'1',
                'page': 1,
                'rows' :1000},
            dataType:"json",
            async:false,
            success: function(res){ // 服务器返回的 json 结果
                $.each(res.rows,function(){
                    var soption = $('<option></option>').attr({'value':this.goodsTypeCode}).text(this.goodsTypeName);
                    cbSelect.append(soption);
                });
            }
        });
    }
    $('#goodsTypeManageModel').on('hide.bs.modal', function () {
        queryGoodsTypeSelect();
    });

    queryGoodsTypeSelect();
    CommonUtils.page(url,null,null,null,'actStoreHouseFn');
    $("#queryStoreBtn").click(function () {
        queryStoreHouseForm();
    });
    function queryStoreHouseForm(){
        var dataObj = {};
        $.each($('#queryStoreHouseForm').serializeArray(), function() {
            dataObj[this.name] = this.value;
        });
        CommonUtils.page(url, 1, dataObj,null,'actStoreHouseFn');
    }

    $("#clearbtn").click(function(){
        $('#queryStoreHouseForm input').val('');
        $('#queryStoreHouseForm select').val('');
    });
});

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

function actStoreHouseFn(res){
    $("#storeHouseBody").html("");
    var rows = res.rows;
    $.each(rows,function(i,n){
        var trObj=$("<tr></tr>");
        var tdCheck=$("<td></td>").append($('<input type="checkbox" name="check" onclick="selectcheck(this);"/>').attr({'ctid': n.id}));
        var tdGoodsName = $("<td></td>").html(n.goodsName);
        var tdStoreCount = $("<td></td>").html(n.storeCount);
        var tdStoreCountAlarm = $("<td></td>").html(n.storeCountAlarm);
        var tdGoodsType = $("<td></td>").html(n.goodsType).hide();
        var tdGoodsBelongTypeName = $("<td></td>").html(n.goodsBelongTypeName);
        var tdCreated = $("<td></td>").html(CommonUtils.formatDatebox(n.lastUpdate));
        var tdInform = $("<td></td>").html(n.inform);
        $("#storeHouseBody").append(trObj.append(tdCheck).append(tdGoodsName).append(tdStoreCount).append(tdStoreCountAlarm)
            .append(tdGoodsType).append(tdGoodsBelongTypeName).append(tdInform).append(tdCreated));
    });

};
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
};

