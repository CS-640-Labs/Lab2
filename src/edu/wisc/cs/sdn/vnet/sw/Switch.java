package edu.wisc.cs.sdn.vnet.sw;

import net.floodlightcontroller.packet.Ethernet;
import edu.wisc.cs.sdn.vnet.Device;
import edu.wisc.cs.sdn.vnet.DumpFile;
import edu.wisc.cs.sdn.vnet.Iface;
import java.util.Map;

import edu.wisc.cs.sdn.vnet.sw.TableEntry;

/**
 * @author Aaron Gember-Jacobson
 */
public class Switch extends Device
{

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

		//		int c = etherPacket.getSourceMAC();
		//		etherPacket.getDestinationMAC();

		for (Map.Entry<String, Iface> entry : this.interfaces.entrySet()) {
			this.sendPacket(etherPacket, entry.getValue());
		}

		// update table




	}
}
