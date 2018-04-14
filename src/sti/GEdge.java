//   Copyright 2007-2018 Maen Artimy
//
//   Permission is hereby granted, free of charge, to any person obtaining a 
//   copy of this software and associated documentation files (the "Software"),
//   to deal in the Software without restriction, including without limitation 
//   the rights to use, copy, modify, merge, publish, distribute, sublicense, 
//   and/or sell copies of the Software, and to permit persons to whom the 
//   Software is furnished to do so, subject to the following conditions:
//
//    The above copyright notice and this permission notice shall be included 
//    in all copies or substantial portions of the Software.

//    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
//    OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY
//    , FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
//    THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
//    FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
//    DEALINGS IN THE SOFTWARE.

package sti;

import java.awt.*;

public class GEdge {

    public GNode n1, n2;
    public String p1, p2;
    public Point[] pts;
    private final Font commonFont = new Font("Arial", Font.PLAIN, 12);
    private final BasicStroke wideStroke = new BasicStroke(4.0f);

    public GEdge(GNode n1, GNode n2) {
        this.n1 = n1;
        this.n2 = n2;
        p1 = null;
        p2 = null;
    }

    public GEdge(GNode n1, String p1, GNode n2, String p2) {
        this.n1 = n1;
        this.n2 = n2;
        this.p1 = p1;
        this.p2 = p2;

        pts = getIconPoints();
    }

    private String shortString(String s) {
        try {
            s = s.replaceAll("Ethernet", "E");
            s = s.replaceAll("Gigabit", "G");
            s = s.replaceAll("Fast", "F");
            return s;
        } catch (Exception e) {
            System.err.println("Exception: " + e.toString() + " " + this);
        }
        return null;
    }

    private double getAngle() {
        double dY = n2.y - n1.y;
        double dX = n2.x - n1.x;

        return (dX == 0) ? Math.PI / 2 : Math.atan(dY / dX);
    }

    private double getLength() {
        double dY = n2.y - n1.y;
        double dX = n2.x - n1.x;

        return Math.sqrt(dX * dX + dY * dY);
    }

    /**
     * Returns an array of 3 (x,y) points where icons can be placed along the
     * edge
     */
    private Point[] getIconPoints() {
        Point[] points = new Point[3];

        int f = n1.x <= n2.x ? 1 : -1;
        double theta = getAngle();
        int dx = (int) Math.floor(n1.x + f * 0.75 * n1.d * Math.cos(theta));
        int dy = (int) Math.floor(n1.y + f * 0.75 * n1.d * Math.sin(theta));

        points[0] = new Point(dx, dy);

        double len = getLength();
        int dx1 = (int) Math.floor(n1.x + f * len / 2 * Math.cos(theta));
        int dy1 = (int) Math.floor(n1.y + f * len / 2 * Math.sin(theta));

        points[1] = new Point(dx1, dy1);

        int dx2 = (int) Math.floor(n1.x + f * (len - 0.75 * n2.d) * Math.cos(theta));
        int dy2 = (int) Math.floor(n1.y + f * (len - 0.75 * n2.d) * Math.sin(theta));

        points[2] = new Point(dx2, dy2);

        return points;
    }

    private Color getPortColor(String s) {
        if (s == null) {
            return Color.LIGHT_GRAY;
        }
        if (s.equals("Root")) {
            return Color.blue;
        } else if (s.equals("Designated")) {
            return Color.green;
        } else if (s.equals("Alternate")) {
            return Color.red;
        } else {
            return Color.orange;
        }
    }

    public void plot(Graphics2D g) {

        if (GNode.rstpRequired) {
            plotRSTPMode(g);
        } else {
            plotRegularMode(g);
        }

    }

    private void plotRegularMode(Graphics2D g) {
        Color currentColor = g.getColor();
        Font currentFont = g.getFont();
        BasicStroke bs = (BasicStroke) g.getStroke();

        g.setStroke(wideStroke);
        g.setColor(Color.blue);
        g.drawLine(n1.x, n1.y, n2.x, n2.y);


        g.setFont(commonFont);
        //FontMetrics fm = g.getFontMetrics();

        g.setColor(Color.black);

        g.translate(pts[0].x, pts[0].y);
        g.scale(1, -1);
        //**************
        String str = shortString(p1);
        //int w = fm.stringWidth(str);
        //if((n2.x > n1.x) && (n2.y > n1.y)) g.drawString(str, -w, 0);
        //else 
        g.drawString(str, 0, 0);
        //**********
        g.scale(1, -1);

        g.translate(-pts[0].x + pts[2].x, -pts[0].y + pts[2].y);
        g.scale(1, -1);
        //**************
        str = shortString(p2);
        //w = fm.stringWidth(str);
        //if((n2.x > n1.x) && (n2.y > n1.y)) g.drawString(str, -w, 0);
        //else 
        g.drawString(str, 0, 0);
        //**********
        g.scale(1, -1);

        g.translate(-pts[2].x, -pts[2].y);

        g.setFont(currentFont);
        g.setColor(currentColor);
        g.setStroke(bs);
    }

    private void plotRSTPMode(Graphics2D g) {

        String[][] table1, table2;
        if (n1.rstpReady) {
            table1 = n1.getRSTPRow();
        } else {
            return;
        }

        if (n2.rstpReady) {
            table2 = n2.getRSTPRow();
        } else {
            return;
        }

        if (table1 == null || table2 == null) {
            return;
        }

        Color currentColor = g.getColor();
        Font currentFont = g.getFont();
        BasicStroke bs = (BasicStroke) g.getStroke();

        g.setStroke(wideStroke);
        g.setColor(Color.blue);

        g.drawLine(n1.x, n1.y, n2.x, n2.y);

        g.setFont(commonFont);

        //       g.setColor(Color.black);

        g.translate(pts[0].x, pts[0].y);            // position the icon in the first point
        g.scale(1, -1);
        for (int i = 1; i < table1[0].length; i++) {
            if (table1[0][i].equals(p1)) {
                g.setColor(getPortColor(table1[1][i]));
                break;
            }
        }
        g.fillOval(-7, -7, 14, 14);
        g.setColor(Color.black);
        g.drawString(shortString(p1), 0, 0);
        g.scale(1, -1);

        g.translate(-pts[0].x + pts[2].x, -pts[0].y + pts[2].y);
        g.scale(1, -1);
        for (int i = 1; i < table2[0].length; i++) {
            if (table2[0][i].equals(p2)) {
                g.setColor(getPortColor(table2[1][i]));
                break;
            }
        }
        g.fillOval(-7, -7, 14, 14);
        g.setColor(Color.black);
        g.drawString(shortString(p2), 0, 0);
        g.scale(1, -1);

        g.translate(-pts[2].x, -pts[2].y);

        g.setFont(currentFont);
        g.setColor(currentColor);
        g.setStroke(bs);
    }

    public String toString() {
        return (n1 + " , " + p1 + " -- " + n2 + " , " + p2);
    }
}