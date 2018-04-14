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
import javax.swing.*;

public class AboutSTInq extends JDialog {

    public AboutSTInq(JFrame parent) {
        super(parent, "About Tree Finder", true /*modal*/);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setUpDisplay();
        this.setLocation(Math.round((parent.getSize().width - this.getSize().width) / 2), Math.round((parent.getSize().height - this.getSize().height) / 2));
        this.setVisible(true);
    }

    private void setUpDisplay() {
        JLabel aboutLabel1 = new JLabel("Tree Finder V1.0");
        JLabel aboutLabel2 = new JLabel("Copyright (c) 2007-2018");
        JLabel aboutLabel3 = new JLabel("Maen Artimy");

        aboutLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        aboutLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        aboutLabel3.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        panel.add(aboutLabel1);
        panel.add(aboutLabel2);
        panel.add(aboutLabel3);

        this.getContentPane().add(panel, BorderLayout.CENTER);
        this.pack();
        this.setSize(300, 100);

    }
}