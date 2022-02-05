package caesreon.core.minecraft;

import org.bukkit.inventory.Inventory;

/**
 * Diese Klasse beinhaltet die jeweiligen Inventare des Spielers
 */
public class Inventar {
    private Inventory spielerInventar;
    private Inventory enderchestInventar;

    public Inventory getSpielerInventar() {
        return spielerInventar;
    }

    public void setSpielerInventar(Inventory spielerInventar) {
        this.spielerInventar = spielerInventar;
    }

    public Inventory getEnderchestInventar() {
        return enderchestInventar;
    }

    public void setEnderchestInventar(Inventory enderchestInventar) {
        this.enderchestInventar = enderchestInventar;
    }
}
