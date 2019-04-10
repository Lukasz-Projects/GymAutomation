package com.company.bot7;

import org.apache.http.NameValuePair;

import java.util.Optional;

public interface DaneWykonywanegoZadaniaHTTP {
    enum KindOfRequest {
        GET,
        POST
    };

    KindOfRequest getKindOfRequest();

    String getUri();

    Optional<NameValuePair[]> getParams();

}
