# Changelog

Alle wichtigen änderungen werden hier festgehalten

## [Unreleased]

### Added

- Fähigkeitenbaum, welcher auf den Berufen basiert

## [1.3.4.20] - 2022-02-05

### Added
+ BungeeCommand: /event /survival /lobby /kathedrale
+ ANSITerminalFarben um 8bit Farben im Terminal zu ermögliche, Unabhängig von Spigot oder Bungee
+ ChatBungee UT um Chat und TextComponents einfacher zu händeln

###Changed
~ ChatUT zu ChatSpigot umbenannt

## [1.3.4.19] - 2022-02-04

### Added
+ ILog
+ BungeeLogger, SpigotLogger

###Changed
~ Log zu SpigotLogger/BungeeLogger in allen Klassen

## [1.3.4.18] - 2022-02-02

Hinweis: Zwischenupdate als Speicherung, Logiken nicht fertig!

### Added
+ bungee.yml
+ BungeeMain
+ Bungee Listener Klassen

###Changed
~ Refaktorisierung für BungeeCord Betrieb

## [1.3.4.17] - 2022-02-01
### Added
- Tab-Completitions für die jeweiligen Befehle und Sub-Befehle

### Fixed
- Erster des Monats Bugs welcher Exception wirft
- onDisable illegalState Exception bei new Runnable()
- NPE bei Spieler FirstJoin Event da Reihenfolge falsch war

## [1.3.4.13-1.3.4.16] - 2022-01-28
### Changed
+ ShopHandler cached für das erstellen und updaten von Shops
+ Readme.md geupdated
+ Skynet Lookup für EnderChests bei Online Spielern
+ Reformatting des Codes zur besseren Lesbarkeit und Uniformität des Codes

### Fixed
- Auf Testserver Bug gefixed das Spieler kein Geld bei Itemshops erhalten
- Auf Testserver Bug gefixed das Shop-Schild nach Befüllung des Shops nicht grün wurde

###Added
+ entferneShop() Methode eingebaut damit Spieler mit einer goldenen Axt ihren Shop löschen können

## [1.3.4.12] - 2022-01-15
### Added

- Validator eingebaut, welcher prüft, ob tatsächlich eine Transaktion erfolgreich stattgefunden hat
### Fixed

- Bug "Spieler erhalten kein Geld bei $ Shops" gefixt
- Spieler können mit Bögen keine Mobs verwunden im PvP Modul

## [1.3.4.11] - 2022-01-13
### Changed

- Refaktorisierung RegionMarktHandler damit Massenverarbeitung von GS Cached möglich wird
- Austausch von Strings "Spielername" durch Spieler Objekte um Datenbanktraffic zu reduzieren

## [1.3.4.10] - 2022-01-12
### Added

- Beruf Landwirt

## [1.3.4.9] - 2022-01-05
### Added

- Spieler wird bei Berufelevel Up nun benachrichtigt und es spielt ein
   Firework Sound ab
- PvP Modul eingeführt in welchem der Spieler togglen und entscheiden
  kann ob er überhaupt am PvP teilnehmen möchte oder ob er dies
  grundlegend ablehnt

## [1.3.4.8] - 2022-01-05
### Added

- Caesreon Immobilien: /gs auflösen hinzugefügt
- Einführung Unterscheidungsklassen vSpieler und Spieler, welche den
  Spieler einmal ermittelt und diesen dann in dem Objekt Spieler/vSpieler speichert. Dies senkt die Anzahl der Datenbankverbindungen

## [1.3.4.7] - 2022-01-05
### Changed

- Refaktorisierung WirtschaftsHandler

## [1.3.4.6] - 2022-01-03
### Fixed

- Fix NPE Loginbelohnung onFirstJoin
- Fix FirstJoin Starterkit + Geld