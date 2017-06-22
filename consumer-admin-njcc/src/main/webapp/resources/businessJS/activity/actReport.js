$(function(){
    var containerId='barDemo';
    var titleText = '大转盘活动';
    var subtitleText = '';
    var yAxisText = '人数（人）';
    var tooltipJson = {'title':'参与人数','decimal':0,'unit':'人'};

    $('#genBigWheelDataReport').unbind("click.genBigWheelDataReport").bind('click.genBigWheelDataReport',function(){
        ReportUtils.bar_Multiple(getBigWheelData(),containerId,titleText,subtitleText,yAxisText,tooltipJson);
    });

    $('.btn-group :button').unbind("click.btnswitch").bind('click.btnswitch',function(){
        $(this).parent().children(':button').removeClass('active');
        $(this).addClass("active");
        $('.workinput').val('');
        $('.rangeDiv').hide();
        if($(this).hasClass('day')){
            $('.daydiv').show();
        }else if($(this).hasClass('month')){
            $('.monthdiv').show();
        }else if($(this).hasClass('year')){
            $('.yeardiv').show();
        }
    });

    function getBigWheelData(){
        var fixdata = new Array();
        var fixRespData={categories:new Array(),
            series:new Array()};
        var sourcedata,dataDiv;
        var targetTimeRangeDiv = $('.btn-group .active');
        var data={};
        if($(targetTimeRangeDiv).hasClass('day')){
            dataDiv = $('.daydiv');
            data.type='day';
        }else if($(targetTimeRangeDiv).hasClass('month')){
            dataDiv = $('.monthdiv');
            data.type='month';
        }else if($(targetTimeRangeDiv).hasClass('year')){
            dataDiv = $('.yeardiv');
            data.type='year';
        }

        data.begin = $(dataDiv).find(":input[name='begin']").val();
        data.end = $(dataDiv).find(":input[name='end']").val();
        data.reqBizProj = $('.reqBizProjDiv').find("select[name='reqBizProj']").val();
        if(data.begin=='' || data.end == ''){
            layer.alert('报表需要时间范围，请选择开始结束时间！', {icon: 5});
            return;
        }
        $.ajax({
            type: "POST",
            url: fhpt_ctx+'/report/actReportController/dataReportMulti',
            data: data,
            dataType:"json",
            async:false,
            success: function(res){ // 服务器返回的 json 结果
                sourcedata=res;
            }
        });
        var categories = new Array();
        var series = new Array();
        var accessCount={name:'访问人数',data:new Array()};
        var drawCount={name:'参与人数',data:new Array()};

        $('#datatable').removeAttr("hidden");//显示
        $('#content').html('');//清空


        $.each(sourcedata,function(index,item){
            categories.push(item.title);
            accessCount.data.push(parseInt(item.accessCount));
            drawCount.data.push(parseInt(item.drawCount));

            //参与率数据
            var tmp = new Array();
            tmp[0]=item.title;
            tmp[1]=parseFloat(item.drawRate);
            fixdata[index]=tmp;

            //表格数据填充
            $('#content').append('<tr>'+
                '<th>'+item.title+'</th>'+
                '<td>'+item.accessCount+'</td>'+
                '<td>'+item.drawCount+'</td>'+
                '<td>'+item.drawRate+'%'+'</td>'+
                '</tr>')

        });
        fixRespData.categories = categories;
        fixRespData.series.push(accessCount);
        fixRespData.series.push(drawCount);

        var containerIdRate='barDemoRate';
        var titleTextRate = '大转盘活动·参与率';
        var subtitleTextRate = '';
        var yAxisTextRate = '百分比（%）';
        var tooltipJsonRate = {'title':'参与率','decimal':1,'unit':'%'};
        ReportUtils.bar(fixdata,containerIdRate,titleTextRate,subtitleTextRate,yAxisTextRate,tooltipJsonRate);

        return fixRespData;
    }
});
