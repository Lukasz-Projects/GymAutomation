package com.company.bot7;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class ValidatorOdpowiedziPoZadaniuOdebraniaListyMoichRezerwacji extends ValidatorOdpowiedziPoZadaniu {
    public ValidatorOdpowiedziPoZadaniuOdebraniaListyMoichRezerwacji(DaneWykonywanegoZadaniaHTTP daneZadania, ImportantDataFromResponse daneOdpowiedzi) {
        super(daneZadania, daneOdpowiedzi);
    }

    @Override
    public boolean czyOdpowiedźMaPoprawnyStatus() {
        if (this.daneZadania == null || this.daneOdpowiedzi == null || this.daneOdpowiedzi.getRESPONSE() == null)
            return false;

        try(ZwracanyDokumentIstringReader zwracanyDokumentIstringReader = twórzParserDokumentu()) {
            if (zwracanyDokumentIstringReader.getDocument() == null)
                return false;
            return sprawdzanieStatusu(zwracanyDokumentIstringReader.getDocument());
        } catch (IOException e) {
            System.err.println("Błąd w ValidatorOdpowiedziPoZadaniuRezerwacji::czyOdpowiedźMaPoprawnyStatus()");
            return false;
        }
    }

    @Override
    public boolean czyZadanieSięUdało() {
        if (this.daneZadania == null || this.daneOdpowiedzi == null || this.daneOdpowiedzi.getRESPONSE() == null)
            return false;

        try(ZwracanyDokumentIstringReader zwracanyDokumentIstringReader = twórzParserDokumentu()) {
            if (zwracanyDokumentIstringReader.getDocument() == null)
                return false;
            return sprawdzanieListyRezerwacji(zwracanyDokumentIstringReader.getDocument());
        } catch (IOException e) {
            System.err.println("Błąd w ValidatorOdpowiedziPoZadaniuRezerwacji::czyZadanieSięUdało()");
            return false;
        }
    }

    @Override
    public Optional<ImportantDataFromResponse> poprawnaOdpowiedź() {
        if (czyZadanieSięUdało())
            return Optional.of(this.daneOdpowiedzi);
        else
            return Optional.empty();
    }

    private ZwracanyDokumentIstringReader twórzParserDokumentu() {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final StringReader stringReader = new StringReader(this.daneOdpowiedzi.getRESPONSE());
        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final InputSource inputSource = new InputSource(stringReader);
            ZwracanyDokumentIstringReader zwracanyObjekt = new ZwracanyDokumentIstringReader(builder.parse(inputSource),stringReader);
            return zwracanyObjekt;
        } catch (ParserConfigurationException e) {
            return new ZwracanyDokumentIstringReader(null,stringReader);
        } catch (IOException e) {
            return new ZwracanyDokumentIstringReader(null,stringReader);
        } catch (SAXException e) {
            return new ZwracanyDokumentIstringReader(null,stringReader);
        }
    }

    private boolean sprawdzanieStatusu(Document document) {
        if (this.daneOdpowiedzi.getSTATUSCODE() == 200){
            //            lista <item>-ów (raczej będzie tylko jeden)
            if(document.getDocumentElement().getChildNodes() != null)
                return true;
        }
     return false;
    }

    private boolean sprawdzanieListyRezerwacji(Document document) {
        if (this.daneOdpowiedzi.getSTATUSCODE() == 200){
            //            lista <item>-ów (raczej będzie tylko jeden)
            NodeList nodeList = document.getDocumentElement().getChildNodes();
            if(nodeList != null){
                for (int i = 0; i < nodeList.getLength(); ++i) {
                    Node node = nodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;

                        final NodeList listaWezlowStatus = element.getElementsByTagName("booking_id");
                        if (listaWezlowStatus == null)
                            return false;
                        final Node wezelID = listaWezlowStatus.item(0);
                        String regexLiczba = "\\d+";
                        if (!wezelID.getTextContent().matches(regexLiczba))
                            return false;

                        final NodeList listaWezlowKategorii = element.getElementsByTagName("category_id");
                        if (listaWezlowKategorii == null)
                            return false;
                        final Node wezelKategorii = listaWezlowKategorii.item(0);
                        if (wezelKategorii == null)
                            return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
