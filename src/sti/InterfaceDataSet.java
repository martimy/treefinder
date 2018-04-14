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

public class InterfaceDataSet implements DataInterface {

    protected String[][] OIDTable = new String[][]{{
            "1.3.6.1.2.1.2.2.1.1",
            "1.3.6.1.2.1.2.2.1.2",
            "1.3.6.1.2.1.2.2.1.3",
            //"1.3.6.1.2.1.2.2.1.4",	//does not work with 3750 for some reason
            "1.3.6.1.2.1.2.2.1.5",
            "1.3.6.1.2.1.2.2.1.6",
            "1.3.6.1.2.1.2.2.1.7",
            "1.3.6.1.2.1.2.2.1.8"
        },
        {"Index", // (1)
            "Descr", // (2)
            "Type", // (3)
            //"ifMtu",		// (4)
            "Speed", // (5)
            "Phys Address", // (6)
            "Admin Status", // (7)
            "Oper Status" // (8)
        }
    };
    private String[] ifState = new String[]{"UP", "DOWN"}; // starts with 1

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
                switch (Integer.parseInt(snmpValue.toString())) {
                    case 1:
                        table[j / cols][j % cols] = "Other";
                        break;
                    case 6:
                        table[j / cols][j % cols] = "Ethernet Csmacd";
                        break;
                    case 53:
                        table[j / cols][j % cols] = "Prop Virtual";
                        break;
                    default:
                        table[j / cols][j % cols] = snmpValue.toString();
                }
            } else if (j % cols == 4) {
                table[j / cols][j % cols] = ((SNMPOctetString) snmpValue).toHexString();
            } else if (j % cols == 5 || j % cols == 6) {
                table[j / cols][j % cols] = ifState[Integer.parseInt(snmpValue.toString()) - 1];
            } else {
                table[j / cols][j % cols] = snmpValue.toString();
            }

        }

        return table;
    }
}