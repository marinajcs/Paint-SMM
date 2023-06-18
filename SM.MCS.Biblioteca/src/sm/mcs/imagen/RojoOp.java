package sm.mcs.imagen;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import sm.image.BufferedImageOpAdapter;

/**
 * Clase RojoOp.
 * Representa un operador que resalta el color rojo en una imagen.
 * Extiende la clase BufferedImageOpAdapter para proporcionar una 
 * implementación de filtro personalizado.
 * 
 * @author Marina Jun Carranza Sánchez
 */
public class RojoOp extends BufferedImageOpAdapter {

    /**
     * El umbral que determina si un píxel se considera predominantemente rojo.
     */
    private int umbral;

    /**
     * Constructor de la clase.
     * 
     * Se le pasa como parámetro el valor del umbral.
     *
     * @param umbral umbral que determina si un píxel se considera predominantemente rojo.
     */
    public RojoOp(int umbral) {
        this.umbral = umbral;
    }

    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (src == null) {
            throw new NullPointerException("src image is null");
        }
        if (dest == null) {
            dest = createCompatibleDestImage(src, null);
        }
        WritableRaster srcRaster = src.getRaster();
        WritableRaster destRaster = dest.getRaster();
        int[] pixelComp = new int[srcRaster.getNumBands()];
        int[] pixelCompDest = new int[srcRaster.getNumBands()];
        
        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                srcRaster.getPixel(x, y, pixelComp);
                //Efecto resaltar rojo
                int Rf = pixelComp[0];
                int Gf = pixelComp[1];
                int Bf = pixelComp[2];

                if ((Rf - Gf - Bf) >= umbral) { // Px predominantemente rojo
                    pixelCompDest[0] = Rf;
                    pixelCompDest[1] = Gf;
                    pixelCompDest[2] = Bf;
                } else {                      // Px no es predominantemente rojo
                    int media = (Rf + Gf + Bf) / 3;
                    pixelCompDest[0] = media;
                    pixelCompDest[1] = media;
                    pixelCompDest[2] = media;
                }
                
                destRaster.setPixel(x, y, pixelCompDest);
            }
        }
        return dest;
    }
}
