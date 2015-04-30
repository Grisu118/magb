package imageprocessing;

import main.PicsiSWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

import javax.swing.*;

/**
 * Created by benjamin on 22.04.2015.
 */
public class Dithering implements IImageProcessor {

    @Override
    public boolean isEnabled(int imageType) {
        return imageType == PicsiSWT.IMAGE_TYPE_GRAY;
    }

    @Override
    public Image run(Image input, int imageType) {
        byte[] mask = new byte[]{(byte) 0b10000000, 0b01000000, 0b00100000, 0b00010000, 0b00001000, 0b00000100, 0b00000010, 0b00000001};
        int threshold = 128;

        RGB[] colors = new RGB[2];
        colors[0] = new RGB(0,0,0);
        colors[1] = new RGB(255,255,255);
        PaletteData paletteData = new PaletteData(colors);
        int width = input.getImageData().width;
        int height = input.getImageData().height;
        byte[] oldData = input.getImageData().data;
        double[] errorData = new double[oldData.length];
        ImageData imgData = new ImageData(width, height, 1, paletteData);
        byte[] newData = imgData.data;
        int j = 0;
        if (getAlgorithm() == 0) {
            for (int i = 0; i < oldData.length; i++) {
                int val = 0xFF & oldData[i];
                int error;
                val += errorData[i];
                if (val > threshold) {
                    newData[j] = (byte) (newData[j] | mask[i % 8]);
                    error  = val-255;
                } else {
                    error = val;
                }
                if (i + 1 < errorData.length && (i + 1) % width < width-1) {
                    errorData[i + 1] += ( error * 7.0 / 16);
                }
                if (i + width - 1 < errorData.length  && (i - 1) % width > 0) {
                    errorData[i + width - 1] += ( error * 3.0 / 16);
                }
                if (i + width < errorData.length) {
                    errorData[i + width] += ( error * 5.0 / 16);
                }
                if (i + width + 1 < errorData.length  && (i + 1) % width < width-1) {
                    errorData[i + width + 1] += ( error * 1.0 / 16);
                }
                if (i % 8 == 7) {
                    j++;
                }
            }
        } else {
            for (int i = 0; i < oldData.length; i++) {
                int val = 0xFF & oldData[i];
                int error;

                val += errorData[i];

                if (val > threshold) {
                    newData[j] = (byte) (newData[j] | mask[i % 8]);
                    error  = val-255;
                } else {
                    error = val;
                }
                if (i + 1 < errorData.length  && (i + 1) % width < width-1) {
                    errorData[i + 1] += error * 8 / 42.0;
                }
                if (i + 2 < errorData.length && (i + 2) % width < width-2) {
                    errorData[i + 2] += error * 4 / 42.0;
                }
                if (i + width - 2 < errorData.length  && (i - 2) % width > 1) {
                    errorData[i + width - 2] += error * 2 / 42.0;
                }
                if (i + width - 1 < errorData.length  && (i - 1) % width > 0) {
                    errorData[i + width - 1] += error * 4 / 42.0;
                }
                if (i + width < errorData.length) {
                    errorData[i + width] += error * 8 / 42.0;
                }
                if (i + width + 1 < errorData.length   && (i + 1) % width < width-1) {
                    errorData[i + width + 1] += error * 4 / 42.0;
                }
                if (i + width + 2 < errorData.length   && (i + 2) % width < width-2) {
                    errorData[i + width + 2] += error * 2 / 42.0;
                }

                if (i + width*2 - 2 < errorData.length   && (i - 2) % width > 1) {
                    errorData[i + width*2 - 2] += error * 1 / 42.0;
                }
                if (i + width*2 - 1 < errorData.length   && (i - 2) % width > 0) {
                    errorData[i + width*2 - 1] += error * 2 / 42.0;
                }
                if (i + width*2 < errorData.length) {
                    errorData[i + width*2] += error * 4 / 42.0;
                }
                if (i + width*2 + 1 < errorData.length   && (i + 1) % width < width-1) {
                    errorData[i + width*2 + 1] += error * 2 / 42.0;
                }
                if (i + width*2 + 2 < errorData.length   && (i + 2) % width < width-1) {
                    errorData[i + width*2 + 2] += error * 1 / 42.0;
                }

                if (i % 8 == 7) {
                    j++;
                }
            }
        }


        return new Image(input.getDevice(), imgData);
    }

    private int getAlgorithm() {
        Object[] options = {"Floyd Steinberg",
                "Stucki"};
        return JOptionPane.showOptionDialog(null,
                "Welchen Algorithmus willst du verwenden?",
                "Dithering",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }
}
