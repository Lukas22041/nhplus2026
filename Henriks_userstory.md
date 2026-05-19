### User Story 1 : Datenexport für Patienten

**User Story**

> Als Patient möchte ich meine gespeicherten Daten als Datei herunterladen können,
um sie bei Bedarf weiterzugeben oder für meine Unterlagen abzusichern.

**Akzeptanzkriterien**

| Nr. | Kriterium                                                                                                                                                 |
|-----|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| A_1 | In der Patientenansicht gibt es eine Möglichkeit, den Datenexport starten.                                                                                |
| A_2 | Der Export enthält alle zum Patienten gespeicherten Stammdaten (Nachname, Vorname, Geburtsdatum, Pflegestufe, Raumnummer).                                |
| A_3 | Der Export enthält alle zum Patienten gespeicherten Behandlungen (Datum, Beginn, Ende, Kurzbeschreibung, Langbeschreibung).                               |
| A_4 | Die exportierte Datei liegt im PDF Format vor und lässt sich mit einem gängigen PDF Viewer öffnen.                                                        |
| A_5 | Nach erfolgreichem Export erhält der Nutzer eine Bestätigung (z. B. Hinweistext) mit dem Speicherort der Datei.                                           |
| A_6 | Schlägt der Export fehl (z. B. kein Schreibrecht im Zielverzeichnis), wird eine verständliche Fehlermeldung angezeigt; es wird keine leere Datei erzeugt. |
| A_7 | Nur der aktuell ausgewählte Patient kann seine eigenen Daten exportieren, andere Patientendaten werden nicht in die Datei aufgenommen.                    |

**Tasks**

| Nr. | Task                                                                                                                                              |
|-----|---------------------------------------------------------------------------------------------------------------------------------------------------|
| T_1 | Export Logik implementieren: Patientenstammdaten und zugehörige Behandlungen aus der Datenbank lesen und als PDF Dokument aufbereiten.            |
| T_2 | Geeignete PDF Bibliothek auswählen, als Abhängigkeit einbinden und ein PDF Utility erstellen, das die fertige PDF Datei im Dateisystem speichert. |
| T_3 | Patientenansicht um eine Export Aktion erweitern (Button oder Menüeintrag).                                                                       |
| T_4 | Controller für die Patientenansicht um die Behandlung der Export Aktion ergänzen (Aufruf der Export Logik, Erfolgs-/Fehlerfeedback).              |
| T_5 | Fehlerfälle behandeln (keine Berechtigung, Festplatte voll, kein Patient ausgewählt) und Nutzerfeedback bereitstellen.                            |

**Testfälle**

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

---


### User Story 2: Pflegekräfte Löschen

**User Story**

> Als Wohnbereichsleiter möchte ich Pflegekräfte zur Löschung vormerken können, damit nicht mehr tätige Mitarbeiter nach Einhaltung gesetzlicher Fristen und nachvollziehbar aus dem System entfernt werden.

**Akzeptanzkriterien**

| Nr. | Kriterium                                                                                                                      |
|-----|--------------------------------------------------------------------------------------------------------------------------------|
| A_1 | Nur Benutzer mit der Rolle "Wohnbereichsleiter" (ggf. Compliance‑Rolle) können Löschungen anstoßen (UI + API).                  |
| A_2 | Löschvorgang ist eine Vormerkung (Soft‑Delete) mit konfigurierbarer Aufbewahrungsfrist; kein sofortiges Hard Delete.           |
| A_3 | Bei Vormerkung werden `deletion_date`, `deletion_requested_by` und ein Audit‑Eintrag erstellt; Login des Betroffenen wird gesperrt. |
| A_4 | Vorgemerkte Datensätze sind für normale Nutzer nicht sichtbar; nur berechtigte Personen können sie einsehen (protokolliert).     |
| A_5 | Nach Ablauf der Frist führt ein automatischer, protokollierter Job die endgültige Löschung durch oder markiert den Abschluss.    |

**Tasks**

| Nr. | Task                                                                                         |
|-----|----------------------------------------------------------------------------------------------|
| T_1 | UI: Button "Zur Löschung vormerken" + Bestätigungs‑Modal mit Hinweis auf Löschdatum/Frist. |
| T_2 | Backend: Endpunkt zur Vormerkung, Autorisierung, Audit‑Log erstellen, Account deaktivieren.  |
| T_3 | DB: Felder ergänzen (`deleted`, `deletion_date`, `deletion_requested_by`, `deletion_status`).|
| T_4 | Scheduler: Täglicher Job zur Ausführung fälliger Löschungen; Fehlerbehandlung und Logging.    |
| T_5 | Tests & Doku: Autorisierungstests, Scheduler‑Tests, Betriebsdokumentation, Abstimmung mit DSB. |

**Testfälle**

**TF1: Berechtigte Vormerkung**

|                         |                                                                                                 |
|-------------------------|-------------------------------------------------------------------------------------------------|
| **Vorbedingung**        | Nutzer mit Rolle Wohnbereichsleiter ist eingeloggt; Pflegekraft vorhanden.                      |
| **Testschritte**        | 1. WB‑Leiter markiert Pflegekraft zur Löschung; bestätigt im Modal.                            |
| **Erwartetes Ergebnis** | Datensatz markiert (`deleted=true`/`deletion_status='SCHEDULED'`), Login deaktiviert, Audit vorhanden, Löschdatum gesetzt. |

**TF2: Unberechtigter Zugriff**

|                         |                                                             |
|-------------------------|-------------------------------------------------------------|
| **Vorbedingung**        | Normaler Nutzer ist eingeloggt.                             |
| **Testschritte**        | 1. Nutzer versucht Lösch‑UI oder API zu nutzen.             |
| **Erwartetes Ergebnis** | Keine Option in UI; API antwortet 403; keine DB‑Änderung.    |

**TF3: Scheduler führt Löschung aus**

|                         |                                                                 |
|-------------------------|-----------------------------------------------------------------|
| **Vorbedingung**        | Datensatz hat `deletion_date` in der Vergangenheit.              |
| **Testschritte**        | 1. Scheduler läuft; verarbeitet fällige Datensätze.             |
| **Erwartetes Ergebnis** | Datensatz final gelöscht/abgeschlossen; `deletion_status='EXECUTED'`; Audit vorhanden. |

**Rechtlicher Hinweis (kurz)**

Art. 17 DSGVO fordert Löschung, Ausnahmen gelten bei gesetzlichen Aufbewahrungspflichten (z. B. AO §147, HGB §257). Aufbewahrungsfristen pro Datenkategorie konfigurieren und mit dem Datenschutzbeauftragten abstimmen. Keine rechtliche Beratung.