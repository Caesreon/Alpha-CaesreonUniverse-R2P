package caesreon.core.skynet;

import caesreon.core.listener.BungeeListener;
import caesreon.core.CoreKomponente;
import caesreon.core.Log;
import caesreon.core.Spieler;
import caesreon.core.handlers.MySqlHandler;
import caesreon.core.handlers.MySqlHandler.ConnectionHandler;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.ZeitUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Objects;

public class SkyNetHandler {
    private final SkyNetModul modul;


    public SkyNetHandler(SkyNetModul modul) {
        this.modul = modul;
        Connection Con = ConnectionHandler.System();
        pruefeTabelle(Con);
    }

    public SpielerProfil ladeDaten() {
        return null;
    }

    public void spielerDisconnect(Player p) {
        PreparedStatement preparedUpdateStatement = null;
        Connection Con = ConnectionHandler.System();
        try {
            String data = SkyNetSQL.speichereSpielerRegisterDaten;
            preparedUpdateStatement = Con.prepareStatement(data);
            preparedUpdateStatement.setLong(1, ZeitUtils.erhalteAktuellenZeitstempel());
            preparedUpdateStatement.setString(2, p.getUniqueId().toString());
            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    protected void info(Player p, Spieler spieler) {
        if (CoreKomponente.istBungeecordModus()) {
            BungeeListener bungeeListener = new BungeeListener();
        }
        try {
            String zuletzt_gesehen = String.valueOf(ZeitUtils.erhalteDatevonZeitstempel(spieler.getZuletzt_gesehen()));
            if (spieler.getZuletzt_gesehen() == 0)
                zuletzt_gesehen = "Unbekannt";
            ChatSpigot.NachrichtSenden(p, modul.prefix, ChatSpigot.chatTrennerModulBefehle(ChatColor.DARK_PURPLE, ChatColor.RED + ""));
            ChatSpigot.NachrichtSenden(p, modul.prefix, "Spielername: " + ChatColor.BLUE  + spieler.getSpielername());
            ChatSpigot.NachrichtSenden(p, modul.prefix, "UUID: " + ChatColor.GRAY + spieler.getUuid());
            ChatSpigot.NachrichtSenden(p, modul.prefix, "Erstmals gesehen: " + ChatColor.GRAY + ZeitUtils.erhalteDatevonZeitstempel(spieler.getZuerst_gesehen()));
            ChatSpigot.NachrichtSenden(p, modul.prefix, "Zuletzt gesehen: " + ChatColor.GRAY + zuletzt_gesehen);
            ChatSpigot.NachrichtSenden(p, modul.prefix, "Kontostand: " + ChatColor.DARK_GREEN + modul.getMain().getMerkurKomponente().getServerWirtschaft().getCachedKontostand(spieler.getUuid()));
        } catch (Exception exception) {
            Log.SpigotLogger.Debug(exception.toString());
        }
    }

    protected void vergleicheIPSpieler(Player p, String spielername1, String spielername2)
    {
        Spieler spieler1 = new Spieler(spielername1);
        Spieler spieler2 = new Spieler(spielername2);
        if (Objects.requireNonNull(spieler1.getBukkitSpieler().getAddress()).getHostName().equals(Objects.requireNonNull(spieler2.getBukkitSpieler().getAddress()).getHostName()))
            ChatSpigot.NachrichtSenden(p, modul.prefix, "Spieler teilen sich die selbe IP");
        ChatSpigot.NachrichtSenden(p, modul.prefix, "Spieler teilen sich nicht die selbe IP");
    }

    private void pruefeTabelle(Connection Con) {
        Log.SpigotLogger.Debug(SkyNetSQL.erstelleTabelle);
        MySqlHandler.pruefeObTabelleExistiert(Con, SkyNetSQL.erstelleTabelle);
        ConnectionHandler.closeConnection(Con);
    }

    protected class SpielerProfil {
        String uuid;
        String spielername;
        int kontostand;
        long zuerst_gesehen;
        long zuletzt_gesehen;
        String ip_adresse;
        Inventory inventar;
    }

}
