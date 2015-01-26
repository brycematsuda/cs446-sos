package sos;

import java.util.*;


/**
 * This class contains the simulated operating system (SOS).  Realistically it
 * would run on the same processor (CPU) that it is managing but instead it uses
 * the real-world processor in order to allow a focus on the essentials of
 * operating system design using a high level programming language.
 * 
 * @author Bryce Matsuda
 * @author Micah Alconcel
 *
 */

public class SOS
{
	//======================================================================
	//Member variables
	//----------------------------------------------------------------------

	/**
	 * This flag causes the SOS to print lots of potentially helpful
	 * status messages
	 **/
	public static final boolean m_verbose = false;

	/**
	 * The CPU the operating system is managing.
	 **/
	private CPU m_CPU = null;

	/**
	 * The RAM attached to the CPU.
	 **/
	private RAM m_RAM = null;

	/*======================================================================
	 * Constructors & Debugging
	 *----------------------------------------------------------------------
	 */

	/**
	 * The constructor does nothing special
	 */
	public SOS(CPU c, RAM r)
	{
		//Init member list
		m_CPU = c;
		m_RAM = r;
	}//SOS ctor

	/**
	 * Does a System.out.print as long as m_verbose is true
	 **/
	public static void debugPrint(String s)
	{
		if (m_verbose)
		{
			System.out.print(s);
		}
	}

	/**
	 * Does a System.out.println as long as m_verbose is true
	 **/
	public static void debugPrintln(String s)
	{
		if (m_verbose)
		{
			System.out.println(s);
		}
	}

	/*======================================================================
	 * Memory Block Management Methods
	 *----------------------------------------------------------------------
	 */

	//None yet!

	/*======================================================================
	 * Device Management Methods
	 *----------------------------------------------------------------------
	 */

	//None yet!

	/*======================================================================
	 * Process Management Methods
	 *----------------------------------------------------------------------
	 */

	//None yet!

	/*======================================================================
	 * Program Management Methods
	 *----------------------------------------------------------------------
	 */

	/**
	 * createProcess
	 * 
	 * Runs a Pidgin assembly program on a simulated CPU.
	 * 
	 * @param prog the Pidgin assembly program to run
	 * @param allocSize the amount of memory to be allocated
	 */
	public void createProcess(Program prog, int allocSize)
	{
		// Compile program into int array
		int[] program = prog.export();

		// Setup CPU base and limit registers along with PC and SP.
		m_CPU.setBASE(10);
		int base = m_CPU.getBASE();

		m_CPU.setLIM(base + allocSize);
		int limit = m_CPU.getLIM();

		m_CPU.setPC(base);
		m_CPU.setSP(limit);

		// Copy entire program into simulated RAM.
		for (int x = 0; x < program.length; x++)
		{

			m_RAM.write(x + base, program[x]);
		}        
	}//createProcess

	/*======================================================================
	 * Interrupt Handlers
	 *----------------------------------------------------------------------
	 */

	//None yet!

	/*======================================================================
	 * System Calls
	 *----------------------------------------------------------------------
	 */

	//None yet!

};//class SOS
