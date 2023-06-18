package sm.mcs.imagen;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import sm.image.BufferedImageOpAdapter;
import sm.image.color.ColorConvertOp;

/**
 * Clase VariarTonosOp.
 * 
 * Representa un operador que varía los tonos de color de una imagen RGB.
 * Extiende la clase BufferedImageOpAdapter para proporcionar una 
 * implementación de filtro personalizado.
 * 
 * @author Marina Jun Carranza Sánchez
 */
public class VariarTonosOp extends BufferedImageOpAdapter {

    /**
     * El parámetro de variación de tonos a aplicar.
     */
    private int param;
    private float satur;

    public VariarTonosOp(int param) {
        this.param = param;
        this.satur = 1.0f;
    }
    
    public void setSaturation(float sat){
        this.satur = sat;
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

                // 1. Convertir el color RGB a HSB
                float[] hsbCol = Color.RGBtoHSB(pixelComp[0], pixelComp[1], pixelComp[2], null);
                float H = hsbCol[0];
                // añadir saturación pixel y pixel

                // 2. Aplicar la transformación a H∈[0,360]:
                float newH = (float) (((H * 360.0 + param) % 360.0) / 360.0);
                hsbCol[0] = newH;
                
                // 2.1 Modificar la saturación
                hsbCol[1] *= satur;
                hsbCol[1] = Math.max(0, Math.min(hsbCol[1], 1));

                // 3. Convertir el nuevo color HSB a RGB
                int rgb = Color.HSBtoRGB(hsbCol[0], hsbCol[1], hsbCol[2]);
                pixelCompDest[0] = (rgb >> 16) & 0xFF; // R
                pixelCompDest[1] = (rgb >> 8) & 0xFF; // G
                pixelCompDest[2] = rgb & 0xFF; // B

                destRaster.setPixel(x, y, pixelCompDest);
            }
        }
        return dest;
    }
}
