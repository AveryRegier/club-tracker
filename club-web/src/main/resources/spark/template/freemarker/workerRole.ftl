<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>Club Setup</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script type="text/javascript">
        <#include "forms.js">
    </script>
</head>
<body>

<form method="post">
    <div class="inputForm">
    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">Add leader to club.</legend>
        <div class="inputGroupFields">
            <div class="inputField">
                <label for="role">${person.name.fullName}'s role in ${club.shortCode} is:</label>
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
</div>
</body>
</html>