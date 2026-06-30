# DocBooking - System Rezerwacji Terminów Lekarskich

**Demo:** https://perpetual-surprise-production-b801.up.railway.app/

## Dane testowe

| Login | Hasło | Rola |
|-------|-------|------|
| dr.kowalski | haslo123 | Lekarz |
| dr.nowak | haslo123 | Lekarz |
| dr.wisniewski | haslo123 | Lekarz |
| user | haslo1234 | Pacjent |

Pacjenci rejestrują się samodzielnie przez formularz rejestracji.

## Uruchomienie lokalne

```bash
docker compose up --build
```

- Frontend: http://localhost:4200
- Backend: http://localhost:8080

