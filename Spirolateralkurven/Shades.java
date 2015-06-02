import java.util.*;

/** Klasse zum Berechnen von Farbschattierungen auf dem Umfang des HLS-Kegels.
  * Es gilt:
  * <dl><dt>Sättigung<dd>0 = reine Farben, 192 = pastell, 255 = alles weiß.
  * <dt>Farbkreis<dd>0° = rot, 60° = gelb, 120° = grün, 180° = cyan, 240° = blau, 300° = magenta.
  * </dl>
  */
public class Shades
{
    /** Ctor für s gleichmäßig verteilte Farben.
      * @param s Anzahl gewünschter Farben.
      */
    public Shades(int s)
    {
        this(s, 0);
    }

    /** Ctor für s gleichmäßig verteilte Farben mit einer
      * Sättigung von l.
      * @param s Anzahl gewünschter Farben.
      * @param l Sättigung.
      */
    public Shades(int s, int l)
    {
        steps = s;
        low = l;
        offset = 128; // rng.nextInt(6*256);
    }

    /** Ctor für s gleichmäßig verteilte Farben mit einer Sättigung von l
      * und einer Startfarbe r.
      * @param s Anzahl gewünschter Farben.
      * @param l Sättigung.
      * @param r Startfarbe. Dabei werden jeweils 60° in 256 Schritte aufgeteilt.
      * 6·256 = 360° = der ganze Umfang des HLS-Kegels.
      * <p>0 = rot, 256 = gelb, 2·256 = grün, 3·256 = cyan, 4·256 = blau, 5·256 = magenta, 6·256 = rot, ...
      * <p>Werte >= 6·256  und < 0 werden in das zulässige Intervall abgebildet.
      */
    public Shades(int s, int l, int r)
    {
        steps = s;
        low = l;
        offset = Math.abs(r)%(6*256);
    }

    /** Liefert die Farbe zum gegebenen Index.
      * @param n Farbindex von 0 bis (n-1), wobei n im Konstruktor angegeben wurde.
      * Zu grosse n werden mit Modulus reduziert.
      * @return int-Array mit 3 Elementen für Rot, Grün, Blau.
      */
    public int[] shade(int n)
    {
        int r;
        int g;
        int b;
        int x = (n%steps*6*256/steps + offset)%(6*256);
        if(x < 256)
        {
            r = 255;
            g = x;
            b = 0;
        }
        else if(x < 2*256)
        {
            r = 2*256 - x - 1;
            g = 255;
            b = 0;
        }
        else if(x < 3*256)
        {
            r = 0;
            g = 255;
            b = x - 2*256;
        }
        else if(x < 4*256)
        {
            r = 0;
            g = 4*256 - x - 1;
            b = 255;
        }
        else if(x < 5*256)
        {
            r = x - 4*256;
            g = 0;
            b = 255;
        }
        else
        {
            r = 255;
            g = 0;
            b = 6*256 - x - 1;
        }
        return new int[] {scale(r), scale(g), scale(b)};
    }

    /** Liefert die Farbe zum gegebenen Index.
      * @param n Farbindex von 0 bis (n-1), wobei n im Konstruktor angegeben wurde.
      * Zu grosse n werden mit Modulus reduziert.
      * @return RGB-Farbe in der Darstellung #RRGGBB
      * mit 2 Hexziffern pro Primärfarbe.
      */
    public String shadehex(int n)
    {
        int[] rgb = shade(n);
        return String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
    }

    private int scale(int x)
    {
        return low + x*(255 - low)/255;
    }

    private int steps;

    private int low;

    private int offset;

    private final Random rng = new Random();
}
