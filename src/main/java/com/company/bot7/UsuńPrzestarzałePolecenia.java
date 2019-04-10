package com.company.bot7;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsuńPrzestarzałePolecenia implements ZwracającyListęPoleceń {
    private final ZwracającyListęPoleceń readOrdersFromFile;


    public UsuńPrzestarzałePolecenia(final ZwracającyListęPoleceń readOrders){
        if (readOrders == null)
            throw new RuntimeException("Null w konstruktorze UsuńPrzestarzałePolecenia");
        this.readOrdersFromFile = readOrders;
    }

    public List<StrukturaParametruKonfigu> zwróćPolecenia(){
//        45 minut przed obecnym czasem. Czyli o 15:45 usunie wpisy z 15:00 i wcześniejsze. Pozwoli to na
//        zarezerwowanie siłowni na obecną godzinę, no chyba że zostało <15 minut, wtedy zwykle nie ma sensu.
        final LocalDateTime currentDateTime = LocalDateTime.now().minusMinutes(45);
        final List<StrukturaParametruKonfigu> wczytanaLista = readOrdersFromFile.zwróćPolecenia();
        final List<StrukturaParametruKonfigu> tmpLista = new ArrayList<>();

        wczytanaLista.forEach((StrukturaParametruKonfigu struktura) -> {
            if (!struktura.getCzas().isBefore(currentDateTime))
                tmpLista.add(struktura);
        });

        return tmpLista;
    }
}
