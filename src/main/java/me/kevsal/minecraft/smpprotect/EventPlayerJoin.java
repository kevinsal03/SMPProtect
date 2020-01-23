package me.kevsal.minecraft.smpprotect;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.InetAddress;
import java.util.Objects;

public class EventPlayerJoin implements Listener {

    private final SMPProtect plugin;

    EventPlayerJoin(SMPProtect plugin)  {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if (p.isWhitelisted()){
            if(plugin.getConfig().getBoolean("auto-add")) {
                InetAddress pIP = Objects.requireNonNull(p.getAddress()).getAddress();
                plugin.addNewIP(pIP, p.getUniqueId().toString(), p.getName());
                plugin.getLogger().info("Automatically added IP: \"" + pIP + "\" to the trusted IP list. (User: " + p.getName() + ")");
                if(plugin.getConfig().getBoolean("notify-auto-add")) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("messages.prefix")) + Objects.requireNonNull(plugin.getConfig().getString("messages.auto-added-ip")) ) );
                }
            }
        }
    }



}
