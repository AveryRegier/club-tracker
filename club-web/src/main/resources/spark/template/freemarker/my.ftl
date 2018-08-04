<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/main.css" rel="stylesheet" type="text/css">
    <title>My Club-Tracker</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="/js-cookie.js"></script>
    <script lang="javascript">
        addEvent(window, 'load', function() {
          var found = false;
          for (var i = 0; i < document.forms[0].elements.length; i++) {
            var elm = document.forms[0].elements[i];
            if(elm.type == 'radio') {
                var value = Cookies.get(elm.name);
                if(value === elm.id) {
                    elm.checked = true;
                    found = true;
                }
            }
          }
          if(!found) {
              for (var i = 0; i < document.forms[0].elements.length; i++) {
                var elm = document.forms[0].elements[i];
                if(elm.type == 'radio' && elm.id.startsWith('tab')) {
                    elm.checked = true;
                    return;
                }
              }
          }
        });

        function selectTab(obj) {
            Cookies.set(obj.name, obj.id, { expires: 7, path: '/protected/my' });
        }

        function addEvent(obj, evType, fn){
            if (obj.addEventListener){
                obj.addEventListener(evType, fn, true);
                return true;
            } else if (obj.attachEvent){
                var r = obj.attachEvent("on"+evType, fn);
                return r;
            } else {
                return false;
            }
        }
    </script>
</head>
<body>

<form>
<div class="tabs">
<#if me.asParent().isPresent()>
    <div class="tab">
        <input type="radio" id="tab-1" name="tab-group-1" onchange="selectTab(this);">
        <label for="tab-1">My Family</label>

        <div class="content">
            <#include "parent.ftl">
        </div>
    </div>
</#if>
<#if me.asClubber().isPresent()>
    <div class="tab">
        <input type="radio" id="tab-2" name="tab-group-1" onchange="selectTab(this);">
        <label for="tab-2">My Progress</label>

        <div class="content">
            <#include "clubber.ftl">
        </div>
    </div>
</#if>
<#if me.asClubLeader().isPresent()>
    <div class="tab">
        <input type="radio" id="tab-3" name="tab-group-1" onchange="selectTab(this);">
        <label for="tab-3">My Club</label>

        <div class="content">
            <#include "leader.ftl">
        </div>
    </div>
</#if>
<#if me.asListener().isPresent()>
    <div class="tab">
        <input type="radio" id="tab-4" name="tab-group-1" onchange="selectTab(this);">
        <label for="tab-4">My Group</label>

        <div class="content">
            <#include "listener.ftl">
        </div>
    </div>
</#if>
</div>
</form>
<#include "footer.ftl">
