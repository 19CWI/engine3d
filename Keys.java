package mechanics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.Main;

public class Keys implements KeyListener {
	
	Main main;
	
	public Keys(Main main) {
		super();
		this.main=main;
		
	}


	@Override
	public void keyPressed(KeyEvent r) {
		
		if(r.getKeyCode()==KeyEvent.VK_W) {
			main.setVelz(0.3f);
		}	
		if(r.getKeyCode()==KeyEvent.VK_S) {
			main.setVelz(-0.3f);
		}
		if(r.getKeyCode()==KeyEvent.VK_D) {
			main.setVelx(-0.3f);
		}
		if(r.getKeyCode()==KeyEvent.VK_A) {
			main.setVelx(0.3f);
		}
		if(r.getKeyCode()==KeyEvent.VK_SHIFT) {
			main.setVely(-0.3f);
		}
		if(r.getKeyCode()==KeyEvent.VK_CONTROL) {
			main.setVely(0.3f);
		}
		if(r.getKeyCode()==KeyEvent.VK_SPACE) {
			if (main.isChangeAngle()) {
				main.setChangeAngle(false);
			}
			else {
				main.setChangeAngle(true);
			}
		}
/////////////////////////////////////////////////////////////////////////////
		if(r.getKeyCode()==KeyEvent.VK_UP) {
			main.setXRot(-0.05f);
		}
		if(r.getKeyCode()==KeyEvent.VK_DOWN) {
			main.setXRot(0.05f);
		}
		if(r.getKeyCode()==KeyEvent.VK_LEFT) {
			main.setYRot(0.05f);
		}
		if(r.getKeyCode()==KeyEvent.VK_RIGHT) {
			main.setYRot(-0.05f);
		}
		if(r.getKeyCode()==KeyEvent.VK_PAGE_UP) {
			main.setZRot(-0.05f);
		}
		if(r.getKeyCode()==KeyEvent.VK_PAGE_DOWN) {
			main.setZRot(0.05f);
		}
//////////////////////////////////////////////////////////////////////////////		
		if(r.getKeyCode()==KeyEvent.VK_ESCAPE) {
			System.exit(0);	
		}
	}
	

	@Override
	public void keyReleased(KeyEvent r) {
		if(r.getKeyCode()==KeyEvent.VK_W) {
			main.setVelz(0);
		}	
		if(r.getKeyCode()==KeyEvent.VK_S) {
			main.setVelz(0);
		}
		if(r.getKeyCode()==KeyEvent.VK_A) {
			main.setVelx(0);
		}
		if(r.getKeyCode()==KeyEvent.VK_D) {
			main.setVelx(0);
		}
		if(r.getKeyCode()==KeyEvent.VK_SHIFT) {
			main.setVely(0);
		}
		if(r.getKeyCode()==KeyEvent.VK_CONTROL) {
			main.setVely(0);
		}
		
		if(r.getKeyCode()==KeyEvent.VK_UP) {
			main.setPitch(0);
		}
		if(r.getKeyCode()==KeyEvent.VK_DOWN) {
			main.setPitch(0);
		}
		if(r.getKeyCode()==KeyEvent.VK_LEFT) {
			main.setYaw(0);
		}
		if(r.getKeyCode()==KeyEvent.VK_RIGHT) {
			main.setYaw(0);
		}
	}

	@Override
	public void keyTyped(KeyEvent r) {
		
		
	}

	
}