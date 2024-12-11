
package architecture;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import components.Bus;
import components.Demux;
import components.Memory;
import components.Register;
import components.Ula;

public class Architecture {
	
	private boolean simulation; //this boolean indicates if the execution is done in simulation mode.
								//simulation mode shows the components' status after each instruction
	
	private boolean halt;
	private Bus extbus1;
	private Bus intbus1;
	private Bus intbus2;
	private Memory memory;
	private Memory statusMemory;
	private int memorySize;
	private Register PC;
	private Register IR;
	private Register RPG0;
	private Register RPG1;
	private Register RPG2;
	private Register RPG3;
	private Register stkTop;
	private Register stkBot;
	private Register Flags;
	private Ula ula;
	private Demux demux; //only for multiple register purposes
	
	private ArrayList<String> commandsList;
	private ArrayList<Register> registersList;
	

	/**
	 * Instanciates all components in this architecture
	 */
	private void componentsInstances() {
		//don't forget the instantiation order
		//buses -> registers -> ula -> memory
		extbus1 = new Bus();
		intbus1 = new Bus();
		intbus2 = new Bus();
		PC = new Register("PC", extbus1, intbus2);
		IR = new Register("IR", extbus1, intbus2);
		RPG0 = new Register("RPG0", intbus2, intbus1);
		RPG1 = new Register ("RPG1", intbus2, intbus1);
		RPG2 = new Register("RPG2", intbus2, intbus1);
		RPG3 = new Register ("RPG3", intbus2, intbus1);
		stkTop = new Register("stkTop", extbus1, null);
		stkBot = new Register("stkBot", extbus1, null);
		Flags = new Register(2, intbus2);
		fillRegistersList();
		ula = new Ula(intbus1, intbus2);
		statusMemory = new Memory(2, extbus1);
		memorySize = 401;
		memory = new Memory(memorySize, extbus1);
		demux = new Demux(); // this bus is used only for multiple register operations
		
		fillCommandsList();
	}
	
	/**
	 * This method fills the registers list inserting into them all the registers we have.
	 * IMPORTANT!
	 * The first register to be inserted must be the default RPG
	 */
	private void fillRegistersList() {
		registersList = new ArrayList<Register>();
		registersList.add(RPG0);
		registersList.add(RPG1);
		registersList.add(RPG2);
		registersList.add(RPG3);
		registersList.add(stkTop);
		registersList.add(stkBot);
		registersList.add(PC);
		registersList.add(IR);
		registersList.add(Flags);
	}

	/**
	 * Constructor that instanciates all components according the architecture diagram
	 */
	public Architecture() {
		componentsInstances();
		
		//by default, the execution method is never simulation mode
		simulation = false;
	}

	
	public Architecture(boolean sim) {
		componentsInstances();
		
		//in this constructor we can set the simoualtion mode on or off
		simulation = sim;
	}

	//Inicializa os Registradores de Pilha
	public void setStack(int position){
		this.extbus1.put(position); //Coloca em bus a primeira posição livre na memória
		this.stkTop.store(); //Inicializa o topo da pilha
		this.stkBot.store(); //Inicializa o fim da pilha
		this.extbus1.put(0); //Limpa o barramento
	}
	//getters
	protected Bus getExtbus1() {
		return extbus1;
	}

	protected Bus getIntbus1() {
		return intbus1;
	}

	protected Bus getIntbus2() {
		return intbus2;
	}

	protected Memory getMemory() {
		return memory;
	}

	protected Register getPC() {
		return PC;
	}

	protected Register getIR() {
		return IR;
	}

	protected Register getRPG0() {
		return RPG0;
	}
	
	protected Register getRPG1() {
		return RPG1;
	}
	
	protected Register getRPG2() {
		return RPG2;
	}
	
	protected Register getRPG3() {
		return RPG3;
	}
	
	protected Register getStkTop() {
		return stkTop;
	}
	
	protected Register getStkBot() {
		return stkBot;
	}

	protected Register getFlags() {
		return Flags;
	}

	protected Ula getUla() {
		return ula;
	}

	public ArrayList<String> getCommandsList() {
		return commandsList;
	}

	//all the microprograms must be impemented here
	//the instructions table is
	/*
	 *
			add addr (rpg <- rpg + addr)
			sub addr (rpg <- rpg - addr)
			jmp addr (pc <- addr)
			jz addr  (se bitZero pc <- addr)
			jn addr  (se bitneg pc <- addr)
			read addr (rpg <- addr)
			store addr  (addr <- rpg)
			ldi x    (rpg <- x. x must be an integer)
			inc    (rpg++)
			move regA regB (regA <- regB)
	 */
	
	/**
	 * This method fills the commands list arraylist with all commands used in this architecture
	 */
	protected void fillCommandsList() {
		commandsList = new ArrayList<String>();
		//commandsList.add("add");            
		commandsList.add("addRegReg");   //0
		commandsList.add("addMemReg");   //1
		commandsList.add("addRegMem");   //2
		commandsList.add("addImmReg");   //3
		//commandsList.add("sub");            
		commandsList.add("subRegReg");   //4
		commandsList.add("subMemReg");   //5
		commandsList.add("subRegMem");   //6
		commandsList.add("subImmReg");   //7
		commandsList.add("imulMemReg");  //8
		commandsList.add("imulRegMem");  //9
		commandsList.add("imulRegReg");  //10
		commandsList.add("moveMemReg");  //11
		commandsList.add("moveRegMem");  //12
		commandsList.add("moveRegReg");  //13
		commandsList.add("moveImmReg");  //14
		//commandsList.add("inc");  	       
		commandsList.add("incReg");	   	   //15
		commandsList.add("jmp");         //16
		commandsList.add("jn");          //17
		commandsList.add("jz");          //18
		commandsList.add("jeq");         //19
		commandsList.add("jneq");        //20
		commandsList.add("jgt");         //21
		commandsList.add("jlw");         //22
		commandsList.add("read");        //23
		commandsList.add("store");       //24
		commandsList.add("ldi");         //25
	}

	
	/**
	 * This method is used after some ULA operations, setting the flags bits according the result.
	 * @param result is the result of the operation
	 * NOT TESTED!!!!!!!
	 */
	private void setStatusFlags(int result) {
		Flags.setBit(0, 0);
		Flags.setBit(1, 0);
		if (result==0) { //bit 0 in flags must be 1 in this case
			Flags.setBit(0,1);
		}
		if (result<0) { //bit 1 in flags must be 1 in this case
			Flags.setBit(1,1);
		}
	}

	/**
	 * This method implements the microprogram for
	 * 					ADD address
	 * In the machine language this command number is 0, and the address is in the position next to him
	 *    
	 * where address is a valid position in this memory architecture
	 * The method reads the value from memory (position address) and 
	 * performs an add with this value and that one stored in the RPG (the first register in the register list).
	 * The final result must be in RPG (the first register in the register list).
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <-  intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. rpg -> intbus1 //rpg.read() the current rpg value must go to the ula 
	 * 7. ula <- intbus1 //ula.store()
	 * 8. pc -> extbus (pc.read())
	 * 9. memory reads from extbus //this forces memory to write the data position in the extbus
	 * 10. memory reads from extbus //this forces memory to write the data value in the extbus
	 * 11. rpg <- extbus (rpg.store())
	 * 12. rpg -> intbus1 (rpg.read())
	 * 13. ula  <- intbus1 //ula.store()
	 * 14. Flags <- zero //the status flags are reset
	 * 15. ula adds
	 * 16. ula -> intbus1 //ula.read()
	 * 17. ChangeFlags //informations about flags are set according the result 
	 * 18. rpg <- intbus1 //rpg.store() - the add is complete.
	 * 19. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 20. ula <- intbus2 //ula.store()
	 * 21. ula incs
	 * 22. ula -> intbus2 //ula.read()
	 * 23. pc <- intbus2 //pc.store() 
	 * end
	 * @param address
	 */
	// public void add() {
	// 	PC.internalRead();
	// 	ula.internalStore(1);
	// 	ula.inc();
	// 	ula.internalRead(1);
	// 	PC.internalStore(); //now PC points to the parameter address
	// 	RPG0.internalRead();
	// 	ula.store(0); //the rpg value is in ULA (0). This is the first parameter
	// 	PC.read(); 
	// 	memory.read(); // the parameter is now in the external bus. 
	// 					//but the parameter is an address and we need the value
	// 	memory.read(); //now the value is in the external bus
	// 	IR.store();
	// 	IR.internalRead();
	// 	ula.internalStore(1); //the rpg value is in ULA (0). This is the second parameter 
	// 	ula.add(); //the result is in the second ula's internal register
	// 	ula.internalRead(1);; //the operation result is in the internalbus 2
	// 	setStatusFlags(intbus2.get()); //changing flags due the end of the operation
	// 	RPG0.internalStore(); //now the add is complete
	// 	PC.internalRead(); //we need to make PC points to the next instruction address
	// 	ula.internalStore(1);
	// 	ula.inc();
	// 	ula.internalRead(1);
	// 	PC.internalStore(); //now PC points to the next instruction. We go back to the FETCH status.
	// }
	
	//add %<regA> %<regB>    || RegB <- RegA + RegB
	public void addRegReg() {
		// Incrementa PC para o primeiro parâmetro
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);

	    PC.internalStore(); // PC aponta para regA

	    // Lê regA
	    PC.read();
	    memory.read();
	    demux.setValue(extbus1.get()); // Seleciona regA
	    registersInternalRead(); // Coloca o valor de regA no intbus1
	    ula.store(0); // Armazena regA na ULA (posição 0)

	    // Incrementa PC para o segundo parâmetro
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore(); // PC aponta para regB

	    // Lê regB
	    PC.read();
	    memory.read();
	    demux.setValue(extbus1.get()); // Seleciona regB
	    registersInternalRead(); // Coloca o valor de regB no intbus1
	    ula.store(1); // Armazena regB na ULA (posição 1)

	    // Realiza a soma
	    ula.add();
	    ula.read(1); // Resultado é colocado no intbus2
		setStatusFlags(intbus1.get());

	    // Atualiza o demux para apontar para regB (onde o resultado será armazenado)
	    demux.setValue(extbus1.get()); // Seleciona regB
	    registersInternalStore(); // Armazena o resultado em regB

	    // Incrementa PC para a próxima instrução
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	}

	public void addMemReg() {
        PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
		
		PC.read();
		memory.read();
		memory.read();
		IR.store(); //Valor da memoria agora esta em IR
		ula.inc();
		ula.internalRead(1); //Ula incrementa o valor de PC e escreve no bus2
		PC.internalStore();
		PC.read();
		memory.read(); //Devolve o ID do RPG
		demux.setValue(extbus1.get());
		registersInternalRead(); //O registrador coloca o dado no intbus1
		ula.store(1);
		IR.internalRead();
		ula.internalStore(0);
		ula.add();
		ula.read(1);
		setStatusFlags(intbus1.get());
		registersInternalStore();
		
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();		
	}

	//add %<regA> <mem>      || Memória[mem] <- RegA + memória[mem]
	public void addRegMem() {
		PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
		PC.internalStore();
	    
	    PC.read();
	    memory.read();
	    demux.setValue(extbus1.get());
	    registersInternalRead();
	    ula.store(0);
	    
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    memory.read();
	    IR.store();
	    IR.internalRead();
	    ula.internalStore(1);
	    
	    ula.add();
	    ula.internalRead(1);
		setStatusFlags(intbus2.get());
	    IR.internalStore();
	    PC.read();
	    memory.read();
	    memory.store();
	    IR.read();
	    memory.store();
	    
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	
	}

	public void addImmReg() {
        PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();

		PC.read();
		memory.read(); //Devolve o valor imediato
		IR.store(); //Armazena o valor imediato no IR
		ula.inc();
		ula.internalRead(1); //Ula incrementa o valor de PC e escreve no intbus2
		PC.internalStore();
		PC.read();
		memory.read(); //Devolve o ID do RPG
		demux.setValue(extbus1.get());
		registersInternalRead(); //O registrador coloca o dado no intbus1
		ula.store(1);
		IR.internalRead();
		ula.internalStore(0);
		ula.add();
		ula.read(1);
		setStatusFlags(intbus1.get());
		registersInternalStore();
		
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();		
	}

	/**
	 * This method implements the microprogram for
	 * 					SUB address
	 * In the machine language this command number is 1, and the address is in the position next to him
	 *    
	 * where address is a valid position in this memory architecture
	 * The method reads the value from memory (position address) and 
	 * performs an SUB with this value and that one stored in the rpg (the first register in the register list).
	 * The final result must be in RPG (the first register in the register list).
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <-  intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. rpg -> intbus1 //rpg.read() the current rpg value must go to the ula 
	 * 7. ula <- intbus1 //ula.store()
	 * 8. pc -> extbus (pc.read())
	 * 9. memory reads from extbus //this forces memory to write the data position in the extbus
	 * 10. memory reads from extbus //this forces memory to write the data value in the extbus
	 * 11. rpg <- extbus (rpg.store())
	 * 12. rpg -> intbus1 (rpg.read())
	 * 13. ula  <- intbus1 //ula.store()
	 * 14. Flags <- zero //the status flags are reset
	 * 15. ula subs
	 * 16. ula -> intbus1 //ula.read()
	 * 17. ChangeFlags //informations about flags are set according the result 
	 * 18. rpg <- intbus1 //rpg.store() - the add is complete.
	 * 19. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 20. ula <- intbus2 //ula.store()
	 * 21. ula incs
	 * 22. ula -> intbus2 //ula.read()
	 * 23. pc <- intbus2 //pc.store() 
	 * end
	 * @param address
	 */
	// public void sub() {
	// 	PC.internalRead();
	// 	ula.internalStore(1);
	// 	ula.inc();
	// 	ula.internalRead(1);
	// 	PC.internalStore(); //now PC points to the parameter address
	// 	RPG0.internalRead();
	// 	ula.store(0); //the rpg value is in ULA (0). This is the first parameter
	// 	PC.read(); 
	// 	memory.read(); // the parameter is now in the external bus. 
	// 					//but the parameter is an address and we need the value
	// 	memory.read(); //now the value is in the external bus
	// 	IR.store();
	// 	IR.internalRead();
	// 	ula.internalStore(1); //the rpg value is in ULA (0). This is the second parameter
	// 	ula.sub(); //the result is in the second ula's internal register
	// 	ula.internalRead(1); //the operation result is in the internalbus 2
	// 	setStatusFlags(intbus2.get()); //changing flags due the end of the operation
	// 	RPG0.internalStore(); //now the sub is complete
	// 	PC.internalRead(); //we need to make PC points to the next instruction address
	// 	ula.internalStore(1);
	// 	ula.inc();
	// 	ula.internalRead(1);
	// 	PC.internalStore(); //now PC points to the next instruction. We go back to the FETCH status.
	// }

	public void subRegReg() {
		
		// PC++
		PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();

		// Armazena RegA em ULA(0)
		PC.read();
		memory.read();
		demux.setValue(extbus1.get());
		registersInternalRead();
		ula.store(0);
		
		// "PC++"
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();

		// Armazena RegB em ULA(1)
		PC.read();
		memory.read();
		demux.setValue(extbus1.get());
		registersInternalRead();
		ula.store(1);

		// Realiza a operação de subtração e armazena em RegB
		ula.sub();
		ula.read(1);
		setStatusFlags(intbus1.get());
		registersInternalStore();

		// PC++
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
	}

	//sub <mem> %<regA>      || RegA <- memória[mem] - RegA
	public void subMemReg() {
	    // Incrementa PC para o endereço da memória
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();

	    // Lê o valor da memória
	    PC.read();
	    memory.read();
	    memory.read(); // Coloca o valor que está na memória no extbus1
	    IR.store(); // Armazena o valor que estava na memória no IR

	    // Incrementa PC para apontar para o registrador
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();

	    // Lê regA
	    PC.read();
	    memory.read();
	    demux.setValue(extbus1.get()); // Seleciona regA
	    registersInternalRead(); // Coloca o valor de regA no intbus1
	    ula.store(1); // Armazena regA na ULA (posição 1)

	    // Realiza a subtração
	    IR.internalRead(); // Coloca o valor que está na memória no intbus2
	    ula.internalStore(0);
	    ula.sub();
	    ula.read(1);
		 // Resultado é colocado no intbus2
	    registersInternalStore(); // Armazena o resultado em regA

	    // Incrementa PC para a próxima instrução
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	}

	public void subRegMem() {
        PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
		
		PC.read();
		memory.read();
		demux.setValue(extbus1.get());
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
		registersInternalRead(); 
		ula.store(0);
		PC.read(); 
		memory.read();
		memory.read(); 
		IR.store();
		IR.internalRead();
		ula.internalStore(1);
		ula.sub();
		ula.internalRead(1);
		setStatusFlags(intbus2.get());
		IR.internalStore();
		PC.read();
		memory.read();
		memory.store();
		IR.read();
		memory.store();
		
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
	}

	//sub imm %<regA>        || RegA <- imm - RegA
	public void subImmReg(){
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
		
		PC.read();
	    memory.read();
	    IR.store();
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    demux.setValue(extbus1.get());
	    registersInternalRead();
	    ula.store(1);
	    IR.internalRead();
	    ula.internalStore(0); 
	    ula.sub();
	    ula.read(1);
	    setStatusFlags(intbus1.get());
	    registersInternalStore();
	    
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	}

	public void imulMemReg(){
		//PC++
		//Primeiro parâmetro
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();

		
	}
	
	public void imulRegMem(){
		/*
		 * PC++ para conseguir o primeiro param
		*/
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();

		//Guardamos PC na memoria
		extbus1.put(301);
		memory.store();
		PC.read();
		memory.store();

		//Guardamos os RPGs
		RPG0.read();
		IR.internalStore();
		extbus1.put(302);
		memory.store();
		IR.read();
		memory.store();
		
		RPG1.read();
		IR.internalStore();
		extbus1.put(303);
		memory.store();
		IR.read();
		memory.store();

		RPG2.read();
		IR.internalStore();
		extbus1.put(304);
		memory.store();
		IR.read();
		memory.store();

		RPG3.read();
		IR.internalStore();
		extbus1.put(305);
		memory.store();
		IR.read();
		memory.store();

		/**
		 * Move RegA Reg0 (upcode: 13)
		 */
		//Armazena o upcode do Move
		extbus1.put(306);
		memory.store();
		extbus1.put(13);
		memory.store();
		//Recuperamos o ID do RegA (o primeiro parametro)
		PC.read();
		memory.read();
		IR.store();		
		//Guardamos na memoria o ID do RegA
		extbus1.put(307);
		memory.store();
		IR.read();
		memory.store();
		//Poe o Reg0 na memoria
		extbus1.put(308);
		memory.store();
		extbus1.put(0);
		memory.store();

		/**
		 * Move imm Reg1 (setar Reg1 como 0)
		 * REG
			*/
		//Colocamos o upcode de MoveImmReg na memoria
		extbus1.put(309);
		memory.store();
		extbus1.put(14);
		memory.store();
		//Colocamos 0 na proxima posição
		extbus1.put(310);
		memory.store();
		extbus1.put(0);
		memory.store();
		//Colocamos o ID do Reg1 na proxima posição
		extbus1.put(311);
		memory.store();
		extbus1.put(1);
		memory.store();

		/**
		 * PC++ Para guardar o valor do segundo parametro
		 */
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //Nesse momento, PC aponta para Mem (do imul)
		
		/**
		 * MoveMemReg (11) para guarduar o valor do param em REG3
		 */
		//Upcode da instrução na mem
		extbus1.put(312);
		memory.store();
		extbus1.put(11);
		memory.store();
		//Valor do primeiro parâmetro
		PC.read();
		memory.read();
		IR.store();
		extbus1.put(313);
		memory.store();
		IR.read();
		memory.store();
		//Valor do segundo parâmetro (REG3)
		extbus1.put(314);
		memory.store();
		extbus1.put(3);
		memory.store();

		/**
		 * Realizamos o ADD Reg0 Reg1 (Onde o loop começa)
		 */
		//Colocamos o upcode de AddRegReg(0) na memoria
		extbus1.put(315);
		memory.store();
		extbus1.put(0);
		memory.store();
		//Colocamos o primeiro parametro na memoria
		extbus1.put(316);
		memory.store();
		extbus1.put(0);
		memory.store();
		//Colocamos o segundo paramentro na memoria
		extbus1.put(317);
		memory.store();
		extbus1.put(1);
		memory.store();
		
		/**
		 * Move Imm Reg2 (Movemos 1 para o Reg2)
		 */
		//Upcode do moveImmReg
		extbus1.put(318);
		memory.store();
		extbus1.put(14);
		memory.store();
		//Primeiro Parâmetro
		extbus1.put(319);
		memory.store();
		extbus1.put(1);
		memory.store();
		//Segundo Parâmetro
		extbus1.put(320);
		memory.store();
		extbus1.put(2);
		memory.store();

		/**
		 * SUB Reg3 Reg2
		 */
		//Upcode do sub na mem
		extbus1.put(321);
		memory.store();
		extbus1.put(4);
		memory.store();
		//Guarda o ID do reg3
		extbus1.put(322);
		memory.store();
		extbus1.put(3);
		memory.store();
		//Guarda o ID do reg2
		extbus1.put(323);
		memory.store();
		extbus1.put(2);
		memory.store();

		/**
		 * Fazemos um Move Reg2Reg3 para guardar o resultado da subtração no local correto 
		 */
		//Upcode do moveRegReg (13)
		extbus1.put(324);
		memory.store();
		extbus1.put(13);
		memory.store();
		//Primeiro parâmetro
		extbus1.put(325);
		memory.store();
		extbus1.put(2);
		memory.store();
		//Segundo parâmetro
		extbus1.put(326);
		memory.store();
		extbus1.put(3);
		memory.store();

		/**
		 * JZ MoveRegMem
		 */
		//Upcode jz (18) na mem
		extbus1.put(327);
		memory.store();
		extbus1.put(18);
		memory.store();
		//Apontar para o endereço do Upcode moveRegMem
		extbus1.put(328);
		memory.store();
		extbus1.put(331); 
		memory.store();

		/**
		 * JMP ADD (retorna pro laço, caso o segundo param > 0)
		 */
		//Guarda o Upcode do jmp (16)
		extbus1.put(329);
		memory.store();
		extbus1.put(16);
		memory.store();
		//Guarda o endereço do add na memória (inicio do laco)
		extbus1.put(330);
		memory.store();
		extbus1.put(315);
		memory.store();
		
		/**
		 * MoveRegMem (Após a multiplicação, guarda o resultado na memória)
		 */
		//Upcode do moveRegMem (12)
		extbus1.put(331);
		memory.store();
		extbus1.put(12);
		memory.store();
		//Primeiro Parâmetro (Reg 1)
		extbus1.put(332);
		memory.store();
		extbus1.put(1);
		memory.store();
		//Segundo Parâmetro (Mem)
		PC.read();
		memory.read();
		IR.store(); //Guarda o endereço do dado em IR
		extbus1.put(333);
		memory.store();
		IR.read();
		memory.store();

		/*Restaura os valores dos RPGs com MoveImmReg
		 * RPG0 = 302
		 * RPG1 = 303
		 * RPG2 = 304
		 * RPG3 = 305
		*/
		//Move o primeiro RPG
		extbus1.put(302);
		memory.read();
		IR.store();//Guarda o valor do RPG no IR
		//UPCODE DO Move (14)
		extbus1.put(334);
		memory.store();
		extbus1.put(14);
		memory.store();
		//Primeiro Parâmetro
		extbus1.put(335);
		memory.store();
		IR.read();
		memory.store();
		//Segundo Parâmetro
		extbus1.put(336);
		memory.store();
		extbus1.put(0);
		memory.store();

		//Move o Segundo RPG
		extbus1.put(303);
		memory.read();
		IR.store();//Guarda o valor do RPG no IR
		//UPCODE DO Move (14)
		extbus1.put(337);
		memory.store();
		extbus1.put(14);
		memory.store();
		//Primeiro Parâmetro
		extbus1.put(338);
		memory.store();
		IR.read();
		memory.store();
		//Segundo Parâmetro
		extbus1.put(339);
		memory.store();
		extbus1.put(1);
		memory.store();

		//Move o Terceiro RPG
		extbus1.put(304);
		memory.read();
		IR.store();//Guarda o valor do RPG no IR
		//UPCODE DO Move (14)
		extbus1.put(340);
		memory.store();
		extbus1.put(14);
		memory.store();
		//Primeiro Parâmetro
		extbus1.put(341);
		memory.store();
		IR.read();
		memory.store();
		//Segundo Parâmetro
		extbus1.put(342);
		memory.store();
		extbus1.put(2);
		memory.store();

		//Move o Quarto RPG
		extbus1.put(305);
		memory.read();
		IR.store();//Guarda o valor do RPG no IR
		//UPCODE DO Move (14)
		extbus1.put(343);
		memory.store();
		extbus1.put(14);
		memory.store();
		//Primeiro Parâmetro
		extbus1.put(344);
		memory.store();
		IR.read();
		memory.store();
		//Segundo Parâmetro
		extbus1.put(345);
		memory.store();
		extbus1.put(3);
		memory.store();
		
		//PC++
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();

		/**
		 * JMP PROXIMA INSTRUCAO
		 * Atualização de PC para a próxima instrução
		 */
		extbus1.put(346);
		memory.store();
		extbus1.put(16);
		memory.store();

		extbus1.put(347);
		memory.store();
		PC.read();
		memory.store();
		
		/**
		 * Faz PC = 306, nessa etapa, estamos desviando PC para 
		 * que o fetch seja realizado na parte reservada na memória para
		 * o IMUL (processo que fizemos acima), em vez de permitir 
		 * que PC siga para a próxima instrução do executavel.
		 * 
		 * No fim da operação, fazemos um outro desvio que leva PC ao 
		 * fluxo normal do executavel novamente para que o programa siga
		 */
		extbus1.put(306);
		PC.store();		
	}

	public void imulRegReg(){
		/*
		 * PC++ para conseguir o primeiro param
		*/
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();

		//Guardamos PC na memoria
		extbus1.put(301);
		memory.store();
		PC.read();
		memory.store();

		//Guardamos os RPGs
		RPG0.read();
		IR.internalStore();
		extbus1.put(302);
		memory.store();
		IR.read();
		memory.store();
		
		RPG1.read();
		IR.internalStore();
		extbus1.put(303);
		memory.store();
		IR.read();
		memory.store();

		RPG2.read();
		IR.internalStore();
		extbus1.put(304);
		memory.store();
		IR.read();
		memory.store();

		RPG3.read();
		IR.internalStore();
		extbus1.put(305);
		memory.store();
		IR.read();
		memory.store();

		/**
		 * Move RegA Reg0 (upcode: 13)
		 */
		//Armazena o upcode do Move
		extbus1.put(306);
		memory.store();
		extbus1.put(13);
		memory.store();
		//Recuperamos o ID do RegA (o primeiro parametro)
		PC.read();
		memory.read();
		IR.store();		
		//Guardamos na memoria o ID do RegA
		extbus1.put(307);
		memory.store();
		IR.read();
		memory.store();
		//Pôe o Reg0 na memoria
		extbus1.put(308);
		memory.store();
		extbus1.put(0);
		memory.store();

		/**
		 * Move imm Reg1 (setar Reg1 como 0)
		 * REG
			*/
		//Colocamos o upcode de MoveImmReg na memoria
		extbus1.put(309);
		memory.store();
		extbus1.put(14);
		memory.store();
		//Colocamos 0 na proxima posição
		extbus1.put(310);
		memory.store();
		extbus1.put(0);
		memory.store();
		//Colocamos o ID do Reg1 na proxima posição
		extbus1.put(311);
		memory.store();
		extbus1.put(1);
		memory.store();

		/**
		 * PC++ Para guardar o valor do segundo parametro
		 */
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //Nesse momento, PC aponta para Mem (do imul)
		
		/**
		 * Move RegB Reg3 (opcode: 13) 
		 */
		//armazena o upcode do Move
		extbus1.put(312);
		memory.store();
		extbus1.put(13);
		memory.store();
		//Recuperamos o ID do RegB (o segundo parametro)
		PC.read();
		memory.read();
		IR.store();		
		//Guardamos na memoria o ID do RegB
		extbus1.put(313);
		memory.store();
		IR.read();
		memory.store();
		//Poe o Reg3 na memoria
		extbus1.put(314);
		memory.store();
		extbus1.put(3);
		memory.store();

		/**
		 * Realizamos o ADD Reg0 Reg1 (Onde o loop começa)
		 */
		//Colocamos o upcode de AddRegReg(0) na memoria
		extbus1.put(315);
		memory.store();
		extbus1.put(0);
		memory.store();
		//Colocamos o primeiro parametro na memoria
		extbus1.put(316);
		memory.store();
		extbus1.put(0);
		memory.store();
		//Colocamos o segundo paramentro na memoria
		extbus1.put(317);
		memory.store();
		extbus1.put(1);
		memory.store();
		
		/**
		 * Move Imm Reg2 (Movemos 1 para o Reg2)
		 */
		//Upcode do moveImmReg
		extbus1.put(318);
		memory.store();
		extbus1.put(14);
		memory.store();
		//Primeiro parâmetro
		extbus1.put(319);
		memory.store();
		extbus1.put(1);
		memory.store();
		//Segundo parâmetro
		extbus1.put(320);
		memory.store();
		extbus1.put(2);
		memory.store();

		/**
		 * SUB Reg3 Reg2
		 */
		//Upcode do sub na mem
		extbus1.put(321);
		memory.store();
		extbus1.put(4);
		memory.store();
		//Guarda o ID do reg3
		extbus1.put(322);
		memory.store();
		extbus1.put(3);
		memory.store();
		//Guarda o ID do reg2
		extbus1.put(323);
		memory.store();
		extbus1.put(2);
		memory.store();

		/**
		 * Fazemos um Move Reg2Reg3 para guardar o resultado da subtração no local correto 
		 */
		//Upcode do moveRegReg (13)
		extbus1.put(324);
		memory.store();
		extbus1.put(13);
		memory.store();
		//Primeiro parâmetro
		extbus1.put(325);
		memory.store();
		extbus1.put(2);
		memory.store();
		//Segundo parâmetro
		extbus1.put(326);
		memory.store();
		extbus1.put(3);
		memory.store();

		/**
		 * JZ MoveRegMem
		 */
		//Upcode jz (18) na mem
		extbus1.put(327);
		memory.store();
		extbus1.put(18);
		memory.store();
		//Apontar para o endereço do Upcode moveRegMem
		extbus1.put(328);
		memory.store();
		extbus1.put(331); 
		memory.store();

		/**
		 * JMP ADD (retorna pro laço, caso o segundo param > 0)
		 */
		//Guarda o Upcode do jmp (16)
		extbus1.put(329);
		memory.store();
		extbus1.put(16);
		memory.store();
		//Guarda o endereço do add na memória (inicio do laco)
		extbus1.put(330);
		memory.store();
		extbus1.put(315);
		memory.store();
		
		/**
		 * MoveRegMem (Após a multiplicação, guarda o resultado na memória, pois o dado seria perdido no momento de recuperação dos valores dos RPGs)
		 */
		//Upcode do moveRegMem (12)
		extbus1.put(331);
		memory.store();
		extbus1.put(12);
		memory.store();
		//Primeiro Parâmetro (Reg 1)
		extbus1.put(332);
		memory.store();
		extbus1.put(1);
		memory.store();
		//Segundo Parametro (Mem)
		PC.read();
		//memory.read();
		IR.store(); //Guarda o endereço do dado em IR
		extbus1.put(333);
		memory.store();
		IR.read();
		memory.store();

		/*Restaura os valores dos RPGs com MoveImmReg
		 * RPG0 = 302
		 * RPG1 = 303
		 * RPG2 = 304
		 * RPG3 = 305
		**/
		//Move o primeiro RPG0
		extbus1.put(302);
		memory.read();
		IR.store();//Guarda o valor do RPG no IR
		//UPCODE DO Move (14)
		extbus1.put(334);
		memory.store();
		extbus1.put(14);
		memory.store();
		//Primeiro Parâmetro
		extbus1.put(335);
		memory.store();
		IR.read();
		memory.store();
		//Segundo Parâmetro
		extbus1.put(336);
		memory.store();
		extbus1.put(0);
		memory.store();

		//Move o Segundo RPG1
		extbus1.put(303);
		memory.read();
		IR.store();//Guarda o valor do RPG no IR
		//UPCODE DO Move (14)
		extbus1.put(337);
		memory.store();
		extbus1.put(14);
		memory.store();
		//Primeiro Parâmetro
		extbus1.put(338);
		memory.store();
		IR.read();
		memory.store();
		//Segundo Parâmetro
		extbus1.put(339);
		memory.store();
		extbus1.put(1);
		memory.store();

		//Move o Terceiro RPG2
		extbus1.put(304);
		memory.read();
		IR.store();//Guarda o valor do RPG no IR
		//UPCODE DO Move (14)
		extbus1.put(340);
		memory.store();
		extbus1.put(14);
		memory.store();
		//Primeiro Parâmetro
		extbus1.put(341);
		memory.store();
		IR.read();
		memory.store();
		//Segundo Parâmetro
		extbus1.put(342);
		memory.store();
		extbus1.put(2);
		memory.store();

		//Move o Quarto RPG3
		extbus1.put(305);
		memory.read();
		IR.store();//Guarda o valor do RPG no IR
		//UPCODE DO Move (14)
		extbus1.put(343);
		memory.store();
		extbus1.put(14);
		memory.store();
		//Primeiro Parâmetro
		extbus1.put(344);
		memory.store();
		IR.read();
		memory.store();
		//Segundo Parâmetro
		extbus1.put(345);
		memory.store();
		extbus1.put(3);
		memory.store();

		/*
		 * MoveMemReg (preciso recuperar o resultado da memória para o registrador)
		**/
		//Opcode do moveMemReg (11)
		extbus1.put(346);
		memory.store();
		extbus1.put(11);
		memory.store();

		//Primeiro parâmetro (Mem)
		extbus1.put(347);
		memory.store();
		extbus1.put(333);
		memory.read();
		memory.store();

		//Segundo parâmetro (RegB)
		extbus1.put(348);
		memory.store();
		extbus1.put(313);
		memory.read();
		memory.store();
		
		//PC++
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();

		/**
		 * JMP PROXIMA INSTRUCAO
		 * Atualização de PC para a próxima instrução
		 */
		extbus1.put(349);
		memory.store();
		extbus1.put(16);
		memory.store();

		extbus1.put(350);
		memory.store();
		PC.read();
		memory.store();
		
		/**
		 * Faz PC = 306, nessa etapa, estamos desviando PC para 
		 * que o fetch seja realizado na parte reservada na memória para
		 * o IMUL (processo que fizemos acima), em vez de permitir 
		 * que PC siga para a próxima instrução do executavel.
		 * 
		 * No fim da operação, fazemos um outro desvio que leva PC ao 
		 * fluxo normal do executavel novamente para que o programa siga
		 */
		extbus1.put(306);
		PC.store();		
	}

	public void moveMemReg() {
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); // Now PC points to the first parameter
		PC.read(); 
		memory.read(); // The address memory is now in the external bus.
		memory.read(); // The data memory is now in the external bus.
		IR.store();
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); // Now PC points to the second parameter (the second reg id)
		PC.read();
		memory.read(); // The register id is now in the external bus.
		demux.setValue(extbus1.get()); // Points to the correct register
		IR.internalRead();
		registersStore(); // Performs an internal store for the register identified into demux bus
		PC.internalRead(); // We need to make PC points to the next instruction address
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //Now PC points to the next instruction. We go back to the FETCH status.
	}

	public void moveRegMem() {
	
		// PC++
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();

		// Acessa RegA e posiciona em IR
		PC.read();
		memory.read();
		demux.setValue(extbus1.get());
		registersRead();
		IR.internalStore();

		// "PC++"
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();

		// Acessa memoria[mem] no modo store e armazena RegA nessa posição de memória
		PC.read();
		memory.read();
		memory.store();
		IR.read();
		memory.store();

		// PC++
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
	}

	/**
	 * This method implements the microprogram for
	 * 					move <reg1> <reg2> 
	 * In the machine language this command number is 9
	 *    
	 * The method reads the two register ids (<reg1> and <reg2>) from the memory, in positions just after the command, and
	 * copies the value from the <reg1> register to the <reg2> register
	 * 
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <-  intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the first parameter
	 * 6. pc -> extbus //(pc.read())the address where is the position to be read is now in the external bus 
	 * 7. memory reads from extbus //this forces memory to write the parameter (first regID) in the extbus
	 * 8. pc -> intbus2 //pc.read() //getting the second parameter
	 * 9. ula <-  intbus2 //ula.store()
	 * 10. ula incs
	 * 11. ula -> intbus2 //ula.read()
	 * 12. pc <- intbus2 //pc.store() now pc points to the second parameter
	 * 13. demux <- extbus //now the register to be operated is selected
	 * 14. registers -> intbus1 //this performs the internal reading of the selected register 
	 * 15. PC -> extbus (pc.read())the address where is the position to be read is now in the external bus 
	 * 16. memory reads from extbus //this forces memory to write the parameter (second regID) in the extbus
	 * 17. demux <- extbus //now the register to be operated is selected
	 * 18. registers <- intbus1 //thid rerforms the external reading of the register identified in the extbus
	 * 19. 10. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 20. ula <- intbus2 //ula.store()
	 * 21. ula incs
	 * 22. ula -> intbus2 //ula.read()
	 * 23. pc <- intbus2 //pc.store()  
	 * 		  
	 */
	
	//move %<regA> %<regB>   || RegB <- RegA
	public void moveRegReg() {		
	    // Incrementa PC para o primeiro parâmetro
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore(); // PC aponta para regA
	    PC.read();
	    memory.read();  // Lê regA // The first register id is now in the external bus.
	    
	    //Now PC points to the second parameter (the second reg id)
	    PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
		
	    demux.setValue(extbus1.get()); // Seleciona regA
	    registersInternalRead(); //Starts the read from the register identified into demux bus // Coloca o valor de regA no intbus1
		PC.read();
		memory.read(); // The second register id is now in the external bus.
		
		demux.setValue(extbus1.get()); // Seleciona regB //Points to the correct register
		registersInternalStore(); //Performs an internal store for the register identified into demux bus

		//Make PC points to the next instruction address // Incrementa PC para a próxima instrução
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	}

	public void moveImmReg() {
        PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
		
		PC.read(); 
		memory.read(); 
		IR.store(); 
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
		PC.read();
		memory.read();
		demux.setValue(extbus1.get());
		IR.internalRead();
		registersStore();
		
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
	}

	/**
	 * This method implements the microprogram for
	 * 					inc 
	 * In the machine language this command number is 8
	 *    
	 * The method moves the value in rpg (the first register in the register list)
	 *  into the ula and performs an inc method
	 * 		-> inc works just like add rpg (the first register in the register list)
	 *         with the mumber 1 stored into the memory
	 * 		-> however, inc consumes lower amount of cycles  
	 * 
	 * The logic is
	 * 
	 * 1. rpg -> intbus1 //rpg.read()
	 * 2. ula  <- intbus1 //ula.store()
	 * 3. Flags <- zero //the status flags are reset
	 * 4. ula incs
	 * 5. ula -> intbus1 //ula.read()
	 * 6. ChangeFlags //informations about flags are set according the result
	 * 7. rpg <- intbus1 //rpg.store()
	 * 8. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 9. ula <- intbus2 //ula.store()
	 * 10. ula incs
	 * 11. ula -> intbus2 //ula.read()
	 * 12. pc <- intbus2 //pc.store()
	 * end
	 * @param address
	 */
	// public void inc() {
	// 	RPG0.internalRead();
	// 	ula.store(1);
	// 	ula.inc();
	// 	ula.read(1);
	// 	setStatusFlags(intbus1.get());
	// 	RPG0.internalStore();
	// 	PC.internalRead(); //we need to make PC points to the next instruction address
	// 	ula.internalStore(1);
	// 	ula.inc();
	// 	ula.internalRead(1);
	// 	PC.internalStore(); //now PC points to the next instruction. We go back to the FETCH status.
	// }
	
	//inc %<regA>   || RegA ++
	public void incReg(){
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();

	    PC.read();
	    memory.read();
	    demux.setValue(extbus1.get());
	    registersInternalRead();
	    ula.store(1);
	    ula.inc();
	    ula.read(1);
		setStatusFlags(intbus1.get());
	    registersInternalStore();

	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	}
		

	/**
	 * This method implements the microprogram for
	 * 					JMP address
	 * In the machine language this command number is 2, and the address is in the position next to him
	 *    
	 * where address is a valid position in this memory architecture (where the PC is redirecto to)
	 * The method reads the value from memory (position address) and 
	 * inserts it into the PC register.
	 * So, the program is deviated
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <-  intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. pc -> extbus //pc.read()
	 * 7. memory reads from extbus //this forces memory to write the data position in the extbus
	 * 8. pc <- extbus //pc.store() //pc was pointing to another part of the memory
	 * end
	 * @param address
	 */
	public void jmp() {
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the parameter address
		PC.read();
		memory.read();
		PC.store();
	}
	
	/**
	 * This method implements the microprogram for
	 * 					jn address
	 * In the machine language this command number is 4, and the address is in the position next to him
	 *    
	 * where address is a valid position in this memory architecture (where 
	 * the PC is redirected to, but only in the case the NEGATIVE bit in Flags is 1)
	 * The method reads the value from memory (position address) and 
	 * inserts it into the PC register if the NEG bit in Flags register is setted.
	 * So, the program is deviated conditionally
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <-  intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.internalstore() now pc points to the parameter
	 * 6. pc -> extbus1 //pc.read() now the parameter address is in the extbus1
	 * 7. Memory -> extbus1 //memory.read() the address (if jn) is in external bus 1
	 * 8. statusMemory(1)<- extbus1 // statusMemory.storeIn1()
	 * 9. ula incs
	 * 10. ula -> intbus2 //ula.read()
	 * 11. PC <- intbus2 // PC.internalStore() PC is now pointing to next instruction
	 * 12. PC -> extbus1 // PC.read() the next instruction address is in the extbus
	 * 13. statusMemory(0)<- extbus1 // statusMemory.storeIn0()
	 * 14. Flags(bitNEGATIVE) -> extbus1 //the NEGATIVE bit is in the external bus
	 * 15. statusMemory <- extbus // the status memory returns the correct address according the ZERO bit
	 * 16. PC <- extbus1 // PC stores the new address where the program is redirected to
	 * end
	 * @param address
	 */
	public void jn() {
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();// now PC points to the parameter address
		
		PC.read();
		memory.read();// now the parameter value (address of the jz) is in the external bus
		statusMemory.storeIn1(); // the address is in position 1 of the status memory
		
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();// now PC points to the next instruction
		
		PC.read();//now the bus has the next istruction address
		statusMemory.storeIn0(); //the address is in the position 0 of the status memory
		
		extbus1.put(Flags.getBit(1)); //the ZERO bit is in the external bus 
		statusMemory.read(); //gets the correct address (next instruction or parameter address)
		PC.store(); //stores into PC
	}
	
	/**
	 * This method implements the microprogram for
	 * 					JZ address
	 * In the machine language this command number is 3, and the address is in the position next to him
	 *    
	 * where address is a valid position in this memory architecture (where 
	 * the PC is redirected to, but only in the case the ZERO bit in Flags is 1)
	 * The method reads the value from memory (position address) and 
	 * inserts it into the PC register if the ZERO bit in Flags register is setted.
	 * So, the program is deviated conditionally
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <-  intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.internalstore() now pc points to the parameter
	 * 6. pc -> extbus1 //pc.read() now the parameter address is in the extbus1
	 * 7. Memory -> extbus1 //memory.read() the address (if jn) is in external bus 1
	 * 8. statusMemory(1)<- extbus1 // statusMemory.storeIn1()
	 * 9. ula incs
	 * 10. ula -> intbus2 //ula.read()
	 * 11. PC <- intbus2 // PC.internalStore() PC is now pointing to next instruction
	 * 12. PC -> extbus1 // PC.read() the next instruction address is in the extbus
	 * 13. statusMemory(0)<- extbus1 // statusMemory.storeIn0()
	 * 14. Flags(bitZero) -> extbus1 //the ZERO bit is in the external bus
	 * 15. statusMemory <- extbus // the status memory returns the correct address according the ZERO bit
	 * 16. PC <- extbus1 // PC stores the new address where the program is redirected to
	 * end
	 * @param address
	 */
	
	//jz <mem>  || Se a última operação = 0 então PC <- mem (desvio condicional)
	public void jz() {
		// Incrementa PC para o endereço de memória
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore(); // PC aponta para o endereço

	    // Lê o endereço de memória
	    PC.read();
	    memory.read(); // Coloca o endereço lido no extbus1
	    statusMemory.storeIn1(); // Armazena o endereço na posição 1 da statusMemory

	    // Incrementa PC para a próxima instrução
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    
	    statusMemory.storeIn0(); // Armazena o endereço na posição 0 da statusMemory

	    // Checa o bit ZERO
	    extbus1.put(Flags.getBit(0)); // Coloca o valor do Bit ZERO (0 ou 1) no extbus1
	    statusMemory.read(); // Lê o endereço correto dependendo do bit ZERO

	    // Desvia para o endereço, se necessário
	    PC.store(); // Armazena o endereço em PC
	}
	
	public void jeq() {
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    
	    PC.read();
	    memory.read();
	    demux.setValue(extbus1.get());
	    registersInternalRead();
	    ula.store(0);
	    
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    demux.setValue(extbus1.get());
	    
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    statusMemory.storeIn1();
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    statusMemory.storeIn0();
	    
	    registersInternalRead();
	    ula.store(1);
	    ula.sub();
	    ula.internalRead(1);
	    setStatusFlags(intbus2.get());
	    extbus1.put(Flags.getBit(0));
	    statusMemory.read();
	    PC.store();
	}
	
	public void jneq(){
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();

	    PC.read();
	    memory.read();
	    demux.setValue(extbus1.get());
	    registersInternalRead();
	    ula.store(0);

	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    demux.setValue(extbus1.get());

	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    statusMemory.storeIn0();
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    statusMemory.storeIn1();
	    
	    registersInternalRead();
	    ula.store(1);
	    ula.sub();
	    ula.internalRead(1);
	    setStatusFlags(intbus2.get());
	    extbus1.put(Flags.getBit(0));
	    statusMemory.read();
	    PC.store();;
	}

	public void jgt(){
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
		PC.read();

		//REG A
		memory.read();
		demux.setValue(extbus1.get());
		registersInternalRead();
		ula.store(0);
		
		//REG B
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
		PC.read();
		memory.read();
		IR.store();
		
		//Pega o último parâmetro e adiciona na posição 1 status Memory (JMP 1)
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
		PC.read();
		memory.read();
		statusMemory.storeIn1();
		
		//JUMP 0
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
		PC.read();
		statusMemory.storeIn0();
		IR.read();
		demux.setValue(extbus1.get());
		registersInternalRead();
		ula.store(1);
		ula.sub();
		ula.internalRead(1);
		setStatusFlags(intbus2.get());
		extbus1.put(Flags.getBit(0));
		statusMemory.read();
		PC.store();
	}
	
	// jlw %<regA> %<regB> <mem> || se RegA<RegB então PC <- mem (desvio condicional)
	public void jlw() {
		
		// PC++
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
		
		// lê o dado de RegA
		PC.read();
		memory.read();
		demux.setValue(extbus1.get());
		registersInternalRead();
		ula.store(0);
		
		// "PC++"
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
		
		// Lê o dado de RegB e armazena em IR
		PC.read();
		memory.read();
		IR.store();
		
		// "PC++"
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
		
		// JUMP 1
		PC.read();
		memory.read();
		statusMemory.storeIn1();
		
		// "PC++"
		ula.inc();
		ula.internalRead(1);
		PC.internalStore();
		
		// JUMP 0
		PC.read();
		statusMemory.storeIn0();
		
		// Coloca o dado de RegB em ULA(1)
		IR.read();
		demux.setValue(extbus1.get());
		registersInternalRead();
		ula.store(1);
		
		// Realiza a subtração e verifica o bit negativo
		ula.sub();
		ula.internalRead(1);
		setStatusFlags(intbus2.get());
		extbus1.put(Flags.getBit(1));
		statusMemory.read();
		PC.store();
			
	}
		
	
	/**
	 * This method implements the microprogram for
	 * 					read address
	 * In the machine language this command number is 5, and the address is in the position next to him
	 *    
	 * where address is a valid position in this memory architecture 
	 * The method reads the value from memory (position address) and 
	 * inserts it into the RPG register (the first register in the register list)
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <-  intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. pc -> extbus //(pc.read())the address where is the position to be read is now in the external bus 
	 * 7. memory reads from extbus //this forces memory to write the address in the extbus
	 * 8. memory reads from extbus //this forces memory to write the stored data in the extbus
	 * 9. RPG <- extbus //the data is read
	 * 10. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 11. ula <- intbus2 //ula.store()
	 * 12. ula incs
	 * 13. ula -> intbus2 //ula.read()
	 * 14. pc <- intbus2 //pc.store() 
	 * end
	 * @param address
	 */
	public void read() {
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the parameter address
		PC.read(); 
		memory.read(); // the address is now in the external bus.
		memory.read(); // the data is now in the external bus.

		IR.store();
		IR.internalRead();
		RPG0.store();
		
		PC.internalRead(); //we need to make PC points to the next instruction address
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the next instruction. We go back to the FETCH status.
	}
	
	/**
	 * This method implements the microprogram for
	 * 					store address
	 * In the machine language this command number is 6, and the address is in the position next to him
	 *    
	 * where address is a valid position in this memory architecture 
	 * The method reads the value from RPG (the first register in the register list) and 
	 * inserts it into the memory (position address) 
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <-  intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. pc -> extbus //(pc.read())the parameter address is the external bus
	 * 7. memory reads // memory reads the data in the parameter address. 
	 * 					// this data is the address where the RPG value must be stores 
	 * 8. memory stores //memory reads the address and wait for the value
	 * 9. RPG -> Externalbus //RPG.read()
	 * 10. memory stores //memory receives the value and stores it
	 * 11. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 12. ula <- intbus2 //ula.store()
	 * 13. ula incs
	 * 14. ula -> intbus2 //ula.read()
	 * 15. pc <- intbus2 //pc.store() 
	 * end
	 * @param address
	 */
	public void store() {
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the parameter address
		PC.read(); 
		memory.read();   //the parameter address (pointing to the addres where data must be stored
		                 //is now in externalbus1
		memory.store(); //the address is in the memory. Now we must to send the data
		
		RPG0.read();
		IR.internalStore();
		IR.read();
		
		memory.store(); //the data is now stored
		PC.internalRead(); //we need to make PC points to the next instruction address
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the next instruction. We go back to the FETCH status.
	}
	
	/**
	 * This method implements the microprogram for
	 * 					ldi immediate
	 * In the machine language this command number is 7, and the immediate value
	 *        is in the position next to him
	 *    
	 * The method moves the value (parameter) into the internalbus1 and the RPG 
	 * (the first register in the register list) consumes it 
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <-  intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. pc -> extbus //(pc.read())the address where is the position to be read is now in the external bus 
	 * 7. memory reads from extbus //this forces memory to write the stored data in the extbus
	 * 8. RPG <- extbus //rpg.store()
	 * 9. 10. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 10. ula <- intbus2 //ula.store()
	 * 11. ula incs
	 * 12. ula -> intbus2 //ula.read()
	 * 13. pc <- intbus2 //pc.store() 
	 * end
	 * @param address
	 */
	public void ldi() {
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the parameter address
		PC.read(); 
		memory.read(); // the immediate is now in the external bus.
		
		IR.store();
		IR.internalRead();
		RPG0.store();   //RPG receives the immediate
		
		PC.internalRead(); //we need to make PC points to the next instruction address
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the next instruction. We go back to the FETCH status.
	}	

	public ArrayList<Register> getRegistersList() {
		return registersList;
	}

	/**
	 * This method performs an (external) read from a register into the register list.
	 * The register id must be in the demux bus
	 */
	private void registersRead() {
		registersList.get(demux.getValue()).read();
	}
	
	/**
	 * This method performs an (internal) read from a register into the register list.
	 * The register id must be in the demux bus
	 */
	private void registersInternalRead() {
		registersList.get(demux.getValue()).internalRead();
	}
	
	/**
	 * This method performs an (external) store toa register into the register list.
	 * The register id must be in the demux bus
	 */
	private void registersStore() {
		registersList.get(demux.getValue()).store();
	}
	
	/**
	 * This method performs an (internal) store toa register into the register list.
	 * The register id must be in the demux bus
	 */
	private void registersInternalStore() {
		registersList.get(demux.getValue()).internalStore();
	}

	/**
	 * This method reads an entire file in machine code and
	 * stores it into the memory
	 * NOT TESTED
	 * @param filename
	 * @throws IOException 
	 */
	public void readExec(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new		 
		FileReader(filename+".dxf"));
		String linha;
		int i=0;
		while ((linha = br.readLine()) != null) {
			extbus1.put(i);
			memory.store();
			extbus1.put(Integer.parseInt(linha));
			memory.store();
			i++;
		}
		br.close();
	}
	
	/**
	 * This method executes a program that is stored in the memory
	 */
	public void controlUnitEexec() {
		halt = false;
		while (!halt) {
			fetch();
			decodeExecute();
		}
	}
	
	/**
	 * This method implements The decode proccess,
	 * that is to find the correct operation do be executed
	 * according the command.
	 * And the execute proccess, that is the execution itself of the command
	 */

	private void decodeExecute() {
		IR.internalRead(); //the instruction is in the internalbus2
		int command = intbus2.get();
		simulationDecodeExecuteBefore(command);
		switch (command) {
		// case 0:
		// 	add();
		// 	break;
		case 0:
			addRegReg();
			break;
		case 1:
			addMemReg();
			break;
		case 2:
			addRegMem();
			break;
		case 3:
			addImmReg();
			break;
		/*case 5:
			sub();
			break;
		*/
		case 4:
			subRegReg();
			break;
		
		case 5:
			subMemReg();
			break;
		
		case 6:
			subRegMem();
			break;
		
		case 7:
			subImmReg();
			break;
		
		case 8:
			imulMemReg(); //Linha 1310
			break;

		case 9:
			imulRegMem(); //Linha 1314
			break;

		case 10:
			imulRegReg(); //Linha 
			break;

		case 11:
			moveMemReg();
			break;

		case 12:
			moveRegMem();
			break;

		case 13:
			moveRegReg();
			break;
		
		case 14:
			moveImmReg();
			break;

		case 15:
			incReg(); 
			break;

		case 16:
			jmp();
			break;
	
		case 17:
			jn();
			break;

		case 18:
			jz();
			break;
			
		case 19:
			jeq();
			break;

		case 20:
			jneq();
			break;

		case 21:
			jgt();
			break;

		case 22:
			jlw();
			break;

		case 23:
			read();
			break;

		case 24:
			store();
			break;
			
		case 25:
			ldi();
			break;

		default:
			halt = true;
			break;
		}
		if (simulation)
			simulationDecodeExecuteAfter();
	}

	/**
	 * This method is used to show the components status in simulation conditions
	 * NOT TESTED
	 * @param command 
	 */
	private void simulationDecodeExecuteBefore(int command) {
		System.out.println("----------BEFORE Decode and Execute phases--------------");
		String instruction;
		int parameter = 0;
		System.out.println("Memory state:");
		for (int i = 400; i >= this.stkTop.getData(); i--) {
			System.out.println("memory["+i+"]="+memory.getDataList()[i]);
		}
		for (Register r:registersList) {
			System.out.println(r.getRegisterName()+": "+r.getData());
		}
		if (command !=-1)
			instruction = commandsList.get(command);
		else
			instruction = "END";
		if (hasOperands(instruction)) {
			parameter = memory.getDataList()[PC.getData()+1];
			System.out.println("Instruction: "+instruction+" "+parameter);
		}
		else
			System.out.println("Instruction: "+instruction);
		if ("read".equals(instruction))
			System.out.println("memory["+parameter+"]="+memory.getDataList()[parameter]);
			
		
	}

	/**
	 * This method is used to show the components status in simulation conditions
	 * NOT TESTED 
	 */
	private void simulationDecodeExecuteAfter() {
		String instruction;
		System.out.println("-----------AFTER Decode and Execute phases--------------");
		System.out.println("Internal Bus 1: "+intbus1.get());
		System.out.println("Internal Bus 2: "+intbus2.get());
		System.out.println("External Bus 1: "+extbus1.get());
		for (Register r:registersList) {
			System.out.println(r.getRegisterName()+": "+r.getData());
		}
		Scanner entrada = new Scanner(System.in);
		System.out.println("Press <Enter>");
		String mensagem = entrada.nextLine();
	}

	/**
	 * This method uses PC to find, in the memory,
	 * the command code that must be executed.
	 * This command must be stored in IR
	 * NOT TESTED!
	 */
	private void fetch() {
		PC.read();
		memory.read();
		IR.store();
		simulationFetch();
	}

	/**
	 * This method is used to show the components status in simulation conditions
	 * NOT TESTED!!!!!!!!!
	 */
	private void simulationFetch() {
		if (simulation) {
			System.out.println("-------Fetch Phase------");
			System.out.println("PC: "+PC.getData());
			System.out.println("IR: "+IR.getData());
		}
	}

	/**
	 * This method is used to show in a correct way the operands (if there is any) of instruction,
	 * when in simulation mode
	 * NOT TESTED!!!!!
	 * @param instruction 
	 * @return
	 */
	private boolean hasOperands(String instruction) {
		if ("inc".equals(instruction)) //inc is the only one instruction having no operands
			return false;
		else
			return true;
	}

	/**
	 * This method returns the amount of positions allowed in the memory
	 * of this architecture
	 * NOT TESTED!!!!!!!
	 * @return
	 */
	public int getMemorySize() {
		return memorySize;
	}
	
	public static void main(String[] args) throws IOException {
		Architecture arch = new Architecture(true);
		arch.readExec("imulRegReg");
		arch.controlUnitEexec();
	}
	

}