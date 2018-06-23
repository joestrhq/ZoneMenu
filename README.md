# ZoneMenu Dev  
This is the branch for development!

**Important**  
This plugin requires [WorldEdit](https://github.com/sk89q/WorldEdit) and [WorldGuard](https://github.com/sk89q/WorldGuard)!

**ZoneMenu**  
Simple, interactive menu for region creation with WorldGuard (and WorldEdit) for players. (Spigot)

**Commands (since build_5)**  
*/zone* - Display interactive menu.  
*/zone find* - Find zones.  
*/zone sign* - Sign the start- and ending point of your new zone.  
*/zone create* - Create a zone from your selection.  
*/zone cancel* - Cancel your zone creation.  
*/zone addmember \<Player\>* - Add a member to your zone.  
*/zone removemember \<Player\>* - Remove a member from your zone.  
*/zone flag \<Flag\> \<Flagvalue\>* - Change flags of your zone.  
*/zone info* - Get information about your zone.  
*/zone delete* - Delete your zone.  

**Permissions (since build_5)**  
*/zone* - zonemenu.\*

**Config entries (since build_7_pre_3)**  
**config.yml**  
```
config: <Map>
  head: <String>
  head_extra: <String>
  find: <String>
  find_hover: <String>
  sign: <String>
  sign_hover: <String>
  create: <String>
  create_hover: <String>
  cancel: <String>
  cancel_hover: <String>
  addmember: <String>
  addmember_hover: <String>
  removemember: <String>
  removemember_hover: <String>
  flag: <String>
  flag_hover: <String>
  info: <String>
  info_hover: <String>
  delete: <String>
  delete_hover: <String>
  zone_find: <String>
  zone_sign: <String>
  zone_create_already_have: <String>
  zone_create_not_signed: <String>
  zone_create_area_min: <Int>
  zone_create_area_max: <Int>
  zone_create_area_under: <String (can contain {0} -> zone_create_area_min)>
  zone_create_area_over: <String (can contain {0} -> zone_create_area_max)>
  zone_create_overlaps_unowned: <String>
  zone_create_priority: <Int>
  zone_create: <String>
  zone_cancel_not_running: <String>
  zone_cancel: <String>
  no_zone: <String>
  zone_addmember_not_existing: <String (can contain {0} -> Given playername)>
  zone_addmember_already_member: <String (can contain {0} -> Given playername)>
  zone_addmember: <String (can contain {0} -> Given playername)>
  zone_removemember_unknownplayer: <String (can contain {0} -> Given playername)>
  zone_removemember: <String (can contain {0} -> Given playername)>
  zone_flag_not_found: <String (can contain {0} -> Flag name)>
  zone_flag_changed: <String (can contain {0} -> Flag name, {1} -> Flag value)>
  zone_info_name_id: <String (can contain {name} -> Name format, {id} -> Worldguard region id)>
  zone_info_priority: <String>
  zone_info_owners: <String>
  zone_info_members: <String>
  zone_info_flag: <String (can contain {0} -> Flag name, {1} -> Flag value)>
  zone_info_start: <String (can contain {0} -> X-Coordinate, {1} -> Z-Coordinate)>
  zone_info_end: <String (can contain {0} -> X-Coordinate, {1} -> Z-Coordinate)>
  zone_info_area: <String (can contain {0} -> Area)>
  zone_delete: <String>
  usage_message: <String (can contain {0} -> Coresponding command)>
  permission_message: <String (can contain {0} -> Coresponding Permission)>
  event_find_multi: <String (can contain {names} -> Name formats, {id} -> Worldguard region ids)>
  event_find_no: <String>
  event_find: <String (can contain {names} -> Name formats, {ids} -> Worldguard region ids)>
  event_sign_first: <String>
  event_sign_second: <String>
  event_sign_area: <String (can contain {0} -> Area)>
  zone_wait_message: <String>
  console_message: <String>
```
**id.yml**  
```
id: <Map>
  zone_id: <String>
  zone_id_counter: <Int>
```
**name.yml**  
```
name: <Map>
  name_format: <String (can contain {owner} -> First region owner, {id_counter} -> zone_id_counter value on creation)'
```

**Removed**  
**Version 0.0.3**  
*/zone unbind* - Unbind your stick. (Removed in version 0.0.3)  
*unbind: \<String\>* (Removed in version 0.0.3)  
*unbind_hover: \<String\>* (Removed in version 0.0.3)

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

**Note**  
Since version 0.0.3 missing config entries will be automatically placed while older versions (0.0.1, 0.0.2) don't do that.

**Version history**  
0.0.1; 0.0.2; 0.0.3; 0.0.4; build\_5; build\_6; build\_7\_pre\_1; build\_7\_pre\_2; *build\_7\_pre\_3 latest*

**Builds**  
Builds are available [here](https://jenkins.joestr.xyz/job/ZoneMenu_Dev/).
