package com.company.bot7;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ZadaniaHTTPUsuwaniaRezerwacji implements DaneWykonywanegoZadaniaHTTP {
    private final LocalDateTime DATA_I_GODZINA;

    public ZadaniaHTTPUsuwaniaRezerwacji(LocalDateTime data_i_godzina) {
        this.DATA_I_GODZINA = data_i_godzina;
    }

    @Override
    public KindOfRequest getKindOfRequest() {
        return KindOfRequest.POST;
    }

    @Override
    public String getUri() {
        return "http://7ds.pl/app/schedule/cancelBooking";
    }

    @Override
    public Optional<NameValuePair[]> getParams() {
        List<NameValuePair> listaParamatrów = new ArrayList<>(4);
        final String numerWierszaRezerwacji = String.valueOf(DATA_I_GODZINA.getHour() - 8);
        listaParamatrów.add(new BasicNameValuePair("xhr","true"));
        listaParamatrów.add(new BasicNameValuePair("categoryId","silownia"));
//        format daty: 24-10-2017
        listaParamatrów.add(new BasicNameValuePair("date",DATA_I_GODZINA.format(DateTimeFormatter.ofPattern( "dd-MM-uuuu" ))));
        listaParamatrów.add(new BasicNameValuePair("showIdx",numerWierszaRezerwacji));
        BasicNameValuePair[] tablicaParNazwaWartosc = listaParamatrów.toArray(new BasicNameValuePair[listaParamatrów.size()]);
        Optional<NameValuePair[]> zwracanaWartość = Optional.of(tablicaParNazwaWartosc);
        return zwracanaWartość;
    }
}
