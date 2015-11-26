<#include "formHeader.ftl">

<#if mygroup.asProgram().isPresent()>
    <#assign program=mygroup.asProgram().get()>
    <div class="menu"><a href="/protected/program/${program.id}">Club Setup</a></div>
<#else>
    <#assign program=mygroup.program>
    <div class="menu"><a href=/protected/club/${mygroup.id}>${mygroup.shortCode}</a></a></div>
</#if>

<form method="post" action="/protected/admin/reset">
    <button type='submit'>Reset</button>
</form>
<#include "footer.ftl">
