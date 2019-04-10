package com.company.bot7;

import org.apache.http.NameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

public final class ValidatorOdpowiedziPoZadaniuRezerwacji extends ValidatorOdpowiedziPoZadaniu {
    public ValidatorOdpowiedziPoZadaniuRezerwacji(DaneWykonywanegoZadaniaHTTP daneZadania, ImportantDataFromResponse daneOdpowiedzi) {
        super(daneZadania, daneOdpowiedzi);
    }

    @Override
    public boolean czyOdpowiedźMaPoprawnyStatus() {
        if (this.daneOdpowiedzi.getSTATUSCODE() != 200)
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
        try(ZwracanyDokumentIstringReader zwracanyDokumentIstringReader = twórzParserDokumentu()) {
            if (zwracanyDokumentIstringReader.getDocument() == null)
                return false;
            return zadanaRezerwacjaZgadzaSieZrezerwacjaWodpowiedzi(zwracanyDokumentIstringReader.getDocument());
        } catch (IOException e) {
            System.err.println("Błąd w ValidatorOdpowiedziPoZadaniuRezerwacji::czyZadanieSięUdało");
            return false;
        }
    }

    public Optional<ImportantDataFromResponse> poprawnaOdpowiedź(){
        if (czyZadanieSięUdało())
            return Optional.of(this.daneOdpowiedzi);
        else
            return Optional.empty();
    }

    private ZwracanyDokumentIstringReader twórzParserDokumentu(){
        if (this.daneOdpowiedzi == null || this.daneOdpowiedzi.getRESPONSE() == null)
            return null;

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


// true means success
    private boolean sprawdzanieStatusu(Document document) {
        if (document == null)
            return false;
        if (this.daneOdpowiedzi.getSTATUSCODE() != 200)
            return false;

        //            lista <params>-ów (raczej będzie tylko jeden)
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        if (nodeList == null)
            return false;

        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            if (node == null)
                return false;

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                NodeList zbiorWezlowZTagiem = element.getElementsByTagName("status");
                if (zbiorWezlowZTagiem == null)
                    return false;
                Node wezelStatus = zbiorWezlowZTagiem.item(0);
                if (wezelStatus.getTextContent().equals("success"))
                    return true;
            }
        }
        return false;
    }

    private boolean zadanaRezerwacjaZgadzaSieZrezerwacjaWodpowiedzi(Document document){
        if (document == null)
            throw new RuntimeException("null jako argument w ValidatorOdpowiedziPoZadaniuRezerwacji::zadanaRezerwacjaZgadzaSieZrezerwacjaWodpowiedzi()");

        if (this.daneOdpowiedzi.getSTATUSCODE() != 200)
            return false;

        //            lista <params>-ów (raczej będzie tylko jeden)
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                final Node wezelStatus = element.getElementsByTagName("status").item(0);
                if (!wezelStatus.getTextContent().equals("success")) {
                    return false;
                }

                final Node wezelZdata = element.getElementsByTagName("showDate").item(0);
                final boolean czyDatySieZgadzaja = this.daneZadania.getParams().filter(nameValuePairs -> {
                    for (NameValuePair nameValuePair : nameValuePairs)
                        if (nameValuePair.getName().equals("date"))
                            if (nameValuePair.getValue().equals(wezelZdata.getTextContent()))
                                return true;
                    return false;
                }).isPresent();
                if (!czyDatySieZgadzaja) {
                    return false;
                }

                final Node wezelZgodzina = element.getElementsByTagName("showHour").item(0);
                final boolean czyGodzinySieZgadzaja = this.daneZadania.getParams().filter(nameValuePairs -> {
                    for (NameValuePair nameValuePair : nameValuePairs)
                        if (nameValuePair.getName().equals("showIdx")) {
                            final Integer godzina = Integer.valueOf(nameValuePair.getValue()) + 8;
                            if (godzina.toString().equals(wezelZgodzina.getTextContent()))
                                return true;
                        }
                    return false;
                }).isPresent();
                if (!czyGodzinySieZgadzaja) {
                    return false;
                }

            }
        }
        return true;
    }
}
