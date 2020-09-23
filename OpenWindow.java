package main;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class OpenWindow {
	
	public OpenWindow(String title, Main main) {

		JFrame frame =new JFrame(title);
		frame.add(main);
		main.setSize(new Dimension(JFrame.MAXIMIZED_HORIZ,JFrame.MAXIMIZED_VERT));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setSize(new Dimension(width,height));
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(true);
		frame.getContentPane().setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(8,8,BufferedImage.TYPE_INT_ARGB), new Point(4,4), "blank"));
		main.start();
		}
}
