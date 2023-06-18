package sm.mcs.graficos;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Clase Línea2D.
 * 
 * Representa una línea.
 * 
 * @author Marina Jun Carranza Sánchez
 */

public class Linea2D extends java.awt.geom.Line2D.Float implements Forma2D {

    /**
     * Atributo que guarda el color del trazo (borde) de la figura.
     */
    private Color color_trazo = null;
    
    /**
     * Atributo que guarda las características del trazo de la figura.
     * Dicho trazo puede tener un grosor distinto y ser continuo o discontinuo.
     */
    private Stroke trazo;
    
    /**
     * Variable que indica si se va a aplicar un alisado de bordes o no.
     */
    private boolean alisado;
    
    /**
     * Atributo que guarda las características de la transparencia de la figura.
     * Puede modificarse el grado de transparencia con un slider.
     */
    private Composite transparencia;
    
    /**
     * Ancho y alto de una hipotética caja delimitadora que contiene dicha figura.
     * Se calculan para obtener el ancho y alto aproximados de la figura.
     */
    private float anchoAprox = 0, altoAprox = 0;
    
    /**
     * Constructor de la clase
     * @param p1 punto inicial de la línea
     * @param p2 punto final de la línea
     * @param colTrz color del trazo
     * @param trz trazo
     * @param alis alisado
     * @param transp transparencia
     */
    public Linea2D(Point2D p1, Point2D p2, Color colTrz, Stroke trz, 
                   boolean alis, Composite transp) {
        setLine(p1, p2);
        color_trazo = colTrz;
        trazo = trz;
        alisado = alis;
        transparencia = transp;
    }
    
    /**
     * Establece una nueva localización para la figura.
     * @param pos Posición donde se va a desplazar
     */
    public void setLocation(Point2D pos) {
        double dx = pos.getX() - this.getX1();
        double dy = pos.getY() - this.getY1();
        Point2D newp2 = new Point2D.Double(this.getX2() + dx, this.getY2() + dy);
        this.setLine(pos, newp2);
    }

    /**
     * Devuelve true si un punto se encuentra contenido en la línea o cercano a ella.
     * @param p punto con el que comparar la distancia
     * @return si @p se encuentra cerca de la línea o contenido en ella o no
     */
    public boolean isNear(Point2D p) {
        // Caso p1=p2 (punto)
        if (this.getP1().equals(this.getP2())) {
            return this.getP1().distance(p) <= 2.0;
        }
        // Caso p1!=p2
        return this.ptLineDist(p) <= 2.0;
    }    
    
    /**
     * Devuelve true si la línea contiene el punto (o está lo suficientemente cerca de él)
     * @param p punto con el que comparar la distancia
     * @return si @p se encuentra cerca de la línea o contenido en ella o no
     */
    @Override
    public boolean contains(Point2D p) {
        return isNear(p);
    }
    
    /**
     * Sobrecarga del método toString().
     * @return La nueva cadena de caracteres
     */
    @Override
    public String toString(){
        return "Línea2D";
    }

    /**
     * Devuelve el color del trazo con el que se dibujará la figura.
     * 
     * @return color de trazo
     */
    @Override
    public Color getColor_trazo() {
        return color_trazo;
    }

    /**
     * Establece el color el trazo con el que se dibujará la figura.
     * 
     * @param color_trazo color del trazo
     */
    @Override
    public void setColor_trazo(Color color_trazo) {
        this.color_trazo = color_trazo;
    }

    /**
     * Devuelve el trazo con el que se dibujará la figura.
     * 
     * @return color de trazo
     */
    @Override
    public Stroke getTrazo() {
        return trazo;
    }

    /**
     * Establece el trazo con el que se dibujará la figura.
     * 
     * @param trazo el trazo
     */
    @Override
    public void setTrazo(Stroke trazo) {
        this.trazo = trazo;
    }

    /**
     * Devuelve el estado de la variable de control del alisado de bordes.
     * 
     * @return booleano alisado
     */
    @Override
    public boolean isAlisado() {
        return alisado;
    }

    /**
     * Indica si se aplicará un alisado de bordes o no a la figura.
     * 
     * @param alisado booleano de alisado
     */
    @Override
    public void setAlisado(boolean alisado) {
        this.alisado = alisado;
    }

    /**
     * Devuelve el objeto composite que establece la transparencia de la figura.
     * 
     * @return composición
     */
    @Override
    public Composite getTransparencia() {
        return transparencia;
    }

    /**
     * Establece la transparencia con la que se dibujará la figura.
     * 
     * @param transparencia composición
     */
    @Override
    public void setTransparencia(Composite transparencia) {
        this.transparencia = transparencia;
    }
    
    /**
     * Devuelve el color del relleno con el que se dibujará la figura.
     * 
     * Método nulo para líneas, ya que no pueden estar rellenas.
     * 
     * @return color el relleno
     */
    public Color getColor_relleno(){
        return null;
    }

    /**
     * Establece el color del relleno con el que se dibujará la figura.
     * 
     * Método nulo para líneas, ya que no pueden estar rellenas.
     * 
     * @param color_relleno color del relleno
     */
    public void setColor_relleno(Color color_relleno){ 
    }

    /**
     * Devuelve el tipo de relleno con el que se dibujará la figura.
     * 
     * Método nulo para líneas, ya que no pueden estar rellenas.
     * 
     * @return tipo de relleno
     */
    @Override
    public int getRelleno(){
        return 0;
    }

    /**
     * Establece el tipo de relleno con el que se dibujará la figura.
     * 
     * Método nulo para líneas, ya que no pueden estar rellenas.
     * 
     * @param relleno tipo de relleno
     */
    @Override
    public void setRelleno(int relleno){
    }

    /**
     * Devuelve el rectángulo que contiene/delimita la figura.
     * 
     * Este rectángulo gris de trazo discontinuo será invocado cuando
     * se quiera seleccionar la figura para editarla.
     * 
     * @return el bounding box
     */
    @Override
    public Rectangulo2D getBoundingBox(){
        float minX = Math.min(x1, x2);
        float minY = Math.min(y1, y2);
        float maxX = Math.max(x1, x2);
        float maxY = Math.max(y1, y2);
        
        anchoAprox = maxX - minX;
        altoAprox = maxY - minY;
        float[] patron = {5.0f, 5.0f};
        
        Stroke trz = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
                                     10.0f, patron, 0.0f);
        Composite cmp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
        
        Rectangulo2D bbox = new Rectangulo2D(minX, minY, anchoAprox, altoAprox,
                                             Color.GRAY, Color.GRAY, trz, 0, false, cmp);
        return bbox;
    }
    
    /**
     * Método que dibuja la forma, según sus atributos almacenados.
     * @param g2d los gráficos 2D
     */
    @Override
    public void draw(Graphics2D g2d){
        g2d.setColor(color_trazo);
        g2d.setStroke(trazo);
        g2d.setComposite(transparencia);

        if (alisado) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        g2d.draw(this);
    }
}
