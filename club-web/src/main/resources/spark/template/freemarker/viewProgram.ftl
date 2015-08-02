<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>Program</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>

<form method="post">
    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">View your club.</legend>
        <div class="inputGroupFields">
            <div class="inputField">
                <label>Organization Name:</label>
                <div class="staticField">${program.shortCode}</div>
            </div>
            <div class="inputField">
                <label>Program:</label>
                <div class="staticField">${program.curriculum.shortCode}</div>
            </div>
            <div class="inputField">
                <label>Clubs</label>
                <div class="staticField">

                    <UL>
                    <#list program.clubs as club>
                        <LI><a href="/protected/club/${club.id}">${club.shortCode}</a></LI>
                    </#list>
                    </UL>

                </div>
            </div>
        </div>
    </fieldset>
</form>
</body>
</html>