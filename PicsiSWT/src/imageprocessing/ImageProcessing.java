// http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.rap.help%2Fhelp%2Fhtml%2Freference%2Fapi%2Forg%2Feclipse%2Fswt%2Fgraphics%2Fpackage-summary.html

package imageprocessing;

import gui.TwinView;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

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
		m_menuItems.add(new ImageMenuItem("&Convert to Binary\tF3", SWT.F3, new BinaryConverter()));
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

	// add general image processing class methods here
	
}