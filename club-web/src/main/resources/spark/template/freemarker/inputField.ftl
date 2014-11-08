<#if descriptor.type = 'action'>
    <div class="action">
    <#list descriptor.values.get() as value>
        <input type="submit" value="${value.value}" name="${value.displayName}"/>
    </#list>
    </div>
<#else>
    <div class="inputField">
    <label for="${descriptor.id}">${descriptor.name}</label>
    <#assign fieldValue = regInfo.fields[descriptor.id]!"">
    <#if descriptor.values.present>
        <select name="${descriptor.id}" id="${descriptor.id}">
        <#list descriptor.values.get() as value>
            <option value="${value.value}"<#if value.value = fieldValue || value.default> default</#if>>${value.displayName}</option>
        </#list>
        </select>
    <#else>
        <input type="text" name="${descriptor.id}" id="${descriptor.id}" value="${fieldValue}" onfocus="this.select()">
    </#if>
    </div>
</#if>