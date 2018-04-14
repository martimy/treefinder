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
//import javax.swing.*;
import snmp.*;

public class CDPDataSet implements DataInterface {

    public static int LocalPortCol = 0;
    public static int DeviceIDCol = 2;
    public static int DevicePortCol = 3;
    protected final String[][] OIDTable = new String[][]{{
            "1.3.6.1.4.1.9.9.23.1.2.1.1.3", // 1= IP address
            "1.3.6.1.4.1.9.9.23.1.2.1.1.4",
            //"1.3.6.1.4.1.9.9.23.1.2.1.1.5",
            "1.3.6.1.4.1.9.9.23.1.2.1.1.6",
            "1.3.6.1.4.1.9.9.23.1.2.1.1.7",
            //"1.3.6.1.4.1.9.9.23.1.2.1.1.8",
            //"1.3.6.1.4.1.9.9.23.1.2.1.1.9",
            //"1.3.6.1.4.1.9.9.23.1.2.1.1.11",
            "1.3.6.1.4.1.9.9.23.1.2.1.1.12"
        },
        {
            "LocalPort", //(3)
            "Address", //(4)
            //"cdpCacheVersion",						//(5)
            "Device ID", //(6)
            "Device Port", //(7)
            //"cdpCachePlatform",						//(8)
            //"cdpCacheCapabilities",					//(9)
            //"cdpCacheNativeVLAN",						//(11)
            "Duplex" //(12)  //  unknown(1),  halfduplex(2),  fullduplex(3)
        }
    };
    protected final String ifOID = "1.3.6.1.2.1.2.2.1.2";
    private String[] portDuplex = new String[]{"Unknown", "Half Duplex", "Full Duplex"};

    public String[] getDataHeader() {
        return OIDTable[1];
    }

    public String[][] getDataTable(SNMPv1CommunicationInterface comInterface)
            throws java.io.IOException, snmp.SNMPBadValueException, snmp.SNMPGetException {

        SNMPVarBindList newVars = comInterface.retrieveMIBTable(OIDTable[0]);

        //int size = newVars.size();
        int cols = OIDTable[0].length;
        String[][] table = new String[newVars.size() / cols][cols];

        for (int j = 0; j < newVars.size(); j++) {
            SNMPSequence pair = (SNMPSequence) (newVars.getSNMPObjectAt(j));
            SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier) pair.getSNMPObjectAt(0);
            SNMPObject snmpValue = pair.getSNMPObjectAt(1);

            if (j % cols == 1) {
                try {
                    table[j / cols][j % cols] = ((SNMPOctetString) snmpValue).toIPString();
                } catch (SNMPBadValueException e) {
                    table[j / cols][j % cols] = ((SNMPOctetString) snmpValue).toString();
                }
            } else if (j % cols == 4) {
                table[j / cols][j % cols] = portDuplex[Integer.parseInt(snmpValue.toString()) - 1];
            } else if (j % cols == 0) {
                String snmpString = snmpOID.toString();
                String prefix = snmpString.substring(0, snmpString.lastIndexOf('.'));
                String ifidex = prefix.substring(prefix.lastIndexOf('.') + 1, prefix.length());
                //String port = snmpString.substring(snmpString.lastIndexOf('.')+1, snmpString.length());
                table[j / cols][j % cols] = ifidex;
            } else {
                table[j / cols][j % cols] = snmpValue.toString();
            }
        }


        newVars = comInterface.retrieveMIBTable(ifOID);
        Map<String, String> map = new HashMap<String, String>();

        for (int j = 0; j < newVars.size(); j++) {
            SNMPSequence pair = (SNMPSequence) (newVars.getSNMPObjectAt(j));
            SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier) pair.getSNMPObjectAt(0);
            SNMPObject snmpValue = pair.getSNMPObjectAt(1);

            String snmpString = snmpOID.toString();
            String ifIdex = snmpString.substring(snmpString.lastIndexOf('.') + 1, snmpString.length());
            map.put(ifIdex, snmpValue.toString());

        }

        for (int i = 0; i < table.length; i++) {
            table[i][0] = map.get(table[i][0]);
        }

        return table;
    }
}