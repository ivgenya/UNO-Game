package com.mycompany.unogame;

import java.awt.Color;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Game {

    private final List<Player> players;
    private final Deck deck;
    private final JButton deckBtn;
    private final JButton unoBtn;
    private final JLabel deckLbl;
    private final JPanel jPanel;
    private Player currentPlayer;
    private boolean reverse = true;
    private String changedColor;
    private final JLabel changedColorLabel;
    private final JLabel directionLbl;
    private Timer timer;
    private final Music musicThread;
    private EventLabel el = new EventLabel();

    public Game(int playersCount, String myName, String playerIcon, List<Integer> scores, JLabel jLabel, JPanel jPanel, JLabel changedColorLabel, JButton deckBtn, JButton unoBtn, JLabel directionLbl, MouseAdapter listener, Music musicThread){
        this.deckLbl = jLabel;
        this.jPanel = jPanel;
        this.changedColorLabel = changedColorLabel;
        this.deckBtn = deckBtn;
        this.unoBtn = unoBtn;
        this.directionLbl = directionLbl;
        this.musicThread = musicThread;
        deck = new Deck();
        deck.Create();
        changedColor = deck.getCards().get(deck.getCount()).toString();
        deck.getCards().remove(deck.getCount());
        if(myName == null || myName.isEmpty()){
            myName = "New player";
        }
        players = new ArrayList<>();
        players.add(new Player(1, myName, scores.get(0), playerIcon, deck, true, false, 300, 610, 45, 0, jPanel, listener));
        currentPlayer = players.get(0);
        switch(playersCount){
            case 2 -> players.add(new Player(2, "MISHA", scores.get(1), "/player2.png", deck, false, false, 300, 30, 45, 0, jPanel, listener));
            case 3 -> {
                players.add(new Player(2, "IVAN", scores.get(1), "/player2.png", deck, false, true, 25, 220, 0, 30, jPanel, listener));
                players.add(new Player(3, "MISHA",scores.get(2), "/player3.png", deck, false, false, 300, 30, 45, 0, jPanel, listener));
            }
            case 4 -> {
                players.add(new Player(2,"IVAN", scores.get(1), "/player2.png", deck, false, true, 25, 220, 0, 30,  jPanel, listener));
                players.add(new Player(3, "MISHA", scores.get(2),"/player3.png" ,deck, false, false, 300, 30, 45, 0,jPanel, listener));
                players.add(new Player(4, "MASHA", scores.get(3),"/player4.png", deck, false, true, 1165, 220, 0, 30,  jPanel, listener));
            }     
        }
    }
    
    public List<Player> getPlayers(){
        return players;
    }
    
    public Deck getDeck(){
        return deck;
    }
    
    public Player getCurrentPlayer(){
        return currentPlayer;
    }
    
    public void setReverse(boolean reverse){
        this.reverse = reverse;
    }
    
    public String getChangedColor(){
        return changedColor;
    }
    
    public void setChangedColor(String color){ 
        this.changedColor = color;
    }
    
    public boolean isReverse(){
        return reverse;
    }
    
    public Player nextPlayer(){
        int curId = currentPlayer.getId();
        if(reverse){
            if(curId < players.size()){
                curId++;
                for(Player p : players){
                    if(p.getId() == curId){
                        currentPlayer = p;
                    }
                }
            }else{
                currentPlayer = players.get(0);
            }
        }else{
            if(curId > 1){
                curId--;
                for(Player p : players){
                    if(p.getId() == curId){
                        currentPlayer = p;
                    }
                }
            }else{
                currentPlayer = players.get(players.size() - 1);
            }    
        }
        return currentPlayer;
    }

    public void makeMove(Player player, Map<String, JLabel> playerLabels){
        if(players.get(0).getCards().size() == 1 && !players.get(0).isUno()){
            players.get(0).TakeCard(deck);
            players.get(0).TakeCard(deck); 
            for(int k = 0; k < players.get(0).getCards().size(); k++){
                players.get(0).getCardsImg().get(k).setEnabled(false);
            }
            players.get(0).repaintCards();
            jPanel.repaint();
            players.get(0).setUno(false);
            unoBtn.setEnabled(false);
        }
        
        List<Card> cards = player.getCards();
        List<JLabel> cardsImg = player.getCardsImg();
        if(!cards.isEmpty()){
            if(!player.isReal()){
                JLabel dropTarg = deckLbl;
                boolean flag = false;
                String tempTarg = dropTarg.getName();
                String[] parts = tempTarg.split("_");
                String TargetColor = parts[0].split("/")[1];
                String TargetValue = parts[1].split("\\.")[0];
                if(TargetColor.equals("NONE")) {
                    TargetColor = changedColor;
                }
                for(int i = 0; i < cards.size(); i++){
                    String SourceColor = cards.get(i).getColor().name();
                    String SourceValue = cards.get(i).getValue().name();

                    if(SourceColor.equals(TargetColor) || SourceValue.equals(TargetValue) || SourceColor.equals("NONE")){
                        switch (SourceValue) {
                            case "TAKEFOUR" -> {
                                changedColor = player.findMostColor();
                                SourceColor = changedColor;
                                PlayerLabelStatus.inactivePlayerLabel(playerLabels, currentPlayer.getId());
                                Player playerNext = nextPlayer();
                                el.eventLabel(playerLabels.get("event"), "+4", playerNext);
                                for(int j = 0; j < 4; j++){
                                    playerNext.TakeCard(deck);
                                }       
                                if(currentPlayer.isReal()){ 
                                    for(int k = 0; k < currentPlayer.getCards().size(); k++){
                                        currentPlayer.getCardsImg().get(k).setEnabled(false);
                                    }
                                }       
                                playerNext.repaintCards();
                            }
                            case "CHANGECOLOR" -> {
                                changedColor = player.findMostColor();
                                SourceColor = changedColor;
                            }
                            case "TAKETWO" -> {
                                    PlayerLabelStatus.inactivePlayerLabel(playerLabels, currentPlayer.getId());
                                    Player playerNext = nextPlayer();
                                    el.eventLabel(playerLabels.get("event"), "+2", playerNext);
                                    for(int j = 0; j < 2; j++){
                                        playerNext.TakeCard(deck);
                                    }       
                                    if(currentPlayer.isReal()){
                                        for(int k = 0; k < currentPlayer.getCards().size(); k++){
                                            currentPlayer.getCardsImg().get(k).setEnabled(false);
                                        }
                                    }       playerNext.repaintCards();
                                }
                            case "REVERSE" -> {
                                el.eventLabel(playerLabels.get("event"), "REVERSE", currentPlayer);
                                reverse = !reverse;
                                if(reverse){
                                    directionLbl.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/currs.png")).getImage().getScaledInstance(51, 51, Image.SCALE_SMOOTH)));
                                }else{
                                    directionLbl.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/currs_reversed.png")).getImage().getScaledInstance(51, 51, Image.SCALE_SMOOTH)));
                                }
                            }
                            case "SKIP" -> {
                                PlayerLabelStatus.inactivePlayerLabel(playerLabels, currentPlayer.getId());
                                nextPlayer();
                                el.eventLabel(playerLabels.get("event"), "SKIP", currentPlayer);
                            }
                        }
                        String temp = "/" + cards.get(i).toString() + ".png";
                        dropTarg.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource(temp)).getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH)));
                        dropTarg.setName(temp);
                        settingBgColor(SourceColor);
                        PlayerLabelStatus.inactivePlayerLabel(playerLabels, currentPlayer.getId());
                        nextPlayer();
                        jPanel.remove(cardsImg.get(i));
                        jPanel.repaint();
                        cards.remove(i);
                        cardsImg.remove(i);
                        player.repaintCards();
                        flag = true;
                        
                        if(cards.isEmpty()){
                            SummingUpResults(player);
                        }
                        gameProcess(playerLabels);
                        break;
                    }
                }
                if(!flag){
                    player.TakeCard(deck);
                    player.repaintCards();
                    PlayerLabelStatus.inactivePlayerLabel(playerLabels, currentPlayer.getId());
                    nextPlayer();
                    jPanel.repaint();
                    gameProcess(playerLabels);
                }
            }
        }
    }
    
    public void settingBgColor(String color){
        switch (color){
            case "RED" -> changedColorLabel.setBackground(Color.RED);
            case "YELLOW" -> changedColorLabel.setBackground(Color.YELLOW);
            case "GREEN" -> changedColorLabel.setBackground(Color.GREEN);
            case "BLUE" -> changedColorLabel.setBackground(Color.BLUE);
        }
    }
    
    public void gameProcess(Map<String, JLabel> playerLabels){
        PlayerLabelStatus.activePlayerLabel(playerLabels, currentPlayer.getId());
        if(!currentPlayer.isReal()){
                timer = new Timer(2000, (ActionEvent e) -> makeMove(currentPlayer, playerLabels));
                timer.setRepeats(false);
                timer.start();
        }else{
            for(int i = 0; i < currentPlayer.getCards().size(); i++){
                currentPlayer.getCardsImg().get(i).setEnabled(true); 
            }
            deckBtn.setEnabled(true);
        }
    }
    
    Object[] options = {"Continue", "End game"};
    public void SummingUpResults(Player winner){
        boolean isOver = false;
        String loser = "";
        StringBuilder result = new StringBuilder(String.format("%s IS WINNER!\n",winner.getName()));
        for(var _player: players){
            int score = _player.Score();
            result.append(String.format("%s: %d points \n", _player.getName(), score));
            if(score >= 200){
                isOver = true;
                loser = _player.getName();
            }
        }
        try (FileWriter writer = new FileWriter("results.txt", false)) {
            writer.write(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(isOver){
            result.append(String.format("GAME OVER\n%s IS LOSER!", loser));
            JOptionPane.showMessageDialog(null,result);
            Window[] windows = Window.getWindows();
            for (Window w : windows){
                w.dispose();
            }
            new Menu().setVisible(true);
            musicThread.stopMusic();
        }else{
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showOptionDialog (null, result ,"Results",dialogButton, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            Window[] windows = Window.getWindows();
            for (Window w : windows){
                w.dispose();
            }

            if(dialogResult == JOptionPane.YES_OPTION){
                List<Integer> scores = new ArrayList<>();
                for(var _player: players){
                    scores.add(_player.Score());
                }
                musicThread.stopMusic();
                new GameStage(players.size(), scores,  players.get(0).getIcon(), players.get(0).getName()).setVisible(true);
            }else{
                musicThread.stopMusic();
                new Menu().setVisible(true);
            }
        }
    }
}

    

        
    

