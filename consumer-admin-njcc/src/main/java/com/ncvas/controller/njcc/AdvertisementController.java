package com.ncvas.controller.njcc;

import com.ncvas.base.service.BaseService;
import com.ncvas.common.entity.Advertisement;
import com.ncvas.common.service.AdvertisementService;
import com.ncvas.common.utils.StringUtil;
import com.ncvas.controller.AbstractBaseController;
import com.ncvas.controller.vo.njcc.AdvertisementVo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author lc_xin.
 * @date 2016年6月16日
 */
@Controller
@RequestMapping("/njcc/advertisementController")
public class AdvertisementController extends AbstractBaseController<Advertisement, AdvertisementVo> {
	
	private final static Logger logger = Logger.getLogger(AdvertisementController.class);
	
	@Autowired
	private AdvertisementService advertisementService;
	
	@Override
	protected BaseService<Advertisement> getBaseService() {
		return advertisementService;
	}

	@Override
	protected String getEntityPagePath() {
		return "/njcc/advertisement";
	}

	@RequestMapping(value = "/modify" , method = {RequestMethod.GET,RequestMethod.POST})
	public String verify(Model model,String id){
		model.addAttribute("imageRequestPathStr", imageRequestPathStr);
		if(!StringUtil.isEmpty(id)){
			Advertisement advertisement = advertisementService.doView(id);
			model.addAttribute("advertisement",advertisement);
			if(advertisement!=null){
				model.addAttribute("pic",imageRequestPathStr+advertisement.getPic());
			}
		}
		return "/njcc/advertisement/modify";
	}
}
