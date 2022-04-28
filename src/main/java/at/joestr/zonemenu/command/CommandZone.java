package at.joestr.zonemenu.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

import at.joestr.zonemenu.ZoneMenu;
import at.joestr.zonemenu.command.subcommand.SubCommandZoneAddmember;
import at.joestr.zonemenu.command.subcommand.SubCommandZoneCancel;
import at.joestr.zonemenu.command.subcommand.SubCommandZoneCreate;
import at.joestr.zonemenu.command.subcommand.SubCommandZoneDelete;
import at.joestr.zonemenu.command.subcommand.SubCommandZoneFind;
import at.joestr.zonemenu.command.subcommand.SubCommandZoneFlag;
import at.joestr.zonemenu.command.subcommand.SubCommandZoneInfo;
import at.joestr.zonemenu.command.subcommand.SubCommandZoneList;
import at.joestr.zonemenu.command.subcommand.SubCommandZoneRemovemember;
import at.joestr.zonemenu.command.subcommand.SubCommandZoneSubcreate;
import at.joestr.zonemenu.command.subcommand.SubCommandZoneUpdate;

public class CommandZone implements CommandExecutor {

    private ZoneMenu zoneMenuPlugin = null;

    private SubCommandZoneFind subCommandZoneFind = null;
    private SubCommandZoneCreate subCommandZoneCreate = null;
    private SubCommandZoneSubcreate subCommandZoneSubcreate = null;
    private SubCommandZoneCancel subCommandZoneCancel = null;
    private SubCommandZoneList subCommandZoneList = null;
    private SubCommandZoneAddmember subCommandZoneAddmember = null;
    private SubCommandZoneRemovemember subCommandZoneRemovemember = null;
    private SubCommandZoneFlag subCommandZoneFlag = null;
    private SubCommandZoneInfo subCommandZoneInfo = null;
    private SubCommandZoneDelete subCommandZoneDelete = null;
    private SubCommandZoneUpdate subCommandZoneUpdate = null;

    public CommandZone(ZoneMenu zonemenu) {
        this.zoneMenuPlugin = zonemenu;

        this.subCommandZoneFind = new SubCommandZoneFind(this.zoneMenuPlugin);
        this.subCommandZoneCreate = new SubCommandZoneCreate(this.zoneMenuPlugin);
        this.subCommandZoneSubcreate = new SubCommandZoneSubcreate(this.zoneMenuPlugin);
        this.subCommandZoneCancel = new SubCommandZoneCancel(this.zoneMenuPlugin);
        this.subCommandZoneList = new SubCommandZoneList(this.zoneMenuPlugin);
        this.subCommandZoneAddmember = new SubCommandZoneAddmember(this.zoneMenuPlugin);
        this.subCommandZoneRemovemember = new SubCommandZoneRemovemember(this.zoneMenuPlugin);
        this.subCommandZoneFlag = new SubCommandZoneFlag(this.zoneMenuPlugin);
        this.subCommandZoneInfo = new SubCommandZoneInfo(this.zoneMenuPlugin);
        this.subCommandZoneDelete = new SubCommandZoneDelete(this.zoneMenuPlugin);
        this.subCommandZoneUpdate = new SubCommandZoneUpdate(this.zoneMenuPlugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, final String[] args) {

        // If the command sender is not a player ...
        if (!(commandSender instanceof Player)) {

            // ... send the command sender a message and do not proceed.
            commandSender
                .sendMessage(this.zoneMenuPlugin.colorCode('&', ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                    + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("not_a_player_message"))));

            return true;
        }

        // Cast the command sender to a player
        final Player player = (Player) commandSender;

        // If the player does not have the permission 'zonemenu.*'
        if (!player.hasPermission("zonemenu.*")) {

            // ... send the player a message and do not proceed.
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("permission_message")).replace("{0}",
                    "zonemenu.*")));

            return true;
        }

        // Check arguments' length here
        // .length = 0 -> No sub command provided
        // /zone
        if (args.length == 0) {

            // Crappy click menu
            player.spigot()
                .sendMessage(new ComponentBuilder(
                    this.zoneMenuPlugin.colorCode('&',
                        ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                        + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("head"))))
                    .create());

            player.spigot()
                .sendMessage(new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                    + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("find")))).event(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                        ((String) this.zoneMenuPlugin.configDelegate.getMap().get("find_hover"))))
                        .create()))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zone find"))
                    .create());

            player.spigot().sendMessage(new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("create"))))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                        ((String) this.zoneMenuPlugin.configDelegate.getMap().get("create_hover"))))
                        .create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zone create")).create());

            player.spigot().sendMessage(new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("subcreate")))).event(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("subcreate_hover"))))
                    .create()))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/zone subcreate "))
                .create());

            player.spigot().sendMessage(new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("cancel"))))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                        ((String) this.zoneMenuPlugin.configDelegate.getMap().get("cancel_hover"))))
                        .create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zone cancel")).create());

            player.spigot().sendMessage(new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("list")))).event(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                    (String) this.zoneMenuPlugin.configDelegate.getMap().get("list_hover"))).create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zone list")).create());

            player.spigot().sendMessage(new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("addmember")))).event(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                    (String) this.zoneMenuPlugin.configDelegate.getMap().get("addmember_hover")))
                    .create()))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/zone addmember "))
                .create());

            player.spigot().sendMessage(new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("removemember")))).event(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("removemember_hover"))))
                    .create()))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/zone removemember "))
                .create());

            player.spigot()
                .sendMessage(new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                    + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("info")))).event(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                        ((String) this.zoneMenuPlugin.configDelegate.getMap().get("info_hover"))))
                        .create()))
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/zone info "))
                    .create());

            player.spigot()
                .sendMessage(new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                    + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("flag")))).event(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                        ((String) this.zoneMenuPlugin.configDelegate.getMap().get("flag_hover"))))
                        .create()))
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/zone flag "))
                    .create());

            player.spigot().sendMessage(new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("delete"))))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(this.zoneMenuPlugin.colorCode('&',
                        ((String) this.zoneMenuPlugin.configDelegate.getMap().get("delete_hover"))))
                        .create()))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/zone delete "))
                .create());

            return true;
        }

        // .length > 0 -> Maybe a sub command is provided
        // /zone find ...
        if (args[0].equalsIgnoreCase("find")) {

            // Continue the work
            this.subCommandZoneFind.process(player, args);

            return true;
        }

        // /zone create ...
        if (args[0].equalsIgnoreCase("create")) {

            // Continue the work
            this.subCommandZoneCreate.process(player, args);

            return true;
        }

        // /zone subcreate ...
        if (args[0].equalsIgnoreCase("subcreate")) {

            // Continue the work
            this.subCommandZoneSubcreate.process(player, args);

            return true;
        }

        // /zone cancel ...
        if (args[0].equalsIgnoreCase("cancel")) {

            // Continue the work
            this.subCommandZoneCancel.process(player, args);

            return true;
        }

        // /zone list ...
        if (args[0].equalsIgnoreCase("list")) {

            // Continue the work
            this.subCommandZoneList.process(player, args);

            return true;
        }

        // /zone addmember ...
        if (args[0].equalsIgnoreCase("addmember")) {

            // Continue the work
            this.subCommandZoneAddmember.process(player, args);

            return true;
        }

        // /zone removemember ...
        if (args[0].equalsIgnoreCase("removemember")) {

            // Continue the work
            this.subCommandZoneRemovemember.process(player, args);

            return true;
        }

        // /zone flag ...
        if (args[0].equalsIgnoreCase("flag")) {

            // Continue the work
            this.subCommandZoneFlag.process(player, args);

            return true;
        }

        // /zone info ...
        if (args[0].equalsIgnoreCase("info")) {

            // Continue the work
            this.subCommandZoneInfo.process(player, args);

            return true;
        }

        // /zone delete ...
        if (args[0].equalsIgnoreCase("delete")) {

            // Continue the work
            this.subCommandZoneDelete.process(player, args);

            return true;
        }

        // /zone update
        if (args[0].equalsIgnoreCase("update")) {

            // Continue the work
            this.subCommandZoneUpdate.process(player, args);

            return true;
        }

        // /zone reload
        if (args[0].equalsIgnoreCase("reload")) {

            // Continue the work
            this.zoneMenuPlugin.ymlDelegates.forEach((y) -> y.Load());

            return true;
        }

        // /zone update
        if (args[0].equalsIgnoreCase("update")) {

            // Continue the work
            this.subCommandZoneUpdate.process(player, args);

            return true;
        }

        // If we are here an invalid sub command was provided -> Wrong usage
        player.sendMessage(this.zoneMenuPlugin.colorCode('&', ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
            + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("usage_message")).replace("{0}", "/zone")));

        return true;
    }
}
