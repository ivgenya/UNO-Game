package com.mycompany.unogame;

public class Card {
    
    enum Color {
        RED, YELLOW, GREEN, BLUE, NONE
    }

    enum Value {
        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, SKIP, REVERSE, TAKETWO, TAKEFOUR, CHANGECOLOR
    }

    private final Color color;
    private final Value value;

    public Card(Color color, Value value){
        this.color = color;
        this.value = value;
    }

    public Color getColor() {
        return color;
    }

    public Value getValue() {
        return value;
    }

    public String Print(){
        return getColor() + " " + getValue();
    }
    
    @Override
    public String toString(){
        return color + "_" + value;
    }
}
