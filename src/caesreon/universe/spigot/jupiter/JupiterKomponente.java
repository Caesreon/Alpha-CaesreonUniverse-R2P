package caesreon.universe.spigot.jupiter;

import caesreon.core.COMMANDS;
import caesreon.core.Log;
import caesreon.core.handlers.AgentConfigHandler;
import caesreon.core.handlers.KonfigurationsDatenSatz;
import caesreon.core.interfaces.ILog;
import caesreon.universe.spigot.jupiter.modul.fly.FlyModul;
import caesreon.universe.spigot.jupiter.modul.loginbelohnungen.BelohnungenModul;
import caesreon.universe.spigot.jupiter.modul.mail.Mail;
import caesreon.universe.spigot.jupiter.modul.mail.MailCommand;
import caesreon.universe.spigot.jupiter.modul.mail.MailHandler;
import caesreon.universe.spigot.jupiter.modul.teleportation.TeleportationsModul;
import caesreon.universe.spigot.jupiter.modul.userwerbenuser.UserWerbenUserModul;
import caesreon.universe.spigot.jupiter.modul.pvp.PvPModul;
import caesreon.main.SpigotMain;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.Objects;

public class JupiterKomponente {
    private static MailHandler mailHandler;
    private final SpigotMain mainClass;
    public List<String> erlaubteWeltenMenu;
    String Prefix;
    String Regex;
    private UserWerbenUserModul userWerbenUserModul;
    private BelohnungenModul belohnungenModul;
    private TeleportationsModul teleportModul;
    private LobbyMenu lobbyMenu;
    private LobbyMenuManager lobbyMenuManager;
    private Scoreboards sc;
    private Mail m;
    private FlyModul flyModul;
    private YamlConfiguration infoBuchConfig;
    private YamlConfiguration menuConfig;
    private YamlConfiguration starterkitConfig;

    public JupiterKomponente(SpigotMain main) {
        this.mainClass = main;

        Log.SpigotLogger.Init("Komponente: Initialisiere Jupiter Komponente..");

        try {
            // Konfigurationen laden
            KonfigurationsDatenSatz infoBuchConfigSet = new KonfigurationsDatenSatz(null, AgentConfigHandler.RootOrdner.Bücher.toString(), "infobuch");
            infoBuchConfig = AgentConfigHandler.getYamlConfigurationDurchKonfigurationsDatenSatz(infoBuchConfigSet);

            KonfigurationsDatenSatz lobbyMenuConfigSet = new KonfigurationsDatenSatz(null, AgentConfigHandler.RootOrdner.Menü.toString(), "Lobby");
            menuConfig = AgentConfigHandler.getYamlConfigurationDurchKonfigurationsDatenSatz(lobbyMenuConfigSet);

            KonfigurationsDatenSatz starterkitConfigSet = new KonfigurationsDatenSatz(null, AgentConfigHandler.RootOrdner.Modulkonfigurationen
                    + AgentConfigHandler.FileSeperator("Jupiter"), "Starterkit");
            starterkitConfig = AgentConfigHandler.getYamlConfigurationDurchKonfigurationsDatenSatz(starterkitConfigSet);


            //Initialisieren der Submodule
            userWerbenUserModul = new UserWerbenUserModul(this);
            belohnungenModul = new BelohnungenModul(this);
            flyModul = new FlyModul(this);
            teleportModul = new TeleportationsModul(this);
            PvPModul pvpModul = new PvPModul(this);

            this.Regex = infoBuchConfig.getString("regex");
            this.Prefix = getMain().getCore().getDefaultConfig().getString("ingameprefix");
            erlaubteWeltenMenu = menuConfig.getStringList("erlaubte_welten");


            // Handler erstellen
            lobbyMenu = new LobbyMenu(this);
            m = new Mail(this);
            mailHandler = new MailHandler(this);
            sc = new Scoreboards(this);

            // Listener aktivieren
            Log.SpigotLogger.Init("Registriere Jupiter Events und Listener");
            main.getServer().getPluginManager().registerEvents(new Basis_Schilder(this), main);
            main.getServer().getPluginManager().registerEvents(new LobbyMenuManager(this), main);
            main.getServer().getPluginManager().registerEvents(new Scoreboards(this), main);
            main.getServer().getPluginManager().registerEvents(new Elderstab(), main);

            submodul_Befehle(getMain().getCore().getDefaultConfig().getBoolean("befehle"));
            submodul_Mail();
            Log.SpigotLogger.InitErfolgreich("Komponente: Jupiter erfolgreich initialisiert");
        } catch (Exception e) {
            Log.SpigotLogger.Error("Komponente konnte nicht initialisiert werden");
            Log.SpigotLogger.Debug(e.toString());
        }

    }

    private void submodul_Befehle(Boolean aktiviert) {
        if (aktiviert) {
            Log.SpigotLogger.Init("Registriere Basis Spieler-Commands...");
            for (COMMANDS.BASIS i : COMMANDS.BASIS.values()) {
                try {
                    Log.SpigotLogger.Info("Registriere /" + i.toString());
                    Objects.requireNonNull(SpigotMain.getSpigotMainInstance().getCommand(i.toString())).setExecutor(new ServerBefehle(getMain().getCore().getDefaultConfig()));
                } catch (Exception e) {
                    Log.SpigotLogger.Debug(e.toString());
                }
            }
            Log.SpigotLogger.Init("Registriere Erweiterte Spieler-Commands...");
            for (COMMANDS.ERWEITERT i : COMMANDS.ERWEITERT.values()) {
                try {
                    Log.SpigotLogger.Info("Registriere /" + i.toString());
                    Objects.requireNonNull(SpigotMain.getSpigotMainInstance().getCommand(i.toString())).setExecutor(new ServerBefehle(getMain().getCore().getDefaultConfig()));
                } catch (Exception e) {
                    Log.SpigotLogger.Debug(e.toString());
                }
            }

            Log.SpigotLogger.Info("Registriere /infobuch");
            Objects.requireNonNull(SpigotMain.getSpigotMainInstance().getCommand("infobuch")).setExecutor(new Basis_Buch(this));
            Log.SpigotLogger.Info("Registriere /cae");
            Objects.requireNonNull(SpigotMain.getSpigotMainInstance().getCommand("cae")).setExecutor(new ReloadConfigCommand(this));
            Log.SpigotLogger.Info("Registriere /es");
            Objects.requireNonNull(SpigotMain.getSpigotMainInstance().getCommand("es")).setExecutor(new Elderstab());

            return;
        }
        Log.SpigotLogger.Info("Submodul Befehle deaktiviert");
    }


    private void submodul_Mail() {
        try {
            Log.SpigotLogger.Info("Registriere /" + COMMANDS.BASIS.MAIL.mail);
            Objects.requireNonNull(SpigotMain.getSpigotMainInstance().getCommand(COMMANDS.BASIS.MAIL.mail.toString())).setExecutor(new MailCommand(this));
            Log.SpigotLogger.InitErfolgreich("Modul Mail aktiviert");
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    public UserWerbenUserModul getUserWerbenUserModul() {
        return userWerbenUserModul;
    }

    public BelohnungenModul getBelohnungenModul() {
        return belohnungenModul;
    }

    public SpigotMain getMain() {
        return mainClass;
    }

    public YamlConfiguration getInfoBuchConfig() {
        return infoBuchConfig;
    }

    public YamlConfiguration getStarterKitConfig() {
        return starterkitConfig;
    }

    public YamlConfiguration getMenuConfig() {
        return menuConfig;
    }


    public LobbyMenu getLobbyMenu() {
        return lobbyMenu;
    }

    public LobbyMenuManager getLobbyMenuManager() {
        return lobbyMenuManager;
    }


    public MailHandler getMailHandler() {
        return mailHandler;
    }

    public Mail getMail() {
        return m;
    }

    public Scoreboards getScoreboard() {
        return sc;
    }

    public YamlConfiguration getLobbyMenuConfig() {
        return menuConfig;
    }

    public FlyModul getFlyModul() {
        return flyModul;
    }

    public TeleportationsModul getTeleportModul() {
        return teleportModul;
    }
}
