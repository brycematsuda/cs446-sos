package sos;

import java.util.*;

/**
 * This class is the centerpiece of a simulation of the essential hardware of a
 * microcomputer.  This includes a processor chip, RAM and I/O devices.  It is
 * designed to demonstrate a simulated operating system (SOS).
 *
 *
 * @author Bryce Matsuda
 * @author Micah Alconcel
 *
 * @see RAM
 * @see SOS
 * @see Program
 * @see Sim
 */

public class CPU
{

	//======================================================================
	//Constants
	//----------------------------------------------------------------------

	//These constants define the instructions available on the chip
	public static final int SET    = 0;    /* set value of reg */
	public static final int ADD    = 1;    // put reg1 + reg2 into reg3
	public static final int SUB    = 2;    // put reg1 - reg2 into reg3
	public static final int MUL    = 3;    // put reg1 * reg2 into reg3
	public static final int DIV    = 4;    // put reg1 / reg2 into reg3
	public static final int COPY   = 5;    // copy reg1 to reg2
	public static final int BRANCH = 6;    // goto address in reg
	public static final int BNE    = 7;    // branch if not equal
	public static final int BLT    = 8;    // branch if less than
	public static final int POP    = 9;    // load value from stack
	public static final int PUSH   = 10;   // save value to stack
	public static final int LOAD   = 11;   // load value from heap
	public static final int SAVE   = 12;   // save value to heap
	public static final int TRAP   = 15;   // system call

	//These constants define the indexes to each register
	public static final int R0   = 0;     // general purpose registers
	public static final int R1   = 1;
	public static final int R2   = 2;
	public static final int R3   = 3;
	public static final int R4   = 4;
	public static final int PC   = 5;     // program counter
	public static final int SP   = 6;     // stack pointer
	public static final int BASE = 7;     // bottom of currently accessible RAM
	public static final int LIM  = 8;     // top of accessible RAM
	public static final int NUMREG = 9;   // number of registers

	//Misc constants
	public static final int NUMGENREG = PC; // the number of general registers
	public static final int INSTRSIZE = 4;  // number of ints in a single instr +
	// args.  (Set to a fixed value for simplicity.)

	//======================================================================
	//Member variables
	//----------------------------------------------------------------------
	/**
	 * specifies whether the CPU should output details of its work
	 **/
	private boolean m_verbose = false;

	/**
	 * This array contains all the registers on the "chip".
	 **/
	private int m_registers[];

	/**
	 * A pointer to the RAM used by this CPU
	 *
	 * @see RAM
	 **/
	private RAM m_RAM = null;

	//======================================================================
	//Methods
	//----------------------------------------------------------------------

	/**
	 * CPU ctor
	 *
	 * Intializes all member variables.
	 */
	public CPU(RAM ram)
	{
		m_registers = new int[NUMREG];
		for(int i = 0; i < NUMREG; i++)
		{
			m_registers[i] = 0;
		}
		m_RAM = ram;

	}//CPU ctor

	/**
	 * getPC
	 *
	 * @return the value of the program counter
	 */
	public int getPC()
	{
		return m_registers[PC];
	}

	/**
	 * getSP
	 *
	 * @return the value of the stack pointer
	 */
	public int getSP()
	{
		return m_registers[SP];
	}

	/**
	 * getBASE
	 *
	 * @return the value of the base register
	 */
	public int getBASE()
	{
		return m_registers[BASE];
	}

	/**
	 * getLIMIT
	 *
	 * @return the value of the limit register
	 */
	public int getLIM()
	{
		return m_registers[LIM];
	}

	/**
	 * getRegisters
	 *
	 * @return the registers
	 */
	public int[] getRegisters()
	{
		return m_registers;
	}

	/**
	 * setPC
	 *
	 * @param v the new value of the program counter
	 */
	public void setPC(int v)
	{
		m_registers[PC] = v;
	}

	/**
	 * setSP
	 *
	 * @param v the new value of the stack pointer
	 */
	public void setSP(int v)
	{
		m_registers[SP] = v;
	}

	/**
	 * setBASE
	 *
	 * @param v the new value of the base register
	 */
	public void setBASE(int v)
	{
		m_registers[BASE] = v;
	}

	/**
	 * setLIM
	 *
	 * @param v the new value of the limit register
	 */
	public void setLIM(int v)
	{
		m_registers[LIM] = v;
	}

	/**
	 * regDump
	 *
	 * Prints the values of the registers.  Useful for debugging.
	 */
	private void regDump()
	{
		for(int i = 0; i < NUMGENREG; i++)
		{
			System.out.print("r" + i + "=" + m_registers[i] + " ");
		}//for
		System.out.print("PC=" + m_registers[PC] + " ");
		System.out.print("SP=" + m_registers[SP] + " ");
		System.out.print("BASE=" + m_registers[BASE] + " ");
		System.out.print("LIM=" + m_registers[LIM] + " ");
		System.out.println("");
	}//regDump

	/**
	 * printIntr
	 *
	 * Prints a given instruction in a user readable format.  Useful for
	 * debugging.
	 *
	 * @param instr the current instruction
	 */
	public static void printInstr(int[] instr)
	{
		switch(instr[0])
		{
		case SET:
			System.out.println("SET R" + instr[1] + " = " + instr[2]);
			break;
		case ADD:
			System.out.println("ADD R" + instr[1] + " = R" + instr[2] + " + R" + instr[3]);
			break;
		case SUB:
			System.out.println("SUB R" + instr[1] + " = R" + instr[2] + " - R" + instr[3]);
			break;
		case MUL:
			System.out.println("MUL R" + instr[1] + " = R" + instr[2] + " * R" + instr[3]);
			break;
		case DIV:
			System.out.println("DIV R" + instr[1] + " = R" + instr[2] + " / R" + instr[3]);
			break;
		case COPY:
			System.out.println("COPY R" + instr[1] + " = R" + instr[2]);
			break;
		case BRANCH:
			System.out.println("BRANCH @" + instr[1]);
			break;
		case BNE:
			System.out.println("BNE (R" + instr[1] + " != R" + instr[2] + ") @" + instr[3]);
			break;
		case BLT:
			System.out.println("BLT (R" + instr[1] + " < R" + instr[2] + ") @" + instr[3]);
			break;
		case POP:
			System.out.println("POP R" + instr[1]);
			break;
		case PUSH:
			System.out.println("PUSH R" + instr[1]);
			break;
		case LOAD:
			System.out.println("LOAD R" + instr[1] + " <-- @R" + instr[2]);
			break;
		case SAVE:
			System.out.println("SAVE R" + instr[1] + " --> @R" + instr[2]);
			break;
		case TRAP:
			System.out.print("TRAP ");
			break;
		default:        // should never be reached
			System.out.println("?? ");
			break;          
		}//switch

	}//printInstr


	/**
	 * accessCheck
	 * 
	 * Check if a physical address being accessed is within the range of 
	 * the base and limit registers
	 * 
	 * @param addr the physical address to check
	 * @return true if it is within range, false if not
	 * 
	 */
	public boolean accessCheck(int addr){

		int base = getBASE();
		int limit = getLIM();

		if (addr >= base && addr <= limit){
			return true;
		} else {
			System.out.println("ERROR: Trying to access address that is out of range.");
			return false;
		}
	}

	/**
	 * pushHelper
	 * 
	 * Pushes a value to the stack.
	 * 
	 * @param val the value to push to the stack
	 */
	public void pushHelper(int val){
		int sp = getSP();
		if (accessCheck(sp)){
			// We push to the stack by pushing the SP down.
			setSP(sp - 1);
			m_RAM.write(sp - 1, val);
		}
	}

	/**
	 * 
	 * popHelper
	 * 
	 * Pops the value from the top of the stack
	 * 
	 * @return the top value from the stack or -1 if an error occurs.
	 */
	public int popHelper(){
		int sp = getSP();
		if(accessCheck(sp)){
			int tosValue = m_RAM.read(sp); // Get value from top of stack 
			setSP(sp + 1); // Move stack pointer back up to "pop" stack
			return tosValue;
		}
		return -1;
	}


	/**
	 * 
	 * run()
	 * 
	 * Runs simulation CPU, decoding instructions recieved from RAM.
	 * 
	 */
	public void run()
	{
		while(true) {

			// Get current PC, base, and instructions loaded in RAM.
			int pc = getPC();
			int base = getBASE();

			int[] currInst = m_RAM.fetch(pc);
			int opcode = currInst[0];

			// Move PC to the next instruction, or return if we're outside the limits.
			if (accessCheck(pc + INSTRSIZE)) setPC(pc + INSTRSIZE);
			else return;

			// Force verbose on for debug.
			m_verbose = true;

			if (m_verbose){
				// Debug dump
				regDump();
				printInstr(currInst);
			}

			// Execute current instruction based on opcode
			//
			// m_registers[currInst[1]]: arg1 register 
			// m_registers[currInst[2]]: arg2 register
			// m_registers[currInst[3]]: arg3 register
			//
			// currInst[1]: arg1 literal 
			// currInst[2]: arg2 literal 
			// currInst[3]: arg3 literal

			switch(opcode) {
			case SET:
				m_registers[currInst[1]] = currInst[2];
				break;
			case ADD:
				m_registers[currInst[1]] = m_registers[currInst[2]] + m_registers[currInst[3]];
				break;
			case SUB:
				m_registers[currInst[1]] = m_registers[currInst[2]] - m_registers[currInst[3]];
				break;
			case MUL:
				m_registers[currInst[1]] = m_registers[currInst[2]] * m_registers[currInst[3]];
				break;
			case DIV:
				m_registers[currInst[1]] = m_registers[currInst[2]] / m_registers[currInst[3]];
				break;
			case COPY:
				m_registers[currInst[1]] = m_registers[currInst[2]];
				break;
			case BRANCH:
				// If we're jumping to another address, make sure it is within range first.
				if (accessCheck(currInst[1] + base))
					setPC(currInst[1] + base);
				break;
			case BNE:
				if ((m_registers[currInst[1]] != m_registers[currInst[2]]) 
						&& accessCheck(currInst[3] + base)) 
					setPC(currInst[3] + base);
				break;
			case BLT:
				if ((m_registers[currInst[1]] < m_registers[currInst[2]]) 
						&& accessCheck(currInst[3] + base))
					setPC(currInst[3] + base);
				break;
			case POP:
				m_registers[currInst[1]] = popHelper();
				break;
			case PUSH:
				pushHelper(m_registers[currInst[1]]);
				break;
			case LOAD:
				if (accessCheck(m_registers[currInst[2]] + base)) 
					m_registers[currInst[1]] = m_RAM.read(m_registers[currInst[2]] + base);
				break;
			case SAVE:
				if (accessCheck(m_registers[currInst[2]] + base))
					m_RAM.write(m_registers[currInst[2]] + base, m_registers[currInst[1]]);
				break;
			case TRAP:
				return;
			default: // should never be reached
				// 404 INSTRUCTION NOT FOUND, ABORT ABORT 
				return;       
			}//switch
		}
	}//run

};//class CPU
