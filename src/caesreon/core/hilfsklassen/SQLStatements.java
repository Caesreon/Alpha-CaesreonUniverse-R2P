package caesreon.core.hilfsklassen;

@Deprecated
public class SQLStatements {
    //MODUL C


    //MODUL G
    public static String CreateDatabaseGilden = "CREATE DATABASE IF NOT EXISTS Caesreon_Gilden";

    //MODUL A
    public static String CreateDatabaseAuktionshaus = "CREATE DATABASE IF NOT EXISTS Caesreon_Auktionshaus";
    public static String CreateTableAuktion = "CREATE TABLE IF NOT EXISTS Auktion( AuktionNR MEDIUMINT NOT NULL AUTO_INCREMENT UNIQUE, ObjektID INT, Auktionierender VARCHAR(50),  UUID VARCHAR(40), Start DATE, Ende , Aktiv BOOL)";
    public static String CreateTableLog = "CREATE TABLE IF NOT EXISTS AuktionsLog( ID MEDIUMINT NOT NULL AUTO_INCREMENT UNIQUE, AuktionNR, Bietender VARCHAR(50),  UUID VARCHAR(40),  Gebot INT)";
    public static String CreateTableObjekt = "CREATE TABLE IF NOT EXISTS AuktionsObjekt( ObjektID MEDIUMINT NOT NULL AUTO_INCREMENT UNIQUE, ObjektBeschreinung VARCHAR(100), ObjektName VARCHAR(50), MindestPreis INT, PRIMARY KEY ( ObjektID ))";
    //MODUL S
    public static String CreateDatabaseSystem = "CREATE DATABASE IF NOT EXISTS Caesreon_System";
}


/*  LOGIK Auktion
    autkionshaus auktion erstellen name
    Erstelle: AuktionsNr
    Abfrage: Name, Beschreibung mindestpreis -> SQL AuktionsObjekt + AuktionNr
    Abfrage: Start, Ende, name, uuid -> SQL Auktion + AuktionNr
    
    auktionshaus autkionen
    Select a.auktionsNr b.name, b.minpreis, a.auktionierender, a.start, a.ende, l.Max(Gebot) from auktionen a, objekt b, log l WHERE a.objektID == b.objektID AND a.Aktiv == true
    
    auktionshaus bieten auktionnr
    Abfrage: AuktionsNr, Gebot, name, uuid -> SQL Log (wenn hoeher als aktuelles gebot
    Insert Into AuktionLog(ID, AuktionNR, Bietender, UUID, Gebot) VALUES (NULL, Variable, Variable, Variable, Variable)
    
    auktionshaus auktionen eigene
    Select a.auktionsNr b.name, b.minpreis, a.auktionierender, a.start, a.ende, l.Max(Gebot) from auktionen a, objekt b, log l WHERE a.objektID == b.objektID AND a.Aktiv == true AND a.Auktionierender == VARIABLE-SPIELRNAME
    
    auktionshaus auktion loeschen
    
    
    AblaufDatum
    Update Auktion... Aktiv = false; //Deaktiviert die Logik
    Select l.MAX(Gebot), l.AuktionNr, a.AuktionNr FROM Auktion a, Log l WHERE a.AuktionNr == l.AuktionNR    
    Mit Vault diesen wert automatisch 
    
    
    LOGIK GILDE
    gilde erstellen name
    gilde verwalten (GUI) (beschreibung)
    gilde konto einzahlen betrag
    gilde konto auszahlen betrag
    gilde kontostand
    gilde beitreten name
    gilde verlassen name
    gilde loeschen
    
    LOGIK Conscribere
    
    caesreon code CODE
    
*/