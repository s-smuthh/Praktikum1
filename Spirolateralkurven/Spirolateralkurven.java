
import java.util.*;

public class Spirolateralkurven {
	public static void main(String args[]) {
	final int pixelWidth 		= Integer.parseInt(args[0]);	
	final int pixelHeight 		= Integer.parseInt(args[1]);
	final int edgeLength 		= Integer.parseInt(args[2]);
	final int angle 		= Integer.parseInt(args[3]);
	int repetitions			= Integer.parseInt(args[4]);	
	int checkAngle 			= 0;
	TurtlePainter turtle 		= new TurtlePainter(pixelWidth, pixelHeight, edgeLength);

	do{	
		for (int i = 1; i<=repetitions; i++){
			turtle.colors(repetitions);
			turtle.color(i-1);	
			turtle.move(i);
			turtle.turn(angle);
			checkAngle = checkAngle + angle;
		}
	
	} while(checkAngle%360 != 0);
	turtle.repaint();
	
	}
}
