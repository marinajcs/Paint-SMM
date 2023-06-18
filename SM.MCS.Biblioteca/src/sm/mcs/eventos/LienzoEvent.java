package sm.mcs.eventos;

import java.awt.Color;
import java.awt.Shape;
import java.util.EventObject;
import sm.mcs.graficos.Forma2D;

/**
 *
 * @author Marina Jun Carranza Sánchez
 */
public class LienzoEvent extends EventObject {

    /**
     * La figura del evento de lienzo.
     */
    private Forma2D forma;
    
    /**
     * El color de la figura.
     */
    private Color color;

    /**
     * Constructor de LienzoEvent.
     * 
     * @param source el lienzo origen
     * @param forma la figura del evento
     */
    public LienzoEvent(Object source, Forma2D forma) {
        super(source);
        this.forma = forma;
    }

    /**
     * Devuelve la forma del evento lienzo.
     * 
     * @return la figura
     */
    public Shape getForma() {
        return forma;
    }

    /**
     * Devuelve el color de la figura.
     * 
     * @return el color
     */
    public Color getColor() {
        return color;
    }
}
