<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
<title>Crafter Admin Console</title>
<link href="resources/image/favicon.ico" rel="Shortcut Icon">
<link rel="stylesheet" href="resources/css/profile.css">
<script src="resources/js/jquery-1.9.1.min.js"></script>
<script language="javascript" src="resources/js/util.js"></script>
</head>
<body>
<div id="content">
  <div id="header">
	 	<a class="logo" href="http://craftercms.org" title="Visit Crafter CMS"></a>
	 	<ul class="page-actions">
	 		<li><a type="submit" href="javascript:onsubmitform('Logout');" value="Logout" id="Logout" name="operation">Logout</a></li>
	 		<li><a style="float:right" name="currentuser" href="item?username=${currentuser.username}">User: ${currentuser.username!""}</a></li>
	 	</ul>
		<h1 class="mainTitle">Manage Tenants</h1>
	</div>
  	  <form id="form-item" onsubmit="return onsubmitform();">
  	  	<div class="box pad mt40 style-inputs">
		  	<p>
		        <label  id="tenantName" for="tenantName">Tenant Name:</label>
    			<@crafter.formInput "tenant.tenantName", "tenantName", "style=width:270", "text"/>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>
            <p>
		        <label  id="roleLabel" for="role">Roles:</label>
	            <@spring.bind "tenant.roles"/>
				<#assign selectedRoles = spring.status.value?default(" ")>
				<select style="width:270px;" multiple="multiple" id="${spring.status.expression}" name="${spring.status.expression}"}>
				    <#list roleOption as role>
					    <#if selectedRoles?contains(role.roleName) >
					        <#assign isSelected = true>
					    <#else>
					        <#assign isSelected = false>
					    </#if>
				    	<option value="${role.roleName?html}"<#if isSelected> selected="selected"</#if>>${role.roleName?html}
				    </#list>
				</select>
    			<@crafter.showErrors "error-msg", "mbs", ""/>
            </p>
            <p>
                <label  id="tenantDomains" for="name">Tenant Domains:</label>
                <@spring.bind "tenant.domains"/>
                <a href="#" id="add">Add</a> | <a href="#" id="remove">Remove</a> | <a href="#" id="reset">Reset</a>
                <div id="domainList">
                    <#if tenant.domains?size == 0>
                        <input type="text" class="field" name="${spring.status.expression}" value="" "style=width:270" />
					<#else>
				        <#list tenant.domains as domain>
                            <input type="text" class="field" name="${spring.status.expression}" value="${domain}" "style=width:270" />
				        </#list>
					</#if>
                </div>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>
            <p>
		        <label>&nbsp;</label>
		        <button class="btn btn-info" id="ManageAttributes" type="submit" value="ManageAttributes" onclick="document.pressed=this.id" formtarget="_blank">Manage Attributes</button>
            </p>
            <p>
		        <label>&nbsp;</label>
		        <button class="btn btn-info" id="CreateTenant" type="submit" value="Accept" onclick="document.pressed=this.id">Accept</button>
		        <button class="btn btn-info" name="operation" id="cancel-account-btn" type="submit" value="CancelTenant" onclick="document.pressed=this.value">Cancel</button>
            </p>
            </div>
		  </form>
</div>
<div class="footer" style="margin: 0 auto; width: 960px; padding: 10px 0pt;">
&copy; 2007-2013 Crafter SoftwareCorporation. All Rights Reserved.
</div>
</body>
</html>