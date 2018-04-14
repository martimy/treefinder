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

public class SysDataSet implements DataInterface {

    protected String[][] OIDEntry = new String[][]{{
            "1.3.6.1.2.1.1.5.0",
            "1.3.6.1.2.1.1.6.0",
            "1.3.6.1.2.1.1.4.0",
            "1.3.6.1.2.1.1.3.0",
            //"1.3.6.1.2.1.1.2.0",
            "1.3.6.1.2.1.1.1.0",
            "1.3.6.1.2.1.1.7.0",
            "1.3.6.1.2.1.2.1.0"
        },
        {"SysName",
            "Location",
            "Contact",
            "Up Time",
            //"SysObjectID",
            "Descr",
            "Services",
            "If Number",},};

    public String[] getDataHeader() {
        return new String[]{"Item", "Value"};
    }

    public String[][] getDataTable(SNMPv1CommunicationInterface comInterface) throws java.io.IOException, snmp.SNMPBadValueException, snmp.SNMPGetException {

        SNMPVarBindList newVars = comInterface.getMIBEntry(OIDEntry[0]);

        String[][] table = new String[newVars.size()][2];

        for (int j = 0; j < newVars.size(); j++) {
            SNMPSequence pair = (SNMPSequence) (newVars.getSNMPObjectAt(j));
            SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier) pair.getSNMPObjectAt(0);
            SNMPObject snmpValue = pair.getSNMPObjectAt(1);

            String typeString = snmpValue.getClass().getName();
            if (typeString.equals("snmp.SNMPOctetString")) {
                String snmpString = snmpValue.toString();

                // truncate at first null character
                int nullLocation = snmpString.indexOf('\0');
                if (nullLocation == -1) { // printable characters
                    table[j][0] = OIDEntry[1][j];
                    table[j][1] = snmpString;
                } else {
                    table[j][0] = OIDEntry[1][j];
                    table[j][1] = ((SNMPOctetString) snmpValue).toHexString();
                }
            } else if (typeString.equals("snmp.SNMPTimeTicks")) {
                table[j][0] = OIDEntry[1][j];
                table[j][1] = ((SNMPTimeTicks) snmpValue).toTimeString();
            } else {
                table[j][0] = OIDEntry[1][j];
                table[j][1] = snmpValue.toString();
            }

        }

        return table;

    }
}