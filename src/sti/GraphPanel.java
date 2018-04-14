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
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

public class GraphPanel extends JPanel {
    public static int GraphRefreshTime = 500;
    private AffineTransform mainTransform;
    private Dimension preferredSize;
    private double D = 1000.0;
       
    private java.util.List<GNode> nodeList;
    private java.util.List<GEdge> edgeList;
    
    GNode pick;
    private STInq parent;
    
    private class MAdapter extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            for (GNode gn : nodeList) {
                Point2D.Double p1 = new Point2D.Double(gn.x - gn.d/2, gn.y - gn.d/2);					//bottom left
                mainTransform.transform(p1, p1);
                Point2D.Double p2 = new Point2D.Double(gn.x + gn.d/2, gn.y + gn.d/2);		//top right
                mainTransform.transform(p2, p2);
                
                Rectangle rec = new Rectangle((int)Math.floor(p1.x), (int)Math.floor(p2.y),
                        (int)Math.abs(Math.floor(p2.x - p1.x)),
                        (int)Math.abs(Math.floor(p2.y - p1.y)));
                if(rec.contains(x, y)) {
                    pick = gn;
                    //Messenger.append("Node ID : "+ gn.stringID + ", Node IP Address : "+ gn.hostAddress.toString() + "\n");
                    parent.nodeField.setText("Waiting..");
                    break;
                }
            }
            e.consume();
        }
        
        public void mouseReleased(MouseEvent e) {
            if(pick != null && pick.isReady()) {
                String[][] set = pick.getSNMPDataSet();
                if(set != null) {
                    parent.createOIDTable(set, pick.dataSet.getDataHeader());
                    parent.nodeField.setText(pick.stringID);
                }
            }
            pick = null;
            e.consume();
        }
    }
    
    public GraphPanel(STInq parent) {
        //preferredSize = new Dimension(500,500);
        setBackground(Color.white);
        
        nodeList = null;
        this.parent = parent;
        
        Action updateGraphAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        };
        
        new javax.swing.Timer(GraphRefreshTime, updateGraphAction).start();
        
    }
    
        /*public void setSize(int size) {
                switch(size) {
                        case -1: preferredSize = new Dimension(250,250);
                        case 1: preferredSize = new Dimension(750,750);
                        default: preferredSize = new Dimension(500,500);
                }
        }*/
    
    public void setNodeList(java.util.List<GNode> list) {
        nodeList = list;
        addMouseListener(new MAdapter());
    }
    
    public void setEdgeList(java.util.List<GEdge> list) {
        edgeList = list;
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Rectangle r = getBounds();
        int w = r.width;
        int h = r.height;
        double d = Math.min(w, h);
        
        Graphics2D g2 = (Graphics2D)g;
        
//        String s = "Text Goes Here";
//        FontMetrics fm = g2.getFontMetrics();
//        g2.drawString(s, w-fm.stringWidth(s)-10, h-fm.getHeight());
        
        // Set translation
        AffineTransform savedAT = g2.getTransform();
        mainTransform = new AffineTransform();
        mainTransform.translate(0, h);
        mainTransform.scale(d/D, -d/D);
        g2.transform(mainTransform);
        
        //draw the reference circles
        //g2.drawOval(300, 300, 400, 400);
        //g2.drawOval(100, 100, 800, 800);
        
        //draw the nodes
                /*
                g2.setColor(Color.lightGray);
                for(int i=90; i<360; i+=120) {
                        int x = (int)Math.floor(200 * Math.cos(i/360.0 * 2.0 * Math.PI));
                        int y = (int)Math.floor(200 * Math.sin(i/360.0 * 2.0 * Math.PI));
                        g2.drawOval(460 + x, 460 + y, 80, 80);
                }
                 
                for(int i=9; i<360; i+=18) {
                        int x = (int)Math.floor(400 * Math.cos(i/360.0 * 2.0 * Math.PI));
                        int y = (int)Math.floor(400 * Math.sin(i/360.0 * 2.0 * Math.PI));
                        g2.drawOval(470 + x, 470 + y, 60, 60);
                }*/
        
        
        if(edgeList != null) {
            for(GEdge ge : edgeList) {
                ge.plot(g2);
            }
        }
        
        if(nodeList != null) {
            for(GNode gn : nodeList) {
                gn.plot(g2);
            }
        }
        
        g2.setTransform(savedAT);
        
    }
    
        /*public Dimension getPreferredSize() {
                return preferredSize;
        }*/
    
    
}

