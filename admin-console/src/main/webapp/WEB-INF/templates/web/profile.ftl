<#import "spring.ftl" as spring />
<#import "common/crafter.ftl" as crafter />
<#import "common/components.ftl" as components />

<!DOCTYPE html>
<html lang="en">
<head>
    <@components.head "Crafter Admin Console - Update Profile"/>
</head>

<body>
<div id="content">
    <@components.header "Manage Profiles > Update Profile"/>

    <form id="form-item" action="<@spring.url '/profile/${profile.id}'/>" method="post" accept-charset="UTF-8">
        <div class="box pad mt40 style-inputs">
            <p>
                <label for="username">Username:</label>
                <@spring.formInput "profile.username", "disabled='disabled'"/>
            </p>

            <p>
                <label for="tenant">Tenant:</label>
                <@spring.formInput "profile.tenant", "disabled='disabled'"/>
            </p>

            <p>
                <label for="email">Email:</label>
                <@spring.formInput "profile.email"/>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
                <label for="password">Password:</label>
                <@spring.formInput "profile.password", "password"/>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
                <label for="confirmPassword">Confirm Password:</label>
                <@spring.formInput "profile.confirmPassword", "password"/>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
                <label for="enabled">Enabled:</label>
                <@spring.formCheckbox "profile.enabled"/>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
                <label for="verified">Verified:</label>
                <@spring.formCheckbox "profile.verified", "disabled='disabled'"/>
            </p>

            <p>
                <label for="createdOn">Created On:</label>
                <@spring.formInput "profile.createdOn", "disabled='disabled'"/>
            </p>

            <p>
                <label for="lastModified">Last Modified On:</label>
                <@spring.formInput "profile.lastModified", "disabled='disabled'"/>
            </p>

            <p>
                <label for="roles">Roles:</label>
                <@spring.bind "profile.roles"/>
                <select multiple="multiple" id="roles" name="roles">
                    <#list availableRoles as role>
                        <#assign isSelected = spring.contains(profile.roles, role)>
                        <option value="${role}"<#if isSelected> selected="selected"</#if>>${role?html}</option>
                    </#list>
                </select>
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
                <button class="btn btn-info" id="update" type="submit" value="accept">Accept</button>
                <button class="btn btn-info" id="cancel" type="button" value="cancel">Cancel</button>
            </p>
        </div>
    </form>
</div>

<@components.footer/>
</body>
</html>