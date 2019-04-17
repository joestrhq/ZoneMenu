package xyz.joestr.zonemenu.command.subcommand;

import java.util.List;
import java.util.concurrent.Executors;

import org.bukkit.entity.Player;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.sk89q.worldguard.WorldGuard;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.util.DomainInputResolver;
import com.sk89q.worldguard.protection.util.DomainInputResolver.UserLocatorPolicy;

import xyz.joestr.zonemenu.ZoneMenu;

/**
 * Class which handles subcommand "addmember" of command "zone".
 *
 * @author joestr
 * @since ${project.version}
 * @version ${project.version}
 */
public class SubCommandZoneAddmember {

    ZoneMenu zoneMenuPlugin = null;

    /**
     * Constrcutor for the null null null null null null null null null null
     * null null null     {@link xyz.joestr.zonemenu.command.subcommand.SubCommandZoneAddmember
     * SubCommandZoneAddmember} class.
     *
     * @param zoneMenuPlugin A {@link xyz.joestr.zonemenu.ZoneMenu ZoneMenu}.
     * @author joestr
     * @since ${project.version}
     * @version ${project.version}
     */
    public SubCommandZoneAddmember(ZoneMenu zoneMenuPlugin) {

        this.zoneMenuPlugin = zoneMenuPlugin;
    }

    /**
     * Processes.
     *
     * @param player A {@link org.bukkit.entity.Player Player}.
     * @param arguments An array of {@link java.lang.String String}s.
     * @author joestr
     * @since ${project.version}
     * @version ${project.version}
     */
    @SuppressWarnings("deprecation")
    public void process(Player player, String[] arguments) {

        if (!player.hasPermission("zonemenu.addmember")) {

            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("permission_message"))
                    .replace("{0}", "zonemenu.addmember")));

            return;
        }

        // If arguments' length does not equals 3 ...
        if (arguments.length != 3) {

            // ... wrong usage of "/zone addmember <Zone> <Player>".
            // Send the player a message.
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("usage_message")).replace("{0}",
                    "/zone addmember <Zone> <Player>")));

            return;
        }

        this.zoneMenuPlugin.futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

            // If the list is empty ...
            if (t.isEmpty()) {

                // ... send the player a message.
                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                    + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("no_zone"))));

                return;
            }

            // Initialise new region with null
            ProtectedRegion protectedRegion = null;

            // Loop through all regions ...
            for (ProtectedRegion protectedRegion_ : t) {

                // ... and if the region ID equals the second argument (<Zone>) ...
                if (protectedRegion_.getId().equalsIgnoreCase(arguments[1])) {

                    // ... set the found region.
                    protectedRegion = protectedRegion_;
                }
            }

            // If region equals null ...
            if (protectedRegion == null) {

                // ... no region with this ID was not found.
                // Send the player a message.
                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                    + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("not_exisiting_zone"))
                        .replace("{0}", arguments[1])));

                return;
            }

            // get the members of the region
            DefaultDomain domainMembers = protectedRegion.getMembers();

            // If members list contains the third argument (<Player>) ...
            if (domainMembers.contains(this.zoneMenuPlugin.worldGuardPlugin
                .wrapOfflinePlayer(this.zoneMenuPlugin.getServer().getOfflinePlayer(arguments[2])))) {

                // ... the given player is laready a member.
                // Send the player a message.
                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                    + ((String) zoneMenuPlugin.configDelegate.getMap().get("zone_addmember_already_member"))
                        .replace("{0}", arguments[2])));

                return;
            }

            // Initiliaze a variable for guava
            final ProtectedRegion protectedRegionForGuava = protectedRegion;

            // From:
            // https://worldguard.enginehub.org/en/latest/developer/regions/protected-region/#domains
            // (modified)
            // --- start ---
            // Google's Guava library provides useful concurrency classes.
            // The following executor would be re-used in your plugin.
            ListeningExecutorService listeningExecutorService = MoreExecutors
                .listeningDecorator(Executors.newCachedThreadPool());

            String[] input = new String[]{arguments[2]};
            DomainInputResolver domainInputResolver = new DomainInputResolver(WorldGuard.getInstance().getProfileService(), input);
            domainInputResolver.setLocatorPolicy(UserLocatorPolicy.UUID_AND_NAME);
            ListenableFuture<DefaultDomain> listenableFuture = listeningExecutorService.submit(domainInputResolver);

            // Add a callback using Guava
            Futures.addCallback(listenableFuture, new FutureCallback<DefaultDomain>() {

                // If it was successfull.
                @Override
                public void onSuccess(DefaultDomain result) {

                    // Add the player as member to a region.
                    protectedRegionForGuava.getMembers().addPlayer(zoneMenuPlugin.worldGuardPlugin
                        .wrapOfflinePlayer(zoneMenuPlugin.getServer().getOfflinePlayer(arguments[2])));

                    // Send the player a message.
                    player.sendMessage(zoneMenuPlugin.colorCode('&',
                        ((String) zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                        + ((String) zoneMenuPlugin.configDelegate.getMap().get("zone_addmember"))
                            .replace("{0}", arguments[2]).replace("{1}", arguments[1])));
                }

                // If it was not successfull.
                @Override
                public void onFailure(Throwable throwable) {

                    // Send the player a message.
                    player.sendMessage(zoneMenuPlugin.colorCode('&', ((String) zoneMenuPlugin.configDelegate.getMap()
                        .get("prefix"))
                        + ((String) zoneMenuPlugin.configDelegate.getMap().get("zone_addmember_not_existing"))
                            .replace("{0}", arguments[2])));
                }
            });
            // --- end ---
        });
    }
}
