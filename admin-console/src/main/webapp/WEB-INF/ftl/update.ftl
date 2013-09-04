<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Crafter Admin Console - Update Profile</title>
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
            <li><a style="float:right" name="currentuser"
                   href="item?username=${currentuser.userName}">User: ${currentuser.userName!""}</a></li>
        </ul>
        <h1 class="mainTitle">Manage Profiles > Update Profile</h1>
    </div>
    <form id="form-item" onsubmit="return onsubmitform();" accept-charset="UTF-8">
        <div class="box pad mt40 style-inputs">
            <p>
                <label id="attributeValue" for="attributeValue">User Name:</label>
                <span class="unit size1of3">${account.username!""}</span><br/>
            </p>

            <p>
                <label id="attributeValue" for="attributeValue">Tenant Name:</label>
                <span class="unit size1of3">${tenantName!""}</span><br/>
            </p>

            <p>
                <label id="emailLabel" for="email">Email:</label>
            <@crafter.formInput "account.email", "email", "style=width:270 class='test'", "text"/>
                <span class="hintField">Valid Profile email account</span>
            <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
                <label id="pass" for="pass">Password:</label>
            <@crafter.formInput "account.password", "password", "style=width:270", "password"/>
                <span class="hintField">Profile password</span>
            <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
                <label id="passwordConfirm" for="passwordConfirm">Confirm Password:</label>
            <@crafter.formInput "account.confirmPassword", "confirmPassword", "style=width:270", "password"/>
                <span class="hintField">Confirm password</span>
            <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <p>
                <label id="acountActive" for="active">Active?:</label>
            <@crafter.formCheckbox "account.active", "active"/>
                <span class="hintField">Is this account active?</span>
            <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>

            <div id="schemaAttributes">
            <#list attributeList as attribute>
                <#assign index=attributeList?seq_index_of(attribute)>
                <p>
                    <#assign attributePath = "account.attributes['"+ attribute.name + "']">
                    <label id="${attribute.name+"Label"}" for="attribute.name" class='schemaAttribute'>
                        <#if attribute.required>
                            *${attribute.label}:
                        <#else>
                        ${attribute.label}:
                        </#if>
                    </label>
                    <@crafter.formInput attributePath, "attribute.name","style=width:270", "text" />
                </p>
            </#list>
                <p>
                    <label id="roleLabel" for="role">Roles:</label>
                <@spring.bind "account.roles"/>
                <#assign selectedRoles = spring.status.value?default(" ")>
                    <select style="width:270px;" multiple="multiple" id="${spring.status.expression}"
                            name="${spring.status.expression}"}>
                <#list account.roleOption?keys as value>
                    <#if selectedRoles?contains(value) >
                        <#assign isSelected = true>
                    <#else>
                        <#assign isSelected = false>
                    </#if>
                <option value="${value?html}"<#if isSelected>
                        selected="selected"</#if>>${account.roleOption[value]?html}
                </#list>
                    </select>
                <@crafter.showErrors "error-msg", "mbs", ""/>
                </p>
            </div>
            <p>
                <span class="hintField">* Required fields mark</span>
            </p>
        <@crafter.formInput "account.username", "username", "style=width:270", "hidden"/>
        <@crafter.formInput "account.id", "id", "style=width:270", "hidden"/>
            <p>
                <label>&nbsp;</label>
                <button class="btn btn-info" id="Update" type="submit" value="Accept"
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