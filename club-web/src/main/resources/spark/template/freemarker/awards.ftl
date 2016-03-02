<#include "normalHeader.ftl">
    <form method="post">
        <header>${club.shortCode} Awards</header>
        <div class="list">
            <header class="list-header">
                <div class="list-item-checkbox"></div>
                <nav class="list-item-content">
                    <button name="submit" type='submit' value="submit" class="primary">Mark Presented</button>
                </nav>
            </header>
            <#list awards as presentation>
                <#include "singleAward.ftl">
            </#list>
        </div>
    </form>
<#include "footer.ftl">
