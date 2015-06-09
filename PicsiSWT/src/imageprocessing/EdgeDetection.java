package imageprocessing;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import utils.Parallel;

import javax.swing.*;

/**
 * Created by benjamin on 07.06.2015.
 */
public class EdgeDetection implements IImageProcessor {
    @Override
    public boolean isEnabled(int imageType) {
        return true;
    }

    @Override
    public Image run(Image input, int imageType) {
        int n = optionDialog();
        ImageData inData = (ImageData) input.getImageData();
        int[][] gauss = {{1,1,1}, {1,1,1}, {1,1,1}};
        inData = ImageProcessing.convolve(inData, imageType, gauss, 9, 0);
        switch (n) {
            case 0: {
                int[][] filter = {{-1, 0, 1}, {-1, 0, 1}, {-1, 0, 1}};
                inData = ImageProcessing.convolve(inData, imageType, filter, 2, 0);
                break;
            }
            case 1: {
                int[][] filter2 = {{-1, -1, -1}, {0, 0, 0}, {1, 1, 1}};
                inData = ImageProcessing.convolve(inData, imageType, filter2, 2, 0);
                break;
            }
            case 2: {
                int[][] filter = {{-1, 0, 1}, {-1, 0, 1}, {-1, 0, 1}};
                final ImageData Dx = ImageProcessing.convolve(inData, imageType, filter, 2, 0);
                int[][] filter2 = {{-1, -1, -1}, {0, 0, 0}, {1, 1, 1}};
                final ImageData Dy = ImageProcessing.convolve(inData, imageType, filter2, 2, 0);

                final ImageData finalInData = inData;
                Parallel.For(0, Dx.height, v -> {
                    for (int u = 0; u < Dx.width; u++) {
                        int px = Dx.getPixel(u, v);
                        int py = Dy.getPixel(u, v);
                        RGB x = Dx.palette.getRGB(px);
                        RGB y = Dy.palette.getRGB(py);

                        RGB q = new RGB(x.red + y.red, x.green + y.green, x.blue + y.blue);
                        finalInData.setPixel(u, v, finalInData.palette.getPixel(q));
                    }
                });
                inData = finalInData;
            }
        }


        return new Image(input.getDevice(), inData);

    }

    public int optionDialog(){
        Object[] options = {"X","Y","Both"};
        return JOptionPane.showOptionDialog(null,
                "Choose an option",
                "",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                options,  //the titles of buttons
                options[0]);
    }

}
