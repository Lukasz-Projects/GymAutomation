package com.company.bot7;


import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;

public class SesjaNaKoncie7DS implements Closeable {
    private CloseableHttpClient klientHttp;
    private CookieStore magazynCiastek;
    private final String login, password;

    public SesjaNaKoncie7DS(String browserUserAgent, String login, String password){
        magazynCiastek = new BasicCookieStore();
        klientHttp = HttpClients.custom()
                .setUserAgent(browserUserAgent)
                .setDefaultCookieStore(magazynCiastek)
                .build();
        this.login = login;
        this.password = password;
    }

    private boolean wyślijŻądanieLogowania(){
        boolean isLogged = false;
        HttpUriRequest requestOfLogging = RequestBuilder.post()
                .setCharset(Charset.forName("UTF-8"))
                .setHeader("Referer","http://7ds.pl/")
                .setHeader("X-Requested-With","XMLHttpRequest")
                .setUri("http://7ds.pl/portal/login")
                .addParameter("username",login)
                .addParameter("password",password)
                .build();

        //        w linijce poniżej wykonuję właściwe połączenie TCP i wewnątrz niego żądanie HTTP
        try(CloseableHttpResponse responseForLogin = klientHttp.execute(requestOfLogging)){
            String odpowiedź = new String(responseForLogin.getEntity().getContent().readAllBytes(),Charset.forName("UTF-8"));
            isLogged = responseForLogin.getStatusLine().getStatusCode() == 200;
            isLogged = isLogged && odpowiedź.equals("success");
            EntityUtils.consumeQuietly(responseForLogin.getEntity());
        } catch (IOException e) {
            System.err.println("Błąd w wyślijŻądanieLogowania() " + e.getMessage());
        }

        return isLogged;
    }

    private boolean zalogowany(){
        //        sprawdzam czy zalogowany. Jeśli tak pomijam pętlę.
//        jeśli nie to próbuję się zalogować (max dwa razy). Po drugim logowaniu, a po trzecim sprawdzeniu zalogowania
//        kończę metodę zwracając false.
        for (int i = 0; !isZalogowany(); ++i){
            if (i == 2) {
                System.err.println("Problem w zalogowany()");
                return false;
            }
            //                przed drugą próbą logowania poczekam 0.5 sekundy, może się coś poprawi...
            if (i == 1)
                try {Thread.sleep(500);} catch (InterruptedException e) { }
            wyślijŻądanieLogowania();
        }

        return true;
    }

    private void wyloguj(){
        HttpUriRequest requestForLogout = new HttpGet("http://7ds.pl/portal/logout");

        //        w linijce poniżej wykonuję właściwe połączenie TCP i wewnątrz niego żądanie HTTP
        try(CloseableHttpResponse responseForLogout = klientHttp.execute(requestForLogout)){
            EntityUtils.consumeQuietly(responseForLogout.getEntity());
            } catch (IOException e) {
            System.err.println("Błąd w wyloguj() " + e.getMessage());
        }

    }

    private boolean isZalogowany(){
        return ping();
    }

    private boolean ping(){
        boolean isOk = false;
        HttpUriRequest requestOfPing = new HttpPost("http://7ds.pl/portal/ping");

        //        w linijce poniżej wykonuję właściwe połączenie TCP i wewnątrz niego żądanie HTTP
        try(CloseableHttpResponse responseForPing = klientHttp.execute(requestOfPing)){
            isOk = responseForPing.getStatusLine().getStatusCode() == 200;
            isOk = isOk && new String(responseForPing.getEntity().getContent().readAllBytes(), Charset.forName("UTF-8")).equals("ok");
            EntityUtils.consumeQuietly(responseForPing.getEntity());
            } catch (IOException e) {
            System.err.println("Błąd w ping " + e.getMessage());
        }
        return isOk;
    }

    public ImportantDataFromResponse wykonajZadanieNaKoncie(DaneWykonywanegoZadaniaHTTP dane) {
        boolean zwracanaWartość = true;
        ImportantDataFromResponse najważniejszeDaneOdpowiedzi = zbudujZdanychZadanieIwykonaj(dane);
        if (najważniejszeDaneOdpowiedzi != null)
            return najważniejszeDaneOdpowiedzi;
        else
            throw new RuntimeException("Odpowiedź w wykonajZadanieNaKoncie() to null");
    }

    private ImportantDataFromResponse zbudujZdanychZadanieIwykonaj(DaneWykonywanegoZadaniaHTTP dane){
        if (!zalogowany())
            return new ImportantDataFromResponse(true,null,0);

        final RequestBuilder BUDOWANE_ZADANIE;
        switch (dane.getKindOfRequest()){
            case POST: BUDOWANE_ZADANIE = RequestBuilder.post(); break;
            case GET:  BUDOWANE_ZADANIE = RequestBuilder.get();  break;
            default:   BUDOWANE_ZADANIE = RequestBuilder.post(); break;
        }

        BUDOWANE_ZADANIE
                .setCharset(Charset.forName("UTF-8"))
                .setHeader("Referer","http://7ds.pl/")
                .setHeader("X-Requested-With","XMLHttpRequest")
                .setUri(dane.getUri());

        dane.getParams().ifPresent((NameValuePair[] nameValuePairs) -> {
            for (NameValuePair paraStringow : nameValuePairs)
                BUDOWANE_ZADANIE.addParameter(paraStringow);
        }  );

        HttpUriRequest zadanie = BUDOWANE_ZADANIE.build();

        String odpowiedź = null;
        int kodHttpOdpowiedzi = 0;
//        w linijce poniżej wykonuję właściwe połączenie TCP i wewnątrz niego żądanie HTTP
        try(CloseableHttpResponse odpowiedz = klientHttp.execute(zadanie)){
            odpowiedź = new String(odpowiedz.getEntity().getContent().readAllBytes(),Charset.forName("UTF-8"));
            kodHttpOdpowiedzi = odpowiedz.getStatusLine().getStatusCode();
            EntityUtils.consumeQuietly(odpowiedz.getEntity());
            return new ImportantDataFromResponse(false,odpowiedź,kodHttpOdpowiedzi);
        } catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            wyloguj();
        } finally {
            try {
                klientHttp.close();
            } catch (IOException e) {
                System.err.println("Błąd zamykania klientHttp.close() w SesjaNaKoncie7DS");
            }
        }
    }
}
