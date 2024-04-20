package com.mycompany.unogame;

import javax.sound.sampled.*;

public class Music extends Thread  {
    private Clip clip;
    private FloatControl volume;
    private static boolean turnedOff = false;

    public Music(String path) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(
                    getClass().getResourceAsStream(path));
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if(turnedOff){
                pauseMusic();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean isTurnedOff(){
        return turnedOff;
    }

    public void run() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopMusic() {
        clip.stop();
    }
    
    public void pauseMusic(){
        float min = volume.getMinimum();
        volume.setValue(min);
        turnedOff = true;
    }
    
    public void resumeMusic(){
        float max = volume.getMaximum();
        volume.setValue(max);
        turnedOff = false;
    }
}
