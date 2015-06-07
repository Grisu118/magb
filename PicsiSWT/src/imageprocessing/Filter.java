package imageprocessing;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import javax.swing.*;

/**
 * Created by benjamin on 07.06.2015.
 */
public class Filter implements IImageProcessor {
    @Override
    public boolean isEnabled(int imageType) {
        return true;
    }

    @Override
    public Image run(Image input, int imageType) {
        int n = optionDialog();
        int iterations = Integer.parseInt(JOptionPane.showInputDialog("Iterations:"));
        ImageData inData = (ImageData) input.getImageData();
        for(int i = 0; i < iterations; i++){
            if(n == 0) {
                int[][] filter = {{1,1,1}, {1,1,1}, {1,1,1}};
                inData = ImageProcessing.convolve(inData, imageType, filter, 9, 0);
            } else if(n == 1) {
                int[][] filter = {{0,1,2,1,0},{1,3,5,3,1},{2,5,9,5,2},{1,3,5,3,1},{0,1,2,1,0}};
                inData = ImageProcessing.convolve(inData, imageType, filter, 57, 0);
            } else if(n == 2) {
                int[][] filter = {{0,0,-1,0,0},{0,-1,-2,-1,0},{-1,-2,16,-2,-1},{0,-1,-2,-1,0},{0,0,-1,0,0}};
                inData = ImageProcessing.convolve(inData, imageType, filter, 0, 0);
            }
        }
        return new Image(input.getDevice(), inData);

    }

    public int optionDialog(){
        Object[] options = {"Box","Gauss","Laplace"};
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
