package me.kevsal.minecraft.smpprotect;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class EventServerListPing implements Listener {

    private final SMPProtect plugin;

    EventServerListPing(SMPProtect plugin)  {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent e) {
        //the IP of the pinger
        InetAddress pingerIP = e.getAddress();
        //get ips from main class
        ArrayList<InetAddress> trustedIPs = plugin.getTrustedIPs();
        //check if ip is known, if not obfuscate server info
        if (!trustedIPs.contains(pingerIP)) {
            if(plugin.getConfig().getBoolean("log-pings")) {
                plugin.getLogger().info("An unknown IP (" + pingerIP + ") has pinged the server!");
            }
            //obfuscate
            obfuscateServerInfo(e);
        }
    }

    private void obfuscateServerInfo(ServerListPingEvent e) {
        //set to default mc max players
        e.setMaxPlayers(plugin.getConfig().getInt("obfuscation-options.max-players"));
        //set to default MC MOTD
        e.setMotd(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull((plugin.getConfig().getString("obfuscation-options.motd")))));
        //set the server icon to be null (not sure if this works)
        e.setServerIcon(null);
        //remove all players from the online players iterator
        Iterator<Player> iterator = e.iterator();
        while(iterator.hasNext()) {
            Player player = iterator.next();
            iterator.remove();
        }
    }
}
