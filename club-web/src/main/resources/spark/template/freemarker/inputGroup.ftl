<fieldset class="inputGroup" name="${descriptor.id}">
    <legend class="inputGroupLabel">${descriptor.name}</legend>
    <div class="inputGroupFields">
        <#list descriptor.fieldDesignations as descriptor>
            <#if descriptor.isGroup()>
                 <#include "inputGroup.ftl">
            <#elseif descriptor.isField()>
                 <#include "inputField.ftl">
            </#if>
        </#list>
    </div>
</fieldset>