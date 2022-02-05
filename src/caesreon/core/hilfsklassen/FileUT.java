package caesreon.core.hilfsklassen;

import caesreon.core.Log;
import caesreon.core.handlers.AgentConfigHandler;
import caesreon.core.handlers.KonfigurationsDatenSatz;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Statische Hilfsklasse fuer das allgemeine Datei-Handling im Unix File-System
 *
 * @author Coriolanus_S
 */
public class FileUT {
    public static void CreateNewFile(File Input) {
        try {
            Input.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<KonfigurationsDatenSatz> getAlleKonfigurationsDateienVonOrdner(final String ZielOrdner) {
        final File ordner = new File(ZielOrdner);
        List<KonfigurationsDatenSatz> temp = new ArrayList<>();

        try {
            for (final File datei : ordner.listFiles()) {
                if (datei.isFile()) {
                    Log.SpigotLogger.Debug(datei.getAbsolutePath());
                    KonfigurationsDatenSatz KDS = new KonfigurationsDatenSatz(null, ZielOrdner, datei.getName());
                    KDS.setYamlConfiguration(AgentConfigHandler.getYamlConfigurationDurchKonfigurationsDatenSatz(KDS));
                    temp.add(KDS);
                }
            }
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }

        return temp;
    }
}
