<#list programs as program>
<div class="menu"><a href="/protected/${program.id}/family">Update ${program.shortCode} Registration</a></div>
</#list>
<#if me.asParent().get().getFamily().isPresent()>
    <#assign group=me.asParent().get().getFamily().get()>
    <#include "childStatus.ftl">
</#if>

