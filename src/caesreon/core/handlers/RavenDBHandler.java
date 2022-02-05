package caesreon.core.handlers;

import caesreon.core.system.DATENBANKEN;
import caesreon.core.system.netzwerk.NetzwerkAdressen;
import gamersocke.data.PlayerInfo;
import net.ravendb.client.documents.DocumentStore;
import net.ravendb.client.documents.conventions.DocumentConventions;
import net.ravendb.client.documents.session.IDocumentSession;
import net.ravendb.client.exceptions.RavenException;
import net.ravendb.client.http.AggressiveCacheMode;

import java.time.Duration;
import java.util.HashMap;

//Quellcode by Gamersocke
public final class RavenDBHandler {
    private static DocumentStore _store = null;

    private static HashMap<String, Class<?>> _mapClasses = null;

    public static void Init() {
        _mapClasses = new HashMap<>();
        _mapClasses.put(PlayerInfo.class.getName(), PlayerInfo.class);
        _store = new DocumentStore("http://" + NetzwerkAdressen.SRV_DATENBANK + NetzwerkAdressen.PRT_RAVEN, DATENBANKEN.Raven);
        DocumentConventions conventions = _store.getConventions();
        conventions.aggressiveCache().setDuration(Duration.ofMinutes(5L));
        conventions.aggressiveCache().setMode(AggressiveCacheMode.TRACK_CHANGES);
        conventions.setFindJavaClassByName(name -> {
            try {
                return _mapClasses.get(name);
            } catch (Exception e) {
                throw new RavenException("Unable to find class by name = " + name, e);
            }
        });
        _store.initialize();
    }

    public static void Dispose() {
        if (_store != null) {
            _store.close();
            _store = null;
        }
    }

    public static IDocumentSession GetSession() {
        return _store.openSession();
    }
}
