<div class="menu"><a href=/protected/club/${program.id}>${program.shortCode}</a></a></div>
<div class="subMenu">
    <#list program.clubs as club>
        <div class="menu"><a href=/protected/club/${club.id}>${club.shortCode}</a></a></div>
    </#list>
</div>