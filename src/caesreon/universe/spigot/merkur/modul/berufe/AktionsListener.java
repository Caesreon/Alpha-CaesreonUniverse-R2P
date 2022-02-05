package caesreon.universe.spigot.merkur.modul.berufe;

import caesreon.core.Log;
import caesreon.core.Spieler;
import caesreon.core.hilfsklassen.PermissionUT;
import caesreon.universe.spigot.merkur.modul.berufe.BerufeHandler.ErmittelEntlohnendeBerufe;
import caesreon.universe.spigot.merkur.modul.berufe.BerufeModul.Handlungen;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.Objects;

public class AktionsListener implements Listener {
    BerufeModul modul;

    public AktionsListener(BerufeModul modul) {
        this.modul = modul;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void bloeckeAbbauenEvent(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player spieler = e.getPlayer();

        if (spieler.hasPermission(modul.permissionNode) && !PermissionUT.istZweitAccount(spieler) && !e.isCancelled())
        {
            String itemInHand = null;
            try {
                itemInHand = Objects.requireNonNull(e.getPlayer().getInventory().getItemInMainHand().getItemMeta()).getDisplayName();
            } catch (Exception ex) {
            }

            if (spieler.getGameMode().equals(GameMode.SURVIVAL)) {
                try {
                    if (!e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
                    }
                } catch (Exception exception) {

                } finally {

                    BerufeSatz entlohnendeBerufe = ErmittelEntlohnendeBerufe.abbauen(block);
                    if (block.getBlockData() instanceof Ageable) {
                        Ageable alter = (Ageable) block.getBlockData();
                        entlohnendeBerufe = ErmittelEntlohnendeBerufe.abbauen(block, alter.getAge());
                        entlohneBerufeAgeables(spieler, Handlungen.abbauen.toString(), block.getBlockData(), alter.getAge(), itemInHand, entlohnendeBerufe);
                    }

                    if (!(block.getBlockData() instanceof Ageable) && entlohnendeBerufe != null) {
                        modul.getBerufeHandler().entlohneBerufe(spieler, Handlungen.abbauen.toString(), block.getBlockData().getMaterial().toString(), entlohnendeBerufe, itemInHand);
                    }
                    //modul.getBerufeHandler().entlohneBerufe(spieler, Handlungen.abbauen.toString(), block.getBlockData().getMaterial().toString(), entlohnendeBerufe, itemInHand);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void bloeckeSetzenEvent(BlockPlaceEvent e) {
        Block block = e.getBlock();
        Player spieler = e.getPlayer();
        if (spieler.hasPermission(modul.permissionNode) && !PermissionUT.istZweitAccount(spieler)&& !e.isCancelled()) {
            if (spieler.getGameMode().equals(GameMode.SURVIVAL)) {
                BerufeSatz entlohnendeBerufe = ErmittelEntlohnendeBerufe.bauen(block);
                if (block.getBlockData() instanceof Ageable) {
                    Ageable alter = (Ageable) block.getBlockData();
                    entlohnendeBerufe = ErmittelEntlohnendeBerufe.abbauen(block, alter.getAge());
                    entlohneBerufeAgeables(spieler, Handlungen.bauen.toString(), block.getBlockData(), alter.getAge(), null, entlohnendeBerufe);
                }
                modul.getBerufeHandler().entlohneBerufe(spieler, Handlungen.bauen.toString(), block.getBlockData().getMaterial().toString(), entlohnendeBerufe, null);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void itemsCraften(CraftItemEvent e) {

    }

    @EventHandler(priority = EventPriority.LOW)
    public void entitätenToetung(EntityDeathEvent e) {
        String entity = e.getEntity().getName().toLowerCase();
        Spieler spieler = null;
        try {
            spieler = new Spieler(Objects.requireNonNull(e.getEntity().getKiller()));
        } catch (Exception exception) {
            Log.SpigotLogger.Debug(exception.toString());
        }
        if (spieler != null)
        {
            BerufeSatz entlohnendeBerufe = ErmittelEntlohnendeBerufe.toeten(e.getEntity());
            modul.getBerufeHandler().entlohneBerufe(spieler.getBukkitSpieler(), Handlungen.toeten.toString(), entity, entlohnendeBerufe, null);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void entityZaehmenEvent(EntityTameEvent e) {
        try {
            Spieler spieler = new Spieler(e.getOwner().getUniqueId());
            BerufeSatz entlohnendeBerufe = ErmittelEntlohnendeBerufe.zaehmen(e.getEntity());
        } catch (Exception ex)
        {
            Log.SpigotLogger.Debug(ex.toString());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void entityZuechtenEvent(EntityBreedEvent e) {
        try {
            Spieler spieler = new Spieler(Objects.requireNonNull(e.getBreeder()).getUniqueId());
            BerufeSatz entlohnendeBerufe = ErmittelEntlohnendeBerufe.zuechten(e.getEntity());
        } catch (Exception ex)
        {
            Log.SpigotLogger.Debug(ex.toString());
        }
    }

    private void entlohneBerufeAgeables(Player spieler, String handlung, BlockData blockdata, int alter, String itemInHand, BerufeSatz entlohnendeBerufe) {
        switch (alter) {
            case 0:
                modul.getBerufeHandler().entlohneBerufe(spieler, handlung, blockdata.getMaterial() + "_0", entlohnendeBerufe, itemInHand);
                break;
            case 2:
                //Cocoa
                modul.getBerufeHandler().entlohneBerufe(spieler, handlung, blockdata.getMaterial() + "_2", entlohnendeBerufe, itemInHand);
                break;
            case 3:
                //Beetroot
                modul.getBerufeHandler().entlohneBerufe(spieler, handlung, blockdata.getMaterial() + "_3", entlohnendeBerufe, itemInHand);
                break;
            case 7:
                //Sweetberries, Weizen, melon_stem, carrot, potato
                modul.getBerufeHandler().entlohneBerufe(spieler, handlung, blockdata.getMaterial() + "_7", entlohnendeBerufe, itemInHand);

            default:
        }
    }
}
