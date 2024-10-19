import bridges.base.Circle;
import bridges.base.Polyline;
import bridges.base.Rectangle;
import bridges.base.SymbolCollection;
import bridges.base.Text;
import bridges.connect.Bridges;

public class Boundary {
    Point topleft;
    Point bottomright;
    int level;
    String[] colors = { "red", "green", "blue", "cyan", "magenta", "yellow", "purple", "orange", "red", "green", "blue",
            "cyan", "magenta", "yellow", "purple", "orange" };
    SymbolCollection sc;

    public Boundary(Point _tl, Point _br, SymbolCollection _sc, String name, float x, float y) {
        topleft = _tl;
        bottomright = _br;
        sc = _sc;

        addCircle(x, y, name);

    }

    public Boundary(Point _tl, Point _br, SymbolCollection _sc, int _level) {
        topleft = _tl;
        bottomright = _br;
        sc = _sc;
        level = _level;

        drawColorRect();

    }

    public Boundary(Point _tl, Point _br, SymbolCollection _sc) {
        topleft = _tl;
        bottomright = _br;
        sc = _sc;

        drawRect();

    }

    public Point getTopLeft() {
        return topleft;
    }

    public Point getBotRight() {
        return bottomright;
    }

    public void drawPoly() {
        float x1 = topleft.getX();
        float y1 = topleft.getY();
        float x2 = bottomright.getX();
        float y2 = bottomright.getY();

        Polyline p = new Polyline();
        p.addPoint(x1, y1);
        p.addPoint(x2, y1);
        p.addPoint(x2, y2);
        p.addPoint(x1, y2);
        p.addPoint(x1, y1);
        p.setStrokeColor("red");
        p.setLayer(3);
        sc.addSymbol(p);
    }

    public void drawRect() {
        Rectangle rect = new Rectangle(topleft.getX(), bottomright.getY(), bottomright.getX() - topleft.getX(),
                topleft.getY() - bottomright.getY());
        rect.setStrokeColor("darkgreen");
        rect.setFillColor("grey");
        rect.setLayer(3);
        sc.addSymbol(rect);
    }

    public void drawColorRect() {
        Rectangle rect = new Rectangle(topleft.getX(), bottomright.getY(), bottomright.getX() - topleft.getX(),
                topleft.getY() - bottomright.getY());
        rect.setStrokeColor("purple");
        rect.setFillColor(colors[level]);
        rect.setLayer(0);
        sc.addSymbol(rect);
    }

    public void addCircle(float x, float y, String name) {
        Circle c = new Circle(x, y, 2.0f);
        c.setFillColor("orange");
        sc.addSymbol(c);
        Text l = new Text();
        l.setAnchorLocation(x, y);
        l.setFontSize(5.0f);
        // l.setOpacity(.3f);
        l.setText(name);
        l.setFillColor("white");
        l.setLayer(0);
        sc.addSymbol(l);
    }
}