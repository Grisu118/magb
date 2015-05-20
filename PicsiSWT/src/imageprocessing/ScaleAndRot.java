package imageprocessing;

import main.PicsiSWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import utils.Matrix;
import utils.Parallel;

import javax.swing.*;

/**
 * Class for Scale and Rotation.
 */
public class ScaleAndRot implements IImageProcessor {
    @Override
    public boolean isEnabled(int imageType) {
        return imageType != PicsiSWT.IMAGE_TYPE_INDEXED;
    }

    @Override
    public Image run(Image input, int imageType) {
        ImageData inData = input.getImageData();
        ImageData outData = (ImageData) inData.clone();

        double alpha = Math.toRadians(Double.parseDouble(JOptionPane.showInputDialog("Um welchen Winkel rotieren?")));
        double scale = (Double.parseDouble(JOptionPane.showInputDialog("Um welchen Wert scalieren?")));

        int mx = outData.width/2;
        int my = outData.height/2;


        Matrix A = Matrix.translation(mx,my)
                .multiply(Matrix.rotation(alpha))
                .multiply(Matrix.scaling(scale, scale))
                .multiply(Matrix.translation(-mx, -my))
                .inverse();

        Parallel.For(0, outData.height, v -> {
            double[] v1 = {0, v, 1};
            for (int u = 0; u < outData.width; u++) {
                v1[0] = u;
                double[] v2 = A.multiply(v1);
                int uu = (int)Math.round(v2[0] / v2[2]);
                int vv = (int)Math.round(v2[1] / v2[2]);
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
