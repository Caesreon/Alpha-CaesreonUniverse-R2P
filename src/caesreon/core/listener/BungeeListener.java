package caesreon.core.listener;

import caesreon.core.Log;
import caesreon.core.Spieler;
import caesreon.core.system.IDENTIFIER;
import caesreon.main.SpigotMain;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

/**
 * Kleine Erkentniss am Rande:
 * Minecradt required bei den PluginMessageChanneln das Format: "namespace:channel"!
 * Namespace kann dabei ein willkürlicher, uniquer String sein, der aber natürlich im Plugin Global genutzt werden soll
 */

public class BungeeListener implements PluginMessageListener {

    private final SpigotMain spigotPlugin = SpigotMain.getSpigotMainInstance();

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        Log.SpigotLogger.Dev(channel);
        if (!channel.equals(IDENTIFIER.Namespace.caesreon + ":" + IDENTIFIER.PluginChannel.cae_sys)) {
            return;
        }
        Log.BungeeLogger.Dev("Einkommende Nachricht");
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("Connect")) {
            // Use the code sample in the 'Response' sections below to read
            // the data.
        } else if (subchannel.equals("IPOther")) {
            player.sendMessage(in.readUTF());
            player.sendMessage(in.readUTF());
            int port = in.readInt();
        }
    }

    /**
     * Sendet Server-Verbindungsbefehl eines Spielers an den Proxy-Server bzw. die Caesreon-Proxy Instanz
     * @param player Der Spieler, welcher einen Befehl ausführt
     * @param server Der Server zu welchem gewechselt werden soll
     */
    public void serverConnect(Player player, String server) {
        Log.SpigotLogger.Dev("Sende Connect-Anfrage an Proxy für Server " + server);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(spigotPlugin, IDENTIFIER.Namespace.caesreon + ":" + IDENTIFIER.PluginChannel.cae_sys, out.toByteArray());
    }

    public void erhalteIP(Player player, Spieler spieler) {
        Log.SpigotLogger.Dev("Sende IP-Anfrage an Proxy für Server " + spieler.getSpielername());
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("IPOther");
        player.sendPluginMessage(spigotPlugin, IDENTIFIER.Namespace.caesreon + ":" + IDENTIFIER.PluginChannel.cae_sys, out.toByteArray());
    }


}
