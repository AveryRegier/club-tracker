<div class="menu"><a href="/protected/setup">Club Setup</a></div>
<#assign group=me.asClubLeader().get().club.get()>
<div class="menu"><a href="/protected/club/${group.id}">Recruit Listeners</a></div>
<div class="menu"><a href="/protected/club/${group.id}/awards">Awards</a></div>
<div class="menu"><a href="/protected/${group.id}/newClubber">Register New Clubber</a></div>

<#include "clubberStatus.ftl">
