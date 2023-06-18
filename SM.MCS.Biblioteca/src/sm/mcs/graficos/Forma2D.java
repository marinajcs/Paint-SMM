package sm.mcs.graficos;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * Interfaz Forma2D.
 * 
 * Representa una forma bidimensional que se puede dibujar en un 
 * contexto gráfico, y que servirá para definir el tipo de lista de 
 * formas del lienzo.
 * Extiende la interfaz Shape para y proporciona un método de dibujo 
 * que será implementado en cada clase de figura 2D.
 * 
 * @author Marina Jun Carranza Sánchez
 */
public interface Forma2D extends Shape{
    
    
    /**
     * Sobrecarga del método toString()
     *
     * @return La nueva cadena de caracteres
     */
    public String toString();
    
    /**
     * Devuelve el color del trazo con el que se dibujará la figura.
     * 
     * @return color de trazo
     */
    public Color getColor_trazo();

    /**
     * Establece el color el trazo con el que se dibujará la figura.
     * 
     * @param color_trazo color del trazo
     */
    public void setColor_trazo(Color color_trazo);

    /**
     * Devuelve el trazo con el que se dibujará la figura.
     * 
     * @return color de trazo
     */
    public Stroke getTrazo();

    /**
     * Establece el trazo con el que se dibujará la figura.
     * 
     * @param trazo el trazo
     */
    public void setTrazo(Stroke trazo);

    /**
     * Devuelve el estado de la variable de control del alisado de bordes.
     * 
     * @return booleano alisado
     */
    public boolean isAlisado();

    /**
     * Indica si se aplicará un alisado de bordes o no a la figura.
     * 
     * @param alisado booleano de alisado
     */
    public void setAlisado(boolean alisado);

    /**
     * Devuelve el objeto composite que establece la transparencia de la figura.
     * 
     * @return composición
     */
    public Composite getTransparencia();

    /**
     * Establece la transparencia con la que se dibujará la figura.
     * 
     * @param transparencia composición
     */
    public void setTransparencia(Composite transparencia);

    /**
     * Devuelve el color del relleno con el que se dibujará la figura.
     * 
     * @return color el relleno
     */
    public Color getColor_relleno();

    /**
     * Establece el color del relleno con el que se dibujará la figura.
     * 
     * @param color_relleno color del relleno
     */
    public void setColor_relleno(Color color_relleno);

    /**
     * Devuelve el tipo de relleno con el que se dibujará la figura.
     * 
     * 0: sin relleno
     * 1: relleno liso
     * 2: relleno con degradado horizontal
     * 3: relleno con degradado vertical
     * 
     * @return tipo de relleno
     */
    public int getRelleno();

    /**
     * Establece el tipo de relleno con el que se dibujará la figura.
     * 
     * 0: sin relleno
     * 1: relleno liso
     * 2: relleno con degradado horizontal
     * 3: relleno con degradado vertical
     * 
     * @param relleno tipo de relleno
     */
    public void setRelleno(int relleno);
    
    /**
     * Devuelve el rectángulo que contiene/delimita la figura.
     * 
     * Este rectángulo gris de trazo discontinuo será invocado cuando
     * se quiera seleccionar la figura para editarla.
     * 
     * @return el bounding box
     */
    Rectangulo2D getBoundingBox();
    /**
     * Método de dibujo de la forma en un contexto gráfico 2D.
     *
     * @param g2d objeto Graphics2D en el que se realizará el dibujo.
     */
    void draw(Graphics2D g2d);
}
