<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>Club Login</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script type="text/javascript">
        <#include "forms.js">
    </script>
</head>
<body>

    <form method="post">
        <div class="inputForm">
            <#list regInfo.form as descriptor>
                <#if descriptor.isGroup()>
                     <#include "inputGroup.ftl">
                <#elseif descriptor.isField()>
                     <#include "inputField.ftl">
                </#if>
            </#list>
            <div class="actions">
                <button type='submit'>Submit</button>
            </div>
        </div>
    </form>
</body>
</html>
