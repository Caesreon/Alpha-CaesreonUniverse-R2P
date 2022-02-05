package caesreon.universe.spigot.jupiter;

import caesreon.core.Log;
import caesreon.core.handlers.ScoreboardDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.*;

import java.util.Objects;

public class Scoreboards implements Listener {
    private Scoreboard board = null;
    private Objective obj = null;
    private final JupiterKomponente modul;

    public Scoreboards(JupiterKomponente modul) {
        this.modul = modul;
    }

    public void erstelleScoreBoardLobby(Player p, ScoreboardDataHandler Data) {
        board = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        //TODO: Testen
        obj = board.registerNewObjective("aaa", "bbb", "ccc");
        obj.setDisplayName("§6" + Data.getServername());
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score Platzhalter0 = obj.getScore("§0");
        Score Name = obj.getScore("§3Name:");
        Score SpielerName = obj.getScore("§f" + p.getName());
        Score Platzhalter1 = obj.getScore("§1");

        Score Rang = obj.getScore("§3Rang: " + Data.getRang(p));
        //Score SpielerRang = obj.getScore("§8" + Data.getRang(p));
        Score Platzhalter2 = obj.getScore("§2");

        Team geld = board.getTeam("Kontostand") == null ? board.registerNewTeam("Kontostand") : board.getTeam("Kontostand");
        assert geld != null;
        geld.setPrefix("§3Kontostand: §f" + Data.getKontostand(p) + "$");
        geld.addEntry("§r");
        Score SpielerKontostand = obj.getScore("§r");

        Team votes = board.getTeam("Votes") == null ? board.registerNewTeam("Votes") : board.getTeam("Votes");
        assert votes != null;
        votes.setPrefix("§3Deine Votes: §f" + Data.getVotes(p));
        votes.addEntry("§v");
        Score Votes = obj.getScore("§v");

        Team zeit = board.getTeam("Spielzeit") == null ? board.registerNewTeam("Spielzeit") : board.getTeam("Spielzeit");
        assert zeit != null;
        zeit.setPrefix("§3Spielzeit: §f" + Data.getSpielzeit(p));
        zeit.addEntry("§s");
        Score Spielzeit = obj.getScore("§s");
        Score Platzhalter3 = obj.getScore("§3");
        Score Webseite = obj.getScore("§3Webseite:");
        Score WebseiteURL = obj.getScore("§6" + Data.getWebseite());
        Score Platzhalter4 = obj.getScore("§4");

        Platzhalter0.setScore(11);
        Name.setScore(10);
        SpielerName.setScore(9);
        Platzhalter1.setScore(8);
        Rang.setScore(7);
        Platzhalter2.setScore(6);
        SpielerKontostand.setScore(5);
        Votes.setScore(4);
        Spielzeit.setScore(3);
        Platzhalter3.setScore(2);
        Webseite.setScore(1);
        WebseiteURL.setScore(0);
        Platzhalter4.setScore(-1);
        p.setScoreboard(board);
    }

    public Scoreboard getScoreboard() {
        return board;
    }

    public void updateScoreboard(Player p, ScoreboardDataHandler Data) {
        Scoreboard sb = getScoreboard();
        if (sb == null) {
            Log.SpigotLogger.Debug("Scoreboard war NPE");
            return;
        }
        Objects.requireNonNull(sb.getTeam("Kontostand")).setPrefix("§3Kontostand: §f" + Data.getKontostand(p) + "$");
        Objects.requireNonNull(sb.getTeam("Votes")).setPrefix("§3Deine Votes: §f" + Data.getVotes(p));
        Objects.requireNonNull(sb.getTeam("Spielzeit")).setPrefix("§3Spielzeit: §f" + Data.getSpielzeit(p));
    }
}