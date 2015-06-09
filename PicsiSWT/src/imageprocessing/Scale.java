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

        Matrix matrix = Matrix.translation(mx, my)
                .multiply(Matrix.scaling(scale, scale))
                .multiply(Matrix.translation(-mx, -my))
                .inverse();

        int offset = 0;
        //Parallel.For(0, outData.height, v -> {
        try {
            for (int v = 0; v < outData.height; v++) {
                double[] v1 = {0, v, 1};

                for (int u = 0; u < outData.width; u++) {
                    v1[0] = u;
                    double[] v2 = matrix.multiply(v1);
                    int x = (int) (scale * u);
                    int y = (int) (scale * v);
                    double x_diff = (scale * v) - x;
                    double y_diff = (scale * u) - y;
                    if (x < 0 || x >= outData.width - 1
                            || y < 0 || y >= outData.height - 1) {
                        outData.setPixel(u, v, 0);
                        outData.setAlpha(u, v, 0);
                    } else {

                        RGB A = inData.palette.getRGB(inData.getPixel(x, y));
                        int Aa = inData.getAlpha(x, y);
                        RGB B = inData.palette.getRGB(inData.getPixel(x + 1, y));
                        int Ba = inData.getAlpha(x + 1, y);
                        RGB C = inData.palette.getRGB(inData.getPixel(x, y + 1));
                        int Ca = inData.getAlpha(x, y + 1);
                        RGB D = inData.palette.getRGB(inData.getPixel(x + 1, y + 1));
                        int Da = inData.getAlpha(x + 1, y + 1);
                        double Er = A.red + x_diff * (B.red - A.red);
                        double Gr = Er + y_diff * ((C.red + x_diff * (D.red - C.red)) - Er);
                        double Eg = A.green + x_diff * (B.green - A.green);
                        double Gg = Eg + y_diff * ((C.green + x_diff * (D.green - C.green)) - Eg);
                        double Eb = A.blue + x_diff * (B.blue - A.blue);
                        double Gb = Eb + y_diff * ((C.blue + x_diff * (D.blue - C.blue)) - Eb);
                        double Ea = Aa + x_diff * (Ba - Aa);
                        double Fa = Ca + x_diff * (Da - Ca);
                        double Ga = Ea + y_diff * (Fa - Ea);
                        RGB rgb = new RGB(ImageProcessing.clamp((int) Gr), ImageProcessing.clamp((int) Gg), ImageProcessing.clamp((int) Gb));
                        int pix = outData.palette.getPixel(rgb);
                        outData.setPixel(u, v, pix);
                        outData.setAlpha(u, v, (int) Ga);
                    }
                }
            }//);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Image(input.getDevice(), outData);
    }
}
