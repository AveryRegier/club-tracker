<div class="menu"><a href="/protected/club/${mygroup.id}/awards">Awards</a></div>

<#if mygroup.asProgram().isPresent()>
    <#assign program=mygroup.asProgram().get()>
    <div class="menu"><a href="/protected/program/${program.id}">Club Setup</a></div>
    <#include "clubMenu.ftl">
<#else>
    <#assign program=mygroup.program>
    <div class="menu"><a href=/protected/club/${mygroup.id}>${mygroup.shortCode}</a></a></div>
</#if>
<div class="menu"><a href="/protected/${program.id}/newClubber">Register New Clubber</a></div>

<#assign group=mygroup>
<#include "clubberStatus.ftl">
