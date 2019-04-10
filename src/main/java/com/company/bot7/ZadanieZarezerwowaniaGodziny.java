package com.company.bot7;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ZadanieZarezerwowaniaGodziny implements DaneWykonywanegoZadaniaHTTP {
    private final LocalDateTime DATA_I_GODZINA;

    public ZadanieZarezerwowaniaGodziny(final LocalDateTime DATA_I_GODZINA){
        this.DATA_I_GODZINA = DATA_I_GODZINA;
    }

    @Override
    public KindOfRequest getKindOfRequest() {
        return KindOfRequest.POST;
    }

    @Override
    public String getUri() {
        return "http://7ds.pl/app/schedule/book";
    }

    @Override
    public Optional<NameValuePair[]> getParams() {
        List<NameValuePair> listaParamatrów = new ArrayList<>(3);
        final String numerWierszaRezerwacji = String.valueOf(DATA_I_GODZINA.getHour() - 8);
        listaParamatrów.add(new BasicNameValuePair("categoryId","silownia"));
//        format daty: 24-10-2017
        listaParamatrów.add(new BasicNameValuePair("date",DATA_I_GODZINA.format(DateTimeFormatter.ofPattern( "dd-MM-uuuu" ))));
        listaParamatrów.add(new BasicNameValuePair("showIdx",numerWierszaRezerwacji));
        BasicNameValuePair[] tablicaParNazwaWartosc = listaParamatrów.toArray(new BasicNameValuePair[listaParamatrów.size()]);
        Optional<NameValuePair[]> zwracanaWartość = Optional.of(tablicaParNazwaWartosc);
        return zwracanaWartość;
    }

}
