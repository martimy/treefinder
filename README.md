# Tree Finder

The Tree Finder is an application that monitor the status of the Rapid/Spanning Tree Protocol in a Layer 2 switched network using Simple Network Management Protocol (SNMP). The application is designed specifically for Cisco networks that use Per-VLAN spanning trees, and it can monitor and display the status of the links that participate in the R/STP per individual VLAN. The Tree Finder also provides other information about the network. A network administrator can use this application to visualize the forwarding and blocking links in the network, which makes a great tool for troubleshooting.  

## Getting Started 

To get a copy of the Tree Finder, you need to clone the source code from GitHub and compile it in your machine. To run the program, you need to define your network using an XML file. 

### Installation

To get a copy of the Tree Finder, you need to clone the source code from GitHub and compile the code in your machine using your favorite IDE.

```
$ git clone https://github.com/martimy/treefinder
```

### Define the Network

Before using the Tree Finder, you need to create an XML file that defines your network. The files also includes the X,Y coordinates of the switch nodes in the network view. Here is an example:

```
<?xml version="1.0"?>
<network>
<netname>Test Network</netname>
<readstring>readcom</readstring>
<vlan><id>VLAN 1</id></vlan>
<vlan><id>VLAN 100</id></vlan>
<vlan><id>VLAN 110</id></vlan>
<vlan><id>VLAN 120</id></vlan>
<vlan><id>VLAN 150</id></vlan>
<vlan><id>VLAN 200</id></vlan>
<node><ip>192.168.64.2 </ip><x> 700</x><y>500</y></node>
<node><ip>192.168.64.3 </ip><x> 500</x><y>300</y></node>
<node><ip>192.168.64.4</ip><x> 500</x><y>500</y></node>
<node><ip>192.168.64.5</ip><x> 400</x><y>600</y></node>
<node><ip>192.168.64.7</ip><x>300</x><y>500</y></node>
</network>
```

### Run the app

Before running the app, make sure that:
* All switches are configured with a management IP Address 
* Your machine can ping all switches in the network
* SNMP is enabled in all switches and the read-only community string matches your file

Finally, run the application:

```
$ java -jar STInqV1.jar testnet.xml
```

## Built With

* [SNMP Package](https://jsevy.com/snmp/) - by Jonathan Sevy.


## Authors

* **Maen Artimy** - [Profile](https://github.com/martimy)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
