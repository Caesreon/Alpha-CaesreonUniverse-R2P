package caesreon.core.listener;

import caesreon.core.CoreKomponente;
import caesreon.core.Log;
import caesreon.core.Spieler;
import caesreon.core.hilfsklassen.SpielerUT;
import caesreon.core.hilfsklassen.WeltUT.CheckWeltenName;
import caesreon.core.skynet.SpielerLookup;
import caesreon.universe.spigot.jupiter.Basis_Buch;
import caesreon.universe.spigot.merkur.modul.wirtschaft.WirtschaftsModul;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

//Uebernommene Klassen von dem Ehemaligen Dev GratisCobalt
public class FirstJoin implements Listener {
    private final CoreKomponente cap;

    public FirstJoin(CoreKomponente cap) {
        this.cap = cap;
    }

    //TODO: Überarbeiten und auslagern
    private static void giveStartItmes(Player p) {
        ArrayList<ItemStack> items = new ArrayList<>();
        items.add(new ItemStack(Material.WOODEN_SWORD));
        items.add(new ItemStack(Material.WOODEN_PICKAXE));
        items.add(new ItemStack(Material.WOODEN_AXE));
        items.add(new ItemStack(Material.WOODEN_SHOVEL));
        items.add(new ItemStack(Material.COOKED_BEEF, 64));

        p.getInventory().getItemInOffHand();
        if (p.getInventory().getItemInOffHand().getType() == Material.AIR) {
            p.getInventory().setItemInOffHand(new ItemStack(Material.SHIELD));
        } else {
            items.add(new ItemStack(Material.SHIELD));
        }
        if (p.getInventory().getHelmet() == null) {
            p.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
        } else {
            items.add(new ItemStack(Material.LEATHER_HELMET));
        }
        if (p.getInventory().getChestplate() == null) {
            p.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        } else {
            items.add(new ItemStack(Material.LEATHER_CHESTPLATE));
        }
        if (p.getInventory().getLeggings() == null) {
            p.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        } else {
            items.add(new ItemStack(Material.LEATHER_LEGGINGS));
        }
        if (p.getInventory().getBoots() == null) {
            p.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
        } else {
            items.add(new ItemStack(Material.LEATHER_BOOTS));
        }
        SpielerUT.Items_AnSpielerGeben(p, items);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPlayedBefore()) {
            SpielerLookup.SchliesseConnection.istInDatenbank(p);
            // Notwendig damit in der Lobby bereits das Startgeld überwiesen wird
            Spieler spieler = new Spieler(p);
            cap.getMain().getMerkurKomponente().getServerWirtschaft().ServerGeldUeberweisen(spieler, WirtschaftsModul.WirtschaftsKonfiguration.getInt("startgeld"));
            Log.SpigotLogger.Dev("Startgeld: " +  WirtschaftsModul.WirtschaftsKonfiguration.getInt("startgeld"));
            if (p.getStatistic(Statistic.LEAVE_GAME) == 0) {
                // Damit das Starterkit auch nur im Survival verteilt wird!! WICHTIG
                if (CheckWeltenName.playerListe(p, cap.getMain().getJupiterKomponente().getStarterKitConfig().getStringList("erlaubte_welten"))) {
                    giveStartItmes(p);
                }
                Basis_Buch b = new Basis_Buch(cap.getMain().getJupiterKomponente());
                b.ErstelleBuch_Tutorial(p);
                //Fügt den Spieler in das Spieler Register ein
            }
        }
    }
}
