package sm.mcs.iu;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupTable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.shape.Ellipse;
import sm.mcs.eventos.LienzoEvent;
import sm.mcs.eventos.LienzoListener;
import sm.mcs.graficos.Linea2D;
import sm.mcs.graficos.Curva2D;
import sm.mcs.graficos.Elipse2D;
import sm.mcs.graficos.Forma2D;
import sm.mcs.graficos.Rectangulo2D;
import sm.mcs.graficos.Smile2D;
import sm.mcs.graficos.TrazoLibre2D;

/**
 * Clase Lienzo2D.
 *
 * Representa el espacio donde se dibujarán las distintas figuras y se
 * realizarán distintas operaciones gráficas. Proporciona métodos para dibujar
 * líneas, rectángulos, elipses, etc, así como abrir imágenes sobre las que se
 * puede pintar.
 *
 * @author Marina Jun Carranza Sánchez
 */
public class Lienzo2D extends javax.swing.JPanel {

    /**
     * La figura o forma actual.
     */
    Point2D pt0 = new Point2D.Double(0.0, 0.0);
    private Forma2D forma;

    /**
     * El tipo de figura o forma actual. Con valores del 1 al 6, que
     * corresponden a las siguientes formas, en orden creciente: 0: línea 1:
     * rectángulo 2: elipse 3: trazo libre 4: curva 5: cara sonriente.
     * Inicialmente con valor a 0.
     */
    private int tipoForma = 0;

    /**
     * El color del trazo. Inicialmente negro.
     */
    private Color color_trazo = Color.BLACK;

    /**
     * El color del relleno. Inicialmente negro.
     */
    private Color color_relleno = Color.BLACK;

    /**
     * Variable de control del relleno. Inicialmente desactivada.
     *
     * 0: sin relleno 1: relleno liso 2: relleno con degradado horizontal 3:
     * relleno con degradado vertical
     */
    private int relleno;

    /**
     * Las coordenadas iniciales y finales usadas para gestionar los eventos del
     * ratón.
     */
    float x1, x2, y1, y2;

    /**
     * El vector de figuras donde se van almacenando las dibujadas en el lienzo.
     */
    private List<Forma2D> vShape = new ArrayList();

    /**
     * Variable de control de la opción "mover figuras". Inicialmente
     * desactivada.
     */
    private Boolean mover = false;

    /**
     * Variable de control de la opción "editar figuras". Inicialmente
     * desactivada.
     */
    private Boolean editar = false;

    /**
     * La imagen que se va a pintar sobre el lienzo.
     */
    private BufferedImage img;

    /**
     * Variable de control de la fase de pintado de curvas. Inicialmente en la
     * primera fase (dibujo de línea), siendo la segunda el desplazamiento del
     * punto de control para trazar la curva.
     */
    private Boolean pintandoCurva = false;

    /**
     * El trazo para poder manipular el grosor y tipo de este. Inicialmente
     * continuo y con grosor 1.
     */
    private Stroke trazo = new BasicStroke(1);

    /**
     * Variable de control de la transparencia. Inicialmente desactivada.
     */
    private Boolean estaTransparente = false;

    /**
     * Variable de control del alisado. Inicialmente desactivada.
     */
    private Boolean estaAlisado = false;

    /**
     * La composición del lienzo usada para la transparencia. Inicialmente
     * desactivada.
     */
    private Composite cmp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);

    /**
     * La lista de eventos listener personalizados del lienzo. Inicialmente
     * vacía.
     */
    ArrayList<LienzoListener> lienzoEventListeners = new ArrayList();

    /**
     * El valor RGB del pixel actual sobre el que se encuentra el cursor.
     */
    public String rgb_val = "";

    /**
     * El área de recorte o clip donde se puede dibujar. Es un rectángulo de
     * dimensiones 300x300.
     */
    Rectangle areaClip = new Rectangle(0, 0, 300, 300);

    /**
     * El rectángulo que delimitará las figuras que vayan a editarse.
     */
    Rectangulo2D bbox = new Rectangulo2D(0.0f, 0.0f, 0.0f, 0.0f, Color.BLACK, Color.BLACK, trazo, 0, estaAlisado, cmp);

    /**
     * Constructor de la clase. Establece el tamaño a 300x300.
     */
    public Lienzo2D() {
        this.forma = new Linea2D(pt0, pt0, Color.BLACK, trazo, estaAlisado, cmp);
        initComponents();
        this.setSize(300, 300);
    }

    /**
     * Devuelve el tipo de forma o figura. Tiene valores del 1 al 6, que
     * corresponden a las siguientes formas, en orden creciente: 0: línea 1:
     * rectángulo 2: elipse 3: trazo libre 4: curva 5: cara sonriente.
     *
     * @return el tipo de forma
     */
    public int getTipoForma() {
        return tipoForma;
    }

    /**
     * Establece el tipo de forma. Tiene valores del 1 al 6, que corresponden a
     * las siguientes formas, en orden creciente: 0: línea 1: rectángulo 2:
     * elipse 3: trazo libre 4: curva 5: cara sonriente.
     *
     * @param tipoForma el tipo de figura
     */
    public void setTipoForma(int tipoForma) {
        this.tipoForma = tipoForma;
    }

    /**
     * Devuelve el área de recorte.
     *
     * @return el área de clip
     */
    public Rectangle getAreaClip() {
        return areaClip;
    }

    /**
     * Establece el área de recorte del lienzo.
     *
     * @param areaClip el área de recorte
     */
    public void setAreaClip(Rectangle areaClip) {
        this.areaClip = areaClip;
    }

    /**
     * Devuelve el trazo.
     *
     * @return el trazo
     */
    public Stroke getTrazo() {
        return trazo;
    }

    /**
     * Establece el trazo.
     *
     * @param trazo el trazo
     */
    public void setTrazo(Stroke trazo) {
        this.trazo = trazo;
    }

    /**
     * Devuelve la lista de figuras.
     *
     * @return lista de formas
     */
    public List<Forma2D> getShapeList() {
        return vShape;
    }

    /**
     * Establece una nueva lista de figuras.
     *
     * @param vShape nueva lista de formas
     */
    public void setShapeList(List<Forma2D> vShape) {
        this.vShape = vShape;
    }

    /**
     * Devuelve la variable de control de la fase de trazado de curva.
     *
     * @return variable de control de la fase de trazado de curva
     */
    public Boolean getPintandoCurva() {
        return pintandoCurva;
    }

    /**
     * Devuelve la imagen asociada a este lienzo, incluyendo las formas
     * dibujadas si así se indica. En caso de que se opte por incluir las formas
     * dibujadas, la imagen devuelta será una copia de la original sobre la que
     * se dibujará las figuras incluidas en el lienzo. En caso contrario, se
     * devolverá una referencia a la imagen actual.
     *
     * @param pintaVector establece si se dibujarán o no las figuras del lienzo
     * en la imagen devuelta. Si <code>true</code>, la imagen incluirá las
     * formas dibujadas.
     * @return la imagen asociada a este lienzo, incluyendo las formas dibujadas
     * si así se indica.
     */
    public BufferedImage getImage(Boolean pintaVector) {
        if (pintaVector) {
            BufferedImage imgout = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
            this.paint(imgout.createGraphics());
            return (imgout);
        } else {
            return img;
        }
    }

    /**
     * Asigna una imagen que se le pasa como parámetro al método. Si esta imagen
     * es un objeto no nulo, se establece el tamaño por defecto del lienzo,
     * usando las dimensiones de la propia imagen.
     *
     * @param img imagen a asignar
     */
    public void setImage(BufferedImage img) {
        this.img = img;
        if (img != null) {
            setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
        }
    }

    /**
     * Devuelve la imagen del lienzo.
     *
     * @return la imagen
     */
    public BufferedImage getImg() {
        return img;
    }

    /**
     * Establece la imagen del lienzo.
     *
     * @param img la imagen
     */
    public void setImg(BufferedImage img) {
        this.img = img;
    }

    /**
     * Devuelve el color actualmente seleccionado.
     *
     * @return el color
     */
    public Color getColorTrazo() {
        return color_trazo;
    }

    /**
     * Establece el color seleccionado del lienzo.
     *
     * @param color el color a asignar
     */
    public void setColorTrazo(Color color) {
        this.color_trazo = color;
    }

    /**
     * Devuelve el color actualmente seleccionado.
     *
     * @return el color
     */
    public Color getColorRelleno() {
        return color_relleno;
    }

    /**
     * Establece el color seleccionado del lienzo.
     *
     * @param color el color a asignar
     */
    public void setColorRelleno(Color color) {
        this.color_relleno = color;
    }

    /**
     * Devuelve si está activa la opción de relleno o no.
     *
     * @return el estado actual de la opción de relleno
     */
    public int getRelleno() {
        return relleno;
    }

    /**
     * Establece el estado de la opción de relleno.
     *
     * @param relleno el estado a asignar de la opción de relleno
     */
    public void setRelleno(int relleno) {
        this.relleno = relleno;
    }

    /**
     * Devuelve si está activa la opción de transparencia o no.
     *
     * @return el estado actual de la opción de transparencia
     */
    public Boolean getEstaTransparente() {
        return estaTransparente;
    }

    /**
     * Establece el estado de la opción de transparencia.
     *
     * @param estaTransparente el estado a asignar de la opción de transparencia
     */
    public void setEstaTransparente(Boolean estaTransparente) {
        this.estaTransparente = estaTransparente;
    }

    /**
     * Devuelve si está activa la opción de alisado.
     *
     * @return el estado actual de la opción de alisado.
     */
    public Boolean getEstaAlisado() {
        return estaAlisado;
    }

    /**
     * Establece el estado de la opción de alisado.
     *
     * @param estaAlisado el estado a asignar de la opción de alisado
     */
    public void setEstaAlisado(Boolean estaAlisado) {
        this.estaAlisado = estaAlisado;
    }

    /**
     * Establece el tipo de forma a dibujar.
     *
     * @param forma la forma a asignar
     */
    public void setForma(Forma2D forma) {
        this.forma = forma;
    }

    /**
     * Devuelve la forma actual.
     *
     * @return forma actual
     */
    public Forma2D getForma() {
        return forma;
    }

    /**
     * Devuelve si está activa la opción de mover figuras o no.
     *
     * @return si está seleccionada la opción "mover"
     */
    public Boolean getMover() {
        return mover;
    }

    /**
     * Establece el estado de la opción de mover figuras.
     *
     * @param mover el estado a asignar de la opción "mover"
     */
    public void setMover(Boolean mover) {
        this.mover = mover;
    }

    /**
     * Devuelve si está activa la opción de editar figuras o no.
     *
     * @return si está seleccionada la opción "editar"
     */
    public Boolean getEditar() {
        return editar;
    }

    /**
     * Establece el estado de la opción de editar figuras.
     *
     * @param editar el estado a asignar de la opción "editar"
     */
    public void setEditar(Boolean editar) {
        this.editar = editar;
    }

    /**
     * Devuelve el objeto Composite del lienzo.
     *
     * @return la composición del lienzo
     */
    public Composite getComposite() {
        return cmp;
    }

    /**
     * Modifica el objeto Composite del lienzo.
     *
     * @param cmp el objeto a asignar
     */
    public void setComposite(Composite cmp) {
        this.cmp = cmp;
    }

    /**
     * Devuelve la figura seleccionada en un determinado punto.
     *
     * @param p el punto donde se coge la figura
     * @return la figura seleccionada
     */
    private Forma2D getFiguraSeleccionada(Point2D p) {
        for (Forma2D s : vShape) {
            if (s.contains(p)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Enlaza el lienzo con el manejador propio que se ha creado.
     *
     * @param listener manejador propio de gestión de eventos de lienzo.
     */
    public void addLienzoListener(LienzoListener listener) {
        if (listener != null) {
            lienzoEventListeners.add(listener);
        }
    }

    /**
     * Notifica el evento de añadir una nueva figura.
     *
     * Si la lista de listeners de eventos del lienzo no está vacía, se irá
     * pasando el evento a todos los listeners de la lista.
     *
     * @param evt objeto de la clase propia de eventos de lienzo
     */
    private void notifyShapeAddedEvent(LienzoEvent evt) {
        if (!lienzoEventListeners.isEmpty()) {
            for (LienzoListener listener : lienzoEventListeners) {
                listener.shapeAdded(evt);
            }
        }
    }

    /**
     * Notifica el evento de cambio de propiedad de la figura.
     *
     * Si la lista de listeners de eventos del lienzo no está vacía, se irá
     * pasando el evento a todos los listeners de la lista.
     *
     * @param evt objeto de la clase propia de eventos de lienzo
     */
    private void notifyPropertyChangeEvent(LienzoEvent evt) {
        if (!lienzoEventListeners.isEmpty()) {
            for (LienzoListener listener : lienzoEventListeners) {
                listener.propertyChange(evt);
            }
        }
    }

    /**
     * Notifica el evento de cambio de píxel y actualización de su valor RGB.
     *
     * Si la lista de listeners de eventos del lienzo no está vacía, se irá
     * pasando el evento a todos los listeners de la lista.
     *
     * @param rgb la cadena de valores RGB
     */
    private void notifyRGBPixel(String rgb) {
        if (!lienzoEventListeners.isEmpty()) {
            for (LienzoListener listener : lienzoEventListeners) {
                listener.updateRGBPixel(rgb);
            }
        }
    }

    /**
     * Método para detener la edición de formas.
     *
     * Se usa para dejar de editar las formas cuando se presiona un botón de
     * figura (o selección) de la interfaz.
     */
    public void stopEdicion() {
        editar = false;
        vShape.remove(bbox);
        bbox = new Rectangulo2D(0.0f, 0.0f, 0.0f, 0.0f, Color.BLACK, Color.BLACK, trazo, 0, estaAlisado, cmp);
    }

    /**
     * Método de edición de atributos.
     *
     * Suponiendo que se ha seleccionado un figura, se le pasa como parámtero al
     * método para modificar sus atributos libremente.
     *
     * @param f2d la forma
     */
    public void editarForma(Forma2D f2d) {
        f2d.setColor_trazo(color_trazo);
        f2d.setTrazo(trazo);
        f2d.setAlisado(this.getEstaAlisado());
        f2d.setTransparencia(cmp);

        if (!(f2d instanceof Linea2D)) {
            f2d.setColor_relleno(color_relleno);
            f2d.setRelleno(relleno);
        }
    }

    /**
     * El método de paint sobrecargado del lienzo. A diferencia de versiones
     * anteriores, delega la responsabilidad de guardar los atributos de dibujo
     * a cada figura, y se encarga exclusivamente de establecer la establecer la
     * imagen, el área de recorte y de llamar al método de dibujo de cada figura
     * del vector.
     *
     * @param g el objeto Graphics
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        if (img != null) {
            g2d.drawImage(img, 0, 0, this);
            areaClip.setRect(0, 0, img.getWidth(), img.getHeight());
        }
        g2d.setClip(areaClip);

        for (Forma2D s : vShape) {
            s.draw(g2d);
        }

        if (editar && (bbox.getWidth() != 0.0f && bbox.getHeight() != 0.0f)) {
            editarForma(forma);
            repaint();
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setPreferredSize(new java.awt.Dimension(300, 300));
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        vShape.remove(bbox);
        bbox = new Rectangulo2D(0.0f, 0.0f, 0.0f, 0.0f, Color.BLACK, Color.BLACK, trazo, 0, estaAlisado, cmp);

        if (mover) {
            forma = this.getFiguraSeleccionada(evt.getPoint());
        } else if (editar) {
            forma = this.getFiguraSeleccionada(evt.getPoint());
            if (forma != null && (bbox.getWidth() == 0.0f && bbox.getHeight() == 0.0f)) {
                bbox = forma.getBoundingBox();
                vShape.add(bbox);
            }
        } else {
            x1 = evt.getPoint().x;
            y1 = evt.getPoint().y;
            switch (tipoForma) {
                case 1:
                    forma = new Linea2D(evt.getPoint(), evt.getPoint(), this.getColorTrazo(),
                            this.getTrazo(), this.getEstaAlisado(), this.getComposite());
                    break;
                case 2:
                    forma = new Rectangulo2D(x1, y1, 0, 0, this.getColorTrazo(), this.getColorRelleno(),
                            this.getTrazo(), this.getRelleno(), this.getEstaAlisado(), this.getComposite());
                    break;
                case 3:
                    forma = new Elipse2D(x1, y1, 0, 0, this.getColorTrazo(), this.getColorRelleno(),
                            this.getTrazo(), this.getRelleno(), this.getEstaAlisado(), this.getComposite());
                    break;
                case 4:
                    forma = new TrazoLibre2D(x1, y1, this.getColorTrazo(), this.getColorRelleno(),
                            this.getTrazo(), this.getRelleno(), this.getEstaAlisado(), this.getComposite());
                    break;
                case 5:
                    if (this.pintandoCurva == false) {
                        forma = new Curva2D(x1, y1, x1, y1, x1, y1, this.getColorTrazo(), this.getColorRelleno(),
                                this.getTrazo(), this.getRelleno(), this.getEstaAlisado(), this.getComposite());
                    }
                    break;
                case 6:
                    forma = new Smile2D(x1, y1, this.getColorTrazo(), this.getColorRelleno(),
                            this.getTrazo(), this.getRelleno(), this.getEstaAlisado(), this.getComposite());
                    break;
            }
            vShape.add(forma);
            if (!pintandoCurva) {
                notifyShapeAddedEvent(new LienzoEvent(this, forma));
            }
        }


    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        x2 = evt.getPoint().x;
        y2 = evt.getPoint().y;

        if (mover && !editar) {
            if (forma != null && forma instanceof Rectangulo2D) {
                ((Rectangulo2D) forma).setLocation(evt.getPoint());
            } else if (forma != null && forma instanceof Elipse2D) {
                ((Elipse2D) forma).setLocation(evt.getPoint());
            } else if (forma != null && forma instanceof Linea2D) {
                ((Linea2D) forma).setLocation(evt.getPoint());
            } else if (forma != null && forma instanceof TrazoLibre2D) {
                ((TrazoLibre2D) forma).setLocation(x2, y2);
            } else if (forma != null && forma instanceof Curva2D) {
                ((Curva2D) forma).setLocation(evt.getPoint());
            } else if (forma != null && forma instanceof Smile2D) {
                ((Smile2D) forma).setLocation(x2, y2);
            }
        } else if (!editar) {
            switch (tipoForma) {
                case 1:
                    ((Linea2D) forma).setLine(((Line2D) forma).getP1(), evt.getPoint());
                    break;
                case 2:
                    ((Rectangulo2D) forma).setRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
                    break;
                case 3:
                    ((Elipse2D) forma).setFrame(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
                    break;
                case 4:
                    ((TrazoLibre2D) forma).lineTo(x2, y2);
                    break;
                case 5:
                    if (this.pintandoCurva == false) {
                        ((Curva2D) forma).setCurve(((QuadCurve2D) forma).getP1(), ((QuadCurve2D) forma).getP1(), evt.getPoint());
                    } else {
                        ((Curva2D) forma).setCurve(((QuadCurve2D) forma).getX1(), ((QuadCurve2D) forma).getY1(), evt.getPoint().getX(),
                                evt.getPoint().getY(), ((QuadCurve2D) forma).getX2(), ((QuadCurve2D) forma).getY2());
                    }
                    break;
            }
        }
        this.repaint();
    }//GEN-LAST:event_formMouseDragged

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        if (tipoForma == 5) {
            if (pintandoCurva == false) {
                pintandoCurva = true;
            } else {
                pintandoCurva = false;
            }
        }
    }//GEN-LAST:event_formMouseReleased

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        Robot robot;
        try {
            robot = new Robot();
            int x = evt.getX();
            int y = evt.getY();

            Point cursorLocation = MouseInfo.getPointerInfo().getLocation();
            Color pixelColor = robot.getPixelColor(cursorLocation.x, cursorLocation.y);

            int red = pixelColor.getRed();
            int green = pixelColor.getGreen();
            int blue = pixelColor.getBlue();

            rgb_val = "POS:(" + x + ", " + y + ") | RGB:[" + red + ", " + green + ", " + blue + "]";
            this.notifyRGBPixel(rgb_val);
        } catch (AWTException exception) {
            exception.printStackTrace();
        }
    }//GEN-LAST:event_formMouseMoved

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked


    }//GEN-LAST:event_formMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
