package edu.wisc.cs.sdn.vnet.sw;

import edu.wisc.cs.sdn.vnet.Iface;
import java.time.Instant;


public class TableEntry {
	private long age;
	private Iface inIface;

	public TableEntry(Iface inIface) {
		this.age = Instant.now().getEpochSecond() + 15;
		this.inIface = inIface;
	}

	public void resetAge() {
		this.age = Instant.now().getEpochSecond() + 15;
	}

	public boolean isTimeout() {
		System.out.println(Instant.now().getEpochSecond());
		System.out.println(this.age);
		return Instant.now().getEpochSecond() > this.age;
	}

	public Iface getInIface() {
		return inIface;
	}
}
