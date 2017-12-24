package xyz.joestr.zonemenu.command;

import java.util.concurrent.Executors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.util.DomainInputResolver;
import com.sk89q.worldguard.protection.util.DomainInputResolver.UserLocatorPolicy;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

import xyz.joestr.zonemenu.ZoneMenu;

@SuppressWarnings("deprecation")
public class ZoneCommand implements CommandExecutor {
	
	private ZoneMenu plugin;
	
	public ZoneCommand(ZoneMenu zonemenu) { this.plugin = zonemenu; }
	
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		
		//Player
		if(sender instanceof Player) {
			
			final Player player = (Player)sender;
			
			if(player.hasPermission("zonemenu.*")) {
				
				// -> /zone
				if(args.length < 1) {
					
					player.spigot().sendMessage(
					new ComponentBuilder(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head") + (String)this.plugin.config.Map().get("head_extra")))
					.append("\n" + this.plugin.colorCode("&", (String)this.plugin.config.Map().get("find")))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("find_hover")))
					.create()))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zone find"))
					.append("\n" + this.plugin.colorCode("&", (String)this.plugin.config.Map().get("sign")))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("sign_hover")))
					.create()))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zone sign"))
					.append("\n" + this.plugin.colorCode("&", (String)this.plugin.config.Map().get("create")))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("create_hover")))
					.create()))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zone create"))
					.append("\n" + this.plugin.colorCode("&", (String)this.plugin.config.Map().get("cancel")))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("cancel_hover")))
					.create()))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zone cancel"))
					.append("\n" + this.plugin.colorCode("&", (String)this.plugin.config.Map().get("addmember")))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("addmember_hover")))
					.create()))
					.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/zone addmember "))
					.append("\n" + this.plugin.colorCode("&", (String)this.plugin.config.Map().get("removemember")))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("removemember_hover")))
					.create()))
					.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/zone removemember "))
					.append("\n" + this.plugin.colorCode("&", (String)this.plugin.config.Map().get("info")))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("info_hover")))
					.create()))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zone info"))
					.append("\n" + this.plugin.colorCode("&", (String)this.plugin.config.Map().get("delete")))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("delete_hover")))
					.create()))
					.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/zone delete"))
					.create());
					return true;
				}
				
				// -> /zone find
				if((args[0].equalsIgnoreCase("find")) && (args.length < 2)) {
					
					if(!player.getInventory().contains(Material.STICK)) {
						
						ItemStack itemstack = new ItemStack(Material.STICK, 1);
						player.getInventory().addItem(new ItemStack[] { itemstack });
					}
					
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_find")));
					this.plugin.tool.put(player.getName(), "find");
					return true;
				}
				
				// -> /zoen sign
				if((args[0].equalsIgnoreCase("sign")) && (args.length < 2)) {
					
					if(!player.getInventory().contains(Material.STICK)) {
						
						ItemStack itemstack = new ItemStack(Material.STICK, 1);
						player.getInventory().addItem(new ItemStack[] { itemstack });
					}
					
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_sign")));
					this.plugin.tool.put(player.getName(), "sign");
					return true;
				}
				
				
				// -> /zone create
				if((args[0].equalsIgnoreCase("create")) && (args.length < 2)) {
					
					try {
						
						if(this.plugin.getRegion(player) != null) {
							
							player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
							player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_create_already_have")));
							return true;
						}
					} catch(Exception e) { e.printStackTrace(); }
					
					Selection selectedregion = this.plugin.getWorldEdit().getSelection(player);
					
					if(selectedregion == null) {
						
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_create_not_signed")));
						return true;
					}
					
					if(selectedregion.getWidth() * selectedregion.getLength() < Integer.parseInt(this.plugin.config.Map().get("zone_create_area_min").toString())) {
						
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_create_area_under")).replace("{0}", this.plugin.config.Map().get("zone_create_area_min").toString()));
						return true;
					}
					
					if(selectedregion.getWidth() * selectedregion.getLength() > Integer.parseInt(this.plugin.config.Map().get("zone_create_area_max").toString())) {
						
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_create_area_over")).replace("{0}", this.plugin.config.Map().get("zone_create_area_max").toString()));
						return true;
					}
					
					Location min = selectedregion.getMinimumPoint();
					Location max = selectedregion.getMaximumPoint();
					double first_x = min.getX();
					double first_y = min.getY();
					double first_z = min.getZ();
					double second_x = max.getX();
					double second_y = max.getY();
					double second_z = max.getZ();
					ProtectedCuboidRegion protectedcuboidregion = new ProtectedCuboidRegion((String)this.plugin.id.Map().get("zone_id") + this.plugin.id.Map().get("zone_id_counter").toString(), new BlockVector(first_x, first_y, first_z), new BlockVector(second_x, second_y, second_z));
					this.plugin.id.Map().put("zone_id_counter", Integer.parseInt(this.plugin.id.Map().get("zone_id_counter").toString()) + 1);
					this.plugin.id.Save();
					
					if(this.plugin.getWorldGuard().getRegionManager(player.getWorld()).overlapsUnownedRegion(protectedcuboidregion, this.plugin.getWorldGuard().wrapPlayer(player))) {
						
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_create_overlaps_unowned")));
						return true;
					}
									
					ProfileService ps = this.plugin.worldguardplugin.getProfileService();
					try { ps.findByName(player.getName()); } catch(Exception e) { e.printStackTrace(); }
					DefaultDomain domain = new DefaultDomain();
					domain.addPlayer(this.plugin.worldguardplugin.wrapPlayer(player));
					protectedcuboidregion.setOwners(domain);
					protectedcuboidregion.setPriority(Integer.parseInt(this.plugin.config.Map().get("zone_create_priority").toString()));
					
					/*
					ProtectRegion.setFlag(DefaultFlag.CREEPER_EXPLOSION, StateFlag.State.DENY);
					ProtectRegion.setFlag(DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE, StateFlag.State.DENY);
					ProtectRegion.setFlag(DefaultFlag.TNT, StateFlag.State.DENY);
					ProtectRegion.setFlag(DefaultFlag.FIRE_SPREAD, StateFlag.State.DENY);
					ProtectRegion.setFlag(DefaultFlag.OTHER_EXPLOSION, StateFlag.State.DENY);
					ProtectRegion.setFlag(DefaultFlag.ENDER_BUILD, StateFlag.State.DENY);
					ProtectRegion.setFlag(DefaultFlag.GHAST_FIREBALL, StateFlag.State.DENY);
					ProtectRegion.setFlag(DefaultFlag.LAVA_FIRE, StateFlag.State.DENY);
					ProtectRegion.setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
					ProtectRegion.setFlag(DefaultFlag.MOB_DAMAGE, StateFlag.State.DENY);
					ProtectRegion.setFlag(DefaultFlag.MOB_SPAWNING, StateFlag.State.DENY);
					*/
					
					this.plugin.getWorldGuard().getRegionManager(player.getWorld()).addRegion(protectedcuboidregion);
					
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_create")));
					this.plugin.tool.remove(player.getName());
					this.plugin.findlocations.remove(player.getName());
					this.plugin.worlds.remove(player.getName());
					this.plugin.firstlocations.remove(player.getName());
					this.plugin.secondlocations.remove(player.getName());
					this.plugin.worldeditplugin.getSession(player).getRegionSelector(plugin.worldeditplugin.getSession(player).getSelectionWorld()).clear();
					this.plugin.resetCorner(player, this.plugin.corner1);
					this.plugin.resetCorner(player, this.plugin.corner2);
					this.plugin.resetCorner(player, this.plugin.corner3);
					this.plugin.resetCorner(player, this.plugin.corner4);
					return true;
				}
				
				// -> /zone cancel
				if((args[0].equalsIgnoreCase("cancel")) && (args.length < 2)) {
					
					if(!this.plugin.tool.containsKey(player.getName())) {
						
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_cancel_not_running")));
						return true;
					}
					
					this.plugin.tool.remove(player.getName());
					this.plugin.findlocations.remove(player.getName());
					this.plugin.worlds.remove(player.getName());
					this.plugin.firstlocations.remove(player.getName());
					this.plugin.secondlocations.remove(player.getName());
					this.plugin.worldeditplugin.getSession(player).getRegionSelector(plugin.worldeditplugin.getSession(player).getSelectionWorld()).clear();
					this.plugin.resetCorner(player, this.plugin.corner1);
					this.plugin.resetCorner(player, this.plugin.corner2);
					this.plugin.resetCorner(player, this.plugin.corner3);
					this.plugin.resetCorner(player, this.plugin.corner4);
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_cancel")));
					return true;
				}
				
				// -> /zone addmember <player>
				if((args[0].equalsIgnoreCase("addmember")) && (args.length > 1) && (args.length < 3)) {
					
					ProtectedRegion protectedregion = null;
					
					try { protectedregion = this.plugin.getRegion(player); } catch(Exception e) { e.printStackTrace(); }
					
					if(protectedregion == null) {
						
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("no_zone")));
						return true;
					}
					
					DefaultDomain domainmembers = protectedregion.getMembers();
					
					if(domainmembers.contains(plugin.worldguardplugin.wrapOfflinePlayer(plugin.getServer().getOfflinePlayer(args[1])))) {
						
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_addmember_already_member")).replace("{0}", args[1]));
						return true;
					}
					
					final ProtectedRegion protectedregionforguava = protectedregion;
					
					//From: https://worldguard.enginehub.org/en/latest/developer/regions/protected-region/#domains (modified)
					//start ---
					// Google's Guava library provides useful concurrency classes.
					// The following executor would be re-used in your this.plugin.
					ListeningExecutorService executor =
					        MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

					String[] input = new String[] { args[1] };
					ProfileService profiles = this.plugin.worldguardplugin.getProfileService();
					DomainInputResolver resolver = new DomainInputResolver(profiles, input);
					resolver.setLocatorPolicy(UserLocatorPolicy.UUID_AND_NAME);
					ListenableFuture<DefaultDomain> future = executor.submit(resolver);

					// Add a callback using Guava
					Futures.addCallback(future, new FutureCallback<DefaultDomain>() {
						
					    public void onSuccess(DefaultDomain result) {
					    	
					    	protectedregionforguava.getMembers().addPlayer(plugin.worldguardplugin.wrapOfflinePlayer(plugin.getServer().getOfflinePlayer(args[1])));
							player.sendMessage(plugin.colorCode("&", (String)plugin.config.Map().get("head")));
							player.sendMessage(plugin.colorCode("&", (String)plugin.config.Map().get("zone_addmember")).replace("{0}", args[1]));
					    }

					    public void onFailure(Throwable throwable) {
					    	
					    	player.sendMessage(plugin.colorCode("&", (String)plugin.config.Map().get("head")));
							player.sendMessage(plugin.colorCode("&", (String)plugin.config.Map().get("zone_addmember_not_existing")).replace("{0}", args[1]));
					    }
					});
					//end ---
					
					return true;
				}
				
				// -> /zone removemember <player>
				if((args[0].equalsIgnoreCase("removemember")) && (args.length > 1) && (args.length < 3)) {
					
					ProtectedRegion protectedregion = null;
					
					try { protectedregion = this.plugin.getRegion(player); } catch(Exception e) { e.printStackTrace(); }
					
					if(protectedregion == null) {
						
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("no_zone")));
						return true;
					}
					
					DefaultDomain domainmembers = protectedregion.getMembers();
					
					if(!domainmembers.contains(this.plugin.worldguardplugin.wrapOfflinePlayer(plugin.getServer().getOfflinePlayer(args[1])))) {
						
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_removemember_unknownplayer")).replace("{0}", args[1]));
						return true;
					}
					
					domainmembers.removePlayer(this.plugin.worldguardplugin.wrapOfflinePlayer(plugin.getServer().getOfflinePlayer(args[1])));
					protectedregion.setMembers(domainmembers);
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_removemember")).replace("{0}", args[1]));
					return true;
				}
				
				// -> /zone info
				if((args[0].equalsIgnoreCase("info")) && (args.length < 2)) {
					
					ProtectedRegion protectedregion = null;
					
					try { protectedregion = this.plugin.getRegion(player); } catch(Exception e) { e.printStackTrace(); }
					
					if(protectedregion == null) {
						
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("no_zone")));
						return true;
					}
					
					DefaultDomain domainowners = protectedregion.getOwners();
					DefaultDomain RegionMember = protectedregion.getMembers();
					int min_x = protectedregion.getMinimumPoint().getBlockX();
					int min_z = protectedregion.getMinimumPoint().getBlockZ();
					int max_x = protectedregion.getMaximumPoint().getBlockX();
					int max_z = protectedregion.getMaximumPoint().getBlockZ();
					int area = (this.plugin.difference(min_x, max_x) + 1) * (this.plugin.difference(min_z, max_z) + 1);
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_info_id")) + protectedregion.getId());
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_info_priority")) + protectedregion.getPriority());
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_info_owners")) + domainowners.toPlayersString(this.plugin.worldguardplugin.getProfileCache()).replace("*", ""));
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_info_members")) + RegionMember.toPlayersString(this.plugin.worldguardplugin.getProfileCache()).replace("*", ""));
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_info_start")).replace("{0}", Integer.toString(min_x)).replace("{1}", Integer.toString(min_z)));
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_info_end")).replace("{0}", Integer.toString(max_x)).replace("{1}", Integer.toString(max_z)));
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_info_area")).replace("{0}", Integer.toString(area)));
					return true;
				}
				
				// -> /zone delete
				if((args[0].equalsIgnoreCase("delete")) && (args.length < 2)) {
					
					ProtectedRegion protectedregion = null;
					
					try { protectedregion = this.plugin.getRegion(player); } catch(Exception e) { e.printStackTrace(); }
					
					if(protectedregion == null) {
						
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
						player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("no_zone")));
						return true;
					}
					
					this.plugin.getWorldGuard().getRegionManager(player.getWorld()).removeRegion(protectedregion.getId());
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
					player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("zone_delete")));
					return true;
				}
				
				// -> /zone - Wrong usage
				player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
				player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("usage_message")).replace("{0}", "/zone"));
				return true;
			}
			
			// -> /zone - No permission
			player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
			player.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("permission_message")).replace("{0}", "zonemenu.*"));
			return true;
		}
		
		//Console
		plugin.getServer().getConsoleSender().sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("head")));
		sender.sendMessage(this.plugin.colorCode("&", (String)this.plugin.config.Map().get("console_message")));
		return true;
	}
}
