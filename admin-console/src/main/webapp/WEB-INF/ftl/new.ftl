<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Crafter Admin Console - New Profile</title>
    <link href="resources/image/favicon.ico" rel="Shortcut Icon">
    <link rel="stylesheet" href="resources/css/profile.css">
    <script src="resources/js/jquery-1.9.1.min.js"></script>
    <script language="javascript" src="resources/js/util.js"></script>
</head>
<body>
<div id="content">
    <div id="header">
        <a class="logo" href="index.jsp" title="Crafter Profile Admin Console"></a>
        <ul class="page-actions">
            <li><a type="submit" href="javascript:onsubmitform('Logout');" value="Logout" id="Logout" name="operation">Logout</a>
            </li>
        </ul>
        <h1 class="mainTitle">Manage Profiles > New Profile</h1>
    </div>

    <form id="form-item" onsubmit="return onsubmitform();" accept-charset="UTF-8">
        <div class="box pad mt40 style-inputs">
            <p>
                <label id="tenantLabel" for="tenantName">*Tenant:</label>
            <@spring.bind "account.tenantName"/>
            <#assign selectedTenant = spring.status.value?default(tenantNames?values[0])>
                <select style="width:270px;" id="selectedTenantName" name="selectedTenantName"
                        onchange="javascript:onsubmitform('New');">
                <#list tenantNames?keys as key>
                    <#if (selectedTenant == key) >
                        <#assign isSelected = true>
                    <#else>
                        <#assign isSelected = false>
                    </#if>
                <option value="${key?html}"<#if isSelected> selected="selected"</#if>>${tenantNames[key]?html}
				    </#list>
                </select>
                <span class="hintField">Select tenant name for the new profile</span>
            <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
                <label id="usernameLabel" for="username">*User Name:</label>
            <@spring.bind "account.username"/>    
            <@crafter.formInput "account.username", "username", "style=width:270 class='test'", "text"/>
                <span class="hintField">Username for the new  Profile</span>
            <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
            	<@spring.bind "account.email"/>
                <label id="emailLabel" for="email">*Email:</label>
            <@crafter.formInput "account.email", "emailAccount", "style=width:270 class='test'", "text"/>
                <span class="hintField">Valid Profile email account</span>
            <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
            	<@spring.bind "account.password"/>
                <label id="passwordLabel" for="password">*Password:</label>
            <@crafter.formInput "account.password", "passwordAccount", "style=width:270 class='test'", "password"/>
                <span class="hintField">Profile password</span>
            <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
            	<@spring.bind "account.confirmPassword"/>
                <label id="passwordConfirmLabel" for="passwordConfirm">*Confirm Password:</label>
            <@crafter.formInput "account.confirmPassword", "confirmPassword", "style=width:270 class='test'", "password"/>
                <span class="hintField">Confirm password</span>
            <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
            	<@spring.bind "account.active"/>
                <label id="acountActive" for="active">Active?:</label>
            <@crafter.formCheckbox "account.active", "active"/>
                <span class="hintField">Is this account active?</span>
            <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <div id="schemaAttributes">
            <#list attributeList as attribute>
                <#assign index=attributeList?seq_index_of(attribute)>
                <p>
                    <#assign attributePath = "account.attributeDefinitions['"+ attribute.name + "']">
                    <label id="${attribute.name+"Label"}" for="attribute.name" class='schemaAttribute'>
                        <#if attribute.required>
                            *${attribute.label}:
                        <#else>
                        ${attribute.label}:
                        </#if>
                    </label>
                    <@crafter.formInput attributePath, "attribute.name","style=width:270 class='test schemaAttribute' ", "text" />
                    <@crafter.showErrors "error-msg", "mbs", ""/>
                </p>
            </#list>
                <p>
                    <label id="roleLabel" for="role">*Roles:</label>
                <@spring.bind "account.roles"/>
                <#assign selectedRoles = spring.status.value?default(" ")>
                    <select style="width:270px;" multiple="multiple" id="${spring.status.expression}"
                            name="${spring.status.expression}">
                    <#list account.roleOption?keys as value>
                        <#if selectedRoles?contains(value) >
                            <#assign isSelected = true>
                        <#else>
                            <#assign isSelected = false>
                        </#if>
                        <option value="${value?html}"<#if isSelected> selected="selected"</#if>>
                        ${account.roleOption[value]?html}
                        </option>
                    </#list>
                    </select>
                <@crafter.showErrors "error-msg", "mbs", ""/>
                </p>
            </div>
            <p>
                <span class="hintField">* Required fields mark</span>
            </p>

            <p>
                <label>&nbsp;</label>
                <button class="btn btn-info" name="Create" id="Create" type="submit" value="Accept"
                        onclick="document.pressed=this.id">Accept
                </button>
                <button class="btn btn-info" name="operation" id="cancel-account-btn" type="submit" value="Cancel"
                        onclick="document.pressed=this.value">Cancel
                </button>
            </p>
        </div>
    </form>
</div>
<div class="footer" style="margin: 0 auto; width: 960px; padding: 10px 0pt;">
    &copy; 2007-2013 Crafter SoftwareCorporation. All Rights Reserved.
</div>
</body>
</html>