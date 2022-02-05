package caesreon.universe.spigot.merkur.modul.shops;

import caesreon.core.Log;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.WeltUT.CheckWeltenName;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Coriolanus_S
 * Flag1:
 * Flag2:
 * <p>
 * If (Flag1 && Flag2 && Flag3)
 * Shop Erstellen
 */
public class ShopCreationListener implements Listener {
    private SchilderHandelsModul modul;
    private ErstelleShopFlags eS;

    public ShopCreationListener(SchilderHandelsModul modul) {
        this.modul = modul;
    }

    @EventHandler
    public void ShopErstellenSchildClick(PlayerInteractEvent e) {
        if (e.getPlayer().hasPermission(modul.permissionNode)) {

            if (e.getAction() != Action.LEFT_CLICK_BLOCK) {
                //Wenn Rechtsklick wird hier die Methode wieder verlassen
                return;
            }

            Player p = e.getPlayer();
            Block b = e.getClickedBlock();

            if (p.getInventory().getItemInMainHand().getType().equals(Material.REDSTONE)) {
                if (b.getType().name().contains("_SIGN") && p.getInventory().getItemInMainHand().getType().equals(Material.REDSTONE)) {
                    Log.SpigotLogger.Verbose("jupiter.shops.ShopCreationListener().ShopErstellenSchildKlick()");
                    eS = new ErstelleShopFlags();
                    eS.setSchild((Sign) b.getState());
                    eS.setFlag_Schild(true);
                }
            }

        }
    }

    @EventHandler
    public void ShopErstellenKisteClick(PlayerInteractEvent e) {
        if (CheckWeltenName.playerInteractEventListe(e, modul.erlaubteWelten)) {
            if (e.getPlayer().hasPermission(modul.permissionNode)) {
                if (e.getAction() != Action.LEFT_CLICK_BLOCK) {
                    //Wenn Rechtsklick wird hier die Methode wieder verlassen
                    return;
                }

                if (eS == null) {
                    return;
                }

                Player p = e.getPlayer();
                Block b = e.getClickedBlock();

                if (b.getType().equals(Material.CHEST) && p.getInventory().getItemInMainHand().getType().equals(Material.REDSTONE)) {
                    Log.SpigotLogger.Debug("jupiter.shops.ShopCreationListener().ShopErstellenKisteKlick()");
                    eS.setFlag_Kiste(true);
                    eS.setKiste((Chest) b.getState());
                    if (eS.creationFlag_Schild() && eS.creationFlag_Kiste()) {
                        preCheck(p, eS.getSchild(), eS.getKiste());
                        faerbeShopSchildBeiErfolgreicherErstellung(eS.getSchild());
                        eS = null;
                    }
                }
            }
        }

    }

    private void faerbeShopSchildBeiErfolgreicherErstellung(Sign Schild) {
        String prefix = modul.colorCodePrefix;
        if (prefix.contains("&")) {
            prefix = "§";
        }
        Log.SpigotLogger.Debug(prefix);
        if (Schild.getLine(0).equals(modul.kaufen))
            Schild.setLine(0, prefix + "2" + modul.kaufen);
        if (Schild.getLine(0).equals(modul.verkaufen))
            Schild.setLine(0, prefix + "1" + modul.verkaufen);
        //ADMINSHOP
        if (Schild.getLine(0).equals(modul.adminShopKaufen))
            Schild.setLine(0, prefix + "2" + modul.adminShopKaufen);
        if (Schild.getLine(0).equals(modul.adminShopVerkaufen))
            Schild.setLine(0, prefix + "1" + modul.adminShopVerkaufen);
        Schild.update();
    }


    private void preCheck(Player e, Sign s, Chest c) {
        Log.SpigotLogger.Debug("jupiter.shops.ShopCreationListener().preCheck()");
        if (s.getLine(2) != null && s.getLine(3) != null) {
            try {
                if (checkAnzahl(s.getLine(2))) {

                    modul.getShopHandler().erstelleShop(e, s, c);
                }
            } catch (Exception ex) {
                Log.SpigotLogger.Debug(ex.toString());
                ChatSpigot.NachrichtSendenFehler(e);
            }
        }

    }

    private Boolean checkAnzahl(String s) {
        String[] data = s.split(" ");
        if (data[0] != null) {
            if (isInt(data[0])) {
                return true;
            }
            return false;
        }
        return false;
    }

    private Boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

}
