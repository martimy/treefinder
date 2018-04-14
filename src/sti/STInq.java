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

import java.util.*;
import java.net.*;
import java.awt.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.io.*;
import sti.ReadXMLFile.InputParam;

public class STInq extends JFrame implements ActionListener, Runnable {

    MenuBar theMenubar;
    Menu fileMenu, viewMenu;
    MenuItem aboutItem, optionsItem, quitItem;
    MenuItem viewItem1, viewItem2, viewItem3;
    JTable OIDTable;
    JScrollPane OIDScroll;
    JTextArea messagesArea;
    JScrollPane messagesScroll;
    JButton clearButton;
    JComboBox typeBox;
    JComboBox viewBox;
    JTextField nodeField;
    GraphPanel graphPanel;
    //int row;
    java.util.List<GNode> nodeList;
    //java.util.List<GEdge> edgeList2;
    Thread managementThread;
    final String[] dataSetsLabels = new String[]{"System", "Interface", "CDP", "STP", "RSTP", "VLAN", "HSRP"};
    String[] viewLabels;
//    = new String[]{"Select RSTP view", "NONE", "VLAN 1", "VLAN 100",
//    "VLAN 101", "VLAN 102", "VLAN 103", "VLAN 104",
//    "VLAN 111", "VLAN 112", "VLAN 113", "VLAN 114",
//    "VLAN 255"};
    final int RefreshInterval = 2000;		// in milliseconds
    ExecutorService tpes = Executors.newFixedThreadPool(5);
    String netName;

    private void setParameters(InputParam p) {
        netName = p.netName;
        GNode.COMMUNITY = p.readString;
        for (int i = 0; i < p.ipList.length; i++) {
            try {
                GNode gn = new GNode(InetAddress.getByName(p.ipList[i])); // get the IP address
                gn.x = Integer.parseInt(p.xCoord[i]);
                gn.y = Integer.parseInt(p.yCoord[i]);
                nodeList.add(gn);
            } catch (UnknownHostException ex) {
                Logger.getLogger(STInq.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        viewLabels = new String[p.vlanList.length + 2];
        viewLabels[0] = "Select VLAN";
        viewLabels[1] = "NONE";
        for (int i = 0; i < p.vlanList.length; i++) {
            viewLabels[i + 2] = p.vlanList[i];
        }
    }

    // WindowCloseAdapter to catch window close-box closings
    private class WindowCloseAdapter extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    };

    public STInq() {
        nodeList = Collections.synchronizedList(new java.util.ArrayList<GNode>());
        //edgeList = new java.util.ArrayList<GEdge>();
        //edgeList2 = Collections.synchronizedList(new java.util.ArrayList<GEdge>());

        GNode.parent = this;
        GNode.dataSet = new SysDataSet();

        //setUpDisplay();
    }

    public void setUpDisplay() {


        this.setTitle("Tree Finder - " + netName);

        this.getRootPane().setBorder(new BevelBorder(BevelBorder.RAISED));

        // add WindowCloseAdapter to catch window close-box closings
        addWindowListener(new WindowCloseAdapter());


        // Menu definition

        theMenubar = new MenuBar();
        this.setMenuBar(theMenubar);
        fileMenu = new Menu("File");
        viewMenu = new Menu("View");

        aboutItem = new MenuItem("About...");
        aboutItem.setActionCommand("about");
        aboutItem.addActionListener(this);
        fileMenu.add(aboutItem);

        optionsItem = new MenuItem("Options...");
        optionsItem.setActionCommand("options");
        optionsItem.addActionListener(this);
        fileMenu.add(optionsItem);

        fileMenu.addSeparator();

        quitItem = new MenuItem("Quit");
        quitItem.setActionCommand("quit");
        quitItem.addActionListener(this);
        fileMenu.add(quitItem);

        theMenubar.add(fileMenu);

        viewItem1 = new MenuItem("Small");
        viewItem1.setActionCommand("small");
        viewItem1.addActionListener(this);
        viewMenu.add(viewItem1);

        viewItem2 = new MenuItem("Normal");
        viewItem2.setActionCommand("normal");
        viewItem2.addActionListener(this);
        viewMenu.add(viewItem2);

        viewItem3 = new MenuItem("Large");
        viewItem3.setActionCommand("large");
        viewItem3.addActionListener(this);
        viewMenu.add(viewItem3);

        theMenubar.add(viewMenu);

        // Controls definition

        JLabel messagesLabel = new JLabel("Responses:");
        JLabel copyrightLabel = new JLabel("Copyright (c) 2007 Maen Artimy");
        JLabel typeLabel = new JLabel("Type:");

        OIDTable = new JTable(new DefaultTableModel(new Object[4][2], new String[]{"Item", "Value"}));
        OIDTable.setPreferredScrollableViewportSize(new Dimension(600, 65));

        messagesArea = new JTextArea(3, 40);
        messagesScroll = new JScrollPane(messagesArea);
        Messenger.messageArea = messagesArea;

        clearButton = new JButton("Clear messages");
        clearButton.setActionCommand("clear messages");
        clearButton.addActionListener(this);

        typeBox = new JComboBox(dataSetsLabels);
        typeBox.setPreferredSize(new Dimension(150, 20));
        typeBox.setActionCommand("change type");
        typeBox.addActionListener(this);

        JLabel snmpLabel = new JLabel("SNMP Set : ");
        JLabel nodeLabel = new JLabel("Node : ");
        nodeField = new JTextField(10);
        nodeField.setEditable(false);

        viewBox = new JComboBox(viewLabels);
        viewBox.setActionCommand("change view");
        viewBox.addActionListener(this);

        graphPanel = new GraphPanel(this);
        graphPanel.setPreferredSize(new Dimension(500, 500));

        // Panel layout

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(snmpLabel);
        topPanel.add(typeBox);
        topPanel.add(nodeLabel);
        topPanel.add(nodeField);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(topPanel, BorderLayout.PAGE_START);
        tablePanel.add(new JScrollPane(OIDTable), BorderLayout.CENTER);

        JPanel centrePanel = new JPanel(new BorderLayout());
        centrePanel.add(viewBox, BorderLayout.PAGE_START);
        centrePanel.add(new JScrollPane(graphPanel), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(clearButton, BorderLayout.PAGE_START);
        bottomPanel.add(messagesScroll, BorderLayout.CENTER);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Messages", bottomPanel);
        tabbedPane.add("SNMP Table", tablePanel);

        //this.getContentPane().add(tablePanel, BorderLayout.PAGE_START);
        this.getContentPane().add(centrePanel, BorderLayout.CENTER);
        this.getContentPane().add(tabbedPane, BorderLayout.PAGE_END);

        setSize(700, 500);
        pack();
        setVisible(true);

    }

    public void createOIDTable(Object[][] set, Object[] header) {
        ((DefaultTableModel) OIDTable.getModel()).setDataVector(set, header);
    }

    public void actionPerformed(ActionEvent theEvent) {
        // respond to button pushes, menu selections
        String command = theEvent.getActionCommand();

        if (command.equals("quit")) {
            System.exit(0);
        }

        if (command.equals("clear messages")) {
            messagesArea.setText("");
        }

        if (command.equals("about")) {
            AboutSTInq aboutDialog = new AboutSTInq(this);
        }

        if (command.equals("options")) {
            OptionDialog optionsDialog = new OptionDialog(this);
        }

        if (command.equals("change type")) {
            String type = (String) typeBox.getSelectedItem();
            Messenger.append("Selected type : " + type + "\n");
            //Messenger.append("Selected index : " + typeBox.getSelectedIndex() + "\n");
            //GNode.entryIDs = dataSets.getOIDEntries(type);
            //GNode.tableIDs = dataSets.getOIDTables(type);
            if (type.equals("Interface")) {
                GNode.dataSet = new InterfaceDataSet();
            } else if (type.equals("STP")) {
                GNode.dataSet = new STPDataSet();
            } else if (type.equals("RSTP")) {
                GNode.dataSet = new RSTPDataSet();
            } else if (type.equals("HSRP")) {
                GNode.dataSet = new HSRPDataSet();
            } else if (type.equals("CDP")) {
                GNode.dataSet = new CDPDataSet();
            } else if (type.equals("VLAN")) {
                GNode.dataSet = new VLANDataSet();
            } else {
                GNode.dataSet = new SysDataSet();
            }
        }

        if (command.equals("change view")) {
            String view = (String) viewBox.getSelectedItem();
            Messenger.append("Selected view : " + view + "\n");

            if (view.equals("NONE")) {
                GNode.rstpRequired = false;
            } else if (view.startsWith("VLAN")) {
                String vlan = view.substring(5);
                GNode.rstpRequired = true;
                GNode.selectedVlan = vlan;
            }
        }

        if (command.equals("small")) {
            graphPanel.setPreferredSize(new Dimension(250, 250));
            graphPanel.revalidate();
        }

        if (command.equals("normal")) {
            graphPanel.setPreferredSize(new Dimension(500, 500));
            graphPanel.revalidate();
        }

        if (command.equals("large")) {
            graphPanel.setPreferredSize(new Dimension(1000, 1000));
            graphPanel.revalidate();
        }

    }

    // protected void Messenger.append(String s) { messagesArea.append(s); }
    /**
     * Reads a text file that contains the hosts' IP addresses seperated by a
     * semicolon.
     */
    public void readIPFile(String fname) {

        StringBuilder sb = new StringBuilder();
        java.util.List<String> ipList = new java.util.ArrayList<String>();

        try {
            BufferedReader in = new BufferedReader(new FileReader(fname + ".node"));

            String s;
            while ((s = in.readLine()) != null) {
                sb.append(s);
            }
            in.close();

            StringTokenizer st = new StringTokenizer(sb.toString(), ";");

            while (st.hasMoreTokens()) {
                String tkn = st.nextToken();
                StringTokenizer ins = new StringTokenizer(tkn, "\t, ");
                while (ins.hasMoreTokens()) {
                    GNode gn = new GNode(InetAddress.getByName(ins.nextToken())); // get the IP address
                    // get x, y
                    gn.x = Integer.parseInt(ins.nextToken());
                    gn.y = Integer.parseInt(ins.nextToken());
                    nodeList.add(gn);
                }
            }
            in.close();


        } catch (IOException e) {
            System.err.println("Exception: " + e + "\n");
        }
    }

    private GNode findNodeString(String stringID) {
        for (GNode gn : nodeList) {
            if (gn.isReady()) {     // means the string is ready
                if (gn.stringID.equals(stringID)) {			// check
                    return gn;
                }
            }
        }
        return null;
    }

    /**
     * creates edges between nodes besed on reterived CDP information
     */
    public void updateEdges() {
        String[][] cdpTable;
        DataInterface cdpSet = new CDPDataSet();

        java.util.List<GEdge> tmpEdgeList = Collections.synchronizedList(new java.util.ArrayList<GEdge>());

        for (GNode gn : nodeList) {
            if (gn.isReady()) {
                //cdpTable = gn.getSNMPDataSet(cdpSet);
                cdpTable = gn.getCDPTable();        // find the neighbours of this nodes using CDP protocol

                if (cdpTable != null) {
                    for (int i = 0; i < cdpTable.length; i++) {  // for each one of the number of neighbours
                        GNode gn2 = findNodeString(cdpTable[i][CDPDataSet.DeviceIDCol]);
                        if (gn2 != null && cdpTable[i][CDPDataSet.LocalPortCol] != null && cdpTable[i][CDPDataSet.DevicePortCol] != null) { // port numbers should never be null for the first node but it happend !!
                            if (gn2.isReady()) {
                                GEdge e = findEdge(tmpEdgeList, gn, cdpTable[i][CDPDataSet.LocalPortCol], gn2, cdpTable[i][CDPDataSet.DevicePortCol]);
                            }
                        }
                    }
                }
            }
        }

        //Messenger.append("New Edges " + tmpEdgeList + "\n");

        graphPanel.setEdgeList(tmpEdgeList);
    }

    private GEdge findEdge(java.util.List<GEdge> list, GNode n1, String p1, GNode n2, String p2) {
        if (n1.hostAddress.equals(n2.hostAddress)) {
            return null;
        } else if (n1.hostAddress.toString().compareTo(n2.hostAddress.toString()) > 0) {
            // the left side node should be always smaller the right edge
            // otherwise, flip the nodes
            GNode tmp1 = n2;
            n2 = n1;
            n1 = tmp1;

            String tmp2 = p2;
            p2 = p1;
            p1 = tmp2;
        }

        for (GEdge ge : list) {
            if (ge.n1.equals(n1) && ge.p1.equals(p1)) {
                return ge;
            }
        }
        GEdge ge = new GEdge(n1, p1, n2, p2);
        list.add(ge);
        return ge;
    }

    public void start() {
        graphPanel.setNodeList(nodeList);

        if (managementThread == null) {
            managementThread = new Thread(this, "SeqInq Thread");
            managementThread.start();
        }
    }

    public void run() {
        graphPanel.setNodeList(nodeList);

        while (true) {
            //long time = System.currentTimeMillis() ;
            //Messenger.append("Refreshing started at " + time);
            //contactAllNodes();
            for (GNode gn : nodeList) {
                tpes.submit(gn);
            }
            //graphPanel.repaint();
            updateEdges();
            //graphPanel.repaint();
            //Messenger.append("... and ended in " + (System.currentTimeMillis()-time + "\n"));

            try {
                Thread.sleep(RefreshInterval);

            } catch (InterruptedException e) {
            }
        }

    }

    public static void main(String args[]) {
        STInq theApp = new STInq();

        if (args.length == 0) {
            System.exit(0);
        }

        Options.load();
        ReadXMLFile r = new ReadXMLFile();
        InputParam p = r.read(args[0]);
        p.print();
        theApp.setParameters(p);
        theApp.setUpDisplay();
        theApp.run();
        Options.store();

    }
}
