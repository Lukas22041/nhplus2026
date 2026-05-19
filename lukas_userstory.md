### User Story 1: Pflegekräfte Anzeigen

**User Story**

> Als Wohnbereichsleiter möchte ich in der Applikation Pflegekräfte anzeigen lassen können, um einen guten Überblick über die verfügbaren Fachkräfte zu erreichen.

**Akzeptanzkriterien**

| Nr. | Kriterium                                                                                                 |
|-----|-----------------------------------------------------------------------------------------------------------|
| A_1 | Die Daten einer Pflegekraft besteht aus dem Vornamen, Nachnamen, der Telefon Nummer und Wochenarbeitszeit |
| A_2 | Alle Pflegekräfte werden in einer tabellarischen darstellung angezeigt                                    |
| A_3 | Die darstellung kann von der Seitenliste aus geöffnet werden                                              |

**Tasks**

| Nr. | Task                                                              |
|-----|-------------------------------------------------------------------|
| T_1 | Caregiver Model Klasse erstellen                                  |
| T_2 | Cargiver DAO Klasse mit CRUD methoden erstellen                   |
| T_3 | Datenbank Tabelle für die Pflegekräfte erstellen                  |
| T_4 | Die Datenbank Tabelle mit Standarddaten befüllen                  |
| T_5 | Die JavaFX view Erstellen                                         |
| T_6 | Den Controller für die View erstellen und Verknüpfen              |
| T_7 | Einen Knopf an die Seitenliste für dsa öffnen der View hinzufügen |

**Testfälle**

**TF1:** Alle Pflegekräfte Anzeigen

| |                                                                                                               |
|---|---------------------------------------------------------------------------------------------------------------|
| **Vorbedingung** | Der Nutzer hat die Option "Pflegekräfte" ausgewählt                                                           |
| **Testschritte** |                                                                                                               |
| **Erwartetes Ergebnis** | Es werden alle Pflegekräfte mit ihren Vornamen, Nachnamen, Telefonnummer und ihrer Wochenarbeitzeit angezeigt |

### User Story 2: Pflegekräfte Erstellen und Bearbeiten

**User Story**

> Als Wohnbereichsleiter möchte ich in der Applikation Pflegekräfte erstellen und ändern können, um die daten der Pflegekräfte über das Programm pflegen zu können.

**Akzeptanzkriterien**

| Nr. | Kriterium                                                                  |
|-----|----------------------------------------------------------------------------|
| A_1 | Durch das Program können neue Pflegekräfte erstellt und bearbeitet werden. |
| A_2 | Die erstellten und veränderten pflegekräfte werden gespeichert             |

**Tasks**

| Nr. | Task                                                                                                                              |
|-----|-----------------------------------------------------------------------------------------------------------------------------------|
| T_1 | Der Presenter für die Pflegekräfte seite muss erweitert werden.                                                                   |
| T_2 | Ein Widget für die erstellung von pflegekräften muss hinzugefügt werden, welches jede Charakteristik der Pflegekräfte beinhaltet. |
| T_3 | Die Tabelle der Pflegekräfte muss bearbeitbar gemacht werden, und änderung in der Datenbank gespeichert werden.                   |

**Testfälle**

**TF1:** Pflegekraft hinzufügen

| |                                                                                                                    |
|---|--------------------------------------------------------------------------------------------------------------------|
| **Vorbedingung** | Der Nutzer hat "Pflegekräfte" ausgewählt                                                                           |
| **Testschritte** | 1. Die Pflegekrafts daten in die Eingaben eingeben. <br/>2. Auf Hinzufügen Drücken. <br/>3. Das Programm neustarten.    |
| **Erwartetes Ergebnis** | Nach dem Hinzufügen ist die Pflegekraft in der Tabellen einsicht sichtbar, auch nach dem neustarten des Programmes |

**TF2:** Pflegekräfte bearbeiten.

| |                                                                                                                  |
|---|------------------------------------------------------------------------------------------------------------------|
| **Vorbedingung** | Der Nutzer hat "Pflegekräfte" ausgewählt                                                                         |
| **Testschritte** | 1. Drücke auf eine Zelle in der Tabelle. <br/>2. Bearbeite den Wert und drücke Enter <br/>3. Starte das Programm neu. |
| **Erwartetes Ergebnis** | Die Zellen sollten bearbeitbar sein und die Veränderung sichtbar sogar nach dem neu starten des Programmes.      |


### User Story 3: Pflegekräfte Löschen

**User Story**

> Als Wohnbereichsleiter möchte ich in der Applikation Pflegekräfte löschen können, um die daten von Pflegekräften die nicht mehr bei uns Arbeiten zu entfernen.

**Akzeptanzkriterien**

| Nr. | Kriterium |
|-----|-----------|
| A_1 | |
| A_2 | |
| A_N | |

**Tasks**

| Nr. | Task |
|-----|------|
| T_1 | |
| T_2 | |
| T_N | |

**Testfälle**

**TF1:**

| | |
|---|---|
| **Vorbedingung** | |
| **Testschritte** | |
| **Erwartetes Ergebnis** | |

**TF2:**

| | |
|---|---|
| **Vorbedingung** | |
| **Testschritte** | |
| **Erwartetes Ergebnis** | |


**TFN:**

| | |
|---|---|
| **Vorbedingung** | |
| **Testschritte** | |
| **Erwartetes Ergebnis** | |
