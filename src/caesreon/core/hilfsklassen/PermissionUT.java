package caesreon.core.hilfsklassen;

import caesreon.core.system.BasisBerechtigungsGruppen;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.user.track.UserPromoteEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class PermissionUT {
    ///Checking for group membership can be most easily achieved using hasPermission checks.
    //private static LuckPerms LP;
    public static boolean isPlayerInGroup(Player player, String group) {
        return player.hasPermission("group." + group);
    }

    public static boolean istZweitAccount(Player p)
    {
        if (p.hasPermission(BasisBerechtigungsGruppen.Zweitaccounts)) return true;
        return false;
    }

    ///We can use the method above with a list of "possible" groups in order to find a player's group.
    public static String getPlayerGroup(Player player, Collection<String> possibleGroups) {
        for (String group : possibleGroups) {
            if (player.hasPermission("group." + group)) {
                return group;
            }
        }
        return null;
    }

    public static UUID uuid(Player p) {
        return p.getUniqueId();
    }

    public static User user(LuckPerms LP, UUID ID) {
        return LP.getUserManager().getUser(ID);
    }

    public static Set<String> GetAlleGruppen(User u) {
        return u.getNodes(NodeType.INHERITANCE).stream().map(InheritanceNode::getGroupName)
                .collect(Collectors.toSet());
    }

    /**
     * Methode, welche einem Spieler für eine bestimmte Zeit eine berechtigungsgruppe setzt
     * @param LP Das Luckperms Modul
     * @param p Der Spieler welcher eine Berechtigung erhalten soll
     * @param Gruppenname Der Name der Berechtigungsgruppe
     * @param Zeit Die Dauer der Berechtigung in Stunden
     */
    public static void setzeSpielerTemporaerGruppe(LuckPerms LP, Player p, String Gruppenname, long Zeit) {
        System.out.println(p.getName() + " " + Gruppenname + " " + Zeit);
        Group G = LP.getGroupManager().getGroup(Gruppenname);
        InheritanceNode N = InheritanceNode.builder(Gruppenname).value(true).expiry(Zeit, TimeUnit.HOURS).build();
        User user = LP.getUserManager().getUser(uuid(p));
        user.data().add(N);
        LP.getUserManager().saveUser(user);
        LP.getGroupManager().saveGroup(G);
    }

    @SuppressWarnings("unused")
    private void onUserPromote(UserPromoteEvent event) {
        // ...
    }

}
