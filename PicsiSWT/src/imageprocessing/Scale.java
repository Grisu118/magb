package imageprocessing;

import main.PicsiSWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
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

        Matrix matrix = Matrix.translation(mx, my)
                .multiply(Matrix.scaling(scale, scale))
                .multiply(Matrix.translation(-mx, -my))
                .inverse();

        int A, B, C, D;
        Parallel.For(0, outData.height, v -> {
            double[] v1 = {0, v, 1};

            for (int u = 0; u < outData.width; u++) {
                v1[0] = u;
                double[] v2 = matrix.multiply(v1);
                int uu = (int) Math.round(v2[0] / v2[2]);
                int vv = (int) Math.round(v2[1] / v2[2]);
                if (uu < 0 || uu >= outData.width
                        || vv < 0 || vv >= outData.height) {
                    outData.setPixel(u, v, 0);
                    outData.setAlpha(u, v, 0);
                } else {
                    outData.setPixel(u, v, inData.getPixel(uu, vv));
                    outData.setAlpha(u, v, inData.getAlpha(uu, vv));
                }
            }
        });

        return new Image(input.getDevice(), outData);
    }
}
