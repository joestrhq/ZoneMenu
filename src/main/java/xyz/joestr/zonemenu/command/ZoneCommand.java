package xyz.joestr.zonemenu.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

import xyz.joestr.zonemenu.ZoneMenu;
import xyz.joestr.zonemenu.subcommand.SubCommandZoneRemovemember;
import xyz.joestr.zonemenu.subcommand.SubCommandZoneCancel;
import xyz.joestr.zonemenu.subcommand.SubCommandZoneCreate;
import xyz.joestr.zonemenu.subcommand.SubCommandZoneDelete;
import xyz.joestr.zonemenu.subcommand.SubCommandZoneFind;
import xyz.joestr.zonemenu.subcommand.SubCommandZoneFlag;
import xyz.joestr.zonemenu.subcommand.SubCommandZoneInfo;
import xyz.joestr.zonemenu.subcommand.SubCommandZoneAddmember;
import xyz.joestr.zonemenu.subcommand.SubCommandZoneSign;

@SuppressWarnings("deprecation")
public class ZoneCommand implements CommandExecutor {

	private ZoneMenu plugin = null;
	
	private SubCommandZoneFind subCommandZoneFind = null;
	private SubCommandZoneSign subCommandZoneSign = null;
	private SubCommandZoneCreate subCommandZoneCreate = null;
	private SubCommandZoneCancel subCommandZoneCancel = null;
	private SubCommandZoneAddmember subCommandZoneAddmember = null;
	private SubCommandZoneRemovemember subCommandZoneRemovemember = null;
	private SubCommandZoneFlag subCommandZoneFlag = null;
	private SubCommandZoneInfo subCommandZoneInfo = null;
	private SubCommandZoneDelete subCommandZoneDelete = null;

	public ZoneCommand(ZoneMenu zonemenu) {
		this.plugin = zonemenu;
		
		this.subCommandZoneFind = new SubCommandZoneFind(this.plugin);
		this.subCommandZoneSign = new SubCommandZoneSign(this.plugin);
		this.subCommandZoneCreate = new SubCommandZoneCreate(this.plugin);
		this.subCommandZoneCancel = new SubCommandZoneCancel(this.plugin);
		this.subCommandZoneAddmember = new SubCommandZoneAddmember(this.plugin);
		this.subCommandZoneRemovemember = new SubCommandZoneRemovemember(this.plugin);
		this.subCommandZoneFlag = new SubCommandZoneFlag(this.plugin);
		this.subCommandZoneInfo = new SubCommandZoneInfo(this.plugin);
		this.subCommandZoneDelete = new SubCommandZoneDelete(this.plugin);
		
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {

		// Check if sender is a player
		if (sender instanceof Player) {

			// Cast sender to a player
			final Player player = (Player) sender;

			// Check if player has the right permission
			if (player.hasPermission("zonemenu.*")) {

				// Check if arguments' length
				// /zone
				if (args.length < 1) {

					player.spigot().sendMessage(
							new ComponentBuilder(
									this.plugin.colorCode(
											"&",
											(String) this.plugin.configDelegate.Map().get("head") +
											(String) this.plugin.configDelegate.Map().get("head_extra")
									)
							)
							.append(
									"\n" +
									this.plugin.colorCode(
											"&",
											(String) this.plugin.configDelegate.Map().get("find")
									)
							)
							.event(
									new HoverEvent(
											HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder(
													this.plugin.colorCode(
															"&",
															(String) this.plugin.configDelegate.Map().get("find_hover")
													)
											)
											.create()
									)
							)
							.event(
									new ClickEvent(
											ClickEvent.Action.RUN_COMMAND,
											"/zone find"
									)
							)
							.append(
									"\n" +
									this.plugin.colorCode(
											"&",
											(String) this.plugin.configDelegate.Map().get("sign")
									)
							)
							.event(
									new HoverEvent(
											HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder(
													this.plugin.colorCode(
															"&",
															(String) this.plugin.configDelegate.Map().get("sign_hover")
													)
											)
											.create()
									)
							)
							.event(
									new ClickEvent(
											ClickEvent.Action.RUN_COMMAND,
											"/zone sign"
									)
							)
							.append(
									"\n" +
									this.plugin.colorCode(
											"&",
											(String) this.plugin.configDelegate.Map().get("create")
									)
							)
							.event(
									new HoverEvent(
											HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder(
													this.plugin.colorCode(
															"&",
															(String) this.plugin.configDelegate.Map().get("create_hover")
													)
											)
											.create()
									)
							)
							.event(
									new ClickEvent(
											ClickEvent.Action.RUN_COMMAND,
											"/zone create"
									)
							)
							.append(
									"\n" +
									this.plugin.colorCode(
											"&",
											(String) this.plugin.configDelegate.Map().get("cancel")
									)
							)
							.event(
									new HoverEvent(
											HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder(
													this.plugin.colorCode(
															"&",
															(String) this.plugin.configDelegate.Map().get("cancel_hover")
													)
											)
											.create()
									)
							)
							.event(
									new ClickEvent(
											ClickEvent.Action.RUN_COMMAND,
											"/zone cancel"
									)
							)
							.append(
									"\n" +
									this.plugin.colorCode(
											"&",
											(String) this.plugin.configDelegate.Map().get("addmember")
									)
							)
							.event(
									new HoverEvent(
											HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder(
													this.plugin.colorCode(
															"&",
															(String) this.plugin.configDelegate.Map().get("addmember_hover")
													)
											)
											.create()
									)
							)
							.event(
									new ClickEvent(
											ClickEvent.Action.SUGGEST_COMMAND,
											"/zone addmember "
									)
							)
							.append(
									"\n" +
									this.plugin.colorCode(
											"&",
											(String) this.plugin.configDelegate.Map().get("removemember")
									)
							)
							.event(
									new HoverEvent(
											HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder(
													this.plugin.colorCode(
															"&",
															(String) this.plugin.configDelegate.Map().get("removemember_hover")
													)
											)
											.create()
									)
							)
							.event(
									new ClickEvent(
											ClickEvent.Action.SUGGEST_COMMAND,
											"/zone removemember "
									)
							)
							.append(
									"\n" +
									this.plugin.colorCode(
											"&",
											(String) this.plugin.configDelegate.Map().get("info")
									)
							)
							.event(
									new HoverEvent(
											HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder(
													this.plugin.colorCode(
															"&",
															(String) this.plugin.configDelegate.Map().get("info_hover")
													)
											)
											.create()
									)
							)
							.event(
									new ClickEvent(
											ClickEvent.Action.RUN_COMMAND,
											"/zone info"
									)
							)
							.append(
									"\n" +
									this.plugin.colorCode(
											"&",
											(String) this.plugin.configDelegate.Map().get("flag")
									)
							)
							.event(
									new HoverEvent(
											HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder(
													this.plugin.colorCode(
															"&",
															(String) this.plugin.configDelegate.Map().get("flag_hover")
													)
											)
											.create()
									)
							)
							.event(
									new ClickEvent(
											ClickEvent.Action.SUGGEST_COMMAND,
											"/zone flag"
									)
							)
							.append(
									"\n" +
									this.plugin.colorCode(
											"&",
											(String) this.plugin.configDelegate.Map().get("delete")
									)
							)
							.event(
									new HoverEvent(
											HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder(
													this.plugin.colorCode(
															"&",
															(String) this.plugin.configDelegate.Map().get("delete_hover")
													)
											)
											.create()
									)
							)
							.event(
									new ClickEvent(
											ClickEvent.Action.SUGGEST_COMMAND,
											"/zone delete"
									)
							)
							.create()
					);
					return true;
				}

				// /zone find
				if ((args[0].equalsIgnoreCase("find")) && (args.length < 2)) {

					this.subCommandZoneFind.process(player);

					return true;
				}

				// /zoen sign
				if ((args[0].equalsIgnoreCase("sign")) && (args.length < 2)) {
					
					this.subCommandZoneSign.process(player);
					
					return true;
				}

				// /zone create
				if ((args[0].equalsIgnoreCase("create")) && (args.length < 2)) {

					this.subCommandZoneCreate.process(player);
					
					return true;
				}

				// /zone cancel
				if ((args[0].equalsIgnoreCase("cancel")) && (args.length < 2)) {

					this.subCommandZoneCancel.process(player);

					return true;
				}

				// /zone addmember <player>
				if ((args[0].equalsIgnoreCase("addmember")) && (args.length > 1) && (args.length < 3)) {

					this.subCommandZoneAddmember.process(player, args);

					return true;
				}

				// /zone removemember <player>
				if ((args[0].equalsIgnoreCase("removemember")) && (args.length > 1) && (args.length < 3)) {
					
					this.subCommandZoneRemovemember.process(player, args);
					
					return true;
				}

				// TODO: Implementig /zone flag <Flag> <Flag value ...> / done: seems complicated
				// TODO: reset flags (/zone flag <Flag> null?)
				// /zone flag <Flag> <Flag value...>
				if ((args[0].equalsIgnoreCase("flag")) && (args.length > 2) && (args.length < 4)) {
					
					this.subCommandZoneFlag.process(player, args);
					
					return true;
				}

				// /zone info
				if ((args[0].equalsIgnoreCase("info")) && (args.length < 2)) {

					this.subCommandZoneInfo.process(player);
					
					return true;
				}

				// /zone delete
				if ((args[0].equalsIgnoreCase("delete")) && (args.length < 2)) {

					this.subCommandZoneDelete.process(player);
					
					return true;
				}

				// Wrong usage of the /zone command
				player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
				player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("usage_message"))
						.replace("{0}", "/zone"));

				return true;
			}

			// Permission zonemenu.* is missing
			player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
			player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("permission_message"))
					.replace("{0}", "zonemenu.*"));

			return true;
		}

		// Console
		this.plugin.getServer().getConsoleSender()
				.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
		sender.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("console_message")));
		return true;
	}
}
