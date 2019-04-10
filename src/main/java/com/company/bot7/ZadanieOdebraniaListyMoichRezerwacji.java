package com.company.bot7;


import org.apache.http.NameValuePair;

import java.util.Optional;

public class ZadanieOdebraniaListyMoichRezerwacji implements DaneWykonywanegoZadaniaHTTP {
    @Override
    public KindOfRequest getKindOfRequest() {
        return KindOfRequest.POST;
    }

    @Override
    public String getUri() {
        return "http://7ds.pl/app/portlets/getMyBookings";
    }

    @Override
    public Optional<NameValuePair[]> getParams() {
        return Optional.empty();
    }

}
