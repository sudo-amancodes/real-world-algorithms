import java.util.List;

import bridges.base.SymbolCollection;

import java.util.ArrayList;

public class QuadTree {
    Node n;
    QuadTree topLeftTree;
    QuadTree topRightTree;
    QuadTree botLeftTree;
    QuadTree botRightTree;
    SymbolCollection sc;
    Boundary bd;

    float minX;
    float minY;
    float maxX;
    float maxY;

    public QuadTree(Boundary _bd, SymbolCollection _sc) {
        n = null;

        topLeftTree = null;
        topRightTree = null;
        botLeftTree = null;
        botRightTree = null;

        sc = _sc;
        bd = _bd;

        minX = bd.getTopLeft().getX();
        maxY = bd.getTopLeft().getY();

        minY = bd.getBotRight().getY();
        maxX = bd.getBotRight().getX();

    }

    boolean NotBoundary(Point p) {
        if (p.getX() <= minX || p.getX() >= maxX || p.getY() <= minY || p.getY() >= maxY) {
            return false;
        }
        return true;
    }

    void insert(Node node) {
        if (node == null) {
            return;
        }
        if (!NotBoundary(node.pos)) {
            return;
        }
        if (Math.abs(minX - maxX) <= 1 && Math.abs(maxY - minY) <= 1) {
            if (n == null)
                n = node;
            return;
        }
        if ((minX + maxX) / 2 >= node.pos.getX()) {
            if ((maxY + minY) / 2 >= node.pos.getY()) {
                // Indicates botLeftTree
                if (botLeftTree == null) {
                    Point point1 = new Point(minX, (maxY + minY) / 2);
                    Point point2 = new Point((minX + maxX) / 2, minY);
                    botLeftTree = new QuadTree(new Boundary(point1, point2, sc), sc);
                }
                botLeftTree.insert(node);
            } else {
                // Indicates topLeftTree
                if (topLeftTree == null) {
                    Point point1 = new Point(minX, maxY);
                    Point point2 = new Point((minX + maxX) / 2, (minY + maxY) / 2);
                    topLeftTree = new QuadTree(new Boundary(point1, point2, sc), sc);
                }
                topLeftTree.insert(node);
            }
        } else {
            if ((maxY + minY) / 2 >= node.pos.getY()) {
                // Indicates botRightTree
                if (botRightTree == null) {
                    Point point1 = new Point((minX + maxX) / 2, (minY + maxY) / 2);
                    Point point2 = new Point(maxX, minY);
                    botRightTree = new QuadTree(new Boundary(point1, point2, sc), sc);
                }
                botRightTree.insert(node);
            } else {
                // Indicates topRightTree
                if (topRightTree == null) {
                    Point point1 = new Point((minX + maxX) / 2, maxY);
                    Point point2 = new Point(maxX, (minY + maxY) / 2);
                    topRightTree = new QuadTree(new Boundary(point1, point2, sc), sc);
                }
                topRightTree.insert(node);
            }
        }
    }

    Point find(String name) {
        if (n != null && n.data.equals(name)) {
            return n.pos;
        }
        Point result;
        if (topLeftTree != null) {
            result = topLeftTree.find(name);
            if (result != null)
                return result;
        }
        if (topRightTree != null) {
            result = topRightTree.find(name);
            if (result != null)
                return result;
        }
        if (botLeftTree != null) {
            result = botLeftTree.find(name);
            if (result != null)
                return result;
        }
        if (botRightTree != null) {
            result = botRightTree.find(name);
            if (result != null)
                return result;
        }
        return null;
    }

    void query(Point p, int _level, String name) {
        if (p == null) {
            return;
        }

        if ((minX + maxX) / 2 >= p.getX()) {
            if ((maxY + minY) / 2 >= p.getY()) {
                // Indicates botLeftTree
                Point point1 = new Point(minX, (maxY + minY) / 2);
                Point point2 = new Point((minX + maxX) / 2, minY);
                new Boundary(point1, point2, sc, _level);

                if (botLeftTree == null) {
                    new Boundary(point1, point2, sc, name, p.getX(), p.getY());

                    return;
                }

                botLeftTree.query(p, _level + 1, name);
            } else {
                // Indicates topLeftTree
                Point point1 = new Point(minX, maxY);
                Point point2 = new Point((minX + maxX) / 2, (minY + maxY) / 2);
                new Boundary(point1, point2, sc, _level);

                if (topLeftTree == null) {
                    new Boundary(point1, point2, sc, name, p.getX(), p.getY());

                    return;
                }

                topLeftTree.query(p, _level + 1, name);
            }
        } else {
            if ((maxY + minY) / 2 >= p.getY()) {
                // Indicates topRightTree
                Point point1 = new Point((minX + maxX) / 2, (minY + maxY) / 2);
                Point point2 = new Point(maxX, minY);
                new Boundary(point1, point2, sc, _level);

                if (botRightTree == null) {
                    new Boundary(point1, point2, sc, name, p.getX(), p.getY());

                    return;
                }

                botRightTree.query(p, _level + 1, name);
            } else {
                // Indicates botRightTree
                Point point1 = new Point((minX + maxX) / 2, maxY);
                Point point2 = new Point(maxX, (minY + maxY) / 2);
                new Boundary(point1, point2, sc, _level);

                if (topRightTree == null) {
                    new Boundary(point1, point2, sc, name, p.getX(), p.getY());

                    return;
                }

                topRightTree.query(p, _level + 1, name);
            }
        }
    }
}