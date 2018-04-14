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
import snmp.*;

public class RSTPDataSet implements DataInterface {

    protected String[][] OIDTable = new String[][]{{
            //"1.3.6.1.4.1.9.9.82.1.2.2.1.2",
            "1.3.6.1.4.1.9.9.82.1.12.2.1.3"
        },
        {
            //"stpxPVSTVlanEnable",
            "stpxRSTPPortRoleValue " //(3) 1 : disabled 2 : root 3 : designated 4 : alternate 5 : backUp 6 : boundary 7 : master
        }
    };
    //protected final String vlanTypeOID = "1.3.6.1.4.1.9.9.46.1.3.1.1.3";
    protected final String ifOID = "1.3.6.1.2.1.2.2.1.2";
    protected final String ifPort = "1.3.6.1.2.1.17.1.4.1.2";
    String[] portRole = {"Disabled", "Root", "Designated", "Alternate", "Backup", "Boundary", "Master"};
    String[] enable = {"Enabled", "Disabled", "Not Applicable"};

    public String[] getDataHeader() {
        return header; //OIDTable[1];
    }
    String[] header = null;

    private int indexOf(String[] arr, String s) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(s)) {
                return i;
            }
        }
        return -1;
    }

    private int indexOf(String[][] arr, String s) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i][0].equals(s)) {
                return i;
            }
        }
        return -1;
    }

    public String[][] getDataTable(SNMPv1CommunicationInterface comInterface)
            throws java.io.IOException, snmp.SNMPBadValueException, snmp.SNMPGetException {

        int numVlans = 0;

        Set<String> vlanSet = new TreeSet<String>();
        Set<String> portSet = new TreeSet<String>();

        SNMPVarBindList newVars = comInterface.retrieveMIBTable(OIDTable[0]);

        // read all VLAN and port number and save then in two sets to be able to find the number of both
        for (int j = 0; j < newVars.size(); j++) {
            SNMPSequence pair = (SNMPSequence) (newVars.getSNMPObjectAt(j));
            SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier) pair.getSNMPObjectAt(0);
            SNMPObject snmpValue = pair.getSNMPObjectAt(1);

            String snmpString = snmpOID.toString();
            String prefix = snmpString.substring(0, snmpString.lastIndexOf('.'));
            String vlan = prefix.substring(prefix.lastIndexOf('.') + 1, prefix.length());
            String port = snmpString.substring(snmpString.lastIndexOf('.') + 1, snmpString.length());

            vlanSet.add(vlan);
            portSet.add(port);
        }

        // create the table header from the port set and the 1st column of the table from the VLAN set
        header = new String[portSet.size() + 1];
        String[][] table = new String[vlanSet.size()][portSet.size() + 1];

        String[] pa = portSet.toArray(new String[0]);
        String[] va = vlanSet.toArray(new String[0]);

        header[0] = "VLAN / Port";
        System.arraycopy(pa, 0, header, 1, pa.length);
        for (int i = 0; i < va.length; i++) {
            table[i][0] = va[i];
        }

        // fill the table
        for (int j = 0; j < newVars.size(); j++) {
            SNMPSequence pair = (SNMPSequence) (newVars.getSNMPObjectAt(j));
            SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier) pair.getSNMPObjectAt(0);
            SNMPObject snmpValue = pair.getSNMPObjectAt(1);

            String snmpString = snmpOID.toString();
            String prefix = snmpString.substring(0, snmpString.lastIndexOf('.'));
            String vlan = prefix.substring(prefix.lastIndexOf('.') + 1, prefix.length());
            String port = snmpString.substring(snmpString.lastIndexOf('.') + 1, snmpString.length());

            int row = indexOf(table, vlan);
            int col = indexOf(header, port);

            table[row][col] = portRole[Integer.parseInt(snmpValue.toString()) - 1];
        }


        // map port number to interface name
        newVars = comInterface.retrieveMIBTable(ifOID);
        Map<String, String> ifMap = new HashMap<String, String>();

        for (int j = 0; j < newVars.size(); j++) {
            SNMPSequence pair = (SNMPSequence) (newVars.getSNMPObjectAt(j));
            SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier) pair.getSNMPObjectAt(0);
            SNMPObject snmpValue = pair.getSNMPObjectAt(1);

            String snmpString = snmpOID.toString();
            String ifIdex = snmpString.substring(snmpString.lastIndexOf('.') + 1, snmpString.length());
            ifMap.put(ifIdex, snmpValue.toString());

        }

        newVars = comInterface.retrieveMIBTable(ifPort);
        Map<String, String> portMap = new HashMap<String, String>();

        for (int j = 0; j < newVars.size(); j++) {
            SNMPSequence pair = (SNMPSequence) (newVars.getSNMPObjectAt(j));
            SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier) pair.getSNMPObjectAt(0);
            SNMPObject snmpValue = pair.getSNMPObjectAt(1);

            String snmpString = snmpOID.toString();
            String ifIdex = snmpString.substring(snmpString.lastIndexOf('.') + 1, snmpString.length());
            portMap.put(ifIdex, snmpValue.toString());

        }

        for (int i = 1; i < header.length; i++) {
            String s = ifMap.get(portMap.get(header[i]));
            header[i] = s == null ? header[i] : s;
        }

        return table;
    }
}