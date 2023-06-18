package sm.mcs.graficos;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Clase Rectángulo2D.
 * 
 * Representa un rectángulo.
 *
 * @author Marina Jun Carranza Sánchez
 */
public class Rectangulo2D extends java.awt.geom.Rectangle2D.Float implements Forma2D {

    /**
     * Atributo que guarda el color del trazo (borde) de la figura.
     */
    private Color color_trazo = null;

    /**
     * Atributo que guarda el color del relleno de la figura.
     */
    private Color color_relleno;

    /**
     * Atributo que guarda las características del trazo de la figura. Dicho
     * trazo puede tener un grosor distinto y ser continuo o discontinuo.
     */
    private Stroke trazo;

    /**
     * Variable entera que indica el tipo de relleno de la figura.
     * 
     * 0: sin relleno
     * 1: relleno liso
     * 2: relleno con degradado horizontal
     * 3: relleno con degradado vertical
     */
    private int relleno;

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
     * Constructor de la clase
     *
     * @param x coordenada x del punto de origen
     * @param y coordenada y del punto de origen
     * @param w anchura
     * @param h altura
     * @param colTrz color del trazo
     * @param colRell color del relleno
     * @param trz trazo
     * @param rell tipo de relleno
     * @param alis alisado
     * @param transp transparencia
     */
    public Rectangulo2D(float x, float y, float w, float h, Color colTrz, Color colRell,
                        Stroke trz, int rell, boolean alis, Composite transp) {
        setRect(x, y, w, h);
        color_trazo = colTrz;
        color_relleno = colRell;
        trazo = trz;
        relleno = rell;
        alisado = alis;
        transparencia = transp;
    }

    /**
     * Establece una nueva localización para la figura.
     *
     * @param pos Posición donde se va a desplazar
     */
    public void setLocation(Point2D pos) {
        setFrame(pos.getX(), pos.getY(), getBounds2D().getWidth(), getBounds2D().getHeight());
    }

    /**
     * Sobrecarga del método toString()
     *
     * @return La nueva cadena de caracteres
     */
    @Override
    public String toString() {
        return "Rectángulo2D";
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
     * @return color el relleno
     */
    public Color getColor_relleno() {
        return color_relleno;
    }

    /**
     * Establece el color del relleno con el que se dibujará la figura.
     * 
     * @param color_relleno color del relleno
     */
    public void setColor_relleno(Color color_relleno) {
        this.color_relleno = color_relleno;
    }

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
    @Override
    public int getRelleno() {
        return relleno;
    }

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
    @Override
    public void setRelleno(int relleno) {
        this.relleno = relleno;
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
        float minX = (float) this.getMinX();
        float minY = (float) this.getMinY();

        float anchoAprox = (float) (this.getWidth()+2.0);
        float altoAprox = (float) (this.getHeight()+2.0);
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
     *
     * @param g2d los gráficos 2D
     */
    @Override
    public void draw(Graphics2D g2d) {
        
        g2d.setStroke(trazo);
        g2d.setComposite(transparencia);

        if (alisado) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        if (relleno != 0) {
            if (relleno == 1) {
                g2d.setPaint(color_relleno);
            } else if (relleno == 2) {
                GradientPaint grad = new GradientPaint(0, 0, color_trazo, (float) getWidth(), 0, color_relleno);
                g2d.setPaint(grad);
            } else {
                GradientPaint grad = new GradientPaint(0, 0, color_trazo, (float) getHeight(), 0, color_relleno);
                g2d.setPaint(grad);
            }
            g2d.fill(this);
        }
        
        g2d.setPaint(color_trazo);
        g2d.draw(this);
    }
}
