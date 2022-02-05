package caesreon.core.minecraft;

import caesreon.core.Log;
import caesreon.core.minecraft.ChunkAdapter.KoordinatenAdjustierung.Ecke;
import org.bukkit.entity.Player;

/**
 * @author Coriolanus_S
 * @implNote ChunkAdapter(org.bukkit.Chunk chunk) empfohlen, da hier alle Koordinationen und Lokationen ermittelt werden, alle anderen Konstruktoren ermöglichen
 * erstmal nur die Koordnaten, nicht die Lokationen.
 */
public class ChunkAdapter {

    private final Koordinate spielerKoordinate;
    private final BoundingBox eckpunkteChunk;
    private Koordinate errecheterEckpunktKoordinate_A;
    private Koordinate errecheterEckpunktKoordinate_B;
    private Koordinate errecheterEckpunktKoordinate_C;
    private Koordinate errecheterEckpunktKoordinate_D;


    /**
     * Berechnet Chunkkoordinaten anhand des Spielers
     *
     * @param spieler
     */
    public ChunkAdapter(Player spieler) {
        spielerKoordinate = new Koordinate(spieler.getLocation().getBlockX(), spieler.getLocation().getBlockZ());
        Log.SpigotLogger.Debug("Spieler_pos: x=" + spielerKoordinate.getX() + " y=" + spielerKoordinate.getZ());
        berechneEckpunktKoordinaten();
        eckpunkteChunk = new BoundingBox(spieler, errecheterEckpunktKoordinate_A, errecheterEckpunktKoordinate_B, errecheterEckpunktKoordinate_C, errecheterEckpunktKoordinate_D);
    }

    private void berechneEckpunktKoordinaten() {
        Koordinate chunkKoordinate = new Koordinate(KoordinatenAdjustierung.ermittelGanzzahligeKoordinate(spielerKoordinate.getX()), KoordinatenAdjustierung.ermittelGanzzahligeKoordinate(spielerKoordinate.getZ()));

        Log.SpigotLogger.Debug("Chunk: x=" + (chunkKoordinate.getX() - 1) + " y=" + (chunkKoordinate.getZ() - 1));


        errecheterEckpunktKoordinate_A = Ecke.a(chunkKoordinate);
        errecheterEckpunktKoordinate_B = Ecke.b(errecheterEckpunktKoordinate_A);
        errecheterEckpunktKoordinate_C = Ecke.c(errecheterEckpunktKoordinate_A);
        errecheterEckpunktKoordinate_D = Ecke.d(errecheterEckpunktKoordinate_A);

        Log.SpigotLogger.Debug("EP1: x=" + errecheterEckpunktKoordinate_A.getX() + " y=" + errecheterEckpunktKoordinate_A.getZ());
        Log.SpigotLogger.Debug("EP2: x=" + errecheterEckpunktKoordinate_B.getX() + " y=" + errecheterEckpunktKoordinate_B.getZ());
        Log.SpigotLogger.Debug("EP3: x=" + errecheterEckpunktKoordinate_C.getX() + " y=" + errecheterEckpunktKoordinate_C.getZ());
        Log.SpigotLogger.Debug("EP4: x=" + errecheterEckpunktKoordinate_D.getX() + " y=" + errecheterEckpunktKoordinate_D.getZ());
    }

    /**
     * Diese Klasse enthält alle Eckpunkte einer bestimmten Region, bspw. eines Chunks und speichert diese in Locations<br>
     * <br> a - Punkt Linksoben
     * <br> b - Punkt Linksunten
     * <br> c - Punkt Rechtsoben
     * <br> d - Punkt Rechtsunten
     *
     * @author Coriolanus_S
     * @implNote Achtung! Worldguard zaehlt anders wie Mojang, deswegen muss fuer eine Protected Region Punkt b und C geladen werden!
     */
    public BoundingBox getEckpunkteChunk() {
        return eckpunkteChunk;
    }

    /**
     * Klasse zum Adjustieren von Rundungsfehlern bei der Koordinatenbestimmung von Chunks
     *
     * @author Coriolanus_S
     * @implNote Weirder Bug: Wenn ich in Minecraft eine negative Koordinate habe, weicht sie um +1 mit der berechneten Blockkoordinate von der reelen
     * Spieler Koordinate ab während dies bei den positiven Koordinatenwerten nicht der Fall ist. Das ist auch der Grund warum ich hier die Zahl
     * nochmal absolut aufrunde!
     */
    static class KoordinatenAdjustierung {
        /**
         * @param koordinatenWert
         * @param zahl
         * @return
         */
        static int additiv(int koordinatenWert, int zahl) {
            return koordinatenWert + zahl;
        }

        static int multiplikativ(int koordinatenWert) {
            if (koordinatenWert > 0) {
                Log.SpigotLogger.Debug("Wert war groesser 0 -> - 1");
                return (int) (Math.floor(koordinatenWert - 1) * 16);
            }
            if (koordinatenWert < 0) {
                Log.SpigotLogger.Debug("Wert war kleiner 0 -> +1");
                return (int) (Math.floor((koordinatenWert + 1) * 16) - 16);
            }
            return 0;
        }

        static int ermittelGanzzahligeKoordinate(int wert) {
            if (wert < 0) {
                Log.SpigotLogger.Debug("Wert war kleiner 0 -> -1");
                return (int) Math.floor(wert / 16) - 1;
            }
            if (wert >= 0) {
                Log.SpigotLogger.Debug("Wert war groesser 0 -> +1");
                return (int) Math.floor(wert / 16) + 1;
            }
            return 0;
        }

        static class Ecke {
            public static Koordinate a(Koordinate k) {
                return new Koordinate(KoordinatenAdjustierung.multiplikativ(k.getX()),
                        KoordinatenAdjustierung.multiplikativ(k.getZ()));
            }

            static Koordinate b(Koordinate k) {
                return new Koordinate(KoordinatenAdjustierung.additiv(k.getX(), 0),
                        KoordinatenAdjustierung.additiv(k.getZ(), 15));
            }

            static Koordinate c(Koordinate k) {
                return new Koordinate(KoordinatenAdjustierung.additiv(k.getX(), 15),
                        KoordinatenAdjustierung.additiv(k.getZ(), 0));
            }

            static Koordinate d(Koordinate k) {
                return new Koordinate(KoordinatenAdjustierung.additiv(k.getX(), 15),
                        KoordinatenAdjustierung.additiv(k.getZ(), 15));
            }
        }

    }

}