<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
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
	 		<li><a style="float:right" name="currentuser" href="item?username=${currentuser.userName}">User: ${currentuser.userName!""}</a></li>
	 	</ul>
		<h1 class="mainTitle">Manage Attributes</h1>
	</div>

  <form id="form-list" onsubmit="return onsubmitform();" accept-charset="UTF-8">
  	<div class="top">
  		<div class="pad">	
		  <nav>
				<ul class="main-nav clearfix">
					<li><a type="submit" href="javascript:onsubmitform('NewProp');" value="New Profile" id="New" name="operation">New Attribute</a></li>
		    		<li><a type="submit" href="javascript:onsubmitform('DeleteProp');" value="Delete Profile" id="Delete" name="operation">Delete Attribute</a></li>
				    <li><a type="submit" href="" value="Search" id="backTenant" name="operation">Back to Tenant</a></li>
		    	</ul>
		    </nav>
		  </div>
   	</div>   	
  <table id="mytable">
  	<tr>
    	<th scope="col"><input type=checkbox onclick="checkAll();" name="all" value="all" unchecked></th>
    	<th scope="col">Name</th>
    	<th scope="col">Label</th>
    </tr>
    <#list propList as prop>
      <tr>
        <td>
        	<input type=checkbox name="item" value="${prop.name}" unchecked>
        </td>
      	<td><a name="name" href="prop?property=${prop.name}">${prop.name!""}</a></td>
        <td>${prop.label!""}</td>
      </tr>
    </#list>
    
    </table>
    </form>
</div> 
<div class="footer" style="margin: 0 auto; width: 960px; padding: 10px 0pt;">
&copy; 2007-2013 Crafter SoftwareCorporation. All Rights Reserved.
</div>  
</body>
</html> 