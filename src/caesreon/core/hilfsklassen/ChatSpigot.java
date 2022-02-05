package caesreon.core.hilfsklassen;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;


public class ChatSpigot {

    public static void NachrichtSenden(Player p, String Prefix, String msg) {
        p.sendMessage(ChatColor.GOLD + setPrefix(Prefix) + " " + ChatColor.WHITE + msg);
    }

    public static void NachrichtSendenFehler(Player p) {
        p.sendMessage("Upps, irgendwas ist schief gegangen");
    }

    public static String setPrefix(String Text) {
        return Text;
    }

    public static String chatTrennerModulBefehle(ChatColor farbe, String string) {
        return farbe + "===============" + string + farbe + "===============";
    }

}
