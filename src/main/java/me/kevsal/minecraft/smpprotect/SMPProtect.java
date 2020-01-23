package me.kevsal.minecraft.smpprotect;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class SMPProtect extends JavaPlugin {

    public final int MainConfigurationVersion = 2;
    public boolean Paper; //public boolean to say if running Paper or not
    private ArrayList<InetAddress> trustedIPs = new ArrayList<>();
    private Connection db;


    @Override
    public void onEnable() {
        //check if server is running PaperSpigot
        if (!Bukkit.getServer().getVersion().contains("Paper")) {
            getLogger().warning("This plugin is written from the Paper API. We recommend using Paper!");
            getLogger().warning("Find out why at: https://whypaper.emc.gs/");
            Paper = true;
        } else {
            Paper = false;
        }

        //enable metrics if user is allowing metrics
        if(getConfig().getBoolean("allow-metrics")){
            Metrics metrics = new Metrics(this, 6327);
        }


        //save the default Main config
        saveDefaultConfig();
        //save the default database
        //this.saveResource("smp-protect.mv.db", false);

        int mainConfigVersion = getConfig().getInt("config-version");
        //check if main config version is current
        if (mainConfigVersion != MainConfigurationVersion) {
            getLogger().warning("Outdated main configuration file! Please delete and allow the plugin to recreate it, then copy values!");
            this.setEnabled(false);
        }

        //init the database
        this.initDatabase();

        //load IPs from config
        this.parseIPs();

        //register events
        //TODO: Add support for the better version of the ServerListPingEvent provided by Paper
        Bukkit.getServer().getPluginManager().registerEvents(new EventServerListPing(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EventPlayerJoin(this), this);

        //register commands
        Objects.requireNonNull(this.getCommand("trust-ip")).setExecutor(new CommandAddIP(this));
        // TODO: Objects.requireNonNull(this.getCommand("untrust-ip")).setExecutor(new CommandRemoveIP(this));
        // TODO: Objects.requireNonNull(this.getCommand("list-trusted")).setExecutor(new CommandListIP(this));

        //plugin enabled
        getLogger().info("SMP Protect has been enabled.");
    }

    @Override
    public void onDisable() {
        //close database if not yet closed
        try {
            if (!db.isClosed()) {
                db.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //plugin disable
        getLogger().info("SMP Protect has been disabled.");
    }


    public ArrayList<InetAddress> getTrustedIPs() {
        return trustedIPs;
    }

    public void parseIPs() {

        ResultSet result;
        int resultSize;

        try {
            String sql = "SELECT * FROM TRUSTEDIP ";
            //create sql statement
            Statement smt = getDatabase().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //execute sql code
            result = smt.executeQuery(sql);

            //get the total size of the sql
            result.last();
            resultSize = result.getRow();
            result.beforeFirst(); //go back to first

            System.out.println(resultSize);
            //get each IP from the database and add to the ArrayList
            if (resultSize != 0) {
                for (int i = 1; i <= resultSize; i++) {
                    result.absolute(i);
                    try {
                        trustedIPs.add(InetAddress.getByName(result.getString("IP")));
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        getLogger().warning("An invalid IP was found in the database... skipping!");
                    }
                }
            } else {
                getLogger().warning("Database is empty.");
            }


            } catch (SQLException e) {
            e.printStackTrace();
            getLogger().warning("A SQL Error has occurred!");
        }
    }

    public void addNewIP(InetAddress ip, String playerUUID, String playerName) {

        if (playerUUID == null) {
            playerUUID = "Unknown";
        }
        if (playerName == null) {
            playerName = "Unknown";
        }

        String ipString = ip.toString().substring(1);
        //sql code to be ran
        String sql = "INSERT INTO TRUSTEDIP \nVALUES (\'" + ipString + "\', \'" + playerUUID + "\', \'" + playerName + "\');";

        //try running the sql
        try {
            //create a new sql statement using the database
            Statement smt = getDatabase().createStatement();
            //execute the sql above
            smt.executeUpdate(sql);;
            smt.close();
            parseIPs();
        } catch (SQLException e) {
            e.printStackTrace();
            getLogger().warning("A SQL Error has occurred!");
        }
    }

    public void initDatabase() {
        try {
            Class.forName("org.h2.Driver");
            db = DriverManager.getConnection("jdbc:h2:" + this.getDataFolder().getAbsolutePath() + "/smp-protect");
            //create table if not exist
            Statement smt = db.createStatement();
            smt.execute("CREATE TABLE IF NOT EXISTS trustedIP(IP VARCHAR(255) NOT NULL, PLAYERUUID VARCHAR(255) NOT NULL, PLAYERUSERNAME VARCHAR(255));");

            //if empty add thing
            getLogger().info("Database initialized successfully.");
        } catch (Exception e) {
           e.printStackTrace();
           getLogger().warning("An error occurred during database initialization. Plugin disabled.");
           this.setEnabled(false);
        }
    }

    public Connection getDatabase() {
        return db;
    }
}
