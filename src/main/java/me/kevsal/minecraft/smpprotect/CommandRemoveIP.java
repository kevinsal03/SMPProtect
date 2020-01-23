package me.kevsal.minecraft.smpprotect;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class CommandRemoveIP implements CommandExecutor {

    private final SMPProtect plugin;

    CommandRemoveIP(SMPProtect plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        //test if user has permission
        if(sender instanceof Player) {
            if (!sender.hasPermission("smpprotect.remove")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("messages.no-permission"))));
                return true;
            }
        }

        if (args.length != 1) {
            return false;
        } else {
            InetAddress ip;
            try {
                ip = InetAddress.getByName(args[0]);
            } catch (UnknownHostException e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("messages.prefix")) + ChatColor.RED + "Invalid IP."));
                return true;
            }
            //test if ip is trusted
            if (plugin.getTrustedIPs().contains(ip)) {
                //if trusted, remove the IP
                if (plugin.removeIP(ip)) {
                    //success
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("messages.prefix")) + "IP removed successfully."));
                } else {
                    //failed
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("messages.prefix")) + "&cError: Unable to remove IP. Check the log for details."));
                }
            } else {
                //ip is not trusted
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("messages.prefix")) + "&cError: That IP is not trusted."));
            }


        }
        return true;
    }
}
