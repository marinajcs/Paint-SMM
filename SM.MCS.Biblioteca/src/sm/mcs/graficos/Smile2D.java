package sm.mcs.graficos;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Clase Smile2D.
 * 
 * Representa una carita sonriente.
 *
 * @author Marina Jun Carranza Sánchez
 */
public class Smile2D extends java.awt.geom.Area implements Forma2D {

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
     * Los distintos elementos que componen la cara sonriente.
     */
    private Area cara, ojo_izq, ojo_dch, sonrisa;

    /**
     * Las coordenadas (x,y) donde se crea la figura.
     */
    private float x, y;
    
    /**
     * Ancho y alto de una hipotética caja delimitadora que contiene dicha figura.
     * Se calculan para obtener el ancho y alto aproximados de la figura.
     */
    private float anchoAprox = 0, altoAprox = 0;

    /**
     * Constructor de la clase. Se crean las distintas áreas que contienen las
     * figuras que componen la cara sonriente: los ojos son elipses, al igual
     * que el contorno de la cara, mientras que la sonrisa es un arco 2D. A la
     * cara se le "extraen" las demás figuras para darle el aspecto de la figura
     * completa resultante.
     *
     * @param px coordenada x del punto de origen
     * @param py coordenada y del punto de origen
     * @param colTrz color del trazo
     * @param colRell color del relleno
     * @param trz trazo
     * @param rell tipo de relleno
     * @param alis alisado
     * @param transp transparencia
     */
    public Smile2D(float px, float py, Color colTrz, Color colRell,
            Stroke trz, int rell, boolean alis, Composite transp) {
        super(new Area(new Ellipse2D.Float(px, py, 100, 100)));
        this.x = px;
        this.y = py;
        ojo_izq = new Area(new Ellipse2D.Float(x + 20, y + 25, 20, 20));
        ojo_dch = new Area(new Ellipse2D.Float(x + 60, y + 25, 20, 20));
        sonrisa = new Area(new Arc2D.Float(x + 20, y + 40, 60, 40, 180, 180, Arc2D.CHORD));

        this.subtract(ojo_izq);
        this.subtract(ojo_dch);
        this.subtract(sonrisa);
        
        color_trazo = colTrz;
        color_relleno = colRell;
        trazo = trz;
        relleno = rell;
        alisado = alis;
        transparencia = transp;
        
        anchoAprox = 100.0f;
        altoAprox = 100.0f;
    }

    /**
     * Devuelve la coordenada x de la figura.
     *
     * @return valor de x
     */
    public float getX() {
        return x;
    }

    /**
     * Establece el valor de la coordenada x
     *
     * @param x valor de x
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Devuelve el valor de la coordenada y.
     *
     * @return valor de y
     */
    public float getY() {
        return y;
    }

    /**
     * Establece el valor de la coordenada y.
     *
     * @param y valor de y
     */
    public void setY(float y) {
        this.y = y;
    }

    public void setLocation(float x2, float y2) {
        AffineTransform at;
        at = AffineTransform.getTranslateInstance((x2 - ((Smile2D) this).getBounds2D().getX()), (y2 - ((Smile2D) this).getBounds2D().getY()));
        ((Smile2D) this).transform(at);
    }

    /**
     * Sobrecarga del método toString()
     *
     * @return La nueva cadena de caracteres
     */
    @Override
    public String toString() {
        return "Smile2D";
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
        float[] patron = {5.0f, 5.0f};
        
        Stroke trz = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
                                     10.0f, patron, 0.0f);
        Composite cmp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
        
        Rectangulo2D bbox = new Rectangulo2D(x, y, anchoAprox, altoAprox,
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
                GradientPaint grad = new GradientPaint(0, 0, color_trazo, (float) anchoAprox, 0, color_relleno);
                g2d.setPaint(grad);
            } else {
                GradientPaint grad = new GradientPaint(0, 0, color_trazo, (float) altoAprox, 0, color_relleno);
                g2d.setPaint(grad);
            }
            g2d.fill(this);
        }
        
        g2d.setPaint(color_trazo);
        g2d.draw(this);
    }
}
