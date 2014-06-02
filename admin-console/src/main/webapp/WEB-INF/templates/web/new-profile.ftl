<#import "spring.ftl" as spring />
<#import "common/crafter.ftl" as crafter />
<#import "common/components.ftl" as components />
<#import "layouts/main-layout.ftl" as main/>

<#assign scripts>
<script src="<@spring.url '/resources/js/new-profile.js'/>"></script>
<script type="text/javascript">
    var allAvailableRoles = {
        <#list tenants as tenant>
            "${tenant.name}" : [
                <#list tenant.availableRoles as role>
                    "${role}"<#if role_has_next>,</#if>
                </#list>
            ]<#if tenant_has_next>,</#if>
        </#list>
    }
</script>
</#assign>

<@main.layout "Crafter Profile Admin Console - New Profile", "New Profile", "", scripts>
<form action="<@spring.url '/profile/new'/>" method="post" role="form">
    <div class="form-group">
        <label for="username">Username</label>
        <@spring.formInput "profile.username", "class='form-control'"/>
    </div>

    <div class="form-group">
        <label for="tenant">Tenant</label>
        <@spring.bind "profile.tenant"/>
        <select id="tenant" name="tenant"class="form-control">
            <#list tenants as tenant>
                <option value="${tenant.name}">${tenant.name?html}</option>
            </#list>
        </select>
    </div>

    <div class="form-group">
        <label for="email">Email</label>
        <@spring.formInput "profile.email", "class='form-control'"/>
    </div>

    <div class="form-group">
        <label for="password">Password</label>
        <@spring.formInput "profile.password", "class='form-control'", "password"/>
    </div>

    <div class="form-group">
        <label for="confirmPassword">Confirm Password</label>
        <@spring.formInput "profile.confirmPassword", "class='form-control'", "password"/>
    </div>

    <div class="checkbox">
        <label for="enabled">Enabled</label>
        <@spring.formCheckbox "profile.enabled"/>
    </div>

    <div class="form-group">
        <label for="roles">Roles</label>
        <@spring.formMultiSelect "profile.roles", {}, "class='form-control'"/>
    </div>

    <button class="btn btn-default" id="accept" type="submit">Accept</button>
    <button class="btn btn-default" id="cancel" type="button">Cancel</button>
</form>
</@main.layout>