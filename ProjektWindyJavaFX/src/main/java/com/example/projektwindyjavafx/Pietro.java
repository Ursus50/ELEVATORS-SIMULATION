package com.example.projektwindyjavafx;


import javafx.scene.control.Label;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Semaphore;

public class Pietro {

    Deque<Pasazer> kolejka ;
    Deque<Pasazer> kolejka0 ;
    Deque<Pasazer> kolejka1 ;
    Deque<Pasazer> kolejka2 ;

    Label licznikLabel;
    Label licznikLabel0;
    Label licznikLabel1;
    Label licznikLabel2;

    Semaphore semafor;

    int numer;
    int licznik = 0;
    int licznik0 = 0;
    int licznik1 = 0;
    int licznik2 = 0;

    public Pietro(int numer)
    {
        this.numer = numer;

        if(numer != 0)                                  //tworzenie kolejki dla kazdego pietra procz 0
        {
            kolejka = new ArrayDeque<Pasazer>();

            licznikLabel = new Label();
            licznikLabel.setPrefWidth(20);
            licznikLabel.setPrefHeight(20);
        }
        else                                            //tworzenie 3 kolejek (po jednej na kazda wieze) dla pietra 0, kazda z kolejek jest obslugiwana przez inny typ wind
        {
            kolejka0 = new ArrayDeque<Pasazer>();
            kolejka1 = new ArrayDeque<Pasazer>();
            kolejka2 = new ArrayDeque<Pasazer>();

            licznikLabel0 = new Label();
            licznikLabel0.setPrefWidth(20);
            licznikLabel0.setPrefHeight(20);

            licznikLabel1 = new Label();
            licznikLabel1.setPrefWidth(20);
            licznikLabel1.setPrefHeight(20);

            licznikLabel2 = new Label();
            licznikLabel2.setPrefWidth(20);
            licznikLabel2.setPrefHeight(20);
        }
        semafor = new Semaphore(1);         //semafor odpowiedzialny za wylaczny dostep do kolejki przy dodawnaiu oraz przy pobieraniu pasazera z pietra
    }
}
