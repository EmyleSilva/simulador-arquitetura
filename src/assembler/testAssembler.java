package assembler;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class testAssembler {

	@Test
	public void testProccessCommand() {
		
		/*
		 *  we must to test if the assembler generates correct sequence lines
		 *  of object program for all of these commands
				Code	Command		arg
				0		add 		addr (rpg0 <- rpg0 + addr)
				1		sub 		addr (rpg0 <- rpg0 - addr)
				2		jmp 		addr (pc <- addr)
				3		jz 			addr  (se bitZero pc <- addr)
				4		jn 			addr  (se bitneg pc <- addr)
				5		read 		addr (rrpg0pg <- addr)
				6		store 		addr  (addr <- rpg0)
				7		ldi 		x (rpg0 <- x. x must be an integer)
				8		inc    		(rpg0++)
				9       move        %regA %regB (regA <- regB)
		 */
		Assembler ass = new Assembler();
		String commandLine[] = new String[4];
		ArrayList<String> returnedObj = new ArrayList<>();
		
		//1º test: add
		// commandLine[0] = "add";
		// commandLine[1] = "variable";
		// ass.proccessCommand(commandLine);
		// returnedObj = ass.getObjProgram();
		// assertEquals("0", returnedObj.get(0)); //the add code is 0
		// assertEquals("&variable", ass.getObjProgram().get(1));
		// assertEquals(2, ass.getObjProgram().size()); //only two lines: the command and the address
		
		//addRegReg
		commandLine[0] = "add";
		commandLine[1] = "%RPG0";
		commandLine[2] = "%RPG1";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("0", returnedObj.get(0)); //the addRegReg code is 1
		assertEquals("%RPG0", ass.getObjProgram().get(1));
		assertEquals("%RPG1", ass.getObjProgram().get(2));
		assertEquals(3, ass.getObjProgram().size()); //only three lines: the command and the address
		
		ass.getObjProgram().clear(); //Limpa o arra
		
		//addMemReg
		commandLine[0] = "add";
		commandLine[1] = "variable";
		commandLine[2] = "%RPG0";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("1", returnedObj.get(0)); //the addRegReg code is 2
		assertEquals("&variable", ass.getObjProgram().get(1));
		assertEquals("%RPG0", ass.getObjProgram().get(2));
		assertEquals(3, ass.getObjProgram().size()); //only three lines: the command and the address		

		ass.getObjProgram().clear(); //limpa o array

		//addRegMem
		commandLine[0] = "add";
		commandLine[1] = "%RPG0";
		commandLine[2] = "var";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("2", returnedObj.get(0)); //the addRegReg code is 3
		assertEquals("%RPG0", ass.getObjProgram().get(1));
		assertEquals("&var", ass.getObjProgram().get(2));
		assertEquals(3, ass.getObjProgram().size()); //only three lines: the command and the address		

		ass.getObjProgram().clear(); //limpa o array

		//addImmReg
		commandLine[0] = "add";
		commandLine[1] = "55";
		commandLine[2] = "%RPG1";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("3", returnedObj.get(0)); //the addImmReg code is 4
		assertEquals("55", ass.getObjProgram().get(1));
		assertEquals("%RPG1", ass.getObjProgram().get(2));
		assertEquals(3, ass.getObjProgram().size()); //only three lines: the command and the address

		// //6º test: sub
		// returnedObj = new ArrayList<>();
		// ass = new Assembler();
		// commandLine[0] = "sub";
		// commandLine[1] = "variable";
		// ass.proccessCommand(commandLine);
		// returnedObj = ass.getObjProgram();
		// assertEquals("5", returnedObj.get(0)); //the sub code is 5
		// assertEquals("&variable", ass.getObjProgram().get(1));
		// assertEquals(2, ass.getObjProgram().size()); //only two lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array

		//subRegReg
		commandLine[0] = "sub";
		commandLine[1] = "%RPG0";
		commandLine[2] = "%RPG1";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("4", returnedObj.get(0)); //the subRegReg code is 6
		assertEquals("%RPG0", ass.getObjProgram().get(1));
		assertEquals("%RPG1", ass.getObjProgram().get(2));
		assertEquals(3, ass.getObjProgram().size()); //only three lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array

		//subMemReg
		commandLine[0] = "sub";
		commandLine[1] = "var";
		commandLine[2] = "%RPG1";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("5", returnedObj.get(0)); //the subMemReg code is 7
		assertEquals("&var", ass.getObjProgram().get(1));
		assertEquals("%RPG1", ass.getObjProgram().get(2));
		assertEquals(3, ass.getObjProgram().size()); //only three lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array

		//subRegMem
		commandLine[0] = "sub";
		commandLine[1] = "%RPG1";
		commandLine[2] = "var";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("6", returnedObj.get(0)); //the subRegMem code is 8
		assertEquals("%RPG1", ass.getObjProgram().get(1));
		assertEquals("&var", ass.getObjProgram().get(2));
		assertEquals(3, ass.getObjProgram().size()); //only three lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array
		
		//subImmReg
		commandLine[0] = "sub";
		commandLine[1] = "18";
		commandLine[2] = "%RPG1";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("7", returnedObj.get(0)); //the subImmReg code is 9
		assertEquals("18", ass.getObjProgram().get(1));
		assertEquals("%RPG1", ass.getObjProgram().get(2));
		assertEquals(3, ass.getObjProgram().size()); //only three lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array

		//imulMemReg
		commandLine[0] = "imul";
		commandLine[1] = "var";
		commandLine[2] = "%RPG1";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("8", returnedObj.get(0)); //the imulMemReg code is 10
		assertEquals("&var", ass.getObjProgram().get(1));
		assertEquals("%RPG1", ass.getObjProgram().get(2));
		assertEquals(3, ass.getObjProgram().size()); //only three lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array

		//imulRegMem
		commandLine[0] = "imul";
		commandLine[1] = "%RPG1";
		commandLine[2] = "var";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("9", returnedObj.get(0)); //the imulRegMem code is 11
		assertEquals("%RPG1", ass.getObjProgram().get(1));
		assertEquals("&var", ass.getObjProgram().get(2));
		assertEquals(3, ass.getObjProgram().size()); //only three lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array

		//imulRegReg
		commandLine[0] = "imul";
		commandLine[1] = "%RPG1";
		commandLine[2] = "%RPG0";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("10", returnedObj.get(0)); //the imulRegReg code is 12
		assertEquals("%RPG1", ass.getObjProgram().get(1));
		assertEquals("%RPG0", ass.getObjProgram().get(2));
		assertEquals(3, ass.getObjProgram().size()); //only three lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array

		//moveMemReg
		commandLine[0] = "move";
		commandLine[1] = "var";
		commandLine[2] = "%RPG0";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("11", returnedObj.get(0)); //the moveMemReg code is 13
		assertEquals("&var", ass.getObjProgram().get(1));
		assertEquals("%RPG0", ass.getObjProgram().get(2));
		assertEquals(3, ass.getObjProgram().size()); //only three lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array

		//moveRegMem
		commandLine[0] = "move";
		commandLine[1] = "%RPG0";
		commandLine[2] = "var";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("12", returnedObj.get(0)); //the moveRegMem code is 14
		assertEquals("%RPG0", ass.getObjProgram().get(1));
		assertEquals("&var", ass.getObjProgram().get(2));
		assertEquals(3, ass.getObjProgram().size()); //only three lines: the command and the address

		ass.getObjProgram().clear(); //limpa o array

		//moveRegReg
		commandLine[0] = "move";
		commandLine[1] = "%RPG1";
		commandLine[2] = "%RPG0";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("13", returnedObj.get(0)); //the moveRegReg code is 15
		assertEquals("%RPG1", ass.getObjProgram().get(1));
		assertEquals("%RPG0", ass.getObjProgram().get(2));
		assertEquals(3, ass.getObjProgram().size()); //only three lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array

		//moveImmReg
		commandLine[0] = "move";
		commandLine[1] = "30";
		commandLine[2] = "%RPG0";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("14", returnedObj.get(0)); //the ImmReg code is 16
		assertEquals("30", ass.getObjProgram().get(1));
		assertEquals("%RPG0", ass.getObjProgram().get(2));
		assertEquals(3, ass.getObjProgram().size()); //only three lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array

		//18º test: inc
		// returnedObj = new ArrayList<>();
		// ass = new Assembler();
		// commandLine[0] = "inc";
		// commandLine[1] = "address";
		// ass.proccessCommand(commandLine);
		// returnedObj = ass.getObjProgram();
		// assertEquals("15", returnedObj.get(0)); //the inc code is 17
		// assertEquals(1, ass.getObjProgram().size()); //only one lines: the command and no parameters

		ass.getObjProgram().clear(); //limpa o array

		//incReg
		commandLine[0] = "inc";
		commandLine[1] = "%RPG0";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("15", returnedObj.get(0)); //the incReg code is 18
		assertEquals("%RPG0", ass.getObjProgram().get(1));
		assertEquals(2, ass.getObjProgram().size()); //only two lines: the command and no parameters

		ass.getObjProgram().clear(); //limpa o array

		//jmp
		returnedObj = new ArrayList<>();
		ass = new Assembler();
		commandLine[0] = "jmp";
		commandLine[1] = "label";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("16", returnedObj.get(0)); //the jmp code is 19
		assertEquals("&label", ass.getObjProgram().get(1));
		assertEquals(2, ass.getObjProgram().size()); //only two lines: the command and the address

		ass.getObjProgram().clear(); //limpa o array
		
		//jn
		returnedObj = new ArrayList<>();
		ass = new Assembler();
		commandLine[0] = "jn";
		commandLine[1] = "label";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("17", returnedObj.get(0)); //the jn code is 20
		assertEquals("&label", ass.getObjProgram().get(1));
		assertEquals(2, ass.getObjProgram().size()); //only two lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array

		//jz
		returnedObj = new ArrayList<>();
		ass = new Assembler();
		commandLine[0] = "jz";
		commandLine[1] = "label";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("18", returnedObj.get(0)); //the jz code is 21
		assertEquals("&label", ass.getObjProgram().get(1));
		assertEquals(2, ass.getObjProgram().size()); //only two lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array
		
		// //sixth test: read
		// returnedObj = new ArrayList<>();
		// ass = new Assembler();
		// commandLine[0] = "read";
		// commandLine[1] = "address";
		// ass.proccessCommand(commandLine);
		// returnedObj = ass.getObjProgram();
		// assertEquals("5", returnedObj.get(0)); //the add code is 0
		// assertEquals("&address", ass.getObjProgram().get(1));
		// assertEquals(2, ass.getObjProgram().size()); //only two lines: the command and the address
		
		// //seventh test: store
		// returnedObj = new ArrayList<>();
		// ass = new Assembler();
		// commandLine[0] = "store";
		// commandLine[1] = "address";
		// ass.proccessCommand(commandLine);
		// returnedObj = ass.getObjProgram();
		// assertEquals("6", returnedObj.get(0)); //the add code is 0
		// assertEquals("&address", ass.getObjProgram().get(1));
		// assertEquals(2, ass.getObjProgram().size()); //only two lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array
		
		//Jeq
		commandLine[0] = "jeq";
		commandLine[1] = "%RPG0";
		commandLine[2] = "%RPG1";
		commandLine[3] = "var";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("19", returnedObj.get(0)); //the jeq code is 22
		assertEquals("%RPG0", ass.getObjProgram().get(1));
		assertEquals("%RPG1", ass.getObjProgram().get(2));
		assertEquals("&var", ass.getObjProgram().get(3));
		assertEquals(4, ass.getObjProgram().size()); //only four lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array

		//Jneq
		commandLine[0] = "jneq";
		commandLine[1] = "%RPG0";
		commandLine[2] = "%RPG1";
		commandLine[3] = "var";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("20", returnedObj.get(0)); //the jneq code is 23
		assertEquals("%RPG0", ass.getObjProgram().get(1));
		assertEquals("%RPG1", ass.getObjProgram().get(2));
		assertEquals("&var", ass.getObjProgram().get(3));
		assertEquals(4, ass.getObjProgram().size()); //only three lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array

		//Jgt
		commandLine[0] = "jgt";
		commandLine[1] = "%RPG0";
		commandLine[2] = "%RPG1";
		commandLine[3] = "var";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("21", returnedObj.get(0)); //the jlw code is 25
		assertEquals("%RPG0", ass.getObjProgram().get(1));
		assertEquals("%RPG1", ass.getObjProgram().get(2));
		assertEquals("&var", ass.getObjProgram().get(3));
		assertEquals(4, ass.getObjProgram().size()); //only four lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array

		//Jlw
		commandLine[0] = "jlw";
		commandLine[1] = "%RPG0";
		commandLine[2] = "%RPG1";
		commandLine[3] = "var";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("22", returnedObj.get(0)); //the jlw code is 25
		assertEquals("%RPG0", ass.getObjProgram().get(1));
		assertEquals("%RPG1", ass.getObjProgram().get(2));
		assertEquals("&var", ass.getObjProgram().get(3));
		assertEquals(4, ass.getObjProgram().size()); //only four lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array

		//Read
		commandLine[0] = "read";
		commandLine[1] = "var";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("23", returnedObj.get(0)); //the read code is 26
		assertEquals("&var", ass.getObjProgram().get(1));
		assertEquals(2, ass.getObjProgram().size()); //only two lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array

		//Store
		commandLine[0] = "store";
		commandLine[1] = "var";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("24", returnedObj.get(0)); //the store code is 27
		assertEquals("&var", ass.getObjProgram().get(1));
		assertEquals(2, ass.getObjProgram().size()); //only two lines: the command and the address
		
		ass.getObjProgram().clear(); //limpa o array

		//ldi
		commandLine[0] = "ldi";
		commandLine[1] = "4";
		ass.proccessCommand(commandLine);
		returnedObj = ass.getObjProgram();
		assertEquals("25", returnedObj.get(0)); //the ldi code is 28
		assertEquals("4", ass.getObjProgram().get(1));
		assertEquals(2, ass.getObjProgram().size()); //only two lines: the command and the address
		
		//final test: a small program
		//testing the following lines
		//sub adr1 %RPG0
		//add adr2 %RPG0
		//jmp label1
		//inc %RPG0
		//jn label2
		//ldi 86
		//move %RPG0 %RPG1
		//read adr3
		//let's start!!!
		returnedObj = new ArrayList<>();
		ass = new Assembler();
		commandLine[0] = "sub";
		commandLine[1] = "adr1";
		commandLine[2] = "%RPG0";
		ass.proccessCommand(commandLine);
		commandLine[0] = "add";
		commandLine[1] = "adr2";
		commandLine[2] = "%RPG0";
		ass.proccessCommand(commandLine);
		commandLine[0] = "jmp";
		commandLine[1] = "label1";
		ass.proccessCommand(commandLine);
		commandLine[0] = "inc";
		commandLine[1] = "%RPG0";
		ass.proccessCommand(commandLine);
		commandLine[0] = "jn";
		commandLine[1] = "label2";
		ass.proccessCommand(commandLine);
		commandLine[0] = "ldi";
		commandLine[1] = "86";
		ass.proccessCommand(commandLine);
		commandLine[0] = "move";
		commandLine[1] = "%RPG0";
		commandLine[2] = "%RPG1";
		ass.proccessCommand(commandLine);
		commandLine[0] = "read";
		commandLine[1] = "adr3";
		ass.proccessCommand(commandLine);
		//now, getting the object program
		returnedObj = ass.getObjProgram();
		assertEquals(19, returnedObj.size()); //the object program must have 19 lines
		
		assertEquals("5", returnedObj.get(0)); //the code of subMemReg is 5
		assertEquals("&adr1", returnedObj.get(1)); //the first parameter
		assertEquals("%RPG0", returnedObj.get(2)); //the second parameter
		
		assertEquals("1", returnedObj.get(3)); //the code of addMemReg is 1
		assertEquals("&adr2", returnedObj.get(4)); //the first parameter
		assertEquals("%RPG0", returnedObj.get(5)); //the second parameter
		
		assertEquals("16", returnedObj.get(6)); //the code of jmp is 16
		assertEquals("&label1", returnedObj.get(7)); //the parameter
		
		assertEquals("15", returnedObj.get(8)); //the code of inc is 15
		assertEquals("%RPG0", returnedObj.get(9)); //The parameter

		assertEquals("17", returnedObj.get(10)); //the code of jn is 17
		assertEquals("&label2", returnedObj.get(11)); //the parameter
		
		assertEquals("25", returnedObj.get(12)); //the code of ldi is 25
		assertEquals("86", returnedObj.get(13)); //the parameter
		
		assertEquals("13", returnedObj.get(14)); //the code of moveRegReg is 13
		assertEquals("%RPG0", returnedObj.get(15)); //the parameter
		assertEquals("%RPG1", returnedObj.get(16)); //the parameter
		
		assertEquals("23", returnedObj.get(17)); //the code of read is 23
		assertEquals("&adr3", returnedObj.get(18)); //the parameter
	}

	@Test
	public void testParse() {
		Assembler ass = new Assembler();
		ArrayList<String> returnedObj = new ArrayList<>();
		ArrayList<String> sourceProgram = new ArrayList<>();
		
		//inserting the following program
		/*
		 * var1
		 * var2
		 * var3
		 * ldi 10
		 * store var3
		 * ldi 2
		 * store var2
		 * ldi 0
		 * store var1
		 * label:
		 * read var1
		 * add var2 %RPG0
		 * store var1
		 * move %RPG1 %RPG0
		 * sub var3 %RPG0
		 * jn label
		 */
		sourceProgram.add("var1");
		sourceProgram.add("var2");
		sourceProgram.add("var3");
		sourceProgram.add("ldi 10");
		sourceProgram.add("store var3");
		sourceProgram.add("ldi 2");
		sourceProgram.add("store var2");
		sourceProgram.add("ldi 0");
		sourceProgram.add("store var1");
		sourceProgram.add("label:");
		sourceProgram.add("read var1");
		sourceProgram.add("add var2 %RPG0");
		sourceProgram.add("store var1");
		sourceProgram.add("move %RPG1 %RPG0");
		sourceProgram.add("sub var3 %RPG0");
		sourceProgram.add("jn label");
		
		//now we can generate the object program
		ass.setLines(sourceProgram);
		ass.parse();
		returnedObj = ass.getObjProgram();
		
		//testing
		assertEquals(27, returnedObj.size()); // sem contar labels
		
		//checking line by line
		assertEquals("25", returnedObj.get(0)); //the code of ldi is 25
		assertEquals("10", returnedObj.get(1)); //the parameter
		
		assertEquals("24", returnedObj.get(2)); //the code of store is 24
		assertEquals("&var3", returnedObj.get(3)); //the parameter
		
		assertEquals("25", returnedObj.get(4)); //the code of ldi is 25
		assertEquals("2", returnedObj.get(5)); //the parameter
		
		assertEquals("24", returnedObj.get(6)); //the code of store is 24
		assertEquals("&var2", returnedObj.get(7)); //the parameter
		
		assertEquals("25", returnedObj.get(8)); //the code of ldi is 25
		assertEquals("0", returnedObj.get(9)); //the parameter
		
		assertEquals("24", returnedObj.get(10)); //the code of store is 24
		assertEquals("&var1", returnedObj.get(11)); //the parameter
		
		assertEquals("23", returnedObj.get(12)); //the code of read is 23
		assertEquals("&var1", returnedObj.get(13)); //the parameter
		
		assertEquals("1", returnedObj.get(14)); //the code of addMemReg is 1
		assertEquals("&var2", returnedObj.get(15)); //the parameter
		assertEquals("%RPG0", returnedObj.get(16));

		assertEquals("24", returnedObj.get(17)); //the code of store is 24
		assertEquals("&var1", returnedObj.get(18)); //the parameter
		
		assertEquals("13", returnedObj.get(19)); //the code of moveRegReg is 13
		assertEquals("%RPG1", returnedObj.get(20)); //the parameter
		assertEquals("%RPG0", returnedObj.get(21)); //the parameter
		
		assertEquals("5", returnedObj.get(22)); //the code of subMemReg is 5
		assertEquals("&var3", returnedObj.get(23)); //the parameter
		assertEquals("%RPG0", returnedObj.get(24)); //the parameter

		assertEquals("17", returnedObj.get(25)); //the code of jn is 17
		assertEquals("&label", returnedObj.get(26)); //the parameter
		
		//now, checking if the label "label" was inserted, pointing to the position 12
		//the line 'read var1' is just after the label
		//once the command was inserted in the position 12, the label must 
		//be pointing to the position 12
		assertTrue(ass.getLabels().contains("label"));
		assertEquals(1, ass.getLabels().size());
		assertEquals(1, ass.getLabelsAddresses().size());
		assertEquals(0, ass.getLabels().indexOf("label"));
		assertEquals(12, (int) ass.getLabelsAddresses().get(0));
		
		//checking if all variables are stored in variables collection
		assertEquals("var1", ass.getVariables().get(0));
		assertEquals("var2", ass.getVariables().get(1));
		assertEquals("var3", ass.getVariables().get(2));
	}

	@Test
	public void testReplaceVariable() {
		
		Assembler ass = new Assembler();
		ArrayList<String> sampleexec = new ArrayList<>();
		
		//creating a fictional exec program with some variables
		sampleexec.add("9");
		sampleexec.add("&var1"); //var1 in the position 1
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("&var2"); //var2 in the position 5
		sampleexec.add("9");
		sampleexec.add("&var1"); //var1 in the position 7
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("&var3"); //var3 in the position 10
		sampleexec.add("9");
		sampleexec.add("&var3"); //var3 in the position 12
		sampleexec.add("9");
		sampleexec.add("&var1"); //var1 in the position 14
		
		//inserting this arraylist into the execprogram collections
		ass.setExecProgram(sampleexec);
		
		//now the test!
		//var1 must be replaced by position 100
		//var2 must be replaced by  position 99
		//var2 must be replaced by  position 98
		
		ass.replaceVariable("var1", 100);
		ass.replaceVariable("var2", 99);
		ass.replaceVariable("var3", 98);
		
		//getting the positions 
		//var1 is now the address 100. It must be found in positions 1, 7 and 14 
		assertEquals("100", ass.getExecProgram().get(1));
		assertEquals("100", ass.getExecProgram().get(7));
		assertEquals("100", ass.getExecProgram().get(14));
		
		//var2 is now the address 99. It must be found in positions 5 
		assertEquals("99", ass.getExecProgram().get(5));
		
		//var3 is now the address 98. It must be found in positions 10 and 12
		assertEquals("98", ass.getExecProgram().get(10));
		assertEquals("98", ass.getExecProgram().get(12));
		
	}
	
	@Test
	public void testReplaceLabels() {
		Assembler ass = new Assembler();
		ArrayList<String> sampleexec = new ArrayList<>();
		
		//creating a fictional exec program with some labels
		sampleexec.add("9");
		sampleexec.add("&label1"); //label1 in the position 1
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("&label2"); //label2 in the position 5
		sampleexec.add("9");
		sampleexec.add("&label1"); //label1 in the position 7
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("&label3"); //label3 in the position 10
		sampleexec.add("9");
		sampleexec.add("&label3"); //label3 in the position 12
		sampleexec.add("9");
		sampleexec.add("&label1"); //label1 in the position 14

		//inserting the labels
		ass.getLabels().add("label1");
		ass.getLabels().add("label2");
		ass.getLabels().add("label3");

		//and inserting the label addresses
		ass.getLabelsAddresses().add(17); //label 1 means position 17
		ass.getLabelsAddresses().add(42); //label 1 means position 42
		ass.getLabelsAddresses().add(63); //label 1 means position 63
		
		
		//inserting this arraylist we made above into the execprogram collections
		ass.setExecProgram(sampleexec);
		
		
		//now the test!
		ass.replaceLabels();
		//label1 (now refering to position 17) in positions 10 and 12
		assertEquals("17", ass.getExecProgram().get(1));
		assertEquals("17", ass.getExecProgram().get(7));
		assertEquals("17", ass.getExecProgram().get(14));
		
		//label2 (now refering to position 42) in positions 5
		assertEquals("42", ass.getExecProgram().get(5));
		
		//label3 (now refering to position 63) in positions 1, 7 and 14
		assertEquals("63", ass.getExecProgram().get(10));
		assertEquals("63", ass.getExecProgram().get(12));
	}

	@Test
	public void testReplaceAllVariables() {
		Assembler ass = new Assembler();
		ArrayList<String> sampleexec = new ArrayList<>();
		
		//creating a fictional exec program with some variables
		sampleexec.add("9");
		sampleexec.add("&var1"); //var1 in the position 1
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("&var2"); //var2 in the position 5
		sampleexec.add("9");
		sampleexec.add("&var1"); //var1 in the position 7
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("&var3"); //var3 in the position 10
		sampleexec.add("9");
		sampleexec.add("&var3"); //var3 in the position 12
		sampleexec.add("9");
		sampleexec.add("&var1"); //var1 in the position 14
		
		//inserting this arraylist into the execprogram collections
		ass.setExecProgram(sampleexec);
		
		//now inserting variables
		ass.getVariables().add("var1");
		ass.getVariables().add("var2");
		ass.getVariables().add("var3");
		
		//in this architecture, the memory size is 128
		//so, var1 must be replaced by 127, var2 by 126 and var3 by 125
		ass.replaceAllVariables();
		//var1 (now 127) is in lines 5, 7 and 14
		assertEquals("127", ass.getExecProgram().get(1));
		assertEquals("127", ass.getExecProgram().get(7));
		assertEquals("127", ass.getExecProgram().get(14));
		
		//var2 (now 126) is in line 5
		assertEquals("126", ass.getExecProgram().get(5));
		
		//var3 (now 125) is in lines 10 and 12
		assertEquals("125", ass.getExecProgram().get(10));
		assertEquals("125", ass.getExecProgram().get(12));
	}
	
	@Test
	public void testReplaceRegisters() {
		
		Assembler ass = new Assembler();
		ArrayList<String> sampleexec = new ArrayList<>();
		
		//creating a fictional exec program with some registers
		sampleexec.add("9");
		sampleexec.add("%RPG1"); //rpg1 in the position 1
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("%PC"); //pc is in position 4
		sampleexec.add("9");
		sampleexec.add("%RPG0"); //rpg0 is in position 6
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("%IR"); //ir in the position 9
		sampleexec.add("9");
		sampleexec.add("%RPG0"); //rpg0 in the position 11
		sampleexec.add("9");
		sampleexec.add("%RPG1"); //rpg1 in the position 13
		
		//inserting this arraylist into the execprogram collections
		ass.setExecProgram(sampleexec);
		
		//now the test!
		//RPG0 must be replaced by 0 
		//RPG1 must be replaced by 1
		//PC must be replaced by 6
		//IR must be replaced by 7
		
		ass.replaceRegisters();
		
		//getting the positions 
		//rpg0 is now the number 0. It must be found in positions 6 and 11
		assertEquals("0", ass.getExecProgram().get(6));
		assertEquals("0", ass.getExecProgram().get(11));
		
		//rpg1 is now the number 1. It must be found in positions 1 and 13 
		assertEquals("1", ass.getExecProgram().get(1));
		assertEquals("1", ass.getExecProgram().get(13));
		
		//pc is now the number 2. It must be found in position 4 
		assertEquals("6", ass.getExecProgram().get(4));

		//ir is now the number 3. It must be found in position 9 
		assertEquals("7", ass.getExecProgram().get(9));
		

		
	}
	
	@Test
	public void testCheckLabels() {
		Assembler ass = new Assembler();
		ArrayList<String> sampleexec = new ArrayList<>();
		
		//creating a fictional exec program with some variables
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&label1"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&label2"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&var1"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&label3"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&label3"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&var1"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&label1"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&var2"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&var1"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&label3"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&var3"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&label1"); 
		
		
		//case 1: some labels are not found
		
						//inserting the labels
						ass.getLabels().add("label1");
						ass.getLabels().add("label2");
						//label 3 is missing
						

								
						//now inserting variables
						ass.getVariables().add("var1");
						ass.getVariables().add("var2");
						ass.getVariables().add("var3");

		
						assertFalse(ass.checkLabels());

						
		//case 2: some variables are not found
						
						ass.getLabels().clear();
						ass.getVariables().clear();


						//inserting the labels
						ass.getLabels().add("label1");
						ass.getLabels().add("label2");
						ass.getLabels().add("label3");
				
								
						//now inserting variables
						ass.getVariables().add("var1");
						ass.getVariables().add("var2");
						//var3 is missing
		
						assertFalse(ass.checkLabels());
						
		//case 3: all variables and labels are found
						
						ass.getLabels().clear();
						ass.getVariables().clear();


						//inserting the labels
						ass.getLabels().add("label1");
						ass.getLabels().add("label2");
						ass.getLabels().add("label3");
				
								
						//now inserting variables
						ass.getVariables().add("var1");
						ass.getVariables().add("var2");
						ass.getVariables().add("var3");
		
						assertTrue(ass.checkLabels());
		
	}
	
	
	//@Test
	public void testRead() {
		fail("Not yet implemented");
	}
}
