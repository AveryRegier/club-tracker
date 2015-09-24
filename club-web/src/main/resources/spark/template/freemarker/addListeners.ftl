<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>Add ${club.shortCode} Listeners</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <div class="menu"><a href="/protected/${club.program.id}/newWorker">Register New Worker</a></div>

    <#assign people=club.program.personManager.people>
    <#include "people.ftl">
</body>
</html>