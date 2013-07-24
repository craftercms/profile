<#import "spring.ftl" as spring />
<#import "crafter.ftl" as crafter />
<!DOCTYPE html>
<html lang="en">
<head>
<title>Crafter Admin Console - New Attribute</title>
<link href="resources/image/favicon.ico" rel="Shortcut Icon">
<link rel="stylesheet" href="resources/css/profile.css">
<script src="resources/js/jquery-1.9.1.min.js"></script>
<script language="javascript" src="resources/js/util-props.js"></script>
</head> 
<body>
<div id="content">
  <div id="header">
	 	<a class="logo" href="index.jsp" title="Crafter Profile Admin Console"></a> 
	 	<ul class="page-actions">
	 		<li><a type="submit" href="javascript:onsubmitform('Logout');" value="Logout" id="Logout" name="operation">Logout</a></li>
	 		<li><a style="float:right" name="currentuser" href="item?username=${currentuser.userName}">User: ${currentuser.userName!""}</a></li>
	 	</ul>
		<h1 class="mainTitle">Manage Attributes</h1>
	</div>
  	  <form id="form-item" onsubmit="return onsubmitform();">
  	  	<div class="box pad mt40 style-inputs">
		  	<p>
		  		<@spring.bind "prop.name"/>
		        <label  id="attributeName" for="name">Attribute Name:</label>
    			<@crafter.formInput "prop.name", "name", "style=width:270", "text"/>
    			<span  class="hintField">It is used to store the information on each profile</span>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>
		    <p>
		    	<@spring.bind "prop.label"/>
		        <label  id="attributeLabel" for="label">Attribute Label:</label>
    			<@crafter.formInput "prop.label", "label", "style=width:270", "text"/>
    			<span  class="hintField">Label displayed to request data in the profile page</span>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>
            <p>
            	<@spring.bind "prop.order"/>
		        <label  id="attributeOrder" for="order">Attribute Order:</label>
    			<@crafter.formInput "prop.order", "order", "style=width:270", "text"/>
    			<span  class="hintField">Order where this field is displayed in the profile page</span>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>
            <p>
		        <label  id="attributeType" for="type">Attribute Type:</label>
    			<@spring.bind "prop.type"/>
				<#assign attributeType = spring.status.value?default(attributeTypes?values[0])>
				<select style="width:270px;" id="${spring.status.expression}" name="${spring.status.expression}">
				    <#list attributeTypes?keys as key>
					    <#if (attributeType == key) >
					        <#assign isSelected = true>
					    <#else>
					        <#assign isSelected = false>
					    </#if>
				    	<option value="${key?html}"<#if isSelected> selected="selected"</#if>>${attributeTypes[key]?html}
				    </#list>
				</select>
				<span  class="hintField">Field Type used to request the information in the profile page</span>
    			<@crafter.showErrors "error-msg", "mbs", ""/>
            </p>
            <p>
            	<@spring.bind "prop.constraint"/>
		        <label  id="attributeConstraint" for="constraint">Attribute Constraint:</label>
    			<@crafter.formInput "prop.constraint", "constraint", "style=width:270", "text"/>
    			<span  class="hintField">Regular expression used during the validation</span>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>
            <p>
		        <label  id="attributeRequired" for="required">Attribute Required:</label>
    			<@crafter.formCheckbox "prop.required", "required"/>
    			<span  class="hintField">Is this field required?</span>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>
            <p>
		        <label>&nbsp;</label>
		        <button class="btn btn-info" id="CreateProp" type="submit" value="Accept" onclick="document.pressed=this.id">Accept</button>
		        <button class="btn btn-info" name="operation" id="cancel-account-btn" type="submit" value="CancelProp" onclick="document.pressed=this.value">Cancel</button>
            </p>
            </div>
		  </form>
		  
</div>  
<div class="footer" style="margin: 0 auto; width: 960px; padding: 10px 0pt;">
&copy; 2007-2013 Crafter SoftwareCorporation. All Rights Reserved.
</div> 	
</body>
</html> 