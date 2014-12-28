<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>Program</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>

<#if me.asParent().isPresent()>
    <#include "parent.ftl">
</#if>
<#if me.asClubber().isPresent()>
    <#include "clubber.ftl">
</#if>
<#if me.asClubLeader().isPresent()>
    <#include "leader.ftl">
</#if>
<#if me.asListener().isPresent()>
    <#include "listener.ftl">
</#if>

</body>
</html>