package com.example.projektwindyjavafx;


import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.*;


public class Winda extends Thread
{
    volatile boolean dziala = true;

    Budynek budynek;
    int pojemnoscWindy;
    int nrWindy;            //0-podziemna, 1-niska, 2-wysoka
    int lPasazerow = 0;
    int kierunek = 1;           //1-gora, -1-dol, 0-stoi
    int maxGora, maxDol;
    List<Pasazer> windaPasazerowie = new ArrayList();   //lista przechowujaca pasazerow, ktorzy wsiedli do windy

    List<Integer> windaWezwania = new ArrayList();      //lista przechowujaca wezwania windy na pietra
    int aktualnePietro = 0;
    int indeksMax = -1;
    int indeksMin = -1;
    int indeks = -1;
    int maxWzawanie;
    int minWzawanie;
    ZmGlobalne zmGlobalne;

    int poprzedniePietro = 0;
    int heightRow = 40;
    int wysokoscBudynku;
    int lPodziemnychPieter;

    GridPane winda;
    Boolean wolne[];

    volatile int dtWinda;           //czasy animacji
    volatile int dtWysiadanie;
    volatile int dtWsiadanie;


    public Winda(int pojemnoscWindy, int nrWindy, Budynek budynek,int maxDol, int maxGora, int aktualnePietro, ZmGlobalne zmGlobalne1, GridPane winda, int dtWinda, int dtWysiadanie, int dtWsiadanie)
    {
        this.pojemnoscWindy = pojemnoscWindy;
        this.nrWindy = nrWindy;
        this.budynek = budynek;
        this.maxDol = maxDol;
        this.maxGora = maxGora;
        this.aktualnePietro = aktualnePietro;
        this.poprzedniePietro = aktualnePietro;
        this.zmGlobalne = zmGlobalne1;

        this.wysokoscBudynku = zmGlobalne.lpieter;
        this.winda = winda;
        wolne = new Boolean[pojemnoscWindy];
        int i;
        for(i = 0; i< pojemnoscWindy; i++)      //ustawienie, ze wszystkie miejsca windzie sa wolne
        {
            wolne[i] = true;
        }

        this.dtWinda = dtWinda;
        this.dtWysiadanie = dtWysiadanie;
        this.dtWsiadanie = dtWsiadanie;
    }

    public void animacjaWindy()         //animacja przesuwania sie windy
    {
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(winda);
        translate.setByY(-heightRow * (aktualnePietro - poprzedniePietro));
        int roznica = Math.abs(aktualnePietro - poprzedniePietro);
        translate.setDuration(Duration.millis(dtWinda * roznica));
        translate.setOnFinished(e -> {
            synchronized (this){
                notifyAll();
            }
        });

        Platform.runLater(()->{
            translate.play();

        });

        synchronized (this){
            try {
                    wait();
            }catch (InterruptedException e){
                e.printStackTrace();
                dziala = false;
                //throw new RuntimeException("Koniec windy nr"+nrWindy);
            }
        }
    }

    public void animacjaWsiadania(Pasazer pas)      //animacja wsiadania pasazera do windy
    {
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(pas.reprezentant);
        translate.setDuration(Duration.millis(dtWsiadanie));

        translate.setOnFinished(e -> {
            synchronized (this){
                notifyAll();
            }
        });
        Platform.runLater(()->{
            translate.play();

        });

        synchronized (this){
            try {
                    wait();
            }catch (InterruptedException e){
                e.printStackTrace();
               // throw new RuntimeException("Koniec windy nr"+nrWindy);
                dziala = false;
            }
        }
        Platform.runLater(()->{
            HelloApplication.root.getChildren().remove(pas.reprezentant);
            winda.add(pas.reprezentant, pas.indexWwindzie, 0);
        });
    }

    public void animacjaZwykleWysiadanie(Pasazer pas)               //animacja zwyklego wysiadania paszera z windy
    {
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(pas.reprezentant);
        translate.setByX(-600);
        translate.setDuration(Duration.millis(dtWysiadanie));
        translate.setOnFinished(e -> {
            synchronized (this){
                    while (winda.getChildren().remove(pas.reprezentant))
                    {
    /*                    try {
                            wait(1);
                        }catch (InterruptedException f){
                            f.printStackTrace();
                            //throw new RuntimeException("Koniec windy nr"+nrWindy);
                            dziala = false;
                        }*/
                        ;
                    }

                notifyAll();
            }
        });

        Platform.runLater(()->{
            translate.play();
        });

        synchronized (this){
            try {
                    wait();
            }catch (InterruptedException e){
                e.printStackTrace();
                //throw new RuntimeException("Koniec windy nr"+nrWindy);
                dziala = false;
            }
        }
    }

    public void animacjaSpecjalneWysiadanie(Pasazer pas)            //animacja specjalnego wysiadania z windy, czyli jezeli pasazer przesiada sie na pietrze 0
    {
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(pas.reprezentant);

        if(nrWindy == 1 && pas.pietroKon < 0)                           //z windy nr 1 do windy nr 0 na parterze
            translate.setByX(575 - 200/pojemnoscWindy * pas.indexWwindzie);// - 10);
        else if(nrWindy == 1 && pas.pietroKon > zmGlobalne.lpieter / 2)   //z windy nr 1 do windy nr 2 na parterze
            translate.setByX(925 - 200/pojemnoscWindy * pas.indexWwindzie);// - 10);
        else if(nrWindy == 0 && pas.pietroKon <= zmGlobalne.lpieter / 2 && pas.pietroKon > 0) //z windy nr 0 do windy nr 1 na parterze
            translate.setByX(-125 - 200/pojemnoscWindy * pas.indexWwindzie);// - 10);
        else if(nrWindy == 0 && pas.pietroKon > zmGlobalne.lpieter / 2)     //z windy nr 0 do windy nr 2 na parterze
            translate.setByX(575 - 200/pojemnoscWindy * pas.indexWwindzie);// - 10);
        else if(nrWindy == 2 && pas.pietroKon < 0)                      //z windy nr 2 do windy nr 0 na parterze
            translate.setByX(-125 - 200/pojemnoscWindy * pas.indexWwindzie);// - 10);
        else if(nrWindy == 2 && pas.pietroKon > 0)
            translate.setByX(-475 - 200/pojemnoscWindy * pas.indexWwindzie);// - 10);    //z windy nr 2 do windy nr 1 na parterze


        translate.setDuration(Duration.millis(dtWysiadanie));
        translate.setOnFinished(e -> {
            synchronized (this){

                while (winda.getChildren().remove(pas.reprezentant))        //usuniecie graficzne pasazera z windy
                {
 /*                   try {
                        sleep(1);
                    }catch (InterruptedException f){
                        f.printStackTrace();
                      //  throw new RuntimeException("Koniec windy nr"+nrWindy);
                        dziala = false;
                        }*/
                    ;
                }

                double posX = 0;
                double posY = zmGlobalne.pod_0 - 10;

                if(pas.pietroKon <= zmGlobalne.lpieter/2 && pas.pietroKon > 0)
                    posX = 615;
                else if(pas.pietroKon < 0)
                    posX = 965;
                else if( pas.pietroKon > zmGlobalne.lpieter/2)
                    posX = 1315;

                Circle kolo;
                Text text;

                pas.reprezentant = new StackPane();
                kolo = new Circle();
                kolo.setRadius(10);
                kolo.setFill(Color.RED);

                text = new Text();
                text.setText("" + pas.nrPasazera);

                pas.reprezentant.getChildren().addAll(kolo, text);
                pas.reprezentant.setLayoutX(posX);
                pas.reprezentant.setLayoutY(posY);
                pas.reprezentant.setLayoutY(zmGlobalne.pod_0 - 30);


                Platform.runLater(()->{                                         //dodanie graficzne pasazera do kolejki na pietrze
                    HelloApplication.root.getChildren().add(pas.reprezentant);
                });

                notifyAll();
            }
        });

        Platform.runLater(()->{
            translate.play();
            //HelloApplication.root.getChildren().add(pas.reprezentant);

        });

        synchronized (this){
            try {
                    wait();
            }catch (InterruptedException e){
                e.printStackTrace();
                //throw new RuntimeException("Koniec windy nr"+nrWindy);
                dziala = false;
            }
        }
    }

    public void animacjaLicznik(Label licznikLabel, int licznik)    //uaktualnienie licznki paszerow oczekujacych na kazdym pietrze
    {
        Platform.runLater(()->{
            licznikLabel.setText(""+licznik);
        });
    }

    public void wezwanieWindy()                     //tworzenie listy wezwan kazdej winy
    {
        windaWezwania.clear();

        for(Pietro e : budynek.pietra)
        {
            if(nrWindy == 1)                        //wezwania dla windy nr 1 (jadacej max do polowy budynku)
            {
                if(e.numer <= maxGora / 2 && e.numer > 0)
                {
                    if(!e.kolejka.isEmpty())
                    {
                        windaWezwania.add(e.numer);
                    }
                }
                else if(e.numer == 0)
                {
                    if(!e.kolejka1.isEmpty())
                    {
                        windaWezwania.add(e.numer);
                    }
                }
            }
            else if(nrWindy == 2)               //wezwania windy dla windy nr 2 (jadacej na parter oraz od polowy budynku do samej gory)
            {
                if(e.numer > maxGora / 2 && e.numer > 0)
                {
                    if(!e.kolejka.isEmpty())
                    {
                        windaWezwania.add(e.numer);
                    }
                }
                else if (e.numer == 0)
                {
                    if(!e.kolejka2.isEmpty())
                    {
                        windaWezwania.add(e.numer);
                    }
                }
            }
            else if(nrWindy == 0)                        //wezwania dla windy nr 0 (obsluguje ujemne pietra i 0)
            {
                if(e.numer < 0)
                {
                    if(!e.kolejka.isEmpty())
                    {
                        windaWezwania.add(e.numer);
                    }
                }
                else if(e.numer == 0)
                {
                    if(!e.kolejka0.isEmpty())
                    {
                        windaWezwania.add(e.numer);
                    }
                }
            }
        }

        int rozmiarPom = windaWezwania.size();
        if (rozmiarPom > 0)
        {
            if(nrWindy != 0)
            {
                maxWzawanie = windaWezwania.get(rozmiarPom - 1);  //pobranie najwiekszego wezwania windy
                minWzawanie = windaWezwania.get(0);             //pobranie najmniejszego wezwania windy
            }
            else
            {
                minWzawanie= windaWezwania.get(rozmiarPom - 1);  //pobranie najwiekszego wezwania windy
                maxWzawanie= windaWezwania.get(0);             //pobranie najmniejszego wezwania windy
            }
        }
    }

    int pomMax;
    int pomMin;

    void kolejnoscPrzystankowGora()             //ustalanie nastepnego przystanku windy jezeli ostatnio winda poruszala sie sie do gory
    {
        indeksMax = -1;
        indeksMin = -1;
        indeks = 0;
        pomMax = 0;
        pomMin = maxGora;

        for(Pasazer e : windaPasazerowie)
        {
            if(e.pietroKon > aktualnePietro && !e.czyPrzesiadka)       //pasazer windy, ktory chce sie dostac najwyzej
            {
                if(e.pietroKon >= pomMax) {
                    pomMax = e.pietroKon;
                    indeksMax = indeks;
                }

                if(e.pietroKon <= pomMin) {     //pasazer windy, ktory chce sie dostac najnizej ale wyzej niz jest aktualnie
                    pomMin = e.pietroKon;
                    indeksMin = indeks;
                }
            }
            else if(e.pietroPocz < 0 && e.czyPrzesiadka)
            {
                indeksMax = indeks;
            }
            ++indeks;
        }

        if(indeksMin >= 0) {                                                //w przydku pustej windy, winda jedzie na gore
            aktualnePietro = windaPasazerowie.get(indeksMin).pietroKon;
        }
        else if(nrWindy == 0 && indeksMax >= 0)
        {
            aktualnePietro = 0;
        }
    }

    void kolejnoscPrzystankowDol()          //ustalanie nastepnego przystanku windy jezeli ostatnio winda poruszala sie do dolu
    {
        indeksMax = -1;
        indeksMin = -1;
        indeks = 0;
        pomMax = aktualnePietro;
        pomMin = 0;
        if(nrWindy == 0)
            pomMin = -zmGlobalne.lpieterPod;

        int wszyscyWgore = 1;

        for(Pasazer e : windaPasazerowie)
        {
            if(e.czyPrzesiadka && pomMin <= 0 && nrWindy != 0)
            {
                indeksMin = indeks;
                wszyscyWgore = 0;
            }
            else if(e.czyPrzesiadka && pomMin < -zmGlobalne.lpieterPod)
            {
                indeksMin = indeks;
            }
            else if(!e.czyPrzesiadka)
            {
                if(e.pietroKon > aktualnePietro)       //pasazer windy, ktory chce sie dostac najwyzej
                {
                    if (e.pietroKon >= pomMax) {
                        pomMax = e.pietroKon;
                        indeksMax = indeks;
                    }
                }

                if (e.pietroKon < aktualnePietro)       //pasazer windy, ktory chce sie dostac niżej niz aktualnie jest
                {
                    if (e.pietroKon >= pomMin && nrWindy != 0)
                    {
                        pomMin = e.pietroKon;
                        indeksMin = indeks;
                    }
                    else if (e.pietroKon >= pomMin)
                    {
                        pomMin = e.pietroKon;
                        indeksMin = indeks;
                    }
                    wszyscyWgore = 0;
                }
            }
            ++indeks;
        }

        int pom1;
        int najbizej = 0;
        if(indeksMin >= 0)                                          //jezeli jest wolnemiejsce w windzie i jedzie na dol to ma sie zatrzymac na wezwanie jezeli jest takie po drodze
        {

            if(windaPasazerowie.get(indeksMin).czyPrzesiadka)       //jezeli dany pasazer, ktory chce jechac nizej sie przesiada
            {
                pom1 = 0;
            }
            else
                pom1 = windaPasazerowie.get(indeksMin).pietroKon;   //jezeli dany pasazer sie nie przesiada

            najbizej = znajdzNajblizsze();                          //znajdowanie czy jest jakis paszer do odebrania po drodze na wczesniej ustalone nastepne pietro

            if(lPasazerow < pojemnoscWindy && najbizej != 0 && najbizej > pom1)
                aktualnePietro = najbizej;
            else
            {
                if(windaPasazerowie.get(indeksMin).czyPrzesiadka)
                    aktualnePietro = 0;
                else
                    aktualnePietro = windaPasazerowie.get(indeksMin).pietroKon;
            }
        }

        if(wszyscyWgore == 1)
        {
            kierunek = 1;
        }
    }

    boolean czyWszyscyWgore()           //sprawdzenie, czy wszyscy pasazerowue w windzie jada na dol
    {
        for(Pasazer e : windaPasazerowie)
        {
            if(e.pietroKon < aktualnePietro || (e.czyPrzesiadka && e.pietroPocz > 0))
            {
                return false;
            }
        }
        return true;
    }

    boolean czyWszyscyWdol()        //sprawdzenie czy wszyscy paszerowie jada do gory
    {
        for(Pasazer e : windaPasazerowie)
        {
            if(e.pietroKon > aktualnePietro && !e.czyPrzesiadka || (e.pietroPocz < 0 && e.czyPrzesiadka))
            {
                return false;
            }
        }
        return true;
    }

    int znajdzNajblizsze()              //jezeli winda ma wolne miejsce i jedzie w dol to zatrzyma sie na najblizszym wezwaniu, jesli takie jest
    {
        int pom = aktualnePietro;
        int najblizsze = 0;

        for(Integer e : windaWezwania)
        {
            if(e < pom)
                najblizsze = e;
        }
        return najblizsze;
    }

    boolean czySaDoOdebrania()      //sprawdzenie, czy na aktualnym pietrze sa pasazerowie do odebrania
    {
        int pom = aktualnePietro;

        if(!windaWezwania.isEmpty())
        {
            for(Integer e : windaWezwania)
            {
                if(e == pom) {
                    System.out.println("Na aktualnym pietrze: " + aktualnePietro + " sa pasazerowie do odebrania przez winde nr: "+nrWindy);
                    return true;
                }
            }
        }
        return false;
    }

    void wzieciePasazerow()             //winda pobiera pasazerow z pietra
    {
        Pasazer pas;

        while(czySaDoOdebrania() && lPasazerow < pojemnoscWindy)        //jezeli na danym pietrze sa pasazerowie do odebrania oraz w indzie jest wolne miejsce
        {
            try {
                budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].semafor.acquire();       //opusczenie semafora by miec indywidualny dostep do kolejki paszerow na pietrze
            }catch (InterruptedException e ){e.printStackTrace();}
            wezwanieWindy();

            if(aktualnePietro != 0 && czySaDoOdebrania())               //pobranie paszera z kolejki jezeli winda znajduje sie na innym pietrze niz 0
            {
                pas = budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].kolejka.getFirst();        //wziecie referencji na pasazera z kolejki
                animacjaLicznik(budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].licznikLabel, --budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].licznik);
                budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].kolejka.removeFirst();           //usuniecie pasazera z kolejki

                budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].semafor.release();               //podniesie semafora, gdyz zakonczyla sie operacja dot. kolejki na wskazanym pietrze

                int i = 0;
                for(Boolean e : wolne)
                {
                    if(e == true)                   //na jakim miejscu w windzie zostanie umiesczony wziety pasazer
                    {
                        pas.indexWwindzie = i;
                        wolne[i] = false;
                        break;
                    }
                    i++;
                }
                animacjaWsiadania(pas);
                windaPasazerowie.add(pas);         //dodanie pasazera do windy
                lPasazerow++;

            }
            else if(czySaDoOdebrania()){            //operacje pobrania psazerra jezeli winda znajduje sie na 0 pietrze
                    if (nrWindy == 1) {
                        pas = budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].kolejka1.getFirst();       //wziecie referencji na pasazera z kolejki
                        animacjaLicznik(budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].licznikLabel1, --budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].licznik1);
                        budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].kolejka1.removeFirst();          //usuniecie pasazera z kolejki

                        budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].semafor.release();               //podniesie semafora, gdyz zakonczyla sie operacja dot. kolejki na wskazanym pietrze

                        int i = 0;
                        for(Boolean e : wolne)
                        {
                            if(e == true)               //na jakim miejscu w windzie zostanie umiesczony wziety pasazer
                            {
                                pas.indexWwindzie = i;
                                wolne[i] = false;
                                break;
                            }
                            i++;
                        }
                        animacjaWsiadania(pas);
                        windaPasazerowie.add(pas);      //dodanie pasazera do windy
                        System.out.println("Pasazer nr: " + pas.nrPasazera + " wsiadl na: " + aktualnePietro + " do windy nr: " + nrWindy);
                        lPasazerow++;
                    } else if (nrWindy == 2) {
                        pas = budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].kolejka2.getFirst();       //wziecie referencji na pasazera z kolejki
                        animacjaLicznik(budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].licznikLabel2, --budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].licznik2);
                        budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].kolejka2.removeFirst();          //usuniecie pasazera z kolejki

                        budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].semafor.release();               //podniesie semafora, gdyz zakonczyla sie operacja dot. kolejki na wskazanym pietrze

                        int i = 0;
                        for(Boolean e : wolne)
                        {
                            if(e == true)               //na jakim miejscu w windzie zostanie umiesczony wziety pasazer
                            {
                                pas.indexWwindzie = i;
                                wolne[i] = false;
                                break;
                            }
                            i++;
                        }
                        animacjaWsiadania(pas);
                        windaPasazerowie.add(pas);      //dodanie pasazera do windy
                        System.out.println("Pasazer nr: " + pas.nrPasazera + " wsiadl na: " + aktualnePietro + " do windy nr: " + nrWindy);
                        lPasazerow++;
                    } else if (nrWindy == 0) {
                        pas = budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].kolejka0.getFirst();       //wziecie referencji na pasazera z kolejki
                        animacjaLicznik(budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].licznikLabel0, --budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].licznik0);
                        budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].kolejka0.removeFirst();          //usuniecie pasazera z kolejki

                        budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].semafor.release();               //podniesie semafora, gdyz zakonczyla sie operacja dot. kolejki na wskazanym pietrze

                        int i = 0;
                        for(Boolean e : wolne)
                        {
                            if(e == true)               //na jakim miejscu w windzie zostanie umiesczony wziety pasazer
                            {
                                pas.indexWwindzie = i;
                                wolne[i] = false;
                                break;
                            }
                            i++;
                        }
                        animacjaWsiadania(pas);         //dodanie pasazera do windy
                        windaPasazerowie.add(pas);
                        System.out.println("Pasazer nr: " + pas.nrPasazera + " wsiadl na: " + aktualnePietro + " do windy nr: " + nrWindy);
                        lPasazerow++;
                    }
            }
            else        //jezeli proces winda wszedl w petle ale inne windy ubiegly ja w pobraniu paszerow z kolejki i na aktualnym pietrze nie ma wiecej pasazerow do odebrania
            {
                budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].semafor.release();       //podniesienie semafora
            }
        }
    }


    void wysiadanie()           //operacja wysiadania pasazera z windy
    {
        int pom;
        pom = aktualnePietro;
        int i ;

        for(i = 0; i < windaPasazerowie.size(); i++ )        //sprawdzenie kazdego z pasazerow czy nie chca wysiasc z windy na aktualnym pietrze
        {
            if (pom == 0)
            {
                if (windaPasazerowie.get(i).czyPrzesiadka)   //jezeli pasazer chce zrobic przesiadke
                {
                    try {
                        animacjaSpecjalneWysiadanie(windaPasazerowie.get(i));          //wysiadanie animacja i uaktualnienie inforamcji, że dane miejsce jest już wolne
                        wolne[windaPasazerowie.get(i).indexWwindzie] = true;

                        budynek.pietra[lPodziemnychPieter].semafor.acquire();          //opuszczenie semafora by miec indywidualny dostep do kolejki do ktorej zmierza przesiadajacy sie pasazer

                        if(budynek.pietra[lPodziemnychPieter].semafor.availablePermits() == 0)
                            System.out.println("Blokowanie windy nr: " + nrWindy);

                        System.out.println("Pasazer z windy nr: " + nrWindy + " o nr: " + windaPasazerowie.get(i).nrPasazera + " przesiadl sie na: " + pom + " jedzie na " + windaPasazerowie.get(i).pietroKon);
                        windaPasazerowie.get(i).pietroPocz = 0;

                        if (windaPasazerowie.get(i).pietroKon <= maxGora / 2 && windaPasazerowie.get(i).pietroKon > 0)                //przesiadka jezeli z windy  do windy nr 1
                        {
                            windaPasazerowie.get(i).czyPrzesiadka = false;                                                            //stwierdzenie, ze dane miejsce w windzie bedzie wolene
                            budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].kolejka1.add(windaPasazerowie.get(i));             //dodanie paszera do kolejki na pietrze

                            animacjaLicznik(budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].licznikLabel1, ++budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].licznik1);

                        } else if (windaPasazerowie.get(i).pietroKon > maxGora / 2)                                                   //przesiadka jezeli z windy  do windy nr 2
                        {
                            windaPasazerowie.get(i).czyPrzesiadka = false;                                                            //stwierdzenie, ze dane miejsce w windzie bedzie wolene
                            budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].kolejka2.add(windaPasazerowie.get(i));             //dodanie paszera do kolejki na pietrze

                            animacjaLicznik(budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].licznikLabel2, ++budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].licznik2);

                        } else if (windaPasazerowie.get(i).pietroKon < 0)                                                             //przesiadka jezeli z windy  do windy nr 2
                        {
                            windaPasazerowie.get(i).czyPrzesiadka = false;                                                            //stwierdzenie, ze dane miejsce w windzie bedzie wolene
                            budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].kolejka0.add(windaPasazerowie.get(i));             //dodanie paszera do kolejki na pietrze

                            animacjaLicznik(budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].licznikLabel0, ++budynek.pietra[aktualnePietro + zmGlobalne.lpieterPod].licznik0);
                        }

                        windaPasazerowie.remove(i);                              //usuniecie paszera z windy
                        lPasazerow--;
                        i--;

                        if(budynek.pietra[lPodziemnychPieter].semafor.availablePermits() == 0)
                            System.out.println("Zwalnianie windy nr: " + nrWindy);

                        budynek.pietra[lPodziemnychPieter].semafor.release();    //podniesienie semafora po zakonczeniu operacji nad kolejka

                    }catch (InterruptedException e){e.printStackTrace();}
                }
                else        //jezeli pasazer po prostu chce dostac sie na pietro 0
                {
                    System.out.println("Pasazer z windy nr: " + nrWindy+" o nr: " + windaPasazerowie.get(i).nrPasazera + " wysiadl na: " + pom);

                    animacjaZwykleWysiadanie(windaPasazerowie.get(i));          //wysiadanie animacja i uaktualnienie inforamcji, że dane miejsce jest już wolne
                    wolne[windaPasazerowie.get(i).indexWwindzie] = true;        //stwierdzenie, ze dane miejsce w windzie bedzie wolene

                    windaPasazerowie.remove(i);
                    lPasazerow--;

                    zmGlobalne.zwiekszObsluzonychPasazerow();                   //ilu w sumie pasazerow zostalo obsluzonych w 100%

                    i--;
                }
            }
            else if(windaPasazerowie.get(i).pietroKon == pom)
            {
                System.out.println("Pasazer z windy nr: " + nrWindy+" o nr: "+windaPasazerowie.get(i).nrPasazera  +" wysiadl na: " + pom);

                animacjaZwykleWysiadanie(windaPasazerowie.get(i));             //wysiadanie animacja i uaktualnienie inforamcji, że dane miejsce jest już wolne
                wolne[windaPasazerowie.get(i).indexWwindzie] = true;           //stwierdzenie, ze dane miejsce w indzie bedzie wolene

                windaPasazerowie.remove(i);                //usuniecie pasazera z windy
                lPasazerow--;

                zmGlobalne.zwiekszObsluzonychPasazerow();  //ilu w sumie pasazerow zostalo obsluzonych w 100%

                i--;
            }
        }
    }

    void ruchWindy()                //dzialanie windy
    {
            int tmp = 0;
            wezwanieWindy();        //pierwsze "uruchomienie" windy
            wypiszWezwania();
            wzieciePasazerow();

            animacjaWindy();
            poprzedniePietro = aktualnePietro;
            boolean czyPrzerwanie = false;
            while (dziala)
            {
                if (lPasazerow == 0)                    //ustalenie w jakim kierunku bedzie jechala winda
                {
                    if (maxWzawanie > aktualnePietro)
                        kierunek = 1;
                    else
                        kierunek = -1;

                    if (nrWindy == 0)
                        aktualnePietro = maxWzawanie;
                    else
                        aktualnePietro = maxWzawanie;
                }
                if (kierunek == 1) {
                    System.out.println("");
                    System.out.println("nr " + nrWindy + " Jade do gory");
                    System.out.println("");
                    kolejnoscPrzystankowGora();                             //ustalenie nastepnego przystanku
                    if (windaPasazerowie.size() != 0)                       //wysiadanie pasazerow gdy winda jedzie do gory
                    {
                        animacjaWindy();
                        poprzedniePietro = aktualnePietro;
                        wysiadanie();
                    }
                } else if (kierunek == -1) {
                    System.out.println("");
                    System.out.println("nr " + nrWindy + " Jade do dolu");
                    System.out.println("");
                    kolejnoscPrzystankowDol();                              //ustalenie nastepnego przystanku
                    if (windaPasazerowie.size() != 0)                       //wysiadanie pasazerow, gdy winda jedzie do dolu
                    {
                        animacjaWindy();
                        poprzedniePietro = aktualnePietro;
                        wysiadanie();
                    }
                }

                animacjaWindy();
                poprzedniePietro = aktualnePietro;

                wzieciePasazerow();

                if (lPasazerow == 0)
                    kierunek = 0;

                wezwanieWindy();
                wypiszWezwania();

                if (kierunek == 1 && czyWszyscyWdol() && lPasazerow > 0)            //winda jechala w gore ale aktualnie wszyscy w windzie (pasazerowie w windzie maja pierszenstwo) chca w dol
                    kierunek = -1;

                else if (kierunek == -1 && czyWszyscyWgore() && lPasazerow > 0)     //winda jechala w dol ale aktualnie wszyscy w windzie  chca do gory, idk w t w
                    kierunek = 1;

                if (windaWezwania.size() == 0 && lPasazerow == 0)                   //przejscie w tryb oczekiwania jezeli dany typ wind obsluzyl wszystkich paszerow na swoich pietrach
                {
                    if (nrWindy == 1)
                        ZmGlobalne.stanWindy1 = 0;
                    else if (nrWindy == 2)
                        ZmGlobalne.stanWindy2 = 0;
                    else
                        ZmGlobalne.stanWindy0 = 0;

                    System.out.println("Winda nr: " + nrWindy + " przechodze w stan uspienia.");
                }

                if (windaWezwania.size() != 0 || lPasazerow != 0)                   //wybudzanie sie wind danego rodzaju, jezeli pojawil sie nowy paszer do obslugi przez dany typ windy
                {
                    if (nrWindy == 1 && ZmGlobalne.stanWindy1 == 0) {
                        ZmGlobalne.stanWindy1 = 1;
                        System.out.println("Winda nr: " + nrWindy + " obudzila sie.");
                    } else if (nrWindy == 2 && ZmGlobalne.stanWindy2 == 0) {
                        ZmGlobalne.stanWindy2 = 1;
                        System.out.println("Winda nr: " + nrWindy + " obudzila sie.");
                    } else if (nrWindy == 0 && ZmGlobalne.stanWindy0 == 0) {
                        ZmGlobalne.stanWindy0 = 1;
                        System.out.println("Winda nr: " + nrWindy + " obudzila sie.");
                    }
                }

                /*if (zmGlobalne.obsluzeniPasazerowie >= zmGlobalne.pasazerowieDoObslugi)         //jezeli wszyscy paszerowie, ktorzy zostali dodanie w czasie pracy wind zostali obsluzeni,
                {                                                                               //to proces odpowiedzialne za symulacje sie koncza
                    System.out.println("Wszyscy pasazerowie ( " + zmGlobalne.obsluzeniPasazerowie + " ) zostali obsluzeni v2 nr:"+nrWindy);

                    break;
                }*/
                tmp++;
                System.out.println("tmp =  " + tmp + " Winda nr: " + nrWindy + " aktualne p " + aktualnePietro + " kierunek: " + kierunek + " liczba pasazerow: " + lPasazerow);
                System.out.println("---------------------------------------------------------------");


                while (windaWezwania.size() == 0 && lPasazerow == 0 && dziala)                            //tryb oczekiwania przez windy na ewentualene wybudzenie (pojawienie sie pasazera do obslugi)
                {
                    try {
                        sleep(1);
                        wezwanieWindy();
                        /*if (zmGlobalne.obsluzeniPasazerowie >= zmGlobalne.pasazerowieDoObslugi) //jezeli wszyscy paszerowie, ktorzy zostali dodani to proces sie konczy
                        {
                            System.out.println("Wszyscy pasazerowie ( " + zmGlobalne.obsluzeniPasazerowie + " ) zostali obsluzeni, nr: "+ nrWindy);
                            break;
                        }*/

                    } catch (InterruptedException e) {e.printStackTrace();
                        //throw new RuntimeException("Koniec windy nr"+nrWindy);
                        dziala = false;
                        break;
                    }
                }
            }
    }

    void wypiszWezwania()                       //wypisanie na jakie pietra zostala wezwana winda
    {
        System.out.println("\nWezwania windy nr: " + nrWindy);
        for(Integer e : windaWezwania)
            System.out.println(e);
        System.out.println("Wezwania windy koniec");
    }

    public void run()
    {
        System.out.println("Utworzono winde nr " + nrWindy);

        ruchWindy();
    }
}