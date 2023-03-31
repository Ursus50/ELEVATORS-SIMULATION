package com.example.projektwindyjavafx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.*;
import java.io.File;

import static javafx.scene.text.Font.*;

public class Main extends Thread{

    int heightRow = 40;
    int wysokoscBudynku = -1;
    int lPodziemnychPieter = -1;
    int pPodziemnePietra = heightRow * (lPodziemnychPieter + 2);
    int pojemnoscWindy = -1;
    int lpasazerow = -1;
    int rozmiarTablicyWind = 41;
    int pod_0 = 500;

    volatile int start = 0;

    int dtWinda = 300;
    int dtWysiadanie = 1000;
    int dtWsiadanie = 1000;

    Boolean wlasneDane = false;
    Boolean wlasneDaneBudynek = false;
    Boolean reset = false;

    Button startButton, wlasneDaneButton, daneBudynekButton, resetButton;
    Label lpieterLabel, lpieterPodLabel, pojemnoscWindLabel, lPasazerowLabel, czasWindaLabel, czasWysiadanieLabel, czasWsiadaniaLabel;
    TextField lpieterField, lpieterPodField, pojemnoscWindField, lPasazerowField, czasWindaField, czasWysiadanieField, czasWsiadaniaField, lWind1Field, lWind2Field, lWind0Field;
    Label lWind0Label, lWind1Label, lWind2Label;

    int lWindNr0 = 3;
    int lWindNr1 = 2;
    int lWindNr2 = 1;
    GridPane tablicaGrid[] = new GridPane[rozmiarTablicyWind];  //tablica przechowujaca przedstawienie graficzne wind
    Winda tablicaWind[] =  new Winda[rozmiarTablicyWind];       //tablica przechowujaca procesy wind
    int indexWindy = 0;
    int sumaWind = 0;

    boolean czyPlik(String nazwaPliku)       //sprawdzenie czy instnieje plik (w zaleznosci od parametru sprawdza PlikWejsciowy lub DanePasazerow)
    {
        String pom;
        File plik;
        pom = new File("").getAbsolutePath() + nazwaPliku;

        plik = new File(pom);
        if(plik.exists() && plik.isFile())          //true jesli plik istnieje
        {
            System.out.println("Znaleziono plik wejscciowy");
            return true;
        }
        else
            System.out.println("Brak pliku");
        return false;                               //false jesli plik nie istnieje
    }

    public void ustawienieStart()           //ustawienie przycisku START
    {
        startButton = new Button();
        startButton.setLayoutX(10);
        startButton.setLayoutY(800);
        startButton.setText("START");
        startButton.setPrefWidth(200);
        startButton.setPrefHeight(50);
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                if((lpasazerow < 0 || lPodziemnychPieter < 0 || pojemnoscWindy < 0 || wysokoscBudynku < 0) && !wlasneDaneBudynek)   //sprawdzenie, czy dane budynku zostaly wporwadzone
                {
                    ostrzezenie();
                    return;
                }
                sumaWind = lWindNr0 + lWindNr1 + lWindNr2;
                if( sumaWind >= rozmiarTablicyWind)             //sprawdzenie czy liczba wind nie przekracza dopuszczalnej ilkosci (rozmiarTablicyWind)
                {
                    ostrzezenieZaDuzoWind();
                    return;
                }
                System.out.println("Start!");

                start = 1;                                      //uruchomienie symulacji
            }
        });

        Platform.runLater(()->{
            HelloApplication.root.getChildren().add(startButton);
        });
    }

    public void ustawienieWlasneDane()      //ustawienie przycisku do pobrania wlasnych danych pasazerow (plik musi zawierac conajmniej tyle samo danych o pasazerach, co zostalo zadeklarowane o ilosci poczatkowych pasazerach)
    {
        wlasneDaneButton = new Button();
        wlasneDaneButton.setLayoutX(10);
        wlasneDaneButton.setLayoutY(730);
        wlasneDaneButton.setText("Wlasne dane pasazerow");
        wlasneDaneButton.setPrefWidth(200);
        wlasneDaneButton.setPrefHeight(50);
        wlasneDaneButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                String pom = "\\src\\main\\java\\com\\example\\projektwindyjavafx\\PlikiTekstowe\\DanePasazerow.txt";
                if(czyPlik(pom))            //sprawdzenie czy plik istnieje
                    wlasneDane = true;
            }
        });

        Platform.runLater(()->{
            HelloApplication.root.getChildren().add(wlasneDaneButton);
        });
    }

    public void ustawienieBudynekButton()   //ustawienie przycisku do pobrania wlasnych danych budynku (wysokosc, pietra podziemne, pojemnosc wind, liczba poczatkowych pasazerow)
    {
        daneBudynekButton = new Button();
        daneBudynekButton.setLayoutX(10);
        daneBudynekButton.setLayoutY(660);
        daneBudynekButton.setText("Wlasne dane wejsciowe");
        daneBudynekButton.setPrefWidth(200);
        daneBudynekButton.setPrefHeight(50);
        daneBudynekButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                String pom = "\\src\\main\\java\\com\\example\\projektwindyjavafx\\PlikiTekstowe\\PlikWejsciowy.txt";
                if(czyPlik(pom))
                    wlasneDaneBudynek = true;
            }
        });

        Platform.runLater(()->{
            HelloApplication.root.getChildren().add(daneBudynekButton);
        });
    }
    public void ustawienieReset()           //ustawienie przycisku reserujacego symulacje po jej zakonczeniu
    {
        resetButton = new Button();
        resetButton.setLayoutX(10);
        resetButton.setLayoutY(590);
        resetButton.setText("RESET");
        resetButton.setPrefWidth(200);
        resetButton.setPrefHeight(50);
        resetButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {

                reset = true;
                for(int i = 0; i < sumaWind; i++)       //czekanie na zakonczenie sie dzialania wszystkich wind
                {
                        tablicaWind[i].interrupt();
                }
            }
        });

        Platform.runLater(()->{
            HelloApplication.root.getChildren().add(resetButton);
        });
    }

//    public void nowyPasazerButton(Budynek budynek)
//    {
//
//        nowyPasazerButton = new Button();
//        nowyPasazerButton.setLayoutX(10);
//        nowyPasazerButton.setLayoutY(400);
//        nowyPasazerButton.setText("Nowy pasazer");
//        nowyPasazerButton.setPrefWidth(200);
//        nowyPasazerButton.setPrefHeight(50);
//        nowyPasazerButton.setOnAction(new EventHandler<ActionEvent>() {
//            public void handle(ActionEvent actionEvent) {
//                 if(start == 1)
//                    budynek.tworzenieNowychPasazerow();
//            }
//        });
//
//        Platform.runLater(()->{
//            HelloApplication.root.getChildren().add(nowyPasazerButton);
//        });
//    }

    public void ustawienieLabeli()                  //ustawienie napisów statycznych
    {
        lpieterLabel = new Label();
        lpieterLabel.setText("Liczba pieter:");
        lpieterLabel.setLayoutX(10);
        lpieterLabel.setLayoutY(30);

        lpieterPodLabel  = new Label();
        lpieterPodLabel.setText("Pietra podziemne:");
        lpieterPodLabel.setLayoutX(10);
        lpieterPodLabel.setLayoutY(60);

        pojemnoscWindLabel  = new Label();
        pojemnoscWindLabel.setText("Pojemnosc wind:");
        pojemnoscWindLabel.setLayoutX(10);
        pojemnoscWindLabel.setLayoutY(90);

        lPasazerowLabel = new Label();
        lPasazerowLabel.setText("Poczatkowi pasazerowie:");
        lPasazerowLabel.setLayoutX(10);
        lPasazerowLabel.setLayoutY(120);

        czasWindaLabel = new Label();
        czasWindaLabel.setText("Czas animacji windy:");
        czasWindaLabel.setLayoutX(10);
        czasWindaLabel.setLayoutY(200);

        czasWysiadanieLabel = new Label();
        czasWysiadanieLabel.setText("Czas animacji wysiadania:");
        czasWysiadanieLabel.setLayoutX(10);
        czasWysiadanieLabel.setLayoutY(230);

        czasWsiadaniaLabel = new Label();
        czasWsiadaniaLabel.setText("Czas animacji wsiadania:");
        czasWsiadaniaLabel.setLayoutX(10);
        czasWsiadaniaLabel.setLayoutY(260);

        lWind1Label = new Label();
        lWind1Label.setText("Liczba wind typu 1 (pietra niskie):");
        lWind1Label.setLayoutX(10);
        lWind1Label.setLayoutY(330);

        lWind0Label = new Label();
        lWind0Label.setText("Liczba wind typu 0 (garaz):");
        lWind0Label.setLayoutX(10);
        lWind0Label.setLayoutY(360);

        lWind2Label = new Label();
        lWind2Label.setText("Liczba wind typu 2 (pietra wysokie):");
        lWind2Label.setLayoutX(10);
        lWind2Label.setLayoutY(390);

        Text text1=new Text("Czas aniamacji w [ms]:");
        text1.setStyle("-fx-font-weight: bold");
        text1.setFont((font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 13)));
        text1.setLayoutY(180);
        text1.setLayoutX(10);

        Text text2=new Text("Podstawowe dane:");
        text2.setStyle("-fx-font-weight: bold");
        text2.setFont((font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 13)));
        text2.setLayoutY(20);
        text2.setLayoutX(10);

        Text text3=new Text("Liczba wind:");
        text3.setStyle("-fx-font-weight: bold");
        text3.setFont((font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 13)));
        text3.setLayoutY(310);
        text3.setLayoutX(10);

        Platform.runLater(()->{
            HelloApplication.root.getChildren().addAll(lpieterLabel, lpieterPodLabel, pojemnoscWindLabel, lPasazerowLabel, czasWindaLabel,
                    czasWysiadanieLabel, czasWsiadaniaLabel, text1, text2,text3, lWind1Label,lWind0Label,lWind2Label);
        });
    }


    public void ustawiniePol()              //ustawienie pol tekstowych do wprowadzania danych
    {

        lpieterField = new TextField();
        lpieterField.setLayoutX(150);
        lpieterField.setLayoutY(30);
        lpieterField.setPrefWidth(50);
        lpieterField.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                String text;
                text = lpieterField.getText();
                wysokoscBudynku = Integer.parseInt(text);
                System.out.println("Wysokosc budynku: " + wysokoscBudynku);
            }
        });

        lpieterPodField  = new TextField();
        lpieterPodField.setLayoutX(150);
        lpieterPodField.setLayoutY(60);
        lpieterPodField.setPrefWidth(50);
        lpieterPodField.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                String text;
                text = lpieterPodField.getText();
                lPodziemnychPieter = Integer.parseInt(text);
                System.out.println("Pietra podziemne: " + lPodziemnychPieter);
            }
        });

        pojemnoscWindField  = new TextField();
        pojemnoscWindField.setLayoutX(150);
        pojemnoscWindField.setLayoutY(90);
        pojemnoscWindField.setPrefWidth(50);
        pojemnoscWindField.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                String text;
                text = pojemnoscWindField.getText();
                pojemnoscWindy = Integer.parseInt(text);
                System.out.println("Pojemnosc windy: " + pojemnoscWindy);
            }
        });

        lPasazerowField = new TextField();

        lPasazerowField.setLayoutX(150);
        lPasazerowField.setLayoutY(120);
        lPasazerowField.setPrefWidth(50);
        lPasazerowField.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                String text;
                text = lPasazerowField.getText();
                lpasazerow = Integer.parseInt(text);
                System.out.println("Poczatkowi pasazerowie: " + lpasazerow);
            }
        });

        czasWindaField = new TextField();
        czasWindaField.setText(""+dtWinda);
        czasWindaField.setLayoutX(150);
        czasWindaField.setLayoutY(200);
        czasWindaField.setPrefWidth(50);
        czasWindaField.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                String text;
                text = czasWindaField.getText();
                dtWinda = Integer.parseInt(text);
                System.out.println("Czas aniamcji windy: " + dtWinda);
            }
        });

        czasWysiadanieField = new TextField();
        czasWysiadanieField.setText(""+dtWysiadanie);
        czasWysiadanieField.setLayoutX(150);
        czasWysiadanieField.setLayoutY(230);
        czasWysiadanieField.setPrefWidth(50);
        czasWysiadanieField.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                String text;
                text = czasWysiadanieField.getText();
                dtWysiadanie = Integer.parseInt(text);
                System.out.println("Poczatkowi pasazerowie: " + dtWysiadanie);
            }
        });

        czasWsiadaniaField = new TextField();
        czasWsiadaniaField.setText(""+dtWsiadanie);
        czasWsiadaniaField.setLayoutX(150);
        czasWsiadaniaField.setLayoutY(260);
        czasWsiadaniaField.setPrefWidth(50);
        czasWsiadaniaField.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                String text;
                text = czasWsiadaniaField.getText();
                dtWsiadanie = Integer.parseInt(text);
                System.out.println("Poczatkowi pasazerowie: " + dtWsiadanie);

            }
        });


        lWind1Field = new TextField();
        lWind1Field.setText(""+lWindNr1);
        lWind1Field.setLayoutX(200);
        lWind1Field.setLayoutY(330);
        lWind1Field.setPrefWidth(50);
        lWind1Field.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                String text;
                text = lWind1Field.getText();
                lWindNr1 = Integer.parseInt(text);
                // System.out.println("Poczatkowi pasazerowie: " + dtWsiadanie);

            }
        });

        lWind0Field = new TextField();
        lWind0Field.setText(""+lWindNr0);
        lWind0Field.setLayoutX(200);
        lWind0Field.setLayoutY(360);
        lWind0Field.setPrefWidth(50);
        lWind0Field.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                String text;
                text = lWind0Field.getText();
                lWindNr0 = Integer.parseInt(text);
            }
        });

        lWind2Field = new TextField();
        lWind2Field.setText(""+lWindNr2);
        lWind2Field.setLayoutX(200);
        lWind2Field.setLayoutY(390);
        lWind2Field.setPrefWidth(50);
        lWind2Field.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                String text;
                text = lWind2Field.getText();
                lWindNr2 = Integer.parseInt(text);
            }
        });

        Platform.runLater(()->{
            HelloApplication.root.getChildren().addAll(lpieterField, lpieterPodField, pojemnoscWindField, lPasazerowField,
                    czasWindaField, czasWysiadanieField, czasWsiadaniaField,lWind1Field, lWind0Field, lWind2Field);
        });
    }

    public void ostrzezenie()
    {
        Dialog.ostrzezenieBrakuDanych();
    }               //ostrzezenie w przypadku braku danych
    public void ostrzezenieZaDuzoWind()
    {
        Dialog.ostrzezenieZaDuzoWind(); //ostrzezenie w przypadku zbyt dużej ilosci wprowadzonych wind
    }

    public Winda tworzenieWindy(int typ, Budynek budynek, ZmGlobalne zmGlobalne)    //tworzenie procesu Windy z przekazanych parametrow
    {
        GridPane winda = new GridPane();            //graficzne obrazowanie windy
        winda.setGridLinesVisible(true);
        winda.getRowConstraints().add(new RowConstraints(heightRow));
        for (int i = 0; i < zmGlobalne.pojemnoscWindy; i++) {
            winda.getColumnConstraints().add(new ColumnConstraints(200/zmGlobalne.pojemnoscWindy));
        }

        if(typ == 1)                //ustalenie w jakim miejscu ma zostac wyswietlona winda
        {
            winda.setLayoutX(400);
            winda.setLayoutY(pod_0 - heightRow);
        }
        else if(typ == 0)
        {
            pPodziemnePietra = heightRow * (zmGlobalne.lpieterPod - 1);

            winda.setLayoutX(750);
            winda.setLayoutY(pod_0 + pPodziemnePietra);
        }
        else if(typ == 2)
        {
            winda.setLayoutX(1100);
            winda.setLayoutY(pod_0 - heightRow);
        }

        tablicaGrid[indexWindy] = winda;    //wpisanie wskazania na watek do tablicy procesow wind

        Platform.runLater(()->
        {
            HelloApplication.root.getChildren().add(winda);
        });

        Winda windaWatek;

        if(typ == 0)
            return windaWatek = new Winda(zmGlobalne.pojemnoscWindy, typ, budynek, zmGlobalne.lpieterPod, zmGlobalne.lpieter, -budynek.lpieterPod, zmGlobalne,winda,
                dtWinda, dtWysiadanie, dtWsiadanie);
        else
            return windaWatek = new Winda(zmGlobalne.pojemnoscWindy, typ, budynek, zmGlobalne.lpieterPod, zmGlobalne.lpieter, 0, zmGlobalne,winda,
                    dtWinda, dtWysiadanie, dtWsiadanie);
    }

    public void run()
    {
        while (true)
        {
            ustawienieLabeli();         //wyswietlenie napisow, pol tekstowych oraz przyciskow
            ustawiniePol();
            ustawienieStart();
            ustawienieWlasneDane();
            ustawienieBudynekButton();
            ustawienieReset();                      //wyswietlenie przycisku RESET

            start = 0;
            while (start == 0 )         //aktywne czekanie, na pomyslne wcisniecie przycisku START
            {
                try {
                    sleep(1);
                }catch (InterruptedException e){e.printStackTrace();}
            }

            ZmGlobalne zmGlobalne;
            if(wlasneDaneBudynek)       //jezeli zostalo zazadane pobranie wlasnych danych wejsciowych
            {
                zmGlobalne = new ZmGlobalne(wysokoscBudynku, lPodziemnychPieter, pojemnoscWindy, lpasazerow);
                zmGlobalne.pobranieDanych();
                lpieterField.setText(""+zmGlobalne.lpieter);
                lpieterPodField.setText(""+zmGlobalne.lpieterPod);
                pojemnoscWindField.setText(""+zmGlobalne.pojemnoscWindy);
                lPasazerowField.setText(""+zmGlobalne.losob);
            }
            else                        //jezeli dane wejsciowe zostaly wprowadzone z klawiatury
            {
                zmGlobalne = new ZmGlobalne(wysokoscBudynku, lPodziemnychPieter, pojemnoscWindy, lpasazerow);
            }
            wlasneDaneBudynek = false;

            Budynek budynek = new Budynek(zmGlobalne.lpieter, zmGlobalne.lpieterPod, zmGlobalne);   //utwirzenie procesu budynek

            wysokoscBudynku = zmGlobalne.lpieter;
            pojemnoscWindy = zmGlobalne.pojemnoscWindy;

            if(!wlasneDane)
                budynek.tworzeniePasazerow();   //tworzenie losowych pasazerow
            else
                budynek.tworzeniePasazerow1();  //tworzenie pasazerow z danycmi z pliku DanePasazerow.txt

            budynek.wypisanieStanuBudynku();
            budynek.inicjalizajaWiez();
            //budynek.run();                    //uruchomienie watku budynek odpowiedzialnego za ryzowanie wiez w jakich beda poruszac sie windy

            for(int i = 0; i < lWindNr0; i++)       //windy nr0             //tworzenie wind w ilosciach wczesniej wporwadzonych
            {
                tablicaWind[indexWindy++] = tworzenieWindy(0, budynek, zmGlobalne);
            }
            for(int i = 0; i < lWindNr1; i++)       //windy nr1
            {
                tablicaWind[indexWindy++] = tworzenieWindy(1, budynek, zmGlobalne);
            }
            for(int i = 0; i < lWindNr2; i++)       //windy nr2
            {
                tablicaWind[indexWindy++] = tworzenieWindy(2, budynek, zmGlobalne);
            }

            for(int i = 0; i < sumaWind; i++)       //uruchomienie wszystkich wind
            {
                tablicaWind[i].start();
            }



            while (!reset)      //aktywne czekanie w oczekiwaniu na wcisniecie przysku RESET
            {
                try {
                    sleep(1);
                }catch (InterruptedException e){e.printStackTrace();}
            }


            for(int i = 0; i < sumaWind; i++)       //czekanie na zakonczenie sie dzialania wszystkich wind
            {
                try {
                    tablicaWind[i].join();
                }catch (InterruptedException e){e.printStackTrace();};
            }
            reset = false;

            /*try {
                budynek.join();                     //czekanie na zakonczenie sie dzialania budynku
            }catch (InterruptedException e){e.printStackTrace();}*/

            indexWindy = 0;                         //ustawienie indeksu wskazujacego w jakie miejsce nalezy wpisac nowy proces Windy do tablicy

 /*           ustawienieReset();                      //wyswietlenie przycisku RESET po zakonczeniu dzialania symulkacji
            while (!reset)      //aktywne czekanie w oczekiwaniu na wcisniecie przysku RESET
            {
                try {
                    sleep(1);
                }catch (InterruptedException e){e.printStackTrace();}
            }
            reset = false;*/

            wlasneDane = false;
            lpasazerow = -1;            //resetowanie wprowadzonych wczesniej danych
            lPodziemnychPieter = -1;
            pojemnoscWindy= -1;
            wysokoscBudynku = -1;

            Platform.runLater(()->{
                HelloApplication.root.getChildren().clear();    //oczyszczenie sceny z wczesniejszych zmian
            });


        }
    }

}
