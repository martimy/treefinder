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

import snmp.*;

public class STPDataSet implements DataInterface {

    protected String[][] OIDTable = new String[][]{{
            "1.3.6.1.2.1.17.2.15.1.1",
            "1.3.6.1.2.1.17.2.15.1.2",
            "1.3.6.1.2.1.17.2.15.1.3",
            "1.3.6.1.2.1.17.2.15.1.4",
            "1.3.6.1.2.1.17.2.15.1.5",
            "1.3.6.1.2.1.17.2.15.1.6",
            "1.3.6.1.2.1.17.2.15.1.7",
            "1.3.6.1.2.1.17.2.15.1.8",
            "1.3.6.1.2.1.17.2.15.1.9",
            "1.3.6.1.2.1.17.2.15.1.10", //"1.3.6.1.2.1.17.2.15.1.11"
        },
        {
            /*dot1dStp*/"Port", //(1)
            "Priority", //(2)
            "State", //(3)
            "Enable", //(4)
            "Path Cost", //(5)
            "Designated Root", //(6)
            "Designated Cost", //(7)
            "Designated Bridge", //(8)
            "Designated Port", //(9)
            "Forward Transitions", //(10)
        //"dot1dStpPortPathCost32" 			//(11)
        }
    };
    private String[] portState = new String[]{"Disabled", "Blocking", "Listening", "Learning", "Forwarding", "Broken"};
    private String[] portEnable = new String[]{"Enabled", "Disabled"};

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

            if (j % cols == 2) {
                table[j / cols][j % cols] = portState[Integer.parseInt(snmpValue.toString()) - 1];
            } else if (j % cols == 3) {
                table[j / cols][j % cols] = portEnable[Integer.parseInt(snmpValue.toString()) - 1];
            } else if (j % cols == 5 || j % cols == 7 || j % cols == 8) {
                table[j / cols][j % cols] = ((SNMPOctetString) snmpValue).toHexString();
            } else {
                table[j / cols][j % cols] = snmpValue.toString();
            }

        }

        return table;
    }
}
