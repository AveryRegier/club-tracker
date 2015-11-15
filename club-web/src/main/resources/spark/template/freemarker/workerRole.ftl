<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script type="text/javascript">
        <#include "forms.js">

        function updateForm() {
            var selectBox = document.getElementById("club");
            var selectedValue = selectBox.options[selectBox.selectedIndex].value;
            document.forms[0].action = '/protected/club/'+selectedValue='/workers/${person.id}';
        }
    </script>
</head>
<body>
<#include "header.ftl">

<form method="post">
    <div class="inputForm">
    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">Add leader to club.</legend>
        <div class="inputGroupFields">
            <div class="inputField">
                <label for="role">${person.name.fullName}'s role in
                <#if !clubs??>${club.shortCode}<#else>
                    <select name="club" id="club" onchange="updateForm()">
                        <#list clubs as current>
                            <option value="${current.id}"/>${current.shortCode}</option>
                        </#list>
                    </select>
                </#if> is:</label>
                <select name="role" id="role">
                    <option value="listener" default>Listener</option>
                <#list roles as role>
                    <option value="${role}"/>${role}</option>
                </#list>
                </select>
            </div>
        </div>
    </fieldset>
    <div class="actions">
        <a href="/protected/club/${club.id}" class='button'>Cancel</a>
        <button type='submit'>Submit</button>
    </div>
    </div>
</form>
<#include "footer.ftl">
