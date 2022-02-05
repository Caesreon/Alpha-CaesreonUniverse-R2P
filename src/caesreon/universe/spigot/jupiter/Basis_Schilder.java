package caesreon.universe.spigot.jupiter;

import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.SchildUT;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Basis_Schilder implements Listener {
    //TODO: Auslagern in Enum evtl.
    final String Webseite = "Webseite";
    final String Konzept = "Konzept";
    final String Wiki = "Wiki";
    final String Forum = "Forum";
    final String Leitfaden = "Leitfaden";
    final String DSGVO = "DSGVO";
    final String Regeln = "Regeln";
    final String Livemap = "Livemap";
    final String Discord = "Discord";
    private final JupiterKomponente modul;
    String Color_Prefix;

    public Basis_Schilder(JupiterKomponente modul) {
        this.modul = modul;

        Color_Prefix = ChatColor.GOLD + modul.Prefix;
    }

    @EventHandler
    public void schildModifizierung(SignChangeEvent e) {
        if (e.getPlayer().hasPermission("caesreon.commands.schilder")) {
            for (int i = 0; i < 4; i++) {
                String line = e.getLine(i);
                if (line != null && !line.equals("")) {
                    e.setLine(i, ChatColor.translateAlternateColorCodes('&', line));
                }
            }


            //Todo Switch Case
            if (e.getLine(0).equals(modul.Prefix)) {
                e.setLine(0, Color_Prefix);
            }

            if (e.getLine(0).equals(Color_Prefix)) {

                switch (e.getLine(2)) {
                    case Webseite:
                        e.setLine(2, SchildUT.aendereSchildStringFarbe_WHITE(Webseite));
                        break;
                    case Konzept:
                        e.setLine(2, SchildUT.aendereSchildStringFarbe_WHITE(Konzept));
                        break;
                    case Wiki:
                        e.setLine(2, SchildUT.aendereSchildStringFarbe_WHITE(Wiki));
                        break;
                    case Forum:
                        e.setLine(2, SchildUT.aendereSchildStringFarbe_WHITE(Forum));
                        break;
                    case Leitfaden:
                        e.setLine(2, SchildUT.aendereSchildStringFarbe_WHITE(Leitfaden));
                        break;
                    case Livemap:
                        e.setLine(2, SchildUT.aendereSchildStringFarbe_WHITE(Livemap));
                        break;
                    case Regeln:
                        e.setLine(2, SchildUT.aendereSchildStringFarbe_WHITE(Regeln));
                        break;
                    case DSGVO:
                        e.setLine(2, SchildUT.aendereSchildStringFarbe_WHITE(DSGVO));
                        break;
                    case Discord:
                        e.setLine(2, SchildUT.aendereSchildStringFarbe_WHITE(Discord));
                        break;
                }
            }
        }
    }

    @EventHandler
    public void schildInteraktionClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            //Wenn Linksklick wird hier die Methode wieder verlassen
            return;
        }

        Player p = e.getPlayer();
        Block b = e.getClickedBlock();

        if ((b.getType().name().contains("_SIGN"))) {
            //DEBUG Block_Type
            //System.out.println(b.getType().name());
            Sign s = (Sign) b.getState();

            if (s.getLine(0).equals(Color_Prefix)) {

                if (s.getLine(2).equals(ChatColor.WHITE + "Webseite")) {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, modul.getMain().getCore().getDefaultConfig().getString("webseite_url"));
                }
                if (s.getLine(2).equals(ChatColor.WHITE + "Konzept")) {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, modul.getMain().getCore().getDefaultConfig().getString("konzept_url"));
                }
                if (s.getLine(2).equals(ChatColor.WHITE + "Wiki")) {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, modul.getMain().getCore().getDefaultConfig().getString("wiki_url"));
                }
                if (s.getLine(2).equals(ChatColor.WHITE + "Forum")) {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, modul.getMain().getCore().getDefaultConfig().getString("forum_url"));
                }
                if (s.getLine(2).equals(ChatColor.WHITE + "Leitfaden")) {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, modul.getMain().getCore().getDefaultConfig().getString("leitfaden_url"));
                }
                if (s.getLine(2).equals(ChatColor.WHITE + "Livemap")) {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, modul.getMain().getCore().getDefaultConfig().getString("livemap_url"));
                }
                if (s.getLine(2).equals(ChatColor.WHITE + "Regeln")) {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, modul.getMain().getCore().getDefaultConfig().getString("regeln_url"));
                }
                if (s.getLine(2).equals(ChatColor.WHITE + "DSGVO")) {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, modul.getMain().getCore().getDefaultConfig().getString("dsgvo_url"));
                }
                if (s.getLine(2).equals(ChatColor.WHITE + "Discord")) {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, modul.getMain().getCore().getDefaultConfig().getString("discord_url"));
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private void schreibeSchild(SignChangeEvent e, String Zeile1, String Zeile2, String Zeile3, String Zeile4) {
        e.setLine(1, Zeile1);
        e.setLine(2, Zeile2);
        e.setLine(3, Zeile3);
        e.setLine(4, Zeile4);
    }

}
