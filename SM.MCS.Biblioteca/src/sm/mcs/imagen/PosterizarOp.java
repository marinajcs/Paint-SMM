package sm.mcs.imagen;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import sm.image.BufferedImageOpAdapter;

/**
 * Clase PosterizarOp. 
 * 
 * Representa un operador de posterización que reduce el número de niveles 
 * de color en una imagen.
 * Extiende la clase BufferedImageOpAdapter para proporcionar una 
 * implementación de filtro personalizado.
 * 
 * @author Marina Jun Carranza Sánchez
 */
public class PosterizarOp extends BufferedImageOpAdapter {

     /**
     * El número de niveles de color a utilizar en la posterización.
     */
    private int niveles;

    /**
     * Constructor de la clase.
     * 
     * Se le pasa como parámetro el número de niveles de color especificado.
     *
     * @param niveles El número de niveles de color para la posterización.
     */
    public PosterizarOp(int niveles) {
        this.niveles = niveles;
    }

    /**
     * Aplica el efecto de posterización a la imagen de origen, reduciendo el número de niveles de color.
     *
     * @param src imagen origen a filtrar
     * @param dest imagen destino donde se almacenará el resultado del filtro. Si es null, se crea una nueva imagen.
     * @return imagen destino con el efecto de posterización
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (src == null) {
            throw new NullPointerException("src image is null");
        }
        if (dest == null) {
            dest = createCompatibleDestImage(src, null);
        }
        WritableRaster srcRaster = src.getRaster();
        WritableRaster destRaster = dest.getRaster();
        int sample;
        
        float K = 256.f/niveles;
        
        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                for (int band = 0; band < srcRaster.getNumBands(); band++) {
                    sample = srcRaster.getSample(x, y, band);
                    // Efecto posterizar
                    sample = (int)(K * (int)(sample/K));
                    destRaster.setSample(x, y, band, sample);
                }
            }
        }
        return dest;
    }
}
