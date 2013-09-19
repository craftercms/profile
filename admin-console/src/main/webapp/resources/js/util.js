function removeDomain(param) {
    if ($('.domainParent').size() > 1) {
        document.getElementById(param).remove();
    }

}
function checkAll() {
    var all = document.getElementsByName('all');
    if (all.length > 0) {
        all = all[0];
    }
    var checklist = document.getElementsByName("item");
    for (var j = 0; j < checklist.length; j++) {
        if (all.checked) {
            checklist[j].checked = true;
        } else {
            checklist[j].checked = false;
        }
    }
}

function onsubmitform(param) {
    var cForm = document.getElementById('form-list');
    if (cForm == null || cForm == undefined) {
        cForm = document.getElementById('form-item');
    }
    if (param == 'New' || document.pressed == 'New') {
        cForm.method = "get";
        cForm.action = "new";
        cForm.submit();

    } else if (param == 'Filter' || document.pressed == 'Filter') {
        var filter = document.getElementById("filter");
        if (filter != null && filter.value != "") {
            cForm.method = "get";
            cForm.action = "search";
            cForm.submit();
        }
    } else if (param == 'Delete' || param == 'DeleteProp'
        || document.pressed == 'Delete' || document.pressed == 'DeleteProp') {
        var checklist = document.getElementsByName("item");
        var selectedItems = false;
        for (var j = 0; j < checklist.length; j++) {
            if (checklist[j].checked == true) {
                selectedItems = true;
                break;
            }
        }
        if (selectedItems == true
            && (document.pressed == 'Delete' || param == 'Delete')) {
            cForm.method = "post";
            cForm.action = "delete";
            cForm.submit();
        } else if (selectedItems == true
            && (document.pressed == 'DeleteProp' || param == 'DeleteProp')) {
            cForm.method = "post";
            cForm.action = "deleteprop";
            cForm.submit();
        }
    } else if (param == 'Logout' || document.pressed == 'Logout') {
        cForm.method = "get";
        cForm.action = "crafter-security-logout";
        cForm.submit();
    } else if (param == 'Previous' || document.pressed == 'Previous') {
        cForm.method = "get";
        cForm.action = "prev";
        cForm.submit();
    } else if (param == 'Next' || document.pressed == 'Next') {
        cForm.method = "get";
        cForm.action = "next";
        cForm.submit();
    } else if (param == 'Create' || document.pressed == 'Create') {
        cForm.method = "post";
        cForm.action = "new";
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
    } else if (param == 'Cancel' || document.pressed == 'Cancel') {
        cForm.method = "get";
        cForm.action = "get";
        cForm.submit();
    } else if (param == 'Update' || document.pressed == 'Update') {
        cForm.method = "post";
        cForm.action = "update";
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
    } else if (param == 'Accounts' || document.pressed == 'Accounts') {
        cForm.method = "get";
        cForm.action = "get";
        cForm.submit();
    } else if (param == 'CreateTenant' || document.pressed == 'CreateTenant') {
        cForm.method = "post";
        cForm.action = "newtenant";
        cForm.submit();
    } else if (param == 'CancelTenant' || document.pressed == 'CancelTenant') {
        cForm.method = "get";
        cForm.action = "gettenants";
        cForm.submit();
    } else if (param == 'NewTenant' || document.pressed == 'NewTenant') {
        cForm.method = "get";
        cForm.action = "newtenant";
        cForm.submit();
    } else if (param == 'ManageAttributes'
        || document.pressed == 'ManageAttributes') {
        cForm.method = "get";
        cForm.action = "getprops";
        cForm.submit();
    } else if (param == 'Groups'
        || document.pressed == 'Groups') {
        cForm.method = "get";
        cForm.action = "grouplist";
        cForm.submit();
    } else if (param == 'CreateGroup'
        || document.pressed == 'CreateGroup') {
        cForm.method = "post";
        cForm.action = "new_group_mapping";
        cForm.submit();
    } else if (param == 'DeleteGroup'
        || document.pressed == 'DeleteGroup') {
        var checklist = document.getElementsByName("item");
        var selectedItems = false;
        for (var j = 0; j < checklist.length; j++) {
            if (checklist[j].checked == true) {
                selectedItems = true;
                break;
            }
        }
        if (selectedItems == true) {
            cForm.method = "post";
            cForm.action = "delete_group_mapping";
            cForm.submit();

        }

    } else if (param == 'CancelGroup'
        || document.pressed == 'CancelGroup') {
        cForm.method = "get";
        cForm.action = "grouplist";
        cForm.submit();
    } else if (param == 'UpdateGroup'
        || document.pressed == 'UpdateGroup') {
        cForm.method = "post";
        cForm.action = "update_group_mapping";
        cForm.submit();
    } else if (param == 'NewGroup'
        || document.pressed == 'NewGroup') {
        cForm.method = "get";
        cForm.action = "newgroup";
        cForm.submit();
    } else if (param == 'Roles'
        || document.pressed == 'Roles') {
        cForm.method = "get";
        cForm.action = "rolelist";
        cForm.submit();
    } else if (param == 'CreateRole'
        || document.pressed == 'CreateRole') {
        cForm.method = "post";
        cForm.action = "new_role";
        cForm.submit();
    } else if (param == 'DeleteRole'
        || document.pressed == 'DeleteRole') {
        cForm.method = "post";
        cForm.action = "delete_role";
        cForm.submit();

    } else if (param == 'CancelRole'
        || document.pressed == 'CancelRole') {
        cForm.method = "get";
        cForm.action = "rolelist";
        cForm.submit();
    } else if (param == 'NewRole'
        || document.pressed == 'NewRole') {
        cForm.method = "get";
        cForm.action = "newrole";
        cForm.submit();
    } else if (param == 'PreviousTenant'
        || document.pressed == 'PreviousTenant') {
        cForm.method = "get";
        cForm.action = "prevtenants";
        cForm.submit();
    } else if (param == 'NextTenant' || document.pressed == 'NextTenant') {
        cForm.method = "get";
        cForm.action = "nexttenants";
        cForm.submit();
    } else if (param == 'GetTenants' || document.pressed == 'GetTenants') {
        cForm.method = "get";
        cForm.action = "gettenants";
        cForm.submit();
    } else if (param == 'FilterTenant' || document.pressed == 'FilterTenant') {
        var filter = document.getElementById("filter");
        if (filter != null && filter.value != "" && filter.value.trim() != " ") {
            cForm.method = "get";
            cForm.action = "searchtenants";
            cForm.submit();
        }
    } else if (param == 'UpdateTenant' || document.pressed == 'UpdateTenant') {
        cForm.method = "post";
        cForm.action = "updatetenant";
        cForm.submit();
    } else {
        return false;
    }
}

$(document)
    .ready(
    function () {

        var index = 1;
        var i = $('.field').size();

        $('#add')
            .click(
            function () {
                domainParent = 'domainParent' + index;
                $(
                    '<div id="' + domainParent + '" class="domainParent"><input type="text" class="field" name="domains" id="domains" value="" "style=width:270" /><button name="domainsButton" id="domainsButton" onclick="removeDomain(\'' + domainParent + '\')">X</button></div>')
                    .fadeIn('slow').appendTo(
                        '#domainList');
                i++;
                index++;
            });
        $('#reset').click(function () {
            while (i > 1) {
                $('.domainParent:last').remove();
                i--;
            }
        });

    });