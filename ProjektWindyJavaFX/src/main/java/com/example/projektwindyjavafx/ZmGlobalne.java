package com.example.projektwindyjavafx;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ZmGlobalne {

    int lpieter = 0;
    int lpieterPod = 0;
    int losob = 0;
    int pojemnoscWindy = 0;
    File plik;
    String pom;
    volatile int obsluzeniPasazerowie = 0;
    volatile int pasazerowieDoObslugi;
    int pod_0 = 500;
    static int stanWindy0 = 1;  //czy dane rodzaje wind nadal pracuja
    static int stanWindy1 = 1;
    static int stanWindy2 = 1;
    static int skonczoneWindy = 3;


//    boolean czyPlik()       //sprawdzenie czy instnieje plik wejsciowy
//    {
//        pom = new File("").getAbsolutePath()+"\\src\\main\\java\\com\\example\\projektwindyjavafx\\PlikiTekstowe\\PlikWejsciowy.txt";
//        //pom = "C:\\JAVA\\ProjektWindyJavaFX\\src\\main\\java\\com\\example\\projektwindyjavafx\\PlikiTekstowe\\PlikWejsciowy.txt";
//        plik = new File(pom);
//        if(plik.exists() && plik.isFile())
//        {
//            System.out.println("Znaleziono plik wejscciowy");
//            return pobranieDanych();
//        }
//        else
//            System.out.println("Brak pliku12");
//        return false;
//    }

    void pobranieDanych()       //pobranie danych z pliku wejsciowego        // lpieter \n lpieterPod \n losob \n pojemnoscWindy
    {
        pom = new File("").getAbsolutePath()+"\\src\\main\\java\\com\\example\\projektwindyjavafx\\PlikiTekstowe\\PlikWejsciowy.txt";
        //pom = "C:\\JAVA\\ProjektWindyJavaFX\\src\\main\\java\\com\\example\\projektwindyjavafx\\PlikiTekstowe\\PlikWejsciowy.txt";
        plik = new File(pom);
        try{
            Scanner odczyt = new Scanner(plik);
            lpieter = odczyt.nextInt();
            lpieterPod = odczyt.nextInt();
            losob = odczyt.nextInt();
            pojemnoscWindy = odczyt.nextInt();

        }catch(FileNotFoundException e) {System.out.println("Nie udało sie odczytac pliku21"); }

        System.out.println("lPieter: "+lpieter+" lPieterPod: " +lpieterPod+" losob: "+losob+" pojemnoscWindy: "+pojemnoscWindy );

    }

    public ZmGlobalne(int lpieter, int lpieterPod, int pojemnoscWindy, int losob)
    {
            this.lpieter = lpieter;
            this.lpieterPod = lpieterPod;
            this.losob = losob;
            this.pojemnoscWindy = pojemnoscWindy;
    }

    public synchronized void zwiekszObsluzonychPasazerow()  //zwiekszenie licznika obsluzonych paszerow
    {
        ++obsluzeniPasazerowie;
    }

}
