package com.company.bot7;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class Czekaj {
    final private int roznicaCzasuMiedzySerweremAKliente;
//    w sekundach
    final long MAX_CZEKANIA_ZA_JEDNA_ITERACJA = 2 * 60;

    public Czekaj(int roznicaCzasuMiedzySerweremAKlientem){
        this.roznicaCzasuMiedzySerweremAKliente = roznicaCzasuMiedzySerweremAKlientem;
    }

    public Czekaj(){
        this(0);
    }

    public boolean czekaj(){
        final LocalDateTime obecnyCzas = LocalDateTime.now();
        final LocalDate dataNastepnegoDnia = obecnyCzas.toLocalDate().plusDays(1);
        final LocalDateTime roznicaMiedzyObecnieAJutro = LocalDateTime.of(dataNastepnegoDnia,LocalTime.MIN)
                .minusSeconds(obecnyCzas.toEpochSecond(ZoneOffset.ofHours(0)));
        final LocalDateTime czasCzekania = roznicaMiedzyObecnieAJutro.minusSeconds(roznicaCzasuMiedzySerweremAKliente);
        final long ileCzekac = czasCzekania.toEpochSecond(ZoneOffset.ofHours(0));
        final int ileIteracji = (int)(ileCzekac / MAX_CZEKANIA_ZA_JEDNA_ITERACJA);
        final int reszta = (int)(ileCzekac % MAX_CZEKANIA_ZA_JEDNA_ITERACJA);
        for (int i = 0; i < ileIteracji; ++i){
            try {
                Thread.sleep(1000 * MAX_CZEKANIA_ZA_JEDNA_ITERACJA);
            } catch (InterruptedException e) {

            }
        }
        try {
            Thread.sleep(reszta * 1000);
        } catch (InterruptedException e) {

        }

        return true;
    }
}
