package org.craftercms.profile.management.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.craftercms.profile.exceptions.ConflictRequestException;
import org.craftercms.profile.impl.domain.GroupRole;
import org.craftercms.profile.impl.domain.Role;
import org.craftercms.profile.impl.domain.Tenant;
import org.craftercms.profile.management.services.GroupRoleMappingService;
import org.craftercms.profile.management.services.TenantDAOService;
import org.craftercms.profile.management.util.GroupRoleValidator;
import org.craftercms.security.api.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes({"tenant"})
public class GroupRoleMappingController {
	
	@Autowired
	private GroupRoleMappingService groupRoleMappingService;
	
	@Autowired
	private TenantDAOService tenantDAOService;

	private GroupRoleValidator groupRoleValidator;
	
	@RequestMapping(value = "/grouplist", method = RequestMethod.GET)
    public ModelAndView findGroups(@ModelAttribute("tenant") Tenant tenant) throws Exception {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("grouplist");
        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        mav.addObject("groupList", groupRoleMappingService.getGroupRoleMapping(tenant.getTenantName()));
        mav.addObject("tenant",tenant);
        return mav;
    }
	
	@RequestMapping(value = "/newgroup", method = RequestMethod.GET)
    public ModelAndView newGroup(@ModelAttribute("tenant") Tenant tenant) throws Exception {
		ModelAndView mav = new ModelAndView();
        mav.setViewName("newgroup");
        tenant = tenantDAOService.getTenantByName(tenant.getTenantName());
        List<String> roleOption = tenant.getRoles();
        RequestContext context = RequestContext.getCurrent();
        mav.addObject("roleOption",roleOption);
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        mav.addObject("group", new GroupRole());
        mav.addObject("tenant",tenant);
        return mav;
    }
	
	@RequestMapping(value = "/new_group_mapping", method = RequestMethod.POST)
    public String mapGroupRole(
    		@ModelAttribute("tenant") Tenant tenant,
    		@ModelAttribute("group") GroupRole groupRole,
    		BindingResult bindingResult,
    		 Model model) throws Exception {
		tenant = tenantDAOService.getTenantByName(tenant.getTenantName());
        groupRole.setTenantName(tenant.getTenantName());
        validateNewGroupRoleMapping(groupRole, bindingResult);
        if (!bindingResult.hasErrors()) {
        	try{
        		groupRoleMappingService.createGroupRoleMapping(groupRole);
        		
	        	RequestContext context = RequestContext.getCurrent();
	             
	             model.addAttribute("currentuser", context.getAuthenticationToken().getProfile());
	             model.addAttribute("groupList", groupRoleMappingService.getGroupRoleMapping(tenant.getTenantName()));
	             model.addAttribute("tenant",tenant);
	             return "grouplist";
        	} catch(ConflictRequestException e) {

        		bindingResult.rejectValue("name", "grouprole.mapping.roles.fields.errors.groupname.already.exist", null, "grouprole.mapping.roles.fields.errors.groupname.already.exist");
        		List<String> roleOption = tenant.getRoles();
                RequestContext context = RequestContext.getCurrent();
                model.addAttribute("roleOption",roleOption);
                model.addAttribute("currentuser", context.getAuthenticationToken().getProfile());
                model.addAttribute("group", groupRole);
                model.addAttribute("tenant",tenant);
                return "newgroup";
	       }
        } else {
        	
        	List<String> roleOption = tenant.getRoles();
            RequestContext context = RequestContext.getCurrent();
            model.addAttribute("roleOption",roleOption);
            model.addAttribute("currentuser", context.getAuthenticationToken().getProfile());
            model.addAttribute("group", groupRole);
            model.addAttribute("tenant",tenant);
            return "newgroup";
        }
    }
	
	@RequestMapping(value = "/group_update", method = RequestMethod.GET)
    public ModelAndView getItem(@RequestParam(required=false) String id, @RequestParam(required=false) String tenantName, @ModelAttribute("tenant") Tenant tenant) throws Exception {
		ModelAndView mav = new ModelAndView();
		tenant = tenantDAOService.getTenantByName(tenant.getTenantName());
        mav.setViewName("updategroup");
        GroupRole gr = groupRoleMappingService.getGroupRoleItem(id);
        List<String> roleOption = tenant.getRoles();
        mav.addObject("roleOption",roleOption);
        RequestContext context = RequestContext.getCurrent();
        
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        mav.addObject("group", gr);
        mav.addObject("tenant",tenant);
        return mav;
    }
	
	@RequestMapping(value = "/update_group_mapping", method = RequestMethod.POST)
    public String updateGroups(
    		@ModelAttribute("tenant") Tenant tenant,
    		@ModelAttribute("group") GroupRole groupRole,
    		BindingResult bindingResult,
    		 Model model) throws Exception {
		tenant = tenantDAOService.getTenantByName(tenant.getTenantName());
        groupRole.setTenantName(tenant.getTenantName());
		validateUpdateGroupRoleMapping(groupRole, bindingResult);
        if (!bindingResult.hasErrors()) {
        	groupRoleMappingService.updateGroupRoleMapping(groupRole);
        	RequestContext context = RequestContext.getCurrent();
            
            model.addAttribute("currentuser", context.getAuthenticationToken().getProfile());
            model.addAttribute("groupList", groupRoleMappingService.getGroupRoleMapping(tenant.getTenantName()));
            model.addAttribute("tenant",tenant);
            return "grouplist";
        } else {
        	//mav.setViewName("newgroup");
        	List<String> roleOption = tenant.getRoles();
            RequestContext context = RequestContext.getCurrent();
            model.addAttribute("roleOption",roleOption);
            model.addAttribute("currentuser", context.getAuthenticationToken().getProfile());
            model.addAttribute("group", groupRole);
            model.addAttribute("tenant",tenant);
            return "updategroup";
        }
    }
	
	@RequestMapping(value = "/delete_group_mapping", method = RequestMethod.POST)
    @ModelAttribute
    public ModelAndView deleteAccount(@RequestParam("item") ArrayList<String> item,
    		@ModelAttribute("tenant") Tenant tenant,
            HttpServletResponse response) throws Exception {
        groupRoleMappingService.deleteGroupRoleMapping(item);
      tenant = tenantDAOService.getTenantByName(tenant.getTenantName());
        ModelAndView mav = new ModelAndView();
        mav.setViewName("grouplist");
        
        RequestContext context = RequestContext.getCurrent();
        
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        //mav.addObject("group", new GroupRole());
        mav.addObject("groupList", groupRoleMappingService.getGroupRoleMapping(tenant.getTenantName()));
        mav.addObject("tenant", tenant);
        
        return mav;
    }
	
	@Autowired
    public void setGroupRoleValidator(GroupRoleValidator validator) {
        this.groupRoleValidator = validator;
    }
	
	private void validateNewGroupRoleMapping(GroupRole groupRole, Errors errors) {
        if (groupRole.getName()==null || groupRole.getName().isEmpty()) {
        	errors.rejectValue("name", "grouprole.mapping.name.validation.error.empty",null,null);
        } else if (groupRole.getRoles()==null || groupRole.getRoles().isEmpty()) {
        	errors.rejectValue("roles", "grouprole.mapping.roles.validation.error.empty",null,null);
        }
        
    }
	
	private void validateUpdateGroupRoleMapping(GroupRole groupRole, Errors errors) {
        if (groupRole.getRoles()==null || groupRole.getRoles().isEmpty()) {
        	errors.rejectValue("roles", "grouprole.mapping.roles.validation.error.empty",null,null);
        }
        
    }

}
