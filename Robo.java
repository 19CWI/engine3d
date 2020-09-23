package mechanics;

import java.awt.AWTException;
import java.awt.Robot;

import main.Main;

public class Robo{

	Main main;
	
    public Robo(Main main) {
    	this.main=main;
    }

    public void centerMouse(){
        try{
            Robot r = new Robot();
            
            r.mouseMove((int) (main.getWidth()*0.5), (int) (main.getHeight()*0.5));
            
        }
        catch (AWTException e){}
    }
    
    /*public void useRobot(){
        try{
            Robot r = new Robot();
            
            //whatever needs doing
            
        }
        catch (AWTException e){}
    }*/

}