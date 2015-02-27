<div class="menu"><a href="/protected/setup">Club Setup</a></div>
<#assign group=me.asClubLeader().get().club.get()>
<div class="menu"><a href="/protected/club/${group.shortCode}">Recruit Listeners</a></div>
<div class="menu"><a href="/protected/club/${group.shortCode}/awards">Awards</a></div>

<#include "clubberStatus.ftl">
