package imageprocessing;

import main.PicsiSWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
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
        int threshold = 128;
        ImageData inData = input.getImageData();
        for (int v = 0; v < inData.height; v++) {
            int failure = 0;
            for (int u = 0; u < inData.width; u++) {
                int pixel = inData.getPixel(u, v);
                if (pixel + failure < threshold) {
                    failure += pixel;
                    pixel = 0;
                } else {
                    pixel = 255;
                    failure -= 255;
                    if (failure > 255) {
                        failure = 0;
                    }
                }
                inData.setPixel(u, v, pixel);
            }
        }


        return new Image(input.getDevice(), inData);
    }
}
