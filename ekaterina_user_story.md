**User Story**

Als Heimleiter möchte ich, dass nur Mitarbeiter mit einem gültigen Passwort auf die
Patientendaten zugreifen können, damit keine fremden Personen die sensiblen Daten
meiner Bewohner sehen.


**Akzeptanzkriterien**

| Nr. | Kriterium                                                                                                               |
|-----|-------------------------------------------------------------------------------------------------------------------------|
| A_1 | Ohne ein gültiges Passwort kann niemand die Patientendaten öffnen oder lesen.                                     |
| A_2 | Beim Start der Anwendung erscheint ein Login-Fenster mit den Feldern "Benutzername" und "Passwort".                      |
| A_3 | Ein Mitarbeiter, der das richtige Passwort eingibt, wird eingeloggt und kann die Patientendaten sehen.                  |
| A_4 | Ein Mitarbeiter, der das falsche Passwort eingibt, bekommt eine Fehlermeldung und keinen Zugang.                        |
| A_5 | Das Passwort wird in der Datenbank nicht im Klartext gespeichert.                                                       |
| A_6 | Ohne erfolgreichen Login kann man keine Patientendaten sehen oder ändern.    |                                               |


**Tasks**

| Nr. | Task                                                                                                                                                 |
|-----|------------------------------------------------------------------------------------------------------------------------------------------------------|
| T_1 | Login-View mit Eingabefeldern für Benutzernamen und Passwort sowie einem "Einloggen" Button erstellen.                                               |
| T_2 | Modelklasse für Nutzer (Benutzername, Passworthash) erstellen und Datenbanktabelle für Nutzer anlegen.                                               |
| T_3 | Authentifizierungslogik implementieren, die die Eingabe mit den gespeicherten Daten vergleicht und den Zugriff entsprechend erlaubt oder verweigert. |
| T_4 | Passworthashing so einbinden, dass kein Passwort im Klartext gespeichert wird.                                                                       |
| T_5 | Fehlermeldungen für ungültige Anmeldungen und für den Fall implementieren, dass das Login-Fenster ohne Eingabe geschlossen wird.                     |

**Testfälle**

**TF1:**

| |            |
|---|------------|
| **Vorbedingung** |Anwendung wird gestartet, das Passwortfeld erscheint|
| **Testschritte** |Der Mitarbeiter gibt das richtige Passwort ein und bestätigt|
| **Erwartetes Ergebnis** |Die Anwendung öffnet sich normal und alle Patientendaten sind sichtbar|


**TF2:**

| | |
|---|-|
| **Vorbedingung** |Anwendung wird gestartet, das Passwortfeld erscheint|
| **Testschritte** |Der Mitarbeiter gibt ein falsches Passwort ein und bestätigt|
| **Erwartetes Ergebnis** |Die Anwendung öffnet sich nicht und zeigt eine Fehlermeldung|


