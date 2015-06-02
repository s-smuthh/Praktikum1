import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import static java.awt.Font.*;
import javax.imageio.*;

/** Einfache Zeichenflaeche mit logischen Koordinaten, die in einem Fenster
  * abgebildet wird.
  *
  * <p>Das folgende Beispielprogramm malt ein Grinsegesicht:
<pre>class PainterTest
{
    public static void main(String... args)
    {
	Painter p = new Painter();
        p.scale(10);
        p.clear(Color.BLUE);
        p.color(255, 224, 64);
        p.filledCircle(0, 0, 4);
        p.color(Color.WHITE);
        p.filledCircle(-1, 2, 1);
        p.filledCircle(1, 2, 1);
        p.color(Color.BLACK);
        p.filledCircle(-0.5, 2, 0.4);
        p.filledCircle(0.5, 2, 0.4);
        p.color(255, 192, 0);
        p.filledCircle(0, 0, 0.5);
        p.color(Color.BLACK);
        p.lineWidth(.1);
        p.arc(0, 0, 3.5, 180, 180);
    }
}</pre>
  * Der Koordinatenursprung liegt in der Mitte der Zeichenfläche.
  */
public class Painter extends Frame
{
    /** Erzeugt eine neue Zeichenflaeche mit 512x512 Pixeln Groesse.
      */
    public Painter()
    {
	this(512, 512, 10.0, true);
    }

    /** Erzeugt eine neue quadratische Zeichenflaeche mit der gegebenen Pixel-Kantenlaenge.
      * Die Flache ist 10 breit und hoch.
      * @param w Kantenlaenge des Fensters.
      */
    public Painter(int w)
    {
	this(w, w, 10, true);
    }

    /** Erzeugt eine neue Zeichenflaeche mit der gegebenen Pixelgroesse.
      * Die Flache ist 10 breit und so hoch, wie es das Seitenverhaeltnis vorgibt.
      * @param w Pixelbreite des Fensters.
      * @param h Pixelhoehe des Fensters.
      */
    public Painter(int w, int h)
    {
	this(w, h, 10, true);
    }

    /** Erzeugt eine neue Zeichenflaeche mit der gegebenen Pixelgroesse.
      * Die Flache ist d breit und so hoch, wie es das Seitenverhaeltnis vorgibt.
      * @param w Pixelbreite des Fensters.
      * @param h Pixelhoehe des Fensters.
      * @param d Logische Breite des Fensters.
      */
    public Painter(int w, int h, double d)
    {
	this(w, h, d, true);
    }

    /** Erzeugt eine neue Zeichenflaeche mit der gegebenen Pixelgroesse.
      * Der Ursprung liegt links unten.
      * Die Flache ist d breit und so hoch, wie es das Seitenverhaeltnis vorgibt.
      * Sie wird nicht dargestellt.
      * @param w Pixelbreite des Fensters.
      * @param h Pixelhoehe des Fensters.
      * @param d Logische Breite des Fensters.
      * @param b true = Fenster sichtbar; false = Fenster bleibt unsichtbar.
      * Wenn das Fenster unsichtbar bleibt, kann das Bild trotzdem in eine Datei
      * gespeichert werden.
      * Damit sind auch grosse Bildformate moeglich.
      */
    public Painter(int w, int h, double d, boolean b)
    {
	width = w;
	height = h;
	scale = width/d;
	bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    	init(b);
    }

    private Painter(BufferedImage bi)
    {
	bufferedImage = bi;
	width = bufferedImage.getWidth();
	height = bufferedImage.getHeight();
	scale = width/1.0;
    	init(true);
    }

    private void init(boolean v)
    {
	Properties sysprops = System.getProperties();
	hidden = sysprops.getProperty("painter.hidden");
	
	/* Close-Button: Fenster schliessen */
	addWindowListener(new WindowAdapter()
			  {
			      public void windowClosing(WindowEvent e)
			      {
				  // System.out.println(e.getX() + " " + e.getY());
				  System.exit(0);
			      }
			  }
			 );

	gc = bufferedImage.createGraphics();
	gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	gc.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	// gc.transform(new AffineTransform(w/dx, 0, 0, h/dy, 0, 0));
	
    	font("Dialog", BOLD, 1);
	fontRenderContext = gc.getFontRenderContext();
	
	textposX = width/scale/2;
	textposY = height/scale/2;
	
	if(v  &&  hidden == null)
	    setVisible(true);

	/* Dekoration ausmessen */
    	insets = getInsets();
	
    	/* Groesse entsprechend Dekoration einstellen */
    	setSize(width + insets.left + insets.right, height + insets.bottom + insets.top);

	/* neu zeichnen */
	repaint();
    }

    /** Sollte eigentlich nicht redefiniert werden.
      * Der Aufruf muss aber abgefangen werden, um das Bild zu sichern,
      * falls das Programm auf einem Server laeuft.
      */
    public void repaint()
    {
	super.repaint();
    	try
	{
	    if(hidden != null)
		save(hidden);
	}
	catch(Exception ex)
	{}
    }
    
    /** Callbackmethode zum Neuzeichnen des Fensters.
      * @param g Graphics context.
      */
    public void paint(Graphics g)
    {
	bufferedImage.flush();
	g.drawImage(bufferedImage, insets.left, insets.top, this);
    }

    /** Callbackmethode zum Neuzeichnen des Fensters.
      * @param g Graphics context.
      */
    public void update(Graphics g)
    {
	bufferedImage.flush();
	g.drawImage(bufferedImage, insets.left, insets.top, this);
    }

    /** Legt die Zeichenfarbe neu fest.
      * Alle weiteren Zeichenoperationen benutzen diese Farbe,
      * wenn sie selbst keine Farbe festlegen.
      * @param c Farbe.
      */
    public void color(Color c)
    {
	color = c;
	gc.setColor(c);
    }

    /** Legt die Zeichenfarbe neu fest.
      * Alle weiteren Zeichenoperationen benutzen diese Farbe,
      * wenn sie selbst keine Farbe festlegen.
      * Werte ausserhalb des zulaessigen Bereichs [0, 255] werden durch die
      * naechste Grenze ersetzt.
      * @param r Rotanteil.
      * @param g Gruenanteil.
      * @param b Blauanteil.
      */
    public void color(int r, int g, int b)
    {
	color = new Color(clamp(r), clamp(g), clamp(b));
	gc.setColor(color);
    }
    
    private static int clamp(int n)
    {
    	return n < 0?  0:  (n > 255?  255:  n);
    }
    
    /** Legt die Anzahl Farben aus dem Regenbogen fest.
      * @param n Anzahl Regenbogenfarben.
      */
    public void colors(int n)
    {
    	shades = new Shades(n);
    }
    
    /** Waehlt eine Regenbogenfarbe aus.
      * @param n Regenbogenfarbe ab 0 = erste Farbe.
      * @see #colors
      */
    public void color(int n)
    {
    	int[] rgb = shades.shade(n);
    	gc.setColor(new Color(rgb[0], rgb[1], rgb[2]));
    }

    /** Uebermalt die ganze Flaeche mit der Farbe c.
      * @param c Farbe.
      */
    public void clear(Color c)
    {
	gc.setBackground(c);
	gc.clearRect(0, 0, width, height);
    }

    /** Uebermalt die ganze Flaeche mit der voreingestellten Farbe.
      */
    public void clear()
    {
	gc.setBackground(color);
	gc.clearRect(0, 0, width, height);
    }

    /** Zeichnet eine Linie in der aktuellen Zeichenfarbe vom ersten zum
      * zweiten Punkt.
      * @param fromx Horizontale Koordinate des ersten Punktes.
      * @param fromy Vertikale Koordinate des ersten Punktes.
      * @param tox Horizontale Koordinate des zweiten Punktes.
      * @param toy Vertikale Koordinate des zweiten Punktes.
      */
    public void line(double fromx, double fromy, double tox, double toy)
    {
	gc.drawLine(scalex(fromx), scaley(fromy), scalex(tox), scaley(toy));
    }

    /** Zeichnet eine Linie in der Farbe c vom ersten zum
      * zweiten Punkt.
      * @param c Farbe fuer diese eine Operation.
      * @param fromx Horizontale Koordinate des ersten Punktes.
      * @param fromy Vertikale Koordinate des ersten Punktes.
      * @param tox Horizontale Koordinate des zweiten Punktes.
      * @param toy Vertikale Koordinate des zweiten Punktes.
      */
    public void line(Color c, double fromx, double fromy, double tox, double toy)
    {
	gc.setColor(c);
	gc.drawLine(scalex(fromx), scaley(fromy), scalex(tox), scaley(toy));
	gc.setColor(color);
    }

    /** Zeichnet einen Bogen in der aktuellen Zeichenfarbe auf dem Umfang des
      * gegebenen Kreises.
      * @param x Horizontale Koordinate des Kreismittelpunktes.
      * @param y Vertikale Koordinate des Kreismittelpunktes.
      * @param r Radius des Kreises.
      * @param start Winkel in Grad, an dem der Bogen beginnt.
      * Der Winkel zaehlt ab 0 = 3 Uhr gegen den Uhrzeigersinn.
      * @param arc Anzahl Grad die der Bogen lang ist.
      */
    public void arc(double x, double y, double r, int start, int arc)
    {
	arc(x, y, r, r, start, arc);
    }

    /** Zeichnet einen gefuellten Bogen in der aktuellen Zeichenfarbe auf dem Umfang des
      * gegebenen Kreises.
      * @param x Horizontale Koordinate des Kreismittelpunktes.
      * @param y Vertikale Koordinate des Kreismittelpunktes.
      * @param r Radius des Kreises.
      * @param start Winkel in Grad, an dem der Bogen beginnt.
      * Der Winkel zaehlt ab 0 = 3 Uhr gegen den Uhrzeigersinn.
      * @param arc Anzahl Grad die der Bogen lang ist.
      */
    public void filledArc(double x, double y, double r, int start, int arc)
    {
	filledArc(x, y, r, r, start, arc);
    }

    /** Zeichnet einen Bogen in der Farbe c auf dem Umfang des
      * gegebenen Kreises.
      * @param c Farbe fuer diese eine Operation.
      * @param x Horizontale Koordinate des Kreismittelpunktes.
      * @param y Vertikale Koordinate des Kreismittelpunktes.
      * @param r Radius des Kreises.
      * @param start Winkel in Grad, an dem der Bogen beginnt.
      * Der Winkel zaehlt ab 0 = 3 Uhr gegen den Uhrzeigersinn.
      * @param arc Anzahl Grad die der Bogen lang ist.
      */
    public void arc(Color c, double x, double y, double r, int start, int arc)
    {
	gc.setColor(c);
	arc(x, y, r, r, start, arc);
	gc.setColor(color);
    }

    /** Zeichnet einen Bogen in der aktuellen Zeichenfarbe auf dem Umfang der
      * gegebenen Ellipse.
      * @param x Horizontale Koordinate des Mittelpunktes.
      * @param y Vertikale Koordinate des Mittelpunktes.
      * @param w Radius quer.
      * @param h Radius senkrecht.
      * @param start Winkel in Grad, an dem der Bogen beginnt.
      * Der Winkel zaehlt ab 0 = 3 Uhr gegen den Uhrzeigersinn.
      * @param arc Anzahl Grad die der Bogen lang ist.
      */
    public void arc(double x, double y, double w, double h, int start, int arc)
    {
	w *= 2;
	h *= 2;
	x -= w/2;
	y += h/2;
	gc.drawArc(scalex(x), scaley(y), tox(w), toy(h), start, arc);
    }

    /** Zeichnet einen gefuellten Bogen in der aktuellen Zeichenfarbe auf dem Umfang der
      * gegebenen Ellipse.
      * @param x Horizontale Koordinate des Mittelpunktes.
      * @param y Vertikale Koordinate des Mittelpunktes.
      * @param w Radius quer.
      * @param h Radius senkrecht.
      * @param start Winkel in Grad, an dem der Bogen beginnt.
      * Der Winkel zaehlt ab 0 = 3 Uhr gegen den Uhrzeigersinn.
      * @param arc Anzahl Grad die der Bogen lang ist.
      */
    public void filledArc(double x, double y, double w, double h, int start, int arc)
    {
	w *= 2;
	h *= 2;
	x -= w/2;
	y += h/2;
	gc.fillArc(scalex(x), scaley(y), tox(w), toy(h), start, arc);
    }

    /** Zeichnet eine Bogen in der Farbe c auf dem Umfang der
      * gegebenen Ellipse.
      * @param c Farbe fuer diese eine Operation.
      * @param x Horizontale Koordinate des Mittelpunktes.
      * @param y Vertikale Koordinate des Mittelpunktes.
      * @param w Radius quer.
      * @param h Radius senkrecht.
      * @param start Winkel in Grad, an dem der Bogen beginnt.
      * Der Winkel zaehlt ab 0 = 3 Uhr gegen den Uhrzeigersinn.
      * @param arc Anzahl Grad die der Bogen lang ist.
      */
    public void arc(Color c, double x, double y, double w, double h, int start, int arc)
    {
	w *= 2;
	h *= 2;
	x -= w/2;
	y += h/2;
	gc.setColor(c);
	gc.drawArc(scalex(x), scaley(y), tox(w), toy(h), start, arc);
	gc.setColor(color);
    }

    /** Zeichnet ein Rechteck in der aktuellen Zeichenfarbe.
      * @param x Horizontale Koordinate der linken, unteren Ecke.
      * @param y Vertikale Koordinate der linken, unteren Ecke.
      * @param w Breite.
      * @param h Hoehe.
      */
    public void box(double x, double y, double w, double h)
    {
	gc.drawRect(scalex(x), scaley(y) - toy(h), tox(w), toy(h));
    }

    /** Zeichnet ein ausgefuelltes Rechteck in der aktuellen Zeichenfarbe.
      * @param x Horizontale Koordinate der linken, unteren Ecke.
      * @param y Vertikale Koordinate der linken, unteren Ecke.
      * @param w Breite.
      * @param h Hoehe.
      */
    public void filledBox(double x, double y, double w, double h)
    {
	gc.fillRect(scalex(x), scaley(y) - toy(h), tox(w), toy(h));
    }

    /** Zeichnet ein Rechteck in der Farbe c.
      * @param c Farbe fuer diese eine Operation.
      * @param x Horizontale Koordinate der linken, unteren Ecke.
      * @param y Vertikale Koordinate der linken, unteren Ecke.
      * @param w Breite.
      * @param h Hoehe.
      */
    public void box(Color c, double x, double y, double w, double h)
    {
	gc.setColor(c);
	gc.drawRect(scalex(x), scaley(y) - toy(h), tox(w), toy(h));
	gc.setColor(color);
    }

    /** Zeichnet ein ausgefuelltes Rechteck in der Farbe c.
      * @param c Farbe fuer diese eine Operation.
      * @param x Horizontale Koordinate der linken, unteren Ecke.
      * @param y Vertikale Koordinate der linken, unteren Ecke.
      * @param w Breite.
      * @param h Hoehe.
      */
    public void filledBox(Color c, double x, double y, double w, double h)
    {
	gc.setColor(c);
	gc.fillRect(scalex(x), scaley(y) - toy(h), tox(w), toy(h));
	gc.setColor(color);
    }

    /** Zeichnet einen Polygonzug in der aktuellen Zeichenfarbe.
      * @param p Paarweise horizontale und vertikale Koordinaten aller Punkte.
      */
    public void poly(double... p)
    {
	int npoints = p.length/2;
	int[] xpoints = new int[npoints];
	int[] ypoints = new int[npoints];
	for(int i = 0;	i < npoints;  i++)
	{
	    xpoints[i] = scalex(p[2*i]);
	    ypoints[i] = scaley(p[2*i + 1]);
	}
	gc.drawPolyline(xpoints, ypoints, npoints);
    }

    /** Zeichnet ein gefuelltes Polygon in der aktuellen Zeichenfarbe.
      * @param p Paarweise horizontale und vertikale Koordinaten aller Punkte.
      */
    public void filledPoly(double... p)
    {
	int npoints = p.length/2;
	int[] xpoints = new int[npoints];
	int[] ypoints = new int[npoints];
	for(int i = 0;	i < npoints;  i++)
	{
	    xpoints[i] = scalex(p[2*i]);
	    ypoints[i] = scaley(p[2*i + 1]);
	}
	gc.fillPolygon(xpoints, ypoints, npoints);
    }

    /** Zeichnet einen Polygonzug in der Farbe c.
      * @param c Farbe fuer diese eine Operation.
      * @param p Paarweise horizontale und vertikale Koordinaten aller Punkte.
      */
    public void poly(Color c, double... p)
    {
	int npoints = p.length/2;
	int[] xpoints = new int[npoints];
	int[] ypoints = new int[npoints];
	for(int i = 0;	i < npoints;  i++)
	{
	    xpoints[i] = scalex(p[2*i]);
	    ypoints[i] = scaley(p[2*i + 1]);
	}
	gc.setColor(c);
	gc.drawPolyline(xpoints, ypoints, npoints);
	gc.setColor(color);
    }

    /** Zeichnet ein gefuelltes Polygon in der Farbe c.
      * @param c Farbe fuer diese eine Operation.
      * @param p Paarweise horizontale und vertikale Koordinaten aller Punkte.
      */
    public void filledPoly(Color c, double... p)
    {
	int npoints = p.length/2;
	int[] xpoints = new int[npoints];
	int[] ypoints = new int[npoints];
	for(int i = 0;	i < npoints;  i++)
	{
	    xpoints[i] = scalex(p[2*i]);
	    ypoints[i] = scaley(p[2*i + 1]);
	}
	gc.setColor(c);
	gc.fillPolygon(xpoints, ypoints, npoints);
	gc.setColor(color);
    }

    /** Zeichnet einen Kreis in der aktuellen Zeichenfarbe.
      * @param x Horizontale Koordinate des Kreismittelpunktes.
      * @param y Vertikale Koordinate des Kreismittelpunktes.
      * @param r Radius des Kreises.
      */
    public void circle(double x, double y, double r)
    {
	ellipse(x, y, r, r);
    }

    /** Zeichnet einen gefuellten Kreis in der aktuellen Zeichenfarbe.
      * @param x Horizontale Koordinate des Kreismittelpunktes.
      * @param y Vertikale Koordinate des Kreismittelpunktes.
      * @param r Radius des Kreises.
      */
    public void filledCircle(double x, double y, double r)
    {
	filledEllipse(x, y, r, r);
    }

    /** Zeichnet eine Ellipse in der aktuellen Zeichenfarbe.
      * @param x Horizontale Koordinate des Mittelpunktes.
      * @param y Vertikale Koordinate des Mittelpunktes.
      * @param w Radius quer.
      * @param h Radius senkrecht.
      */
    public void ellipse(double x, double y, double w, double h)
    {
	w *= 2;
	h *= 2;
	x -= w/2;
	y += h/2;
	gc.drawOval(scalex(x), scaley(y), tox(w), toy(h));
    }

    /** Zeichnet eine gefuellte Ellipse in der aktuellen Zeichenfarbe.
      * @param x Horizontale Koordinate des Mittelpunktes.
      * @param y Vertikale Koordinate des Mittelpunktes.
      * @param w Radius quer.
      * @param h Radius senkrecht.
      */
    public void filledEllipse(double x, double y, double w, double h)
    {
	w *= 2;
	h *= 2;
	x -= w/2;
	y += h/2;
	gc.fillOval(scalex(x), scaley(y), tox(w), toy(h));
    }

    /** Zeichnet einen Kreis in der Farbe c.
      * @param c Farbe fuer diese eine Operation.
      * @param x Horizontale Koordinate des Kreismittelpunktes.
      * @param y Vertikale Koordinate des Kreismittelpunktes.
      * @param r Radius des Kreises.
      */
    public void circle(Color c, double x, double y, double r)
    {
	gc.setColor(c);
	ellipse(x, y, r, r);
	gc.setColor(color);
    }

    /** Zeichnet einen gefuellten Kreis in der Farbe c.
      * @param c Farbe fuer diese eine Operation.
      * @param x Horizontale Koordinate des Kreismittelpunktes.
      * @param y Vertikale Koordinate des Kreismittelpunktes.
      * @param r Radius des Kreises.
      */
    public void filledCircle(Color c, double x, double y, double r)
    {
	gc.setColor(c);
	filledEllipse(x, y, r, r);
	gc.setColor(color);
    }

    /** Zeichnet eine Ellipse in der Farbe c.
      * @param c Farbe fuer diese eine Operation.
      * @param x Horizontale Koordinate des Mittelpunktes.
      * @param y Vertikale Koordinate des Mittelpunktes.
      * @param w Radius quer.
      * @param h Radius senkrecht.
      */
    public void ellipse(Color c, double x, double y, double w, double h)
    {
	w *= 2;
	h *= 2;
	x -= w/2;
	y += h/2;
	gc.setColor(c);
	gc.drawOval(scalex(x), scaley(y), tox(w), toy(h));
	gc.setColor(color);
    }

    /** Zeichnet eine gefuellte Ellipse in der aktueFarbe cichenfarbe.
      * @param c Farbe fuer diese eine Operation.
      * @param x Horizontale Koordinate des Mittelpunktes.
      * @param y Vertikale Koordinate des Mittelpunktes.
      * @param w Radius quer.
      * @param h Radius senkrecht.
      */
    public void filledEllipse(Color c, double x, double y, double w, double h)
    {
	w *= 2;
	h *= 2;
	x -= w/2;
	y += h/2;
	gc.setColor(c);
	gc.fillOval(scalex(x), scaley(y), tox(w), toy(h));
	gc.setColor(color);
    }

    /** Aendert die logische Breite der Zeichenflaeche.
      * @param d Neue logische Breite.
      */
    public void scale(double d)
    {
	scale = width/d;
    }

    /** Aendert die Breite von Linien.
      * @param d Neue Linienbreite.
      */
    public void lineWidth(double d)
    {
	gc.setStroke(new BasicStroke((float)(scale*d), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    }

    private final int scalex(double x)
    {
	return tox(x) + width/2;
    }

    private final int scaley(double y)
    {
	return height/2 - toy(y);
    }

    private final int tox(double x)
    {
	return (int)(scale*x);
    }

    private final int toy(double y)
    {
	return (int)(scale*y);
    }
    
    /** Liefert die logische Breite.
      * @return Logische Breite.
      */
    public double width()
    {
    	return width/scale;
    }

    /** Liefert die logische Hoehe.
      * @return Logische Hoehe.
      */
    public double height()
    {
    	return height/scale;
    }
    
    /** Stellt die Fontgroesse neu ein.
      * @param pt Neue Fontgroesse in logischen Koordinaten.
      */
    public void font(double pt)
    {
    	font(fontname, fontstyle, pt);
    }
    
    /** Stellt die Schriftattribute neu ein.
      * @param st Schriftattribute (Font.PLAIN, Font.ITALIC, Font.BOLD oder eine Summe davon).
      */
    public void style(int st)
    {
    	font(fontname, st, fontsize);
    }
    
    /** Stellt die Schriftart neu ein.
      * @param fn Schriftart.
      * Entweder ein generischer Name (Serif, SansSerif, Monospaced, Dialog, DialogInput)
      * oder der absolute Filename einer Truetype-Datei.
      */
    public void font(String fn)
    {
    	font(fn, fontstyle, fontsize);
    }
    
    /** Stellt die Ausrichtung neu ein.
      * @param h Horizontale Textausrichtung bezueglich des Startpunktes
      * (-1 = rechtsbuendig, 0 = zentriert, 1 = linksbuendig = Voreinstellung).
      * @param v Vertikale Textausrichtung bezueglich des Startpunktes
      * (-1 = Oberkante, 0 = zentriert, 1 = Unterkante = Voreinstellung).
      */
    public void align(int h, int v)
    {
    	if(h > 0)
    	    alignHorizontal = Align.Left;
	else if(h < 0)
	    alignHorizontal = Align.Right;
	else
	    alignHorizontal = Align.Center;
    	if(v > 0)
    	    alignVertical = Align.Bottom;
	else if(v < 0)
	    alignVertical = Align.Top;
	else
	    alignVertical = Align.Center;
    }
    
    /** Stellt einen neuen Font ein.
      * @param fn Schriftart.
      * Entweder ein generischer Name (Serif, SansSerif, Monospaced, Dialog, DialogInput)
      * oder der absolute Filename einer Truetype-Datei
      * oder der Rumpf einer Truetype-Datei.
      * Im letzten Fall wird /usr/share/fonts/{fn}.ttf versucht.
      * @param st Schriftattribute (Font.PLAIN, Font.ITALIC, Font.BOLD oder eine Summe daraus).
      * @param pt Neue Fontgroesse in logischen Koordinaten.
      */
    public void font(String fn, int st, double pt)
    {
    	fontname = fn;
	fontstyle = st;
	fontsize = pt;
	try
	{
	    InputStream fontstream;
	    try
	    {
	        fontstream = new FileInputStream(fontname);
	    }
	    catch(FileNotFoundException ex)
	    {
	    	fontstream = new FileInputStream("/usr/share/fonts/" + fontname + ".ttf");
	    }
	    font = Font.createFont(Font.TRUETYPE_FONT, fontstream);
	    fontstream.close();
	}
	catch(Exception ex)
	{
	    font = new Font(fontname, fontstyle, 12);
	}
	font = font.deriveFont((float)tox(pt));
	gc.setFont(font);
    }
    
    /** Schreibt einen Text an die gegebene Position.
      * @param x Horizontale Koordinate.
      * @param y Vertikale Koordinate.
      * @param s Text.
      */
    public void text(double x, double y, String s)
    {
    	text(null, x, y, s);
    }
    
    /** Schreibt einen Text hinten an den zuletzt ausgegebenen Text.
      * Wenn das die erste Textausgabe ist, wird in der Mitte der Flaeche begonnen.
      * Die Ausrichtung wird hierbei ignoriert.
      * @param s Text.
      * @see #textAt
      */
    public void text(String s)
    {
    	text(null, textposX, textposY, s);
    }
    
    /** Schreibt einen Text in der Farbe c hinten an den zuletzt ausgegebenen Text.
      * Wenn das die erste Textausgabe ist, wird in der Mitte der Flaeche begonnen.
      * Die Ausrichtung wird hierbei ignoriert.
      * @param c Farbe.
      * @param s Text.
      * @see #textAt
      */
    public void text(Color c, String s)
    {
    	text(c, textposX, textposY, s);
    }
    
    /** Schreibt einen Text in der Farbe c an die gegebene Position.
      * @param c Farbe.
      * @param x Horizontale Koordinate.
      * @param y Vertikale Koordinate.
      * @param s Text.
      */
    public void text(Color c, double x, double y, String s)
    {
    	// Versatz der linken unteren Ecke der Boundingbox gegenueber 
	// den Textkoordinaten
    	double bbx = 0;
	double bby = 0;
	
	// Die Boundingbox wird gebraucht, um den Text auszurichten
	// und um die Textposition mitzufuehren
	Rectangle2D boundingBox = font.getStringBounds(s, fontRenderContext);
	
	// Aus der Ausrichtung den Versatz berechnen
	switch(alignHorizontal)
	{
	    case Center:	bbx = boundingBox.getWidth()/2; break;
	    case Right: 	bbx = boundingBox.getWidth(); 	break; 
	}
	switch(alignVertical)
	{
	    case Center:	bby = boundingBox.getHeight()/3; break;
	    case Top: 	bby = boundingBox.getHeight();  break; 
	}
	
	// Text ausgeben
	if(c == null)
    	    gc.drawString(s, (int)(scalex(x) - bbx), (int)(scaley(y) + bby));
	else
	{
	    gc.setColor(c);
    	    gc.drawString(s, (int)(scalex(x) - bbx), (int)(scaley(y) + bby));
	    gc.setColor(color);
	}
	
	// Textposition aktualisieren
	textposX = x + boundingBox.getWidth()/scale;
	textposY = y;
    }
    
    /** Liefert die aktuelle, horizontale Textposition.
      * Die naechste Textausgabe ohne Positionsangabe wuerde an dieser
      * horizontalen Position platziert.
      * @return Horizontale Textposition.
      * @see #text(String s)
      * @see #text(Color c, String s)
      */
    public double textAt()
    {
    	return textposX;
    }

    /** Speichert das aktuelle Bild auf eine Datei.
      * @param filename Name der Datei.
      * Das Format haengt von der Extension ab.
      */
    public void save(String filename) throws IOException
    {
	File f = new File(filename);
	String extension = f.getName().substring(f.getName().lastIndexOf('.') + 1);
	ImageIO.write(bufferedImage, extension, f);
    }

    /** Liest eine Zeichenflaeche aus einer Datei.
      * @param filename Name einer Bilddatei.
      */
    public static Painter read(String filename) throws IOException
    {
	return new Painter(ImageIO.read(new File(filename)));
    }
    
    /** Konstanten fuer die Ausrichtung. */
    private enum Align {Left, Right, Bottom, Top, Center}

    /** Lichte Weite in Pixel. */
    private final int width;

    /** Lichte Hoehe in Pixel. */
    private final int height;

    /** Skalierungsfaktor von logischen auf Pixelkoordinaten.
      * 10 bedeutet zum Beispiel, dass 1 logische Laengeneinheit auf 10
      * Pixel abgebildet wird.
      */
    private double scale;

    /** Aktuelle Zeichenfarbe. */
    private Color color = Color.BLACK;

    /** Dekoration. */
    private Insets insets = null;

    /** Das Pixelbild in das alle Zeichenoperationen laufen. */
    private final BufferedImage bufferedImage;

    /** Der Graphicskontext des Pixelbildes. */
    private Graphics2D gc;
    
    /** Der aktuelle Schriftsatz. */
    private Font font;
    
    /** Wird gebraucht um die Groesse eines gerasterten Textes herauszukriegen. */
    private FontRenderContext fontRenderContext;

    /** Der Name des aktuellen Fonts. */    
    private String fontname = "Dialog";
    
    /** Die Groesse des aktuellen Fonts in Point, nicht in logischen Einheiten. */    
    private double fontsize = 12;
    
    /** Die aktuelle horizontale Ausrichtung. */
    private Align alignHorizontal = Align.Left;
    
    /** Die aktuelle vertikale Ausrichtung. */
    private Align alignVertical = Align.Bottom;
    
    /** Die aktuellen Schriftattribute. */
    private int fontstyle = BOLD;
    
    /** Die horizontale Position der naechsten Textausgabe. */
    private double textposX;
    
    /** Die vertikale Position der naechsten Textausgabe. */
    private double textposY;
    
    /** Dateiname auf die die Ausgabe geschrieben wird, falls ohne Bildschirm benutzt. */
    private String hidden;
    
    /** Regenbogenfarben. */
    private Shades shades;
}
