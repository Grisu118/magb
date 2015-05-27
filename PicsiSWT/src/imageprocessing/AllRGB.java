package imageprocessing;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import utils.Parallel;

/**
 * Created by benjamin on 27.05.2015.
 */
public class AllRGB implements IImageProcessor {
    @Override
    public boolean isEnabled(int imageType) {
        return true;
    }

    @Override
    public Image run(Image input, int imageType) {
        /*
        RGB[] colors = new RGB[4096*4096];
        int r = 0;
        int g = 0;
        int b = 0;
        for (int i = 0; i < colors.length; i++) {
            b = i%256;
            colors[i] = new RGB(r,g,b);
            if (b == 255) {
                g++;
            }
            if (g == 256) {
                g = 0;
                r++;
            }
        }
        PaletteData paletteData = new PaletteData(colors);
        */
        ImageData data = new ImageData(4096, 4096, 24, input.getImageData().palette);

        Parallel.For(0, data.height, y -> {
            for(int x = 0; x < data.width; x++) {
                data.setPixel(x,y, x + data.width * y);
            }
        });

        return new Image(input.getDevice(), data);
    }
}
