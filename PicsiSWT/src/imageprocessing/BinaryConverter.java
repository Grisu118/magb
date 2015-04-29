package imageprocessing;

import gui.MainWindow;
import main.PicsiSWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import utils.Parallel;

/**
 * Created by benjamin on 22.04.2015.
 */
public class BinaryConverter implements IImageProcessor {

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
        ImageData imgData = new ImageData(width, height, 1, paletteData);
        byte[] newData = imgData.data;
        int j = 0;
        for (int i = 0; i < oldData.length; i++) {
            int val = 0xFF & oldData[i];

            if (val > threshold) {
                newData[j] = (byte) (newData[j] | mask[i % 8]);

            }
            if (i%8 == 7) {
                j++;
            }
        }


        return new Image(input.getDevice(), imgData);
    }
}
