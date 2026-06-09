# NHPlus — Lernsituation Lernfeld 5

## Worum geht es?

NHPlus ist eine bestehende Anwendung zur Verwaltung eines Pflegeheims. Sie ermöglicht das Anlegen, Anzeigen, Ändern und Löschen von Patienten und deren Behandlungen. Die Anwendung ist mit **Java**, **JavaFX** und einer **SQLite-Datenbank** realisiert.

Eure Aufgabe ist es, die Anwendung zu **analysieren**, **zu erweitern** und **datenschutzkonform** zu machen.

## Was lernt ihr?

- Entwurfsmuster (DAO-Pattern, MVP-Pattern, Singleton, Factory) in einer bestehenden Codebasis erkennen und anwenden
- Eine relationale Datenbank über JDBC ansprechen
- Datenschutzanforderungen (DSGVO) analysieren und technisch umsetzen
- User Stories mit Akzeptanzkriterien, Tasks und Testfällen formulieren
- Im Team an einer gemeinsamen Codebasis arbeiten
- Software systematisch testen

## Ablauf

Die Lernsituation besteht aus fünf Arbeitsblättern, die aufeinander aufbauen:

| AB | Thema | Zeitrahmen | Was ihr tut |
|---|---|---|---|
| [AB 01](doc/AB01-Analyse-der-Architektur.md) | Analyse der Architektur | ~4 UStd | Bestehenden Code und Entwurfsmuster verstehen |
| [AB 02](doc/AB02-Datenschutzanalyse.md) | Datenschutzanalyse | ~3-4 UStd | DSGVO-Grundlagen (Webapp) + Verstöße in NHPlus identifizieren |
| [AB 03](doc/AB03-User-Stories-Akzeptanzkriterien-Tasks-Testfaelle.md) | Planung | ~4-5 UStd | User Stories, Tasks und Testfälle erstellen |
| [AB 04](doc/AB04-Implementierung.md) | Implementierung | ~9-12 UStd | Pfleger-Modul und Login-System entwickeln |
| [AB 05](doc/AB05-Testen.md) | Testen | ~3 UStd | Manuell und mit JUnit testen, Retrospektive |

## Gruppen

Ihr arbeitet in **festen 3er-Gruppen über alle Arbeitsblätter**. 

## Erste Schritte

1. **Git-Repository anlegen:** Forkt oder klont dieses Repository in eurer Gruppe. Hinterlegt den Link zu eurem Repository in **Its-Learning**.
2. **Projekt in IntelliJ öffnen**.
3. **Vor dem ersten Start einmal `SetUpDB` ausführen**, damit die Datenbank mit Tabellen und Testdaten angelegt wird.

## Ergebnisse dokumentieren

Eure Antworten tragt ihr in den vorbereiteten Templates im Ordner [`abgabe/`](abgabe/README.md) ein — eine Markdown-Datei pro Arbeitsblatt (`abgabe/AB01.md` – `abgabe/AB05.md`). Die Aufgabenstellungen selbst in `doc/` bleiben unverändert.

## Projektstruktur

```
src/main/java/de/hitec/nhplus/
├── Main.java                  ← Startklasse der Anwendung
├── model/                     ← Datenmodelle (Patient, Treatment, ...)
├── datastorage/               ← Datenbankzugriff (DAO-Pattern)
├── controller/                ← Presenter/Controller (MVP-Pattern)
└── utils/                     ← Hilfsfunktionen (DateConverter, SetUpDB, PasswordUtil für AB 04)

src/main/resources/de/hitec/nhplus/
├── MainWindowView.fxml        ← Hauptfenster
├── AllPatientView.fxml        ← Patientenübersicht
└── ...                        ← Weitere Views

db/nursingHome.db              ← SQLite-Datenbank
```

## Datenbank zurücksetzen

Falls eure Datenbank durch Tests inkonsistent geworden ist, könnt ihr sie über die Klasse `SetUpDB` (im Package `utils`) zurücksetzen. **Achtung:** Das löscht alle Tabellen und erstellt sie mit Testdaten neu. Eigene Tabellenänderungen gehen dabei verloren.

## Demo-Logins

Die Standardnutzer werden beim Start bzw. nach einem Reset der Datenbank automatisch angelegt:

- `admin` / `admin123` — **Wohnbereichsleitung** (alle Rechte inkl. Benutzerverwaltung)
- `pflege` / `pflege` — **Pflegekraft** (anzeigen, anlegen, bearbeiten)
- `guest` / `qwerty` — **Leserechte** (nur anzeigen/exportieren)
- `blocked` / `password` — **Keine Rechte**

## Kurz zu den hinzugefügten Features

- Login mit Benutzerkonto, Passwort-Hashing und aktiver Session
- Rollen- und Rechteverwaltung inkl. Ansicht `Benutzerverwaltung`
- Rechteabhängige Freigabe/Sperrung von Aktionen in den Modulen
- PDF-Export für Patientendaten
- Löschvormerkung für Pflegekräfte mit Aufbewahrungsfrist

## Wichtige Ressourcen

| Ressource | Beschreibung |
|---|---|
| [Technische Referenz](doc/IB-Technische-Referenz.md) | IntelliJ-Einrichtung, SQLite, SceneBuilder, module-info |
| [MVP in JavaFX](doc/IB-MVP-in-JavaFX.md) | Erklärung der verwendeten Architektur |
| [Informationsblatt: User Stories](doc/IB-User-Stories.md) | Aufbau und Beispiele für User Stories |
| [JDBC-Treiber einbinden](doc/IB-JDBC-Treiber.md) | Wie die SQLite-JDBC-Abhängigkeit in `pom.xml` eingetragen wird |
| [Klassendiagramm: DAO-Pattern](doc/media/klassendiagramm-dao-pattern.jpg) | Übersicht der DAO-Schicht |
| [Bewertungsraster](doc/Bewertungsraster.md) | Woran eure Arbeit gemessen wird |

## Javadoc

Für jede Klasse, die ihr neu implementiert, muss eine Javadoc erstellt werden. Details und ein Beispiel findet ihr in [AB 04 — Implementierung](doc/AB04-Implementierung.md#javadoc).
