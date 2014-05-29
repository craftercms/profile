<#import "spring.ftl" as spring />
<#import "common/crafter.ftl" as crafter />
<#import "common/components.ftl" as components />

<!DOCTYPE html>
<html lang="en">
<head>
    <@components.head "Crafter Admin Console - New Profile"/>
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
</head>

<body>
<div id="content">
    <@components.header "Manage Profiles > New Profile"/>

    <form id="form-item" action="<@spring.url '/profile/new'/>" method="post" accept-charset="UTF-8">
        <div class="box pad mt40 style-inputs">
            <p>
                <label for="username">Username:</label>
                <@spring.formInput "profile.username"/>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
                <label for="tenant">Tenant:</label>
                <@spring.bind "profile.tenant"/>
                <select id="tenant" name="tenant">
                <#list tenants as tenant>
                    <option value="${tenant.name}">${tenant.name?html}</option>
                </#list>
                </select>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
                <label for="email">Email:</label>
                <@spring.formInput "profile.email"/>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
                <label for="password">Password:</label>
                <@spring.formInput "profile.password", "", "password"/>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
                <label for="confirmPassword">Confirm Password:</label>
                <@spring.formInput "profile.confirmPassword", "", "password"/>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
                <label for="enabled">Enabled:</label>
                <@spring.formCheckbox "profile.enabled"/>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
                <label for="roles">Roles:</label>
                <@spring.formMultiSelect "profile.roles", {}/>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <#--#list profile.attributes?keys as attributeName>
                <label for="attributes['${attributeName}']">${attributeName}</label>
                <@spring.formInput "profile.attributes['${attributeName}']", "style='width: 270'"/>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </#list-->

            <p>
                <span class="hintField">* Required fields mark</span>
            </p>

            <p>
                <label>&nbsp;</label>
                <button class="btn btn-info" id="accept" type="submit">Accept</button>
                <button class="btn btn-info" id="cancel" type="button">Cancel</button>
            </p>
        </div>
    </form>
</div>

<@components.footer/>
</body>
</html>