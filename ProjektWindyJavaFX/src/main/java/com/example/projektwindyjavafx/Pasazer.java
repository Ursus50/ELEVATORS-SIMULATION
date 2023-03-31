package com.example.projektwindyjavafx;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import java.util.Random;

public class Pasazer {

    int stan;                   // 0- oczekuje na winde, 1-w windzie, 2-dotarl na pietro
    int pietroPocz;
    int pietroKon;
    int pom;
    int dol = 0, gora = 1;
    int nrPasazera;
    boolean czyPrzesiadka;
    int lpieter;
    boolean nowy;

    Circle kolo;
    Text text;
    StackPane reprezentant;
    int indexWwindzie;

    int losuj(int zakresDol, int zakresGora)                //losowanie pietra
    {
        Random generator = new Random();
        return generator.nextInt(zakresGora + zakresDol) - zakresDol;
    }

    public Pasazer(int dol, int gora, int nrPasazera)       //tworzenie pasarzera z losowaniem pieter
    {
        this.dol = dol;
        this.gora = gora;
        this.nrPasazera = nrPasazera;
        this.lpieter = this.gora;

        pietroPocz = losuj(dol, gora);      //z jakiego pietra pasazer startuje

        pom = losuj(dol, gora);             //na jakie pietro chce sie dostac pasazer
        while (pom == pietroPocz)
            pom = losuj(dol, gora);
        pietroKon = pom;

        if(pietroPocz > 0)                  //czy paszer potrzebuje sie przesiadac aby dotrzec na ustalone pietro
        {
            if(((pietroPocz <= this.lpieter/2 && pietroKon > this.lpieter/2) || (pietroPocz > this.lpieter/2 && pietroKon <= this.lpieter/2)) && pietroKon !=0)
            {
                czyPrzesiadka = true;
            }
            else if(pietroKon < 0)
                czyPrzesiadka = true;
            else
                czyPrzesiadka = false;
        }
        else if(pietroPocz < 0)
        {
            if(pietroKon > 0)
                czyPrzesiadka = true;
            else
                czyPrzesiadka = false;
        }
        else
            czyPrzesiadka = false;

        kolo = new Circle();            //reprezentacja graficzna paszera
        kolo.setRadius(10);
        kolo.setFill((Color.GREY));

        text = new Text();
        text.setText(""+nrPasazera);

        reprezentant = new StackPane();
        reprezentant.getChildren().addAll(kolo,text);

        System.out.println(pietroPocz);
        System.out.println(pietroKon);
        System.out.println(czyPrzesiadka);
    }

    public Pasazer(int dol, int gora, int nrPasazera, boolean nowy)           //tworzenie nowego paszera, po wcisnieciu przycisku Nowy paszer z losowym pietrwm poczatkowym oraz pietrem koncowym
    {
        this.dol = dol;
        this.gora = gora;
        this.nrPasazera = nrPasazera;
        this.lpieter = this.gora;
        this.nowy = nowy;

        pietroPocz = losuj(dol, gora);      //z jakiego pietra pasazer startuje

        pom = losuj(dol, gora);             //na jakie pietro chce sie dostac pasazer
        while (pom == pietroPocz)
            pom = losuj(dol, gora);
        pietroKon = pom;

        if(pietroPocz > 0)                  //czy paszer potrzebuje sie przesiadac aby dotrzec na ustalone pietro
        {
            if(((pietroPocz <= this.lpieter/2 && pietroKon > this.lpieter/2) || (pietroPocz > this.lpieter/2 && pietroKon <= this.lpieter/2)) && pietroKon !=0)
            {
                czyPrzesiadka = true;
            }
            else if(pietroKon < 0)
                czyPrzesiadka = true;
            else
                czyPrzesiadka = false;
        }
        else if(pietroPocz < 0)
        {
            if(pietroKon > 0)
                czyPrzesiadka = true;
            else
                czyPrzesiadka = false;
        }
        else
            czyPrzesiadka = false;

        kolo = new Circle();
        kolo.setRadius(10);
        kolo.setFill((Color.GREEN));

        text = new Text();
        text.setText(""+nrPasazera);

        reprezentant = new StackPane();
        reprezentant.getChildren().addAll(kolo,text);

        System.out.println(pietroPocz);
        System.out.println(pietroKon);
        System.out.println(czyPrzesiadka);
    }


    public Pasazer(int pocz, int kon, int nrPasazera, int lpieter)                              //tworzenie pasazera z wczytywanymi pietrami
    {
        stan = 0;
        pietroPocz = pocz;
        pietroKon = kon;
        this.nrPasazera = nrPasazera;
        this.lpieter = lpieter;

        if(pietroPocz > 0)                              //czy paszer potrzebuje sie przesiadac aby dotrzec na ustalone pietro
        {
            if(((pietroPocz <= this.lpieter/2 && pietroKon > this.lpieter/2) || (pietroPocz > this.lpieter/2 && pietroKon <= this.lpieter/2)) && pietroKon !=0)
            {
                czyPrzesiadka = true;
            }
            else if(pietroKon < 0)
                czyPrzesiadka = true;
            else
                czyPrzesiadka = false;
        }
        else if(pietroPocz < 0)
        {
            if(pietroKon > 0)
                czyPrzesiadka = true;
            else
                czyPrzesiadka = false;
        }
        else
            czyPrzesiadka = false;

        kolo = new Circle();                //reprezentacja graficzna paszera
        kolo.setRadius(10);
        kolo.setFill((Color.GREY));

        text = new Text();
        text.setText(""+nrPasazera);

        reprezentant = new StackPane();
        reprezentant.getChildren().addAll(kolo,text);

        System.out.println(pietroPocz);
        System.out.println(pietroKon);
        System.out.println(czyPrzesiadka);

    }

}
