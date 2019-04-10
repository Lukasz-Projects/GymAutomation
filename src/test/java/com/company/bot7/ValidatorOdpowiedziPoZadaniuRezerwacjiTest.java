package com.company.bot7;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.Assert.*;

public class ValidatorOdpowiedziPoZadaniuRezerwacjiTest {

    private final ValidatorOdpowiedziPoZadaniu validatorPozytywnejOdpowiedzi;
    private final ValidatorOdpowiedziPoZadaniu validatorNegatywnejOdpowiedzi;


    public ValidatorOdpowiedziPoZadaniuRezerwacjiTest(){
        final LocalDateTime obiektCzasu = LocalDateTime.parse("24-10-2017 19",DateTimeFormatter.ofPattern("dd-MM-uuuu HH"));
        this.validatorPozytywnejOdpowiedzi = new ValidatorOdpowiedziPoZadaniuRezerwacji(
                new ZadanieZarezerwowaniaGodziny(obiektCzasu),
                new ImportantDataFromResponse(false,"<?xml version=\"1.0\"?>\n" +
                        "<response>\n" +
                        "\t<params>\n" +
                        "\t\t<status>success</status>\n" +
                        "\t\t<showDate>24-10-2017</showDate>\n" +
                        "\t\t<showHour>19</showHour>\n" +
                        "\t\t<showWday>2</showWday>\n" +
                        "\t</params>\n" +
                        "</response>",200)
                );
        this.validatorNegatywnejOdpowiedzi = new ValidatorOdpowiedziPoZadaniuRezerwacji(
                new ZadanieZarezerwowaniaGodziny(obiektCzasu),
                new ImportantDataFromResponse(true,"<?xml version=\"1.0\"?>\n" +
                        "<response><params><status>error</status><error>zajety</error></params></response>",200)
        );
    }

    @org.junit.Test
    public void czyOdpowiedźMaPoprawnyStatus() throws Exception {
        assertTrue(validatorPozytywnejOdpowiedzi.czyOdpowiedźMaPoprawnyStatus());
        assertFalse(validatorNegatywnejOdpowiedzi.czyOdpowiedźMaPoprawnyStatus());
    }

    @org.junit.Test
    public void czyZadanieSięUdało() throws Exception {
        assertTrue(validatorPozytywnejOdpowiedzi.czyZadanieSięUdało());
        assertFalse(validatorNegatywnejOdpowiedzi.czyZadanieSięUdało());
    }

    @org.junit.Test
    public void poprawnaOdpowiedź() throws Exception {
        final Optional optionalPozytywnejOdpowiedzi = validatorPozytywnejOdpowiedzi.poprawnaOdpowiedź();
        final Optional optionalNegatywnejOdpowiedzi = validatorNegatywnejOdpowiedzi.poprawnaOdpowiedź();
        assertTrue(optionalPozytywnejOdpowiedzi.isPresent());
        assertFalse(optionalNegatywnejOdpowiedzi.isPresent());
    }

}