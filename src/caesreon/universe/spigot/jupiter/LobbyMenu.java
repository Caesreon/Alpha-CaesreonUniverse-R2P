package caesreon.universe.spigot.jupiter;

import caesreon.core.ColorCodeParser;
import caesreon.core.Menu;
import caesreon.core.MenuBuilder;
import caesreon.core.hilfsklassen.WeltUT.CheckWeltenName;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class LobbyMenu implements Listener {
    private final JupiterKomponente jam;
    private Inventory LobbyInv;
    private Inventory InfoGUI;
    private Inventory ProjektGUI;
    private String Regex;

    public LobbyMenu(JupiterKomponente jam) {
        this.jam = jam;
    }

    public void onJoin(Player p) {
        if (CheckWeltenName.playerListe(p, jam.erlaubteWeltenMenu)) {
            Regex = jam.getMenuConfig().getString("regex");
            PlayerInventory playerInventory = p.getInventory();
            MenuBuilder m = new MenuBuilder();
            ItemStack InformationenItem = m.erstelleGUIItem(jam.getMenuConfig().getString("hotbar.informationen.material"),
                    jam.getMenuConfig().getString("hotbar.informationen.titel"),
                    jam.getMenuConfig().getStringList("hotbar.informationen.lore"), Regex);
            ItemStack SurvivalItem = m.erstelleGUIItem(jam.getMenuConfig().getString("hotbar.survival.material"),
                    jam.getMenuConfig().getString("hotbar.survival.titel"),
                    jam.getMenuConfig().getStringList("hotbar.survival.lore"), Regex);
            ItemStack CreativeItem = m.erstelleGUIItem(jam.getMenuConfig().getString("hotbar.creative.material"),
                    jam.getMenuConfig().getString("hotbar.creative.titel"),
                    jam.getMenuConfig().getStringList("hotbar.creative.lore"), Regex);
            ItemStack ProjekteItem = m.erstelleGUIItem(jam.getMenuConfig().getString("hotbar.projekte.material"),
                    jam.getMenuConfig().getString("hotbar.projekte.titel"),
                    jam.getMenuConfig().getStringList("hotbar.projekte.lore"), Regex);
            Basis_Buch b = new Basis_Buch(jam);
            ItemStack TutorialItem = b.TutorialBuch();
            ItemMeta meta = TutorialItem.getItemMeta();
            Objects.requireNonNull(meta).setLore(ColorCodeParser.ParseList(jam.getMenuConfig().getStringList("hotbar.tutorial.lore"), Regex));
            meta.setDisplayName(ColorCodeParser.ParseString(Objects.requireNonNull(jam.getMenuConfig().getString("hotbar.tutorial.titel")), Regex));
            TutorialItem.setItemMeta(meta);
	        /*ItemStack TutorialItem = m.erstelleGUIItem(jam.getMenuConfig().getString("hotbar.tutorial.material"), 
	        		jam.getMenuConfig().getString("hotbar.tutorial.titel"), 
	        		jam.getMenuConfig().getStringList("hotbar.tutorial.lore"), Regex);
	        */
            playerInventory.clear();

            playerInventory.setItem(jam.getMenuConfig().getInt("hotbar.informationen.position"), InformationenItem);
            playerInventory.setItem(jam.getMenuConfig().getInt("hotbar.survival.position"), SurvivalItem);
            playerInventory.setItem(jam.getMenuConfig().getInt("hotbar.creative.position"), CreativeItem);
            playerInventory.setItem(jam.getMenuConfig().getInt("hotbar.projekte.position"), ProjekteItem);
            playerInventory.setItem(jam.getMenuConfig().getInt("hotbar.tutorial.position"), TutorialItem);
            LobbyInv = playerInventory;

        }
    }

    public void Information_GUI(Player p) {
        MenuBuilder m = new MenuBuilder();
        MenuBuilder.MenuLayoutContainer mlc = new MenuBuilder.MenuLayoutContainer();

        Regex = jam.getMenuConfig().getString("regex");
        ItemStack RegelnItem = m.erstelleGUIItem(jam.getMenuConfig().getString("gui.informationen.subitems.regeln.material"),
                jam.getMenuConfig().getString("gui.informationen.subitems.regeln.titel"),
                jam.getMenuConfig().getStringList("gui.informationen.subitems.regeln.lore"), Regex);
        ItemStack WikiItem = m.erstelleGUIItem(jam.getMenuConfig().getString("gui.informationen.subitems.wiki.material"),
                jam.getMenuConfig().getString("gui.informationen.subitems.wiki.titel"),
                jam.getMenuConfig().getStringList("gui.informationen.subitems.wiki.lore"), Regex);
        ItemStack DiscordItem = m.erstelleGUIItem(jam.getMenuConfig().getString("gui.informationen.subitems.discord.material"),
                jam.getMenuConfig().getString("gui.informationen.subitems.discord.titel"),
                jam.getMenuConfig().getStringList("gui.informationen.subitems.discord.lore"), Regex);
        ItemStack LivemapItem = m.erstelleGUIItem(jam.getMenuConfig().getString("gui.informationen.subitems.livemap.material"),
                jam.getMenuConfig().getString("gui.informationen.subitems.livemap.titel"),
                jam.getMenuConfig().getStringList("gui.informationen.subitems.livemap.lore"), Regex);
        ItemStack VoteseitenItem = m.erstelleGUIItem(jam.getMenuConfig().getString("gui.informationen.subitems.voteseiten.material"),
                jam.getMenuConfig().getString("gui.informationen.subitems.voteseiten.titel"),
                jam.getMenuConfig().getStringList("gui.informationen.subitems.voteseiten.lore"), Regex);
        ItemStack SocialMediaItem = m.erstelleGUIItem(jam.getMenuConfig().getString("gui.informationen.subitems.socialmedia.material"),
                jam.getMenuConfig().getString("gui.informationen.subitems.socialmedia.titel"),
                jam.getMenuConfig().getStringList("gui.informationen.subitems.socialmedia.lore"), Regex);
        ItemStack ChangelogItem = m.erstelleGUIItem(jam.getMenuConfig().getString("gui.informationen.subitems.changelog.material"),
                jam.getMenuConfig().getString("gui.informationen.subitems.changelog.titel"),
                jam.getMenuConfig().getStringList("gui.informationen.subitems.changelog.lore"), Regex);

        Menu infoMenu = m.erstelleBasisMenue(null, ColorCodeParser.Parse(jam.getMenuConfig().getString("gui.informationen.titel"), Regex), 36);

        for (int i = 0; i <= 35; i++)

            m.itemHinzufuegen(infoMenu, new ItemStack(Objects.requireNonNull(Material.matchMaterial(Objects.requireNonNull(jam.getMenuConfig().getString("gui.informationen.hintergrund")))), 1), i);
        m.itemHinzufuegen(infoMenu, RegelnItem, jam.getMenuConfig().getInt("gui.informationen.subitems.regeln.position"));
        m.itemHinzufuegen(infoMenu, WikiItem, jam.getMenuConfig().getInt("gui.informationen.subitems.wiki.position"));
        m.itemHinzufuegen(infoMenu, DiscordItem, jam.getMenuConfig().getInt("gui.informationen.subitems.discord.position"));
        m.itemHinzufuegen(infoMenu, LivemapItem, jam.getMenuConfig().getInt("gui.informationen.subitems.livemap.position"));
        m.itemHinzufuegen(infoMenu, VoteseitenItem, jam.getMenuConfig().getInt("gui.informationen.subitems.voteseiten.position"));
        m.itemHinzufuegen(infoMenu, SocialMediaItem, jam.getMenuConfig().getInt("gui.informationen.subitems.socialmedia.position"));
        m.itemHinzufuegen(infoMenu, ChangelogItem, jam.getMenuConfig().getInt("gui.informationen.subitems.changelog.position"));
        m.oeffneMenu(p, infoMenu);
        InfoGUI = infoMenu.inventar;
    }

    public void Projekte_GUI(Player p) {
        MenuBuilder m = new MenuBuilder();
        Regex = jam.getMenuConfig().getString("regex");
        ItemStack KathedraleItem = m.erstelleGUIItem(jam.getMenuConfig().getString("gui.projekte.subitems.kathedrale.material"),
                jam.getMenuConfig().getString("gui.projekte.subitems.kathedrale.titel"),
                jam.getMenuConfig().getStringList("gui.projekte.subitems.kathedrale.lore"), Regex);
        ItemStack GalleoneItem = m.erstelleGUIItem(jam.getMenuConfig().getString("gui.projekte.subitems.galleone.material"),
                jam.getMenuConfig().getString("gui.projekte.subitems.galleone.titel"),
                jam.getMenuConfig().getStringList("gui.projekte.subitems.galleone.lore"), Regex);
        ItemStack BasilicaItem = m.erstelleGUIItem(jam.getMenuConfig().getString("gui.projekte.subitems.basilica.material"),
                jam.getMenuConfig().getString("gui.projekte.subitems.basilica.titel"),
                jam.getMenuConfig().getStringList("gui.projekte.subitems.basilica.lore"), Regex);
        ItemStack JapanItem = m.erstelleGUIItem(jam.getMenuConfig().getString("gui.projekte.subitems.japan.material"),
                jam.getMenuConfig().getString("gui.projekte.subitems.japan.titel"),
                jam.getMenuConfig().getStringList("gui.projekte.subitems.japan.lore"), Regex);
        ItemStack SolterraItem = m.erstelleGUIItem(jam.getMenuConfig().getString("gui.projekte.subitems.solterra_spawn.material"),
                jam.getMenuConfig().getString("gui.projekte.subitems.solterra_spawn.titel"),
                jam.getMenuConfig().getStringList("gui.projekte.subitems.solterra_spawn.lore"), Regex);

        Menu projekteMenu = m.erstelleBasisMenue(null, ColorCodeParser.Parse(jam.getMenuConfig().getString("gui.projekte.titel"), Regex), 27);
        for (int i = 0; i <= 26; i++)
            m.itemHinzufuegen(projekteMenu, new ItemStack(Objects.requireNonNull(Material.matchMaterial(Objects.requireNonNull(jam.getMenuConfig().getString("gui.projekte.hintergrund")))), 1), i);
        m.itemHinzufuegen(projekteMenu, KathedraleItem, jam.getMenuConfig().getInt("gui.projekte.subitems.kathedrale.position"));
        m.itemHinzufuegen(projekteMenu, BasilicaItem, jam.getMenuConfig().getInt("gui.projekte.subitems.basilica.position"));
        m.itemHinzufuegen(projekteMenu, GalleoneItem, jam.getMenuConfig().getInt("gui.projekte.subitems.galleone.position"));
        m.itemHinzufuegen(projekteMenu, JapanItem, jam.getMenuConfig().getInt("gui.projekte.subitems.japan.position"));
        m.itemHinzufuegen(projekteMenu, SolterraItem, jam.getMenuConfig().getInt("gui.projekte.subitems.solterra_spawn.position"));
        m.oeffneMenu(p, projekteMenu);
        //TODO: Auf Funktion Prüfen
        ProjektGUI = projekteMenu.inventar;
    }

    public Inventory getInventarLobby() {
        return LobbyInv;
    }

    public Inventory getInventarInfoGUI() {
        return InfoGUI;
    }

    public Inventory getInventarProjektGUI() {
        return ProjektGUI;
    }
}
