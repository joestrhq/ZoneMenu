package xyz.joestr.zonemenu.command;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.Executors;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.EnumFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.util.DomainInputResolver;
import com.sk89q.worldguard.protection.util.DomainInputResolver.UserLocatorPolicy;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import xyz.joestr.zonemenu.ZoneMenu;
import xyz.joestr.zonemenu.enumeration.ZoneMenuTool;

@SuppressWarnings("deprecation")
public class ZoneCommand implements CommandExecutor {

	private ZoneMenu plugin;

	public ZoneCommand(ZoneMenu zonemenu) {
		this.plugin = zonemenu;
	}

	@SuppressWarnings({ "unchecked" })
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

					// Check if players inventory contains a stick
					if (!player.getInventory().contains(Material.STICK)) {

						// Add a stick to players inventory
						player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.STICK, 1) });
					}

					// Send player a message
					player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
					player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("zone_find")));

					// Put player into a map where his name and the current action are stored
					this.plugin.tool.put(player, ZoneMenuTool.FIND);

					return true;
				}

				// /zoen sign
				if ((args[0].equalsIgnoreCase("sign")) && (args.length < 2)) {

					// Check if players inventory contains a stick
					if (!player.getInventory().contains(Material.STICK)) {

						// Add a stick to players inventory
						player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.STICK, 1) });
					}

					// Send player a message
					player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
					player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("zone_sign")));

					// Put player into a map where his name and the current action are stored
					this.plugin.tool.put(player, ZoneMenuTool.SIGN);

					return true;
				}

				// /zone create
				if ((args[0].equalsIgnoreCase("create")) && (args.length < 2)) {

					try {

						if (this.plugin.getRegion(player) != null) {

							player.sendMessage(
									this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
							player.sendMessage(this.plugin.colorCode('&',
									(String) this.plugin.configDelegate.Map().get("zone_create_already_have")));
							return true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					// Grab players worldedit selection
					Selection selectedregion = this.plugin.getWorldEditPlugin().getSelection(player);

					// Check if selection is valid
					if (selectedregion == null) {

						player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
						player.sendMessage(this.plugin.colorCode('&',
								(String) this.plugin.configDelegate.Map().get("zone_create_not_signed")));
						return true;
					}

					// Check if selected area is smaller than the specified maximum area in the
					// config file
					if (selectedregion.getWidth() * selectedregion.getLength() < Integer
							.parseInt(this.plugin.configDelegate.Map().get("zone_create_area_min").toString())) {

						player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
						player.sendMessage(this.plugin
								.colorCode('&', (String) this.plugin.configDelegate.Map().get("zone_create_area_under"))
								.replace("{0}", this.plugin.configDelegate.Map().get("zone_create_area_min").toString()));

						return true;
					}

					// Check if selected area is larger than the specified minimum area in the
					// config file
					if (selectedregion.getWidth() * selectedregion.getLength() > Integer
							.parseInt(this.plugin.configDelegate.Map().get("zone_create_area_max").toString())) {

						player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
						player.sendMessage(this.plugin
								.colorCode('&', (String) this.plugin.configDelegate.Map().get("zone_create_area_over"))
								.replace("{0}", this.plugin.configDelegate.Map().get("zone_create_area_max").toString()));

						return true;
					}

					// Grab some values to work with
					Location min = selectedregion.getMinimumPoint();
					Location max = selectedregion.getMaximumPoint();
					double first_x = min.getX();
					double first_y = min.getY();
					double first_z = min.getZ();
					double second_x = max.getX();
					double second_y = max.getY();
					double second_z = max.getZ();

					// Create a new WorldGuard region
					ProtectedCuboidRegion protectedcuboidregion = new ProtectedCuboidRegion(
							(String) this.plugin.idDelegate.Map().get("zone_id")
									+ this.plugin.idDelegate.Map().get("zone_id_counter").toString(),
							new BlockVector(first_x, first_y, first_z), new BlockVector(second_x, second_y, second_z));

					// Increment the region id counter
					this.plugin.idDelegate.Map().put("zone_id_counter",
							Integer.parseInt(this.plugin.idDelegate.Map().get("zone_id_counter").toString()) + 1);
					this.plugin.idDelegate.Save();

					// Check if region overlaps with unowned regions
					if (this.plugin.worldGuardPlugin.getRegionManager(player.getWorld()).overlapsUnownedRegion(
							protectedcuboidregion, this.plugin.worldGuardPlugin.wrapPlayer(player))) {

						player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
						player.sendMessage(this.plugin.colorCode('&',
								(String) this.plugin.configDelegate.Map().get("zone_create_overlaps_unowned")));

						return true;
					}

					// Check if Worldguards profileservice contains players name
					ProfileService ps = this.plugin.worldGuardPlugin.getProfileService();
					try {
						ps.findByName(player.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}

					// Create a new domain
					DefaultDomain domain = new DefaultDomain();
					// Wrap player and add it to the domain
					domain.addPlayer(this.plugin.worldGuardPlugin.wrapPlayer(player));
					// Apply the domain to owners
					protectedcuboidregion.setOwners(domain);
					// Set the priority to the specified value in the config file
					protectedcuboidregion.setPriority(
							Integer.parseInt(this.plugin.configDelegate.Map().get("zone_create_priority").toString()));

					// Some flags
					/*
					 * ProtectRegion.setFlag(DefaultFlag.CREEPER_EXPLOSION, StateFlag.State.DENY);
					 * ProtectRegion.setFlag(DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE,
					 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.TNT,
					 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.FIRE_SPREAD,
					 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.OTHER_EXPLOSION,
					 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.ENDER_BUILD,
					 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.GHAST_FIREBALL,
					 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.LAVA_FIRE,
					 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.PVP,
					 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.MOB_DAMAGE,
					 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.MOB_SPAWNING,
					 * StateFlag.State.DENY);
					 */

					// Finally, add the region to worlds region manager
					this.plugin.worldGuardPlugin.getRegionManager(player.getWorld()).addRegion(protectedcuboidregion);

					// Send player a message
					player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
					player.sendMessage(
							this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("zone_create")));

					// Clean up user
					this.plugin.tool.remove(player);
					this.plugin.findLocations.remove(player);
					this.plugin.worlds.remove(player);
					this.plugin.selectedFirstLocations.remove(player);
					this.plugin.selectedSecondLocations.remove(player);
					this.plugin.worldEditPlugin.getSession(player)
							.getRegionSelector(plugin.worldEditPlugin.getSession(player).getSelectionWorld()).clear();

					// Reset Beacons
					this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner1);
					this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner2);
					this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner3);
					this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner4);

					return true;
				}

				// /zone cancel
				if ((args[0].equalsIgnoreCase("cancel")) && (args.length < 2)) {

					// Check if player is in map
					if (!this.plugin.tool.containsKey(player)) {

						player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
						player.sendMessage(this.plugin.colorCode('&',
								(String) this.plugin.configDelegate.Map().get("zone_cancel_not_running")));

						return true;
					}

					// Clean up user
					this.plugin.tool.remove(player);
					this.plugin.findLocations.remove(player);
					this.plugin.worlds.remove(player);
					this.plugin.selectedFirstLocations.remove(player);
					this.plugin.selectedSecondLocations.remove(player);
					
					if(this.plugin.worldEditPlugin.getSession(player).getSelectionWorld() != null) {
						this.plugin.worldEditPlugin.getSession(player).getRegionSelector(this.plugin.worldEditPlugin.getSession(player).getSelectionWorld()).clear();
					}

					// Reset beacons
					this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner1);
					this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner2);
					this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner3);
					this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner4);

					player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
					player.sendMessage(
							this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("zone_cancel")));

					return true;
				}

				// /zone addmember <player>
				if ((args[0].equalsIgnoreCase("addmember")) && (args.length > 1) && (args.length < 3)) {

					// Initialise new region
					ProtectedRegion protectedregion = null;

					// Try to get a region
					try {
						protectedregion = this.plugin.getRegion(player);
					} catch (Exception e) {
						e.printStackTrace();
					}

					// Check if region in invalid
					if (protectedregion == null) {

						player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
						player.sendMessage(
								this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("no_zone")));

						return true;
					}

					// Grab the members
					DefaultDomain domainmembers = protectedregion.getMembers();

					// Check if mebers list contains the specified player
					if (domainmembers.contains(
							plugin.worldGuardPlugin.wrapOfflinePlayer(plugin.getServer().getOfflinePlayer(args[1])))) {

						player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
						player.sendMessage(this.plugin
								.colorCode('&', (String) this.plugin.configDelegate.Map().get("zone_addmember_already_member"))
								.replace("{0}", args[1]));

						return true;
					}

					// Initiliaze a variable for later
					final ProtectedRegion protectedregionforguava = protectedregion;

					// From:
					// https://worldguard.enginehub.org/en/latest/developer/regions/protected-region/#domains
					// (modified)
					// start ---
					// Google's Guava library provides useful concurrency classes.
					// The following executor would be re-used in your this.plugin.
					ListeningExecutorService executor = MoreExecutors
							.listeningDecorator(Executors.newCachedThreadPool());

					String[] input = new String[] { args[1] };
					ProfileService profiles = this.plugin.worldGuardPlugin.getProfileService();
					DomainInputResolver resolver = new DomainInputResolver(profiles, input);
					resolver.setLocatorPolicy(UserLocatorPolicy.UUID_AND_NAME);
					ListenableFuture<DefaultDomain> future = executor.submit(resolver);

					// Add a callback using Guava
					Futures.addCallback(future, new FutureCallback<DefaultDomain>() {

						public void onSuccess(DefaultDomain result) {

							protectedregionforguava.getMembers().addPlayer(plugin.worldGuardPlugin
									.wrapOfflinePlayer(plugin.getServer().getOfflinePlayer(args[1])));
							player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.Map().get("head")));
							player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.Map().get("zone_addmember"))
									.replace("{0}", args[1]));
						}

						public void onFailure(Throwable throwable) {

							player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.Map().get("head")));
							player.sendMessage(plugin
									.colorCode('&', (String) plugin.configDelegate.Map().get("zone_addmember_not_existing"))
									.replace("{0}", args[1]));
						}
					});
					// end ---

					return true;
				}

				// /zone removemember <player>
				if ((args[0].equalsIgnoreCase("removemember")) && (args.length > 1) && (args.length < 3)) {

					// Initialise new region
					ProtectedRegion protectedregion = null;

					// Try to get a region
					try {
						protectedregion = this.plugin.getRegion(player);
					} catch (Exception e) {
						e.printStackTrace();
					}

					// Check if region in invalid
					if (protectedregion == null) {

						player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
						player.sendMessage(
								this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("no_zone")));

						return true;
					}

					// Grab the members
					DefaultDomain domainmembers = protectedregion.getMembers();

					// Check if members does not contain the specified player
					if (!domainmembers.contains(this.plugin.worldGuardPlugin
							.wrapOfflinePlayer(plugin.getServer().getOfflinePlayer(args[1])))) {

						player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
						player.sendMessage(
								this.plugin
										.colorCode('&',
												(String) this.plugin.configDelegate.Map()
														.get("zone_removemember_unknownplayer"))
										.replace("{0}", args[1]));

						return true;
					}

					// Remove specified player from the members
					domainmembers.removePlayer(this.plugin.worldGuardPlugin
							.wrapOfflinePlayer(plugin.getServer().getOfflinePlayer(args[1])));

					// Set the new members
					protectedregion.setMembers(domainmembers);

					player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
					player.sendMessage(
							this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("zone_removemember"))
									.replace("{0}", args[1]));

					return true;
				}

				// TODO: Implementig /zone flag <Flag> <Flag value ...> / done: seems complicated
				// TODO: reset flags (/zone flag <Flag> null?)
				// /zone flag <Flag> <Flag value...>
				if ((args[0].equalsIgnoreCase("flag")) && (args.length > 2) && (args.length < 4)) {

					ProtectedRegion protectedregion = null;

					try {
						protectedregion = this.plugin.getRegion(player);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (protectedregion == null) {

						player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
						player.sendMessage(
								this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("no_zone")));
						return true;
					}

					String all = "";

					for (int i = 2; i < args.length; i++) {
						all.concat(args[i]);
					}

					all.trim();
					all.replace(" ", "");

					for (Flag<?> f : DefaultFlag.getDefaultFlags()) {
						if (f.getName().equalsIgnoreCase(args[1])) {
							if (f instanceof StateFlag) {
								StateFlag sf = (StateFlag) f;
								try {
									protectedregion.setFlag(sf, sf.parseInput(FlagContext.create().setSender(player)
											.setInput(all).setObject("region", protectedregion).build()));
									player.sendMessage(this.plugin.colorCode('&',
											(String) this.plugin.configDelegate.getMap().get("head")));
									player.sendMessage(
											this.plugin
													.colorCode('&',
															(String) this.plugin.configDelegate.getMap()
																	.get("zone_flag_changed"))
													.replace("{0}", f.getName()).replace("{1}", args[2]));
								} catch (InvalidFlagFormat e) {
									player.sendMessage(this.plugin.colorCode('&',
											(String) this.plugin.configDelegate.getMap().get("head")));
									player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <allow|deny|none>");
									return false;
									// e.printStackTrace();
								}
							} else if (f instanceof SetFlag<?>) {
								if (DefaultFlag.DENY_SPAWN.getName().equalsIgnoreCase(((SetFlag<?>) f).getName())) {
									SetFlag<EntityType> sfet = (SetFlag<EntityType>) f;
									try {
										protectedregion.setFlag(sfet,
												sfet.parseInput(FlagContext.create().setSender(player).setInput(all)
														.setObject("region", protectedregion).build()));
									} catch (InvalidFlagFormat e) {
										player.sendMessage(ChatColor.RED + "/zone flag " + args[1]
												+ " <EntityType [EntityType ...]>");
										// e.printStackTrace();
									}
								}

								Type type = ((ParameterizedType) f.getClass().getGenericSuperclass())
										.getActualTypeArguments()[0];

								SetFlag<Type> sfet = (SetFlag<Type>) f;
								try {
									protectedregion.setFlag(sfet, sfet.parseInput(FlagContext.create().setSender(player)
											.setInput(args[2]).setObject("region", protectedregion).build()));
									player.sendMessage(this.plugin.colorCode('&',
											(String) this.plugin.configDelegate.getMap().get("head")));
									player.sendMessage(
											this.plugin
													.colorCode('&',
															(String) this.plugin.configDelegate.getMap()
																	.get("zone_flag_changed"))
													.replace("{0}", f.getName()).replace("{1}", args[2]));
								} catch (InvalidFlagFormat e) {
									player.sendMessage(this.plugin.colorCode('&',
											(String) this.plugin.configDelegate.getMap().get("head")));
									player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <"
											+ type.getTypeName() + "[," + type.getTypeName() + " ...]>");
									// e.printStackTrace();
								}
							} else if (f instanceof StringFlag) {
								StringFlag sf = (StringFlag) f;
								try {
									protectedregion.setFlag(sf, sf.parseInput(FlagContext.create().setSender(player)
											.setInput(args[2]).setObject("region", protectedregion).build()));
									player.sendMessage(this.plugin.colorCode('&',
											(String) this.plugin.configDelegate.getMap().get("head")));
									player.sendMessage(
											this.plugin
													.colorCode('&',
															(String) this.plugin.configDelegate.getMap()
																	.get("zone_flag_changed"))
													.replace("{0}", f.getName()).replace("{1}", args[2]));
								} catch (InvalidFlagFormat e) {
									player.sendMessage(this.plugin.colorCode('&',
											(String) this.plugin.configDelegate.getMap().get("head")));
									player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <String...>");
									// e.printStackTrace();
								}
							} else if (f instanceof BooleanFlag) {
								BooleanFlag bf = (BooleanFlag) f;
								try {
									protectedregion.setFlag(bf, bf.parseInput(FlagContext.create().setSender(player)
											.setInput(args[2]).setObject("region", protectedregion).build()));
									player.sendMessage(this.plugin.colorCode('&',
											(String) this.plugin.configDelegate.getMap().get("head")));
									player.sendMessage(
											this.plugin
													.colorCode('&',
															(String) this.plugin.configDelegate.getMap()
																	.get("zone_flag_changed"))
													.replace("{0}", f.getName()).replace("{1}", args[2]));
								} catch (InvalidFlagFormat e) {
									player.sendMessage(this.plugin.colorCode('&',
											(String) this.plugin.configDelegate.getMap().get("head")));
									player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <true|false>");
									// e.printStackTrace();
								}
							} else if (f instanceof IntegerFlag) {
								IntegerFlag inf = (IntegerFlag) f;
								try {
									protectedregion.setFlag(inf, inf.parseInput(FlagContext.create().setSender(player)
											.setInput(args[2]).setObject("region", protectedregion).build()));
									player.sendMessage(this.plugin.colorCode('&',
											(String) this.plugin.configDelegate.getMap().get("head")));
									player.sendMessage(
											this.plugin
													.colorCode('&',
															(String) this.plugin.configDelegate.getMap()
																	.get("zone_flag_changed"))
													.replace("{0}", f.getName()).replace("{1}", args[2]));
								} catch (InvalidFlagFormat e) {
									player.sendMessage(this.plugin.colorCode('&',
											(String) this.plugin.configDelegate.getMap().get("head")));
									player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <Integer>");
									// e.printStackTrace();
								}
							} else if (f instanceof DoubleFlag) {
								DoubleFlag df = (DoubleFlag) f;
								try {
									protectedregion.setFlag(df, df.parseInput(FlagContext.create().setSender(player)
											.setInput(args[2]).setObject("region", protectedregion).build()));
									player.sendMessage(this.plugin.colorCode('&',
											(String) this.plugin.configDelegate.getMap().get("head")));
									player.sendMessage(
											this.plugin
													.colorCode('&',
															(String) this.plugin.configDelegate.getMap()
																	.get("zone_flag_changed"))
													.replace("{0}", f.getName()).replace("{1}", args[2]));
								} catch (InvalidFlagFormat e) {
									player.sendMessage(this.plugin.colorCode('&',
											(String) this.plugin.configDelegate.getMap().get("head")));
									player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <Double>");
									// e.printStackTrace();
								}
							} else if (f instanceof LocationFlag) {
								LocationFlag lf = (LocationFlag) f;
								try {
									protectedregion.setFlag(lf, lf.parseInput(FlagContext.create().setSender(player)
											.setInput(args[2]).setObject("region", protectedregion).build()));
									player.sendMessage(this.plugin.colorCode('&',
											(String) this.plugin.configDelegate.getMap().get("head")));
									player.sendMessage(
											this.plugin
													.colorCode('&',
															(String) this.plugin.configDelegate.getMap()
																	.get("zone_flag_changed"))
													.replace("{0}", f.getName()).replace("{1}", args[2]));
								} catch (InvalidFlagFormat e) {
									player.sendMessage(this.plugin.colorCode('&',
											(String) this.plugin.configDelegate.getMap().get("head")));
									player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <here|X,Y,Z>");
									// e.printStackTrace();
								}
							} else if (f instanceof EnumFlag<?>) {
								if (DefaultFlag.GAME_MODE.getName().equalsIgnoreCase(((EnumFlag<?>) f).getName())) {
									EnumFlag<GameMode> efgm = (EnumFlag<GameMode>) f;
									try {
										protectedregion.setFlag(efgm,
												efgm.parseInput(FlagContext.create().setSender(player).setInput(args[2])
														.setObject("region", protectedregion).build()));
										player.sendMessage(this.plugin.colorCode('&',
												(String) this.plugin.configDelegate.getMap().get("head")));
										player.sendMessage(
												this.plugin
														.colorCode('&',
																(String) this.plugin.configDelegate.getMap()
																		.get("zone_flag_changed"))
														.replace("{0}", f.getName()).replace("{1}", args[2]));
									} catch (InvalidFlagFormat e) {
										player.sendMessage(this.plugin.colorCode('&',
												(String) this.plugin.configDelegate.getMap().get("head")));
										player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <Gamemode>");
										// e.printStackTrace();
									}
								} else if (DefaultFlag.WEATHER_LOCK.getName()
										.equalsIgnoreCase(((EnumFlag<?>) f).getName())) {
									EnumFlag<WeatherType> efwt = (EnumFlag<WeatherType>) f;
									try {
										protectedregion.setFlag(efwt,
												efwt.parseInput(FlagContext.create().setSender(player).setInput(args[2])
														.setObject("region", protectedregion).build()));
										player.sendMessage(this.plugin.colorCode('&',
												(String) this.plugin.configDelegate.getMap().get("head")));
										player.sendMessage(
												this.plugin
														.colorCode('&',
																(String) this.plugin.configDelegate.getMap()
																		.get("zone_flag_changed"))
														.replace("{0}", f.getName()).replace("{1}", args[2]));
									} catch (InvalidFlagFormat e) {
										player.sendMessage(this.plugin.colorCode('&',
												(String) this.plugin.configDelegate.getMap().get("head")));
										player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <Weathertype>");
										// e.printStackTrace();
									}
								}
							} else {
								player.sendMessage(
										this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
								player.sendMessage(this.plugin
										.colorCode('&', (String) this.plugin.configDelegate.Map().get("zone_flag_not_found"))
										.replace("{0}", args[1]));
							}
						}
					}

					return true;
				}

				// /zone info
				if ((args[0].equalsIgnoreCase("info")) && (args.length < 2)) {

					// Initialise new region
					ProtectedRegion protectedregion = null;

					// Try to get a region
					try {
						protectedregion = this.plugin.getRegion(player);
					} catch (Exception e) {
						e.printStackTrace();
					}

					// Check if region in invalid
					if (protectedregion == null) {

						player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
						player.sendMessage(
								this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("no_zone")));

						return true;
					}
					
					// Grab some infos
					DefaultDomain domainowners = protectedregion.getOwners();
					DefaultDomain regionmembers = protectedregion.getMembers();
					int min_x = protectedregion.getMinimumPoint().getBlockX();
					int min_z = protectedregion.getMinimumPoint().getBlockZ();
					int max_x = protectedregion.getMaximumPoint().getBlockX();
					int max_z = protectedregion.getMaximumPoint().getBlockZ();
					int area = (this.plugin.difference(min_x, max_x) + 1) * (this.plugin.difference(min_z, max_z) + 1);

					// Send infos to player
					player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
					player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("zone_info_id"))
							+ protectedregion.getId());
					player.sendMessage(
							this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("zone_info_priority"))
									+ protectedregion.getPriority());
					player.sendMessage(
							this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("zone_info_owners"))
									+ domainowners.toPlayersString(this.plugin.worldGuardPlugin.getProfileCache())
											.replace("*", ""));
					player.sendMessage(
							this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("zone_info_members"))
									+ regionmembers.toPlayersString(this.plugin.worldGuardPlugin.getProfileCache())
											.replace("*", ""));
					
					Iterator<Entry<Flag<?>, Object>>  it = protectedregion.getFlags().entrySet().iterator();
					while(it.hasNext()) {
						Entry<Flag<?>, Object> e = it.next();
						player.sendMessage(
								this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("zone_info_flag"))
										.replace("{0}", e.getKey().getName()).replace("{1}", e.getValue().toString()));
					}
					
					player.sendMessage(
							this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("zone_info_start"))
									.replace("{0}", Integer.toString(min_x)).replace("{1}", Integer.toString(min_z)));
					player.sendMessage(
							this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("zone_info_end"))
									.replace("{0}", Integer.toString(max_x)).replace("{1}", Integer.toString(max_z)));
					player.sendMessage(
							this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("zone_info_area"))
									.replace("{0}", Integer.toString(area)));
					return true;
				}

				// /zone delete
				if ((args[0].equalsIgnoreCase("delete")) && (args.length < 2)) {

					// Initialise region
					ProtectedRegion protectedregion = null;

					// Try to get a region
					try {
						protectedregion = this.plugin.getRegion(player);
					} catch (Exception e) {
						e.printStackTrace();
					}

					// Check if region in invalid
					if (protectedregion == null) {

						player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
						player.sendMessage(
								this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("no_zone")));

						return true;
					}

					// Remove the region from worlds region manager
					this.plugin.worldGuardPlugin.getRegionManager(player.getWorld())
							.removeRegion(protectedregion.getId());

					// Send a message to the player
					player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("head")));
					player.sendMessage(
							this.plugin.colorCode('&', (String) this.plugin.configDelegate.Map().get("zone_delete")));

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
