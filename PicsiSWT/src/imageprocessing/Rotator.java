package imageprocessing;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import utils.Parallel;

import javax.swing.*;

/**
 * Created by benjamin on 13.05.2015.
 */
public class Rotator implements IImageProcessor {

    @Override
    public boolean isEnabled(int imageType) {
        return true;
    }

    @Override
    public Image run(Image input, int imageType) {
        double alpha = Math.toRadians(Double.parseDouble(JOptionPane.showInputDialog("Um welchen Winkel rotieren?")));
        double sina = Math.sin(alpha);
        double cosa = Math.cos(alpha);

        ImageData data = (ImageData) input.getImageData().clone();
        int mx = data.width / 2;
        int my = data.height / 2;
            Parallel.For(0, data.height, v -> {
                for (int u = 0; u < data.width; u++) {
                    int uu = (int) Math.floor((u - mx) * cosa + (v - my) * sina + 0.5) + mx;
                    int vv = (int) Math.floor((u - mx) * sina * -1 + (v - my) * cosa + 0.5) + my;
                    if (uu < 0 || uu >= data.width
                            || vv < 0 || vv >= data.height) {
                        data.setPixel(u, v, 0);
                    } else {
                        data.setPixel(u, v, input.getImageData().getPixel(uu, vv));
                    }
                }
            });
        return new Image(input.getDevice(), data);
    }
}
