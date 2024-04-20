package com.mycompany.unogame;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import javax.swing.JLabel;
import java.util.Map;

public class PlayerLabelStatus {
    private static final Font font = CustomFont.getCustomFont(24);
    private static final Map attributes = font.getAttributes();
    private static final Color active = new Color(15,218,36);
    private static final Color inactive = new Color(255,187,11);
    
    public static void activePlayerLabel(Map<String, JLabel> playerLabels, Integer playerId){
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_TWO_PIXEL);
        playerLabels.get("player" + playerId).setFont(font.deriveFont(attributes));
        playerLabels.get("player" + playerId).setForeground(active);
    }
    
    public static void inactivePlayerLabel(Map<String, JLabel> playerLabels, Integer playerId){
        attributes.put(TextAttribute.UNDERLINE, -1);
        playerLabels.get("player" + playerId).setFont(font.deriveFont(attributes));
        playerLabels.get("player" + playerId).setForeground(inactive);
    }
}
