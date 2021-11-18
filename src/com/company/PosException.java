package com.company;

public class PosException extends Throwable {
    public PosException(String card_out_of_bounds) {
        super(card_out_of_bounds);
    }
}
