package com.company.bot7;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ZapiszWystapienieBleduDoLogu {
    public enum RodzajOperacjiNaKoncie{
        BOOK,
        UNBOOK,
        RETRIEVE_MY_BOOKED_HOURS
    }

    private final String sciezka;
    private final RodzajOperacjiNaKoncie rodzajOperacjiNaKoncie;
    private final LocalDateTime godzinaIdataRezerwacji;

    public ZapiszWystapienieBleduDoLogu(String sciezka, RodzajOperacjiNaKoncie rodzajOperacjiNaKoncie) {
        this.sciezka = sciezka;
        this.rodzajOperacjiNaKoncie = rodzajOperacjiNaKoncie;
        this.godzinaIdataRezerwacji = null;
    }

    public ZapiszWystapienieBleduDoLogu(String sciezka, RodzajOperacjiNaKoncie rodzajOperacjiNaKoncie,LocalDateTime godzinaIdataRezerwacji) {
        this.sciezka = sciezka;
        this.rodzajOperacjiNaKoncie = rodzajOperacjiNaKoncie;
        this.godzinaIdataRezerwacji = godzinaIdataRezerwacji;
    }

    public void zapisz(){
        final LocalDateTime localDateTime = LocalDateTime.now();
        final String komunikat;

        if (this.godzinaIdataRezerwacji == null){
            switch (rodzajOperacjiNaKoncie){
                case BOOK: komunikat = (localDateTime.format(DateTimeFormatter.ISO_DATE_TIME) + " wystąpił błąd podczas rezerwowania.") ; break;
                case UNBOOK: komunikat = (localDateTime.format(DateTimeFormatter.ISO_DATE_TIME) + " wystąpił błąd podczas rezygnacji z rezerwacji.") ; break;
                case RETRIEVE_MY_BOOKED_HOURS: komunikat = (localDateTime.format(DateTimeFormatter.ISO_DATE_TIME) + " wystąpił błąd podczas pobierania listy moich rezerwacji.") ; break;
                default: komunikat = "Wystąpił jakiś błąd." ; break;
            }
        } else {
            switch (rodzajOperacjiNaKoncie){
                case BOOK: komunikat = (localDateTime.format(DateTimeFormatter.ISO_DATE_TIME) + " wystąpił błąd podczas rezerwowania na godzinę " + godzinaIdataRezerwacji.format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH"))) ; break;
                case UNBOOK: komunikat = (localDateTime.format(DateTimeFormatter.ISO_DATE_TIME) + " wystąpił błąd podczas rezygnacji z rezerwacji na godzinę " + godzinaIdataRezerwacji.format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH"))) ; break;
                case RETRIEVE_MY_BOOKED_HOURS: komunikat = (localDateTime.format(DateTimeFormatter.ISO_DATE_TIME) + " wystąpił błąd podczas pobierania listy moich rezerwacji na godzinę " + godzinaIdataRezerwacji.format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH"))) ; break;
                default: komunikat = "Wystąpił jakiś błąd." ; break;
            }
        }

        File plik = new File(sciezka);
        if (!plik.exists()){
            try {
                plik.createNewFile();
            } catch (IOException e) {
                System.err.println("Problem z tworzeniem pliku w ZapiszWystapienieBleduDoLogu::zapisz()");
            }
        }
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(plik,true))){
            writer.write(komunikat);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Problem z zapisem do logu");
        }
    }
}
