<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
<title>Crafter Admin Console - Update Group</title>
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
		<h1 class="mainTitle">Role Detail</h1>
	</div>
  	  <form id="form-item" onsubmit="return onsubmitform();">
  	    <div class="top">
  			<div class="pad">
  			
			<nav>
				<ul class="main-nav clearfix">
				    <#if (enableDelete) >
				    	<li><a type="submit" href="javascript:onsubmitform('DeleteRole');" onclick="javascript:onsubmitform('DeleteRole');" value="Delete Role" id="DeleteRole" name="operation">Delete Role</a></li>
				    </#if>	
					<li><a type="submit" href="javascript:onsubmitform('Roles');" value="Roles" id="Roles" name="operation">Manage System Roles</a></li>
		    	</ul>
			   </ul>
		    </nav>
		    
	    </div>
  	  	<div class="box pad mt40 style-inputs">
		  	<p>
		        <label  id="roleName" for="groupName">Role Name:</label>
                <span class="unit size1of3">${role.roleName!""}</span><br/>
            </p>
            <@crafter.formInput "role.roleName","roleName", "style=width:270", "hidden"/>
            <br />
            <p>
                <#if (enableDelete== false) >
            		<span  class="hintField">Tenants that are using this role. You should remove this role from those tenants if you want to delete it</span>
            	</#if>	
            	<table id="mytable">
			  	<tr>
    			<th scope="col">Tenant Name</th>
    			</tr>
    			<#list tenantNames as t>
				      <tr>
				        <td><a name="${t}" id="${t}"   href="#">${t!""}</a></td>
				      </tr>

    			</#list>
    			</table>
		    </div>
		  </form>
		  
</div>  
<div class="footer" style="margin: 0 auto; width: 960px; padding: 10px 0pt;">
&copy; 2007-2013 Crafter SoftwareCorporation. All Rights Reserved.
</div> 	
</body>
</html> 