package imageprocessing;

import gui.LineTracker;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import utils.Parallel;

/**
 * Created by benjamin on 10.06.2015.
 */
public class TemplateDetection implements IImageProcessor {

    int points = 2;
    private int[][] coords = new int[4][2];
    double r, r2, R, R2, I, i, k, sig, cl, IR, i2;
    double clMax = 0;
    int tempWidth;
    int tempHeight;
    int uMax, vMax;

    @Override
    public boolean isEnabled(int imageType) {
        return true;
    }
    @Override
    public Image run(Image input, int imageType) {
        ImageData inData = input.getImageData();
        selectTemplate();
        preCalc(inData);

        ImageData outData = new ImageData(tempWidth , tempHeight, 24, inData.palette);

        for(int v = 0; v < inData.height - (coords[2][1] - coords[0][1]); v++){
            for(int u = 0; u < inData.width - (coords[1][0] - coords[0][0]); u++){
                cl = calc(inData, u, v);
                        if(cl > clMax && cl < 0.635) {
//                if(cl > clMax) {
                    clMax = cl;
                    uMax = u;
                    vMax = v;
                }
            }
        }

        System.out.println(clMax+" "+uMax+" "+vMax);

        for(int v = 0; v < tempHeight; v++){
            for(int u = 0; u < tempWidth; u++){
                int pixel = inData.getPixel(u + uMax, v + vMax);
                outData.setPixel(u, v, pixel);
            }
        }



        return new Image(input.getDevice(), outData);
    }

    private void selectTemplate() {
        LineTracker tracker = new LineTracker();
        int [] c = tracker.start(4, true);
        int j = 0;
        for (int i = 0; i < coords.length; i++) {
            coords[i][0] = c[j++];
            coords[i][1] = c[j++];
        }
    }

    private void preCalc(ImageData inData){
        int x0 = coords[0][0];
        int x1 = coords[1][0];
        int y0 = coords[0][1];
        int y1 = coords[2][1];

        tempWidth = coords[1][0] + 1 - coords[0][0];
        tempHeight = coords[2][1] + 1 - coords[0][1];
        k = tempWidth * tempHeight;

        for(int j = y0; j <= y1; j++){
            for(int i = x0; i <= x1; i++){
                int pixel = inData.getPixel(i, j);
                RGB rgb = inData.palette.getRGB(pixel);
                double temp = rgb.red * 0.299;
                temp += rgb.green * 0.587;
                temp += rgb.blue * 0.114;
                r += temp;
                r2 += temp*temp;
            }
        }
        R = r/k;
        R2 = r2;
        sig = Math.sqrt(R2 - k * R * R);
        r = 0;
    }

    private double calc(ImageData inData, int u, int v){

        IR = 0;
        I = 0;
        i2 = 0;
        for(int j = 0; j < tempHeight; j++){
            for(int w = 0; w < tempWidth; w++){
                int pixelI = inData.getPixel(u+w, v+j);
                int pixelR = inData.getPixel(coords[0][0]+w, coords[0][1]+j);
                RGB rgbI = inData.palette.getRGB(pixelI);
                RGB rgbR = inData.palette.getRGB(pixelR);
                i = rgbI.red * 0.299;
                i += rgbI.green * 0.587;
                i += rgbI.blue * 0.114;
                i2 += (i*i);
                I += i;
                r = rgbR.red * 0.299;
                r += rgbR.green * 0.587;
                r += rgbR.blue * 0.114;
                IR += (i*r);
            }
        }
        I = I / k;
        return (IR - k * I * R)/(1 + Math.sqrt(i2 - k * I * I) * sig);
    }


}
