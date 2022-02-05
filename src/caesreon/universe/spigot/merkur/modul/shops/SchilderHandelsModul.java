package caesreon.universe.spigot.merkur.modul.shops;

import caesreon.core.Log;
import caesreon.core.handlers.AgentConfigHandler;
import caesreon.core.handlers.KonfigurationsDatenSatz;
import caesreon.main.SpigotMain;
import caesreon.universe.spigot.merkur.MerkurKomponente;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class SchilderHandelsModul {
    String colorCodePrefix; //Hier definiert um bei Konvertierungsproblemen so wenig wie moeglich Fehler zu haben
    String kaufen;
    String verkaufen;
    String adminShopKaufen;
    String adminShopVerkaufen;
    String firmenBezeichner; //Alternativ: Kaufhaus, Laden, Warenhaus, Unternehmen, Lokal, Filiale, Geschäft PS Umlaute vermeiden
    String permissionNode;
    String prefix;
    List<String> erlaubteWelten;
    private final MerkurKomponente modul;
    private ShopHandler shopHandler;
    private ShopCreationListener shopErstellungsListener;
    private final YamlConfiguration shopConfig;

    public SchilderHandelsModul(MerkurKomponente modul) {
        this.modul = modul;
        // Konfiguration laden
        KonfigurationsDatenSatz shopAllgemeinConfigSet = new KonfigurationsDatenSatz(null,
                AgentConfigHandler.RootOrdner.Modulkonfigurationen +
                        AgentConfigHandler.FileSeperator("Merkur"),
                konfigurationsDateien.ShopsAllgemeinSettings.toString());
        shopConfig = AgentConfigHandler.getYamlConfigurationDurchKonfigurationsDatenSatz(shopAllgemeinConfigSet);

        if (shopConfig.getBoolean("allgemein.aktiviert")) {
            kaufen = shopConfig.getString("schilder.beschriftungen.kaufen");
            verkaufen = shopConfig.getString("schilder.beschriftungen.verkaufen");
            adminShopKaufen = shopConfig.getString("schilder.beschriftungen.adminShopKaufen");
            adminShopVerkaufen = shopConfig.getString("schilder.beschriftungen.adminShopVerkaufen");
            firmenBezeichner = shopConfig.getString("firmenbezeichner");
            permissionNode = shopConfig.getString("permissions.spieler");
            colorCodePrefix = shopConfig.getString("allgemein.regex");
            prefix = shopConfig.getString("allgemein.prefix");
            erlaubteWelten = shopConfig.getStringList("erlaubte_welten");
            Log.SpigotLogger.Debug(colorCodePrefix);
            shopErstellungsListener = new ShopCreationListener(this);
            shopHandler = new ShopHandler(this);

            //Registriere Event Listener
            modul.getMain().getServer().getPluginManager().registerEvents(new ShopSchild(this), modul.getMain());
            modul.getMain().getServer().getPluginManager().registerEvents(new ShopCreationListener(this), modul.getMain());
        }
    }

    public SpigotMain getMain() {
        return modul.getMain();
    }

    public YamlConfiguration getShopConfig() {
        return shopConfig;
    }

    public ShopHandler getShopHandler() {
        return shopHandler;
    }

    public MerkurKomponente getWirtschaftsKomponente() {
        return modul;
    }

    protected enum konfigurationsDateien {ShopsAllgemeinSettings}

}
