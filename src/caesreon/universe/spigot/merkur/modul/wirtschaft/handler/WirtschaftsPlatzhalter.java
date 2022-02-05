package caesreon.universe.spigot.merkur.modul.wirtschaft.handler;

import caesreon.universe.spigot.merkur.MerkurKomponente;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;


public class WirtschaftsPlatzhalter extends PlaceholderExpansion {

    private MerkurKomponente modul;

    public WirtschaftsPlatzhalter(MerkurKomponente modul) {
        this.modul = modul;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "Coriolanus_S";
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return "cae";
    }

    /**
     * This is the version of this expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion() {
        return "1.0.0";
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param player     A {@link org.bukkit.OfflinePlayer OfflinePlayer}.
     * @param identifier A String containing the identifier/value.
     * @return Possibly-null String of the requested identifier.
     */
    @Override
    public String onRequest(OfflinePlayer player, String identifier) {

        //main.Debug("PlaceholderAPI on Request");
        // %example_placeholder1%
        if (identifier.equals("kontostand")) {
            //TODO: Er spammt auch global herum. Vll eine globale Abfrage alle 3 Sekunden die dann verteilt wird?
            //main.Debug("PlaceholderAPI: kontostand");
            return String.format("%.0f", modul.getServerWirtschaft().getCachedKontostand((player.getUniqueId())));
        }

        // We return null if an invalid placeholder (f.e. %example_placeholder3%)
        // was provided
        return null;
    }
}
