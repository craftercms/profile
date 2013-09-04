function removeDomain(param) {
    document.getElementById(param).remove();
}
function onsubmitform(param) {
    var cForm = document.getElementById('form-list');
    if (cForm == null || cForm == undefined) {
        cForm = document.getElementById('form-item');
    }
    if (param == 'DeleteProp'
        || document.pressed == 'DeleteProp') {
        var checklist = document.getElementsByName("item");
        var selectedItems = false;
        for (var j = 0; j < checklist.length; j++) {
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
    function () {

        var i = $('.field').size();
        var index = 1;
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

        $('#backTenant').click(function () {
            window.close();
        });

        $('#selectedTenantName').change(function () {
            this.form.submit();
        });
    });
