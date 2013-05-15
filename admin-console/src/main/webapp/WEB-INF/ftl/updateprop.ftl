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
	 		<li><a style="float:right" name="currentuser" href="item?username=${currentuser.userName}">User: ${currentuser.userName!""}</a></li>
	 	</ul>
		<h1 class="mainTitle">Manage Attributes</h1>
	</div>
        <form id="form-item" onsubmit="return onsubmitform();">
            <p>
            <label  id="attributeValue" for="attributeValue">Attribute Name:</label>
            <span class="unit size1of3">${prop.name!""}</span><br/>
    		<@crafter.formInput "prop.name", "name", "hidden", "text"/>
            </p>
            <p>
                <label  id="attributeLabel" for="label">Attribute Label:</label>
                <@crafter.formInput "prop.label", "label", "style=width:270", "text"/>
                <span  class="hintField">Label displayed to request data in the profile page</span>
                <@crafter.showErrors "error-msg", "mbs", ""/>
            </p>
            <p>
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
                <button class="btn btn-info" id="UpdateProp" type="submit" value="Accept" onclick="document.pressed=this.id">Accept</button>
                <button class="btn btn-info" name="operation" id="cancel-account-btn" type="submit" value="CancelProp" onclick="document.pressed=this.value">Cancel</button>
            </p>
         </form>
         <table id="mytable">
		  	<tr>
		    	<th scope="col">Attribute Order</th>
		    	<th scope="col"">Attribute Name</th>
		    	<th scope="col"">Attribute Label</th>
		    	<th scope="col">Attribute Type</th>
		    	<th scope="col">Attribute Constraint</th>
		    	<th scope="col">Attribute Required</th>
		    </tr>
		    <#list propList as prop>
		      <tr>
		        <td>${prop.order!""}</td>
		      	<td>${prop.name!""}</td>
		        <td>${prop.label!""}</td>
		        <td>${prop.type!""}</td>
		        <td>${prop.constraint!"Not Defined"}</td>
		        <td>${prop.required?string("Yes", "No")}</td>
		      </tr>
		    </#list>
		   </table>
		
  
</div>  
<div class="footer" style="margin: 0 auto; width: 960px; padding: 10px 0pt;">
&copy; 2007-2013 Crafter SoftwareCorporation. All Rights Reserved.
</div> 	
</body>
</html> 