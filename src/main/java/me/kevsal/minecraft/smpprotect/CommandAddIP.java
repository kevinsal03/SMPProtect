package me.kevsal.minecraft.smpprotect;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class CommandAddIP implements CommandExecutor {

    public final SMPProtect plugin;

    CommandAddIP(SMPProtect plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        //test if user has permission
        if(sender instanceof Player) {
            if (!sender.hasPermission("smpprotect.add")) {
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
                e.printStackTrace();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("messages.prefix")) + ChatColor.RED + "Invalid IP."));
                return true;
            }
            plugin.addNewIP(ip, null, null);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("messages.prefix")) + "IP added successfully."));
        }
        return true;
    }
}
