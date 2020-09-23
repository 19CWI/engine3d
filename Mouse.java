package mechanics;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

import main.Main;
import structures.Vec3D;

public class Mouse implements MouseInputListener {

	Robo robo;
	Main main;
	Vec3D center = new Vec3D(0,0,0);

	public Mouse(Robo robo, Main main) {
		this.robo = robo;
		this.main = main;
		
		center.x = main.getWidth()*0.5f;
		center.y = main.getHeight()*0.5f;
		
		robo.centerMouse();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		robo.centerMouse();

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// robo.centerMouse();

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Vec3D vlook = new Vec3D(e.getX(),e.getY(),0);
		vlook = Vec3D.vectorSub(center, vlook);
		vlook = Vec3D.vectorMul(vlook, 0.001f);
		main.setPitch(vlook.y);
		main.setYaw(-vlook.x);
		robo.centerMouse();
		
	}

}