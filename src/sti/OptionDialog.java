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
//import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.SpringLayout;

/**
 *
 * @author martimy
 */
public class OptionDialog extends JDialog {

    public OptionDialog(JFrame parent) {
        super(parent, "Options", true /*modal*/);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        initComponents();
        this.setLocation(Math.round((parent.getSize().width - this.getSize().width) / 2), Math.round((parent.getSize().height - this.getSize().height) / 2));
        //this.setVisible(true);

        //Schedule a job for the event-dispatching thread:
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible(true);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents() {

        String[] labels = {
            "Read Community : ",
            "SNMP Timeout (ms) : ",
            "Data Refresh Rate (ms) : ",
            "Graph Refresh Rate (ms) : "};
        int numPairs = labels.length;

        fields = new JTextField[numPairs];
        JPanel panel = new JPanel(new SpringLayout());
        for (int i = 0; i < numPairs; i++) {
            JLabel lbl = new JLabel(labels[i], JLabel.TRAILING);
            lbl.setLabelFor(fields[i]);

            fields[i] = new JTextField(10);
            panel.add(lbl);
            panel.add(fields[i]);
        }

        setDefaults();

        SpringUtilities.makeCompactGrid(panel,
                numPairs, 2, //rows, cols
                5, 5, //initialX, initialY
                5, 5);//xPad, yPad

        okButton = new JButton("Ok");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        defaultButton = new JButton("Reset values");
        defaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultButtonActionPerformed(evt);
            }
        });

        cancleButton = new JButton("Cancel");
        cancleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancleButtonActionPerformed(evt);
            }
        });

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(okButton);
        buttons.add(defaultButton);
        buttons.add(cancleButton);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.PAGE_END);
        //Display the window.
        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            Options.PUBLIC_COMMUNITY = fields[0].getText();
            Options.SNMP_TIMEOUT = new Integer(fields[1].getText()).toString();
            Options.REFRESH_RATE = new Integer(fields[2].getText()).toString();
            Options.GRAPH_REFRESH_TIME = new Integer(fields[3].getText()).toString();
            this.dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Number formatting", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void defaultButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setDefaults();
    }

    private void cancleButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private void setDefaults() {
        fields[0].setText(Options.PUBLIC_COMMUNITY);
        fields[1].setText(Options.SNMP_TIMEOUT);
        fields[2].setText(Options.REFRESH_RATE);
        fields[3].setText(Options.GRAPH_REFRESH_TIME);
    }
    // Variables declaration - do not modify
    private JButton okButton, cancleButton, defaultButton;
    private JTextField[] fields;
}
