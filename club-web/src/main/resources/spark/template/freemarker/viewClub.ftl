<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>Program</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <div class="menu"><a href=/protected/club/${club.id}/workers>Recruit ${club.shortCode} Workers</a></a></div>

    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">View your club.</legend>
        <div class="inputGroupFields">
            <div class="inputField">
                <label>Organization Name:</label>
                <div class="staticField">${club.program.shortCode}</div>
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
                        <LI>
                            <#if listener.club.isPresent() && listener.family.isPresent()>
                                 <a href="/protected/${listener.club.get().program.id}/family/${listener.family.get().id}">${listener.name.fullName}</a>
                            <#else>
                                ${listener.name.fullName}
                            </#if>
                        </LI>
                    </#list>
                    </UL>
                </div>
            </div>
            <div class="inputField">
                <label>Clubbers</label>
                <div class="staticField">
                    <UL>
                        <#list clubbers as entry>
                            <#assign clubber = entry.key>
                            <LI>${clubber.name.fullName} - <a href="/protected/clubbers/${clubber.id}/sections">${entry.value} Sections</a></LI>
                        </#list>
                    </UL>
                </div>
            </div>
        </div>
    </fieldset>
</body>
</html>