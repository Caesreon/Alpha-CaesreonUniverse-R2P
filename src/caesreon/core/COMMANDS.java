package caesreon.core;

public enum COMMANDS {
    ;

    public enum BASIS {
        lobby, creative, survival, citybuild, farmwelt, dungeon, kathedrale, tutorial, hub, galleone, basilica, japanhaus, solterraspawn;

        public enum GILDE {
            gilde, gruenden, beitreten, info;

            public enum SPIELER {
                verlassen
            }

            public enum LEITER {
                loeschen, bearbeiten
            }
        }

        public enum MAIL {
            mail
        }

        public enum LOTTERIE {
            lotterie;

            public enum WOCHENLOTTERIE {
                woche, kaufen, ziehung
            }

            public enum TAGESLOTTERIE {
                tag, kaufen, ziehung
            }
        }

        public enum UserWerbenUser {
            uwu;

            public enum CODE {
                code, einlösen, erstellen
                //public final String label;
                //private CODE(String label)
                //{
                //    this.label = label;
                //}
            }
        }
    }

    public enum ERWEITERT {haustiere, outfits, cosmetics, kopfbedeckungen, partikel, gadgets}

    public enum HILFE {
        hilfe;

        enum DSGVO {loeschung, einsicht}
    }

    public enum FLY {tempfly, fly, flyspeed}

    public enum ECONOMY {
        geld, senden, top;

        public enum ADMIN {geben, abziehen, setzen, speichern, einsehen}
    }
}

