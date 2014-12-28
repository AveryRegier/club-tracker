<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>Program</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">View your club.</legend>
        <div class="inputGroupFields">
            <div class="inputField">
                <label>Organization Name:</label>
                <div class="staticField">${club.shortName}</div>
            </div>
            <div class="inputField">
                <label>Program:</label>
                <div class="staticField">${club.curriculum.shortCode}</div>
            </div>
            <div class="inputField">
                <label>Listeners</label>
                <div class="staticField">
                    <UL>
                    <#list club.listeners as listener>
                        <LI>${listener.name.fullName}</LI>
                    </#list>
                    </UL>
                </div>
            </div>
            <div class="inputField">
                <label>Clubbers</label>
                <div class="staticField">
                    <UL>
                        <#list club.clubbers as clubber>
                            <LI>${clubber.name.fullName}</LI>
                        </#list>
                    </UL>
                </div>
            </div>
        </div>
    </fieldset>
</body>
</html>