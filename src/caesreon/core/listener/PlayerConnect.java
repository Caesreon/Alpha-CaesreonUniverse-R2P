package caesreon.core.listener;

import caesreon.core.Bossbar;
import caesreon.core.CoreKomponente;
import caesreon.core.Log;
import caesreon.core.skynet.SpielerLookup;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;


public class PlayerConnect implements Listener {
    private static CoreKomponente modul;
    //TODO: Server-Hauptwelten ueber Configfile einlesen
    private final String Weltenname = "Hub";

    public PlayerConnect(CoreKomponente modul) {
        this.modul = modul;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ladeEconomy(event);
        SpielerLookup.SchliesseConnection.istInDatenbank(event.getPlayer());
        ladeScoreBoard(event);
        syncMail(event);
        updateScoreBoard(event);
        modul.getMain().getJupiterKomponente().getLobbyMenu().onJoin(event.getPlayer());
        checkTagesBelohnung(event);
        setBossBar(event);
        ladeBerufe(event);
    }

    private void ladeScoreBoard(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (modul.getDependencyHandler().CheckWeltName(p, Weltenname)) {
            modul.getMain().getJupiterKomponente().getScoreboard().erstelleScoreBoardLobby(p, modul.getScoreBoardDataHandler());
        }
    }

    private void updateScoreBoard(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (modul.getDependencyHandler().CheckWeltName(p, Weltenname)) {
            BukkitScheduler scheduler = modul.getMain().getServer().getScheduler();
            scheduler.runTaskLaterAsynchronously(modul.getMain(), () -> modul.getMain().getJupiterKomponente().getScoreboard().updateScoreboard(p, modul.getScoreBoardDataHandler()), 60L);
        }
    }

    private void syncMail(PlayerJoinEvent event) {
        BukkitScheduler scheduler = modul.getMain().getServer().getScheduler();
        scheduler.runTaskLaterAsynchronously(modul.getMain(), () -> modul.getMain().getJupiterKomponente().getMailHandler().MailEmpfangen(event.getPlayer()), 150L);
    }

    private void ladeBerufe(PlayerJoinEvent event) {
        BukkitScheduler scheduler = modul.getMain().getServer().getScheduler();
        scheduler.runTaskLaterAsynchronously(modul.getMain(), () -> {
            if (event.getPlayer().isOnline()) {
                Player p = event.getPlayer();
                modul.getMain().getMerkurKomponente().getBerufeModul().getBerufeHandler().spielerBeitritt(p);
            }

        }, 15L);
    }

    private void ladeEconomy(final PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (event.getPlayer().isOnline()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(modul.getMain(), () -> {
                modul.getMain().getMerkurKomponente().getWirtschaftsModul().getEconomySQLHandler().spielerBeitritt(p);
                //syncCompleteTask(p);
            }, 10L);
        }
    }

    private void checkTagesBelohnung(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(modul.getMain(), () -> modul.getMain().getJupiterKomponente().getBelohnungenModul().getBelohnungsHandler().belohnungsReminderOnLogin(event.getPlayer()), 70L);
    }

    @Deprecated
    private void syncCompleteTask(final Player p) {
        Log.SpigotLogger.Debug("syncCompleteTask()");
		  /*if (p != null && p.isOnline())
		    {
		      final long startTime = System.currentTimeMillis();
		      BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)modul.getMain(), new Runnable()
		      {
		            public void run() 
		            {
		              if (p.isOnline())
		              {
		            	  if (modul.getMain().getMerkurKomponente().getWirtschaftsModul().getEconomyDataHandler().isSyncComplete(p))
			                {
			                  if (PlayerConnect.modul.getMain().syncCompleteTasks.containsKey(p)) 
			                  {
			                    int taskID = ((Integer)PlayerConnect.modul.getMain().syncCompleteTasks.get(p)).intValue();
			                    PlayerConnect.modul.getMain().syncCompleteTasks.remove(p);
			                    Bukkit.getScheduler().cancelTask(taskID);
			                  } 
			                } 
			                else if (System.currentTimeMillis() - startTime >= 10000L && PlayerConnect.modul.getMain().syncCompleteTasks.containsKey(p))
			                {
			                  int taskID = ((Integer)PlayerConnect.modul.getMain().syncCompleteTasks.get(p)).intValue();
			                  PlayerConnect.this.modul.getMain().syncCompleteTasks.remove(p);
			                  Bukkit.getScheduler().cancelTask(taskID);
			                }  
		              }
	            }
	            //TODO: U.a. spammt das hier herum
	          },  5L, 100L);
	      modul.getMain().syncCompleteTasks.put(p, Integer.valueOf(task.getTaskId()));
	   
	    }    */
    }

    /**
     * Erstmal nur Bossbar fuer die Lobby, spaeter auch fuer alle Welten des jeweiligen Servers. Erfordert Server-Erkennung
     *
     * @param event
     */
    public void setBossBar(final PlayerJoinEvent event) {
        Player p = event.getPlayer();
        Bossbar b = new Bossbar();
        b.setBarSytyleSegmented_20();
        b.setBarColor_Blue();
        BossBar ServerAnzeige = b.erstelleBossBar(modul.getMain().getJupiterKomponente().getLobbyMenuConfig().getString("bossbar.text.text_lobby"), "&");
        ServerAnzeige.addPlayer(p);
        BukkitScheduler scheduler = modul.getMain().getServer().getScheduler();
        scheduler.runTaskLaterAsynchronously(modul.getMain(), () -> ServerAnzeige.removePlayer(p), 250L);
    }
}
