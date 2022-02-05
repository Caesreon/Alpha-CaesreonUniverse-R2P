package caesreon.universe.spigot.merkur.modul.regionmarkt;

import caesreon.core.ColorCodeParser;
import caesreon.core.Log;
import caesreon.core.handlers.AgentConfigHandler;
import caesreon.core.handlers.KonfigurationsDatenSatz;
import caesreon.main.SpigotMain;
import caesreon.universe.spigot.merkur.MerkurKomponente;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.StringUtil;

import java.util.*;


public class RegionMarktModul {

    //Regionmarkt Schild
    protected final String Verkauft = "[Verkauft]";
    protected final String Kaufen = "[Erwerben]";
    protected final String Mieten = "[Mieten]";
    protected final String Vermietet = "[Vermietet]";
    protected final String Identifier = "[GS]";
    protected final String Verkaufen = "Verkaufen";
    final String Farbiger_Identifier = ChatColor.GOLD + "[GS]";
    final List<String> erlaubteWelten;
    final String configRootFreebuild = "gs_typ.freebuild";
    protected String Prefix;
    protected String Regex;
    protected String Permissions_Spieler;
    protected String Permissions_Ehrenlegion;
    private final RegionMarktHandler handler;
    private final MenuManager menuManager;
    private YamlConfiguration config;
    private final MerkurKomponente ankermodul;

    public RegionMarktModul(MerkurKomponente Ankermodul) {
        this.ankermodul = Ankermodul;
        Log.SpigotLogger.Init("Modul: Initialisiere Modul Regionmarkt..");

        // Konfiguration laden
        KonfigurationsDatenSatz regionMarktConfigSet = new KonfigurationsDatenSatz(null,
                AgentConfigHandler.RootOrdner.Modulkonfigurationen +
                        AgentConfigHandler.FileSeperator("Merkur"), "RegionmarktSettings");
        config = AgentConfigHandler.getYamlConfigurationDurchKonfigurationsDatenSatz(regionMarktConfigSet);

        this.Regex = config.getString("allgemein.regex");
        this.Prefix = ColorCodeParser.ParseString(Objects.requireNonNull(config.getString("allgemein.prefix")), Regex);
        this.Permissions_Ehrenlegion = config.getString("permissions.ehrenlegion");
        this.Permissions_Spieler = config.getString("permissions.spieler");
        this.erlaubteWelten = config.getStringList("erlaubte_welten");


        // Handler erstellen
        handler = new RegionMarktHandler(this);
        menuManager = new MenuManager(this);

        // Commands aktivieren
        PluginCommand command = ankermodul.getMain().getCommand("gs");
        assert command != null;
        command.setAliases(Arrays.asList("rm", "plotmarkt", "regionmarkt"));
        command.setTabCompleter((sender, command1, label, args) -> {
            if (args.length == 1)
            {
                List<String> completions = new ArrayList<String>();
                completions.add("info");
                completions.add("limits");
                completions.add("kaufen");
                completions.add("verkaufen");
                completions.add("auflösen");
                completions.add("claim");
                completions.add("claims");
                completions.add("hinzufügen");
                completions.add("entfernen");
                completions.add("hinzufügen member");
                completions.add("entfernen member");
                completions.add("hinzufügen owner");
                completions.add("entfernen owner");
                completions.add("hinzufügen member alle");
                completions.add("entfernen member alle");
                completions.add("hinzufügen owner alle");
                completions.add("entfernen owner alle");
                completions.add("+ -o -a");
                completions.add("- -o -a");
                completions.add("+ -m -a");
                completions.add("- -m -a");
                return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
            } else {
                return Collections.emptyList();
            }

        });
        command.setExecutor(new RegionMarktCommands(this));

        // Listener aktivieren
        ankermodul.getMain().getServer().getPluginManager().registerEvents(new RegionSchild(this), ankermodul.getMain());
        Log.SpigotLogger.InitErfolgreich("Modul: Regionmarkt erfolgreich initialisiert");
    }

    ;

    public SpigotMain getMain() {
        return ankermodul.getMain();
    }

    public YamlConfiguration getRegionMarktConfig() {
        return config;
    }

    public RegionMarktHandler getRegionMarketHandler() {
        return handler;
    }

    public MerkurKomponente getMerkurKomponente() {
        return getMain().getMerkurKomponente();
    }

    public MenuManager getMenuManager() {
        return menuManager; }

    public enum gs_typen {starter_gs, buerger_gs, legion_gs, freebuild_gs, villen_gs, shop_gs}
}
