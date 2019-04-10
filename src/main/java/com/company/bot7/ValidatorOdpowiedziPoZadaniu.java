package com.company.bot7;

import org.w3c.dom.Document;

import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

public abstract class ValidatorOdpowiedziPoZadaniu {
    protected final ImportantDataFromResponse daneOdpowiedzi;
    protected final DaneWykonywanegoZadaniaHTTP daneZadania;

    protected final class ZwracanyDokumentIstringReader implements Closeable {
        private Document document;
        private StringReader stringReader;

        protected ZwracanyDokumentIstringReader(Document document, StringReader stringReader) {
            this.document = document;
            this.stringReader = stringReader;
        }

        public Document getDocument() {
            return document;
        }

        public StringReader getStringReader() {
            return stringReader;
        }

        @Override
        public void close() throws IOException {
            stringReader.close();
        }
    }

    public ValidatorOdpowiedziPoZadaniu(DaneWykonywanegoZadaniaHTTP daneZadania, ImportantDataFromResponse daneOdpowiedzi){
        this.daneZadania = daneZadania;
        this.daneOdpowiedzi = daneOdpowiedzi;
    }

    public abstract boolean czyOdpowiedźMaPoprawnyStatus();

    public abstract boolean czyZadanieSięUdało();

    public abstract Optional<ImportantDataFromResponse> poprawnaOdpowiedź();
}
