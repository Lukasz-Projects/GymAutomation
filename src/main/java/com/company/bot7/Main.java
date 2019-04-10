package com.company.bot7;

// adres IPv4 domeny 7ds.pl to 212.191.78.177, przynajmiej wewnątrz sieci siódemki

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 1; ++i){
            try {
                Thread.sleep(9 * 60 * 1000);
            } catch (InterruptedException e) {
                i=-1;
            }
        }

        try(BotDoSiłowni bds = new BotDoSiłowni()){
            bds.uruchom();
        }

        final String komendaWylaczenia = "shutdown /s /t 30";
        Runtime.getRuntime().exec(komendaWylaczenia);
    }
}
