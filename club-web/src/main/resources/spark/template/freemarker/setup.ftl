<#include "formHeader.ftl">

<form method="post">
    <div class="inputForm">
    <fieldset class="inputGroup">
        <legend class="inputGroupLabel">Setup your club.</legend>
        <div class="inputGroupFields">
            <div class="inputField">
                <label for="organizationName">Organization Name:</label>
                <input type="text" size="50" name="organizationName" id="organizationName" value=""/>
            </div>
            <div class="inputField">
                <label for="program">Program:</label>
                <select name="program" id="program">
                <#list programs as program>
                    <option value="${program}">${program}</option>
                </#list>
                </select>
            </div>
            <div class="inputField">
                <label for="role">My role in this program is:</label>
                <select name="role" id="role">
                <#list roles as role>
                    <option value="${role}"/>${role}</option>
                </#list>
                </select>
            </div>
        </div>
    </fieldset>
    <div class="actions">
        <button type='submit'>Submit</button>
    </div>
    </div>
</form>
<#include "footer.ftl">
