// http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.rap.help%2Fhelp%2Fhtml%2Freference%2Fapi%2Forg%2Feclipse%2Fswt%2Fgraphics%2Fpackage-summary.html

package imageprocessing;

import gui.TwinView;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import utils.Parallel;

/**
 * Image processing class: contains widely used image processing functions
 * 
 * @author Christoph Stamm
 *
 */
public class ImageProcessing {
	private static class ImageMenuItem {
		private String m_text;
		private int m_accelerator;
		private IImageProcessor m_process;
		
		public ImageMenuItem(String text, int accelerator, IImageProcessor proc) {
			m_text = text;
			m_accelerator = accelerator;
			m_process = proc;
		}
	}
	
	private TwinView m_views;
	private ArrayList<ImageMenuItem> m_menuItems = new ArrayList<ImageMenuItem>();
	
	/**
	 * Registration of image operations
	 * @param views
	 */
	public ImageProcessing(TwinView views) {
		assert views != null : "views are null";
		m_views = views;
		
		m_menuItems.add(new ImageMenuItem("&Invert\tF1", SWT.F1, new Inverter()));
		// TODO add here further image processing objects (they are inserted into the Image menu)
		//Shortcut Shift+F1 = SWT.SHIFT | SWT.F1
		m_menuItems.add(new ImageMenuItem("&Convert\tF2", SWT.F2, new Converter()));
		m_menuItems.add(new ImageMenuItem("&Convert to Binary\tF3", SWT.F3, new Dithering()));
		m_menuItems.add(new ImageMenuItem("&Rotate\tF4", SWT.F4, new Rotator()));
		m_menuItems.add(new ImageMenuItem("&Scale + Rotate\tF5", SWT.F5, new ScaleAndRot()));
		m_menuItems.add(new ImageMenuItem("&AllRGB\tF6", SWT.F6, new AllRGB()));
        m_menuItems.add(new ImageMenuItem("&Filter\tF7", SWT.F7, new Filter()));
	}
	
	public void createMenuItems(Menu menu) {
		for(final ImageMenuItem item : m_menuItems) {
			MenuItem mi = new MenuItem(menu, SWT.PUSH);
			mi.setText(item.m_text);
			mi.setAccelerator(item.m_accelerator);
			mi.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					Image output = null;
					try {
						output = item.m_process.run(m_views.getFirstImage(), m_views.getFirstimageType());
					} catch(Throwable e) {
						int last = item.m_text.indexOf('\t');
						if (last == -1) last = item.m_text.length();
						String location = item.m_text.substring(0, last).replace("&", "");
						m_views.m_mainWnd.showErrorDialog("ImageProcessing", location, e);
					}						
					if (output != null) {
						m_views.showImageInSecondView(output);
					}
				}
			});
		}
	}
	
	public boolean isEnabled(int i) {
		return m_menuItems.get(i).m_process.isEnabled(m_views.getFirstimageType());
	}

	public static ImageData convolve(ImageData inData, int imageType,
									 int[][] filter, int norm, int offset) {
        try {
            ImageData outData = (ImageData) inData.clone();

            int left = -(filter[0].length / 2);
            int right = -left;
            int top = -(filter.length / 2);
            int bottom = -top;

            //Parallel.For(0, outData.height, v -> {
            for (int v = 0; v < outData.height; v++) {
                for (int u = 0; u < outData.width; u++) {
                    int uu = 0;
                    int vv = 0;
                    RGB rgb = new RGB(0, 0, 0);


                    for (int y = top; y <= bottom; y++) {
                        for (int x = left; x <= right; x++) {
                            vv = v + y;
                            uu = u + x;
                            if (vv < 0 || vv >= outData.height) {
                                vv = v - y;
                            }
                            if (uu < 0 || uu >= outData.width) {
                                uu = u - x;
                            }
                            int pixel = inData.getPixel(uu, vv);
                            RGB tmp = inData.palette.getRGB(pixel);
                            rgb.red += tmp.red * filter[y + bottom][x + right];
                            rgb.green += tmp.green * filter[y + bottom][x + right];
                            rgb.blue += tmp.blue * filter[y + bottom][x + right];
                        }
                    }
                    if (norm != 0) {
                        rgb.red /= norm;
                        rgb.blue /= norm;
                        rgb.green /= norm;
                    }
                    if (offset != 0) {
                        rgb.red += offset;
                        rgb.blue += offset;
                        rgb.green += offset;
                    }

                    outData.setPixel(u, v, outData.palette.getPixel(clamp(rgb)));
                }
            }//);

            return outData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}

    public static int clamp(int intensity){
        if(intensity > 255) intensity = 255;
        else if(intensity < 0) intensity = 0;
        return intensity;
    }

    public static RGB clamp(RGB rgb){
        rgb.red = clamp(rgb.red);
        rgb.green = clamp(rgb.green);
        rgb.blue = clamp(rgb.blue);
        return rgb;
    }


    // add general image processing class methods here
	
}
