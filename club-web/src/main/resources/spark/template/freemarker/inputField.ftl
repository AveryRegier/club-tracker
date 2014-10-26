<div class="inputField">
    <label>${descriptor.name}</label>
    <#assign fieldValue = regInfo.fields[descriptor.id]!"">
    <#if descriptor.values.present>
        <select name="${descriptor.id}">
        <#list descriptor.values.get() as value>
            <option value="${value.value}"<#if value.value = fieldValue || value.default> default</#if>>${value.displayName}</option>
        </#list>
        </select>
    <#else>
        <input type="text" name="${descriptor.id}" value="${fieldValue}">
    </#if>
</div>