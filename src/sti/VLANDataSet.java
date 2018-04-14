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

public class VLANDataSet implements DataInterface {

    protected String[][] OIDTable = new String[][]{{
            "1.3.6.1.4.1.9.9.46.1.3.1.1.18",
            "1.3.6.1.4.1.9.9.46.1.3.1.1.3",
            "1.3.6.1.4.1.9.9.46.1.3.1.1.4",
            //"1.3.6.1.4.1.9.9.46.1.3.1.1.17",
            "1.3.6.1.4.1.9.9.46.1.3.1.1.2"
        },
        {
            "Index", // (18)
            "Type", // (3)
            "Name", // (4)
            //"vtpVlanTypeExt",	// (17)
            "State" // (2)
        }
    };
    private String[] vlanState = new String[]{"Operational", "Suspended", "MTU Too Big For Device", "MTU Too Big For Trunk"}; // starts with 1
    private String[] vlanType = new String[]{"Ethernet", "FDDI", "Token Ring", "FDDI Net", "TR Net", "Deprecated"};			// starts w/ 1
    private String[] vlanTypeExt = new String[]{"VTP Manageable", "Internal", "Reserved", "RSPAN", "Dynamic Gvrp"};		// starts w/0

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
                table[j / cols][j % cols] = vlanType[Integer.parseInt(snmpValue.toString()) - 1];
            } else if (j % cols == 3) {
                table[j / cols][j % cols] = vlanState[Integer.parseInt(snmpValue.toString()) - 1];
                //} else if (j%cols == 3) {
                //	table[j/cols][j%cols] = ((SNMPOctetString)snmpValue).toHexString(); //vlanTypeExt[Integer.parseInt(snmpValue.toString())];
            } else {
                table[j / cols][j % cols] = snmpValue.toString();
            }

        }

        return table;
    }
}