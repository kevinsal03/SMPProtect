## SMP Protect  
A Bukkit/Spigot/Paper plugin designed to protect private SMP servers from IP enumeration attacks  

[Spigot Page](https://www.spigotmc.org/resources/smp-protect.74281/)  
[Bukkit Page](https://dev.bukkit.org/projects/smp-protect)  

### About SMPProtect

*Note: This plugin is still early in its development cycle and many features are in the planning phase.*

SMP Protect is a Bukkit/Spigot/Paper plugin designed to help protect private SMP servers from IP enumeration attacks by obfuscating the information sent in reply to a ServerListPingEvent (eg. the MOTD, players online, and total player count) to make the server appear as a generic Minecraft server, without totally removing the identifying features of the server for whitelisted players.

Since player usernames are not available before the player has connected to the server, the plugin instead uses the IP address of the player to determine whether or not to obfuscate the response by the ping. When a player connects to the server, if they are whitelisted, their IP address is automatically added to the list of trusted IP addresses.

Trusted player IP addresses and associated usernames/UUIDs are stored in a H2 database.

#### TODO:

* Allow specific players to be trusted without relying on the vanilla whitelist system.
* Add a command to list all trusted IP addresses

#### Issues/Suggestions

If you discover any issues with the plugin or have any suggestions for changes to the plugin, please feel free to open an issue on the plugin’s GitHub page [here]('https://github.com/kevinsal03/SMPProtect/issues').