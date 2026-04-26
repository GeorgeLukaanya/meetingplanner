package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;

/**
 * Unit tests for {@link Meeting}.
 *
 * <h2>Test strategy</h2>
 * <p>Tests cover:</p>
 * <ol>
 *   <li><b>Constructors</b> — each of the five constructors is tested to
 *       confirm that every field is set to its expected value and that the
 *       attendees list is never left null.</li>
 *   <li><b>Setters</b> — all setters are exercised via a single round-trip
 *       test.</li>
 *   <li><b>toString</b> — normal output is verified; null-safety for a missing
 *       room and an empty attendees list is confirmed (bugs fixed in this
 *       iteration).</li>
 *   <li><b>addAttendee / removeAttendee</b> — list mutation is verified,
 *       including that partial constructors no longer leave the list null.</li>
 * </ol>
 */
public class MeetingTest {

	// -----------------------------------------------------------------------
	// Constructors
	// -----------------------------------------------------------------------

	/**
	 * The 2-arg constructor should set month and day, default start to 0 and
	 * end to 23 (all-day block), and initialise the attendees list.
	 */
	@Test
	public void testConstructor_twoArg_setsMonthAndDay() {
		Meeting m = new Meeting(5, 20);
		assertEquals("Month should be 5", 5, m.getMonth());
		assertEquals("Day should be 20", 20, m.getDay());
		assertEquals("Start should default to 0", 0, m.getStartTime());
		assertEquals("End should default to 23", 23, m.getEndTime());
		assertNotNull("Attendees list must be initialised by constructor", m.getAttendees());
	}

	/**
	 * The 3-arg constructor (month, day, description) should work identically
	 * to the 2-arg constructor while also storing the description.
	 */
	@Test
	public void testConstructor_threeArg_setsDescription() {
		Meeting m = new Meeting(5, 20, "Public Holiday");
		assertEquals("Month should be 5", 5, m.getMonth());
		assertEquals("Day should be 20", 20, m.getDay());
		assertEquals("Start should default to 0", 0, m.getStartTime());
		assertEquals("End should default to 23", 23, m.getEndTime());
		assertEquals("Description should be set", "Public Holiday", m.getDescription());
		assertNotNull("Attendees list must be initialised", m.getAttendees());
	}

	/**
	 * The 4-arg constructor should set month, day, start, and end correctly.
	 * The attendees list must still be initialised even though it is not
	 * passed as a parameter.
	 */
	@Test
	public void testConstructor_fourArg_setsTimes() {
		Meeting m = new Meeting(7, 4, 10, 12);
		assertEquals("Month should be 7", 7, m.getMonth());
		assertEquals("Day should be 4", 4, m.getDay());
		assertEquals("Start should be 10", 10, m.getStartTime());
		assertEquals("End should be 12", 12, m.getEndTime());
		assertNotNull("Attendees list must be initialised", m.getAttendees());
	}

	/**
	 * The full 7-arg constructor should set every field, including the
	 * attendees list and room reference.
	 */
	@Test
	public void testConstructor_full_setsAllFields() {
		ArrayList<Person> attendees = new ArrayList<>();
		attendees.add(new Person("Alice"));
		Room room = new Room("LLT6A");
		Meeting m = new Meeting(7, 4, 10, 12, attendees, room, "Team Meeting");

		assertEquals(7, m.getMonth());
		assertEquals(4, m.getDay());
		assertEquals(10, m.getStartTime());
		assertEquals(12, m.getEndTime());
		assertEquals("Team Meeting", m.getDescription());
		assertNotNull("Attendees should not be null", m.getAttendees());
		assertEquals("Should have one attendee", 1, m.getAttendees().size());
		assertEquals("Attendee name should match", "Alice", m.getAttendees().get(0).getName());
		assertNotNull("Room should not be null", m.getRoom());
		assertEquals("Room ID should match", "LLT6A", m.getRoom().getID());
	}

	// -----------------------------------------------------------------------
	// Setters
	// -----------------------------------------------------------------------

	/**
	 * All setters should update the corresponding field so that the subsequent
	 * getter returns the new value.
	 */
	@Test
	public void testSetters_updateAllFields() {
		Meeting m = new Meeting();
		m.setMonth(8);
		m.setDay(15);
		m.setStartTime(9);
		m.setEndTime(11);
		m.setDescription("Updated");
		m.setRoom(new Room("RoomX"));

		assertEquals(8, m.getMonth());
		assertEquals(15, m.getDay());
		assertEquals(9, m.getStartTime());
		assertEquals(11, m.getEndTime());
		assertEquals("Updated", m.getDescription());
		assertEquals("RoomX", m.getRoom().getID());
	}

	// -----------------------------------------------------------------------
	// toString
	// -----------------------------------------------------------------------

	/**
	 * A fully-constructed meeting should produce a formatted string that
	 * includes the date, time window, room ID, description, and attendee name.
	 */
	@Test
	public void testToString_fullMeeting_formattedCorrectly() {
		ArrayList<Person> attendees = new ArrayList<>();
		attendees.add(new Person("Alice"));
		Room room = new Room("Room101");
		Meeting m = new Meeting(3, 15, 9, 11, attendees, room, "Planning");

		String result = m.toString();
		assertTrue("toString should contain month/day", result.contains("3/15"));
		assertTrue("toString should contain start time", result.contains("9"));
		assertTrue("toString should contain end time", result.contains("11"));
		assertTrue("toString should contain room ID", result.contains("Room101"));
		assertTrue("toString should contain description", result.contains("Planning"));
		assertTrue("toString should contain attendee name", result.contains("Alice"));
	}

	/**
	 * When no room is set, {@code toString} must substitute the literal
	 * {@code "No Room"} rather than throwing {@link NullPointerException}.
	 * This is a regression test for a bug in the original implementation.
	 */
	@Test
	public void testToString_nullRoom_usesDefaultString() {
		Meeting m = new Meeting(3, 15, 9, 11); // room is null
		String result = m.toString();
		assertNotNull("toString should not return null", result);
		assertTrue("toString should substitute 'No Room' when room is null",
				result.contains("No Room"));
	}

	/**
	 * When the attendees list is empty, {@code toString} must still produce a
	 * valid string without throwing.
	 */
	@Test
	public void testToString_emptyAttendees_noException() {
		Meeting m = new Meeting(3, 15, 9, 11);
		m.setRoom(new Room("Room101")); // room set; attendees list is empty but not null
		String result = m.toString();
		assertNotNull("toString should not return null", result);
		assertTrue("toString should still contain date info", result.contains("3/15"));
	}

	// -----------------------------------------------------------------------
	// addAttendee / removeAttendee
	// -----------------------------------------------------------------------

	/**
	 * {@code addAttendee} must work on a meeting created via the full
	 * constructor because its attendees list is supplied externally.
	 */
	@Test
	public void testAddAttendee_toInitialisedList_succeeds() {
		ArrayList<Person> attendees = new ArrayList<>();
		Room room = new Room("R1");
		Meeting m = new Meeting(3, 15, 9, 11, attendees, room, "Demo");

		m.addAttendee(new Person("Bob"));
		assertEquals("Attendee list should have one entry", 1, m.getAttendees().size());
		assertEquals("Bob", m.getAttendees().get(0).getName());
	}

	/**
	 * Regression test: the original partial constructors left {@code attendees}
	 * as {@code null}, causing {@link NullPointerException} on
	 * {@code addAttendee}.  The constructors now initialise the list, so this
	 * call must succeed.
	 */
	@Test
	public void testAddAttendee_afterPartialConstructor_succeeds() {
		Meeting m = new Meeting(3, 15, 9, 11); // attendees initialised by constructor
		m.addAttendee(new Person("Alice"));
		assertNotNull("Attendees list should be initialised", m.getAttendees());
		assertEquals("Should have one attendee after add", 1, m.getAttendees().size());
		assertEquals("Alice", m.getAttendees().get(0).getName());
	}

	/**
	 * {@code removeAttendee} must decrease the list size by one when the
	 * specified person is present.
	 */
	@Test
	public void testRemoveAttendee_reducesListSize() {
		ArrayList<Person> attendees = new ArrayList<>();
		Person alice = new Person("Alice");
		attendees.add(alice);
		Room room = new Room("R1");
		Meeting m = new Meeting(3, 15, 9, 11, attendees, room, "Review");

		m.removeAttendee(alice);
		assertEquals("Attendee list should be empty after removal", 0, m.getAttendees().size());
	}
}
