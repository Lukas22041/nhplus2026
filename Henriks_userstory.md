> # User Story : `Datenexport für Patienten`

Als Patient möchte ich meine gespeicherten Daten als Datei herunterladen können,
um sie bei Bedarf weiterzugeben oder für meine Unterlagen abzusichern.

### Akzeptanzkriterien

| Nr. | Kriterium                                                                                                                                                 |
|-----|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| A_1 | In der Patientenansicht gibt es eine Möglichkeit, den Datenexport starten.                                                                                |
| A_2 | Der Export enthält alle zum Patienten gespeicherten Stammdaten (Nachname, Vorname, Geburtsdatum, Pflegestufe, Raumnummer).                                |
| A_3 | Der Export enthält alle zum Patienten gespeicherten Behandlungen (Datum, Beginn, Ende, Kurzbeschreibung, Langbeschreibung).                               |
| A_4 | Die exportierte Datei liegt im PDF Format vor und lässt sich mit einem gängigen PDF Viewer öffnen.                                                        |
| A_5 | Nach erfolgreichem Export erhält der Nutzer eine Bestätigung (z. B. Hinweistext) mit dem Speicherort der Datei.                                           |
| A_6 | Schlägt der Export fehl (z. B. kein Schreibrecht im Zielverzeichnis), wird eine verständliche Fehlermeldung angezeigt; es wird keine leere Datei erzeugt. |
| A_7 | Nur der aktuell ausgewählte Patient kann seine eigenen Daten exportieren, andere Patientendaten werden nicht in die Datei aufgenommen.                    |

### Tasks

| Nr. | Task                                                                                                                                              |
|-----|---------------------------------------------------------------------------------------------------------------------------------------------------|
| T_1 | Export Logik implementieren: Patientenstammdaten und zugehörige Behandlungen aus der Datenbank lesen und als PDF Dokument aufbereiten.            |
| T_2 | Geeignete PDF Bibliothek auswählen, als Abhängigkeit einbinden und ein PDF Utility erstellen, das die fertige PDF Datei im Dateisystem speichert. |
| T_3 | Patientenansicht um eine Export Aktion erweitern (Button oder Menüeintrag).                                                                       |
| T_4 | Controller für die Patientenansicht um die Behandlung der Export Aktion ergänzen (Aufruf der Export Logik, Erfolgs-/Fehlerfeedback).              |
| T_5 | Fehlerfälle behandeln (keine Berechtigung, Festplatte voll, kein Patient ausgewählt) und Nutzerfeedback bereitstellen.                            |

### Testfälle

**TF1: Datenexport erfolgreich durchführen**

|                         |                                                                                                                                                                                                 |
|-------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Vorbedingung**        | Der Nutzer hat in der Patientenansicht einen Patienten ausgewählt, für den Stammdaten und mindestens eine Behandlung in der Datenbank vorhanden sind.                                           |
| **Testschritte**        | 1. Der Nutzer löst den Datenexport aus.                                                                                                                                                         |
| **Erwartetes Ergebnis** | Eine PDF Datei wird im Zielverzeichnis angelegt. Die Datei enthält alle Stammdaten sowie alle Behandlungen des ausgewählten Patienten. Eine Erfolgsmeldung mit dem Speicherpfad wird angezeigt. |

**TF2: Export schlägt fehl (kein Schreibrecht)**

|                         |                                                                                                          |
|-------------------------|----------------------------------------------------------------------------------------------------------|
| **Vorbedingung**        | Das Zielverzeichnis für den Export existiert, aber der Nutzer besitzt keine Schreibberechtigung dafür.   |
| **Testschritte**        | 1. Der Nutzer löst den Datenexport aus.                                                                  |
| **Erwartetes Ergebnis** | Es wird eine verständliche Fehlermeldung angezeigt. Im Zielverzeichnis wird keine (leere) Datei erzeugt. |

**TF3: Exportdatei enthält keine fremden Patientendaten**

|                         |                                                                                                                     |
|-------------------------|---------------------------------------------------------------------------------------------------------------------|
| **Vorbedingung**        | In der Datenbank sind mindestens zwei Patienten mit Behandlungen vorhanden. Der Nutzer hat Patient A ausgewählt.    |
| **Testschritte**        | 1. Der Nutzer löst den Datenexport aus. 2. Die erzeugte Datei wird geöffnet und auf ihren Inhalt geprüft.           |
| **Erwartetes Ergebnis** | Die Datei enthält ausschließlich Daten von Patient A. Daten von Patient B oder anderen Patienten tauchen nicht auf. |