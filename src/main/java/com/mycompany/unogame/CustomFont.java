package com.mycompany.unogame;

import java.awt.Font;
import java.io.InputStream;

public class CustomFont {
    
    private static Font customFont = null;

    public static Font getCustomFont(int size) {
        if (customFont == null) {
            try {
                InputStream fontStream = CustomFont.class.getResourceAsStream("/FredokaOne-Regular.ttf");
                customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            } catch (Exception e) {
                e.printStackTrace();
                customFont = new Font("Arial", Font.PLAIN, size);
            }
        }
        return customFont.deriveFont(Font.PLAIN, size);
    }
}
