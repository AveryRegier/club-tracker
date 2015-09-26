<div class="menu"><a href="/protected/club/${mygroup.id}/awards">Awards</a></div>

<#if mygroup.asProgram().isPresent()>
    <#assign program=mygroup.asProgram().get()>
    <div class="menu"><a href="/protected/program/${program.id}">Club Setup</a></div>
    <div class="menu"><a href="/protected/${program.id}/newClubber">Register New Clubber</a></div>
<#else>
    <#assign program=mygroup.program>
</#if>
<#include "clubMenu.ftl">

<#assign group=mygroup>
<#include "clubberStatus.ftl">
