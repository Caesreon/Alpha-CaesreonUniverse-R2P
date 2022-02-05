package caesreon.core.interfaces;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public interface IMenuManager {
    /**
     * @implNote Jedes zu  prüfende Menü muss hier mit If + Else If Statements eingetragen werden,
     * damit die Logiken sauber getrennt bleiben und If Events nicht mehrfach feuern!
     * @param e Das Inventar Klick Event eines beliebigen Spielers
     */
    @EventHandler
    void onInventarClick(final InventoryClickEvent e);

    /**
     * Diese Methode ist in jedem einzelnen Menu Manager zwingend notwendig, damit das Event im Zweifelsfall gecancelt
     * werden kann. Wird e.setCancelled() nicht genutzt, kann der Spieler jedes erdenkliche Item aus dem Menü heraus
     * holen!
     * @param e Das Inventar Drag Event eines beliebigen Spielers
     */
    @EventHandler
    void onInventarDrag(final InventoryDragEvent e);
}
