<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>Add Login Provider</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script type="text/javascript">
        <#include "forms.js">
    </script>
</head>
<body>

<form method="post">
    <div class="inputForm">
    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">Add Login Provider</legend>
        <div class="inputGroupFields">
            <div class="inputField">
                <label for="providerId">ID:</label>
                <input type="text" size="50" name="providerId" id="providerId" value=""/>
            </div>
            <div class="inputField">
                <label for="providerName">Name:</label>
                <input type="text" size="50" name="providerName" id="providerName" value=""/>
            </div>
            <div class="inputField">
                <label for="image">Image:</label>
                <input type="text" size="50" name="image" id="image" value=""/>
            </div>
            <div class="inputField">
                <label for="site">Site:</label>
                <input type="text" size="50" name="site" id="site" value=""/>
            </div>
            <div class="inputField">
                <label for="clientKey">Client Key:</label>
                <input type="text" size="50" name="clientKey" id="clientKey" value=""/>
            </div>
            <div class="inputField">
                <label for="clientSecret">Client Secret:</label>
                <input type="text" size="50" name="clientSecret" id="clientSecret" value=""/>
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