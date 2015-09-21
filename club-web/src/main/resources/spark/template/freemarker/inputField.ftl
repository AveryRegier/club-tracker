<#if descriptor.type = 'action'>
    <div class="action">
    <#list descriptor.values.get() as value>
        <button type="submit" value="${value.value}" name="${descriptor.id}">${value.displayName}</button>
    </#list>
    </div>
<#else>
    <div class="inputField">
    <label for="${descriptor.id}">${descriptor.name}</label>
    <#assign fieldValue = regInfo.fields[descriptor.id]!"">
    <#if descriptor.values.present>
        <select name="${descriptor.id}" id="${descriptor.id}">
            <option value="" selected disabled>Select...</option>
        <#list descriptor.values.get() as value>
            <option value="${value.value}"<#if value.value = fieldValue || value.default> selected</#if>>${value.displayName}</option>
        </#list>
        </select>
    <#elseif descriptor.type.name() == "date">
        <input type="date" name="${descriptor.id}" id="${descriptor.id}" value="${fieldValue}" onfocus="this.select()">
    <#else>
        <input type="text" name="${descriptor.id}" id="${descriptor.id}" value="${fieldValue}" onfocus="this.select()">
    </#if>
    </div>
</#if>