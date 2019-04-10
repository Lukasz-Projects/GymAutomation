package com.company.bot7;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class ZapiszStanRezerwacjiDoPlikuCSV {
    private final String sciezka;
    private final Set<OdebranaRezerwacja> zbiórRezerwacji;

    public ZapiszStanRezerwacjiDoPlikuCSV(final String ścieżka, final Set<OdebranaRezerwacja> zbiórRezerwacji) {
        if (ścieżka == null || zbiórRezerwacji == null)
            throw new RuntimeException("null jako argument w konstruktorze ZapiszStanRezerwacjiDoPlikuCSV");
        this.sciezka = ścieżka;
        this.zbiórRezerwacji = zbiórRezerwacji;
    }

    public void zapisz() {
        final File plik = new File(sciezka);
        if (!plik.exists()) {
            try {
                plik.createNewFile();
            } catch (IOException e) {
                System.err.print("Problem z tworzeniem pliku w ZapiszStanRezerwacjiDoPlikuCSV::zapisz()");
                return;
            }
        }

        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(plik, false))) {
            final DateTimeFormatter obiektDoFormatowania = DateTimeFormatter.ofPattern("dd-MM-uuuu HH");
            for (OdebranaRezerwacja odebranaRezerwacja : zbiórRezerwacji) {
                String dataIgodzina = odebranaRezerwacja.getCZAS().format(obiektDoFormatowania);
                writer.write(odebranaRezerwacja.getSALKA() + "=" + dataIgodzina);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.print("Problem z zapisem w ZapiszStanRezerwacjiDoPlikuCSV::zapisz()");
        }
    }
}