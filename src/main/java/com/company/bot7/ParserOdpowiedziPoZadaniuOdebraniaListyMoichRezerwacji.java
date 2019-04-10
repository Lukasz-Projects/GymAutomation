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
import java.util.*;

public class ParserOdpowiedziPoZadaniuOdebraniaListyMoichRezerwacji {
    private final String DANE_W_XML;

    public ParserOdpowiedziPoZadaniuOdebraniaListyMoichRezerwacji(ValidatorOdpowiedziPoZadaniu validatorZdanymi){
        if (validatorZdanymi == null || validatorZdanymi.poprawnaOdpowiedź() == null)
            throw new RuntimeException("null lub brak danych w konstruktorze ParserOdpowiedziPoZadaniuOdebraniaListyMoichRezerwacji()");

        final String[] tmpDANE_W_XML = {null};
        validatorZdanymi.poprawnaOdpowiedź().ifPresent(importantDataFromResponse -> {
            tmpDANE_W_XML[0] = importantDataFromResponse.getRESPONSE();});
        this.DANE_W_XML = tmpDANE_W_XML[0];
    }

    public Set<OdebranaRezerwacja> getZBIOR_REZERWACJI_SILOWNI() {
        final Set<OdebranaRezerwacja> zbiórRezerwacjiSilowni = new HashSet<>();

        if (this.DANE_W_XML == null || DANE_W_XML.length() == 0) {
            throw new RuntimeException("null lub pusty string w getZBIOR_REZERWACJI_SILOWNI()");
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        StringReader stringReader = new StringReader(DANE_W_XML);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(stringReader);
            Document document = builder.parse(inputSource);
//            lista item-ów
            NodeList nodeList = document.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodeList.getLength(); ++i){
//                item nr i
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE){
//                    element to któryś z <item> w xml-u odebranego z serwera
                    Element element = (Element) node;
                    NodeList listaWezlowZeZnacznikiemCategoryId = element.getElementsByTagName("category_id");
//                    String salka = listaWezlowZeZnacznikiemCategoryId.item(0).getNodeValue();
                    String salka = listaWezlowZeZnacznikiemCategoryId.item(0).getTextContent();
                    if (!salka.contains("silownia"))
                        continue;
                    int numer_rezerwacji = Integer.valueOf(element.getElementsByTagName("booking_id").item(0).getTextContent());
                    final String dzień = element.getElementsByTagName("show_date").item(0).getTextContent();
                    final int numer_wiersza = Integer.valueOf(element.getElementsByTagName("show_idx").item(0).getTextContent()) + 8;
                    final String dzieńIgodzina = dzień + " " + numer_wiersza;
                    LocalDateTime data = LocalDateTime.parse(
                            dzieńIgodzina,
                            DateTimeFormatter.ofPattern( "dd-MM-uuuu HH")
                    );
                    zbiórRezerwacjiSilowni.add (new OdebranaRezerwacja(numer_rezerwacji,salka,data));
                }
            }
        } catch (ParserConfigurationException e) {
            System.err.println("Problem z konfiguracją parsera w ParserOdpowiedziPoZadaniuOdebraniaListyMoichRezerwacji()");
        } catch (SAXException e) {
            System.err.println("Problem z SAXException w ParserOdpowiedziPoZadaniuOdebraniaListyMoichRezerwacji()");
        } catch (IOException e) {
        } finally {
            stringReader.close();
        }

        return Collections.unmodifiableSet(zbiórRezerwacjiSilowni);
    }
}
