package imageprocessing;

import gui.RectTracker;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;

public class Crop implements IImageProcessor {
    @Override
    public boolean isEnabled(int imageType) {
        return true;
    }

    @Override
    public Image run(Image input, int imageType) {
        RectTracker tracker = new RectTracker();

        final ImageData inData = input.getImageData();
        Rectangle r = tracker.track(0, 0, 5, 5);
        final ImageData outData = new ImageData(r.width, r.height, inData.depth, inData.palette);
        int startV = r.y;
        int startU = r.x;
        for (int v = 0; v < outData.height; v++) {
            for (int u = 0; u < outData.width; u++) {
                outData.setPixel(u, v, inData.getPixel(startU, startV));
                outData.setAlpha(u, v, inData.getAlpha(startU++, startV));
            }
            startV++;
            startU = r.x;
        }
        return new Image(input.getDevice(), outData);
    }
}
