package caesreon.universe.spigot.jupiter;

import caesreon.core.hilfsklassen.SpielerUT;
import caesreon.core.hilfsklassen.StringUT;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

public class Basis_Buch implements CommandExecutor {
    private final JupiterKomponente jam;
    private ItemStack Buch;
    private BookMeta BuchMeta;
    public Basis_Buch(JupiterKomponente jam) {
        this.jam = jam;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, String[] args) {
        Player p = (Player) sender;
        ErstelleBuch_Tutorial(p);
        return true;
    }

    public void ErstelleBuch_Tutorial(Player p) {

        Buch = new ItemStack(Material.WRITTEN_BOOK);
        //List<BaseComponent[]> Buchseiten  = (BuchMeta.spigot().getPages());
        BuchMeta = BuchMeta_Tutorial(Buch);
        BuchMeta.setTitle("Tutorial");
        BuchMeta.setAuthor("Caesreon-Server");
        //Update
        Buch.setItemMeta(BuchMeta);
        SpielerUT.Item_AnSpielerGeben(p, Buch);
    }

    public ItemStack TutorialBuch() {
        Buch = new ItemStack(Material.WRITTEN_BOOK);
        BuchMeta = BuchMeta_Tutorial(Buch);
        BuchMeta.setTitle("Tutorial");
        BuchMeta.setAuthor("Caesreon-Server");
        //Update
        Buch.setItemMeta(BuchMeta);
        return Buch;
    }

    private BookMeta BuchMeta_Tutorial(ItemStack Buch) {
        BookMeta Temp = (BookMeta) Buch.getItemMeta();
        Temp.spigot().addPage(Buchseite(
                jam.getInfoBuchConfig().getString("seite1.zeile1"),
                jam.getInfoBuchConfig().getString("seite1.zeile2"),
                jam.getInfoBuchConfig().getString("seite1.zeile3"),
                jam.getInfoBuchConfig().getString("seite1.zeile4"),
                jam.getInfoBuchConfig().getString("seite1.zeile5"),
                jam.getInfoBuchConfig().getString("seite1.zeile6"),
                jam.getInfoBuchConfig().getString("seite1.zeile7"),
                jam.getInfoBuchConfig().getString("seite1.zeile8"),
                jam.getInfoBuchConfig().getString("seite1.zeile9"),
                jam.getInfoBuchConfig().getString("seite1.zeile10"),
                jam.getInfoBuchConfig().getString("seite1.zeile11"),
                jam.getInfoBuchConfig().getString("seite1.zeile12"),
                jam.getInfoBuchConfig().getString("seite1.zeile13"),
                jam.getInfoBuchConfig().getString("seite1.zeile14")
        ));
        Temp.spigot().addPage(Buchseite(
                jam.getInfoBuchConfig().getString("seite2.zeile1"),
                jam.getInfoBuchConfig().getString("seite2.zeile2"),
                jam.getInfoBuchConfig().getString("seite2.zeile3"),
                jam.getInfoBuchConfig().getString("seite2.zeile4"),
                jam.getInfoBuchConfig().getString("seite2.zeile5"),
                jam.getInfoBuchConfig().getString("seite2.zeile6"),
                jam.getInfoBuchConfig().getString("seite2.zeile7"),
                jam.getInfoBuchConfig().getString("seite2.zeile8"),
                jam.getInfoBuchConfig().getString("seite2.zeile9"),
                jam.getInfoBuchConfig().getString("seite2.zeile10"),
                jam.getInfoBuchConfig().getString("seite2.zeile11"),
                jam.getInfoBuchConfig().getString("seite2.zeile12"),
                jam.getInfoBuchConfig().getString("seite2.zeile13"),
                jam.getInfoBuchConfig().getString("seite2.zeile14")
        ));
        Temp.spigot().addPage(Buchseite(
                jam.getInfoBuchConfig().getString("seite3.zeile1"),
                jam.getInfoBuchConfig().getString("seite3.zeile2"),
                jam.getInfoBuchConfig().getString("seite3.zeile3"),
                jam.getInfoBuchConfig().getString("seite3.zeile4"),
                jam.getInfoBuchConfig().getString("seite3.zeile5"),
                jam.getInfoBuchConfig().getString("seite3.zeile6"),
                jam.getInfoBuchConfig().getString("seite3.zeile7"),
                jam.getInfoBuchConfig().getString("seite3.zeile8"),
                jam.getInfoBuchConfig().getString("seite3.zeile9"),
                jam.getInfoBuchConfig().getString("seite3.zeile10"),
                jam.getInfoBuchConfig().getString("seite3.zeile11"),
                jam.getInfoBuchConfig().getString("seite3.zeile12"),
                jam.getInfoBuchConfig().getString("seite3.zeile13"),
                jam.getInfoBuchConfig().getString("seite3.zeile14")
        ));
        Temp.spigot().addPage(Buchseite(
                jam.getInfoBuchConfig().getString("seite4.zeile1"),
                jam.getInfoBuchConfig().getString("seite4.zeile2"),
                jam.getInfoBuchConfig().getString("seite4.zeile3"),
                jam.getInfoBuchConfig().getString("seite4.zeile4"),
                jam.getInfoBuchConfig().getString("seite4.zeile5"),
                jam.getInfoBuchConfig().getString("seite4.zeile6"),
                jam.getInfoBuchConfig().getString("seite4.zeile7"),
                jam.getInfoBuchConfig().getString("seite4.zeile8"),
                jam.getInfoBuchConfig().getString("seite4.zeile9"),
                jam.getInfoBuchConfig().getString("seite4.zeile10"),
                jam.getInfoBuchConfig().getString("seite4.zeile11"),
                jam.getInfoBuchConfig().getString("seite4.zeile12"),
                jam.getInfoBuchConfig().getString("seite4.zeile13"),
                jam.getInfoBuchConfig().getString("seite4.zeile14")
        ));
        Temp.spigot().addPage(Buchseite(
                jam.getInfoBuchConfig().getString("seite5.zeile1"),
                jam.getInfoBuchConfig().getString("seite5.zeile2"),
                jam.getInfoBuchConfig().getString("seite5.zeile3"),
                jam.getInfoBuchConfig().getString("seite5.zeile4"),
                jam.getInfoBuchConfig().getString("seite5.zeile5"),
                jam.getInfoBuchConfig().getString("seite5.zeile6"),
                jam.getInfoBuchConfig().getString("seite5.zeile7"),
                jam.getInfoBuchConfig().getString("seite5.zeile8"),
                jam.getInfoBuchConfig().getString("seite5.zeile9"),
                jam.getInfoBuchConfig().getString("seite5.zeile10"),
                jam.getInfoBuchConfig().getString("seite5.zeile11"),
                jam.getInfoBuchConfig().getString("seite5.zeile12"),
                jam.getInfoBuchConfig().getString("seite5.zeile13"),
                jam.getInfoBuchConfig().getString("seite5.zeile14"),
                jam.getInfoBuchConfig().getString("seite5.url1"),
                jam.getInfoBuchConfig().getString("seite5.url2"),
                jam.getInfoBuchConfig().getString("seite5.url3")
        ));
        return Temp;
    }


    private BaseComponent[] Buchseite(String Z1, String Z2, String Z3, String Z4, String Z5, String Z6, String Z7, String Z8, String Z9, String Z10, String Z11, String Z12, String Z13, String Z14) {
        BaseComponent[] Seite = new ComponentBuilder(StringUT.ParseColorCodes(Z1, jam.Regex))
                .append(StringUT.ParseColorCodes(Z2, jam.Regex))
                .append(StringUT.ParseColorCodes(Z3, jam.Regex))
                .append(StringUT.ParseColorCodes(Z4, jam.Regex))
                .append(StringUT.ParseColorCodes(Z5, jam.Regex))
                .append(StringUT.ParseColorCodes(Z6, jam.Regex))
                .append(StringUT.ParseColorCodes(Z7, jam.Regex))
                .append(StringUT.ParseColorCodes(Z8, jam.Regex))
                .append(StringUT.ParseColorCodes(Z9, jam.Regex))
                .append(StringUT.ParseColorCodes(Z10, jam.Regex))
                .append(StringUT.ParseColorCodes(Z11, jam.Regex))
                .append(StringUT.ParseColorCodes(Z12, jam.Regex))
                .append(StringUT.ParseColorCodes(Z13, jam.Regex))
                .append(StringUT.ParseColorCodes(Z14, jam.Regex))
                .create();
        return Seite;
    }

    private BaseComponent[] Buchseite(String Z1, String Z2, String Z3, String Z4, String Z5, String Z6, String Z7, String Z8, String Z9, String Z10, String Z11, String Z12, String Z13, String Z14, String URL1, String URL2, String URL3) {


        @SuppressWarnings("deprecation")
        BaseComponent[] Seite = new ComponentBuilder(StringUT.ParseColorCodes(Z1, jam.Regex))
                .append(StringUT.ParseColorCodes(Z2, jam.Regex))
                .append(StringUT.ParseColorCodes(Z3, jam.Regex))
                .append(StringUT.ParseColorCodes(Z4, jam.Regex))
                .append(StringUT.ParseColorCodes(Z5, jam.Regex))
                .append(StringUT.ParseColorCodes(Z6, jam.Regex))
                .append(StringUT.ParseColorCodes(Z7, jam.Regex))
                .append(StringUT.ParseColorCodes(Z8, jam.Regex))
                .append(StringUT.ParseColorCodes(Z9, jam.Regex))
                .append(StringUT.ParseColorCodes(Z10, jam.Regex))
                .append(StringUT.ParseColorCodes(Z11, jam.Regex))
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, URL1))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Besuche unseren Discord").create()))
                .append(StringUT.ParseColorCodes(Z12, jam.Regex))
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, URL2))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Besuche unsere Wiki fweitere Hilfe!").create()))
                .append(StringUT.ParseColorCodes(Z13, jam.Regex))
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, URL3))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Besuche unser Forum").create()))
                .append(StringUT.ParseColorCodes(Z14, jam.Regex))
                .create();
        return Seite;
    }

}