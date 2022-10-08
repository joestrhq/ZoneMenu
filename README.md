# ZoneMenu

**Important**  
This plugin requires [WorldEdit](https://github.com/sk89q/WorldEdit) and [WorldGuard](https://github.com/sk89q/WorldGuard)!

**ZoneMenu**  
Simple, interactive menu for region creation with WorldGuard (and WorldEdit) for players. (Spigot)

**Commands (since build_8)**  
*/zone* - Display interactive menu.  
*/zone find* - Find zones.  
*/zone create* - Create a zone from your selection.  
*/zone subcreate \<Zone\>* - Create a subzone.
*/zone cancel* - Cancel a zone creation.  
*/zone list* - List all your zones.
*/zone addmember \<Zone\>\<Player\>* - Add a member to a zone.  
*/zone removemember \<Zone\> \<Player\>* - Remove a member from a zone.  
*/zone flag \<Zone\> \<Flag\> \<Flagvalue\>* - Change flags of a zone.  
*/zone info \<Zone\>* - Get information about a zone.  
*/zone delete \<Zone\>* - Delete a zone.  
*/zone update* - Check for an update

**Permissions (since build_5)**  
*/zone* - zonemenu.\*

**Config entries (since build_9)**  
**config.yml**  
```
config:
  head: "&a--- ZoneMenu ---"
  head_extra: " &b(Options are clickable)"
  find: "&b>> Look for a free place."
  find_hover: "&b/zone find"
  create: "&b>> Create a new zone."
  create_hover: "&b/zone create"
  subcreate: "&b>> Create a new subzone."
  subcreate_hover: "&b/zone subcreate <Zone>"
  list: "&b>> List all your zones."
  list_hover: "&b/zone list"
  cancel: "&b>> Cancel creation."
  cancel_hover: "&b/zone cancel"
  addmember: "&b>> Add a player to your zone."
  addmember_hover: "&b/zone addmember <Zone> <Player>"
  removemember: "&b>> Remove a player from your zone."
  removemember_hover: "&b/zone removemember <Zone> <Player>"
  flag: "&b>> Modify the flag of your zone."
  flag_hover: "&b/zone flag <Zone> <Flag> <Flagvalue>"
  info: "&b>> Get information about your zone."
  info_hover: "&b/zone info <Zone>"
  delete: "&b>> Delete your zone."
  delete_hover: "&b/zone delete <Zone>"
  zone_find: "&bUse the stick to look for a free place by &aleft- &band &aright-click&b."
  zone_create_sign: "&bUse the stick to sign the &5first &bpoint by &aleft- &band the &dsecond &bpoint by &aright-click&b."
  zone_create_not_signed: "&cSign a zone first."
  zone_create_area_min: 100
  zone_create_area_max: 10000
  zone_create_area_under: "&cMinimum area: &b{0}"
  zone_create_area_over: "&cMaximum area: &b{0}"
  zone_create_have_max: 5
  zone_create_have_over_equal: "&cYou are already having &b{count}&c (out of &b{zone_create_have_max}&c) zone(s) in this world."
  zone_create_area_max_claimable: 10000
  zone_create_area_max_claimable_over: "&cYou are already having an area of &b{area}&c block(s) spread over &b{count}&c zone(s)."
  zone_create_overlaps_unowned: "&cYour selection overlaps with one or more unowned zones."
  zone_create_priority: 1
  zone_create: "&bZone &a{0} &bcreated."
  zone_subcreate_sign: "&bUse the stick to sign the first point by &aleft- &band the second point by &aright-click&b."
  zone_subcreate_not_in_zone: "&cYour subzone is not completely in zone &b{0}&a."
  zone_subcreate_circular: "&cThere was an circular inheritance exception."
  zone_subcreate: "&bSubzone &a{0} &bcreated."
  zone_cancel_not_running: "&cZone creation not running."
  zone_cancel: "&bZone creation cancelled."
  zone_list: "&bList of all your zones: &a{list}"
  zone_addmember_not_existing: "&b{0} &cdoes not exist."
  zone_addmember_already_member: "&b{0} &cis already a member at zone &b{1}."
  zone_addmember: "&a{0} &bis now a member of zone &a{1}."
  zone_removemember_unknownplayer: "&b{0} &cis not a member at zone &b{1}."
  zone_removemember: "&a{0} &bis no longer a member at zone &b{1}."
  zone_flag_not_found: "&cFlag &b{0} &cdoes not exist."
  zone_flag_changed: "&bFlag &a{0} &b at zone &a{1} changed to &a{2}&b."
  zone_info_id: "&bName: &a{id}"
  zone_info_priority: "&bPriority: &a"
  zone_info_parent: "&bParent: &a"
  zone_info_owners: "&bOwners: &a"
  zone_info_members: "&bMembers: &a"
  zone_info_flag: "&bFlag: &a{0} - &bFlagvalue: &a{1}"
  zone_info_start: "&5Starting &bpoint: &aX: {0} &b/ &aZ: {1}"
  zone_info_end: "&dEnding &bpoint: &aX: {0} &b/ &aZ: {1}"
  zone_info_area: "&bArea: &a{0}"
  zone_delete: "&bZone &a{0} &bdeleted."
  event_find_multi: "&bZones found: &a{ids}"
  event_find_no: "&cNo zones found."
  event_find: "&bZone found: &a{ids}"
  event_sign_first: "&5First &bposition marked."
  event_sign_second: "&dSecond &bposition marked."
  event_sign_area: " &bArea: &a{0}"
  zone_wait_message: "&aZoneMenu &c- &bPlease wait ..."
  no_zone: "&cYou do not have any zone in this world."
  not_exisiting_zone: "&cZone &b{0} &cdoes not exist."
  usage_message: "&cUsage: &b{0}"
  permission_message: "&cLacking permission: &b{0}"
  not_a_player_message: "&cNot a player?"
```
**id.yml**  
```
id:
  zone_id: '{creator}_{count}'
  subzone_id: '{parent}/{count}'
```

**Removed**  
**Version 0.0.3**  
*/zone unbind* - Unbind your stick. (Removed in version 0.0.3)  
*unbind: \<String\>* (Removed in version 0.0.3)  
*unbind_hover: \<String\>* (Removed in version 0.0.3)
**Version build_8**  
*/zone sign* - Sign a zone.
*sign: <String>* - Sign message.
*sign_hover: <String>* - Hover on the sign message.
*zone_sign: <String>* - Shows message how to use the sign tool.
*zone_id: <String>* - Defines a format for Worldguard regions.
*zone_id_counter: <Int>* - Counts up every zone created.

**Changed**  
**Version 0.0.3**  
*zone_area_limit: \<Int\>* - Maximum area a user can claim. (Replaced by *zone_area_max: \<Int\>* in version 0.0.3)  
**Version 0.0.4**  
*zone_in_world: \<String\>* (Replaced by *zone_create_already_have: \<String\>* in version 0.0.4)  
*zone_not_sign: \<String\>* (Replaced by *zone_create_not_signed: \<String\>* in version 0.0.4)  
*zone_not_sign: \<String\>* (Replaced by *zone_create_not_signed: \<String\>* in version 0.0.4)  
*zone_area_min: \<Int\>* (Replaced by *zone_create_area_min: \<Int\>* in version 0.0.4)  
*zone_area_max: \<Int\>* (Replaced by *zone_create_area_max: \<Int\>* in version 0.0.4)  
*zone_area_under: \<String\>* (Replaced *by zone_create_area_under: \<String\>* in version 0.0.4)  
*zone_area_over:\<String\>* (Replaced by *zone_create_area_over: \<String\>* in version 0.0.4)  
*zone_overlap: \<String\>* (Replaced by *zone_create_overlaps_unowned: \<String\>* in version 0.0.4)  
*zone_addmember_unkownplayer: \<String\>* (Replaced by *zone_addmember_not_existing: \<String\>* in version 0.0.4)  
*console_msg: \<String\>* (Replaced by *console_message: \<String\>* in version 0.0.4)  
*zone_id_search: \<String\>* (Replaced by *zone_wait_message: \<String\>* in version 0.0.4)  
*zone_id: \<String\>* (Exported to *id.yml* in version 0.0.4)  
*zone_id_counter: \<Int\>* (Exported to *id.yml* in version 0.0.4)  
**Version build\_7\_pre\_3**  
*zone_info_id: \<String\>* - WorldGuard region id showed in /zone info (Replaced by *zone_info_name_id: \<String (can contain {name} -> Name format, {id} -\> Worldguard region id)\>* in build\_7\_pre\_3)  
**Version build\_9**
*console_message: \<String\>* (Replaced by *not_a_player_message: \<String\>* in version build\_9) 

**Note**  
Since version 0.0.3 missing config entries will be automatically placed while older versions (0.0.1, 0.0.2) don't do that.

**Version history**  
0.0.1; 0.0.2; 0.0.3; 0.0.4; build\_5; build\_6; build\_7\_pre\_1; build\_7\_pre\_2; build\_7\_pre\_3; build\_8; build\_9 latest; 0.1.0; __latest__: *0.1.2-SNAPSHOT.jar*

**Wiki**

Wiki is located [here](https://github.com/joestr/ZoneMenu/wiki).

**Report Bugs, Features etc.**

The bugtracker is located [here](https://github.com/joestr/ZoneMenu/issues).

**Builds**  
Builds are available [here](https://ci.joestr.at/job/github.com_joestr/job/ZoneMenu-develop/).
