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

import java.io.File;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ReadXMLFile {

    public class InputParam {

        public String readString, netName;
        public String[] ipList, xCoord, yCoord, vlanList;

        public void print() {
            System.out.println("Network Name : " + netName);
            System.out.println("SNMP String : " + readString);
            for (int i = 0; i < vlanList.length; i++) {
                System.out.println("VLAN : " + vlanList[i]);
            }
            for (int i = 0; i < ipList.length; i++) {
                System.out.println("Node : " + ipList[i] + ", " + xCoord[i] + ", " + yCoord[i]);
            }
        }
    }

    public InputParam read(String filename) {
        InputParam param = new InputParam();

        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(filename));

            // normalize text representation
            doc.getDocumentElement().normalize();
            //System.out.println("Root element of the doc is " + doc.getDocumentElement().getNodeName());

            NodeList nameItems = doc.getElementsByTagName("netname");
            Element firstNameElement = (Element) nameItems.item(0);
            NodeList textNAMEList = firstNameElement.getChildNodes();
            param.netName = ((Node) textNAMEList.item(0)).getNodeValue().trim();
            //System.out.println("Network Name : " + ((Node) textNAMEList.item(0)).getNodeValue().trim());

            NodeList snmpItems = doc.getElementsByTagName("readstring");
            Element firstSNMPElement = (Element) snmpItems.item(0);
            NodeList textSNMPList = firstSNMPElement.getChildNodes();
            param.readString = ((Node) textSNMPList.item(0)).getNodeValue().trim();
            //System.out.println("Read String : " + ((Node) textSNMPList.item(0)).getNodeValue().trim());

            NodeList listOfVLANs = doc.getElementsByTagName("vlan");
            int totalVLANs = listOfVLANs.getLength();
            //System.out.println("Total no of vlans : " + totalVLANs);

            param.vlanList = new String[totalVLANs];
            for (int s = 0; s < listOfVLANs.getLength(); s++) {
                Node firstNode = listOfVLANs.item(s);
                if (firstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element nodeElement = (Element) firstNode;
                    //-------
                    NodeList idList = nodeElement.getElementsByTagName("id");
                    Element ipElement = (Element) idList.item(0);
                    NodeList textIDList = ipElement.getChildNodes();
                    param.vlanList[s] = ((Node) textIDList.item(0)).getNodeValue().trim();
                    //System.out.println("VLAN ID : " + ((Node) textIDList.item(0)).getNodeValue().trim());

                }//end of if clause

            }//end of for loop with s var

            NodeList listOfNodes = doc.getElementsByTagName("node");
            int totalNodes = listOfNodes.getLength();
            //System.out.println("Total no of nodes : " + totalNodes);

            param.ipList = new String[totalNodes];
            param.xCoord = new String[totalNodes];
            param.yCoord = new String[totalNodes];
            for (int s = 0; s < listOfNodes.getLength(); s++) {
                Node firstNode = listOfNodes.item(s);
                if (firstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element nodeElement = (Element) firstNode;
                    //-------
                    NodeList ipList = nodeElement.getElementsByTagName("ip");
                    Element ipElement = (Element) ipList.item(0);
                    NodeList textIPList = ipElement.getChildNodes();
                    param.ipList[s] = ((Node) textIPList.item(0)).getNodeValue().trim();
                    //System.out.println("IP Address : " + ((Node) textIPList.item(0)).getNodeValue().trim());
                    //-------
                    NodeList xElement = nodeElement.getElementsByTagName("x");
                    Element lastNameElement = (Element) xElement.item(0);
                    NodeList textXList = lastNameElement.getChildNodes();
                    param.xCoord[s] = ((Node) textXList.item(0)).getNodeValue().trim();
                    //System.out.println("X : " + ((Node) textXList.item(0)).getNodeValue().trim());
                    //-------
                    NodeList yElement = nodeElement.getElementsByTagName("y");
                    Element ageElement = (Element) yElement.item(0);
                    NodeList textYList = ageElement.getChildNodes();
                    param.yCoord[s] = ((Node) textYList.item(0)).getNodeValue().trim();
                    //System.out.println("Y : " + ((Node) textYList.item(0)).getNodeValue().trim());

                }//end of if clause

            }//end of for loop with s var

        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());

        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return param;

    }//end of main
}