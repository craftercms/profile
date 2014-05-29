//package org.craftercms.profile.management.services;
//
//import java.util.List;
//
//import org.craftercms.profile.client.exceptions.AppAuthenticationFailedException;
//import org.craftercms.profile.client.impl.domain.Role;
//import org.springframework.validation.BindingResult;
//
//public interface RoleService {
//    List<Role> getAllRoles() throws AppAuthenticationFailedException;
//
//    Role createRole(Role role) throws AppAuthenticationFailedException;
//
//    void deleteRole(String id) throws AppAuthenticationFailedException;
//
//    void deleteRole(List<String> item, BindingResult bindingResult) throws AppAuthenticationFailedException;
//
//    List<String> getTenantsByRoleName(String roleName) throws AppAuthenticationFailedException;
//}
