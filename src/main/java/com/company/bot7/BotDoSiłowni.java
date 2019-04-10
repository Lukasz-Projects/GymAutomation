package com.company.bot7;



import java.io.Closeable;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

public class BotDoSiłowni implements Closeable {
    private final String OBECNY_KATALOG;
    private final String NAZWA_KATALOGU = "botsilownia";
    private final String NAZWA_PLIKU_POLECEN = "polecenia_rezerwacji.txt";
    private final String NAZWA_PLIKU_STANU_REZERWACJI = "obecne_rezerwacje.txt";
    private final String NAZWA_PLIKU_DANYCH_DOSTEPOWYCH = "credentials.txt";
    private final String NAZWA_PLIKU_LOGU = "log.txt";
    private Set<LocalDateTime> datyDoZarezerwowania;
    private Set<LocalDateTime> datyRezerwacjiDoUsunięcia;
    private Credentials dane_logowania;
    final private SesjaNaKoncie7DS sns7ds;

    @Override
    public void close(){
        sns7ds.close();
    }

    private enum  SystemOperacyjny {WINDOWS, UNIXLIKE};
    private final SystemOperacyjny systemOperacyjny;

    public BotDoSiłowni() {
        systemOperacyjny = System.getProperty("os.name").contains("Windows") ? SystemOperacyjny.WINDOWS : SystemOperacyjny.UNIXLIKE;
        switch (systemOperacyjny) {
            case WINDOWS:   OBECNY_KATALOG = System.getenv("USERPROFILE"); break;
            case UNIXLIKE:  OBECNY_KATALOG = System.getenv("HOME"); break;
            default:        OBECNY_KATALOG = System.getenv("USERPROFILE"); break;
        }
        datyDoZarezerwowania = new HashSet<>();
        czytajDaneLogowaniaZpliku();
        sns7ds = new SesjaNaKoncie7DS(
                "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0"
                ,dane_logowania.getLogin()
                ,dane_logowania.getPassword());
    }

    public void uruchom(){
        init();
//        System.out.println("init zakończony");
//        main_loop();
    }

    private void main_loop(){
        final Czekaj stoj = new Czekaj();
        while (stoj.czekaj()){
            proceduraAktualizacjiRezerwacji();
        }
    }

    private void init(){
        czytajDaneLogowaniaZpliku();
        proceduraAktualizacjiRezerwacji();
    }

    private void czytajDaneLogowaniaZpliku() {
        dane_logowania = (new ReadCredentialsFromFile(OBECNY_KATALOG + "/" + NAZWA_KATALOGU + "/" + NAZWA_PLIKU_DANYCH_DOSTEPOWYCH)).getDANE_LOGOWANIA();
    }

    private void proceduraAktualizacjiRezerwacji(){
        zaaktualizujZbioryPoleceń();
        zapiszDoPlikuStanSetu(synchronizuj_dane_w_setach_do_serwera());
        rezerwujZgodnieZPoleceniami();
        usuwajZgodnieZpoleceniami();
        zapiszDoPlikuStanSetu(synchronizuj_dane_w_setach_do_serwera());
    }

    private Set<OdebranaRezerwacja> synchronizuj_dane_w_setach_do_serwera(){
        final DaneWykonywanegoZadaniaHTTP daneZadania = new ZadanieOdebraniaListyMoichRezerwacji();
        ImportantDataFromResponse dataFromResponse = sns7ds.wykonajZadanieNaKoncie(daneZadania);
        ValidatorOdpowiedziPoZadaniu validator = new ValidatorOdpowiedziPoZadaniuOdebraniaListyMoichRezerwacji(daneZadania,dataFromResponse);
        int i = 0;
        while (!validator.czyZadanieSięUdało()){
//            nie udało się 1 raz poza pętlą + 2 wewnątrz. Po 3 nieudanych próbach olej i zwróć pusty Set.
            if (i >= 2)
                return new HashSet<>();

            //            czekam 0.5 sekundy - może się coś poprawi
            try {Thread.sleep(500); } catch (InterruptedException e) {}

            dataFromResponse = sns7ds.wykonajZadanieNaKoncie(daneZadania);
            validator = new ValidatorOdpowiedziPoZadaniuOdebraniaListyMoichRezerwacji(daneZadania,dataFromResponse);

            ++i;
        }

        ParserOdpowiedziPoZadaniuOdebraniaListyMoichRezerwacji parserRez =
                new ParserOdpowiedziPoZadaniuOdebraniaListyMoichRezerwacji(validator);
        usuńDatyJużZarezerwowaneZeZbioruDoRezerwacji(parserRez.getZBIOR_REZERWACJI_SILOWNI());
        pozostawTylkoZarezerwowaneDatyWzbiorzeRezerwacjiDoUsunięcia(parserRez.getZBIOR_REZERWACJI_SILOWNI());
        return parserRez.getZBIOR_REZERWACJI_SILOWNI();
    }

    private void zapiszDoPlikuStanSetu(Set<OdebranaRezerwacja> zbiór) {
        final String gdzie = OBECNY_KATALOG + File.separator + NAZWA_KATALOGU + File.separator + NAZWA_PLIKU_STANU_REZERWACJI;
        new ZapiszStanRezerwacjiDoPlikuCSV(gdzie,zbiór).zapisz();
    }

    private void usuńDatyJużZarezerwowaneZeZbioruDoRezerwacji(Set<OdebranaRezerwacja> lista_rezerwacji) {
        for (OdebranaRezerwacja odebranaRezerwacja : lista_rezerwacji){
            datyDoZarezerwowania.remove(odebranaRezerwacja.getCZAS());
        }
    }

    private void pozostawTylkoZarezerwowaneDatyWzbiorzeRezerwacjiDoUsunięcia(Set<OdebranaRezerwacja> zbiórRezerwacjiZserwera){
        Set<LocalDateTime> tmpDatyRezerwacjiDoUsunięcia = new HashSet<>();
        for (OdebranaRezerwacja odebranaRezerwacja : zbiórRezerwacjiZserwera) {
//            porównywanie po  identity hash code nie dział dla objektów tej klasy, dlatego trzeba .equals
            for (LocalDateTime localDateTime : this.datyRezerwacjiDoUsunięcia) {
                if (localDateTime.equals(odebranaRezerwacja.getCZAS()))
                    tmpDatyRezerwacjiDoUsunięcia.add(localDateTime);
            }
        }
        this.datyRezerwacjiDoUsunięcia = tmpDatyRezerwacjiDoUsunięcia;
    }

    private void rezerwujZgodnieZPoleceniami(){
        for (LocalDateTime data_rezerwacji : datyDoZarezerwowania){
            DaneWykonywanegoZadaniaHTTP zadanieZarezerwowaniaGodziny = new ZadanieZarezerwowaniaGodziny(data_rezerwacji);
            boolean czyRezerwacjaSięPowiodła = new ValidatorOdpowiedziPoZadaniuRezerwacji(
                    zadanieZarezerwowaniaGodziny
                    ,sns7ds.wykonajZadanieNaKoncie(zadanieZarezerwowaniaGodziny)
            ).czyZadanieSięUdało();
            if (!czyRezerwacjaSięPowiodła)
                (new ZapiszWystapienieBleduDoLogu(
                        OBECNY_KATALOG + File.separator + NAZWA_KATALOGU + File.separator + NAZWA_PLIKU_LOGU,
                        ZapiszWystapienieBleduDoLogu.RodzajOperacjiNaKoncie.BOOK,
                        data_rezerwacji
                )).zapisz();
        }
    }

    private void usuwajZgodnieZpoleceniami(){
        for (LocalDateTime dataGodzinaRezerwacjiDoUsunięcia : datyRezerwacjiDoUsunięcia){
            DaneWykonywanegoZadaniaHTTP zadanieUsunieciaRezerwacji = new ZadaniaHTTPUsuwaniaRezerwacji(dataGodzinaRezerwacjiDoUsunięcia);
            boolean czyUsunięcieSięPowiodło = new ValidatorOdpowiedziPoZadaniuUsuwaniaRezerwacji(
                    zadanieUsunieciaRezerwacji
                    ,sns7ds.wykonajZadanieNaKoncie(zadanieUsunieciaRezerwacji)
            ).czyZadanieSięUdało();
            if (!czyUsunięcieSięPowiodło)
                (new ZapiszWystapienieBleduDoLogu(OBECNY_KATALOG + File.separator + NAZWA_KATALOGU + File.separator + NAZWA_PLIKU_LOGU,
                        ZapiszWystapienieBleduDoLogu.RodzajOperacjiNaKoncie.UNBOOK)).zapisz();
        }
    }


    private void zaaktualizujZbioryPoleceń(){
        ZwracającyListęPoleceń czytajPoleceniaZpliku = new UsuńPrzestarzałePolecenia(new ReadOrdersFromFile(OBECNY_KATALOG + "/" + NAZWA_KATALOGU + "/" + NAZWA_PLIKU_POLECEN));
        List<StrukturaParametruKonfigu> listaOpcji = czytajPoleceniaZpliku.zwróćPolecenia();
        (new ZapiszPolecenia(czytajPoleceniaZpliku,OBECNY_KATALOG + "/" + NAZWA_KATALOGU + "/" + NAZWA_PLIKU_POLECEN)).zapisz();
        Set<LocalDateTime> tmpDatyDoZarezerwowania,tmpDatyRezerwacjiDoUsunięcia;
        tmpDatyDoZarezerwowania = new HashSet<>();
        tmpDatyRezerwacjiDoUsunięcia = new HashSet<>();
        for (StrukturaParametruKonfigu spk : listaOpcji){
            if (spk.getRodzajPolecenia().equals(StrukturaParametruKonfigu.RodzajPolecenia.BOOK)){
                tmpDatyDoZarezerwowania.add(spk.getCzas());
            } else if (spk.getRodzajPolecenia().equals(StrukturaParametruKonfigu.RodzajPolecenia.UNBOOK)){
                tmpDatyRezerwacjiDoUsunięcia.add(spk.getCzas());
            }
        }
        this.datyDoZarezerwowania = tmpDatyDoZarezerwowania;
        this.datyRezerwacjiDoUsunięcia = tmpDatyRezerwacjiDoUsunięcia;

    }
}
