<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>Club Login</title>
    <script type="text/javascript">
        <#include "forms.js">
    </script>
</head>
<body>

    <form action='submitRegistration' method="post">
        <#list regInfo.form as descriptor>
            <#if descriptor.isGroup()>
                 <#include "inputGroup.ftl">
            <#elseif descriptor.isField()>
                 <#include "inputField.ftl">
            </#if>
        </#list>
        <br clear="all"/>
        <div class="actions"><input type="submit"></div>
    </form>
</body>
</html>
