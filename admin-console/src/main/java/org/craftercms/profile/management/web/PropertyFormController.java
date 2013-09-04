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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.craftercms.profile.impl.domain.Attribute;
import org.craftercms.profile.impl.domain.Schema;
import org.craftercms.profile.impl.domain.Tenant;
import org.craftercms.profile.management.services.TenantDAOService;
import org.craftercms.profile.management.util.ProfilePropertyFormValidator;
import org.craftercms.profile.management.util.ProfileUserAccountUtil;
import org.craftercms.profile.management.util.TenantUtil;
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
@SessionAttributes("tenant")
public class PropertyFormController {

    private TenantDAOService tenantDAOService;

    private ProfilePropertyFormValidator profilePropertyFormValidator;

    @RequestMapping(value = "/getprops", method = RequestMethod.GET)
    public ModelAndView findProps(@ModelAttribute("tenant") Tenant tenant) throws Exception {
        ModelAndView mav = new ModelAndView();

        Schema baseUser = tenant.getSchema();
        mav.setViewName("proplist");
        mav.addObject("baseUser", baseUser);
        mav.addObject("propList", baseUser.getAttributes());
        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
    }

    @RequestMapping(value = "/newprop", method = RequestMethod.GET)
    public ModelAndView newProp(@ModelAttribute("tenant") Tenant tenant) throws Exception {
        Attribute property = tenantDAOService.createNewAttribute(tenant.getSchema());
        ModelAndView mav = new ModelAndView();
        Map<String, String> supportedTypes = ProfileUserAccountUtil.getAttributesSupportedTypes();
        mav.setViewName("newprop");
        mav.addObject("attributeTypes", supportedTypes);
        mav.addObject("prop", property);
        mav.addObject("propList", tenant.getSchema().getAttributes());
        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
    }

    @RequestMapping(value = "/newprop", method = RequestMethod.POST)
    public String newProp(@ModelAttribute("tenant") Tenant tenant, @ModelAttribute("prop") Attribute prop,
                          BindingResult bindingResult, Model model) throws Exception {
        if (validateNewProperty(prop, tenant, bindingResult)) {
            tenantDAOService.setSchemaAttribute(prop, tenant);
            return "redirect:/getprops";
        } else {
            Map<String, String> supportedTypes = ProfileUserAccountUtil.getAttributesSupportedTypes();
            model.addAttribute("attributeTypes", supportedTypes);
            model.addAttribute("propList", tenant.getSchema().getAttributes());
            RequestContext context = RequestContext.getCurrent();
            model.addAttribute("currentuser", context.getAuthenticationToken().getProfile());
            return "newprop";
        }

    }

    @RequestMapping(value = "/updateprop", method = RequestMethod.POST)
    public String updateProp(@ModelAttribute("tenant") Tenant tenant, @ModelAttribute("prop") Attribute prop,
                             BindingResult bindingResult, Model model) throws Exception {
        if (validateUpdateProperty(prop, tenant, bindingResult)) {
            tenantDAOService.setSchemaAttribute(prop, tenant);
            return "redirect:/getprops";
        } else {
            model.addAttribute("propList", tenant.getSchema().getAttributes());
            RequestContext context = RequestContext.getCurrent();
            model.addAttribute("currentuser", context.getAuthenticationToken().getProfile());
            Map<String, String> supportedTypes = ProfileUserAccountUtil.getAttributesSupportedTypes();
            model.addAttribute("attributeTypes", supportedTypes);
            return "updateprop";
        }

    }

    @RequestMapping(value = "/prop", method = RequestMethod.GET)
    public ModelAndView findProp(@ModelAttribute("tenant") Tenant tenant, @RequestParam(required = false) String
        property) throws Exception {

        Attribute prop = TenantUtil.findSchemaAttributeByName(property, tenant);
        Map<String, String> supportedTypes = ProfileUserAccountUtil.getAttributesSupportedTypes();
        ModelAndView mav = new ModelAndView();
        if (prop != null) {
            mav.setViewName("updateprop");
            mav.addObject("attributeTypes", supportedTypes);
            mav.addObject("prop", prop);
            mav.addObject("propList", tenant.getSchema().getAttributes());
            RequestContext context = RequestContext.getCurrent();
            mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        } else {
            mav.setViewName("proplist");

            mav.addObject("propList", tenant.getSchema().getAttributes());
            RequestContext context = RequestContext.getCurrent();
            mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
            return mav;
        }

        return mav;
    }

    @RequestMapping(value = "/deleteprop", method = RequestMethod.POST)
    @ModelAttribute
    public ModelAndView deleteProp(@RequestParam("item") ArrayList<String> item, @ModelAttribute("tenant") Tenant
        tenant) throws Exception {
        tenantDAOService.deleteSchemaAttributes(item, tenant);

        ModelAndView mav = new ModelAndView();

        mav.setViewName("proplist");
        mav.addObject("baseUser", tenant.getSchema());
        mav.addObject("propList", tenant.getSchema().getAttributes());
        RequestContext context = RequestContext.getCurrent();
        mav.addObject("currentuser", context.getAuthenticationToken().getProfile());
        return mav;
    }

    private boolean validateNewProperty(Attribute prop, Tenant target, Errors errors) {
        boolean pass = true;

        profilePropertyFormValidator.validate(prop, errors);
        Pattern pattern = Pattern.compile("[,\\s]|@.*@");
        Matcher m = pattern.matcher(prop.getName());
        if (errors.hasErrors()) {
            pass = false;
        }
        if (m.find()) {
            errors.rejectValue("name", "property.name.validation.error.empty", null, null);
            pass = false;
        }
        for (Attribute attribute : target.getSchema().getAttributes()) {
            if (attribute.getName().equals(prop.getName())) {
                pass = false;
                errors.rejectValue("name", "property.value.validation.error.name", null, null);
            } else if (attribute.getOrder() == prop.getOrder()) {
                pass = false;
                errors.rejectValue("order", "property.value.validation.error.order", null, null);
            }
        }
        return pass;
    }

    private boolean validateUpdateProperty(Attribute prop, Tenant target, Errors errors) {
        boolean pass = true;
        profilePropertyFormValidator.validate(prop, errors);
        if (errors.hasErrors()) {
            pass = false;
        }
        for (Attribute c : target.getSchema().getAttributes()) {
            if (!c.getName().equals(prop.getName()) && c.getOrder() == prop.getOrder()) {
                pass = false;
                errors.rejectValue("order", "property.value.validation.error.order", null, null);
                break;
            }
        }
        return pass;
    }

    @Autowired
    public void setProfilePropertyFormValidator(ProfilePropertyFormValidator validator) {
        this.profilePropertyFormValidator = validator;
    }

    @Autowired
    public void setTenantDAOService(TenantDAOService tenantDAOService) {
        this.tenantDAOService = tenantDAOService;
    }

}
