<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>Catchup</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script type="text/javascript">
        <#include "forms.js">
    </script>
</head>
<body>

<form method="post">
    <div class="inputForm">
    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">Catch-up</legend>
        <div class="inputGroupFields">
            <div class="inputField">
                <label>Clubber Name:</label>
                <div class="staticField">${clubber.name.fullName}</div>
            </div>
            <div class="inputField">
                <label for="listener">Listener:</label>
                <select name="listener" id="listener">
                <#list listeners as listener>
                    <option value="${listener.id}"<#if listener.id == defaultListener> default</#if>>${listener.name.fullName}</option>
                </#list>
                </select>
            </div>
            <div class="inputField">
                <label for="date">Approximate Sign Date:</label>
                <input type="date" name="date" id="date" value="${suggestedDate}"/>
            </div>
        </div>
    </fieldset>
    <div class="actions">
        <button type='submit'>Submit</button>
    </div>
    </div>
</form>
</div>
</body>
</html>