function onsubmitform(param) {
	var cForm = document.getElementById('form-list');
	if (cForm == null || cForm == undefined) {
		cForm = document.getElementById('form-item');
	}
	if  (param == 'DeleteProp'
			||  document.pressed == 'DeleteProp') {
		var checklist = document.getElementsByName("item");
		var selectedItems = false;
		for ( var j = 0; j < checklist.length; j++) {
			if (checklist[j].checked == true) {
				selectedItems = true;
				break;
			}
		}
		if (selectedItems == true
				&& (document.pressed == 'DeleteProp' || param == 'DeleteProp')) {
			cForm.method = "post";
			cForm.action = "deleteprop";
			cForm.submit();
		}
	} else if (param == 'Logout' || document.pressed == 'Logout') {
		cForm.method = "get";
		cForm.action = "crafter-security-logout";
		cForm.submit();
	} else if (param == 'CreateProp' || document.pressed == 'CreateProp') {
		cForm.method = "post";
		cForm.action = "newprop";
		cForm.submit();
	} else if (param == 'NewProp' || document.pressed == 'NewProp') {
		cForm.method = "get";
		cForm.action = "newprop";
		cForm.submit();

	} else if (param == 'CancelProp' || document.pressed == 'CancelProp') {
		cForm.method = "get";
		cForm.action = "getprops";
		cForm.submit();
	} else if (param == 'UpdateProp' || document.pressed == 'UpdateProp') {
		cForm.method = "post";
		cForm.action = "updateprop";
		cForm.submit();
	} else if (param == 'UserProperties'
			|| document.pressed == 'UserProperties') {
		cForm.method = "get";
		cForm.action = "getprops";
		cForm.submit();
	} else if (param == 'ManageAttributes'
			|| document.pressed == 'ManageAttributes') {
		cForm.method = "get";
		cForm.action = "getprops";
		cForm.submit();
	} 
}

$(document)
		.ready(
				function() {

					var appName = 'admin-console';

					var i = $('.field').size();

					$('#add')
							.click(
									function() {
										$(
												'<input type="text" class="field" name="domains" value="" "style=width:270" />')
												.fadeIn('slow').appendTo(
														'#domainList');
										i++;
									});

					$('#remove').click(function() {
						if (i > 1) {
							$('.field:last').remove();
							i--;
						}
					});

					$('#reset').click(function() {
						while (i > 1) {
							$('.field:last').remove();
							i--;
						}
					});

					$('#backTenant').click(function() {
						window.close();
					});

					$('#tenantName')
							.change(
									function() {
										var data = {
											tenantName : $(this).val()
										};
										var url = '/'
												+ appName
												+ '/tenant_attributes_and_roles';

										$
												.ajax({
													url : url,
													data : data,
													dataType : 'html',
													contentTypeString : "text/html;charset=UTF-8",
													cache : false,
													type : 'GET',
													success : function(aData,
															textStatus, jqXHR) {
														$('#schemaAttributes')
																.children()
																.remove();
														$('#schemaAttributes')
																.append(aData);
													},
													error : function(xhr,
															ajaxOptions,
															thrownError) {
														alert(xhr.status);
													}
												});
									});

					$('#selectedTenantName').change(function() {
						this.form.submit();
					});
				});
