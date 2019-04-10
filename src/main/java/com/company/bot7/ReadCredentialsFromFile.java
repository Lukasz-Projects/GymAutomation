package com.company.bot7;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class ReadCredentialsFromFile {
    private final String sciezka;

    public ReadCredentialsFromFile(String ścieżka){
        this.sciezka = ścieżka;
    }

    public Credentials getDANE_LOGOWANIA() {
        final File PLIK = new File(sciezka);
        String login = null;
        String haslo = null;
        if (!PLIK.exists() || PLIK.isDirectory())
            throw new RuntimeException("Nie ma pliku z danymi logowania.");

        try (BufferedReader czytnik = new BufferedReader(new FileReader(PLIK));
        ){
            String linia;
            while ((linia = czytnik.readLine()) != null){
                Scanner skaner = new Scanner(linia);
                skaner.useDelimiter("=");
                String lewaStrona = skaner.next().trim();
                String prawaStrona = skaner.next().trim();
                if (lewaStrona.equals("login"))
                    login = prawaStrona;
                else if (lewaStrona.equals("password"))
                    haslo = prawaStrona;
            }

        } catch (IOException e) {
            throw new RuntimeException("Błąd typu IOException e w getDANE_LOGOWANIA()");
        }

        if (login == null || haslo == null)
            throw new RuntimeException("brakuje loginu lub hasła w getDANE_LOGOWANIA(). Dopisz do pliku");

        return new Credentials(login,haslo);
    }
}
