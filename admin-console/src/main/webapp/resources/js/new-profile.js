function populateRoles() {
    var selectedTenant = $("#tenant").val();
    var roles = $("#roles");

    $.each(allAvailableRoles[selectedTenant], function(index, value) {
        roles.append($("<option>", { value : value }).text(value));
    });
}

$(function(){
    $("#tenant").change(populateRoles);

    populateRoles();
});