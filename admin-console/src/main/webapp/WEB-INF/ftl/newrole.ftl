<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
<title>Crafter Admin Console - New Role</title>
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
		<h1 class="mainTitle">New System Role</h1>
	</div>
  	  <form id="form-item" onsubmit="return onsubmitform();">
  	  	<div class="box pad mt40 style-inputs">
		  	<p>
		        <label  id="roleName" for="name">Role Name:</label>
    			<@crafter.formInput "role.roleName", "roleName", "style=width:270", "text"/>
    			<span  class="hintField">System Role name</span>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>
            <br />
		    <p>
		        <label>&nbsp;</label>
		        <button class="btn btn-info" id="CreateRole" type="submit" value="Accept" onclick="document.pressed=this.id">Accept</button>
		        <button class="btn btn-info" name="operation" id="cancel-account-btn" type="submit" value="CancelRole" onclick="document.pressed=this.value">Cancel</button>
            </p>
            </div>
		  </form>
		  
</div>  
<div class="footer" style="margin: 0 auto; width: 960px; padding: 10px 0pt;">
&copy; 2007-2013 Crafter SoftwareCorporation. All Rights Reserved.
</div> 	
</body>
</html> 