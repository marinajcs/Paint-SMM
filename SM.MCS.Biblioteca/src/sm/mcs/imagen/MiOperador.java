package sm.mcs.imagen;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import sm.image.BufferedImageOpAdapter;

/**
 * Clase MiOperador.
 * 
 * Extiende la clase 'BufferedImageOpAdapter'.
 * Proporciona un efecto personalizado para modificar píxel a píxel
 * la intensidad de una imagen, dándole un efecto de viñeteado.
 * Permite aplicar o deshacer el efecto, con un factor de intensidad 
 * modificable. Se basa en calcular la distancia del píxel al centro 
 * de la imagen.
 * 
 * @author Marina Jun Carranza Sánchez
 */
public class MiOperador extends BufferedImageOpAdapter {

    /**
     * Factor de intensidad del efecto que será modificable.
     */
    private float param;
    
    /**
     * Variable de control para saber si aplicar el efecto o revertirlo.
     */
    private boolean apply;

    /**
     * Constructor de la clase.
     * 
     * Se le pasa como parámetros el valor del factor y un boolean
     * que indica si aplicar o deshacer el efecto.
     * 
     * @param param factor de intensidad
     * @param apply indica si hay que aplicar o deshacer el efecto
     */
    public MiOperador(float param, boolean apply) {
        this.param = param;
        this.apply = apply;
    }

    /**
     * Método filter.
     * 
     * Aplica el efecto personalizado a una imagen.
     * El efecto modifica la intensidad de los píxeles en función 
     * de su distancia al centro de la imagen.
     * 
     * @param src imagen origen.
     * @param dest imagen destino donde se aplicará el efecto.
     * @return imagen destino con el efecto aplicado.
     */
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (src == null) {
            throw new NullPointerException("src image is null");
        }
        if (dest == null) {
            dest = createCompatibleDestImage(src, null);
        }
        
        float center_x = src.getWidth() / 2.0f;
        float center_y = src.getHeight() / 2.0f;
        float max_dist = (float) Math.sqrt(center_x * center_x + center_y * center_y);

        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                int rgb = src.getRGB(x, y);

                int r = (rgb >> 16) & 0xFF; // R
                int g = (rgb >> 8) & 0xFF; // G
                int b = rgb & 0xFF; // B

                float dist_x = x - center_x;
                float dist_y = y - center_y;
                float dist = (float) Math.sqrt(dist_x * dist_x + dist_y * dist_y);
                float fact_vig = 1.0f - (dist / max_dist) * param;

                if (apply) {
                    r = (int) (r * fact_vig);
                    g = (int) (g * fact_vig);
                    b = (int) (b * fact_vig);
                } else {
                    r = (int) (r / fact_vig);
                    g = (int) (g / fact_vig);
                    b = (int) (b / fact_vig);

                    r = Math.max(Math.min(r, 255), 0); // r:[0,255]
                    g = Math.max(Math.min(g, 255), 0);
                    b = Math.max(Math.min(b, 255), 0);
                }
                
                int nrgb = (r << 16) | (g << 8) | b;
                dest.setRGB(x, y, nrgb);
            }
        }
        return dest;
    }
}
