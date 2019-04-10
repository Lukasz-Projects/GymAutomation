package com.company.bot7;

import java.time.LocalDateTime;

public class OdebranaRezerwacja {
    private final int NUMER_REZERWACJI;
    private final String SALKA;
    private final LocalDateTime CZAS;

    public OdebranaRezerwacja(int numer_rezerwacji, String salka, LocalDateTime czas) {
        NUMER_REZERWACJI = numer_rezerwacji;
        SALKA = salka;
        CZAS = czas;
    }

    public int getNUMER_REZERWACJI() {
        return NUMER_REZERWACJI;
    }

    public String getSALKA() {
        return SALKA;
    }

    public LocalDateTime getCZAS() {
        return CZAS;
    }
}
