package edu.wisc.cs.sdn.vnet.sw;

import net.floodlightcontroller.packet.Ethernet;
import edu.wisc.cs.sdn.vnet.Device;
import edu.wisc.cs.sdn.vnet.DumpFile;
import edu.wisc.cs.sdn.vnet.Iface;
import net.floodlightcontroller.packet.MACAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aaron Gember-Jacobson
 */
public class Switch extends Device
{
	private Map <MACAddress, TableEntry> forwardingTable = new HashMap<MACAddress, TableEntry>();

	/**
	 * Creates a router for a specific host.
	 * @param host hostname for the router
	 */
	public Switch(String host, DumpFile logfile)
	{
		super(host,logfile);
	}

	/**
	 * Handle an Ethernet packet received on a specific interface.
	 * @param etherPacket the Ethernet packet that was received
	 * @param inIface the interface on which the packet was received
	 */
	public void handlePacket(Ethernet etherPacket, Iface inIface)
	{
		System.out.println("*** -> Received packet: " +
                etherPacket.toString().replace("\n", "\n\t"));


		// get table entry
		TableEntry te = forwardingTable.get(etherPacket.getSourceMAC());

		// if timeout then remove table entry
		if(te != null && te.isTimeout()) {
			forwardingTable.remove(etherPacket.getSourceMAC());
		}

		// if destination mac still in table
		if(te != null) {
				this.sendPacket(etherPacket, te.getInIface());
				te.resetAge();
		}
		else {
			// add to table
			forwardingTable.put(etherPacket.getSourceMAC(), new TableEntry(inIface));


			// broadcast
			for (Map.Entry<String, Iface> entry : this.interfaces.entrySet()) {
				if(entry.getValue() != inIface) {
					this.sendPacket(etherPacket, entry.getValue());
				}
			}
		}
	}
}
