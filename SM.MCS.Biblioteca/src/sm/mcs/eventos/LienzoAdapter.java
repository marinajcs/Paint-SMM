package sm.mcs.eventos;

/**
 *
 * @author Marina Jun Carranza Sánchez
 */
public class LienzoAdapter implements LienzoListener {

    /**
     * Evento de añadir una nueva figura al lienzo.
     * 
     * Será implementado por el manejador de lienzo.
     * 
     * @param evt objeto de la clase propia de eventos de lienzo
     */
    public void shapeAdded(LienzoEvent evt) {
    }

    /**
     * Evento de cambiar propiedad de figura del lienzo.
     * 
     * Será implementado por el manejador de lienzo.
     * 
     * @param evt objeto de la clase propia de eventos de lienzo
     */
    public void propertyChange(LienzoEvent evt) {
    }

    /**
     * Evento de actualizar el color de píxel actual.
     * 
     * Será implementado por el manejador de lienzo.
     * 
     * @param rgb cadena de valores RGB
     */
    public void updateRGBPixel(String rgb) {
    }
}
