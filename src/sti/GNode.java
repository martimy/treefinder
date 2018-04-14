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

import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import snmp.*;

public class GNode implements Runnable { //Callable<Integer> {
    // The GNode states

    public static final int ACTIVE_STATE = 2;
    public static final int STANDBY_STATE = 1;
    public static final int INACTIVE_STATE = 0;
    private int state;
    private final Color ACTIVE_COLOR = Color.GREEN;
    private final Color STANDBY_COLOR = Color.YELLOW;
    private final Color INACTIVE_COLOR = Color.RED;
    private Color[] colorArray = {INACTIVE_COLOR, STANDBY_COLOR, ACTIVE_COLOR};
    private static int MAX_TRIALS = 2;
    private int stateCounter;
    public String stringID;
    public InetAddress hostAddress;
    public int x, y, d;
    public boolean active;
    private StringBuffer textMessage;
    Thread myThread;
    public static STInq parent;
    public static String COMMUNITY = "s$cn$t_ro";
    public static int VERSION = 1;
    //public static String[][] entryIDs = { {"1.3.6.1.2.1.1.5.0"}, {"Label"} };
    public static String[][] entryIDs = {{"1.3.6.1.4.1.9.2.1.3.0"}, {"Label"}};
    public static DataInterface dataSet;
    public String[][] rstpTable, hsrpTable;
    public String[] rstpHeader, hsrpHeader;
    public static String selectedVlan;
    public static boolean rstpRequired;
    public boolean rstpReady;
    protected final String idOID = "1.3.6.1.4.1.9.2.1.3.0";
    protected final String ifOID = "1.3.6.1.2.1.2.2.1.3";
    private final Font commonFont = new Font("Arial", Font.PLAIN, 18);
    private final BasicStroke thickStroke = new BasicStroke(8.0f);
    final static float dash1[] = {10.0f};
    final static BasicStroke dashedStroke = new BasicStroke(8.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
    public Map<String, String> ifMap;

    /*public GNode (String id) {
     this.ID = id;
         
     x = 0;
     y = 0;
     d = 60;
     active = false;
     }*/
    public GNode(InetAddress ipAdd) {
        this.hostAddress = ipAdd;

        x = 0;
        y = 0;
        d = 60;

        state = INACTIVE_STATE;
        stateCounter = 0;
    }

    public void setIPAddress(InetAddress ipAdd) {
        this.hostAddress = ipAdd;
    }

    public boolean isReady() {
        return state != INACTIVE_STATE;
    }

    /*public synchronized void setState(int t) {
     state = t;
     }
    
     public synchronized int getState() {
     return state;
     }*/
    public void plot(Graphics2D g) {

        Color currentColor = g.getColor();
        Font currentFont = g.getFont();

        //GradientPaint gp = new GradientPaint(x - d/2, y, colorArray[state], x + d/2, y, Color.WHITE);
        //g.setPaint(gp);
        g.setColor(colorArray[state]);
        g.fillOval(x - d / 2, y - d / 2, d, d);
        g.setColor(Color.BLACK);
        g.drawOval(x - d / 2, y - d / 2, d, d);


        if (rstpRequired && rstpReady) {
            BasicStroke bs = (BasicStroke) g.getStroke();
            //g.setColor(Color.CYAN);           
            if (getHSRPStatus().equals("Standby")) {
                g.setStroke(dashedStroke);
                g.drawOval(x - d / 2 - 4, y - d / 2 - 4, d + 8, d + 8);
            }
            if (getHSRPStatus().equals("Active")) {
                g.setStroke(thickStroke);
                g.drawOval(x - d / 2 - 4, y - d / 2 - 4, d + 8, d + 8);
            }
            g.setColor(Color.BLACK);
            g.setStroke(bs);
        }

        g.translate(x, y);
        g.scale(1, -1);

        if (isReady()) {
            g.setFont(commonFont);
            FontMetrics metrics = g.getFontMetrics();

            int h = metrics.getHeight();
            int w = metrics.stringWidth(stringID);

            g.drawString(stringID, -(float) w / 2, (float) h / 2);

        }
        /*if(active) {
         int row = 0;
         StringTokenizer st = new StringTokenizer(textMessage.toString(), "\n");
         while(st.hasMoreTokens()) {
         g.drawString(st.nextToken(), 0, row+=15);
         }
         }*/

        g.scale(1, -1);
        g.translate(-x, -y);

        g.setFont(currentFont);
        g.setColor(currentColor);

    }
    protected SNMPv1CommunicationInterface comInterface;

    protected SNMPv1CommunicationInterface getComInterface()
            throws IOException, SocketException, SNMPBadValueException, SNMPGetException {
        if (comInterface == null) {
            comInterface = new SNMPv1CommunicationInterface(VERSION, hostAddress, COMMUNITY);
            comInterface.setSocketTimeout(5000);
        }
        return comInterface;
    }

    public void getHSRPView(SNMPv1CommunicationInterface comInterface)
            throws IOException, SocketException, SNMPBadValueException, SNMPGetException {

        HSRPDataSet set = new HSRPDataSet();

        hsrpTable = set.getDataTable(comInterface);
        hsrpHeader = set.getDataHeader();

    }

    public void getRSTPView(SNMPv1CommunicationInterface comInterface)
            throws IOException, SocketException, SNMPBadValueException, SNMPGetException {

        RSTPDataSet set = new RSTPDataSet();

        rstpTable = set.getDataTable(comInterface);
        rstpHeader = set.getDataHeader();

    }

    public String[][] getRSTPRow() {
        String[][] ports = new String[2][rstpTable[0].length];

        int row;
        for (row = 0; row < rstpTable.length; row++) {
            if (rstpTable[row][0].equals(selectedVlan)) {
                break;
            }
        }
        if (row < rstpTable.length) {
            ports[0] = rstpHeader;
            ports[1] = rstpTable[row];
            return ports;
        }

        return null;
    }
    private int rstpStatus;

    public String getHSRPStatus() {
        int row;
        for (row = 0; row < hsrpTable.length; row++) {
            if (hsrpTable[row][0].equals(selectedVlan)) {
                break;
            }
        }
        if (row < hsrpTable.length) {
            return hsrpTable[row][7];       // 7 is the col that contain the status
        }

        return "";
    }

    public String[][] getSNMPDataSet() {
        try {

            //SNMPv1CommunicationInterface comInterface = new SNMPv1CommunicationInterface(VERSION, hostAddress, COMMUNITY);
            if (dataSet != null) {
                return dataSet.getDataTable(getComInterface());
            }
        } catch (Exception e) {
            Messenger.append("Exception caused by " + this + "\n" + e + "\n");
            cancelComInterface();
        }
        return null;
    }

    public String[][] getSNMPDataSet(DataInterface set) {
        try {
            if (set != null) {
                return set.getDataTable(getComInterface());
            }
        } catch (Exception e) {
            Messenger.append("Exception caused by " + this + "\n" + e + "\n");
            cancelComInterface();
        }
        return null;
    }
    private String[][] cdpTable;

    public synchronized void setCDPTable(String[][] set) {
        cdpTable = set;
    }

    public synchronized String[][] getCDPTable() {
        return cdpTable;
    }

    /**
     * updates the permenant SNMP data for this node. If this data cannot be
     * retrieved successfully, the node is considered inactive.
     */
    public void refreshPermantSNMPData() {
        try {
            textMessage = new StringBuffer();

            stringID = getID(getComInterface());
            //ifMap = getInterfaces(getComInterface());

            DataInterface set = new CDPDataSet();
            //cdpTable = set.getDataTable(getComInterface());
            setCDPTable(set.getDataTable(getComInterface()));

            if (rstpRequired) {
                getRSTPView(getComInterface());
                getHSRPView(getComInterface());
                rstpReady = true;
            }

            //textMessage.append(stringID + "\n");
            active = true;

            state = ACTIVE_STATE;
            stateCounter = MAX_TRIALS;

            //Messenger.append("Connection to " + this + " was successful \n");

        } catch (Exception e) {
            active = false;

            stateCounter = Math.max(0, stateCounter - 1);
            state = (stateCounter == 0) ? INACTIVE_STATE : STANDBY_STATE;

            Messenger.append("Exception occured  while refreshing SNMP data for " + this + "\n" + e + "\n"
                    + "State changed to (" + stateCounter + ")\n");

        }
        if (!isReady()) { //(state != ACTIVE_STATE) {
            cancelComInterface();
        }
    }

    private void cancelComInterface() {
        try {
            getComInterface().closeConnection();
            comInterface = null;
        } catch (Exception e) {
        }
    }

    public void run() {
        refreshPermantSNMPData();
    }

    private String getID(SNMPv1CommunicationInterface comInterface)
            throws IOException, SocketException, SNMPBadValueException, SNMPGetException {

        SNMPVarBindList newVars = comInterface.getMIBEntry(idOID);

        SNMPSequence pair = (SNMPSequence) (newVars.getSNMPObjectAt(0));
        SNMPObject snmpValue = pair.getSNMPObjectAt(1);

        return snmpValue.toString();
    }

    private Map getInterfaces(SNMPv1CommunicationInterface comInterface)
            throws IOException, SocketException, SNMPBadValueException, SNMPGetException {

        SNMPVarBindList newVars = comInterface.retrieveMIBTable(ifOID);

        Map<String, String> map = new HashMap<String, String>();

        for (int j = 0; j < newVars.size(); j++) {
            SNMPSequence pair = (SNMPSequence) (newVars.getSNMPObjectAt(j));
            SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier) pair.getSNMPObjectAt(0);
            SNMPObject snmpValue = pair.getSNMPObjectAt(1);

            String snmpString = snmpOID.toString();
            String prefix = snmpString.substring(0, snmpString.lastIndexOf('.'));
            String ifIdex = prefix.substring(prefix.lastIndexOf('.') + 1, prefix.length());
            map.put(ifIdex, snmpValue.toString());
        }

        return map;
    }

    public String toString() {
        return ("Node: " + hostAddress);
    }
}