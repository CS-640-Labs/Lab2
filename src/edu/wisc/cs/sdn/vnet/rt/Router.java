package edu.wisc.cs.sdn.vnet.rt;

import edu.wisc.cs.sdn.vnet.Device;
import edu.wisc.cs.sdn.vnet.DumpFile;
import edu.wisc.cs.sdn.vnet.Iface;

import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.MACAddress;

import java.util.Map;

/**
 * @author Aaron Gember-Jacobson and Anubhavnidhi Abhashkumar
 */
public class Router extends Device
{	
	/** Routing table for the router */
	private RouteTable routeTable;
	
	/** ARP cache for the router */
	private ArpCache arpCache;
	
	/**
	 * Creates a router for a specific host.
	 * @param host hostname for the router
	 */
	public Router(String host, DumpFile logfile)
	{
		super(host,logfile);
		this.routeTable = new RouteTable();
		this.arpCache = new ArpCache();
	}
	
	/**
	 * @return routing table for the router
	 */
	public RouteTable getRouteTable()
	{ return this.routeTable; }
	
	/**
	 * Load a new routing table from a file.
	 * @param routeTableFile the name of the file containing the routing table
	 */
	public void loadRouteTable(String routeTableFile)
	{
		if (!routeTable.load(routeTableFile, this))
		{
			System.err.println("Error setting up routing table from file "
					+ routeTableFile);
			System.exit(1);
		}
		
		System.out.println("Loaded static route table");
		System.out.println("-------------------------------------------------");
		System.out.print(this.routeTable.toString());
		System.out.println("-------------------------------------------------");
	}
	
	/**
	 * Load a new ARP cache from a file.
	 * @param arpCacheFile the name of the file containing the ARP cache
	 */
	public void loadArpCache(String arpCacheFile)
	{
		if (!arpCache.load(arpCacheFile))
		{
			System.err.println("Error setting up ARP cache from file "
					+ arpCacheFile);
			System.exit(1);
		}
		
		System.out.println("Loaded static ARP cache");
		System.out.println("----------------------------------");
		System.out.print(this.arpCache.toString());
		System.out.println("----------------------------------");
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


		// check if ipv4
		if(Ethernet.etherTypeClassMap.get(etherPacket.getEtherType()) == IPv4.class) {
			// check checksum is good and no error
			IPv4 packet = ((IPv4)etherPacket.getPayload());

			short oldChecksum = packet.getChecksum();

			packet.resetChecksum();
			packet.serialize();

			if(oldChecksum - packet.getChecksum() == 0) {
				packet.setTtl((byte) (packet.getTtl() - ((byte) 1)));
				// if ttl is zero then drop otherwise continue
				if(packet.getTtl() != 0) {
					// for each interface (port) of the router
					// if IP matches destination of packet then drop
					boolean drop = false;
					for (Map.Entry<String, Iface> entry : this.getInterfaces().entrySet()) {
						// drop if equal
						if(entry.getValue().getIpAddress() == packet.getDestinationAddress()) {
							drop = true;
							break;
						}
					}
					// if dest not one of iface ips
					if(!drop) {
						RouteEntry matchingEntry = this.routeTable.lookup(packet.getDestinationAddress());
						// if not matching entry the drop

						if(matchingEntry != null) {
							System.out.println(matchingEntry.toString());
							// get mac address of arpCache next-hop
							System.out.println(IPv4.fromIPv4Address(matchingEntry.getDestinationAddress()));
							MACAddress macAddr = arpCache.lookup(matchingEntry.getDestinationAddress()).getMac();
							System.out.println(macAddr.toString());
							// set next hop mac-addr as packet new destination
							etherPacket.setDestinationMACAddress(macAddr.toBytes());

							// set packet sourced as port/interface mac-addr
							etherPacket.setSourceMACAddress(inIface.getMacAddress().toBytes());

							// send packet out
							sendPacket(etherPacket, inIface);
						}
					}
				}
			}
		}
	}
}
