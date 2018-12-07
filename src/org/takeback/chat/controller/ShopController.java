// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.controller;

import org.takeback.util.exception.CodedBaseRuntimeException;
import org.takeback.util.annotation.AuthPassport;
import org.takeback.chat.entity.PubExchangeLog;
import org.springframework.web.util.WebUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.takeback.chat.entity.PubShop;
import java.util.List;
import org.takeback.mvc.ResponseUtils;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.ShopService;
import org.springframework.stereotype.Controller;

@Controller
public class ShopController
{
    @Autowired
    private ShopService shopService;
    
    @RequestMapping(value = { "/shop/list" }, method = { RequestMethod.POST })
    public ModelAndView shopList(@RequestBody final Map<String, Object> param, final HttpServletRequest request) {
        int pageNo = 1;
        int pageSize = 20;
        if (param.containsKey("pageNo")) {
            pageNo = Integer.valueOf(param.get("pageNo").toString());
        }
        if (param.containsKey("pageSize")) {
            pageSize = Integer.valueOf(param.get("pageSize").toString());
        }
        final List<PubShop> list = this.shopService.list(pageNo, pageSize);
        if (list == null || list.isEmpty()) {
            return ResponseUtils.jsonView(null);
        }
        return ResponseUtils.jsonView(list);
    }
    
    @RequestMapping({ "/shop/get" })
    public ModelAndView get(@RequestParam final int id, final HttpServletRequest request) {
        final PubShop shop = this.shopService.get(id);
        return ResponseUtils.jsonView(shop);
    }
    
    @AuthPassport
    @RequestMapping({ "/shop/getContactInfo" })
    public ModelAndView getContactInfo(final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        final PubExchangeLog log = this.shopService.getContactInfo(uid);
        return ResponseUtils.jsonView(log);
    }
    
    @AuthPassport
    @RequestMapping(value = { "/shop/doExchange" }, method = { RequestMethod.POST })
    public ModelAndView doExchange(@RequestBody final Map<String, Object> param, final HttpServletRequest request) {
        try {
            final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
            final Integer shopId = Integer.valueOf(param.get("shopId").toString());
            final String name = param.get("name").toString();
            final String mobile = param.get("mobile").toString();
            final String address = param.get("address").toString();
            this.shopService.doExchage(uid, shopId, name, address, mobile);
        }
        catch (CodedBaseRuntimeException e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(500, e.getMessage());
        }
        return ResponseUtils.jsonView(200, "兑换成功");
    }
}
