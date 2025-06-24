# Passwort-Safe – Grundkonzept

## 1. Einleitung

Die vorliegende Web-Applikation dient der sicheren Speicherung von Zugangsdaten wie Passwörtern, Benutzernamen und URLs. Sie basiert auf einem modernen Technologie-Stack mit einem **React-Frontend**, einem **Spring Boot-Backend** und einer optionalen **relationalen Datenbank**. Ziel ist es, eine Multi-User-fähige Anwendung zu entwickeln, die höchsten Sicherheitsstandards genügt.

---

## 2. Systemarchitektur

Die Anwendung folgt einer klassischen 2-Schichten-Architektur (Client & Server). Das System besteht aus:

- **Frontend (React):** Darstellung, Benutzerinteraktion, Login-Maske, Passwortverwaltung
- **Backend (Spring Boot):** REST-API, Authentifizierung, Autorisierung, Datenverschlüsselung
- **Persistenzschicht:** Speicherung verschlüsselter Datensätze (wahlweise echte DB oder In-Memory Mock)

![Systemarchitektur](./bilder/architektur.png)

---

## 3. Funktionalität

- Benutzer-Login (mit Master-Passwort)
- Verschlüsselte Speicherung von Zugangsdaten (URL, Benutzername, Passwort, E-Mail, Bemerkung, Rubrik)
- CRUD-Funktionen: Einträge hinzufügen, bearbeiten, löschen
- Rubriken wie „Privat“, „Geschäft“, „Games“, etc.
- Änderung des Master-Passworts

---

## 4. Authentisierung & Autorisierung

- Benutzer sind fest im System (hart codiert) hinterlegt.
- Authentisierung erfolgt über Master-Passwort.
- Nach erfolgreichem Login:
  - Session- oder Token-Handling
  - Entschlüsselung der Zugangsdaten
- Jeder Benutzer sieht nur seine eigenen Daten (Zugriffskontrolle)

---
0
## 5. Kryptografie-Konzept

### Verwendete Algorithmen:

- **Symmetrische Verschlüsselung:** AES (Advanced Encryption Standard)
- **Key-Derivation:** PBKDF2 mit Salt & Pepper für Master-Passwort
- Optional: RSA für Schlüsselaustausch oder Sicherung

### Ablauf:

Master-Passwort --PBKDF2+Salt--> AES-Key --> Daten werden verschlüsselt/entschlüsselt
Besonderheit:
Der AES-Schlüssel wird nie gespeichert.

Alle Zugangsdaten werden ausschliesslich verschlüsselt gespeichert.

## 6. Passwort ändern – Konzept
- Benutzer gibt altes und neues Passwort ein.
- Daten werden mit altem Schlüssel entschlüsselt.
- Danach mit neuem Schlüssel verschlüsselt gespeichert.

Vergessener Zugang:
Ohne Master-Passwort ist kein Zugriff mehr möglich → bewusstes Sicherheitskonzept (Zero-Knowledge)

## 7. Sicherheitsaspekte
Allgemeine Sicherheitsmassnahmen
Kein Klartext-Passwort im Speicher oder in der Datenbank

- Verwendung von type="password" im Frontend

- Passwortstärke-Prüfung (Länge, Sonderzeichen, Gross-/Kleinbuchstaben)

- Salt + Pepper bei Passwort-Hashing

- Zugriffskontrolle serverseitig                                                                                                                    

- HTTPS empfohlen

### OWASP Top 10 Risiko-Minderung
Risiko	                    Massnahme
Broken Access Control	      Serverseitige Rechteprüfung
Injection	                  Prepared Statements / Validierung
Cross-Site-Scripting (XSS)	Input-Sanitizing im Frontend
Cryptographic Failures	    AES + PBKDF2, keine Klartext-Daten
Insecure Design	            Trennung Logik/UI, Zero-Knowledge-Konzept
Identification Failures	    Starke Authentisierung mit Passwortprüfung

## 8. Datenstruktur (Beispiel)
json
Kopieren
Bearbeiten
{
  "id": 1,
  "rubrik": "Privat",
  "url": "https://example.com",
  "benutzername": "max@example.com",
  "passwort": "ENCRYPTED_DATA",
  "bemerkung": "Backup via 2FA",
  "email": "max@example.com"
}