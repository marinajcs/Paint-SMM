package sm.mcs.eventos;

import java.util.EventListener;

/**
 *
 * @author Marina
 */
public interface LienzoListener extends EventListener {

    /**
     * Evento de añadir una nueva figura al lienzo.
     * 
     * Será implementado por LienzoAdapter.
     * 
     * @param evt objeto de la clase propia de eventos de lienzo
     */
    public void shapeAdded(LienzoEvent evt);

    /**
     * Evento de cambiar propiedad de figura del lienzo.
     * 
     * Será implementado por LienzoAdapter.
     * 
     * @param evt objeto de la clase propia de eventos de lienzo
     */
    public void propertyChange(LienzoEvent evt);
    
    /**
     * Evento de actualizar el color de píxel actual.
     * 
     * Será implementado por LienzoAdapter.
     * 
     * @param rgb cadena de valores RGB
     */
    public void updateRGBPixel(String rgb);
    
}
