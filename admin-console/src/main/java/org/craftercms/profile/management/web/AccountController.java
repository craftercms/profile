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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.craftercms.profile.impl.domain.Tenant;
import org.craftercms.profile.management.services.TenantDAOService;
import org.craftercms.profile.management.util.TenantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import org.springframework.web.servlet.ModelAndView;

import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.RestException;
import org.craftercms.profile.management.model.FilterForm;
import org.craftercms.profile.management.model.ProfileUserAccountForm;

import org.craftercms.profile.management.services.ProfileAccountService;
import org.craftercms.profile.management.services.ProfileDAOServiceImpl;
import org.craftercms.profile.management.util.ProfileAccountPaging;
import org.craftercms.profile.management.util.ProfileUserAccountValidator;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.UserProfile;

@Controller
@SessionAttributes({"account"})
public class AccountController {

    private ProfileDAOServiceImpl profileDao;

    private TenantDAOService tenantDAOService;
    
    private ProfileAccountService profileAccountService;
    
    private ProfileAccountPaging profileAccountPaging;

    private ProfileUserAccountValidator profileUserAccountValidator;
    
    @RequestMapping(value = "/init-get", method = RequestMethod.GET)
    public String getAccounts(@RequestParam(required=false) String selectedTenantName) throws Exception {
        tenantDAOService.restartAppToken();
        profileDao.restartAppToken();
        return "redirect:/get";
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ModelAndView findAllAccounts(@RequestParam(required=false) String selectedTenantName) throws Exception {
        ModelAndView mav = new ModelAndView();

        List<Tenant> tenantList = tenantDAOService.getAllTenants();
        if(tenantList==null) {
            throw new AppAuthenticationFailedException();
        }
        Map<String, String> tenantNames = TenantUtil.getTenantsMap(tenantList);
        if(selectedTenantName == null || selectedTenantName.isEmpty()){
            selectedTenantName = tenantList.get(0).getTenantName();
        }
        List<ProfileUserAccountForm> list = profileAccountService.getProfileUsers(selectedTenantName);
        FilterForm filter = new FilterForm();
        mav.setViewName("accountlist");
        mav.addObject("userList", list);
        mav.addObject("filter",filter);
        mav.addObject("tenantNames",tenantNames);
        mav.addObject("selectedTenantName",selectedTenantName);
        mav.addObject("profileAccountPaging", profileAccountPaging);

        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
    }

    @RequestMapping(value = "/next", method = RequestMethod.GET)
    public ModelAndView findNextPage(@RequestParam String selectedTenantName) throws Exception {
        ModelAndView mav = new ModelAndView();
        FilterForm filter = new FilterForm();
        List<Tenant> tenantList = tenantDAOService.getAllTenants();
        if(tenantList==null) {
            throw new AppAuthenticationFailedException();
        }
        Map<String, String> tenantNames = TenantUtil.getTenantsMap(tenantList);
        List<ProfileUserAccountForm> list = profileAccountService.getNextUserPage(selectedTenantName);

        mav.setViewName("accountlist");
        mav.addObject("userList", list);
        mav.addObject("profileAccountPaging", profileAccountPaging);
        mav.addObject("filter",filter);
        mav.addObject("tenantNames",tenantNames);
        mav.addObject("selectedTenantName",selectedTenantName);
        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
    }
    
    @RequestMapping(value = "/prev", method = RequestMethod.GET)
    public ModelAndView findPrevPage(@RequestParam String selectedTenantName) throws Exception {
        ModelAndView mav = new ModelAndView();
        FilterForm filter = new FilterForm();
        List<Tenant> tenantList = tenantDAOService.getAllTenants();
        if(tenantList==null) {
            throw new AppAuthenticationFailedException();
        }
        Map<String, String> tenantNames = TenantUtil.getTenantsMap(tenantList);
        List<ProfileUserAccountForm> list = profileAccountService.getPrevUserPage(selectedTenantName);

        mav.setViewName("accountlist");
        mav.addObject("userList", list);
        mav.addObject("filter",filter);
        mav.addObject("tenantNames",tenantNames);
        mav.addObject("selectedTenantName",selectedTenantName);
        mav.addObject("profileAccountPaging", profileAccountPaging);
        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
    }
    
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView searchProfiles(@ModelAttribute("filter") FilterForm filter,
                                       @RequestParam String selectedTenantName) throws Exception {
        ModelAndView mav = new ModelAndView();
        List<Tenant> tenantList = tenantDAOService.getAllTenants();
        if(tenantList==null) {
            throw new AppAuthenticationFailedException();
        }
        Map<String, String> tenantNames = TenantUtil.getTenantsMap(tenantList);
        List<ProfileUserAccountForm> list = profileAccountService.getSearchProfileUsers(filter, selectedTenantName);
        
        mav.setViewName("accountlist");
        mav.addObject("userList", list);
        mav.addObject("filter",filter);
        mav.addObject("tenantNames",tenantNames);
        mav.addObject("selectedTenantName",selectedTenantName);
        mav.addObject("profileAccountPaging", profileAccountPaging);
      
        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
    }
    
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public ModelAndView newForm(@RequestParam(required=false) String selectedTenantName) throws Exception {
        List<Tenant> tenantList = tenantDAOService.getAllTenants();
        if(tenantList==null) {
            throw new AppAuthenticationFailedException();
        }
        Tenant t = getSelectedTenantObject(tenantList, selectedTenantName);
        ProfileUserAccountForm account = profileAccountService.createNewProfileUserAccountForm(t);
        
        Map<String, String> tenantNames = TenantUtil.getTenantsMap(tenantList);
        
        ModelAndView mav = new ModelAndView();
        mav.setViewName("new");
        mav.addObject("account", account);
        mav.addObject("attributeList",t.getSchema().getAttributes());
        mav.addObject("tenantNames", tenantNames);
        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
    }
    
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String newAccount(@ModelAttribute("account") ProfileUserAccountForm account,
            BindingResult bindingResult, Model model) throws Exception {
        validateNewAccount(account, bindingResult, account.getTenantName());
        if (!bindingResult.hasErrors()) {
            profileAccountService.createUserAccount(account);
            model.addAttribute("selectedTenantName",account.getTenantName());
            return "redirect:/get";
        } else {
            List<Tenant> tenantList = tenantDAOService.getAllTenants();
            Map<String, String> tenantNames = TenantUtil.getTenantsMap(tenantList);
            Tenant tenant = tenantDAOService.getTenantByName(account.getTenantName());
            model.addAttribute("account", account);
            model.addAttribute("attributeList", tenant.getSchema().getAttributes());
            model.addAttribute("tenantNames", tenantNames);
            RequestContext context = RequestContext.getCurrent();
            model.addAttribute("currentuser", context.getAuthenticationToken().getProfile());
            return "new";
        }
    }

    @RequestMapping(value = "/tenant_attributes_and_roles", method = RequestMethod.GET)
    public ModelAndView getTenantByTenantName(@ModelAttribute("account") ProfileUserAccountForm account)  throws Exception {
        Tenant result = tenantDAOService.getTenantByName(account.getTenantName());
        account.initTenantValues(result);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("profileattributes");
        mav.addObject("account", account);
        mav.addObject("attributeList", result.getSchema().getAttributes());
        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());

        return mav;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateAccount(@ModelAttribute("account") ProfileUserAccountForm account,
            BindingResult bindingResult, Model model) throws Exception {
    
        profileAccountService.updateUserAccount(account);
        model.addAttribute("selectedTenantName",account.getTenantName());
        return "redirect:/get";
    }
    
    @RequestMapping(value = "/item", method = RequestMethod.GET)
    public ModelAndView findAccount(@RequestParam(required=false) String username,
                                    @RequestParam(required=false) String tenantName) throws Exception {
        ProfileUserAccountForm a = profileAccountService.getUserForUpdate(username, tenantName);
        Tenant tenant = tenantDAOService.getTenantByName(a.getTenantName());
        ModelAndView mav = new ModelAndView();
        mav.setViewName("update");
        mav.addObject("account", a);
        mav.addObject("tenantName", tenant.getTenantName());
        mav.addObject("attributeList", tenant.getSchema().getAttributes());
        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
    }
    
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ModelAttribute
    public ModelAndView deleteAccount(@RequestParam("item") ArrayList<String> item,
                                      @RequestParam String selectedTenantName,
            HttpServletResponse response) throws Exception {
        profileAccountService.deleteUsers(item);
        ModelAndView mav = new ModelAndView();

        List<Tenant> tenantList = tenantDAOService.getAllTenants();
        Map<String, String> tenantNames = TenantUtil.getTenantsMap(tenantList);
        List<ProfileUserAccountForm> list = profileAccountService.getProfileUsers(selectedTenantName);
        mav.setViewName("accountlist");
        mav.addObject("userList", list);
        mav.addObject("filter", new FilterForm());
        mav.addObject("tenantNames", tenantNames);
        mav.addObject("selectedTenantName",selectedTenantName);
        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
    }
    
    @ExceptionHandler(AppAuthenticationFailedException.class)
    public String logoutException() {
        return "redirect:/login?logout=true";
    }
    
    @ExceptionHandler(RestException.class)
    public String Exception() {
        return "redirect:/get";
    }
    
    @ExceptionHandler(Throwable.class)
    public String handleException(Throwable t) {
        return "redirect:/error.jsp";
    }
    
    @RequestMapping("/login")
    public String login(Model model, @RequestParam(required=false) String message) {
        return "login";
    }
    
    @Autowired
    public void setProfileDAOService(ProfileDAOServiceImpl profileDAO) {
        this.profileDao = profileDAO;
    }

    @Autowired
    public void setTenantDAOService(TenantDAOService tenantDAOService) {
        this.tenantDAOService = tenantDAOService;
    }
    
    @Autowired
    public void setProfileAccountService(ProfileAccountService profileAccountService) {
        this.profileAccountService = profileAccountService;
    }
    
    @Autowired
    public void setProfileUserAccountValidator(ProfileUserAccountValidator validator) {
        this.profileUserAccountValidator = validator;
    }
    
    @Autowired
    public void setProfileAccountPaging(ProfileAccountPaging paging) {
        this.profileAccountPaging = paging;
    }
    
    private void validateNewAccount(ProfileUserAccountForm account, BindingResult bindingResult, String selectedTenantName) {
        Pattern pattern = Pattern.compile("[,\\s]|@.*@");
        Matcher m = pattern.matcher(account.getUsername());
        profileUserAccountValidator.validate(account, bindingResult);

        if (!account.getPassword().equals(account.getConfirmPassword())) {

            bindingResult.rejectValue("password", "user.validation.fields.errors.confirm.password", null, "user.validation.fields.errors.confirm.password");

        }
        if (account.getRoles() == null || account.getRoles().size() == 0) {
            bindingResult.rejectValue("roles", "user.validation.fields.errors.roles", null, "user.validation.fields.errors.roles");
        }
        if (m.find()){
            bindingResult.rejectValue("username", "user.validation.error.empty.or.whitespace", null, "user.validation.error.empty.or.whitespace");
        }
    }
    
    private Tenant getSelectedTenantObject(List<Tenant> tenantList,
            String selectedTenantName) {
        if (tenantList == null) {
            return null;
        }
        for (Tenant t:tenantList) {
            if (t.getTenantName().equalsIgnoreCase(selectedTenantName)) {
                return t;
            }
        }
        return null;
    }
}
