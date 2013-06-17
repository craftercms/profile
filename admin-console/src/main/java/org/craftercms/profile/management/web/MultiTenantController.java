/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.profile.management.web;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.craftercms.profile.impl.domain.Role;
import org.craftercms.profile.impl.domain.Tenant;
import org.craftercms.profile.management.model.TenantFilterForm;
import org.craftercms.profile.management.services.RoleDAOService;
import org.craftercms.profile.management.services.TenantDAOService;
import org.craftercms.profile.management.util.TenantPaging;
import org.craftercms.profile.management.util.TenantValidator;
import org.craftercms.security.api.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes({"tenant"})
public class MultiTenantController {

    private RoleDAOService roleDAOService;

    private TenantDAOService tenantDAOService;

    private TenantValidator tenantValidator;

    private TenantPaging tenantPaging;

    @RequestMapping(value = "/gettenants", method = RequestMethod.GET)
    public ModelAndView findAllAccounts() throws Exception {
        ModelAndView mav = new ModelAndView();

        List<Tenant> tenantList = tenantDAOService.getTenantPage();
        TenantFilterForm filter = new TenantFilterForm();
        mav.setViewName("tenantlist");
        mav.addObject("tenantList", tenantList);
        mav.addObject("filter",filter);
        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
    }

    @RequestMapping(value = "/nexttenants", method = RequestMethod.GET)
    public ModelAndView findNextPage() throws Exception {
        ModelAndView mav = new ModelAndView();
        TenantFilterForm filter = new TenantFilterForm();
        List<Tenant> tenantList = tenantDAOService.getNextTenantPage();
        mav.addObject("filter",filter);
        mav.setViewName("tenantlist");
        mav.addObject("tenantList", tenantList);
        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
    }

    @RequestMapping(value = "/prevtenants", method = RequestMethod.GET)
    public ModelAndView findPrevPage() throws Exception {
        ModelAndView mav = new ModelAndView();
        TenantFilterForm filter = new TenantFilterForm();
        List<Tenant> tenantList = tenantDAOService.getPrevTenantPage();
        mav.addObject("filter",filter);
        mav.setViewName("tenantlist");
        mav.addObject("tenantList", tenantList);
        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
    }

    @RequestMapping(value = "/searchtenants", method = RequestMethod.GET)
    public ModelAndView searchProfiles(@ModelAttribute("filter") TenantFilterForm filter) throws Exception {
        ModelAndView mav = new ModelAndView();
        List<Tenant> tenantList = tenantDAOService.getSearchTenants(filter);

        mav.setViewName("tenantlist");
        mav.addObject("tenantList", tenantList);
        mav.addObject("filter",filter);

        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
    }

    @RequestMapping(value = "/newtenant", method = RequestMethod.GET)
    public ModelAndView newForm() throws Exception {
        Tenant tenant = tenantDAOService.createEmptyTenant();
        List<Role> roleOption = roleDAOService.getAllRoles();
        List<Tenant> tenantList = tenantDAOService.getAllTenants();

        ModelAndView mav = new ModelAndView();
        mav.setViewName("newtenant");
        mav.addObject("tenant", tenant);
        mav.addObject("roleOption",roleOption);
        mav.addObject("tenantList",tenantList);
        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
    }

    @RequestMapping(value = "/newtenant", method = RequestMethod.POST)
    public String newAccount(@ModelAttribute("tenant") Tenant tenant,
                             BindingResult bindingResult, Model model) throws Exception {
        validateNewTenant(tenant, bindingResult);
        if (!bindingResult.hasErrors()) {
            tenantDAOService.createNewTenant(tenant);
            return "redirect:/gettenants";
        } else {
            List<Role> roleOption = roleDAOService.getAllRoles();
            List<Tenant> tenantList = tenantDAOService.getAllTenants();

            model.addAttribute("tenant", tenant);
            model.addAttribute("roleOption", roleOption);
            model.addAttribute("tenantList", tenantList);
            RequestContext context = RequestContext.getCurrent();
            model.addAttribute("currentuser", context.getAuthenticationToken().getProfile());
            return "newtenant";
        }
    }

    @RequestMapping(value = "/tenant", method = RequestMethod.GET)
    public ModelAndView findAccount(@RequestParam(required=false) String tenantName) throws Exception {
        Tenant tenant = tenantDAOService.getTenantForUpdate(tenantName);
        List<Role> roleOption = roleDAOService.getAllRoles();
        List<Tenant> tenantList = tenantDAOService.getAllTenants();

        ModelAndView mav = new ModelAndView();
        mav.setViewName("updatetenant");
        mav.addObject("tenant", tenant);
        mav.addObject("roleOption",roleOption);
        mav.addObject("tenantList",tenantList);
        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
    }

    @RequestMapping(value = "/updatetenant", method = RequestMethod.POST)
    public String updateAccount(@ModelAttribute("tenant") Tenant tenant,
                                BindingResult bindingResult, Model model) throws Exception {
        validateUpdateTenant(tenant, bindingResult);
        if (!bindingResult.hasErrors()) {
            Tenant result = tenantDAOService.updateTenant(tenant);
            return "redirect:/gettenants";
        } else {
            List<Role> roleOption = roleDAOService.getAllRoles();
            List<Tenant> tenantList = tenantDAOService.getAllTenants();

            model.addAttribute("tenant", tenant);
            model.addAttribute("roleOption", roleOption);
            model.addAttribute("tenantList", tenantList);
            RequestContext context = RequestContext.getCurrent();
            model.addAttribute("currentuser", context.getAuthenticationToken().getProfile());
            return "newtenant";
        }
    }

    private void validateNewTenant(Tenant t, BindingResult result){
        Pattern pattern = Pattern.compile("^((http(s?):\\/\\/)?(((www\\.)?+" +
                "[a-zA-Z0-9\\.\\-\\_]+(\\.[a-zA-Z]{2,3})+)|(\\b(?:(?:25[0-5]|2[0-4][0-9]|" +
                "[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b))" +
                "(\\/[a-zA-Z0-9\\_\\-\\s\\.\\/\\?\\%\\#\\&\\=]*)?)|localhost$");
        tenantValidator.validate(t, result);
//        try {
//            if (tenantDAOService.getTenantByName(t.getTenantName()) != null) {
//                result.rejectValue("tenantName", "tenant.name.validation.tenant.already.exist", null, "tenant.name.validation.tenant.already.exist");
//            }
//        }catch (Exception e){
//
//        }
        if(t.getRoles().isEmpty()){
            result.rejectValue("roles", "tenant.roles.validation.error.empty", null, "tenant.roles.validation.error.empty");
        }
        if(t.getDomains().isEmpty()){
            result.rejectValue("domains", "tenant.domains.validation.error.empty", null, "tenant.domains.validation.error.empty");
        }else{
            for(String domain : t.getDomains()){
                Matcher m = pattern.matcher(domain);
                if(!m.find()){
                    result.rejectValue("domains", "tenant.domains.validation.error.invalid.format", null, "tenant.domains.validation.error.invalid.format");
                    break;
                }
            }
        }
        pattern = Pattern.compile("[,\\s]|@.*@");
        Matcher m = pattern.matcher(t.getTenantName());
        if (m.find()) {
            result.rejectValue("tenantName", "tenant.name.validation.error.empty.or.whitespace", null, "tenant.name.validation.error.empty.or.whitespace");
        }

    }

    private void validateUpdateTenant(Tenant t, BindingResult result){
        Pattern pattern = Pattern.compile("^((http(s?):\\/\\/)?(((www\\.)?+" +
                "[a-zA-Z0-9\\.\\-\\_]+(\\.[a-zA-Z]{2,3})+)|(\\b(?:(?:25[0-5]|2[0-4][0-9]|" +
                "[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b))" +
                "(\\/[a-zA-Z0-9\\_\\-\\s\\.\\/\\?\\%\\#\\&\\=]*)?)|localhost$");
        tenantValidator.validate(t, result);

        if(t.getRoles().isEmpty()){
            result.rejectValue("roles", "tenant.roles.validation.error.empty", null, "tenant.roles.validation.error.empty");
        }
        if(t.getDomains().isEmpty()){
            result.rejectValue("domains", "tenant.domains.validation.error.empty", null, "tenant.domains.validation.error.empty");
        }else{
            for(String domain : t.getDomains()){
                Matcher m = pattern.matcher(domain);
                if(!m.find()){
                    result.rejectValue("domains", "tenant.domains.validation.error.invalid.format", null, "tenant.domains.validation.error.invalid.format");
                    break;
                }
            }
        }
        pattern = Pattern.compile("[,\\s]|@.*@");
        Matcher m = pattern.matcher(t.getTenantName());
        if (m.find()) {
            result.rejectValue("tenantName", "tenant.name.validation.error.empty.or.whitespace", null, "tenant.name.validation.error.empty.or.whitespace");
        }

    }

    @Autowired
    public void setRoleDAOService(RoleDAOService roleDAOService) {
        this.roleDAOService = roleDAOService;
    }

    @Autowired
    public void setTenantDAOService(TenantDAOService tenantDAOService) {
        this.tenantDAOService = tenantDAOService;
    }

    @Autowired
    public void setTenantValidator(TenantValidator tenantValidator) {
        this.tenantValidator = tenantValidator;
    }

    @Autowired
    public void setTenantPaging(TenantPaging tenantPaging) {
        this.tenantPaging = tenantPaging;
    }

}
