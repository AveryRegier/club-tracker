<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script type="text/javascript">
        <#include "forms.js">
    </script>
</head>
<body>
    <#include "header.ftl">
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
                <button name="submit" type='submit' value="submit">Submit</button>
            </div>
        </div>
    </form>

    <div>
    <p>Please see our <a href="/privacy.html">Privacy Policy</a> for how each field is used, and
        if you have any questions, contact the <a href="mailto:avery.regier@gmail.com">site owner</a>.</p>
    </div>
</body>
</html>
