<div class="inputGroup" name="${descriptor.id}">
    <div class="inputGroupLabel">${descriptor.name}</div>
    <div class="inputGroupFields">
        <#list descriptor.fieldDesignations as descriptor>
            <#if descriptor.isGroup()>
                 <#include "inputGroup.ftl">
            <#elseif descriptor.isField()>
                 <#include "inputField.ftl">
            </#if>
        </#list>
    </div>
</div>