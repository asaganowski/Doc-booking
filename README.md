# DocBooking - System Rezerwacji Terminów Lekarskich

**Demo:** https://perpetual-surprise-production-b801.up.railway.app/doctors

## Dane testowe

| Login | Hasło | Rola |
|-------|-------|------|
| dr.kowalski | haslo123 | Lekarz (Kardiologia) |
| dr.nowak | haslo123 | Lekarz (Pediatria) |
| dr.wisniewski | haslo123 | Lekarz (Ortopedia) |

Pacjenci rejestrują się samodzielnie przez formularz rejestracji.

## Uruchomienie lokalne

```bash
git clone https://github.com/artursaganowski/Proj_ZTI.git
cd Proj_ZTI
docker compose up --build
```

- Frontend: http://localhost:4200
- Backend: http://localhost:8080

## Technologie

- **Backend:** Spring Boot 3.4, Spring Security + JWT, Spring AOP, WebSocket (STOMP)
- **Frontend:** Angular 20, Angular Material
- **Baza:** PostgreSQL 16
- **Infra:** Docker, Railway
