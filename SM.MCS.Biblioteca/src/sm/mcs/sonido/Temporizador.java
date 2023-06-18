package sm.mcs.sonido;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 * Clase para el temporizar de audios (ampliación).
 * 
 * NO terminada. No implementada.
 * 
 * @author Marina Jun Carranza Sánchez
 */
public class Temporizador extends Thread {
    private int secs;
    private boolean exec;
    private String txt;
    private JLabel label;
  
    public Temporizador(){
        secs = 0;
        exec = false;
        this.txt = "0:0";
    }
    
    public void startTimer(JLabel label){
        secs = 0;
        exec = true;
        this.label = label;
    }
    
    public void stopTimer(){
        exec = false;
    }
    
    public String getTxt(){
        return txt;
    }
    
    @Override
    public void run(){
        while (exec){
            try {
                Thread.sleep(1000);
                secs++;
                int min = 0;
                int seg = 0;
                if (secs >= 60){
                    min = secs / 60;
                    seg = secs - (60*min);
                }
                txt = min + ":" + seg;
                label.setText(txt);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
