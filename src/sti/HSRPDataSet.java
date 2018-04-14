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

public class HSRPDataSet implements DataInterface{
    
    protected String[][] OIDTable = new String[][]{	{
        //"1.3.6.1.4.1.9.9.106.1.2.1.1.1",
        "1.3.6.1.4.1.9.9.106.1.2.1.1.2",
                "1.3.6.1.4.1.9.9.106.1.2.1.1.3",
                "1.3.6.1.4.1.9.9.106.1.2.1.1.4",
                "1.3.6.1.4.1.9.9.106.1.2.1.1.5",
                "1.3.6.1.4.1.9.9.106.1.2.1.1.11",
                //"1.3.6.1.4.1.9.9.106.1.2.1.1.12",
                "1.3.6.1.4.1.9.9.106.1.2.1.1.13",
                "1.3.6.1.4.1.9.9.106.1.2.1.1.14",
                "1.3.6.1.4.1.9.9.106.1.2.1.1.15",
                "1.3.6.1.4.1.9.9.106.1.2.1.1.16"
    },
    {	//"cHsrpGrpNumber",
        "VLAN",						// (2)
                "Priority",					// (3)
                "Preempt",
                "PreemptDelay",
                "VirtualIpAddr",			// (11)
                //"UseConfigVirtualIpAddr",
                "ActiveRouter",
                "StandbyRouter",
                "StandbyState",
                "VirtualMacAddr"
    }
    };
    
    String[] preempt = new String[]{"True", "False"};
    String[] standbyState = new String[]{"Initial", "Learn", "Listen", "Speak", "Standby", "Active"};
    
    public String[] getDataHeader() {
        return OIDTable[1];
    }
    
    public String[][] getDataTable(SNMPv1CommunicationInterface comInterface)
    throws java.io.IOException, snmp.SNMPBadValueException, snmp.SNMPGetException{
        
        SNMPVarBindList newVars = comInterface.retrieveMIBTable(OIDTable[0]);
        
        //int size = newVars.size();
        int cols = OIDTable[0].length;
        String[][] table = new String[newVars.size()/cols][cols];
        
        for (int j = 0; j < newVars.size(); j++) {
            SNMPSequence pair = (SNMPSequence)(newVars.getSNMPObjectAt(j));
            SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
            SNMPObject snmpValue = pair.getSNMPObjectAt(1);
            
                        /*String typeString = snmpValue.getClass().getName();
                        if (typeString.equals("snmp.SNMPOctetString")) {
                                String snmpString = snmpValue.toString();
                         
                                // truncate at first null character
                                int nullLocation = snmpString.indexOf('\0');
                                if (nullLocation == -1) { // printable characters
                                        table[j/cols][j%cols] = snmpString;
                                } else {
                                        table[j/cols][j%cols] = ((SNMPOctetString)snmpValue).toHexString();
                                }
                         
                        } else {
                                        table[j/cols][j%cols] = snmpValue.toString();
                        }*/
            
            if (j%cols == 0) {
                String snmpString = snmpOID.toString();
                String prefix = snmpString.substring(0, snmpString.lastIndexOf('.'));
                String ifidex = prefix.substring(prefix.lastIndexOf('.')+1, prefix.length());
                //String port = snmpString.substring(snmpString.lastIndexOf('.')+1, snmpString.length());
                table[j/cols][j%cols] = ifidex;
            } else if (j%cols == 2) {
                table[j/cols][j%cols] = preempt[Integer.parseInt(snmpValue.toString()) - 1];
            } else if (j%cols == 7) {
                table[j/cols][j%cols] = standbyState[Integer.parseInt(snmpValue.toString()) - 1];
            } else if (j%cols == 8) {
                table[j/cols][j%cols] = ((SNMPOctetString)snmpValue).toHexString();
            } else {
                table[j/cols][j%cols] = snmpValue.toString();
            }
            
            
        }
        
        return table;
    }
    
    private int status; // 0: listining, 1: standby, 2: active
    public int getNodeStatus() {
        return status;
    }
}