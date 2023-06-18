package smm_practica_final;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBuffer;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RescaleOp;
import java.awt.image.ShortLookupTable;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import sm.image.EqualizationOp;
import sm.image.LookupTableProducer;
import sm.mcs.iu.Lienzo2D;
import sm.image.KernelProducer;
import sm.image.SepiaOp;
import sm.image.TintOp;
import sm.image.color.*;
import sm.mcs.eventos.LienzoAdapter;
import sm.mcs.eventos.LienzoEvent;
import sm.mcs.graficos.Curva2D;
import sm.mcs.graficos.Elipse2D;
import sm.mcs.graficos.Forma2D;
import sm.mcs.graficos.Linea2D;
import sm.mcs.graficos.Rectangulo2D;
import sm.mcs.graficos.Smile2D;
import sm.mcs.graficos.TrazoLibre2D;
import sm.mcs.imagen.MiOperador;
import sm.mcs.imagen.PosterizarOp;
import sm.mcs.imagen.RojoOp;
import sm.mcs.imagen.VariarTonosOp;
import sm.mcs.sonido.Temporizador;
import sm.sound.SMClipPlayer;
import sm.sound.SMSoundRecorder;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

/**
 * Clase Ventana Principal
 *
 * @author Marina Jun Carranza Sánchez
 */
public class VentanaPrincipal extends javax.swing.JFrame {

    /**
     * La imagen fuente. Inicialmente null.
     */
    private BufferedImage imgFuente = null;
    
    /**
     * El tipo de filtro a aplicar a la imagen. Inicialmente Media 3x3.
     */
    private String tipoFiltro = "Media 3x3";
    
    /**
     * El espacio de colores seleccionado. Inicialmente RGB.
     */
    private String espColor = "RGB";
    
    private SMClipPlayer player = null;
    private SMSoundRecorder recorder = null;
    private Temporizador timer = new Temporizador();
    private VentanaInternaImagen spline_graph = null;

    /**
     * Constructor de la clase. 
     * 
     * También inicializa y asigna el modelo de Spinner usado para 
     * determinar el grosor del trazo. Tiene configurado por defecto
     * el valor 1 (mínimo) y el máximo es de 20.
     * Asigna la lista de valores posibles al combo box de filtros de 
     * imagen y al de espacio de colores, e inicializa la lista de figuras.
     */
    public VentanaPrincipal() {
        initComponents();
        SpinnerNumberModel spin = new SpinnerNumberModel();
        spin.setMaximum(20);
        spin.setMinimum(1);
        spin.setValue(1);
        grosor.setModel(spin);
        String[] tiposFiltros = {"Media 3x3", "Media 5x5", "Media 7x7",
            "Binomial", "Enfoque", "Relieve", "Laplaciano", "Horizontal 5x1",
            "Horizontal 7x1", "Horizontal 10x1"};
        filtro_menu.setModel(new DefaultComboBoxModel(tiposFiltros));
        String[] espaciosCol = {"RGB", "YCC", "GREY"};
        menu_espacioColor.setModel(new DefaultComboBoxModel(espaciosCol));
        listaFormas.setModel(new DefaultListModel());
        this.setTitle("Aplicación multimedia - SMM");

    }

    /**
     * Devuelve el lienzo de la ventana interna activa actual. Para ello, llama
     * al método de la ventana interna que devuelve el frame seleccionado
     * actualmente.
     *
     * @return el lienzo seleccionado
     */
    public Lienzo2D getLienzoSeleccionado() {
        VentanaInternaImagen vi = (VentanaInternaImagen) panelEscritorio.getSelectedFrame();
        return vi != null ? vi.getLienzo() : null;
    }

    /**
     * Actualiza la barra de estado inferior. Informa sobre la selección actual
     * de los siguientes elementos: el color, el tipo de figura, las opciones de
     * relleno, transparencia y alisado.
     */
    /*
    public void actualizaBarraEstado() {

        String form;
        String col;
        if (getLienzoSeleccionado().getColor() == Color.BLACK) {
            col = "BLACK";
        } else if (getLienzoSeleccionado().getColor() == Color.RED) {
            col = "RED";
        } else if (getLienzoSeleccionado().getColor() == Color.BLUE) {
            col = "BLUE";
        } else if (getLienzoSeleccionado().getColor() == Color.YELLOW) {
            col = "YELLOW";
        } else if (getLienzoSeleccionado().getColor() == Color.GREEN) {//GREEN
            col = "GREEN";
        } else {
            col = getLienzoSeleccionado().getColor().toString();
        }

        if (getLienzoSeleccionado().getTipoForma() == 1) {
            form = "LINE";
        } else if (getLienzoSeleccionado().getTipoForma() == 2) {
            form = "RECTANGLE";
        } else if (getLienzoSeleccionado().getTipoForma() == 3) {
            form = "ELLIPSE";
        } else if (getLienzoSeleccionado().getTipoForma() == 4) {
            form = "FREE";
        } else if (getLienzoSeleccionado().getTipoForma() == 5) {
            form = "CURVE";
        } else {
            form = "SMILE";
        }
        String txt = "Forma: " + form + " | Color: " + col + " | Relleno: "
                + getLienzoSeleccionado().getEstaRelleno().toString()
                + " | Transparencia: " + getLienzoSeleccionado().getEstaTransparente().toString() + " | Alisado: "
                + getLienzoSeleccionado().getEstaAlisado().toString();
        barra_est.setText(txt);
    }
     */
    
    public String getTipoFiltro() {
        return tipoFiltro;
    }

    public void setTipoFiltro(String tipoFiltro) {
        this.tipoFiltro = tipoFiltro;
    }

    public String getEspColor() {
        return espColor;
    }

    public void setEspColor(String espColor) {
        this.espColor = espColor;
    }

    public class MiManejadorLienzo extends LienzoAdapter {

        @Override
        public void shapeAdded(LienzoEvent evt) {
            //System.out.println("Figura " + evt.getForma() + " añadida");
            Forma2D s = (Forma2D) evt.getForma();
            ((DefaultListModel) listaFormas.getModel()).addElement(s);
        }

        @Override
        public void updateRGBPixel(String rgb) {
            barra_est.setText(rgb);
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

        barra_formas = new javax.swing.JToolBar();
        bot_nuevo = new javax.swing.JButton();
        bot_abrir = new javax.swing.JButton();
        bot_guardar = new javax.swing.JButton();
        duplicado_button = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        trazo = new javax.swing.JToggleButton();
        linea = new javax.swing.JToggleButton();
        rectangulo = new javax.swing.JToggleButton();
        elipse = new javax.swing.JToggleButton();
        curva = new javax.swing.JToggleButton();
        seleccion = new javax.swing.JToggleButton();
        smile = new javax.swing.JToggleButton();
        edicion = new javax.swing.JToggleButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        trazo_discontinuo = new javax.swing.JToggleButton();
        panel_col = new javax.swing.JPanel();
        negro = new javax.swing.JToggleButton();
        rojo = new javax.swing.JToggleButton();
        amarillo = new javax.swing.JToggleButton();
        azul = new javax.swing.JToggleButton();
        verde = new javax.swing.JToggleButton();
        more_colors = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        panel_col2 = new javax.swing.JPanel();
        negro1 = new javax.swing.JToggleButton();
        rojo1 = new javax.swing.JToggleButton();
        amarillo1 = new javax.swing.JToggleButton();
        azul1 = new javax.swing.JToggleButton();
        verde1 = new javax.swing.JToggleButton();
        more_colors1 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        bot_rellenar = new javax.swing.JToggleButton();
        degradado_horiz = new javax.swing.JToggleButton();
        degradado_vert = new javax.swing.JToggleButton();
        transparencia = new javax.swing.JToggleButton();
        val_transp = new javax.swing.JSlider();
        alisar = new javax.swing.JToggleButton();
        grosor = new javax.swing.JSpinner();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        abrirSonido_barra = new javax.swing.JButton();
        play_button = new javax.swing.JButton();
        stop_button = new javax.swing.JButton();
        sound_list = new javax.swing.JComboBox<>();
        record_button = new javax.swing.JButton();
        temporizador = new javax.swing.JLabel();
        tiempo_total = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        abrirVideo_barra = new javax.swing.JButton();
        abrirCamara_barra = new javax.swing.JButton();
        capturar_barra = new javax.swing.JButton();
        panel_inf = new javax.swing.JPanel();
        panel_colores = new javax.swing.JPanel();
        separador = new javax.swing.JSeparator();
        panel_est = new javax.swing.JPanel();
        filtro_menu = new javax.swing.JComboBox<>();
        panel_transformaciones = new javax.swing.JPanel();
        contraste = new javax.swing.JButton();
        iluminar = new javax.swing.JButton();
        oscurecer = new javax.swing.JButton();
        cuadratica = new javax.swing.JButton();
        mi_lookup = new javax.swing.JButton();
        negativo = new javax.swing.JButton();
        spline = new javax.swing.JToggleButton();
        spline_a = new javax.swing.JSlider();
        spline_b = new javax.swing.JSlider();
        m_cuadratica = new javax.swing.JSlider();
        slider_miLookup = new javax.swing.JSlider();
        panel_rotescale = new javax.swing.JPanel();
        aumentar = new javax.swing.JButton();
        disminuir = new javax.swing.JButton();
        rotacion = new javax.swing.JButton();
        slider_rotacion = new javax.swing.JSlider();
        barra_est = new javax.swing.JLabel();
        panel_brillocontraste = new javax.swing.JPanel();
        brillo_slider = new javax.swing.JSlider();
        contraste_slider = new javax.swing.JSlider();
        panel_color_rgb = new javax.swing.JPanel();
        bandas = new javax.swing.JButton();
        menu_espacioColor = new javax.swing.JComboBox<>();
        combinar = new javax.swing.JButton();
        panel_tintado = new javax.swing.JPanel();
        tintar = new javax.swing.JButton();
        sepia = new javax.swing.JButton();
        ecualizar = new javax.swing.JButton();
        rojo_efecto = new javax.swing.JButton();
        vignette = new javax.swing.JToggleButton();
        histograma = new javax.swing.JToggleButton();
        slider_tintado = new javax.swing.JSlider();
        slider_posterizar = new javax.swing.JSlider();
        arcoiris = new javax.swing.JSlider();
        slider_rojo = new javax.swing.JSlider();
        slider_miOp = new javax.swing.JSlider();
        slider_sat_col = new javax.swing.JSlider();
        splitPanel = new javax.swing.JSplitPane();
        panelEscritorio = new javax.swing.JDesktopPane();
        panelListaFormas = new javax.swing.JPanel();
        volcado = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        listaFormas = new javax.swing.JList<>();
        barra_archivo = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        nuevo = new javax.swing.JMenuItem();
        abrir = new javax.swing.JMenuItem();
        guardar = new javax.swing.JMenuItem();
        menu_mg = new javax.swing.JMenu();
        rescaleOP = new javax.swing.JMenuItem();
        convolveOP = new javax.swing.JMenuItem();
        affineTransformOP = new javax.swing.JMenuItem();
        lookupOP = new javax.swing.JMenuItem();
        bandcombineOP = new javax.swing.JMenuItem();
        colorspaceOP = new javax.swing.JMenuItem();
        duplicar = new javax.swing.JMenuItem();
        sonido_menu = new javax.swing.JMenu();
        abrirAudio = new javax.swing.JMenuItem();
        sonido_menu1 = new javax.swing.JMenu();
        abrirVideo = new javax.swing.JMenuItem();
        abrirCamara = new javax.swing.JMenuItem();
        capturar = new javax.swing.JMenuItem();
        ayuda_menu = new javax.swing.JMenu();
        acercaDe = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Paint_Básico");

        barra_formas.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        barra_formas.setRollover(true);

        bot_nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevo.png"))); // NOI18N
        bot_nuevo.setToolTipText("nuevo_lienzo");
        bot_nuevo.setFocusable(false);
        bot_nuevo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bot_nuevo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bot_nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bot_nuevoActionPerformed(evt);
            }
        });
        barra_formas.add(bot_nuevo);

        bot_abrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/abrir.png"))); // NOI18N
        bot_abrir.setToolTipText("abrir_imagen");
        bot_abrir.setFocusable(false);
        bot_abrir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bot_abrir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bot_abrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bot_abrirActionPerformed(evt);
            }
        });
        barra_formas.add(bot_abrir);

        bot_guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/guardar.png"))); // NOI18N
        bot_guardar.setToolTipText("guardar");
        bot_guardar.setFocusable(false);
        bot_guardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bot_guardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bot_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bot_guardarActionPerformed(evt);
            }
        });
        barra_formas.add(bot_guardar);

        duplicado_button.setText("Dup");
        duplicado_button.setToolTipText("duplicar");
        duplicado_button.setFocusable(false);
        duplicado_button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        duplicado_button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        duplicado_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                duplicado_buttonActionPerformed(evt);
            }
        });
        barra_formas.add(duplicado_button);
        barra_formas.add(jSeparator1);

        trazo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/trazo.png"))); // NOI18N
        trazo.setToolTipText("trazo_libre");
        trazo.setFocusable(false);
        trazo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        trazo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        trazo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trazoActionPerformed(evt);
            }
        });
        barra_formas.add(trazo);

        linea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/linea.png"))); // NOI18N
        linea.setToolTipText("línea");
        linea.setFocusable(false);
        linea.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        linea.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        linea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineaActionPerformed(evt);
            }
        });
        barra_formas.add(linea);

        rectangulo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rectangulo.png"))); // NOI18N
        rectangulo.setToolTipText("rectángulo");
        rectangulo.setFocusable(false);
        rectangulo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rectangulo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rectangulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rectanguloActionPerformed(evt);
            }
        });
        barra_formas.add(rectangulo);

        elipse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/elipse.png"))); // NOI18N
        elipse.setToolTipText("elipse");
        elipse.setFocusable(false);
        elipse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        elipse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        elipse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                elipseActionPerformed(evt);
            }
        });
        barra_formas.add(elipse);

        curva.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/curva.png"))); // NOI18N
        curva.setToolTipText("curva_1pt");
        curva.setFocusable(false);
        curva.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        curva.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        curva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                curvaActionPerformed(evt);
            }
        });
        barra_formas.add(curva);

        seleccion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/seleccion.png"))); // NOI18N
        seleccion.setToolTipText("seleccionar");
        seleccion.setFocusable(false);
        seleccion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        seleccion.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        seleccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionActionPerformed(evt);
            }
        });
        barra_formas.add(seleccion);

        smile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/smile.png"))); // NOI18N
        smile.setToolTipText("smile");
        smile.setFocusable(false);
        smile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        smile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        smile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smileActionPerformed(evt);
            }
        });
        barra_formas.add(smile);

        edicion.setText("Ed.");
        edicion.setToolTipText("edición");
        edicion.setFocusable(false);
        edicion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        edicion.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        edicion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edicionActionPerformed(evt);
            }
        });
        barra_formas.add(edicion);
        barra_formas.add(jSeparator3);

        trazo_discontinuo.setText("Disc.");
        trazo_discontinuo.setToolTipText("trazo_discontinuo");
        trazo_discontinuo.setFocusable(false);
        trazo_discontinuo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        trazo_discontinuo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        trazo_discontinuo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trazo_discontinuoActionPerformed(evt);
            }
        });
        barra_formas.add(trazo_discontinuo);

        panel_col.setMaximumSize(new java.awt.Dimension(100, 100));
        panel_col.setPreferredSize(new java.awt.Dimension(40, 25));
        panel_col.setLayout(new java.awt.GridLayout(2, 2));

        negro.setBackground(new java.awt.Color(0, 0, 0));
        negro.setSelected(true);
        negro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                negroActionPerformed(evt);
            }
        });
        panel_col.add(negro);

        rojo.setBackground(new java.awt.Color(255, 0, 0));
        rojo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rojoActionPerformed(evt);
            }
        });
        panel_col.add(rojo);

        amarillo.setBackground(new java.awt.Color(255, 255, 51));
        amarillo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                amarilloActionPerformed(evt);
            }
        });
        panel_col.add(amarillo);

        azul.setBackground(new java.awt.Color(0, 51, 204));
        azul.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                azulActionPerformed(evt);
            }
        });
        panel_col.add(azul);

        verde.setBackground(new java.awt.Color(51, 255, 51));
        verde.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verdeActionPerformed(evt);
            }
        });
        panel_col.add(verde);

        more_colors.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        more_colors.setText("+");
        more_colors.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                more_colorsMouseClicked(evt);
            }
        });
        panel_col.add(more_colors);

        barra_formas.add(panel_col);
        barra_formas.add(jSeparator2);

        panel_col2.setMaximumSize(new java.awt.Dimension(100, 100));
        panel_col2.setPreferredSize(new java.awt.Dimension(40, 25));
        panel_col2.setLayout(new java.awt.GridLayout(2, 2));

        negro1.setBackground(new java.awt.Color(0, 0, 0));
        negro1.setSelected(true);
        negro1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                negro1ActionPerformed(evt);
            }
        });
        panel_col2.add(negro1);

        rojo1.setBackground(new java.awt.Color(255, 0, 0));
        rojo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rojo1ActionPerformed(evt);
            }
        });
        panel_col2.add(rojo1);

        amarillo1.setBackground(new java.awt.Color(255, 255, 51));
        amarillo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                amarillo1ActionPerformed(evt);
            }
        });
        panel_col2.add(amarillo1);

        azul1.setBackground(new java.awt.Color(0, 51, 204));
        azul1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                azul1ActionPerformed(evt);
            }
        });
        panel_col2.add(azul1);

        verde1.setBackground(new java.awt.Color(51, 255, 51));
        verde1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verde1ActionPerformed(evt);
            }
        });
        panel_col2.add(verde1);

        more_colors1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        more_colors1.setText("+");
        more_colors1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                more_colors1MouseClicked(evt);
            }
        });
        panel_col2.add(more_colors1);

        barra_formas.add(panel_col2);
        barra_formas.add(jSeparator6);

        bot_rellenar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rellenar.png"))); // NOI18N
        bot_rellenar.setToolTipText("relleno");
        bot_rellenar.setFocusable(false);
        bot_rellenar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bot_rellenar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bot_rellenar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bot_rellenarActionPerformed(evt);
            }
        });
        barra_formas.add(bot_rellenar);

        degradado_horiz.setText("DH");
        degradado_horiz.setToolTipText("degradado_horizontal");
        degradado_horiz.setFocusable(false);
        degradado_horiz.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        degradado_horiz.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        degradado_horiz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                degradado_horizActionPerformed(evt);
            }
        });
        barra_formas.add(degradado_horiz);

        degradado_vert.setText("DV");
        degradado_vert.setToolTipText("degradado_vertical");
        degradado_vert.setFocusable(false);
        degradado_vert.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        degradado_vert.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        degradado_vert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                degradado_vertActionPerformed(evt);
            }
        });
        barra_formas.add(degradado_vert);

        transparencia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/transparencia.png"))); // NOI18N
        transparencia.setToolTipText("transparencia");
        transparencia.setFocusable(false);
        transparencia.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        transparencia.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        transparencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transparenciaActionPerformed(evt);
            }
        });
        barra_formas.add(transparencia);

        val_transp.setMaximum(10);
        val_transp.setPaintLabels(true);
        val_transp.setToolTipText("valor_transparencia");
        val_transp.setValue(5);
        val_transp.setMaximumSize(new java.awt.Dimension(100, 26));
        val_transp.setPreferredSize(new java.awt.Dimension(50, 26));
        val_transp.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                val_transpStateChanged(evt);
            }
        });
        barra_formas.add(val_transp);

        alisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/alisar.png"))); // NOI18N
        alisar.setToolTipText("alisado");
        alisar.setFocusable(false);
        alisar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        alisar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        alisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alisarActionPerformed(evt);
            }
        });
        barra_formas.add(alisar);

        grosor.setToolTipText("grosor_trazo");
        grosor.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        grosor.setMaximumSize(new java.awt.Dimension(25, 25));
        grosor.setRequestFocusEnabled(false);
        grosor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                grosorStateChanged(evt);
            }
        });
        barra_formas.add(grosor);
        barra_formas.add(jSeparator4);

        abrirSonido_barra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/openAudio24x24.png"))); // NOI18N
        abrirSonido_barra.setToolTipText("abrir_audio");
        abrirSonido_barra.setFocusable(false);
        abrirSonido_barra.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        abrirSonido_barra.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        abrirSonido_barra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirSonido_barraActionPerformed(evt);
            }
        });
        barra_formas.add(abrirSonido_barra);

        play_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/play24x24.png"))); // NOI18N
        play_button.setToolTipText("play");
        play_button.setFocusable(false);
        play_button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        play_button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        play_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                play_buttonActionPerformed(evt);
            }
        });
        barra_formas.add(play_button);

        stop_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/stop24x24.png"))); // NOI18N
        stop_button.setToolTipText("stop");
        stop_button.setFocusable(false);
        stop_button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stop_button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        stop_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stop_buttonActionPerformed(evt);
            }
        });
        barra_formas.add(stop_button);

        sound_list.setToolTipText("lista_audios");
        sound_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sound_listActionPerformed(evt);
            }
        });
        barra_formas.add(sound_list);

        record_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/record24x24.png"))); // NOI18N
        record_button.setToolTipText("grabar_audio");
        record_button.setFocusable(false);
        record_button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        record_button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        record_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                record_buttonActionPerformed(evt);
            }
        });
        barra_formas.add(record_button);

        temporizador.setText("0:0");
        barra_formas.add(temporizador);

        tiempo_total.setText("T_total");
        barra_formas.add(tiempo_total);
        barra_formas.add(jSeparator5);

        abrirVideo_barra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/AbrirVideo.png"))); // NOI18N
        abrirVideo_barra.setToolTipText("abrir_video");
        abrirVideo_barra.setFocusable(false);
        abrirVideo_barra.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        abrirVideo_barra.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        abrirVideo_barra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirVideo_barraActionPerformed(evt);
            }
        });
        barra_formas.add(abrirVideo_barra);

        abrirCamara_barra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Camara.png"))); // NOI18N
        abrirCamara_barra.setToolTipText("cámara");
        abrirCamara_barra.setFocusable(false);
        abrirCamara_barra.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        abrirCamara_barra.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        abrirCamara_barra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirCamara_barraActionPerformed(evt);
            }
        });
        barra_formas.add(abrirCamara_barra);

        capturar_barra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Capturar.png"))); // NOI18N
        capturar_barra.setToolTipText("snapshot");
        capturar_barra.setFocusable(false);
        capturar_barra.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        capturar_barra.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        capturar_barra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                capturar_barraActionPerformed(evt);
            }
        });
        barra_formas.add(capturar_barra);

        getContentPane().add(barra_formas, java.awt.BorderLayout.PAGE_START);

        panel_inf.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panel_inf.setPreferredSize(new java.awt.Dimension(578, 110));
        panel_inf.setLayout(new java.awt.BorderLayout());

        panel_colores.setLayout(new java.awt.GridLayout(2, 3));
        panel_inf.add(panel_colores, java.awt.BorderLayout.LINE_START);

        separador.setMinimumSize(new java.awt.Dimension(50, 30));
        separador.setName("Barra de estado"); // NOI18N
        panel_inf.add(separador, java.awt.BorderLayout.PAGE_END);

        panel_est.setMinimumSize(new java.awt.Dimension(500, 130));
        panel_est.setPreferredSize(new java.awt.Dimension(800, 130));

        filtro_menu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        filtro_menu.setToolTipText("filtros");
        filtro_menu.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtro"));
        filtro_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtro_menuActionPerformed(evt);
            }
        });

        panel_transformaciones.setBorder(javax.swing.BorderFactory.createTitledBorder("Transformaciones"));
        panel_transformaciones.setLayout(new java.awt.GridLayout(2, 0));

        contraste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/contraste.png"))); // NOI18N
        contraste.setToolTipText("contraste");
        contraste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contrasteActionPerformed(evt);
            }
        });
        panel_transformaciones.add(contraste);

        iluminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iluminar.png"))); // NOI18N
        iluminar.setToolTipText("iluminar");
        iluminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iluminarActionPerformed(evt);
            }
        });
        panel_transformaciones.add(iluminar);

        oscurecer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/oscurecer.png"))); // NOI18N
        oscurecer.setToolTipText("oscurecer");
        oscurecer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oscurecerActionPerformed(evt);
            }
        });
        panel_transformaciones.add(oscurecer);

        cuadratica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cuadratica.png"))); // NOI18N
        cuadratica.setToolTipText("cuadratica");
        cuadratica.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cuadraticaStateChanged(evt);
            }
        });
        cuadratica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cuadraticaActionPerformed(evt);
            }
        });
        panel_transformaciones.add(cuadratica);

        mi_lookup.setText("M");
        mi_lookup.setToolTipText("mi_Lookup");
        mi_lookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_lookupActionPerformed(evt);
            }
        });
        panel_transformaciones.add(mi_lookup);

        negativo.setText("N");
        negativo.setToolTipText("negativo");
        negativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                negativoActionPerformed(evt);
            }
        });
        panel_transformaciones.add(negativo);

        spline.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/lineal.png"))); // NOI18N
        spline.setToolTipText("spline_lineal");
        spline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                splineActionPerformed(evt);
            }
        });
        panel_transformaciones.add(spline);

        spline_a.setMaximum(255);
        spline_a.setToolTipText("spline_a");
        spline_a.setValue(150);
        spline_a.setPreferredSize(new java.awt.Dimension(50, 26));
        spline_a.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spline_aStateChanged(evt);
            }
        });
        spline_a.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                spline_aFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                spline_aFocusLost(evt);
            }
        });
        panel_transformaciones.add(spline_a);

        spline_b.setMaximum(255);
        spline_b.setToolTipText("spline_b");
        spline_b.setValue(100);
        spline_b.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spline_bStateChanged(evt);
            }
        });
        spline_b.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                spline_bFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                spline_bFocusLost(evt);
            }
        });
        panel_transformaciones.add(spline_b);

        m_cuadratica.setMaximum(255);
        m_cuadratica.setToolTipText("m_cuadratica");
        m_cuadratica.setValue(128);
        m_cuadratica.setPreferredSize(new java.awt.Dimension(50, 26));
        m_cuadratica.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                m_cuadraticaStateChanged(evt);
            }
        });
        m_cuadratica.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                m_cuadraticaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                m_cuadraticaFocusLost(evt);
            }
        });
        panel_transformaciones.add(m_cuadratica);

        slider_miLookup.setMinimum(-100);
        slider_miLookup.setToolTipText("slider_miLookup");
        slider_miLookup.setValue(0);
        slider_miLookup.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider_miLookupStateChanged(evt);
            }
        });
        slider_miLookup.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                slider_miLookupFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                slider_miLookupFocusLost(evt);
            }
        });
        panel_transformaciones.add(slider_miLookup);

        panel_rotescale.setBorder(javax.swing.BorderFactory.createTitledBorder("Rotación y escalado"));
        panel_rotescale.setLayout(new java.awt.GridLayout(2, 0));

        aumentar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/aumentar.png"))); // NOI18N
        aumentar.setToolTipText("aumentar");
        aumentar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aumentarActionPerformed(evt);
            }
        });
        panel_rotescale.add(aumentar);

        disminuir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/disminuir.png"))); // NOI18N
        disminuir.setToolTipText("disminuir");
        disminuir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disminuirActionPerformed(evt);
            }
        });
        panel_rotescale.add(disminuir);

        rotacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rotacion180.png"))); // NOI18N
        rotacion.setToolTipText("giro_180");
        rotacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rotacionActionPerformed(evt);
            }
        });
        panel_rotescale.add(rotacion);

        slider_rotacion.setMaximum(180);
        slider_rotacion.setMinimum(-180);
        slider_rotacion.setToolTipText("giro_libre");
        slider_rotacion.setValue(0);
        slider_rotacion.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider_rotacionStateChanged(evt);
            }
        });
        slider_rotacion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                slider_rotacionFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                slider_rotacionFocusLost(evt);
            }
        });
        panel_rotescale.add(slider_rotacion);

        barra_est.setText("Barra de estado");
        barra_est.setToolTipText("barra_estado");

        panel_brillocontraste.setBorder(javax.swing.BorderFactory.createTitledBorder("Brillo y contraste"));
        panel_brillocontraste.setLayout(new java.awt.GridLayout(1, 0));

        brillo_slider.setMaximum(50);
        brillo_slider.setMinimum(-50);
        brillo_slider.setToolTipText("brillo");
        brillo_slider.setValue(0);
        brillo_slider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                brillo_sliderStateChanged(evt);
            }
        });
        brillo_slider.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                brillo_sliderFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                brillo_sliderFocusLost(evt);
            }
        });
        panel_brillocontraste.add(brillo_slider);

        contraste_slider.setMaximum(20);
        contraste_slider.setToolTipText("contraste");
        contraste_slider.setValue(10);
        contraste_slider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                contraste_sliderStateChanged(evt);
            }
        });
        contraste_slider.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                contraste_sliderFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                contraste_sliderFocusLost(evt);
            }
        });
        panel_brillocontraste.add(contraste_slider);

        panel_color_rgb.setBorder(javax.swing.BorderFactory.createTitledBorder("Color"));
        panel_color_rgb.setLayout(new java.awt.GridLayout(1, 0));

        bandas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/bandas.png"))); // NOI18N
        bandas.setToolTipText("extracc_bandas");
        bandas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bandasActionPerformed(evt);
            }
        });
        panel_color_rgb.add(bandas);

        menu_espacioColor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        menu_espacioColor.setToolTipText("espacio_colores");
        menu_espacioColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_espacioColorActionPerformed(evt);
            }
        });
        panel_color_rgb.add(menu_espacioColor);

        combinar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/combinar.png"))); // NOI18N
        combinar.setToolTipText("combinar");
        combinar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combinarActionPerformed(evt);
            }
        });
        panel_color_rgb.add(combinar);

        panel_tintado.setBorder(javax.swing.BorderFactory.createTitledBorder("Tintar y ecualizar"));
        panel_tintado.setLayout(new java.awt.GridLayout(2, 4));

        tintar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/tintar.png"))); // NOI18N
        tintar.setToolTipText("tintado");
        tintar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tintarActionPerformed(evt);
            }
        });
        panel_tintado.add(tintar);

        sepia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/sepia.png"))); // NOI18N
        sepia.setToolTipText("sepia");
        sepia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sepiaActionPerformed(evt);
            }
        });
        panel_tintado.add(sepia);

        ecualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/ecualizar.png"))); // NOI18N
        ecualizar.setToolTipText("ecualizar");
        ecualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ecualizarActionPerformed(evt);
            }
        });
        panel_tintado.add(ecualizar);

        rojo_efecto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rojo.png"))); // NOI18N
        rojo_efecto.setToolTipText("resltar_rojo");
        rojo_efecto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rojo_efectoActionPerformed(evt);
            }
        });
        panel_tintado.add(rojo_efecto);

        vignette.setText("V");
        vignette.setToolTipText("Mi_operador");
        vignette.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vignetteActionPerformed(evt);
            }
        });
        panel_tintado.add(vignette);

        histograma.setText("H");
        histograma.setToolTipText("no_implementado");
        panel_tintado.add(histograma);

        slider_tintado.setMaximum(10);
        slider_tintado.setToolTipText("tintado");
        slider_tintado.setValue(0);
        slider_tintado.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider_tintadoStateChanged(evt);
            }
        });
        slider_tintado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                slider_tintadoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                slider_tintadoFocusLost(evt);
            }
        });
        panel_tintado.add(slider_tintado);

        slider_posterizar.setMaximum(20);
        slider_posterizar.setMinimum(2);
        slider_posterizar.setToolTipText("posterizar");
        slider_posterizar.setValue(2);
        slider_posterizar.setMinimumSize(new java.awt.Dimension(25, 26));
        slider_posterizar.setPreferredSize(new java.awt.Dimension(50, 26));
        slider_posterizar.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider_posterizarStateChanged(evt);
            }
        });
        slider_posterizar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                slider_posterizarFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                slider_posterizarFocusLost(evt);
            }
        });
        panel_tintado.add(slider_posterizar);

        arcoiris.setMaximum(360);
        arcoiris.setToolTipText("variar_tonos");
        arcoiris.setValue(5);
        arcoiris.setPreferredSize(new java.awt.Dimension(50, 26));
        arcoiris.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                arcoirisStateChanged(evt);
            }
        });
        arcoiris.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                arcoirisFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                arcoirisFocusLost(evt);
            }
        });
        panel_tintado.add(arcoiris);

        slider_rojo.setMaximum(255);
        slider_rojo.setMinimum(1);
        slider_rojo.setToolTipText("rojoOp");
        slider_rojo.setValue(5);
        slider_rojo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider_rojoStateChanged(evt);
            }
        });
        slider_rojo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                slider_rojoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                slider_rojoFocusLost(evt);
            }
        });
        panel_tintado.add(slider_rojo);

        slider_miOp.setMaximum(10);
        slider_miOp.setToolTipText("mi_operador");
        slider_miOp.setValue(1);
        slider_miOp.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider_miOpStateChanged(evt);
            }
        });
        slider_miOp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                slider_miOpFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                slider_miOpFocusLost(evt);
            }
        });
        panel_tintado.add(slider_miOp);

        slider_sat_col.setMaximum(10);
        slider_sat_col.setToolTipText("saturar_variar_tonos");
        slider_sat_col.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider_sat_colStateChanged(evt);
            }
        });
        slider_sat_col.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                slider_sat_colFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                slider_sat_colFocusLost(evt);
            }
        });
        panel_tintado.add(slider_sat_col);

        javax.swing.GroupLayout panel_estLayout = new javax.swing.GroupLayout(panel_est);
        panel_est.setLayout(panel_estLayout);
        panel_estLayout.setHorizontalGroup(
            panel_estLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_estLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_estLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(barra_est)
                    .addGroup(panel_estLayout.createSequentialGroup()
                        .addComponent(panel_brillocontraste, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(filtro_menu, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel_transformaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(panel_rotescale, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panel_color_rgb, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_tintado, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(109, Short.MAX_VALUE))
        );
        panel_estLayout.setVerticalGroup(
            panel_estLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_estLayout.createSequentialGroup()
                .addGroup(panel_estLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_estLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(panel_estLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(panel_brillocontraste, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filtro_menu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(barra_est, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_estLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(panel_estLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panel_rotescale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel_transformaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel_color_rgb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel_tintado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        panel_inf.add(panel_est, java.awt.BorderLayout.CENTER);

        getContentPane().add(panel_inf, java.awt.BorderLayout.PAGE_END);

        splitPanel.setDividerLocation(1000);

        javax.swing.GroupLayout panelEscritorioLayout = new javax.swing.GroupLayout(panelEscritorio);
        panelEscritorio.setLayout(panelEscritorioLayout);
        panelEscritorioLayout.setHorizontalGroup(
            panelEscritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 999, Short.MAX_VALUE)
        );
        panelEscritorioLayout.setVerticalGroup(
            panelEscritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 382, Short.MAX_VALUE)
        );

        splitPanel.setLeftComponent(panelEscritorio);

        panelListaFormas.setLayout(new java.awt.BorderLayout());

        volcado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/volcar.png"))); // NOI18N
        volcado.setToolTipText("volcado");
        volcado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                volcadoActionPerformed(evt);
            }
        });
        panelListaFormas.add(volcado, java.awt.BorderLayout.PAGE_END);

        jScrollPane2.setViewportView(listaFormas);

        panelListaFormas.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        splitPanel.setRightComponent(panelListaFormas);

        getContentPane().add(splitPanel, java.awt.BorderLayout.CENTER);

        jMenu1.setText("Archivo");

        nuevo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_DOWN_MASK));
        nuevo.setText("Nuevo");
        nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoActionPerformed(evt);
            }
        });
        jMenu1.add(nuevo);

        abrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_DOWN_MASK));
        abrir.setText("Abrir");
        abrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirActionPerformed(evt);
            }
        });
        jMenu1.add(abrir);

        guardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.ALT_DOWN_MASK));
        guardar.setText("Guardar");
        guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarActionPerformed(evt);
            }
        });
        jMenu1.add(guardar);

        barra_archivo.add(jMenu1);

        menu_mg.setText("Imagen");

        rescaleOP.setText("Operador RescaleOp");
        rescaleOP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rescaleOPActionPerformed(evt);
            }
        });
        menu_mg.add(rescaleOP);

        convolveOP.setText("Operador ConvolveOp");
        convolveOP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                convolveOPActionPerformed(evt);
            }
        });
        menu_mg.add(convolveOP);

        affineTransformOP.setText("Operador AffineTransformOp");
        affineTransformOP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                affineTransformOPActionPerformed(evt);
            }
        });
        menu_mg.add(affineTransformOP);

        lookupOP.setText("Operador LookupOp");
        lookupOP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lookupOPActionPerformed(evt);
            }
        });
        menu_mg.add(lookupOP);

        bandcombineOP.setText("Operador BandCombineOp");
        bandcombineOP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bandcombineOPActionPerformed(evt);
            }
        });
        menu_mg.add(bandcombineOP);

        colorspaceOP.setText("Operador ColorSpaceOp");
        colorspaceOP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorspaceOPActionPerformed(evt);
            }
        });
        menu_mg.add(colorspaceOP);

        duplicar.setText("Duplicar");
        duplicar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                duplicarActionPerformed(evt);
            }
        });
        menu_mg.add(duplicar);

        barra_archivo.add(menu_mg);

        sonido_menu.setText("Sonido");

        abrirAudio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/openAudio24x24.png"))); // NOI18N
        abrirAudio.setText("Abrir");
        abrirAudio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirAudioActionPerformed(evt);
            }
        });
        sonido_menu.add(abrirAudio);

        barra_archivo.add(sonido_menu);

        sonido_menu1.setText("Vídeo");

        abrirVideo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/AbrirVideo.png"))); // NOI18N
        abrirVideo.setText("Abrir");
        abrirVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirVideoActionPerformed(evt);
            }
        });
        sonido_menu1.add(abrirVideo);

        abrirCamara.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Camara.png"))); // NOI18N
        abrirCamara.setText("Cámara");
        abrirCamara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirCamaraActionPerformed(evt);
            }
        });
        sonido_menu1.add(abrirCamara);

        capturar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Capturar.png"))); // NOI18N
        capturar.setText("Captura");
        capturar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                capturarActionPerformed(evt);
            }
        });
        sonido_menu1.add(capturar);

        barra_archivo.add(sonido_menu1);

        ayuda_menu.setText("Ayuda");

        acercaDe.setText("Acerca de");
        acercaDe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acercaDeActionPerformed(evt);
            }
        });
        ayuda_menu.add(acercaDe);

        barra_archivo.add(ayuda_menu);

        setJMenuBar(barra_archivo);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void abrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abrirActionPerformed
        JFileChooser dlg = new JFileChooser();
        FileNameExtensionFilter ext_img = new FileNameExtensionFilter("Formato imágenes", ImageIO.getReaderFormatNames());
        dlg.setFileFilter(ext_img);
        int resp = dlg.showOpenDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            try {
                File f = dlg.getSelectedFile();
                VentanaInternaImagen vi = new VentanaInternaImagen();
                BufferedImage img = ImageIO.read(f);
                vi.getLienzo().setImage(img);
                this.panelEscritorio.add(vi);
                vi.setTitle(f.getName());
                vi.setVisible(true);
                vi.addInternalFrameListener(new manejadorVentanaInterna());
                vi.getLienzo().addLienzoListener(new MiManejadorLienzo());
                
            } catch (Exception ex) {
                System.err.println("Error al leer la imagen");
            }
        }
    }//GEN-LAST:event_abrirActionPerformed

    private void negroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_negroActionPerformed
        getLienzoSeleccionado().setColorTrazo(Color.BLACK);
    }//GEN-LAST:event_negroActionPerformed

    private void rojoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rojoActionPerformed
        getLienzoSeleccionado().setColorTrazo(Color.RED);
    }//GEN-LAST:event_rojoActionPerformed

    private void azulActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_azulActionPerformed
        getLienzoSeleccionado().setColorTrazo(Color.BLUE);
    }//GEN-LAST:event_azulActionPerformed

    private void amarilloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_amarilloActionPerformed
        getLienzoSeleccionado().setColorTrazo(Color.YELLOW);
    }//GEN-LAST:event_amarilloActionPerformed

    private void verdeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verdeActionPerformed
        getLienzoSeleccionado().setColorTrazo(Color.GREEN);
    }//GEN-LAST:event_verdeActionPerformed

    private void lineaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineaActionPerformed
        linea.setSelected(linea.isSelected());

        if (linea.isSelected()) {
            this.getLienzoSeleccionado().setTipoForma(1);
            rectangulo.setSelected(false);
            elipse.setSelected(false);
            trazo.setSelected(false);
            curva.setSelected(false);
            smile.setSelected(false);
            seleccion.setSelected(false);
            edicion.setSelected(false);
            if (this.getLienzoSeleccionado().getEditar()){
                this.getLienzoSeleccionado().stopEdicion();
            }
            if (this.getLienzoSeleccionado().getMover()){
                this.getLienzoSeleccionado().setMover(false);
            }
        }
    }//GEN-LAST:event_lineaActionPerformed

    private void rectanguloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rectanguloActionPerformed
        rectangulo.setSelected(rectangulo.isSelected());

        if (rectangulo.isSelected()) {
            this.getLienzoSeleccionado().setTipoForma(2);
            linea.setSelected(false);
            elipse.setSelected(false);
            trazo.setSelected(false);
            curva.setSelected(false);
            smile.setSelected(false);
            seleccion.setSelected(false);
            edicion.setSelected(false);
            if (this.getLienzoSeleccionado().getEditar()){
                this.getLienzoSeleccionado().stopEdicion();
            }
            if (this.getLienzoSeleccionado().getMover()){
                this.getLienzoSeleccionado().setMover(false);
            }
        }
    }//GEN-LAST:event_rectanguloActionPerformed

    private void elipseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_elipseActionPerformed
        elipse.setSelected(elipse.isSelected());

        if (elipse.isSelected()) {
            this.getLienzoSeleccionado().setTipoForma(3);
            rectangulo.setSelected(false);
            linea.setSelected(false);
            trazo.setSelected(false);
            curva.setSelected(false);
            smile.setSelected(false);
            seleccion.setSelected(false);
            edicion.setSelected(false);
            if (this.getLienzoSeleccionado().getEditar()){
                this.getLienzoSeleccionado().stopEdicion();
            }
            if (this.getLienzoSeleccionado().getMover()){
                this.getLienzoSeleccionado().setMover(false);
            }
        }
    }//GEN-LAST:event_elipseActionPerformed

    private void guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) panelEscritorio.getSelectedFrame();
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                JFileChooser dlg = new JFileChooser();
                FileNameExtensionFilter ext_img = new FileNameExtensionFilter("Formato imágenes", ImageIO.getReaderFormatNames());
                dlg.setFileFilter(ext_img);
                int resp = dlg.showSaveDialog(this);
                if (resp == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = dlg.getSelectedFile();
                        String ext = "";
                        int indx = f.getName().lastIndexOf(".");
                        if (indx > 0 && indx < f.getName().length() - 1) {
                            ext = f.getName().substring(indx + 1);
                        }
                        ImageIO.write(img, ext, f);
                        vi.setTitle(f.getName());
                    } catch (Exception ex) {
                        System.err.println("Error al guardar la imagen");
                    }
                }
            }
        }
    }//GEN-LAST:event_guardarActionPerformed

    private void nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoActionPerformed
        VentanaInternaImagen vi = new VentanaInternaImagen();
        panelEscritorio.add(vi);
        
        int w = 300, h = 300;
        int resp = JOptionPane.showConfirmDialog(null, "¿Desea modificar las dimensiones de la imagen?", "Cambio", JOptionPane.YES_NO_OPTION);

        if (resp == JOptionPane.YES_OPTION) {
            try {
                w = Integer.parseInt(JOptionPane.showInputDialog("Introduzca el ancho:"));
                h = Integer.parseInt(JOptionPane.showInputDialog("Introduzca el alto:"));
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null, "Error: no se pudo crear la imagen");
            }
        }
        vi.setVisible(true);
        BufferedImage img;
        img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        g2d.setPaint(Color.WHITE);
        g2d.fillRect(img.getMinX(), img.getMinY(), img.getWidth(), img.getHeight());
        vi.getLienzo().setImage(img);
        
        vi.addInternalFrameListener(new manejadorVentanaInterna());
        vi.getLienzo().addLienzoListener(new MiManejadorLienzo());
    }//GEN-LAST:event_nuevoActionPerformed

    private void bot_rellenarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bot_rellenarActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) panelEscritorio.getSelectedFrame();
        if (vi != null) {
            bot_rellenar.setSelected(bot_rellenar.isSelected());
            if (bot_rellenar.isSelected()) {
                vi.getLienzo().setRelleno(1);
                degradado_horiz.setSelected(false);
                degradado_vert.setSelected(false);
            } else {
                vi.getLienzo().setRelleno(0);
            }
            vi.getLienzo().repaint();
        }
    }//GEN-LAST:event_bot_rellenarActionPerformed

    private void seleccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionActionPerformed
        linea.setSelected(false);
        rectangulo.setSelected(false);
        elipse.setSelected(false);
        trazo.setSelected(false);
        curva.setSelected(false);
        smile.setSelected(false);
        edicion.setSelected(false);
        if (this.getLienzoSeleccionado().getEditar()){
            this.getLienzoSeleccionado().stopEdicion();
        }

        this.getLienzoSeleccionado().setMover(seleccion.isSelected());
    }//GEN-LAST:event_seleccionActionPerformed

    private void bot_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bot_guardarActionPerformed
        this.guardarActionPerformed(evt);
    }//GEN-LAST:event_bot_guardarActionPerformed

    private void bot_abrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bot_abrirActionPerformed
        this.abrirActionPerformed(evt);
    }//GEN-LAST:event_bot_abrirActionPerformed

    private void bot_nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bot_nuevoActionPerformed
        this.nuevoActionPerformed(evt);
    }//GEN-LAST:event_bot_nuevoActionPerformed

    private void trazoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trazoActionPerformed
        trazo.setSelected(trazo.isSelected());

        if (trazo.isSelected()) {
            this.getLienzoSeleccionado().setTipoForma(4);
            rectangulo.setSelected(false);
            elipse.setSelected(false);
            linea.setSelected(false);
            curva.setSelected(false);
            smile.setSelected(false);
            seleccion.setSelected(false);
            edicion.setSelected(false);
            if (this.getLienzoSeleccionado().getEditar()){
                this.getLienzoSeleccionado().stopEdicion();
            }
            if (this.getLienzoSeleccionado().getMover()){
                this.getLienzoSeleccionado().setMover(false);
            }
        }
    }//GEN-LAST:event_trazoActionPerformed

    private void curvaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_curvaActionPerformed
        curva.setSelected(curva.isSelected());

        if (curva.isSelected()) {
            this.getLienzoSeleccionado().setTipoForma(5);
            rectangulo.setSelected(false);
            elipse.setSelected(false);
            trazo.setSelected(false);
            linea.setSelected(false);
            smile.setSelected(false);
            seleccion.setSelected(false);
            edicion.setSelected(false);
            if (this.getLienzoSeleccionado().getEditar()){
                this.getLienzoSeleccionado().stopEdicion();
            }
            if (this.getLienzoSeleccionado().getMover()){
                this.getLienzoSeleccionado().setMover(false);
            }
        }
    }//GEN-LAST:event_curvaActionPerformed

    private void smileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smileActionPerformed
        smile.setSelected(smile.isSelected());

        if (smile.isSelected()) {
            this.getLienzoSeleccionado().setTipoForma(6);
            rectangulo.setSelected(false);
            elipse.setSelected(false);
            trazo.setSelected(false);
            curva.setSelected(false);
            linea.setSelected(false);
            seleccion.setSelected(false);
            edicion.setSelected(false);
            if (this.getLienzoSeleccionado().getEditar()){
                this.getLienzoSeleccionado().stopEdicion();
            }
            if (this.getLienzoSeleccionado().getMover()){
                this.getLienzoSeleccionado().setMover(false);
            }
        }
    }//GEN-LAST:event_smileActionPerformed

    private void grosorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_grosorStateChanged
        VentanaInternaImagen vi = (VentanaInternaImagen) panelEscritorio.getSelectedFrame();
        if (vi != null) {
            Stroke trazo = null;
            if (trazo_discontinuo.isSelected()) {
                float[] patron = {5.0f, 5.0f};
                trazo = new BasicStroke((int) grosor.getValue(), BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 10.0f, patron, 0.0f);
            } else {
                trazo = new BasicStroke((int) grosor.getValue());
            }
            vi.getLienzo().setTrazo(trazo);
            vi.getLienzo().repaint();
        }

    }//GEN-LAST:event_grosorStateChanged

    private void transparenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transparenciaActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) panelEscritorio.getSelectedFrame();
        if (vi != null) {
            vi.getLienzo().setEstaTransparente(transparencia.isSelected());
            vi.getLienzo().repaint();
        }
        if (vi.getLienzo().getEstaTransparente()) {
            float valor = (float) val_transp.getValue() / 10.0f;
            vi.getLienzo().setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, valor));
        } else {
            vi.getLienzo().setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        }
    }//GEN-LAST:event_transparenciaActionPerformed

    private void more_colorsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_more_colorsMouseClicked
        Color color = JColorChooser.showDialog(this, "Elige un color", Color.RED);
        this.getLienzoSeleccionado().setColorTrazo(color);
    }//GEN-LAST:event_more_colorsMouseClicked

    private void alisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alisarActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) panelEscritorio.getSelectedFrame();
        if (vi != null) {
            vi.getLienzo().setEstaAlisado(alisar.isSelected());
            vi.getLienzo().repaint();
        }
    }//GEN-LAST:event_alisarActionPerformed

    private void brillo_sliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_brillo_sliderStateChanged
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null && imgFuente != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    int brillo = brillo_slider.getValue();
                    RescaleOp rop = new RescaleOp(1.0F, brillo, null);
                    rop.filter(imgFuente, img);
                    panelEscritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_brillo_sliderStateChanged

    private void rescaleOPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rescaleOPActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    RescaleOp rop = new RescaleOp(1.0F, 100.0F, null);
                    rop.filter(img, img);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_rescaleOPActionPerformed

    private void convolveOPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_convolveOPActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    float filtro[] = {0.1f, 0.1f, 0.1f, 0.1f, 0.2f, 0.1f, 0.1f, 0.1f, 0.1f};
                    Kernel k = new Kernel(3, 3, filtro);
                    ConvolveOp cop = new ConvolveOp(k);
                    BufferedImage imgdest = cop.filter(img, null);
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_convolveOPActionPerformed

    private void focusGained() {
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo().getImg().getColorModel();
            WritableRaster raster = vi.getLienzo().getImg().copyData(null);
            boolean alfaPre = vi.getLienzo().getImg().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }

    private void brillo_sliderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_brillo_sliderFocusGained
        this.focusGained();
    }//GEN-LAST:event_brillo_sliderFocusGained

    private void brillo_sliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_brillo_sliderFocusLost
        imgFuente = null;
        this.brillo_slider.setValue(0);
    }//GEN-LAST:event_brillo_sliderFocusLost

    private void affineTransformOPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_affineTransformOPActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(45), img.getWidth() / 2, img.getHeight() / 2);
                    //AffineTransform at = AffineTransform.getScaleInstance(1.5, 1.5)
                    AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imgdest = atop.filter(img, null);
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_affineTransformOPActionPerformed

    private void lookupOPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lookupOPActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    byte funcionT[] = new byte[256];
                    for (int x = 0; x < 256; x++) {
                        funcionT[x] = (byte) (255 - x); // Negativo
                    }
                    LookupTable tabla = new ByteLookupTable(0, funcionT);
                    LookupOp lop = new LookupOp(tabla, null);
                    lop.filter(img, img);
                    /*
                    BufferedImage imgdest = lop.filter(img, null);
                    vi.getLienzo().setImage(imgdest);
                     */
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }

        }
    }//GEN-LAST:event_lookupOPActionPerformed

    private void contrasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contrasteActionPerformed
        int type = LookupTableProducer.TYPE_SFUNCION;
        LookupTable lt = LookupTableProducer.createLookupTable(type);
        aplicarLookup(lt);
     }//GEN-LAST:event_contrasteActionPerformed

    private void rotacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotacionActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(180), img.getWidth() / 2, img.getHeight() / 2);
                    AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imgdest = atop.filter(img, null);
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_rotacionActionPerformed

    private void aplicarLookup(LookupTable tabla) {
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    LookupOp lop = new LookupOp(tabla, null);
                    lop.filter(img, img); // Imagen origen y destino iguales
                    vi.getLienzo().repaint();
                } catch (Exception e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }


    private void aumentarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aumentarActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    AffineTransform at = AffineTransform.getScaleInstance(1.25, 1.25);
                    AffineTransformOp atop = new AffineTransformOp(at, null);
                    BufferedImage imgdest = atop.filter(img, null);
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_aumentarActionPerformed

    private void disminuirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disminuirActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    AffineTransform at = AffineTransform.getScaleInstance(0.75, 0.75);
                    AffineTransformOp atop = new AffineTransformOp(at, null);
                    BufferedImage imgdest = atop.filter(img, null);
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_disminuirActionPerformed

    private void contraste_sliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_contraste_sliderStateChanged
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null && imgFuente != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    float ct = (float) contraste_slider.getValue() / 10.0f;
                    RescaleOp rop = new RescaleOp(ct, 0.0F, null);
                    rop.filter(imgFuente, img);
                    panelEscritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_contraste_sliderStateChanged

    private void contraste_sliderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_contraste_sliderFocusGained
        this.focusGained();
    }//GEN-LAST:event_contraste_sliderFocusGained

    private void contraste_sliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_contraste_sliderFocusLost
        imgFuente = null;
        this.contraste_slider.setValue(10);
    }//GEN-LAST:event_contraste_sliderFocusLost

    private void filtro_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtro_menuActionPerformed
        this.setTipoFiltro(filtro_menu.getSelectedItem().toString());
        Kernel k = getKernel(getTipoFiltro());

        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    ConvolveOp cop = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);
                    BufferedImage imgdest = cop.filter(img, null);
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_filtro_menuActionPerformed

    private Kernel getKernel(String selecc_masc) {
        Kernel k = null;
        switch (selecc_masc) {
            case "Media 3x3":
                //float filtro[] = KernelProducer.MASCARA_MEDIA_3X3;
                //k = new Kernel(3, 3, filtro);
                k = KernelProducer.createKernel(KernelProducer.TYPE_MEDIA_3x3);
                break;
            case "Media 5x5":
                float filtroMedia5x5[] = {0.03f, 0.06f, 0.08f, 0.06f, 0.03f,
                    0.06f, 0.10f, 0.12f, 0.10f, 0.06f,
                    0.08f, 0.12f, 0.15f, 0.12f, 0.08f,
                    0.06f, 0.10f, 0.12f, 0.10f, 0.06f,
                    0.03f, 0.06f, 0.08f, 0.06f, 0.03f};
                k = new Kernel(5, 5, filtroMedia5x5);
                break;
            case "Media 7x7":
                float filtroMedia7x7[] = {0.01f, 0.01f, 0.02f, 0.02f, 0.02f, 0.01f, 0.01f,
                    0.01f, 0.01f, 0.02f, 0.03f, 0.02f, 0.01f, 0.01f,
                    0.02f, 0.02f, 0.03f, 0.04f, 0.03f, 0.02f, 0.02f,
                    0.02f, 0.03f, 0.04f, 0.05f, 0.04f, 0.03f, 0.02f,
                    0.02f, 0.02f, 0.03f, 0.04f, 0.03f, 0.02f, 0.02f,
                    0.01f, 0.01f, 0.02f, 0.03f, 0.02f, 0.01f, 0.01f,
                    0.01f, 0.01f, 0.02f, 0.02f, 0.02f, 0.01f, 0.01f};
                k = new Kernel(7, 7, filtroMedia7x7);
                break;
            case "Binomial":
                k = KernelProducer.createKernel(KernelProducer.TYPE_BINOMIAL_3x3);
                break;
            case "Enfoque":
                k = KernelProducer.createKernel(KernelProducer.TYPE_ENFOQUE_3x3);
                break;
            case "Relieve":
                k = KernelProducer.createKernel(KernelProducer.TYPE_RELIEVE_3x3);
                break;
            case "Laplaciano":
                k = KernelProducer.createKernel(KernelProducer.TYPE_LAPLACIANA_3x3);
                break;
            case "Horizontal 5x1":
                float filtroHoriz5x1[] = {0.2f, 0.2f, 0.2f, 0.2f, 0.2f};
                k = new Kernel(5, 1, filtroHoriz5x1);
                break;
            case "Horizontal 7x1":
                float filtroHoriz7x1[] = {0.1f, 0.15f, 0.2f, 0.25f, 0.2f, 0.15f, 0.1f};
                k = new Kernel(7, 1, filtroHoriz7x1);
                break;
            case "Horizontal 10x1":
                float filtroHoriz10x1[] = {0.05f, 0.05f, 0.1f, 0.1f, 0.2f, 0.1f, 0.1f, 0.05f, 0.05f, 0.05f};
                k = new Kernel(10, 1, filtroHoriz10x1);
                break;
        }
        return k;
    }

    private void combinarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combinarActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    float[][] matriz = {{0.0F, 0.5F, 0.5F},
                    {0.5F, 0.0F, 0.5F},
                    {0.5F, 0.5F, 0.0F}};
                    BandCombineOp bcop = new BandCombineOp(matriz, null);
                    bcop.filter(img.getRaster(), img.getRaster());
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_combinarActionPerformed

    private void aplicarTintado(float alfa) {
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    float alpha = alfa / 10.0f;
                    TintOp tintado = new TintOp(vi.getLienzo().getColorTrazo(), alpha);
                    tintado.filter(img, img);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }
    private void tintarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tintarActionPerformed
        this.aplicarTintado(5);
    }//GEN-LAST:event_tintarActionPerformed

    private void sepiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sepiaActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    SepiaOp sepia = new SepiaOp();
                    sepia.filter(img, img);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_sepiaActionPerformed

    private void ecualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ecualizarActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    EqualizationOp ecualizacion = new EqualizationOp();
                    ecualizacion.filter(img, img);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_ecualizarActionPerformed

    private void aplicarRojoOp(int umbral) {
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    RojoOp rojo = new RojoOp(umbral);
                    rojo.filter(img, img);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }
    private void rojo_efectoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rojo_efectoActionPerformed
        this.aplicarRojoOp(5);
    }//GEN-LAST:event_rojo_efectoActionPerformed

    private void iluminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iluminarActionPerformed
        int type = LookupTableProducer.TYPE_LOGARITHM;
        LookupTable lt = LookupTableProducer.createLookupTable(type);
        aplicarLookup(lt);
    }//GEN-LAST:event_iluminarActionPerformed

    private void oscurecerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oscurecerActionPerformed
        int type = LookupTableProducer.TYPE_POWER;
        LookupTable lt = LookupTableProducer.createLookupTable(type);
        aplicarLookup(lt);
    }//GEN-LAST:event_oscurecerActionPerformed

    private void aplicarCuadratica(double m) {
        double x = 0.0;
        if (m < 128) {
            x = 255.0;
        }
        double Max = (1.0 / 100.0) * (Math.pow(x - m, 2));
        double K = 255.0 / Max;
        byte lt[] = new byte[256];

        for (int l = 0; l < 256; l++) {
            double val = K * ((1.0 / 100.0) * (Math.pow((l - m), 2)));
            lt[l] = (byte) val;

        }
        ByteLookupTable cuadratica = new ByteLookupTable(0, lt);
        aplicarLookupFuente(cuadratica);
    }

    private void cuadraticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cuadraticaActionPerformed
        aplicarCuadratica(128.0);
    }//GEN-LAST:event_cuadraticaActionPerformed

    private void bandcombineOPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bandcombineOPActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    float[][] matriz = {{1.0F, 0.0F, 0.0F},
                    {0.0F, 0.0F, 1.0F},
                    {0.0F, 1.0F, 0.0F}};
                    BandCombineOp bcop = new BandCombineOp(matriz, null);
                    bcop.filter(img.getRaster(), img.getRaster());
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_bandcombineOPActionPerformed

    private void colorspaceOPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorspaceOPActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);//ColorSpace.getInstance(ColorSpace.CS_GRAY);
                    ColorConvertOp op = new ColorConvertOp(cs, null);
                    BufferedImage imgdest = op.filter(img, null);
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_colorspaceOPActionPerformed

    private void bandasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bandasActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                for (int i = 0; i < img.getRaster().getNumBands(); i++) {
                    BufferedImage imgbanda = getImageBand(img, i);
                    vi = new VentanaInternaImagen();
                    vi.getLienzo().setImg(imgbanda);
                    panelEscritorio.add(vi);
                    vi.setVisible(true);
                }
            }
        }
    }//GEN-LAST:event_bandasActionPerformed

    private void menu_espacioColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_espacioColorActionPerformed
        this.setEspColor(menu_espacioColor.getSelectedItem().toString());
        if (getEspColor() == "RGB") {
            cambiarEspColor(ColorSpace.CS_sRGB);
        } else if (getEspColor() == "YCC") {
            cambiarEspColor(ColorSpace.CS_PYCC);
        } else {
            cambiarEspColor(ColorSpace.CS_GRAY);
        }

    }//GEN-LAST:event_menu_espacioColorActionPerformed

    private void slider_posterizarStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider_posterizarStateChanged
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null && imgFuente != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    int nv = slider_posterizar.getValue();
                    PosterizarOp post = new PosterizarOp(nv);
                    post.filter(imgFuente, img);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_slider_posterizarStateChanged

    private void slider_posterizarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider_posterizarFocusGained
        this.focusGained();
    }//GEN-LAST:event_slider_posterizarFocusGained

    private void slider_posterizarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider_posterizarFocusLost
        imgFuente = null;
        this.slider_posterizar.setValue(2);
    }//GEN-LAST:event_slider_posterizarFocusLost

    private void arcoirisStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_arcoirisStateChanged
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null && imgFuente != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    int p = arcoiris.getValue();
                    VariarTonosOp variar = new VariarTonosOp(p);
                    variar.filter(imgFuente, img);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_arcoirisStateChanged

    private void arcoirisFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_arcoirisFocusGained
        this.focusGained();
    }//GEN-LAST:event_arcoirisFocusGained

    private void arcoirisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_arcoirisFocusLost
        imgFuente = null;
        this.arcoiris.setValue(1);
    }//GEN-LAST:event_arcoirisFocusLost

    private void abrirAudioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abrirAudioActionPerformed
        JFileChooser dlg = new JFileChooser();
        int resp = dlg.showOpenDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            try {
                //File f = dlg.getSelectedFile();
                File f = new File(dlg.getSelectedFile().getAbsolutePath()) {
                    @Override
                    public String toString() {
                        return this.getName();
                    }
                };
                this.sound_list.addItem(f);
            } catch (Exception ex) {
                System.err.println("Error al abirir el sonido");
            }
        }
    }//GEN-LAST:event_abrirAudioActionPerformed

    private void sound_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sound_listActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sound_listActionPerformed

    private void play_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_play_buttonActionPerformed
        File f = (File) sound_list.getSelectedItem();
        VentanaInternaVideo vv = (VentanaInternaVideo) panelEscritorio.getSelectedFrame();

        if (f != null && vv == null) {
            player = new SMClipPlayer(f);
            /*
            AudioFileFormat file;
            int secs = 0;
            int mins = 0;
            try {
                file = AudioSystem.getAudioFileFormat(f);
                int microsecs = (int)file.properties().get("duration");
                secs = microsecs / 1000000;
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (secs >= 60){
                mins = secs / 60;
                secs = secs - (60*mins);
            }
             */

            if (player != null) {
                player.addLineListener(new ManejadorAudio());
                player.play();
                timer.startTimer(temporizador);
                temporizador.setText(timer.getTxt());
                //tiempo_total.setText(mins+":"+secs);
            }
        } else {
            if (vv != null) {
                vv.play();
            }
        }
    }//GEN-LAST:event_play_buttonActionPerformed

    private void stop_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stop_buttonActionPerformed
        if (player != null || recorder != null) {
            if (player != null) {
                player.stop();
                player = null;
                timer.stopTimer();
            }
            if (recorder != null) {
                recorder.stop();
                recorder = null;
                timer.stopTimer();
            }
        } else {
            VentanaInternaVideo vv = (VentanaInternaVideo) panelEscritorio.getSelectedFrame();
            if (vv != null) {
                vv.stop();
            }
        }
    }//GEN-LAST:event_stop_buttonActionPerformed

    private void record_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_record_buttonActionPerformed
        JFileChooser dlg = new JFileChooser();
        int resp = dlg.showSaveDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            try {
                File f = dlg.getSelectedFile();
                recorder = new SMSoundRecorder(f);
                if (recorder != null) {
                    recorder.record();
                    timer.startTimer(temporizador);
                    temporizador.setText(timer.getTxt());
                }
            } catch (Exception ex) {
                System.err.println("Error al guardar el sonido");
            }
        }
    }//GEN-LAST:event_record_buttonActionPerformed

    private void volcadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_volcadoActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                DefaultListModel<Forma2D> modelo = (DefaultListModel<Forma2D>) listaFormas.getModel();
                Graphics2D g2d = img.createGraphics();
                List<Forma2D> selecc = listaFormas.getSelectedValuesList();

                if (selecc != null) {

                    for (Forma2D s : selecc) {
                        s.draw(g2d);
                        vi.getLienzo().getShapeList().remove(s);
                        modelo.removeElement(s);
                    }
                    listaFormas.setModel(modelo);
                    g2d.dispose();
                    vi.getLienzo().setImg(img);
                }
            }
        }

    }//GEN-LAST:event_volcadoActionPerformed

    private void negativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_negativoActionPerformed
        short[] negTable = new short[256];
        for (int i = 0; i < 256; i++) {
            negTable[i] = (short) (255 - i);
        }
        LookupTable lt = new ShortLookupTable(0, negTable);
        aplicarLookup(lt);

    }//GEN-LAST:event_negativoActionPerformed

    private void duplicarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_duplicarActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img1 = vi.getLienzo().getImg();
            BufferedImage img2 = new BufferedImage(img1.getWidth(), img1.getHeight(), img1.getType());
            img2.setData(img1.getData());

            VentanaInternaImagen vi2 = new VentanaInternaImagen();
            vi2.setTitle("Copia");
            vi2.getLienzo().setImage(img2);
            panelEscritorio.add(vi2);
            vi2.setVisible(true);
            vi2.addInternalFrameListener(new manejadorVentanaInterna());
            vi2.getLienzo().addLienzoListener(new MiManejadorLienzo());
        }
    }//GEN-LAST:event_duplicarActionPerformed

    private void duplicado_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_duplicado_buttonActionPerformed
        this.duplicarActionPerformed(evt);
    }//GEN-LAST:event_duplicado_buttonActionPerformed

    private void m_cuadraticaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_m_cuadraticaStateChanged
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null && imgFuente != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    double m = m_cuadratica.getValue();
                    this.aplicarCuadratica(m);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_m_cuadraticaStateChanged

    private void m_cuadraticaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_m_cuadraticaFocusGained
        this.focusGained();
    }//GEN-LAST:event_m_cuadraticaFocusGained

    private void m_cuadraticaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_m_cuadraticaFocusLost
        imgFuente = null;
        this.m_cuadratica.setValue(128);
    }//GEN-LAST:event_m_cuadraticaFocusLost

    private void cuadraticaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cuadraticaStateChanged

    }//GEN-LAST:event_cuadraticaStateChanged

    private void aplicarLookupFuente(LookupTable tabla) {
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    LookupOp lop = new LookupOp(tabla, null);
                    lop.filter(imgFuente, img); // Imagen origen y destino iguales
                    vi.getLienzo().repaint();
                } catch (Exception e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }

    public void paintGraph(double a, double b, double val[]) {
        BufferedImage img;
        img = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2dimg = (Graphics2D) img.getGraphics();
        g2dimg.setPaint(Color.WHITE);
        g2dimg.fillRect(img.getMinX(), img.getMinY(), img.getWidth(), img.getHeight());

        if (spline_graph == null) {
            spline_graph = new VentanaInternaImagen();
            panelEscritorio.add(spline_graph);
            spline_graph.setTitle("Spline lineal");
            spline_graph.setVisible(true);
            spline_graph.addInternalFrameListener(new manejadorVentanaInterna());
            spline_graph.getLienzo().setImage(img);
        }

        Graphics g = spline_graph.getLienzo().getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        spline_graph.getLienzo().paint(g);

        Path2D funcionSpline = new Path2D.Double();
        funcionSpline.moveTo(0, 0);

        for (int x = 0; x < 256; x++) {
            funcionSpline.lineTo(x, val[x]);
        }
        g2d.draw(funcionSpline);
        g2d.fillOval((int) a - 3, (int) b - 3, 6, 6);
        String txt = "(" + a + ", " + b + ")";
        g2d.drawString(txt, 100, 15);
        g2d.dispose();
    }

    public double[] aplicarSpline(double a, double b) {
        double m = 0.0;
        double T = 0.0;

        if (a != 255) {
            m = (double) ((255.0 - b) / (255.0 - a));
        } else {
            m = 0.0;
        }
        byte lt[] = new byte[256];
        double val[] = new double[256];

        for (int x = 0; x < 256; x++) {
            if (x < a) {
                T = (double) ((b / a) * x);
            } else {
                T = (double) (m * (x - a) + b);
            }
            lt[x] = (byte) T;
            val[x] = (double) T;
        }
        ByteLookupTable spline_lineal = new ByteLookupTable(0, lt);
        aplicarLookupFuente(spline_lineal);

        return val;
    }

    private void splineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_splineActionPerformed
        if (this.spline.isSelected()) {
            double a = spline_a.getValue();
            double b = spline_b.getValue();
            double pts[];
            pts = aplicarSpline(a, b);
            paintGraph(a, b, pts);
        } else {
            spline_graph.setVisible(false);
            spline_graph = null;
        }
    }//GEN-LAST:event_splineActionPerformed

    private void spline_aStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spline_aStateChanged
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null && imgFuente != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    double a = spline_a.getValue();
                    double b = spline_b.getValue();
                    double val[];
                    val = aplicarSpline(a, b);
                    paintGraph(a, b, val);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_spline_aStateChanged

    private void spline_aFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_spline_aFocusGained
        this.focusGained();
    }//GEN-LAST:event_spline_aFocusGained

    private void spline_aFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_spline_aFocusLost
        imgFuente = null;
    }//GEN-LAST:event_spline_aFocusLost

    private void spline_bStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spline_bStateChanged
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null && imgFuente != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    double a = spline_a.getValue();
                    double b = spline_b.getValue();
                    double val[];
                    val = aplicarSpline(a, b);
                    paintGraph(a, b, val);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_spline_bStateChanged

    private void spline_bFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_spline_bFocusGained
        this.focusGained();
    }//GEN-LAST:event_spline_bFocusGained

    private void spline_bFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_spline_bFocusLost
        imgFuente = null;
    }//GEN-LAST:event_spline_bFocusLost

    private void slider_tintadoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider_tintadoStateChanged
        this.aplicarTintado(slider_tintado.getValue());
    }//GEN-LAST:event_slider_tintadoStateChanged

    private void slider_tintadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider_tintadoFocusGained
        this.focusGained();
    }//GEN-LAST:event_slider_tintadoFocusGained

    private void slider_tintadoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider_tintadoFocusLost
        imgFuente = null;
        slider_tintado.setValue(0);
    }//GEN-LAST:event_slider_tintadoFocusLost

    private void slider_rojoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider_rojoStateChanged
        this.aplicarRojoOp(slider_rojo.getValue());
    }//GEN-LAST:event_slider_rojoStateChanged

    private void slider_rojoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider_rojoFocusGained
        this.focusGained();
    }//GEN-LAST:event_slider_rojoFocusGained

    private void slider_rojoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider_rojoFocusLost
        imgFuente = null;
        slider_rojo.setValue(5);
    }//GEN-LAST:event_slider_rojoFocusLost

    private void slider_sat_colStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider_sat_colStateChanged
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null && imgFuente != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    int p = arcoiris.getValue();
                    VariarTonosOp variar = new VariarTonosOp(p);
                    float sat = slider_sat_col.getValue() / 10.0f;
                    variar.setSaturation(sat);
                    variar.filter(imgFuente, img);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_slider_sat_colStateChanged

    private void slider_sat_colFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider_sat_colFocusGained
        this.focusGained();
    }//GEN-LAST:event_slider_sat_colFocusGained

    private void slider_sat_colFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider_sat_colFocusLost
        imgFuente = null;
        slider_rojo.setValue(10);
    }//GEN-LAST:event_slider_sat_colFocusLost

    private void aplicarViñeta(float fact, boolean apply) {
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    MiOperador miop = new MiOperador(fact, apply);
                    miop.filter(img, img);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }

    private void slider_miOpStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider_miOpStateChanged
        float p = slider_miOp.getValue() / 10.0f;
        this.aplicarViñeta(p, vignette.isSelected());
    }//GEN-LAST:event_slider_miOpStateChanged

    private void slider_miOpFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider_miOpFocusGained
        this.focusGained();
    }//GEN-LAST:event_slider_miOpFocusGained

    private void slider_miOpFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider_miOpFocusLost
        imgFuente = null;
        slider_miOp.setValue(1);
    }//GEN-LAST:event_slider_miOpFocusLost

    private void abrirVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abrirVideoActionPerformed
        JFileChooser dlg = new JFileChooser();
        int resp = dlg.showOpenDialog(this);

        if (resp == JFileChooser.APPROVE_OPTION) {
            try {
                File f = dlg.getSelectedFile();
                VentanaInternaVideo vv = VentanaInternaVideo.getInstance(f);
                if (vv != null) {
                    vv.addMediaPlayerEventListener(new VideoListener());
                    panelEscritorio.add(vv);
                    vv.setVisible(true);
                } else {
                    System.out.println("Error ventana nula");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error al abrir el video");
                //ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_abrirVideoActionPerformed

    private void abrirCamaraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abrirCamaraActionPerformed
        VentanaInternaCamara vc = VentanaInternaCamara.getInstance();
        if (vc != null) {
            panelEscritorio.add(vc);
            vc.setVisible(true);
        }
    }//GEN-LAST:event_abrirCamaraActionPerformed

    private void capturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_capturarActionPerformed
        JInternalFrame vi1 = panelEscritorio.getSelectedFrame();
        if (vi1 instanceof VentanaInternaCamara) {
            VentanaInternaCamara vc = (VentanaInternaCamara) panelEscritorio.getSelectedFrame();
            if (vc != null) {
                BufferedImage img = vc.getImage();
                VentanaInternaImagen vi = new VentanaInternaImagen();
                vi.addInternalFrameListener(new manejadorVentanaInterna());
                vi.getLienzo().addLienzoListener(new MiManejadorLienzo());
                vi.getLienzo().setImage(img);
                panelEscritorio.add(vi);
                vi.setVisible(true);
            }
        }
        if (vi1 instanceof VentanaInternaVideo) {
            VentanaInternaVideo vv = (VentanaInternaVideo) panelEscritorio.getSelectedFrame();
            if (vv != null) {
                BufferedImage img = vv.getImage();
                VentanaInternaImagen vi = new VentanaInternaImagen();
                vi.addInternalFrameListener(new manejadorVentanaInterna());
                vi.getLienzo().addLienzoListener(new MiManejadorLienzo());
                vi.getLienzo().setImage(img);
                panelEscritorio.add(vi);
                vi.setVisible(true);
            }
        }

    }//GEN-LAST:event_capturarActionPerformed

    private void abrirVideo_barraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abrirVideo_barraActionPerformed
        this.abrirVideoActionPerformed(evt);
    }//GEN-LAST:event_abrirVideo_barraActionPerformed

    private void abrirCamara_barraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abrirCamara_barraActionPerformed
        this.abrirCamaraActionPerformed(evt);
    }//GEN-LAST:event_abrirCamara_barraActionPerformed

    private void capturar_barraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_capturar_barraActionPerformed
        this.capturarActionPerformed(evt);
    }//GEN-LAST:event_capturar_barraActionPerformed

    private void abrirSonido_barraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abrirSonido_barraActionPerformed
        this.abrirAudioActionPerformed(evt);
    }//GEN-LAST:event_abrirSonido_barraActionPerformed

    private void negro1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_negro1ActionPerformed
        getLienzoSeleccionado().setColorRelleno(Color.BLACK);
    }//GEN-LAST:event_negro1ActionPerformed

    private void rojo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rojo1ActionPerformed
        getLienzoSeleccionado().setColorRelleno(Color.RED);
    }//GEN-LAST:event_rojo1ActionPerformed

    private void amarillo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_amarillo1ActionPerformed
        getLienzoSeleccionado().setColorRelleno(Color.YELLOW);
    }//GEN-LAST:event_amarillo1ActionPerformed

    private void azul1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_azul1ActionPerformed
        getLienzoSeleccionado().setColorRelleno(Color.BLUE);
    }//GEN-LAST:event_azul1ActionPerformed

    private void verde1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verde1ActionPerformed
        getLienzoSeleccionado().setColorRelleno(Color.GREEN);
    }//GEN-LAST:event_verde1ActionPerformed

    private void more_colors1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_more_colors1MouseClicked
        Color color = JColorChooser.showDialog(this, "Elige un color", Color.RED);
        this.getLienzoSeleccionado().setColorRelleno(color);
    }//GEN-LAST:event_more_colors1MouseClicked

    private void val_transpStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_val_transpStateChanged
        VentanaInternaImagen vi = (VentanaInternaImagen) panelEscritorio.getSelectedFrame();
        if (vi != null) {
            vi.getLienzo().setEstaTransparente(transparencia.isSelected());
            vi.getLienzo().repaint();
        }
        if (vi.getLienzo().getEstaTransparente()) {
            float valor = (float) val_transp.getValue() / 10.0f;
            vi.getLienzo().setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, valor));
        } else {
            vi.getLienzo().setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        }
    }//GEN-LAST:event_val_transpStateChanged

    private void trazo_discontinuoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trazo_discontinuoActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) panelEscritorio.getSelectedFrame();
        if (vi != null) {
            Stroke trazo = null;
            if (trazo_discontinuo.isSelected()) {
                float[] patron = {5.0f, 5.0f};
                trazo = new BasicStroke((int) grosor.getValue(), BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 10.0f, patron, 0.0f);
            } else {
                trazo = new BasicStroke((int) grosor.getValue());
            }
            vi.getLienzo().setTrazo(trazo);
            vi.getLienzo().repaint();
        }
    }//GEN-LAST:event_trazo_discontinuoActionPerformed

    private void degradado_horizActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_degradado_horizActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) panelEscritorio.getSelectedFrame();
        if (vi != null) {
            degradado_horiz.setSelected(degradado_horiz.isSelected());
            if (degradado_horiz.isSelected()) {
                vi.getLienzo().setRelleno(2);
                bot_rellenar.setSelected(false);
                degradado_vert.setSelected(false);
            } else {
                vi.getLienzo().setRelleno(0);
            }
            vi.getLienzo().repaint();
        }
    }//GEN-LAST:event_degradado_horizActionPerformed

    private void degradado_vertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_degradado_vertActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) panelEscritorio.getSelectedFrame();
        if (vi != null) {
            degradado_vert.setSelected(degradado_vert.isSelected());
            if (degradado_vert.isSelected()) {
                vi.getLienzo().setRelleno(3);
                bot_rellenar.setSelected(false);
                degradado_horiz.setSelected(false);
            } else {
                vi.getLienzo().setRelleno(0);
            }
            vi.getLienzo().repaint();
        }
    }//GEN-LAST:event_degradado_vertActionPerformed

    private void acercaDeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acercaDeActionPerformed
        String txt = "Aplicación multimedia - Versión Final.\n"
                + "Desarrollado en NetBeans IDE 12.5\n"
                + "Compilado con JDK 1.8\n"
                + "Autora: Marina Jun Carranza Sánchez";
        JOptionPane.showMessageDialog(null, txt);

    }//GEN-LAST:event_acercaDeActionPerformed

    private void slider_rotacionStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider_rotacionStateChanged
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    double grados = slider_rotacion.getValue();
                    AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(grados), vi.getWidth() / 2, vi.getHeight() / 2);
                    AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imgdest = atop.filter(img, null);
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_slider_rotacionStateChanged

    private void slider_rotacionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider_rotacionFocusGained
        this.focusGained();
    }//GEN-LAST:event_slider_rotacionFocusGained

    private void slider_rotacionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider_rotacionFocusLost
        imgFuente = null;
        //slider_rotacion.setValue(0);
    }//GEN-LAST:event_slider_rotacionFocusLost

    private double calcularMaximo(double m) {
        double max = Double.NEGATIVE_INFINITY;

        for (int x = 0; x < 256; x++) {
            double val = ((50.0 - (double) x) * (Math.pow((double) m / 23.0, 3)));
            if (val > max) {
                max = val;
            }
        }
        return max;
    }

    private double normalizar(double x, double min, double max) {
        x = Math.max(Math.min(x, max), min);

        // Calcula el rango de valores de entrada y salida
        double inputRange = max - min;
        double outputRange = 255;

        // Aplica la fórmula de escala lineal
        double normalizedValue = (double) (x - min) / inputRange * outputRange;

        return normalizedValue;
    }

    private void aplicarMiFuncion(double m) {
        byte lt[] = new byte[256];

        for (int x = 0; x < 256; x++) {
            double val = (double) m * Math.atan(x - 120.0) + 120.0;
            val = (double) Math.max(0.0, Math.min(val, 255.0));
            lt[x] = (byte) val;  
        }
        ByteLookupTable cubica = new ByteLookupTable(0, lt);
        aplicarLookupFuente(cubica);
    }

    private void mi_lookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_lookupActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null && imgFuente != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    this.aplicarMiFuncion(0.0);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_mi_lookupActionPerformed

    private void slider_miLookupStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider_miLookupStateChanged
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null && imgFuente != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    double m = (double) slider_miLookup.getValue();
                    this.aplicarMiFuncion(m);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_slider_miLookupStateChanged

    private void slider_miLookupFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider_miLookupFocusGained
        this.focusGained();
    }//GEN-LAST:event_slider_miLookupFocusGained

    private void slider_miLookupFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider_miLookupFocusLost
        imgFuente = null;
        this.slider_miLookup.setValue(0);
    }//GEN-LAST:event_slider_miLookupFocusLost

    private void vignetteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vignetteActionPerformed
        float p = slider_miOp.getValue() / 10.0f;
        this.aplicarViñeta(p, vignette.isSelected());
    }//GEN-LAST:event_vignetteActionPerformed

    private void edicionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edicionActionPerformed
        linea.setSelected(false);
        rectangulo.setSelected(false);
        elipse.setSelected(false);
        trazo.setSelected(false);
        curva.setSelected(false);
        smile.setSelected(false);
        seleccion.setSelected(false);

        this.getLienzoSeleccionado().setEditar(edicion.isSelected());
    }//GEN-LAST:event_edicionActionPerformed

    class ManejadorAudio implements LineListener {
        
        @Override
        public void update(LineEvent event) {
            if (event.getType() == LineEvent.Type.START) {
                play_button.setEnabled(false);
            }
            if (event.getType() == LineEvent.Type.STOP) {
                play_button.setEnabled(true);
            }
            if (event.getType() == LineEvent.Type.CLOSE) {
            }
        }
    }

    private void cambiarEspColor(int espCol) {
        VentanaInternaImagen vi = (VentanaInternaImagen) (panelEscritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImg();
            if (img != null) {
                try {
                    ColorSpace cs = ColorSpace.getInstance(espCol);//ColorSpace.getInstance(ColorSpace.CS_GRAY);
                    ColorConvertOp op = new ColorConvertOp(cs, null);
                    BufferedImage imgdest = op.filter(img, null);
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }

    private BufferedImage getImageBand(BufferedImage img, int banda) {
        //Creamos el modelo de color de la nueva imagen basado en un espcio de color GRAY
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ComponentColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        //Creamos el nuevo raster a partir del raster de la imagen original
        int vband[] = {banda};
        WritableRaster bRaster = (WritableRaster) img.getRaster().createWritableChild(0, 0, img.getWidth(), img.getHeight(), 0, 0, vband);
        //Creamos una nueva imagen que contiene como raster el correspondiente a la banda
        return new BufferedImage(cm, bRaster, false, null);

    }

    private class VideoListener extends MediaPlayerEventAdapter {

        @Override
        public void playing(MediaPlayer mediaPlayer) {
            stop_button.setEnabled(true);
            play_button.setEnabled(false);
        }

        @Override
        public void paused(MediaPlayer mediaPlayer) {
            stop_button.setEnabled(false);
            play_button.setEnabled(true);
        }

        @Override
        public void finished(MediaPlayer mediaPlayer) {
            this.paused(mediaPlayer);
        }
    }

// 1) Definir clase Manejadora
// 2) Crear objeto manejador (en evento)
// 3) Enlazar generador con manejador
    private class manejadorVentanaInterna extends InternalFrameAdapter {

        @Override
        public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            VentanaInternaImagen vi = (VentanaInternaImagen) evt.getInternalFrame();

            // sincronización de botones de la interfaz
            switch (vi.getLienzo().getTipoForma()) {
                case 1:
                    linea.setSelected(true);
                    rectangulo.setSelected(false);
                    elipse.setSelected(false);
                    trazo.setSelected(false);
                    curva.setSelected(false);
                    smile.setSelected(false);
                    break;
                case 2:
                    rectangulo.setSelected(true);
                    linea.setSelected(false);
                    elipse.setSelected(false);
                    trazo.setSelected(false);
                    curva.setSelected(false);
                    smile.setSelected(false);
                    break;
                case 3:
                    elipse.setSelected(true);
                    rectangulo.setSelected(false);
                    linea.setSelected(false);
                    trazo.setSelected(false);
                    curva.setSelected(false);
                    smile.setSelected(false);
                    break;
                case 4:
                    trazo.setSelected(true);
                    rectangulo.setSelected(false);
                    elipse.setSelected(false);
                    linea.setSelected(false);
                    curva.setSelected(false);
                    smile.setSelected(false);
                    break;
                case 5:
                    curva.setSelected(true);
                    rectangulo.setSelected(false);
                    elipse.setSelected(false);
                    trazo.setSelected(false);
                    linea.setSelected(false);
                    smile.setSelected(false);
                    break;
                case 6:
                    smile.setSelected(true);
                    rectangulo.setSelected(false);
                    elipse.setSelected(false);
                    trazo.setSelected(false);
                    curva.setSelected(false);
                    linea.setSelected(false);
                    break;
            }

            if (vi.getLienzo().getRelleno() == 0) {
                bot_rellenar.setSelected(false);
            } else if (vi.getLienzo().getRelleno() == 1) {
                bot_rellenar.setSelected(true);
            } else if (vi.getLienzo().getRelleno() == 2) {
                degradado_horiz.setSelected(true);
            } else {
                degradado_vert.setSelected(true);
            }
            seleccion.setSelected(vi.getLienzo().getMover());
            transparencia.setSelected(vi.getLienzo().getEstaTransparente());
            alisar.setSelected(vi.getLienzo().getEstaAlisado());
            edicion.setSelected(vi.getLienzo().getEditar());
            if(!vi.getLienzo().getEditar()){
                vi.getLienzo().stopEdicion();
            }
            if (!vi.getLienzo().getMover()){
                vi.getLienzo().setMover(false);
            }
            
            DefaultListModel modelo = new DefaultListModel();
            List<Forma2D> formas = vi.getLienzo().getShapeList();
            for (int i = 0; i < vi.getLienzo().getShapeList().size(); i++) {
                modelo.addElement(formas.get(i));
            }
            listaFormas.setModel(modelo);
        }

        @Override
        public void internalFrameClosing(InternalFrameEvent evt) {
            //VentanaInterna vi = (VentanaInternaImagen) evt.getInternalFrame();
            ((DefaultListModel) listaFormas.getModel()).removeAllElements();
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem abrir;
    private javax.swing.JMenuItem abrirAudio;
    private javax.swing.JMenuItem abrirCamara;
    private javax.swing.JButton abrirCamara_barra;
    private javax.swing.JButton abrirSonido_barra;
    private javax.swing.JMenuItem abrirVideo;
    private javax.swing.JButton abrirVideo_barra;
    private javax.swing.JMenuItem acercaDe;
    private javax.swing.JMenuItem affineTransformOP;
    private javax.swing.JToggleButton alisar;
    private javax.swing.JToggleButton amarillo;
    private javax.swing.JToggleButton amarillo1;
    private javax.swing.JSlider arcoiris;
    private javax.swing.JButton aumentar;
    private javax.swing.JMenu ayuda_menu;
    private javax.swing.JToggleButton azul;
    private javax.swing.JToggleButton azul1;
    private javax.swing.JButton bandas;
    private javax.swing.JMenuItem bandcombineOP;
    private javax.swing.JMenuBar barra_archivo;
    private javax.swing.JLabel barra_est;
    private javax.swing.JToolBar barra_formas;
    private javax.swing.JButton bot_abrir;
    private javax.swing.JButton bot_guardar;
    private javax.swing.JButton bot_nuevo;
    private javax.swing.JToggleButton bot_rellenar;
    private javax.swing.JSlider brillo_slider;
    private javax.swing.JMenuItem capturar;
    private javax.swing.JButton capturar_barra;
    private javax.swing.JMenuItem colorspaceOP;
    private javax.swing.JButton combinar;
    private javax.swing.JButton contraste;
    private javax.swing.JSlider contraste_slider;
    private javax.swing.JMenuItem convolveOP;
    private javax.swing.JButton cuadratica;
    private javax.swing.JToggleButton curva;
    private javax.swing.JToggleButton degradado_horiz;
    private javax.swing.JToggleButton degradado_vert;
    private javax.swing.JButton disminuir;
    private javax.swing.JButton duplicado_button;
    private javax.swing.JMenuItem duplicar;
    private javax.swing.JButton ecualizar;
    private javax.swing.JToggleButton edicion;
    private javax.swing.JToggleButton elipse;
    private javax.swing.JComboBox<String> filtro_menu;
    private javax.swing.JSpinner grosor;
    private javax.swing.JMenuItem guardar;
    private javax.swing.JToggleButton histograma;
    private javax.swing.JButton iluminar;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToggleButton linea;
    private javax.swing.JList<Forma2D> listaFormas;
    private javax.swing.JMenuItem lookupOP;
    private javax.swing.JSlider m_cuadratica;
    private javax.swing.JComboBox<String> menu_espacioColor;
    private javax.swing.JMenu menu_mg;
    private javax.swing.JButton mi_lookup;
    private javax.swing.JLabel more_colors;
    private javax.swing.JLabel more_colors1;
    private javax.swing.JButton negativo;
    private javax.swing.JToggleButton negro;
    private javax.swing.JToggleButton negro1;
    private javax.swing.JMenuItem nuevo;
    private javax.swing.JButton oscurecer;
    private javax.swing.JDesktopPane panelEscritorio;
    private javax.swing.JPanel panelListaFormas;
    private javax.swing.JPanel panel_brillocontraste;
    private javax.swing.JPanel panel_col;
    private javax.swing.JPanel panel_col2;
    private javax.swing.JPanel panel_color_rgb;
    private javax.swing.JPanel panel_colores;
    private javax.swing.JPanel panel_est;
    private javax.swing.JPanel panel_inf;
    private javax.swing.JPanel panel_rotescale;
    private javax.swing.JPanel panel_tintado;
    private javax.swing.JPanel panel_transformaciones;
    private javax.swing.JButton play_button;
    private javax.swing.JButton record_button;
    private javax.swing.JToggleButton rectangulo;
    private javax.swing.JMenuItem rescaleOP;
    private javax.swing.JToggleButton rojo;
    private javax.swing.JToggleButton rojo1;
    private javax.swing.JButton rojo_efecto;
    private javax.swing.JButton rotacion;
    private javax.swing.JToggleButton seleccion;
    private javax.swing.JSeparator separador;
    private javax.swing.JButton sepia;
    private javax.swing.JSlider slider_miLookup;
    private javax.swing.JSlider slider_miOp;
    private javax.swing.JSlider slider_posterizar;
    private javax.swing.JSlider slider_rojo;
    private javax.swing.JSlider slider_rotacion;
    private javax.swing.JSlider slider_sat_col;
    private javax.swing.JSlider slider_tintado;
    private javax.swing.JToggleButton smile;
    private javax.swing.JMenu sonido_menu;
    private javax.swing.JMenu sonido_menu1;
    private javax.swing.JComboBox<File> sound_list;
    private javax.swing.JToggleButton spline;
    private javax.swing.JSlider spline_a;
    private javax.swing.JSlider spline_b;
    private javax.swing.JSplitPane splitPanel;
    private javax.swing.JButton stop_button;
    private javax.swing.JLabel temporizador;
    private javax.swing.JLabel tiempo_total;
    private javax.swing.JButton tintar;
    private javax.swing.JToggleButton transparencia;
    private javax.swing.JToggleButton trazo;
    private javax.swing.JToggleButton trazo_discontinuo;
    private javax.swing.JSlider val_transp;
    private javax.swing.JToggleButton verde;
    private javax.swing.JToggleButton verde1;
    private javax.swing.JToggleButton vignette;
    private javax.swing.JButton volcado;
    // End of variables declaration//GEN-END:variables
}
