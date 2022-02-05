package caesreon.core.handlers;

import caesreon.core.ColorCodeParser;
import caesreon.core.CoreKomponente;
import caesreon.core.hilfsklassen.PermissionUT;
import gamersocke.data.PlayerInfo;
import org.bukkit.entity.Player;
import teozfrank.ultimatevotes.util.UltimateVotesAPI;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

public class ScoreboardDataHandler {
    private static final String Servername = ColorCodeParser.ColorPrefixParagraph + "lCaesreon";
    private static final String Webseite = "https://caesreon.de";
    private static CoreKomponente modul;
    private static String Rang;
    private static String Votes;
    private static String SpielerOnline;

    private final DecimalFormat df = new DecimalFormat("0");

    public ScoreboardDataHandler(CoreKomponente modul) {
        ScoreboardDataHandler.modul = modul;
    }

    public String getServername() {
        return Servername;
    }

    public String getRang(Player p) {
        ermittelRang(p);
        return Rang;
    }

    public String getVotes(Player p) {
        ermittelVotes(p);
        return Votes;
    }

    public String getWebseite() {
        return Webseite;
    }

    public String getOnlineSpieler() {
        return SpielerOnline;
    }

    public String getKontostand(Player p) {
        double geld = modul.getMain().getMerkurKomponente().getServerWirtschaft().getCachedKontostand(p.getUniqueId());
        ermittelSpielzeit(p);
        if (Objects.equals(String.valueOf(df.format(geld)), "null")) {
            geld = 0;
        }
        return ColorCodeParser.ColorPrefixParagraph + "f" + df.format(geld);
    }

    public String getSpielzeit(Player p) {
        String spielzeit = ermittelSpielzeit(p);
        if (Objects.equals(spielzeit, "null")) {
            spielzeit = "0";
        }
        return spielzeit;
    }

    private void ermittelRang(Player p) {
        if (PermissionUT.isPlayerInGroup(p, "default")) {
            Rang = ColorCodeParser.ColorPrefixParagraph + "fReisender";
        }
        if (PermissionUT.isPlayerInGroup(p, "member")) {
            Rang = ColorCodeParser.ColorPrefixParagraph + "aBürger";
        }
        if (PermissionUT.isPlayerInGroup(p, "patron")) {
            Rang = ColorCodeParser.ColorPrefixParagraph + "2Patron";
        }
        if (PermissionUT.isPlayerInGroup(p, "protektor")) {
            Rang = ColorCodeParser.ColorPrefixParagraph + "2Protektor";
        }
        if (PermissionUT.isPlayerInGroup(p, "ehrenlegion")) {
            Rang = ColorCodeParser.ColorPrefixParagraph + "6Ehrenlegion";
        }
        if (PermissionUT.isPlayerInGroup(p, "developer")) {
            Rang = ColorCodeParser.ColorPrefixParagraph + "3Developer";
        }
        if (PermissionUT.isPlayerInGroup(p, "team")) {
            Rang = ColorCodeParser.ColorPrefixParagraph + "3Team";
        }

        if (PermissionUT.isPlayerInGroup(p, "admin_l1")) {
            Rang = ColorCodeParser.ColorPrefixParagraph + "4Admin";
        }

        if (PermissionUT.isPlayerInGroup(p, "admin")) {
            Rang = ColorCodeParser.ColorPrefixParagraph + "4Admin";
        }
        if (PermissionUT.isPlayerInGroup(p, "founder")) {
            Rang = ColorCodeParser.ColorPrefixParagraph + "6Founder";
        }

    }

    private String ermittelSpielzeit(Player p) {
        RavenDBHandler.Init();
        List<PlayerInfo> Spielzeit = RavenDBHandler.GetSession().query(PlayerInfo.class).whereEquals("PlayerName", p.getDisplayName()).toList();
        for (PlayerInfo i : Spielzeit) {
            //main.Debug("RavenData:" + String.valueOf(i.OnlineTime));
            long spielerzeit = i.OnlineTime / 1000;
            long tage = spielerzeit / 86400L;
            spielerzeit = spielerzeit - tage * 86400L;
            long stunden = spielerzeit / 3600L;
            spielerzeit = spielerzeit - stunden * 3600L;
            long minuten = spielerzeit / 60L;
            return tage + "T " + stunden + "Std " + minuten + "m";
        }
        return null;
    }

    private String ermittelVotes(Player p) {
        Votes = String.valueOf(UltimateVotesAPI.getPlayerAllTimeVotes(p.getDisplayName()));

        return Votes;
    }
}