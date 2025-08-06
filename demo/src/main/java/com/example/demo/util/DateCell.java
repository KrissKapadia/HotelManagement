package com.example.demo.util;

import java.time.LocalDate;

public class DateCell extends javafx.scene.control.DateCell {
    private LocalDate minDate;

    public DateCell(LocalDate minDate) {
        this.minDate = minDate;
    }

    @Override
    public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        setDisable(empty || item.isBefore(minDate));
    }
}