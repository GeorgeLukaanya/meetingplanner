package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Unit tests for {@link Organization}.
 *
 * <h2>Test strategy</h2>
 * <p>{@code Organization} acts as a directory — it holds a fixed list of
 * pre-configured {@link Person employees} and {@link Room rooms} and provides
 * lookup operations for both.  Tests verify:</p>
 * <ol>
 *   <li><b>List initialisation</b> — the employee and room lists are non-null
 *       and contain exactly the five entries defined in the constructor.</li>
 *   <li><b>Room lookup</b> — every known room ID is found and returned with
 *       the correct ID; an unknown ID must throw a descriptive
 *       {@link Exception}.</li>
 *   <li><b>Employee lookup</b> — every known name is found and returned with
 *       the correct name; an unknown name must throw; and lookups are
 *       case-sensitive.</li>
 * </ol>
 *
 * <p>Known employees: Namugga Martha, Shema Collins, Acan Brenda, Kazibwe Julius,
 * Kukunda Lynn.</p>
 * <p>Known rooms: LLT6A, LLT6B, LLT3A, LLT2C, LAB2.</p>
 */
public class OrganizationTest {

	// -----------------------------------------------------------------------
	// List initialisation
	// -----------------------------------------------------------------------

	/**
	 * The employee list must be non-null immediately after construction.
	 */
	@Test
	public void testGetEmployees_returnsNonNullList() {
		Organization org = new Organization();
		assertNotNull("Employee list should not be null", org.getEmployees());
	}

	/**
	 * The default constructor must add exactly five employees.
	 */
	@Test
	public void testGetEmployees_hasFiveMembers() {
		Organization org = new Organization();
		assertEquals("Organization should have exactly 5 employees",
				5, org.getEmployees().size());
	}

	/**
	 * The room list must be non-null immediately after construction.
	 */
	@Test
	public void testGetRooms_returnsNonNullList() {
		Organization org = new Organization();
		assertNotNull("Room list should not be null", org.getRooms());
	}

	/**
	 * The default constructor must add exactly five rooms.
	 */
	@Test
	public void testGetRooms_hasFiveRooms() {
		Organization org = new Organization();
		assertEquals("Organization should have exactly 5 rooms",
				5, org.getRooms().size());
	}

	// -----------------------------------------------------------------------
	// Room lookup
	// -----------------------------------------------------------------------

	/**
	 * {@code getRoom("LLT6A")} must return a non-null {@link Room} whose
	 * {@code getID()} equals the requested ID.
	 */
	@Test
	public void testGetRoom_existingRoom_returnsCorrectRoom() {
		Organization org = new Organization();
		try {
			Room room = org.getRoom("LLT6A");
			assertNotNull("Should return a non-null Room for a known ID", room);
			assertEquals("Returned room should have the requested ID",
					"LLT6A", room.getID());
		} catch (Exception e) {
			fail("Should not throw for a known room ID: " + e.getMessage());
		}
	}

	/**
	 * Every one of the five pre-defined room IDs must be retrievable, and
	 * each returned room's {@code getID()} must match the requested ID.
	 */
	@Test
	public void testGetRoom_allKnownRooms_returnSuccessfully() {
		Organization org = new Organization();
		String[] knownIDs = {"LLT6A", "LLT6B", "LLT3A", "LLT2C", "LAB2"};
		for (String id : knownIDs) {
			try {
				Room room = org.getRoom(id);
				assertEquals("Room ID should match the requested ID", id, room.getID());
			} catch (Exception e) {
				fail("Should not throw for known room ID '" + id + "': " + e.getMessage());
			}
		}
	}

	/**
	 * Requesting a room ID that does not exist must throw an {@link Exception}
	 * whose message communicates that the room was not found.
	 */
	@Test
	public void testGetRoom_nonExistentRoom_throwsException() {
		Organization org = new Organization();
		try {
			org.getRoom("NONEXISTENT_ROOM");
			fail("Should have thrown an exception for a non-existent room");
		} catch (Exception e) {
			assertNotNull("Exception message should not be null", e.getMessage());
			assertTrue("Exception message should mention the room not existing",
					e.getMessage().toLowerCase().contains("not exist"));
		}
	}

	// -----------------------------------------------------------------------
	// Employee lookup
	// -----------------------------------------------------------------------

	/**
	 * {@code getEmployee("Namugga Martha")} must return a non-null
	 * {@link Person} whose {@code getName()} equals the requested name.
	 */
	@Test
	public void testGetEmployee_existingEmployee_returnsCorrectPerson() {
		Organization org = new Organization();
		try {
			Person employee = org.getEmployee("Namugga Martha");
			assertNotNull("Should return a non-null Person for a known name", employee);
			assertEquals("Returned person should have the requested name",
					"Namugga Martha", employee.getName());
		} catch (Exception e) {
			fail("Should not throw for a known employee name: " + e.getMessage());
		}
	}

	/**
	 * Every one of the five pre-defined employee names must be retrievable.
	 */
	@Test
	public void testGetEmployee_allKnownEmployees_returnSuccessfully() {
		Organization org = new Organization();
		String[] knownNames = {
			"Namugga Martha", "Shema Collins", "Acan Brenda",
			"Kazibwe Julius", "Kukunda Lynn"
		};
		for (String name : knownNames) {
			try {
				Person employee = org.getEmployee(name);
				assertEquals("Employee name should match the requested name",
						name, employee.getName());
			} catch (Exception e) {
				fail("Should not throw for known employee '" + name + "': " + e.getMessage());
			}
		}
	}

	/**
	 * Requesting an employee name that does not exist must throw an
	 * {@link Exception} whose message communicates that the employee was not
	 * found.
	 */
	@Test
	public void testGetEmployee_nonExistentEmployee_throwsException() {
		Organization org = new Organization();
		try {
			org.getEmployee("Nobody Here");
			fail("Should have thrown an exception for a non-existent employee");
		} catch (Exception e) {
			assertNotNull("Exception message should not be null", e.getMessage());
			assertTrue("Exception message should mention the employee not existing",
					e.getMessage().toLowerCase().contains("not exist"));
		}
	}

	/**
	 * Employee lookup must be case-sensitive: the name "namugga martha"
	 * (all lower-case) must not match "Namugga Martha".
	 */
	@Test
	public void testGetEmployee_caseSensitive_throwsForWrongCase() {
		Organization org = new Organization();
		try {
			org.getEmployee("namugga martha");
			fail("Lookup should be case-sensitive and throw for wrong case");
		} catch (Exception e) {
			// Expected — correct-case name is "Namugga Martha"
		}
	}
}
