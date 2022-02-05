package caesreon.universe.spigot.merkur;

import caesreon.core.Log;
import caesreon.core.interfaces.ILog;
import caesreon.main.SpigotMain;
import caesreon.universe.spigot.merkur.modul.berufe.BerufeCommands;
import caesreon.universe.spigot.merkur.modul.berufe.BerufeModul;
import caesreon.universe.spigot.merkur.modul.regionmarkt.RegionMarktCommands;
import caesreon.universe.spigot.merkur.modul.regionmarkt.RegionMarktMenuManager;
import caesreon.universe.spigot.merkur.modul.regionmarkt.RegionMarktModul;
import caesreon.universe.spigot.merkur.modul.shops.SchilderHandelsModul;
import caesreon.universe.spigot.merkur.modul.wirtschaft.Wirtschaft;
import caesreon.universe.spigot.merkur.modul.wirtschaft.WirtschaftsModul;
import caesreon.universe.spigot.merkur.modul.wirtschaft.handler.WirtschaftsPlatzhalter;

/*
 * Die Merkur Komponente umfasst alle der Server-Wirtschaft zugehoerigen Module.
 *
 */
public class MerkurKomponente {
    private SpigotMain main;
    private RegionMarktModul regionMarktModul;
    private SchilderHandelsModul schilderHandelsModul;
    private BerufeModul berufeModul;
    private WirtschaftsModul wirtschaftsModul;

    public MerkurKomponente(SpigotMain m) {
        main = m;

        Log.SpigotLogger.Init("Komponente: Initialisiere Merkur (Wirtschafts) Komponente..");
        try {
            wirtschaftsModul = new WirtschaftsModul(this);
            regionMarktModul = new RegionMarktModul(this);
            schilderHandelsModul = new SchilderHandelsModul(this);
            berufeModul = new BerufeModul(this);

            Log.SpigotLogger.Info("Registriere Merkur Events und Listener");
            main.getServer().getPluginManager().registerEvents(new RegionMarktMenuManager(regionMarktModul), main);

            Log.SpigotLogger.Init("Registriere /gs {...}");
            SpigotMain.getSpigotMainInstance().getCommand("gs").setExecutor(new RegionMarktCommands(regionMarktModul));
            Log.SpigotLogger.Init("Registriere /beruf {...}");
            SpigotMain.getSpigotMainInstance().getCommand("beruf").setExecutor(new BerufeCommands(berufeModul));
            setupPlaceholderApi();

            Log.SpigotLogger.InitErfolgreich("Komponente: Merkur Komponente erfolgreich initialisiert");
        } catch (Exception e) {
            Log.SpigotLogger.Error("Komponente:  Konnte nicht initialisiert werden");
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    private void setupPlaceholderApi() {
        if (getMain().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            //Registering placeholder will be use here
            new WirtschaftsPlatzhalter(this).register();
        }
    }

    public BerufeModul getBerufeModul() {
        return berufeModul;
    }

    public RegionMarktModul getRegionMerktModul() {
        return regionMarktModul;
    }

    public Wirtschaft getServerWirtschaft() {
        return wirtschaftsModul.getServerWirtschaft();
    }

    public WirtschaftsModul getWirtschaftsModul() {
        return wirtschaftsModul;
    }

    public SpigotMain getMain() {
        return main;
    }
}
