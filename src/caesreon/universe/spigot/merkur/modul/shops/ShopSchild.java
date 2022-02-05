package caesreon.universe.spigot.merkur.modul.shops;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ShopSchild implements Listener {

    private final SchilderHandelsModul modul;

    public ShopSchild(SchilderHandelsModul modul) {
        this.modul = modul;
    }

    @EventHandler
    public void schildZerstörtEvent(BlockBreakEvent e)
    {
        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_AXE))
        {
            Block b = e.getBlock();
            if ((b.getType().name().contains("_SIGN"))) {
                Sign s = (Sign) b.getState();
                if (s.getLine(0).contains("aufen"))
                {
                    modul.getShopHandler().entferneShop(e.getPlayer(), s);
                }
            }
        }
    }

    @EventHandler
    public void SchildInteraktionClick(PlayerInteractEvent e) {
        //TODO: Logik fuer if Type = GoldHoe dann Shop loeschen
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            //Wenn Linksklick wird hier die Methode wieder verlassen
            return;
        }

        Player p = e.getPlayer();
        Block b = e.getClickedBlock();

        assert b != null;
        if ((b.getType().name().contains("_SIGN"))) {
            Sign s = (Sign) b.getState();

            if (s.getLine(0).equals(modul.colorCodePrefix + "2" + modul.kaufen) ||
                            s.getLine(0).equals(modul.colorCodePrefix+ "4" + modul.kaufen)) {

                //Kunde kauft von Verkauefer Logik
                modul.getShopHandler().kaufen(s, p);
            }
            if (s.getLine(0).equals(modul.colorCodePrefix + "1" + modul.verkaufen) ||
                    s.getLine(0).equals(modul.colorCodePrefix + "4" + modul.verkaufen)) {
                //Ladenbesitzer kauft von Kunde Logik
                modul.getShopHandler().verkaufen(s, p);
            }
            //==============ADMINSHOP==================================
            if (s.getLine(0).equals(modul.colorCodePrefix + "2" + modul.adminShopKaufen)) {
                //Kunde kauft von Adminshop
                modul.getShopHandler().adminShopKaufen(s, p);
            }
            if (s.getLine(0).equals(modul.colorCodePrefix + "1" + modul.adminShopVerkaufen)) {
                //Adminshop kauft von Kunde Logik
                modul.getShopHandler().adminShopVerkaufen(s, p);
            }
        }


    }
}
