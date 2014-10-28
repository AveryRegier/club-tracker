<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>Club Setup</title>
    <script type="text/javascript">
        <#include "forms.js">
    </script>
</head>
<body>

<form method="post">
    <div class="inputGroup">
        <div class="inputGroupLabel">Setup your club.</div>
        <div class="inputGroupFields">
            <div class="inputField">
                <label for="organizationName">Organization Name:</label>
                <input type="text" size="50" name="organizationName" id="organizationName" value=""/>
            </div>
            <div class="inputField">
                <label for="program">Program:</label>
                <select name="program" id="program">
                <#list programs as program>
                    <option value="${program}">${program}</option>
                </#list>
                </select>
            </div>
            <div class="inputField">
                <label for="role">My role in this program is:</label>
                <select name="role" id="role">
                <#list roles as role>
                    <option value="${role}"/>${role}</option>
                </#list>
                </select>
            </div>
        </div>
    </div>
    <div class="actions">
        <input type='submit'/>
    </div>
</form>
</div>
</body>
</html>