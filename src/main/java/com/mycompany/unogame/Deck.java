package com.mycompany.unogame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private final List<Card> cards;
    private static final int count = 0;

    public Deck(){
        cards = new ArrayList<>();
    }

    //создание колоды
    public void Create(){
        Card.Color[] colors = Card.Color.values();
        Card.Value[] values = Card.Value.values();
        for(int i = 0; i < Card.Color.values().length - 1; i++){
            cards.add(new Card(colors[i], values[0]));
            cards.add(new Card(colors[4], values[13]));
            cards.add(new Card(colors[4], values[14]));
            for(int j = 1; j <= 12; j++){
                cards.add(new Card(colors[i], values[j]));
                cards.add(new Card(colors[i], values[j]));
            }
        }
        //перемешать колоду
        Collections.shuffle(cards);
        for(int i = 0; i < cards.size(); i++){
            if((!cards.get(i).getColor().name().equals("NONE")) && (!cards.get(i).getValue().name().equals("TAKETWO")) && (!cards.get(i).getValue().name().equals("REVERSE")) && (!cards.get(i).getValue().name().equals("SKIP"))){
                Collections.swap(cards, 0, i);
                break;
            }
        }
    }

    public int getCount(){
        return count;
    }
    
    public List<Card> getCards(){
        return cards;
    }
    
    public void Show(){
        for (Card card: cards) {
            System.out.println(card.Print());
        }
        System.out.println(cards.size());
    }
}

