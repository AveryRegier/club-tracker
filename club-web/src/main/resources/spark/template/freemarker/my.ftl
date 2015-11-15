<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>My Club-Tracker</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>

<div class="tabs">
<#if me.asParent().isPresent()>
    <div class="tab">
        <input type="radio" id="tab-1" name="tab-group-1" checked>
        <label for="tab-1">My Family</label>

        <div class="content">
            <#include "parent.ftl">
        </div>
    </div>
</#if>
<#if me.asClubber().isPresent()>
    <div class="tab">
        <input type="radio" id="tab-2" name="tab-group-1">
        <label for="tab-2">My Progress</label>

        <div class="content">
            <#include "clubber.ftl">
        </div>
    </div>
</#if>
<#if me.asClubLeader().isPresent()>
    <div class="tab">
        <input type="radio" id="tab-3" name="tab-group-1">
        <label for="tab-3">My Club</label>

        <div class="content">
            <#include "leader.ftl">
        </div>
    </div>
</#if>
<#if me.asListener().isPresent()>
    <div class="tab">
        <input type="radio" id="tab-4" name="tab-group-1">
        <label for="tab-4">My Group</label>

        <div class="content">
            <#include "listener.ftl">
        </div>
    </div>
</#if>
</div>
<#include "footer.ftl">
