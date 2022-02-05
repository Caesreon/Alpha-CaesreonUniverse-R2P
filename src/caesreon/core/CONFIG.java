package caesreon.core;

import caesreon.core.minecraft.MATERIAL;

import java.util.HashMap;
import java.util.Map;

public enum CONFIG {
    ;

    public enum DefaultConfig {
        prefix, systemprefix, debug, befehle, fliegen, creative, survival, hub, lobby, kathedrale, dungeon, tutorial, citybuild, haustiere, gadgets,
        kopfbedeckungen, outfits, partikel, cosmetics, back;
    }

    public enum EconomyConfig {
        ;

        public enum Waehrung {Caesh}
    }

    public enum MYSQL {
        mysql, host, database, port, username, passwort
    }

    public enum SERVER_UUID {
        uuid("ffffffff-ffff-ffff-ffff-000000000000");
        private static Map<String, MATERIAL> IdZuEnumMap = new HashMap<>();

        static {
            for (MATERIAL id : MATERIAL.values()) {
                IdZuEnumMap.put(id.getMinecraftItemID(), id);
            }
        }

        private String UUID = "";

        SERVER_UUID(String UUID) {
            this.UUID = UUID;
        }

        public static MATERIAL getMaterialfromString(String NumerischeID) {
            return IdZuEnumMap.get(NumerischeID);
        }

        public String getMinecraftItemID() {
            return UUID;
        }
    }

    ;


}
