package caesreon.core.hilfsklassen;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ChatBungee {
    public static void nachrichtAnSpielerSenden(ProxiedPlayer p, String prefix, String nachricht) {
        p.sendMessage(new ComponentBuilder(ChatColor.GOLD + prefix + " " + nachricht).color(ChatColor.WHITE).create());
    }
}
