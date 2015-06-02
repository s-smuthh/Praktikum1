import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.imageio.*;
import static java.lang.Math.*;

/** Zeichenflaeche mit Turtlegrafik.
  * 
  * <p>Das folgende Beispielprogramm malt das "Haus vom Nikolaus":
<pre>class TurtleTest
{
    public static void main(String... args)
    {
        TurtlePainter tp = new TurtlePainter();
	double unit = 1;
	double sqrt2 = Math.sqrt(2);
	tp.color(Color.GREEN);
	tp.lineWidth(3);
	tp.move(unit);
	tp.turn(90);
	tp.move(unit);
	tp.turn(45);
	tp.move(unit/sqrt2);
	tp.turn(90);
	tp.move(unit/sqrt2);
	tp.turn(135);
	tp.move(unit);
	tp.turn(-135);
	tp.move(unit*sqrt2);
	tp.turn(-135);
	tp.move(unit);
	tp.turn(-135);
	tp.move(unit*sqrt2);
    }
}</pre>
  */ 
public class TurtlePainter extends Painter
{
    /** Erzeugt eine neue Zeichenflaeche mit 512x512 Pixeln Groesse.
      * Der Ursprung liegt links unten, der Punkt rechts oben hat die Koordinaten (10, 10).
      * Die logische Breite des Fensters ist 10.
      * Die Turtle sitzt in der Mitte (5, 5) und schaut nach rechts.
      */
    public TurtlePainter()
    {
        this(512, 512, 5.0);
    }

    /** Erzeugt eine neue Zeichenflaeche mit der gegebenen Pixelgroesse.
      * Der Ursprung liegt links unten.
      * Die Flache ist d breit und so hoch, wie es das Seitenverhaeltnis vorgibt.
      * Die Turtle sitzt in der Mitte und schaut nach rechts.
      * Die logische Breite des Fensters ist 10.
      * @param w Pixelbreite des Fensters.
      * @param h Pixelhoehe des Fensters.
      */
    public TurtlePainter(int w, int h)
    {
        this(w, h, 5);
    }
    
    /** Erzeugt eine neue Zeichenflaeche mit der gegebenen Pixelgroesse.
      * Der Ursprung liegt links unten.
      * Die Flache ist d breit und so hoch, wie es das Seitenverhaeltnis vorgibt.
      * Die Turtle sitzt in der Mitte und schaut nach rechts.
      * @param w Pixelbreite des Fensters.
      * @param h Pixelhoehe des Fensters.
      * @param d Logische Breite des Fensters.
      */
    public TurtlePainter(int w, int h, double d)
    {
        super(w, h, d);
	atx = 0;
	aty = 0;
	direction = 0;
	super.clear(Color.BLACK);
	super.color(Color.WHITE);
    }
    
    /** Bewegt die Turtle um die Entfernung dx vorwaerts und malt dabei einen
      * Strich.
      * @param dx Entfernung um die sich die Turtle bewegt.
      */
    public void move(double dx)
    {
    	double nextx = atx + dx*cos(direction);
	double nexty = aty + dx*sin(direction);
	line(atx, aty, nextx, nexty);
	atx = nextx;
	aty = nexty;
    }
    
    /** Bewegt die Turtle zum Punkte (x, y) und malt dabei einen
      * Strich.
      * @param x Horizontale Koordinate des Zielpunktes.
      * @param y Vertikale Koordinate des Zielpunktes.
      */
    public void moveTo(double x, double y)
    {
    	double nextx = x;
	double nexty = y;
	line(atx, aty, nextx, nexty);
	atx = nextx;
	aty = nexty;
    }
    
    /** Bewegt die Turtle um die Entfernung dx vorwaerts und malt dabei nicht.
      * @param dx Entfernung um die sich die Turtle bewegt.
      */
    public void fly(double dx)
    {
    	double nextx = atx + dx*cos(direction);
	double nexty = aty + dx*sin(direction);
	atx = nextx;
	aty = nexty;
    }
    
    /** Bewegt die Turtle zum Punkte (x, y) und malt dabei nicht.
      * @param x Horizontale Koordinate des Zielpunktes.
      * @param y Vertikale Koordinate des Zielpunktes.
      */
    public void flyTo(double x, double y)
    {
	atx = x;
	aty = y;
    }
    
    /** Dreht die Turtle um d Grad gegen den Uhrzeigersinn.
      * @param d Anzahl Grad um die sich die Turtle dreht.
      */
    public void turn(double d)
    {
    	direction += d*PI/180;
    }
    
    /** Dreht die Turtle auf die Orientierung d Grad.
      * Die Orientierung zaehlt ab 0 Grad = 3 Uhr gegen den Uhrzeigersinn.
      * @param d Anzahl Grad um die sich die Turtle dreht.
      */
    public void turnTo(double d)
    {
    	direction = d*PI/180;
    }
    
    /** Legt die Zeichenfarbe neu fest.
      * @param c Farbe.
      */
    public void color(Color c)
    {
    	super.color(c);
    }
    
    /** Legt die Zeichenfarbe neu fest.
      * @param r Rotanteil im Bereich (0, 255).
      * @param g Gruenanteil im Bereich (0, 255).
      * @param b Blauanteil im Bereich (0, 255).
      */
    public void color(int r, int g, int b)
    {
    	super.color(r, g, b);
    }

    /** Legt die Zeichenfarbe neu fest.
      * @param r Rotanteil im Bereich (0.0, 1.0).
      * @param g Gruenanteil im Bereich (0.0, 1.0).
      * @param b Blauanteil im Bereich (0.0, 1.0).
      */
    public void color(double r, double g, double b)
    {
    	super.color((int)(r*255), (int)(g*255), (int)(b*255));
    }

    /** Aendert die Breite von Linien.
      * @param d Neue Linienbreite.
      */
    public void lineWidth(double d)
    {
        super.lineWidth(d);
    }
    
    /** Gibt Auskunft über den Ort der Turtle.
      * @return Horizontale Position.
      */
    public double getXPos()
    {
        return atx;
    }

    /** Gibt Auskunft über den Ort der Turtle.
      * @return Vertikale Position.
      */
    public double getYPos()
    {
        return aty;
    }

    public static void main(String... args)
    {
        TurtlePainter tp = new TurtlePainter();
	double unit = 1;
	double sqrt2 = Math.sqrt(2);
	tp.color(Color.GREEN);
	tp.lineWidth(.1);
	tp.move(unit);
	tp.turn(90);
	tp.move(unit);
	tp.turn(45);
	tp.move(unit/sqrt2);
	tp.turn(90);
	tp.move(unit/sqrt2);
	tp.turn(135);
	tp.move(unit);
	tp.turn(-135);
	tp.move(unit*sqrt2);
	tp.turn(-135);
	tp.move(unit);
	tp.turn(-135);
	tp.move(unit*sqrt2);
    }
    
    /** Aktuelle Position der Turtle. */
    private double atx;
    
    /** Aktuelle Position der Turtle. */
    private double aty;
    
    /** Aktuelle Orientierung der Turtle. */
    private double direction;
}
