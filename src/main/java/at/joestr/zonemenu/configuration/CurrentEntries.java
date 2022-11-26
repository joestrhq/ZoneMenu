//
// Private License
//
// Copyright (c) 2019-2020 Joel Strasser <strasser999@gmail.com>
//
// Only the copyright holder is allowed to use this software.
//
package at.joestr.zonemenu.configuration;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Joel
 */
public enum CurrentEntries {

  // Configuration entries
  CONF_VERSION("version"),
  CONF_LENGTH_MIN("length_min"),
  CONF_LENGTH_MAX("length_max"),
  CONF_WIDTH_MIN("width_min"),
  CONF_WIDTH_MAX("width_max"),
  CONF_AREA_MIN("area_min"),
  CONF_AREA_MAX("area_max"),
  CONF_HAVE_MAX("have_max"),
  CONF_AREA_MAX_CLAIMABLE("area_max_claimable"),
  CONF_PRIORITY("priority"),
  CONF_ZONE_ID("zone_id"),
  CONF_SUBZONE_ID("subzone_id"),
  CONF_UPDATER_ENABLED("updater.enabled"),
  CONF_UPDATER_DOWNLOADTOPLUGINUPDATEFOLDER("updater.downloadToPluginUpdateFolder"),
  CONF_UPDATER_TARGETURL("updater.targetUrl"),
  CONF_UPDATER_POMPROPERTIESFILE("updater.pomPropertiesFile"),
  CONF_UPDATER_CLASSIFIER("updater.classifier"),
  // Languages
  LANG_VERSION("version"),
  LANG_PREFIX("prefix"),
  LANG_GEN_NOT_A_PLAYER("generic.not_a_player"),
  LANG_GEN_WAIT_MESSAGE("generic.wait_message"),
  LANG_GEN_NO_ZONE("generic.no_zone"),
  LANG_GEN_NOT_EXISTING_ZONE("generic.not_exisiting_zone"),
  LANG_CMD_ZONE_X_HEADER("commands.zone.header"),
  LANG_CMD_ZONE_X_MSG_FIND("commands.zone.message_find"),
  LANG_CMD_ZONE_X_MSG_CREATE("commands.zone.message_create"),
  LANG_CMD_ZONE_X_MSG_SUBCREATE("commands.zone.message_subcreate"),
  LANG_CMD_ZONE_X_MSG_LIST("commands.zone.message_list"),
  LANG_CMD_ZONE_X_MSG_CANCEL("commands.zone.message_cancel"),
  LANG_CMD_ZONE_X_MSG_ADDMEMBER("commands.zone.message_addmember"),
  LANG_CMD_ZONE_X_MSG_REMOVEMEMBER("commands.zone.message_removemember"),
  LANG_CMD_ZONE_X_MSG_FLAG("commands.zone.message_flag"),
  LANG_CMD_ZONE_X_MSG_INFO("commands.zone.message_info"),
  LANG_CMD_ZONE_X_MSG_DELETE("commands.zone.message_delete"),
  LANG_CMD_ZONE_X_MSG_SELECT("commands.zone.message_select"),
  LANG_CMD_ZONE_X_MSG_UPDATE("commands.zone.message_update"),
  LANG_CMD_ZONE_FIND_MESSAGE("commands.zone-find.message"),
  LANG_CMD_ZONE_CREATE_SIGN("commands.zone-create.sign"),
  LANG_CMD_ZONE_CREATE_NOT_SIGNED("commands.zone-create.not_signed"),
  LANG_CMD_ZONE_CREATE_WIDTH_LENGTH_LIMIT("commands.zone-create.width_length_limit"),
  LANG_CMD_ZONE_CREATE_AREA_UNDER("commands.zone-create.area_under"),
  LANG_CMD_ZONE_CREATE_AREA_OVER("commands.zone-create.area_over"),
  LANG_CMD_ZONE_CREATE_HAVE_OVER_EQUAL("commands.zone-create.have_over_equal"),
  LANG_CMD_ZONE_CREATE_OVERLAPS_UNOWNED("commands.zone-create.overlaps_unowned"),
  LANG_CMD_ZONE_CREATE_CREATED("commands.zone-create.created"),
  LANG_CMD_ZONE_SUBCREATE_SIGN("commands.zone-subcreate.sign"),
  LANG_CMD_ZONE_SUBCREATE_NOT_IN_ZONE("commands.zone-subcreate.not_in_zone"),
  LANG_CMD_ZONE_SUBCREATE_CIRCULAR("commands.zone-subcreate.circular"),
  LANG_CMD_ZONE_CANCEL_NOT_RUNNING("commands.zone-cancel.not_running"),
  LANG_CMD_ZONE_CANCEL_CANCEL("commands.zone-cancel.cancel"),
  LANG_CMD_ZONE_LIST_LIST("commands.zone-list.list"),
  LANG_CMD_ZONE_ADDMEMBER_PLAYER_DOES_NOT_EXIST("commands.zone-addmember.player_does_not_exist"),
  LANG_CMD_ZONE_ADDMEMBER_ALREADY_MEMBER("commands.zone-addmember.already_member"),
  LANG_CMD_ZONE_ADDMEMBER_SUCCESS("commands.zone-addmember.success"),
  LANG_CMD_ZONE_REMOVEMEMBER_NOT_A_MEMBER("commands.zone-removemember.not_a_member"),
  LANG_CMD_ZONE_REMOVEMEMBER_SUCCESS("commands.zone-removemember.success"),
  LANG_CMD_ZONE_FLAG_NOT_FOUND("commands.zone-flag.not_found"),
  LANG_CMD_ZONE_FLAG_CHANGED("commands.zone-flag.changed"),
  LANG_CMD_ZONE_INFO_ID("commands.zone-info.id"),
  LANG_CMD_ZONE_INFO_PRIORITY("commands.zone-info.priority"),
  LANG_CMD_ZONE_INFO_PARENT("commands.zone-info.parent"),
  LANG_CMD_ZONE_INFO_OWNERS("commands.zone-info.owners"),
  LANG_CMD_ZONE_INFO_MEMBERS("commands.zone-info.members"),
  LANG_CMD_ZONE_INFO_FLAG("commands.zone-info.flag"),
  LANG_CMD_ZONE_INFO_START("commands.zone-info.start"),
  LANG_CMD_ZONE_INFO_END("commands.zone-info.end"),
  LANG_CMD_ZONE_INFO_AREA("commands.zone-info.area"),
  LANG_CMD_ZONE_DELETE_SUCCESS("commands.zone-delete.success"),
  LANG_CMD_ZONE_SELECT_SUCCESS("commands.zone-select.success"),
  LANG_CMD_ZONE_UPDATE_OFF("commands.zone-update.off"),
  LANG_CMD_ZONE_UPDATE_ASYNCSTART("commands.zone-update.asyncstart"),
  LANG_CMD_ZONE_UPDATE_ERROR("commands.zone-update.error"),
  LANG_CMD_ZONE_UPDATE_UPTODATE("commands.zone-update.uptodate"),
  LANG_CMD_ZONE_UPDATE_AVAILABLE("commands.zone-update.available"),
  LANG_CMD_ZONE_UPDATE_DOWNLOADED("command.zone-update.downloaded"),
  LANG_EVT_FIND_FOUND("events.find.found"),
  LANG_EVT_FIND_NONE("events.find.none"),
  LANG_EVT_SIGN_FIRST("events.sign.first"),
  LANG_EVT_SIGN_SECOND("events.sign.second"),
  LANG_EVT_SIGN_AREA("events.sign.area"),
  // Permissions
  PERM_CMD_ZONE("zonemenu.commands.zone"),
  PERM_CMD_ZONE_FIND("zonemenu.commands.zone-find"),
  PERM_CMD_ZONE_CREATE("zonemenu.commands.zone-create"),
  PERM_CMD_ZONE_SUBCREATE("zonemenu.commands.zone-subcreate"),
  PERM_CMD_ZONE_CANCEL("zonemenu.commands.zone-cancel"),
  PERM_CMD_ZONE_ADDMEMBER("zonemenu.commands.zone-addmember"),
  PERM_CMD_ZONE_REMOVEMEMBER("zonemenu.commands.zone-removemember"),
  PERM_CMD_ZONE_FLAG("zonemenu.commands.zone-flag"),
  PERM_CMD_ZONE_INFO("zonemenu.commands.zone-info"),
  PERM_CMD_ZONE_DELETE("zonemenu.commands.zone-delete"),
  PERM_CMD_ZONE_SELECT("zonemenu.commands.zone-select"),
  PERM_CMD_ZONE_UPDATE("zonemenu.commands.zone-update");

  private final String text;

  private CurrentEntries(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return this.text;
  }

  public static CurrentEntries find(String text) {
    Optional<CurrentEntries> result =
        Arrays.asList(values()).stream()
            .filter(cE -> cE.toString().equalsIgnoreCase(text))
            .findFirst();
    if (result.isPresent()) {
      return result.get();
    }
    throw new NullPointerException(MessageFormat.format("The text {0} is not in this Enum!", text));
  }

  public static List<CurrentEntries> getGlobalEntries() {
    return List.of(CONF_VERSION, LANG_VERSION);
  }

  public static List<CurrentEntries> getConfigurationEntries() {
    return List.of(CurrentEntries.values()).stream()
        .filter(x -> x.name().startsWith("CONF"))
        .collect(Collectors.toList());
  }

  public static List<CurrentEntries> getLanguageEntries() {
    return List.of(CurrentEntries.values()).stream()
        .filter(x -> x.name().startsWith("LANG"))
        .collect(Collectors.toList());
  }

  public static List<CurrentEntries> getPermissionEntries() {
    return List.of(CurrentEntries.values()).stream()
        .filter(x -> x.name().startsWith("PERM"))
        .collect(Collectors.toList());
  }
}
