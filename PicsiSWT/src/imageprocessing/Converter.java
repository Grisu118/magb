package imageprocessing;

import main.PicsiSWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import utils.Parallel;

import javax.swing.*;

/**
 * Created by benjamin on 22.04.2015.
 */
public class Converter implements IImageProcessor {
    @Override
    public boolean isEnabled(int imageType) {
        return imageType == PicsiSWT.IMAGE_TYPE_RGB;
    }

    @Override
    public Image run(Image input, int imageType) {
        //Custom button text
        int n = getConverterType();

        ImageData inData = input.getImageData();

        RGB[] rgbs = new RGB[256];
        for (int i = 0; i < rgbs.length; i++) {
            rgbs[i] = new RGB(i,i,i);
        }
        PaletteData pallet = new PaletteData(rgbs);
        ImageData imgData = new ImageData(inData.width, inData.height, 8, pallet);
        switch (n) {
            case 0:
                Parallel.For(0, inData.height, v -> {
                    for (int u = 0; u < inData.width; u++) {
                        int pixel = inData.getPixel(u, v);
                        RGB rgb = inData.palette.getRGB(pixel);
                        int grayScale = (int) (rgb.red * 0.299 + rgb.green * 0.587 + rgb.blue * 0.114);
                        imgData.setPixel(u, v, grayScale);
                    }
                });
                break;
            case 1:
                Parallel.For(0, inData.height, v -> {
                    for (int u = 0; u < inData.width; u++) {
                        int pixel = inData.getPixel(u, v);
                        RGB rgb = inData.palette.getRGB(pixel);
                        rgb.green = rgb.red;
                        rgb.blue = rgb.red;
                        imgData.setPixel(u, v, rgb.red);
                    }
                });
                break;
            case 2:
                Parallel.For(0, inData.height, v -> {
                    for (int u = 0; u < inData.width; u++) {
                        int pixel = inData.getPixel(u, v);
                        RGB rgb = inData.palette.getRGB(pixel);
                        rgb.red = rgb.green;
                        rgb.blue = rgb.green;
                        imgData.setPixel(u, v, rgb.green);
                    }
                });
                break;
            case 3:
                Parallel.For(0, inData.height, v -> {
                    for (int u = 0; u < inData.width; u++) {
                        int pixel = inData.getPixel(u, v);
                        RGB rgb = inData.palette.getRGB(pixel);
                        rgb.red = rgb.blue;
                        rgb.green = rgb.blue;
                        imgData.setPixel(u, v, rgb.blue);
                    }
                });
                break;
        }
        return new Image(input.getDevice(), imgData);
    }

    private int getConverterType() {
        Object[] options = {"Gray",
                "Red",
                "Green",
                "Blue"};
        return JOptionPane.showOptionDialog(null,
                "In welchen Kanal moechten Sie konvertieren?",
                "Converter",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }
}
