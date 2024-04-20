package com.mycompany.unogame;


import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Player {

    private final int id;
    private final String name;
    private final List<Card> cards;
    private final List<JLabel> cardsImg;
    private MouseAdapter listener;
    private final String icon;
    private final int x;
    private final int y;
    private final int dx;
    private final int dy;
    private final boolean real;
    private boolean madeMove;
    private final boolean vertical;
    private final JPanel jPanel;
    private boolean uno = false;
    private int finalScore;
 
    public Player(int id, String name, int score, String icon, Deck deck, boolean real, boolean vertical, int x, int y, int dx, int dy, JPanel jPanel, MouseAdapter listener){
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.real = real;
        this.vertical = vertical;
        this.jPanel = jPanel;
        finalScore = score;
        
        cards = new ArrayList<>();
        cardsImg = new ArrayList<>();
        if(real){
            this.listener = listener;
        }
        for(int i = 0; i < 7; i++){
            TakeCard(deck);
        }
    }
    
    public List<JLabel> getCardsImg(){
        return cardsImg;
    }
    
    public List<Card> getCards(){
        return cards;
    }
    
    public boolean isReal(){
        return real;
    }
    
    public boolean isUno(){
        return uno;
    }
    
    public boolean isVertical(){
        return vertical;
    }
    
    public void setUno(boolean uno){
        this.uno = uno;
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public int getDx(){
        return dx;
    }
    
    public int getDy(){
        return dy;
    }
    
    public int getId(){
        return id;
    }
    
    public String getName(){
        return name;
    }
    
    public String getIcon(){
        return icon;
    }
    
    public boolean isMadeMove(){
        return madeMove;
    }
    
    public void setMadeMove(boolean madeMove){
        this.madeMove = madeMove;
    }

       
   
    public void TakeCard(Deck deck){
        int count = deck.getCount();
        if(!deck.getCards().isEmpty()){
            Card _card = deck.getCards().get(count);
            cards.add(_card);
            JLabel _jlabel = new JLabel();
            String temp;
            if(real){
                temp = "/" + _card.toString() + ".png";
                _jlabel.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource(temp)).getImage().getScaledInstance(60, 90, Image.SCALE_SMOOTH)));
                _jlabel.setName(temp);
                _jlabel.addMouseListener(listener);
                _jlabel.addMouseListener(new MouseAdapter() {
                    public void mouseReleased(MouseEvent e) {
                        deleteCards();
                        paintCards();
                    }

                    boolean isEntered = false;

                    public void mouseEntered(MouseEvent e) {
                        if(!_jlabel.isEnabled()){
                            return;
                        }
                        _jlabel.setLocation(_jlabel.getX(), _jlabel.getY() - 10);
                        isEntered = true;
                    }

                    public void mouseExited(MouseEvent e) {
                        if(!_jlabel.isEnabled()){
                            return;
                        }
                        if(!isEntered){
                            return;
                        }
                        _jlabel.setLocation(_jlabel.getX(), _jlabel.getY() + 10);
                        isEntered = false;
                    }
                });
            }else if(vertical){
                temp = "/default2.png";
                _jlabel.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource(temp)).getImage().getScaledInstance(90, 60, Image.SCALE_SMOOTH)));
            }else{
                temp = "/default1.png";
                _jlabel.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource(temp)).getImage().getScaledInstance(60, 90, Image.SCALE_SMOOTH)));
            }
            cardsImg.add(_jlabel);
            deck.getCards().remove(count);
        }
    }
    
    public void deleteCards(){
        for(var _card : cardsImg){
            jPanel.remove(_card);
        } 
    }
    
    public void paintCards(){
        int x = getX();
        int y = getY();
        int dx = getDx();
        int dy = getDy();
        for(var _card : cardsImg){
            _card.setLocation(x, y);
            if(vertical){
                _card.setSize(90, 60);
            }else{
                _card.setSize(60, 90);
            }
            x += dx;
            y += dy;
            jPanel.add(_card);
        } 
    }
    
    public void repaintCards(){
        deleteCards();
        paintCards();
    }
    
    
    public String findMostColor(){
        HashMap<String, Integer> colorCount = new HashMap<>();
        for (Card card : cards) {
            String color = card.getColor().name();
            if(!color.equals("NONE")){
                if (!colorCount.containsKey(color)) {
                    colorCount.put(color, 1);
                } else {
                    colorCount.put(color, colorCount.get(color) + 1);
                }
            }
        }
        String mostFrequentColor = "";
        int maxCount = 0;
        for (String color : colorCount.keySet()) {
            int count = colorCount.get(color);
            if (count > maxCount) {
                mostFrequentColor = color;
                maxCount = count;
            }
        }
        return mostFrequentColor;
    }
    
    public void ShowMyDeck(){
        for (Card card: cards) {
            System.out.println(card.Print());
        }
        System.out.println(cards.size());
    }
    
    public int Score(){
        for(var card: cards){
            if(card.getColor().name().equals("NONE")){
                finalScore += 50;
            }else if(card.getValue().name().equals("REVERSE") || card.getValue().name().equals("TAKETWO") || card.getValue().name().equals("SKIP")){
                finalScore += 20;
            }else{
                finalScore += card.getValue().ordinal();
            }
        }
        return finalScore;
    }
}
