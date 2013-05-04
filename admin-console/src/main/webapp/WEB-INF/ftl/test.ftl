<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<html>
<head>
<title>Crafter Profile Account Management</title>
<link href="resources/image/favicon.ico" rel="Shortcut Icon">
<link rel="stylesheet" href="resources/css/profile.css">
<script>
function onsubmitform()
{
  if(document.pressed == 'Create') {
   document.forms["form-new-account"].method = "post";
   document.forms["form-new-account"].action = "new";
  }
  else if(document.pressed == 'Cancel') {
    document.forms["form-new-account"].method = "get";
	document.forms["form-new-account"].action = "get";
  }
  return true;
}
</script>
</head> 
<body>
<!--hgroup id="heading">
<div id="header">
 <h1 class="company-title"> <a title="Visit Crafter CMS" href="http://craftercms.org" class="logo"> <span>Crafter Profile Account Management</span> </a> </h1>
</div>
</hgroup-->
<div id="content">
  <div id="header">
	 <a title="Visit Crafter CMS" href="http://craftercms.org" class="logo"></a> 
  </div>
  <fieldset>
  	<legend>New Account</legend>
		  <form id="form-new-account" onsubmit="return onsubmitform();">
		    <#list testList as u>
      
		      <tr>
		        <td colspan="1"><input type=checkbox name="item" value="${u.id}" unchecked></td>
		      	<td><a name="username" href="item?username=${u.username}">${u.username!""}</a></td>
		        <td>${u.firstName!""}</td>
		        <td>${u.lastName!""}</td>
		        <td>${u.email!""}</td>
		      </tr>
		      <label for="{u}">
                <span class="unit size1of3">{u}:</span><br/>
                <@crafter.formInput "{u}", "{u}", "style=width:270", "text"/>
                
            	</label><br/>
		     
		    </#list>
		    <#list testList?keys as key>
		         <tr><td>${key}</td><td>
		         <!--#list document.assocs[key] as t>
		            ${t.displayPath}/${t.name}<br>
		         </#list-->
		         </td></tr>
		      </#list>
            
		  	<!--input type="submit" value="   Save   " /-->
		  	<div>
                 <span>
                      <input name="operation" class="new-account-btn" id="new-account-btn" type="submit" value="Create" onclick="document.pressed=this.value"/>
                 </span>
                 <span>
                      <input name="operation" class="cancel-account-btn" id="cancel-account-btn" type="submit" value="Cancel" onclick="document.pressed=this.value"/>
                 </span>
            </div>
		  </form>
	</fieldset>	
  
</div>  
	<!--script type="text/javascript" src="../resources/js/add-new.js"></script-->
</body>
</html> 