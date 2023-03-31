package com.example.projektwindyjavafx;

import javafx.scene.control.Alert;

public class Dialog {

    public static void ostrzezenieBrakuDanych()
    {
        Alert brakDanychInfo = new Alert(Alert.AlertType.ERROR);
        brakDanychInfo.setTitle("Brak danych!");
        brakDanychInfo.setHeaderText("Wprowadz wszystkie dane.");
        brakDanychInfo.showAndWait();
    }

    public static void ostrzezenieZaDuzoWind()
    {
        Alert brakDanychInfo = new Alert(Alert.AlertType.ERROR);
        brakDanychInfo.setTitle("Za duzo wind!");
        brakDanychInfo.setHeaderText("Zmniejsz liczbe wind.");
        brakDanychInfo.showAndWait();
    }

}
