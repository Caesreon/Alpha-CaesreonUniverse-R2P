# Caesreon-Server-Plugin

## Status: Alpha

Fehlende Module:

- Fähigkeitenbaum

## Allgemein

Das Caesreon Server Plugin ist die Hauptapplikation unseres Minecraft Servers und modular entwickelt worden. 
Je nach Runtime Modus kann dieses Plugin auf einem Spigot-Server als "Agent" und auf einem BungeeCord-Server als "Proxy" installiert werden.

Dabei muss dieses Plugin auf jedem einzelnen Server/Sub Server installiert sein,
damit die datenbankbasierten Module funktionieren.

Zudem kann jedes Modul bzw. Submodul in den Konfigurationsdateien konfiguriert, aktiviert bzw. deaktiviert werden.


Im RuntimeModus.SPIGOT ist es dabei unerheblich, wie viele Instanzen innerhalb 
eines physischen oder virtualisierten Servernetzwerkes genutzt werden.

Ein Beispiel: <br>
Im RuntimeModus.SPIGOT, welcher in der config.yml aktiviert wird mit: bungeecord: 'false', funktioniert dieses Plugin mit allen
Funktionen auf einem einzigen Server. Unterschiede fänden sich hier allerdings dann im Handling von
bestimmten Befehlen (bspw. Teleportationsbefehle), welche dann zusätzlich über die config.yml konfiguriert werden müssten<br>

Ist der Runtime Modus BUNGEE aktiviert (config.yml -> bungeecord: 'true'), muss mindestens ein Spigot Server
und ein Bungeecord Proxy-Server vorhanden sein und das Plugin auf beiden installiert sein, damit beide Server über dieses Plugin
miteinander kommunizieren können.

## Dokumentation: Core & Services

Die technische Dokumentation enthält alle wichtigen Informationen wie z.b Abhängigkeiten, Core-Befehle sowie alle
implementierten Module und die dazugehörigen Erklärungen.

### Core

Wichtig: Core-Befehle werden grundlegend immer ausgeführt. Diese sind nicht deaktivierbar, allerdings konfigurierbar
über die config.yml im Caesreon Ordner!

#### Abhängigkeiten

- [LuckPerms]
- [PlaceholderApi]
- [UltimateVotes]
- [BossPlugin]
- [LightAPI] -> Indirekte Abhängigkeit durch das Bossplugin von Entchenklein

#### Befehle:

- /lobby
- /hub
- /spawn
- /dungeon
- /kathedrale
- /survival
- /creative
- /citybuild
- /infobuch
- /fly
- /flyspeed [0.Wert]
- /tempfly
- /gadgets
- /haustiere
- /kopfbedeckungen
- /partikel
- /cosmetics

#### Administrator-Befehle

- /cae sys reload (Liest alle Konfigurationsdateien neu ein, aktuell nicht funktional)

#### Permissions:

- caesreon.commands.spieler.basis
- caesreon.commands.spieler.erweitert
- caesreon.commands.spieler.premium
- caesreon.commands.flyspeed
- caesreon.commands.fly
- caesreon.commands.tempfly
- caesreon.commands.admin
- caesreon.commands.schilder

#### Konfigurationshinweis!

Umlaute können in den Konfigurationsdateien nicht sauber genutzt werden da aktuell beim Erstellen der jeweiligen
Konfigurationsdateien unter Linux der Encoding Standard ISO-8859 genutzt wird und dieser nicht unterstützt wird von Java.
Folgende unten aufgeführte Sequenzen können allerdings anstelle des jeweiligen Umlauts eingesetzt werden,
damit der Umlaut im Spiel richtig angezeigt wird.


| Umlaut | Variante 1 | Variante 2 |
|:------:|------------|------------|
|   Ä    | \u00C4     | \umlAE     |
|   Ö    | \u00D6     | \umlOE     |
|   Ü    | \u00DC     | \umlUE     |
|   ä    | \u00F4     | \umlae     |
|   ö    | \u00F6     | \umloe     |
|   ü    | \u00FC     | \umlue     |
|   ß    | \u00DF     | \umls      |

### Services

#### Caesreon Wirtschaft's Service

Das Modul Wirtschaft umfasst eine Standalone Wirtschaft welche global genutzt werden kann und über die PlaceholderAPI
auch durch andere Plugins abgefragt werden kann. Außerdem fungiert dieses Modul durch die MySQL Datenbank auch als "
Economy-Bridge", welche den Kontostand des Spielers über mehrere Server hinweg synchronisiert.

##### Funktionsweise:

Um eine möglichst hohe Performance zu erzielen und um die SQL Last zu mindern, wird der Kontostand des Spielers inform
einer Hashmap im Servercache gespeichert, sobald dieser sich auf unserem Server einloggt. Alle auf den Kontostand
zugreifenden Methoden fragen dabei den Cached Hashwert ab, während alle modifizierenden Methoden den Spielerkontostand
direkt in der Datenbank un den Cached Hashwert verändern. Loggt der Spieler sich aus, wird der Hashwert in die Datenbank
übertragen und der Spieler aus dem Cache entfernt.

##### Spielerbefehle:

- /geld senden [Spielername] <Betrag> - Überweist Geld an den Spieler
- /geld top - Zeigt die Top 10 Besitzenden an

##### Permissiongruppe:

- caesreon.commands.spieler.basis

##### Teambefehle:

- /geld setzen [Spielername] <Betrag> - setzt den Geldwert des Spielers absolut
- /geld geben [Spielername] <Betrag> - addiert Betrag zum aktuellen Kontostand des Spielers
- /geld geben [Spielername] <Betrag> <Verwendungszweck> - addiert Betrag zum aktuellen Kontostand des Spielers und
  begründet Vorgang in Transaktionslog
- /geld abziehen [Spielername] <Betrag> - subtrahiert Betrag zum aktuellen Kontostand des Spielers
- /geld einsehen [Spielername] - zeigt den Kontostand eines Spielers an

##### Permissiongruppe:

- caesreon.commands.team

##### Placeholders:

- %cae_kontostand% - gibt Kontostand des Spielers als String wieder

##### Nationalbank:

- uuid: ffffffff-ffff-ffff-ffff-000000000000
- name: Nationalbank

#### Caesreon Shops / Handelssystem

Dieses Modul ermöglicht es Spielern, eigene Geschäfte zu eröffnen und dadurch auch mit Offlinespielern zu handeln.
Zugrundeliegend ist hierfür das Economy Modul.

##### Funktionsweise
Es muss ein Schild wie unten erstellt werden und der gewünschte Inhalt, welcher verkauft werden soll
in die Kiste hereingelegt werden. Danach muss der Spieler einmal mit Links-Klick das Schild anklicken
und danach mit Links-Klick die Kiste. Hat alles funktioniert, wird der Spieler benachrichtigt, dass er
erfolgreich sein Geschäft erstellt hat. Wenn er selbiges Geschäft löschen möchte, so muss
er das aktive Geschäftsschild mit einer goldenen Holzaxt abbauen. Nach erfolgreicher Löschung aus der 
Datenbank wird der Spieler wieder informiert
###### Schild: Händler verkauft an Spieler
1. [Kaufen]
2. Beschreibung
3. Anzahl
4. Preis
###### Schild: Händler kauft von Spieler
1. [Verkaufen]
2. Beschreibung
3. Anzahl
4. Preis
###### Schild: AdminShop verkauft an Spieler
1. [A-Kaufen]
2. Beschreibung
3. Anzahl
4. Preis
###### Schild: AdminShop  kauft von Spieler
1. [A-Verkaufen]
2. Beschreibung
3. Anzahl
4. Preis

##### Permissiongruppe:

- caesreon.commands.shops

#### Caesreon Mailing Service

Dieses Modul ermöglicht es, Nachrichten an Spieler zu senden, welche nicht Online sind. Diese Nachrichten werden dabei
mit einer 128-Bit Verschlüsselung übermittelt und auf unseren Servern gespeichert. In Zukunft soll dieses Modul auch die
Kommunikation über eine eigens für den Server entwickelte App ermöglichen.

##### Commands:

- /mail senden [Spielername] [Nachricht] - Sendet eine Nachricht an einen Offline/Online Spieler
- /mail send [Spielername] [Nachricht] - Sendet eine Nachricht an einen Offline/Online Spieler
- /mail empfangen - Forciert beim Befehlsausführenden die Abfrage, ob eine Mail im Postkasten ist
- /mail öffnen [Nummer] - Öffnet Mail im Postkasten, sofern diese vorhanden ist

##### Permissiongruppe:

- caesreon.commands.spieler.basis

#### Caesreon User-Werben-User Service

Über das "User werben User" Modul können Spieler Freunde, welche noch nie auf unserem Server gespielt haben, einladen
und exklusive Belohnungen erhalten. Dabei wird es in kommenden Versionen auch möglich sein, nachträglich Belohnungen zu
erhalten in Relation zu der Spielzeit des angeworbenen Spielers bzw. der Anzahl der geworbenen Spieler durch den
Werbenden.

##### Spielerbefehle:

- /uwu code erstellen - Gibt Token des Spielers wieder und erstellt einen Token, wenn noch keiner vorhanden ist
- /uwu code einlösen [TOKEN] - Löst den Token ein und belohnt Tokenbesitzer und den einlösenden Spieler

#### Login-Belohnungen

Im Modul "Login-Belohnungen" kann der Spieler sich Belohnungen für das tägliche einloggen claimen. Je größer der Streak,
desto wahrscheinlicher sind auch hochwertige Belohnungen.

##### Funktionsweise:

Wenn der Spieler sich einloggt, prüft das System ob der Spieler bereits seine Belohnung eingelöst hat und ob dieser in der Gruppe 
Zweitaccount ist. Wenn Nein,
erinnert das System den Spieler inform einer Nachricht. Claimed der Spieler seine Login-Belohnung, prüft das System
wieviele Claims der Spieler in dem jeweiligen Monat schon hatte und ob der Zeitstempel größer ist als der in der DB
gespeicherte Zeitstempel (Validierungszeit). Dabei kann ein Spieler nie mehr Claims haben wie der Monat an Tage hat.
Sind die beiden Bedingungen erfült, erhält der Spieler seine für den jeweiligen Tag in einer Konfigurationsdatei
gespeicherte Belohnung. Danach wird in der Datenbank der Wert der Claims erhöht sowie die Validierungszeit auf 0:00 des
nächsten Tages gesetzt. Parallel dazu prüft das System ob der erste des Monats ist und setzt wenn Monatsanfang, alle
Monatlichen Claims auf 0.

##### Spielerbefehle:

- /belohnung einlösen
- /belohnung heute
- /belohnung monat

##### Permissiongruppe:

- caesreon.commands.spieler.loginbelohnung

##### Adminbefehle:

- /belohnung test <tag> (Testet jeweiliges Tagesitem)

##### Permissiongruppe:

- caesreon.commands.admin

#### Caesreon Region Markt

Dieses Modul ermöglicht es, Grundstücke mit einem vorgegebenen Preis vom Server oder an den Server zu verkaufen. Auch
ist es möglich, Grundstücke zu einem eigenen Preis an andere Spieler zu verkaufen. Auch ist eine einfache Verwaltung des
Grundstücks möglich, um Spieler als Besitzer oder Mitglied hinzuzufügen. Außerdem können Spieler sich ihre eigenen
Freebuild Grundstücke sichern indem sie mit einem einfachen Befehl diese claimen.

##### Funktionsweise

Die maximale Anzahl der Claims (nachfolgend x genannt) und der Basispreis (nachfolgend y genannt) sind in der
Konfiguration hinterlegt und veränderbar. Der Plotpreis für die Freebuildgrundstücke basiert aktuell auf der
progressiven Formel f(x)=0,1x²*60x+y und ist nicht veränderbar.

In kommenden Erweiterungen wird es zudem möglich sein, dass Grundstück per GUI zu verwalten sowie sich die
Grundstücksgrenzen per Befehl anzeigen zu lassen. Auch soll der Nutzer eigene Formeln implementieren können welche dann
in das Plugin geparst wird.

##### Spielerbefehle:

- /gs kaufen
- /gs verkaufen
- /gs hinzufügen owner [GS-Name] [Spielername]
- /gs hinzufügen member [GS-Name] [Spielername]
- /gs + -o [GS-Name] [Spielername]
- /gs hinzufügen owner alle [Spielername]
- /gs hinzufügen member alle [Spielername]
- /gs + -o -a [Spielername]
- /gs + -m -a [Spielername]
- /gs entfernen owner [GS-Name] [Spielername]
- /gs entfernen member [GS-Name] [Spielername]
- /gs - -o [GS-Name] [Spielername]
- /gs - -m [GS-Name] [Spielername]
- /gs limits
- /gs claim
- /gs claims
- /gs gui
- /gs info

##### Permissiongruppe:

- caesreon.commands.spieler.grundstücke
- caesreon.schilder.grundstücke

##### Teambefehle:

- /gs enteignen [Plotname]
- /gs setze [Owner]/[Member] [Plotname] [Spielername]
- /gs entferne [Owner]/[Member] [Plotname] [Spielername]
- /gs erstellen [Plotname]
- /gs löschen [Plotname]
- /gs import [Plotname]

##### Permissiongruppe:

- caesreon.commands.team
- caesreon.commands.team.grundstücke.import

#### Caesreon Berufe

##### Funktionsweise:

##### Spielerbefehle:

- /beruf

##### Permissiongruppe:

- caesreon.commands.spieler.berufe
- caesreon.schilder.beruf
- caesreon.commands.spieler.beruf.verdienst

##### Teambefehle:

- /beruf hinzuefuegen [berufname]
- /beruf entfernen [berufname]

##### Permissiongruppe:

- caesreon.commands.team.administrator
- caesreon.commands.team.moderator

#### Sonstiges

##### Adminbefehle:

- /es (Beschwört Elderstab)

##### Permissiongruppe:

- caesreon.items.admin.elderstab

[PlaceholderApi]: <https://www.spigotmc.org/resources/placeholderapi.6245>

[LuckPerms]: <https://luckperms.net/>

[UltimateVotes]: <https://www.spigotmc.org/resources/ultimatevotes-1-8-8-1-16-2-spigot-bungeecord-uuid.516/>

[LightApi]: <https://www.spigotmc.org/resources/lightapi-fork.48247/>
