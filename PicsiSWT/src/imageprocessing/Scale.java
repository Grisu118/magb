package imageprocessing;

import main.PicsiSWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import utils.Matrix;
import utils.Parallel;

import javax.swing.*;

/**
 * Created by benjamin on 27.05.2015.
 */
public class Scale implements IImageProcessor {
    @Override
    public boolean isEnabled(int imageType) {
        return imageType != PicsiSWT.IMAGE_TYPE_INDEXED;
    }

    @Override
    public Image run(Image input, int imageType) {
        ImageData inData = input.getImageData();
        ImageData outData = (ImageData) inData.clone();

        double scale = (Double.parseDouble(JOptionPane.showInputDialog("Um welchen Wert scalieren?")));

        int mx = outData.width/2;
        int my = outData.height/2;
        Parallel.For(0, outData.height, v -> {
                for (int u = 0; u < outData.width; u++) {

                    double x = u/scale;
                    double y = v/scale;

                    if (x < 0 || x >= outData.width - 1
                            || y < 0 || y >= outData.height - 1) {
                        outData.setPixel(u, v, 0);
                        outData.setAlpha(u, v, 0);
                    } else {
                        int uA = (int)x;
                        int vA = (int)y;
                        int uB = uA + 1;
                        if (uB >= inData.width) {
                            uB = uA;
                        }
                        int vC = vA + 1;
                        if (vC >= inData.height) {
                            vC = vA;
                        }
                        int uD = uB;
                        int vD = vC;

                        double a = x-uA;
                        double b = y-vA;

                        RGB A = inData.palette.getRGB(inData.getPixel(uA, vA));
                        int aA = inData.getAlpha(uA, vA);
                        RGB B = inData.palette.getRGB(inData.getPixel(uB, vA));
                        int aB = inData.getAlpha(uB, vA);
                        RGB C = inData.palette.getRGB(inData.getPixel(uA, vC));
                        int aC = inData.getAlpha(uA, vA);
                        RGB D = inData.palette.getRGB(inData.getPixel(uD, vD));
                        int aD = inData.getAlpha(uD, vD);

                        RGB E = new RGB((int)((A.red + a * (B.red - A.red))),(int)((A.green + a * (B.green - A.green))),(int)((A.blue + a * (B.blue - A.blue))));
                        int aE = (int) (aA + a * (aB - aA));
                        RGB F = new RGB((int)((C.red + a * (D.red - C.red))),(int)((C.green + a * (D.green - C.green))),(int)((C.blue + a * (D.blue - C.blue))));
                        int aF = (int) (aC + a* (aD - aC));
                        RGB G = new RGB((int)((E.red + b * (F.red - E.red))),(int)((E.green + b * (F.green - E.green))),(int)((E.blue + b * (F.blue - E.blue))));
                        int aG = (int) (aE + b * (aF - aE));

                        outData.setPixel(u, v, outData.palette.getPixel(G));
                        outData.setAlpha(u, v, aG);
                    }
                }
            });

        return new Image(input.getDevice(), outData);
    }
}
