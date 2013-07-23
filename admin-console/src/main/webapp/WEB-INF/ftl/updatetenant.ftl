<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
<title>Crafter Admin Console - Update Tenant</title>
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
	 		<li><a style="float:right" name="currentuser" href="item?username=${currentuser.userName}">User: ${currentuser.userName!""}</a></li>
	 	</ul>
		<h1 class="mainTitle">Manage Tenants</h1>
	</div>
  	  <form id="form-item" onsubmit="return onsubmitform();">
  	  	<!--div class="top">
  			<div class="pad">
			<nav>
				<ul class="main-nav clearfix">
					<li><a type="submit" href="javascript:onsubmitform('Groups');" onclick="javascript:onsubmitform('Groups');" value="Manage Groups" id="Groups" name="operation">Manage Groups-Roles Mapping</a></li>
		    	</ul>
			   </ul>
		    </nav>
	    </div>
	    </div-->   
  	  	<div class="box pad style-inputs">
           <p>
                <label  id="tenantName" for="tenantName">Tenant Name:</label>
                <span class="unit size1of3">${tenant.tenantName!""}</span><br/>
           </p>
           <@crafter.formInput "tenant.tenantName", "tenantName", "style=width:270", "hidden"/>
            <p>
		        <label  id="roleLabel" for="role">*Roles:</label>
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
				<span  class="hintField">Roles will be assigned to the Profiles of this Tenant</span>
    			<@crafter.showErrors "error-msg", "mbs", ""/>
            	 <label>&nbsp;</label>
            	<!--a type="submit" href="javascript:onsubmitform('Groups');" onclick="javascript:onsubmitform('Groups');" value="Manage Groups" id="Groups" name="operation">Manage Groups-Roles Mapping</a-->
            	<button class="btn btn-info" id="Groups" type="submit" value="Groups" onclick="document.pressed=this.id">Manage Groups-Roles Mapping</button>
            	<span  class="hintField">Mappings between the tenant roles and enterprise groups.</span>
            </p>
            <p>
                <label  id="tenantDomains" for="name">*Tenant Domains:</label>
                <@spring.bind "tenant.domains"/>
                <a id="add" style="text-decoration: none;cursor: pointer;">Add</a> | <a id="remove" style="text-decoration: none;cursor: pointer;">Remove</a> | <a id="reset" style="text-decoration: none;cursor: pointer;">Reset</a>
                <div id="domainList">
                    <#if tenant.domains?size == 0>
                        <input type="text" class="field" name="${spring.status.expression}" value="" "style=width:270" />
					<#else>
				        <#list tenant.domains as domain>
                            <input type="text" class="field" name="${spring.status.expression}" value="${domain}" "style=width:270" />
				        </#list>
					</#if>
                </div>
                <span id="tenantDomainHint" class="hintField">Domain names allow to access this Tenant</span>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>
            <p>
		        <label>&nbsp;</label>
		        <button class="btn btn-info" id="ManageAttributes" type="submit" value="ManageAttributes" onclick="document.pressed=this.id" formtarget="_blank">Manage Attributes</button>
            </p>
            <p>
            <span  class="hintField">* Required fields mark</span>
            </p>
            <p>
		        <label>&nbsp;</label>
		        <button class="btn btn-info" id="UpdateTenant" type="submit" value="UpdateTenant" onclick="document.pressed=this.id">Accept</button>
		        <button class="btn btn-info" name="operation" id="cancel-account-btn" type="submit" value="GetTenants" onclick="document.pressed=this.value">Cancel</button>
            </p>
            </div>
		  </form>
		  <table id="mytable">
		  	<tr>
		    	<th scope="col"">Tenant Name</th>
		    </tr>
		    <#list tenantList as t>
		      <tr>
		      	<td>${t.tenantName!""}</td>
		      </tr>
		    </#list>
		   </table>
</div>
<div class="footer" style="margin: 0 auto; width: 960px; padding: 10px 0pt;">
&copy; 2007-2013 Crafter SoftwareCorporation. All Rights Reserved.
</div>
</body>
</html>