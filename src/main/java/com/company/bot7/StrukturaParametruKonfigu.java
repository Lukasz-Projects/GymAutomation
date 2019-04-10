package com.company.bot7;

import java.time.LocalDateTime;

public class StrukturaParametruKonfigu {
    enum RodzajPolecenia {
        BOOK,
        UNBOOK
    }
    private final RodzajPolecenia rodzajPolecenia;
    private final LocalDateTime czas;

    public StrukturaParametruKonfigu(RodzajPolecenia rodzajpolecenia,LocalDateTime czas){
        this.rodzajPolecenia = rodzajpolecenia;
        this.czas = czas;
    }

    public RodzajPolecenia getRodzajPolecenia() {
        return rodzajPolecenia;
    }

    public LocalDateTime getCzas() {
        return czas;
    }
}
