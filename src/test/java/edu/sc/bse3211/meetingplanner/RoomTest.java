package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Unit tests for {@link Room}.
 *
 * <h2>Test strategy</h2>
 * <p>{@code Room} mirrors {@link Person} structurally — it wraps a
 * {@link Calendar} and delegates all operations to it.  The tests therefore
 * follow the same structure as {@link PersonTest} with one important
 * distinction: the conflict exception message must identify the <em>room</em>
 * rather than a person.</p>
 *
 * <p>Areas covered:</p>
 * <ol>
 *   <li><b>Construction</b> — room ID is stored correctly by both constructors.</li>
 *   <li><b>Meeting management</b> — valid addition; conflict detection with
 *       room ID in the exception message.</li>
 *   <li><b>Availability queries</b> — {@code isBusy} for booked, free, and
 *       adjacent slots.</li>
 *   <li><b>Retrieval and removal</b> — round-trip get and remove.</li>
 *   <li><b>Agenda printing</b> — header format for both overloads.</li>
 * </ol>
 */
public class RoomTest {

	// -----------------------------------------------------------------------
	// Constructors
	// -----------------------------------------------------------------------

	/**
	 * The parametrised constructor must store the room ID so that
	 * {@code getID()} returns it unchanged.
	 */
	@Test
	public void testConstructor_setsID() {
		Room room = new Room("LLT6A");
		assertEquals("getID should return the ID given at construction",
				"LLT6A", room.getID());
	}

	/**
	 * The default constructor must initialise the ID to an empty string
	 * rather than {@code null}.
	 */
	@Test
	public void testDefaultConstructor_emptyID() {
		Room room = new Room();
		assertEquals("Default constructor should set ID to empty string",
				"", room.getID());
	}

	// -----------------------------------------------------------------------
	// addMeeting
	// -----------------------------------------------------------------------

	/**
	 * Adding a meeting with a valid date and non-overlapping time window must
	 * complete without throwing any exception.
	 */
	@Test
	public void testAddMeeting_validMeeting_noException() {
		Room room = new Room("LAB2");
		try {
			room.addMeeting(new Meeting(4, 10, 9, 11));
		} catch (TimeConflictException e) {
			fail("Adding a valid meeting should not throw: " + e.getMessage());
		}
	}

	/**
	 * Adding a second meeting that overlaps an existing one must throw
	 * {@link TimeConflictException}.  The exception message must contain the
	 * room's ID so that callers can identify which room caused the conflict.
	 */
	@Test
	public void testAddMeeting_conflict_exceptionContainsRoomID() {
		Room room = new Room("LLT3A");
		try {
			room.addMeeting(new Meeting(4, 10, 9, 12));
			room.addMeeting(new Meeting(4, 10, 11, 14)); // start=11 overlaps 9-12
			fail("Should have thrown TimeConflictException for overlapping meetings");
		} catch (TimeConflictException e) {
			assertTrue("Exception message should include the room ID",
					e.getMessage().contains("LLT3A"));
		}
	}

	// -----------------------------------------------------------------------
	// isBusy
	// -----------------------------------------------------------------------

	/**
	 * After a meeting has been added, the room should be reported as busy
	 * during that exact time slot.
	 */
	@Test
	public void testIsBusy_afterAddMeeting_returnsTrue() {
		Room room = new Room("LLT2C");
		try {
			room.addMeeting(new Meeting(4, 10, 14, 16));
			assertTrue("Room should be busy in the booked slot",
					room.isBusy(4, 10, 14, 16));
		} catch (TimeConflictException e) {
			fail("Should not throw: " + e.getMessage());
		}
	}

	/**
	 * A room with no meetings must be reported as free for any valid slot.
	 */
	@Test
	public void testIsBusy_emptyCalendar_returnsFalse() {
		Room room = new Room("LLT6B");
		try {
			assertFalse("Room with no meetings should not be busy",
					room.isBusy(4, 10, 14, 16));
		} catch (TimeConflictException e) {
			fail("Should not throw: " + e.getMessage());
		}
	}

	/**
	 * A time slot that does not overlap the booked slot must still be free.
	 * <p>Booked: 09:00-11:00. Queried: 13:00-15:00.</p>
	 */
	@Test
	public void testIsBusy_differentSlot_returnsFalse() {
		Room room = new Room("LLT6B");
		try {
			room.addMeeting(new Meeting(4, 10, 9, 11));
			assertFalse("Room should be free outside the booked slot",
					room.isBusy(4, 10, 13, 15));
		} catch (TimeConflictException e) {
			fail("Should not throw: " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------
	// getMeeting / removeMeeting
	// -----------------------------------------------------------------------

	/**
	 * {@code getMeeting} at index 0 must return the meeting that was added,
	 * with its description and start time intact.
	 */
	@Test
	public void testGetMeeting_returnsCorrectMeeting() {
		Room room = new Room("LLT6A");
		try {
			Meeting meeting = new Meeting(5, 5, 8, 10);
			meeting.setDescription("Lecture");
			room.addMeeting(meeting);
			Meeting retrieved = room.getMeeting(5, 5, 0);
			assertEquals("Description should match", "Lecture", retrieved.getDescription());
			assertEquals("Start time should match", 8, retrieved.getStartTime());
		} catch (TimeConflictException e) {
			fail("Should not throw: " + e.getMessage());
		}
	}

	/**
	 * After {@code removeMeeting}, the previously booked slot must be
	 * reported as free.
	 */
	@Test
	public void testRemoveMeeting_roomBecomesAvailable() {
		Room room = new Room("LLT6A");
		try {
			room.addMeeting(new Meeting(5, 5, 8, 10));
			room.removeMeeting(5, 5, 0);
			assertFalse("Room should be free after removing the only meeting",
					room.isBusy(5, 5, 8, 10));
		} catch (TimeConflictException e) {
			fail("Should not throw: " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------
	// printAgenda
	// -----------------------------------------------------------------------

	/**
	 * The day-specific agenda header must include the month and day numbers
	 * in {@code month/day} format.
	 */
	@Test
	public void testPrintAgenda_day_containsHeader() {
		Room room = new Room("LLT6A");
		String agenda = room.printAgenda(8, 20);
		assertTrue("Day agenda should include the month/day header",
				agenda.contains("8/20"));
	}

	/**
	 * The monthly agenda header must include the month number.
	 */
	@Test
	public void testPrintAgenda_month_containsHeader() {
		Room room = new Room("LLT6A");
		String agenda = room.printAgenda(8);
		assertTrue("Monthly agenda should include the month number in its header",
				agenda.contains("8"));
	}
}
