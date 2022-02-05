package caesreon.universe.bungee.befehle;

import caesreon.core.system.IDENTIFIER;
import caesreon.core.hilfsklassen.ChatBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Event extends Command {

    public Event() {
        super("Event");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if ((commandSender instanceof ProxiedPlayer)) {
            ProxiedPlayer p = (ProxiedPlayer) commandSender;
            ChatBungee.nachrichtAnSpielerSenden(p, IDENTIFIER.DefaultPrefix, "Teleportiere dich zum Event-Server.. ");
            p.connect(ProxyServer.getInstance().getServerInfo("event"));
        }
    }
}
