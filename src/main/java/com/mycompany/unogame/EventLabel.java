package com.mycompany.unogame;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;

public class EventLabel {
    
    private final int delay = 300;
    private final int bigLabelWidth = 340;
    private final int smallLabelWidth = 135;
    private final int labelHeight = 80;
    private int flickerCount = 0;
    private ActionListener eventLabelTask;
    private Timer timer;
    
    public void eventLabel(JLabel jLabelEventCard, String message, Player player){
        if(eventLabelTask == null){
            eventLabelTask = new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent evt){
                    if(flickerCount == 4){
                        jLabelEventCard.setVisible(false);
                        flickerCount = 0;
                        timer.stop();
                    }else{
                        if(flickerCount % 2 == 0)
                            jLabelEventCard.setVisible(false);
                        else
                            jLabelEventCard.setVisible(true);
                        flickerCount++;
                    }
                    }
                };  
           timer = new Timer(delay, eventLabelTask);
        }
        jLabelEventCard.setText(message);
        switch(message){
            case("REVERSE"):
                jLabelEventCard.setFont(jLabelEventCard.getFont().deriveFont(72f));
                jLabelEventCard.setBounds((jLabelEventCard.getParent().getWidth() - bigLabelWidth) / 2, (jLabelEventCard.getParent().getHeight() - labelHeight) / 2, bigLabelWidth, 200);
                break;
            default:
                jLabelEventCard.setFont(jLabelEventCard.getFont().deriveFont(58f));
                if(player.isVertical()){
                    jLabelEventCard.setBounds(player.getX() - 15, player.getY(), smallLabelWidth, labelHeight);
                }else
                    jLabelEventCard.setBounds(player.getX(), player.getY(), smallLabelWidth, labelHeight);
                break;
        }
        jLabelEventCard.setVisible(true);
        timer.start();
    }
    
}
