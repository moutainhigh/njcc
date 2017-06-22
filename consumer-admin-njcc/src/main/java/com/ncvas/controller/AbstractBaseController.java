package com.ncvas.controller;

import com.alibaba.fastjson.JSONObject;
import com.ncvas.ExcelHelper.ExcelTool;
import com.ncvas.base.entity.Page;
import com.ncvas.base.entity.PageUtil;
import com.ncvas.base.entity.ValueObject;
import com.ncvas.base.exception.YIXUNExceptionCode;
import com.ncvas.base.exception.YIXUNUNCheckedException;
import com.ncvas.base.service.BaseService;
import com.ncvas.controller.vo.AbstractValueObject;
import com.yixun.system.controller.BaseController;
import com.yixuninfo.common.utils.ConfigUtil;
import com.yixuninfo.system.dto.SessionInfoDto;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Calendar;
import java.util.Map;

/**
 * 抽象控制
 * @author twy
 *
 * @param <E>
 * @param <V>
 */
public abstract class AbstractBaseController<E extends ValueObject, V extends AbstractValueObject<E>> extends BaseController {
	
	private static final Logger logger = Logger.getLogger(AbstractBaseController.class);

	protected abstract BaseService<E> getBaseService();
	
	protected abstract String getEntityPagePath();
	
	/**
	 * 图片上传请求路径
	 */
//	@Value("${requestPath}")
	@Value("#{configProperties['requestPath']}")
	protected String  imageRequestPathStr;
	
	@RequestMapping({"/manage"})
	public String index(Model model,HttpServletRequest request){
		model.addAttribute("imageRequestPathStr", imageRequestPathStr);
		SessionInfoDto sessionInfo = (SessionInfoDto) request.getSession().getAttribute(ConfigUtil.getSessionInfoName());
		model.addAttribute("sessionName",sessionInfo.getName());
		return getEntityPagePath() + "/list";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/list")
	public String indexJson(V entity, HttpServletResponse response, int page, int rows) throws IOException {
		response.setContentType("text/html; charset=utf-8");
		Page<E> pageModel = null;
		try {
			// 检查
			String checkErrorStr = this.checkOptBefore(OPT_LIST, entity);
			if (checkErrorStr != null) {
				JSONObject res = new JSONObject();
				res.put("errorMsg", checkErrorStr);
				response.getWriter().append(res.toJSONString());
				return null;
			}
			pageModel = getBaseService().getPageModel((E)entity.toEntityDto(), PageUtil.begin(page,rows), PageUtil.end(page,rows));
			pageModel.setPageNumber(page);
			pageModel.setPageSize(rows);
			pageModel.setTotalPages(pageModel.getTotal()%pageModel.getPageSize()==0?pageModel.getTotal()/pageModel.getPageSize():pageModel.getTotal()/pageModel.getPageSize()+1);
			PageUtil.printJsonToPage(pageModel,response);
		} catch (YIXUNUNCheckedException e) {
			logger.error("分页信息失败", e);
			JSONObject res = new JSONObject();
			res.put("errorMsg", e.getCode() + ":" + e.getOutMsg());
			response.getWriter().append(res.toJSONString());
		} catch (Exception e) {
			logger.error("分页信息失败", e);
			JSONObject res = new JSONObject();
			res.put("errorMsg", YIXUNExceptionCode.UNKNOW_EXCEPTON.getDesout());
			response.getWriter().append(res.toJSONString());
		}
		return null;
	}
	
	@RequestMapping("/add")
	public String create(V entity, HttpServletResponse response) throws IOException {
		response.setContentType("text/html; charset=utf-8");
		JSONObject res = new JSONObject();
		try {
			// 检查
			String checkErrorStr = this.checkOptBefore(OPT_ADD, entity);
			if (checkErrorStr != null) {
				res.put("errorMsg", checkErrorStr);
				response.getWriter().append(res.toJSONString());
				return null;
			}
			E mdto = getBaseService().doCreate(entity.toEntity());
			res.put("count","1");
			//res.put("row",JSONObject.toJSON(mdto));
		} catch (YIXUNUNCheckedException e) {
			logger.error("创建失败", e);
			res.put("errorMsg", e.getCode() + ":" + e.getOutMsg());
		} catch (Exception e) {
			logger.error("创建失败", e);
			res.put("errorMsg", YIXUNExceptionCode.UNKNOW_EXCEPTON.getDesout());
		}
		response.getWriter().append(res.toJSONString());
		return null;
	}
	
	@RequestMapping("/edit")
	public String update(V entity, HttpServletResponse response) throws IOException {
	    response.setContentType("text/html; charset=utf-8");
	    JSONObject res = new JSONObject();
		try {
			// 检查
			String checkErrorStr = this.checkOptBefore(OPT_EDIT, entity);
			if (checkErrorStr != null) {
				res.put("errorMsg", checkErrorStr);
				response.getWriter().append(res.toJSONString());
				return null;
			}
			int updateCount = getBaseService().doUpdate((E)entity.toEntity());
			res.put("count",updateCount);
			//res.put("row",JSONObject.toJSON(entity.toEntityDto()));
		} catch (YIXUNUNCheckedException e) {
			logger.error("编辑失败", e);
			res.put("errorMsg", e.getCode() + ":" + e.getOutMsg());
		} catch (Exception e) {
			logger.error("编辑失败", e);
			res.put("errorMsg",YIXUNExceptionCode.UNKNOW_EXCEPTON.getDesout());
		}
		response.getWriter().append(res.toJSONString());
		return null;
	}
	
	@RequestMapping("/del")
	public String destroy(V entity, HttpServletResponse response) throws IOException {
	    response.setContentType("text/html; charset=utf-8");
	    JSONObject res = new JSONObject();
		try {
			// 检查
			String checkErrorStr = this.checkOptBefore(OPT_DEL, entity);
			if (checkErrorStr != null) {
				res.put("errorMsg", checkErrorStr);
				response.getWriter().append(res.toJSONString());
				return null;
			}
			int destroyCount = getBaseService().doRemove(entity.toEntity().getId());
			res.put("count",destroyCount);
			//res.put("row",JSONObject.toJSON(entity.toEntityDto()));
		} catch (YIXUNUNCheckedException e) {
			logger.error("删除失败", e);
			res.put("errorMsg", e.getCode() + ":" + e.getOutMsg());
		} catch (Exception e) {
			logger.error("删除失败", e);
			res.put("errorMsg",YIXUNExceptionCode.UNKNOW_EXCEPTON.getDesout());
		}
		response.getWriter().append(res.toJSONString());
		return null;
	}
	
	/**列表*/
	protected static final String OPT_LIST = "list";
	/**添加*/
	protected static final String OPT_ADD = "add";
	/**编辑*/
	protected static final String OPT_EDIT = "edit";
	/**删除*/
	protected static final String OPT_DEL = "del";
	
	/**
	 * 执行操作之前参数校验
	 * @param opt
	 * @param entity
	 * @return
	 */
	protected String checkOptBefore(String opt, V entity) {
		return null;
	}
	protected void export(String path,Map map,HttpServletResponse response) throws Exception{
		OutputStream out=null;
		BufferedOutputStream bos=null;
		BufferedInputStream bis=null;
		InputStream in=null;
		try{
			in= ExcelTool.exportExcel(path, map);
			bis=new BufferedInputStream(in);
			response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(Calendar.getInstance().getTimeInMillis()+".xls", "UTF-8"));//设置头文件  可参照 http://blog.csdn.net/fanyuna/article/details/5568089
			byte[] data=new byte[1024];
			int bytes=0;
			out=response.getOutputStream();
			bos=new BufferedOutputStream(out);
			while((bytes=bis.read(data, 0, data.length))!=-1){
				bos.write(data,0,bytes);                                        //写出文件流
			}
			bos.flush();
		}catch(Exception e){
			throw new Exception(e);
		}finally{
			try {
				bos.close();
				out.close();
				bis.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
