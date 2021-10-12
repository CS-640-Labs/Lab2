package edu.wisc.cs.sdn.vnet.sw;

import net.floodlightcontroller.packet.MACAddress;
import edu.wisc.cs.sdn.vnet.Iface;



public class TableEntry {
	private int age;
	private MACAddress macAddr;
	private Iface inIface;

	public TableEntry(MACAddress macAddr, Iface inIface) {
		this.age = 15;
		this.macAddr = macAddr;
		this.inIface = inIface;
	}

	public int getAge() {
		return age;
	}

	public void resetAge() {
		this.age = 15;
	}

	public MACAddress getMacAddr() {
		return macAddr;
	}

	public Iface getInIface() {
		return inIface;
	}
}
