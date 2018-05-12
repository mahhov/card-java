import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Manager;
import javax.media.Player;
import javax.media.control.FormatControl;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.format.YUVFormat;
import javax.media.util.BufferToImage;

//
// source code by tejopa / popscan.blogspot.com, 2012
//

public class FrameGrabber {

	CaptureDeviceInfo _cdi;
	Player _player;
	FrameGrabbingControl _frameGrabber;
	FormatControl _formatControl;

	public FrameGrabber() {
	}

	public boolean init() {
		Vector devices = CaptureDeviceManager.getDeviceList(new YUVFormat());
		if (devices.size() < 1) {
			System.out.println("No capture devices available!");
		} else {
			_cdi = (CaptureDeviceInfo) devices.elementAt(0);
		}
		try {
			_player = Manager.createRealizedPlayer(_cdi.getLocator());
			_frameGrabber = (FrameGrabbingControl) _player
					.getControl("javax.media.control.FrameGrabbingControl");
			_formatControl = (FormatControl) _player
					.getControl("javax.media.control.FormatControl");
			_formatControl.setFormat(_cdi.getFormats()[0]);
			_player.start();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public BufferedImage grab() {
		if (_frameGrabber == null) {
			System.out.println("FrameGrabber is not initialized!");
			return null;
		}
		// Grab a frame from the capture device
		Buffer buf = _frameGrabber.grabFrame();
		// check that buf actually exists!
		if (buf != null) {
			Image img = new BufferToImage((VideoFormat) buf.getFormat())
					.createImage(buf);
			// image creation may also fail!
			if (img != null) {
				BufferedImage bi = new BufferedImage(img.getWidth(null),
						img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = bi.createGraphics();
				g.drawImage(img, 0, 0, null);
				return bi;
			}
		}
		// grab failed, still return image, but make it empty and small
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		return bi;
	}
}