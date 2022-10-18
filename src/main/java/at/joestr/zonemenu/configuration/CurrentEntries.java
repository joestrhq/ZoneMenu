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
import java.util.Optional;

/**
 *
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
  LANG_CMD_ZONE_ADDMEMBER_PLAYER_DOES_NOT_EXIST("commands.zone-addmember.not_a_member"),
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
  PERM_CMD_CARTJETS("cartjets.commands.cartjets"),
  PERM_CMD_CARTJETS_SETUPWIZARD("cartjets.commands.cartjets-setupwizard"),
  PERM_CMD_CARTJETS_LIST("cartjets.commands.cartjets-list"),
  PERM_CMD_CARTJETS_DELETE("cartjets.commands.cartjets-delete"),
  PERM_CMD_CARTJETS_UPDATE("cartjets.commands.cartjets-update");

  private final String text;

  private CurrentEntries(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return this.text;
  }

  public static CurrentEntries find(String text) {
    Optional<CurrentEntries> result = Arrays.asList(values())
      .stream()
      .filter(cE -> cE.toString().equalsIgnoreCase(text))
      .findFirst();
    if (result.isPresent()) {
      return result.get();
    }
    throw new NullPointerException(MessageFormat.format("The text {0} is not in this Enum!", text));
  }

  public static CurrentEntries[] getGlobalEntries() {
    return new CurrentEntries[]{
      CONF_VERSION
    };
  }

  public static CurrentEntries[] getConfigurationEntries() {
    return new CurrentEntries[]{
      CONF_VERSION,
      CONF_JDBCURI,
      CONF_MAXSPEED,
      CONF_VECTORMULTIPLIER,
      CONF_TASKREPEATINGDELAYINTICKS,
      CONF_JENKINSUPDATER_ENABLED,
      CONF_JENKINSUPDATER_DOWNLOADTOPLUGINUPDATEFOLDER,
      CONF_JENKINSUPDATER_TARGETURL,
      CONF_JENKINSUPDATER_POMPROPERTIESFILE,
      CONF_JENKINSUPDATER_CLASSIFIER
    };
  }

  public static CurrentEntries[] getLanguageEntries() {
    return new CurrentEntries[]{
      LANG_PREFIX,
      LANG_GEN_NOT_A_PLAYER,
      LANG_CMD_CARTJETS_X_MSG_SETUPWIZARD,
      LANG_CMD_CARTJETS_X_MSG_DELETE,
      LANG_CMD_CARTJETS_X_MSG_LIST,
      LANG_CMD_CARTJETS_X_MSG_UPDATE,
      LANG_CMD_CARTJETS_SETUPWIZARD_BUTTON_INSTRUCTION,
      LANG_CMD_CARTJETS_SETUPWIZARD_BUTTON_SUCCESS,
      LANG_CMD_CARTJETS_SETUPWIZARD_BUTTON_OVERLAPPING,
      LANG_CMD_CARTJETS_SETUPWIZARD_RAIL_INSTRUCTION,
      LANG_CMD_CARTJETS_SETUPWIZARD_RAIL_SUCCESS,
      LANG_CMD_CARTJETS_SETUPWIZARD_NAME_ANVIL_GUI_TEXT,
      LANG_CMD_CARTJETS_SETUPWIZARD_NAME_PLACEHOLDER,
      LANG_CMD_CARTJETS_SETUPWIZARD_NAME_DUPLICATE,
      LANG_CMD_CARTJETS_SETUPWIZARD_NAME_SUCCESS,
      LANG_CMD_CARTJETS_SETUPWIZARD_CANCEL,
      LANG_CMD_CARTJETS_DELETE_NON_EXISTING,
      LANG_CMD_CARTJETS_DELETE_SUCCESS,
      LANG_CMD_CARTJETS_LIST_MESSAGE,
      LANG_CMD_CARTJETS_UPDATE_OFF,
      LANG_CMD_CARTJETS_UPDATE_ASYNCSTART,
      LANG_CMD_CARTJETS_UPDATE_ERROR,
      LANG_CMD_CARTJETS_UPDATE_UPTODATE,
      LANG_CMD_CARTJETS_UPDATE_AVAILABLE,
      LANG_CMD_CARTJETS_UPDATE_DOWNLOADED
    };
  }

  public static CurrentEntries[] getPermissionEntries() {
    return new CurrentEntries[]{
      PERM_CMD_CARTJETS,
      PERM_CMD_CARTJETS_SETUPWIZARD,
      PERM_CMD_CARTJETS_LIST,
      PERM_CMD_CARTJETS_DELETE,
      PERM_CMD_CARTJETS_UPDATE
    };
  }
}
