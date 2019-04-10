package com.company.bot7;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ZapiszPolecenia {
    private final ZwracającyListęPoleceń zwracającyListęPoleceń;
    private final String sciezka;

    public ZapiszPolecenia(ZwracającyListęPoleceń zwracającyListęPoleceń,String sciezka){
        this.zwracającyListęPoleceń = zwracającyListęPoleceń;
        this.sciezka = sciezka;
    }

    public void zapisz() {
        List<StrukturaParametruKonfigu> listaPoleceń = zwracającyListęPoleceń.zwróćPolecenia();

        final File plik = new File(sciezka);
        if (!plik.exists()) {
            try {
                plik.createNewFile();
            } catch (IOException e) {
                System.err.print("Problem z tworzeniem pliku w ZapiszPolecenia::zapisz()");
                return;
            }
        }

        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(plik, false))) {
            final DateTimeFormatter obiektDoFormatowania = DateTimeFormatter.ofPattern("dd-MM-uuuu HH");
            for (StrukturaParametruKonfigu struktura : listaPoleceń) {
                String dataIgodzina = struktura.getCzas().format(obiektDoFormatowania);
                String rodzajPolecenia = null;
                if (struktura.getRodzajPolecenia().equals(StrukturaParametruKonfigu.RodzajPolecenia.BOOK))
                    rodzajPolecenia = "book a gym";
                if (struktura.getRodzajPolecenia().equals(StrukturaParametruKonfigu.RodzajPolecenia.UNBOOK))
                    rodzajPolecenia = "delete booking";
                writer.write(rodzajPolecenia + "=" + dataIgodzina);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.print("Problem z zapisem w ZapiszPolecenia::zapisz()");
        }
    }
}
