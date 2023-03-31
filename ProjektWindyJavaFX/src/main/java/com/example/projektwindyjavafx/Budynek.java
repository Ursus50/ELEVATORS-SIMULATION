package com.example.projektwindyjavafx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Budynek extends Thread{

    int lPieter;
    int lpieterPod;
    Pietro pietra[];
    ZmGlobalne zmGlobalne;
    int pomi = 0;

    public Budynek(int lPieter, int lpieterPod, ZmGlobalne zmGlobalne)  //konstruktor
    {
        this.zmGlobalne = zmGlobalne;
        this.lPieter = lPieter;
        this.lpieterPod = lpieterPod;
        pietra = new Pietro[lPieter + lpieterPod];  //stworzenie tablicy zawierajacej wszyskie pietra w budynku (garaz, pietra niskie oraz pietra wysokie)
        budowa();
    }

    void budowa()               //wypelnienie tablicy przechowujacych pietra pietrami
    {
        int pom = -lpieterPod;
        int i;

        for(i = 0 ; i<lPieter + lpieterPod ; i++)
        {
            pietra[i] = new Pietro(pom++);
        }

        for(Pietro e : pietra)
            System.out.println(e.numer);
    }

    public void poczatkowaPozPasazera(Pasazer pas)      //ustawinie nowoutworzonego paszera na odpowiednia pozycje na scenie (odpowiednie pietro w oczekiwaniu na przyjazd windy)
    {
        int heightRow = 40;
        double posX = 0;
        double posY;

        if(pas.pietroPocz == 0 && pas.pietroKon <= zmGlobalne.lpieter/2 && pas.pietroKon > 0)   //do windy 1
            posX = 615;
        else if(pas.pietroPocz == 0 && pas.pietroKon < 0)       //do windy 0
            posX = 965;
        else if(pas.pietroPocz == 0 && pas.pietroKon > zmGlobalne.lpieter/2)    //do windy 2
            posX = 1315;
        else if(pas.pietroPocz <= zmGlobalne.lpieter/2 && pas.pietroPocz > 0)   //do windy 1
            posX = 615;
        else if(pas.pietroPocz < 0)     //do windy 0
            posX = 965;
        else if(pas.pietroPocz > zmGlobalne.lpieter/2)      //do windy 2
            posX = 1315;

        if(pas.pietroPocz == 0)                         //ustawienie pasazera na odpowieniedniej wysokosci
            posY = zmGlobalne.pod_0 - heightRow + 10;
        else
        {
            posY = zmGlobalne.pod_0 - (pas.pietroPocz + 1) * heightRow + 10; //- 0.5 * heightRow;
        }

        pas.reprezentant.setLayoutX(posX);
        pas.reprezentant.setLayoutY(posY);

        Platform.runLater(()->{
            HelloApplication.root.getChildren().add(pas.reprezentant);
        });
    }

    public void tworzeniePasazerow1()           //tworzenie pasazerow z pliku DanePasazerow zawierajacego inforamcji o pietrze poczatkowym oraz pietrze koncowym kazdego pasazera
    {                                           //plik ten musi zawierac dane mieszczace sie w przedialach wczesniej wprowadzonych inforamcji o budynku (wysokosc budynku i pietra podziemnie)
        System.out.println("Pasazerowie:");     //danych pasazerow nie moize byc mniej niz wprowadzonej ilosci pasazerow poczatkowych
        Pasazer pas;
        File plik;
        String pomTxt;
        try{
            pomTxt = new File("").getAbsolutePath()+"\\src\\main\\java\\com\\example\\projektwindyjavafx\\PlikiTekstowe\\DanePasazerow.txt";
            //pomTxt = "C:\\JAVA\\ProjektWindyJavaFX\\src\\main\\java\\com\\example\\projektwindyjavafx\\PlikiTekstowe";
            plik = new File(pomTxt);
            Scanner odczyt = new Scanner(plik);
            System.out.println("Znaleziono plik wejscciowy");

            for(int i = 0; i<zmGlobalne.losob; i++ )
            {

                if(plik.exists() && plik.isFile())      //ponowne sprawdzenie czy plik istnieje
                {
                    pas = new Pasazer(odczyt.nextInt(), odczyt.nextInt(), i, zmGlobalne.lpieter);   //jezeli tak, to tworzy pasazerow z pliku
                }
                else
                {
                    pas = new Pasazer(zmGlobalne.lpieterPod, zmGlobalne.lpieter, i);                //jezeli nie, to tworzy losowych pasazerow
                    System.out.println("Nie udalo sie odczytac pliku12");
                }

                if(pas.pietroPocz == 0 && pas.pietroKon <= lPieter/2 && pas.pietroKon > 0)          //rozdzielenie paszerow do odpowieniej kolejki jezeli pietro poczatkowe wynosi 0
                {
                    pietra[lpieterPod].kolejka1.add(pas);
                    poczatkowaPozPasazera(pas);
                    pietra[lpieterPod].licznik = pietra[lpieterPod].kolejka1.size();
                }
                else if(pas.pietroPocz == 0 && pas.pietroKon > lPieter/2 && pas.pietroKon > 0)
                {
                    pietra[lpieterPod].kolejka2.add(pas);
                    poczatkowaPozPasazera(pas);
                    pietra[lpieterPod].licznik = pietra[lpieterPod].kolejka2.size();
                }
                else if(pas.pietroPocz == 0 && pas.pietroKon < 0)
                {
                    pietra[lpieterPod].kolejka0.add(pas);
                    poczatkowaPozPasazera(pas);
                    pietra[lpieterPod].licznik = pietra[lpieterPod].kolejka0.size();
                }
                else                                                                                //przydzielenie pasazera do kolejki, jezeli nie jest na pietrze 0
                {
                    pietra[pas.pietroPocz + lpieterPod].kolejka.add(pas);
                    poczatkowaPozPasazera(pas);
                    pietra[pas.pietroPocz + lpieterPod].licznik = pietra[pas.pietroPocz + lpieterPod].kolejka.size();
                }


                ++zmGlobalne.pasazerowieDoObslugi;                  //ilu pasazerow trzeba obsluzyc

                System.out.println("Pasazer nr: "+ pas.nrPasazera + " zostal utworzony");
                pomi = i;
               // ++zmGlobalne.pasazerowieDoObslugi;                  //ilu pasazerow trzeba obsluzyc
            }

        }catch(FileNotFoundException e) {System.out.println("Nie udało sie odczytac pliku");}
    }


    public void tworzeniePasazerow()            //tworzenie losowych paszaerow
    {
        System.out.println("Pasazerowie:");
        Pasazer pas;

            for(int i = 0; i<zmGlobalne.losob; i++ )
            {
                pas = new Pasazer(zmGlobalne.lpieterPod, zmGlobalne.lpieter, i);

                if(pas.pietroPocz == 0 && pas.pietroKon <= lPieter/2 && pas.pietroKon > 0)          //rozdzielenie paszerow do odpowieniej kolejki, jezeli pietro poczatkowe wynosi 0
                {
                    pietra[lpieterPod].kolejka1.add(pas);
                    poczatkowaPozPasazera(pas);
                    pietra[lpieterPod].licznik = pietra[lpieterPod].kolejka1.size();
                }
                else if(pas.pietroPocz == 0 && pas.pietroKon > lPieter/2 && pas.pietroKon > 0)
                {
                    pietra[lpieterPod].kolejka2.add(pas);
                    poczatkowaPozPasazera(pas);
                    pietra[lpieterPod].licznik = pietra[lpieterPod].kolejka2.size();
                }
                else if(pas.pietroPocz == 0 && pas.pietroKon < 0)
                {
                    pietra[lpieterPod].kolejka0.add(pas);
                    poczatkowaPozPasazera(pas);
                    pietra[lpieterPod].licznik = pietra[lpieterPod].kolejka0.size();
                }
                else                                                                                //przydzielenie pasazera do kolejki jezeli pietro poczatkowe jest rozne od 0
                {
                    pietra[pas.pietroPocz + lpieterPod].kolejka.add(pas);
                    poczatkowaPozPasazera(pas);
                    pietra[pas.pietroPocz + lpieterPod].licznik = pietra[pas.pietroPocz + lpieterPod].kolejka.size();
                    
                }

                ++zmGlobalne.pasazerowieDoObslugi;                  //ilu pasazerow trzeba obsluzyc

                System.out.println("Pasazer nr: "+ pas.nrPasazera + " zostal utworzony");
                pomi = i;
            }
            pomi -=1;

    }

    public void tworzenieNowychPasazerow()                      //tworzenie nowych pasazerow z przysku
    {
        System.out.println("Nowy:");
        Pasazer pas;

        pas = new Pasazer(zmGlobalne.lpieterPod, zmGlobalne.lpieter, pomi++, true); //utworzenie nowego paszera

        try{
            pietra[pas.pietroPocz + lpieterPod].semafor.acquire();      //opuszczenie semafora by miec indywidualny dostep do wybranego pietra (pietro poczatkowe pasazera)
        }catch (InterruptedException e ){e.printStackTrace();}

        if (pas.pietroPocz == 0 && pas.pietroKon <= lPieter / 2 && pas.pietroKon > 0)          //rozdzielenie paszerow do odpowieniej kolejki
        {
            pietra[lpieterPod].kolejka1.add(pas);
            poczatkowaPozPasazera(pas);
            pietra[lpieterPod].licznik1++;

          //  pietra[pas.pietroPocz + lpieterPod].semafor.release();

            animacjaLicznik(pietra[pas.pietroPocz + lpieterPod].licznikLabel1, pietra[pas.pietroPocz + lpieterPod].licznik1);
        } else if (pas.pietroPocz == 0 && pas.pietroKon > lPieter / 2 && pas.pietroKon > 0) {
            pietra[lpieterPod].kolejka2.add(pas);
            poczatkowaPozPasazera(pas);
            pietra[lpieterPod].licznik2++;

         //   pietra[pas.pietroPocz + lpieterPod].semafor.release();

            animacjaLicznik(pietra[pas.pietroPocz + lpieterPod].licznikLabel2, pietra[pas.pietroPocz + lpieterPod].licznik2);
        } else if (pas.pietroPocz == 0 && pas.pietroKon < 0) {
            pietra[lpieterPod].kolejka0.add(pas);
            poczatkowaPozPasazera(pas);
            pietra[lpieterPod].licznik0++;

        //    pietra[pas.pietroPocz + lpieterPod].semafor.release();

            animacjaLicznik(pietra[pas.pietroPocz + lpieterPod].licznikLabel0, pietra[pas.pietroPocz + lpieterPod].licznik0);
        } else {
            pietra[pas.pietroPocz + lpieterPod].kolejka.add(pas);

            poczatkowaPozPasazera(pas);
            pietra[pas.pietroPocz + lpieterPod].licznik++;

           // pietra[pas.pietroPocz + lpieterPod].semafor.release();

            animacjaLicznik(pietra[pas.pietroPocz + lpieterPod].licznikLabel, pietra[pas.pietroPocz + lpieterPod].licznik);
        }

        ++zmGlobalne.pasazerowieDoObslugi;                          //ilu pasazerow trzeba obsluzyc
        pietra[pas.pietroPocz + lpieterPod].semafor.release();      //podniesienie semafora po dodaniu nowego paszaera do pietra


        System.out.println("Nowy pasazer nr: " + pas.nrPasazera + " zostal utworzony");
    }

    public void animacjaLicznik(Label licznikLabel, int licznik)    //uaktualnienie wyswietlanej liczby pasazerow na daneym pietrze
    {
        Platform.runLater(()->{
            licznikLabel.setText(""+licznik);
        });
    }


    void wypisanieStanuBudynku()            //wyspisanie poszczegolnych wezwan na kazdym pietrze
    {
        System.out.println("");
        System.out.println("Stan budynku");


        for(Pietro e : pietra)
        {
            if(e.numer == 0)
            {
                for(Pasazer f : e.kolejka1)
                {
                    System.out.println("Pasazer na pietrze " + e.numer);
                }
                for(Pasazer f : e.kolejka2)
                {
                    System.out.println("Pasazer na pietrze " + e.numer);
                }
                for(Pasazer f : e.kolejka0)
                {
                    System.out.println("Pasazer na pietrze " + e.numer);
                }
            }
            else
            {
                for(Pasazer f : e.kolejka)
                {
                    System.out.println("Pasazer na pietrze " + e.numer);
                }
            }
        }
    }
    Button nowyPasazerButton;

    public void nowyPasazerButton()             //ustawineie przysku odpowiadjacego za dodawanie nowych pasazerow
    {
        nowyPasazerButton = new Button();
        nowyPasazerButton.setLayoutX(10);
        nowyPasazerButton.setLayoutY(520);
        nowyPasazerButton.setText("Nowy pasazer");
        nowyPasazerButton.setPrefWidth(200);
        nowyPasazerButton.setPrefHeight(50);
        nowyPasazerButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                    tworzenieNowychPasazerow();
            }
        });

        Platform.runLater(()->{
            HelloApplication.root.getChildren().add(nowyPasazerButton);
        });
    }

    //public void run() {
    public void inicjalizajaWiez() {

        int heightRow = 40;
        int wysokoscBudynku = zmGlobalne.lpieter;
        int pod_0 = 500;
        int pWysokosc1 = heightRow * ((wysokoscBudynku / 2) + 1);
        int pWysokosc2 = heightRow * wysokoscBudynku;
        int lPodziemnychPieter = zmGlobalne.lpieterPod;

        GridPane wieza1 = new GridPane();                       //wyrysowanie szkieletu budynku o parametrach wczesniej wprowadzonych
        GridPane wieza2 = new GridPane();                       //poprwne wyswietlenie wystepuje dla liczby pietr podziemnych max 8, dla liczby pieter max 12
        GridPane wieza0 = new GridPane();

        wieza1.setGridLinesVisible(true);
        wieza2.setGridLinesVisible(true);
        wieza0.setGridLinesVisible(true);

        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();

        column1.setPrefWidth(200);
        column2.setPrefWidth(50);

        wieza1.getColumnConstraints().addAll(column1, column2);
        wieza2.getColumnConstraints().addAll(column1, column2);
        wieza0.getColumnConstraints().addAll(column1, column2);

        for (int i = 0; i <= wysokoscBudynku / 2; i++) {                //wieza1
            RowConstraints row = new RowConstraints(heightRow);
            wieza1.getRowConstraints().add(row);
        }

        for (int i = 0; i < lPodziemnychPieter + 1; i++) {              //wieza0
            RowConstraints row = new RowConstraints(heightRow);
            wieza0.getRowConstraints().add(row);
        }

        for (int i = 0; i < wysokoscBudynku; i++) {                     //wieza2
            RowConstraints row = new RowConstraints(heightRow);
            wieza2.getRowConstraints().add(row);
        }

        wieza1.setLayoutX(400);
        wieza1.setLayoutY(pod_0 - pWysokosc1);

        wieza0.setLayoutX(750);
        wieza0.setLayoutY(pod_0 - heightRow);

        wieza2.setLayoutX(1100);
        wieza2.setLayoutY(pod_0 - pWysokosc2);


        Platform.runLater(() -> {
            HelloApplication.root.getChildren().add(wieza0);
            HelloApplication.root.getChildren().add(wieza1);
            HelloApplication.root.getChildren().add(wieza2);
        });

        for (Pietro e : pietra)                     //wyrysowanie przy kazdym pietrze licznika odpowiadajacego za wyswietlanie liczby oczekujacy paszerow na pietrze
        {
            if (e.numer < 0) {
                e.licznikLabel.setLayoutX(1000);
                e.licznikLabel.setLayoutY(pod_0 + (Math.abs(e.numer) - 1) * heightRow);

                e.licznik = e.kolejka.size();
                e.licznikLabel.setText(""+e.licznik);
                Platform.runLater(() -> {
                    HelloApplication.root.getChildren().add(e.licznikLabel);
                });
            }
            else if (e.numer == 0)
            {
                e.licznikLabel0.setLayoutX(1000);                   //dla kolejki 0 na pietrze 0
                e.licznikLabel0.setLayoutY(pod_0 - heightRow);
                e.licznik0 = e.kolejka0.size();
                e.licznikLabel0.setText(""+e.licznik0);

                e.licznikLabel1.setLayoutX(650);                    //dla kolejki 1 na pietrze 0
                e.licznikLabel1.setLayoutY(pod_0 - heightRow);
                e.licznik1 = e.kolejka1.size();
                e.licznikLabel1.setText(""+e.licznik1);


                e.licznikLabel2.setLayoutX(1350);                   //dla kolejki 2 na pietrze 0
                e.licznikLabel2.setLayoutY(pod_0 - heightRow);
                e.licznik2 = e.kolejka2.size();
                e.licznikLabel2.setText(""+e.licznik2);

                Platform.runLater(() -> {
                    HelloApplication.root.getChildren().addAll(e.licznikLabel0, e.licznikLabel1, e.licznikLabel2);
                });
            }
            else if(e.numer <= zmGlobalne.lpieter / 2)
            {
                e.licznikLabel.setLayoutX(650);
                e.licznikLabel.setLayoutY(pod_0 - (Math.abs(e.numer) + 1) * heightRow);

                e.licznik = e.kolejka.size();
                e.licznikLabel.setText(""+e.licznik);
                Platform.runLater(() -> {
                    HelloApplication.root.getChildren().add(e.licznikLabel);
                });
            }
            else if(e.numer > zmGlobalne.lpieter / 2)
            {
                e.licznikLabel.setLayoutX(1350);
                e.licznikLabel.setLayoutY(pod_0 - (Math.abs(e.numer) + 1) * heightRow);

                e.licznik = e.kolejka.size();
                e.licznikLabel.setText(""+e.licznik);
                Platform.runLater(() -> {
                    HelloApplication.root.getChildren().add(e.licznikLabel);
                });
            }

        }
        nowyPasazerButton();
    }
}
