$(function(){
    if(displayParam==0){
        $("#displayParam").removeAttr();
        $("#displayParam").attr("style","display:block");
    }else {
        $("#displayParam").removeAttr();
        $("#displayParam").attr("style","display:none");
    }

    //切换广告预览的模板
    var imgType = document.getElementById("type");
    var imgClass = document.getElementsByClassName("body_float")[0];
    var imgClassChild = document.getElementsByClassName("body_float_img")[0];
    var imgPic = document.getElementById('imgPic');
    imgType.onchange = function(){
        if(imgType.value==0||imgType.value==1){
            imgClass.style.backgroundImage="url("+fhpt_img+"/phone.png)";
            imgClassChild.style.height="100px";
            imgClassChild.style.margin="102px auto 0";
            imgPic.style.height="100px";
            $("#greetBtn").removeAttr();
            $("#greetBtn").attr("style","display:none");
        };
        if(imgType.value==2){
            imgClass.style.backgroundImage="url("+fhpt_img+"/phone1.png)";
            imgClassChild.style.height="365px";
            imgClassChild.style.margin="57px auto 0";
            imgPic.style.height="365px";
            $("#greetBtn").removeAttr();
            $("#greetBtn").attr("style","display:block");
        };
    };
    var typeNo=$("#typeNo").val();
    var reqBizTypeNo=$("#reqBizTypeNo").val();
    var select = $("#type");
    var selectReqBizType = $("#reqBizType");
    if(typeof(typeNo) == 'undefined'||typeNo == ''){
        select.removeAttr('disabled');
    }else {

        select.removeAttr('disabled');
        select.html('');
        select.append('<option value="'+typeNo+'">'+formatType(typeNo)+'</option>');
        select.attr('disabled','true');
        var picSrc=$("#picSrc").val();
        $("#pic").val(picSrc);
        var imghead=$("#imghead");

        if(typeNo==0||typeNo==1){
            imgClass.style.backgroundImage="url("+fhpt_img+"/phone.png)";
            imgClassChild.style.height="100px";
            imgClassChild.style.margin="102px auto 0";
            imgPic.style.height="100px";
            imghead.css({'width':'430px','height':'180px'});
            $("#greetBtn").removeAttr();
            $("#greetBtn").attr("style","display:none");
        };
        if(typeNo==2){
            imgClass.style.backgroundImage="url("+fhpt_img+"/phone1.png)";
            imgClassChild.style.height="365px";
            imgClassChild.style.margin="57px auto 0";
            imgPic.style.height="365px";
            imghead.css({'width':'113px','height':'200px'});
            $("#greetBtn").removeAttr();
            $("#greetBtn").attr("style","display:block");
        };

    }

    if(typeof(reqBizTypeNo) == 'undefined'||reqBizTypeNo == ''){
        selectReqBizType.removeAttr('disabled');
    }else {
        selectReqBizType.removeAttr('disabled');
        selectReqBizType.html('');
        selectReqBizType.append('<option value="'+reqBizTypeNo+'">'+formatReqBizType(reqBizTypeNo)+'</option>');
        selectReqBizType.attr('disabled','true');
    }
});

function doSave(){
    if($("#type").val()==''||$("#title").val()==''||$("#reqBizType").val()==''){
        return;
    }
    var pic = $("#pic").val();
    if(pic==''){
        layer.msg("请点击上传按钮上传图片！", {time:2000});
        return;
    }
    if($("#starttime").val()==''||$("#endtime").val()==''){
        layer.msg("请填写轮播图的开始时间或者结束时间！", {time:2000});
        return;
    }

    var data = $('#resultForm').serialize();

    if($("#id").val()==""){
        var formUrl =fhpt_ctx+"/njcc/advertisementController/add";
    }else{
        var reqBizType =  $("#reqBizType").val();
        var type = $("#type").val();
        data = 'reqBizType'+'='+reqBizType+ '&'+'type'+'='+type+'&'+data
        var formUrl =fhpt_ctx+"/njcc/advertisementController/edit";
    }

    if($("#type").val()==2&&$("#second").val()==''){
        return;
    }

    $.ajax({
        type: "POST",
        url: formUrl,
        data: data,
        dataType:"json",
        asnyc:false,
        success: function(data){ // 服务器返回的 json 结果
            if (data && !data.errorMsg) {
                window.location.href=fhpt_ctx+'/njcc/advertisementController/manage';
            }else{
                layer.msg(data.errorMsg, {time:2000});
            }
        }
    });
    return;
}

function upload(){
    var file = $("#imgSel").val();
    if(file==""){
        layer.alert('请选择文件！！', {icon: 0});
    }else if(!/\.(gif|jpg|jpeg|png|bmp|GIF|JPG|PNG|BMP)$/.test(file)){
        layer.alert('图片类型必须是.gif,jpeg,jpg,png,bmp中的一种', {icon: 0});
        $("#imgSel").val("");
    }else{
        $.ajaxFileUpload({
            url : fhpt_ctx+"/njcc/fileUpload/image?type=advertisement",   //submit to UploadFileServlet
            secureuri : false,
            fileElementId : 'imgSel',
            dataType : 'json', //or json xml whatever you like~
            success : function(data, status) {
                layer.msg("成功上传图片！", {time:2000});
                $("#pic").val(data.imageGroup.sourceUrl);
                $("#imghead").attr("src",imagePathStr + data.imageGroup.sourceUrl);
                $("#imgPic").attr("src",imagePathStr + data.imageGroup.sourceUrl);
            },
            error : function(data, status, e) {
                alert(data+"       "+status+"            "+e);
            }
        });
    }
}

//图片上传预览
function previewImage(file) {
    var MAXWIDTH  = 430;
    var MAXHEIGHT = 200;
    var div = document.getElementById('preview');
    if (file.files && file.files[0]) {
        div.innerHTML ='<img id="imghead">';
        var img = document.getElementById('imghead');
        img.onload = function(){
            var rect = clacImgZoomParam(MAXWIDTH, MAXHEIGHT, img.offsetWidth, img.offsetHeight);
            img.width  =  rect.width;
            img.height =  rect.height;
            //img.style.marginLeft = rect.left+'px';
            img.style.marginTop = rect.top+'px';
        }
        var reader = new FileReader();
        reader.onload = function(evt){img.src = evt.target.result;	//预览图片地址
            imgPic.src = img.src;
        };
        reader.readAsDataURL(file.files[0]);
    }else {	//兼容IE
        var sFilter='filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale,src="';
        file.select();
        var src = document.selection.createRange().text;
        div.innerHTML = '<img id=imghead>';
        var img = document.getElementById('imghead');
        img.filters.item('DXImageTransform.Microsoft.AlphaImageLoader').src = src;
        var rect = clacImgZoomParam(MAXWIDTH, MAXHEIGHT, img.offsetWidth, img.offsetHeight);
        status =('rect:'+rect.top+','+rect.left+','+rect.width+','+rect.height);
        div.innerHTML = "<div id=divhead style='width:"+rect.width+"px;height:"+rect.height+"px;margin-top:"+rect.top+"px;"+sFilter+src+"\"'></div>";
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

function formatType(value){
    if (value == '0') {
        return '首页轮播图';
    } else if (value == '1') {
        return '卡片轮播图';
    } else if(value=='2'){
        return 'APP欢迎页';
    }
    return value;
}

function formatReqBizType(value){
     if (value == '1') {
        return '智汇APP';
    } else if(value=='2'){
        return '医疗APP';
    }
    return value;
}