package com.company.bot7;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReadOrdersFromFile implements ZwracającyListęPoleceń {
    private final String SCIEZKA;
    private final File PLIK;

    public ReadOrdersFromFile(String SCIEZKA){
        this.SCIEZKA = SCIEZKA;
        this.PLIK = new File(SCIEZKA);
    }

    public List<StrukturaParametruKonfigu> zwróćPolecenia(){
        List<StrukturaParametruKonfigu> lista = new LinkedList<>();

        if (!PLIK.exists() || PLIK.isDirectory()) {
            System.err.print("Nie ma pliku z poleceniami.");
            return lista;
        }

        try (BufferedReader czytnik = new BufferedReader(new FileReader(PLIK))
        ){
            String linia;
            final DateTimeFormatter obiektDoFormatowania = DateTimeFormatter.ofPattern("dd-MM-uuuu HH");
            while ((linia = czytnik.readLine()) != null){
                Scanner skaner = new Scanner(linia);
                skaner.useDelimiter("=");
                String lewaStrona = skaner.next().trim();
                String prawaStrona = skaner.next().trim();
                final StrukturaParametruKonfigu.RodzajPolecenia rodzaj;
                switch (lewaStrona){
                    case "book a gym": rodzaj = StrukturaParametruKonfigu.RodzajPolecenia.BOOK; break;
                    case "delete booking": rodzaj = StrukturaParametruKonfigu.RodzajPolecenia.UNBOOK; break;
                    default: rodzaj = null;
                }
                LocalDateTime czas = LocalDateTime.parse(prawaStrona,obiektDoFormatowania);
                if (czas != null && rodzaj != null)
                    lista.add(new StrukturaParametruKonfigu(rodzaj,czas));
            }

        } catch (IOException e) {
            System.err.println("Błąd we zwróćPolecenia()");
        }

        return lista;
    }
}
