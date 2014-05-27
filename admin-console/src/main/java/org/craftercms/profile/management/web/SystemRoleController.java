//package org.craftercms.profile.management.web;
//
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import javax.servlet.http.HttpServletResponse;
//
//import org.craftercms.profile.client.exceptions.ConflictRequestException;
//import org.craftercms.profile.client.impl.domain.Role;
//import org.craftercms.profile.management.services.RoleService;
//import org.craftercms.security.api.RequestContext;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.Errors;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.ModelAndView;
//
//@Controller
//public class SystemRoleController {
//
//    @Autowired
//    private RoleService roleService;
//
//    @RequestMapping(value = "/rolelist", method = RequestMethod.GET)
//    public ModelAndView findRoles() throws Exception {
//        ModelAndView mav = new ModelAndView();
//
//        mav.setViewName("rolelist");
//        RequestContext context = RequestContext.getCurrent();
//        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
//        mav.addObject("roleList", roleService.getAllRoles());
//        //        mav.addObject("tenant",tenant);
//        return mav;
//    }
//
//    @RequestMapping(value = "/newrole", method = RequestMethod.GET)
//    public ModelAndView newRole() throws Exception {
//        ModelAndView mav = new ModelAndView();
//        mav.setViewName("newrole");
//
//        RequestContext context = RequestContext.getCurrent();
//        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
//        mav.addObject("role", new Role());
//
//        return mav;
//    }
//
//    @RequestMapping(value = "/new_role", method = RequestMethod.POST)
//    public String mapRole(@ModelAttribute("role") Role role, BindingResult bindingResult,
//                          Model model) throws Exception {
//        validateNewRole(role, bindingResult);
//        if (!bindingResult.hasErrors()) {
//            try {
//                roleService.createRole(role);
//                RequestContext context = RequestContext.getCurrent();
//
//                model.addAttribute("currentuser", context.getAuthenticationToken().getProfile());
//                model.addAttribute("roleList", roleService.getAllRoles());
//                return "rolelist";
//            } catch (ConflictRequestException e) {
//                bindingResult.rejectValue("roleName", "role.name.validation.already.exist", null,
//                    "role.name.validation.already.exist");
//                RequestContext context = RequestContext.getCurrent();
//                model.addAttribute("currentuser", context.getAuthenticationToken().getProfile());
//                model.addAttribute("role", role);
//
//                return "newrole";
//            }
//        } else {
//
//            RequestContext context = RequestContext.getCurrent();
//            model.addAttribute("currentuser", context.getAuthenticationToken().getProfile());
//            model.addAttribute("role", role);
//
//            return "newrole";
//        }
//    }
//
//    @RequestMapping(value = "/role_detail", method = RequestMethod.GET)
//    public ModelAndView getItem(@RequestParam(required = false) String id, @RequestParam(required = false) String
//        roleName) throws Exception {
//        ModelAndView mav = new ModelAndView();
//
//        mav.setViewName("updaterole");
//        Role role = new Role();
//        role.setRoleName(roleName);
//        List<String> tenantNames = roleService.getTenantsByRoleName(roleName);
//        mav.addObject("tenantNames", tenantNames);
//        RequestContext context = RequestContext.getCurrent();
//
//        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
//        mav.addObject("enableDelete", tenantNames.size() == 0);
//        mav.addObject("role", role);
//
//        return mav;
//    }
//
//    @RequestMapping(value = "/delete_role", method = RequestMethod.POST)
//    @ModelAttribute
//    public ModelAndView deleteAccount(@ModelAttribute("role") Role role, BindingResult bindingResult,
//                                      HttpServletResponse response) throws Exception {
//
//        try {
//            roleService.deleteRole(role.getRoleName());
//        } catch (ConflictRequestException e) {
//            bindingResult.reject("group.name.validation.error.used.bytenant", new String[] {role.getRoleName()},
//                "Some roles were not deleted because are used by Tenants");
//        }
//
//        ModelAndView mav = new ModelAndView();
//        mav.setViewName("rolelist");
//
//        RequestContext context = RequestContext.getCurrent();
//
//        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
//
//        mav.addObject("roleList", roleService.getAllRoles());
//
//
//        return mav;
//    }
//
//    private void validateNewRole(Role role, Errors errors) {
//        Pattern pattern = Pattern.compile("[,\\s]|@.*@");
//        Matcher m = pattern.matcher(role.getRoleName());
//
//        if (role.getRoleName() == null || role.getRoleName().isEmpty() ||
//            m.find()) {
//            errors.rejectValue("roleName", "role.name.validation.error.empty.or.whitespace", null, "role.name.validation.error.empty.or.whitespace");
//        }
//
//    }
//
//
//}
