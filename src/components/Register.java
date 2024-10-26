package components;

public class Register {
	
	private String registerName;
	
	private int flagBits[];
	private int numFlags;
	
	private int data;
	private Bus busExt, busInt1, busInt2;
	
	/**
	 * Default constructor
	 * @param extBus 
	 * @param intBus
	 */
	public Register(String name, Bus extBus, Bus intBus, int regId) {
		this.registerName = name;
		this.busExt = extBus;
		
		if(regId == 1)
			this.busInt1 = intBus;
		else 
			this.busInt2 = intBus;
	}
	
	/**
	 * Construct for a register with one bus connection 
	 * @param bus
	 * @param busId Number that identifies if its external bus (0), intBus1 (1), intBus2 (2)
	 */
	public Register(String name, Bus bus, int busId) {
		this.registerName = name;
		if(busId == 0)
			this.busExt= bus;
		else if(busId == 1)
			this.busInt1 = bus;
		else 
			this.busInt2 = bus;
	
	}
		
	public Register(String name, Bus busExt, Bus intBus1, Bus intBus2) {
		this.registerName = name;
		this.busExt = busExt;
		this.busInt1 = intBus1;
		this.busInt2 = intBus2;
	}
	
	public int getData() {
		return data;
	}
	
	/**
	 * This special constructor is used to make Flags register
	 * with special bits for special informations
	 * @param numberOfBits
	 * @param bus
	 */
	public Register(int numberOfBits, Bus bus) {
		super();
		this.registerName = "Flags";
		this.numFlags = numberOfBits;
		this.flagBits = new int[numFlags];
		for (int i=0;i<numFlags;i++) {
			flagBits[i] = 0;
		}
		this.busExt = bus;
	}
	
	public String getRegisterName() {
		return registerName;
	}

	/**
	 * This method allows the UC or the ULA to access any special bit
	 * @param pos
	 */
	public int getBit(int pos) {
		return flagBits[pos];
		
	}
	
	/**
	 * This method allows the UC or the ULA to set any special bit
	 * @param pos
	 */
	public void setBit(int pos, int bit) {
		flagBits[pos] = bit;
	}



	/**
	 * This method stores the data from the bus into this register
	 */
	public void store() {
		data = busExt.get();
	}
	
	
	/**
	 * This method reads the data from this register and stores it into the bus
	 */
	public void read() {
		busExt.put(data);
	}
	
	/**
	 * This method copies the data from this register to the internalbus1
	 */
	public void internalRead1() {
		busInt1.put(data);
	}
	/**
	 * This method copies the data from this register to the internalbus2
	 */
	public void internalRead2() {
		busInt2.put(data);
	}
	
	/**
	 * This method copies the data from the internalbus1 to this register
	 */
	public void internalStore1() {
		data = busInt1.get();
	}
	
	/**
	 * This method copies the data from the internalbus2 to this register
	 */
	public void internalStore2() {
		data = busInt2.get();
	}


}
