package me.kevsal.minecraft.smpprotect;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class SMPProtect extends JavaPlugin {

    public final int MainConfigurationVersion = 1;
    private ArrayList<InetAddress> trustedIPs = new ArrayList<InetAddress>(); //FUCK I was stuck with an NPE for like 20 mins because I didn't realize I forgot to instantiate the ArrayList

    @Override
    public void onEnable() {
        //plugin enable
        getLogger().info("SMP Protect has been enabled.");
        //save the default Main config
        saveDefaultConfig();

        int mainConfigVersion = getConfig().getInt("config-version");
        //check if main config version is current
        if (mainConfigVersion != MainConfigurationVersion) {
            getLogger().warning("Outdated main configuration file! Please delete and allow the plugin to recreate it, then copy values");
            this.setEnabled(false);
        }

        //load IPs from config
        this.parseIPs();

        //register events
        Bukkit.getServer().getPluginManager().registerEvents(new EventServerListPing(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EventPlayerJoin(this), this);
    }

    @Override
    public void onDisable() {
        //plugin disable
        getLogger().info("SMP Protect has been disabled.");
    }


    public ArrayList<InetAddress> getTrustedIPs() {
        return trustedIPs;
    }

    public void parseIPs() {
        //parse IPs from config StringList into INetAddresses stored in an arraylist
        List<String> ipList = getConfig().getStringList("trusted-ips");
        for (String ipString : ipList) {
            try {
                InetAddress x = InetAddress.getByName(ipString);
                trustedIPs.add(x);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                getLogger().warning("An invalid IP was found in the config... skipping!");
            }
        }
    }

    public void addNewIP(InetAddress ip) {
        if(!getTrustedIPs().contains(ip)) {
            //add the new ip
            List<String> list = getConfig().getStringList("trusted-ips");
            list.add(ip.toString().substring(1));
            getConfig().set("trusted-ips", list);
            //save the config
            saveConfig();
            //reload the config from storage to memory
            reloadConfig();
            //reparse all IPs so they are being checked for and ignore invalid ones
            parseIPs();
        }
    }
}
