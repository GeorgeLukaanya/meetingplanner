package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Unit tests for {@link Person}.
 *
 * <h2>Test strategy</h2>
 * <p>{@code Person} is a thin wrapper around {@link Calendar}, so the tests
 * here focus on:</p>
 * <ol>
 *   <li><b>Construction</b> — the name is stored correctly by both the
 *       parametrised and default constructors.</li>
 *   <li><b>Meeting management</b> — adding a valid meeting succeeds; adding an
 *       overlapping meeting throws {@link TimeConflictException} with a message
 *       that names the person, making error diagnosis straightforward.</li>
 *   <li><b>Availability queries</b> — {@code isBusy} returns the right answer
 *       for booked, free, and adjacent time slots.</li>
 *   <li><b>Retrieval and removal</b> — {@code getMeeting} returns the correct
 *       object; {@code removeMeeting} frees the slot.</li>
 *   <li><b>Agenda printing</b> — {@code printAgenda} headers are verified for
 *       both day and month overloads.</li>
 * </ol>
 */
public class PersonTest {

	// -----------------------------------------------------------------------
	// Constructors
	// -----------------------------------------------------------------------

	/**
	 * The parametrised constructor must store the supplied name so that
	 * {@code getName()} returns it unchanged.
	 */
	@Test
	public void testConstructor_setsName() {
		Person person = new Person("Alice");
		assertEquals("getName should return the name given at construction",
				"Alice", person.getName());
	}

	/**
	 * The default constructor must initialise the name to an empty string
	 * rather than {@code null}.
	 */
	@Test
	public void testDefaultConstructor_emptyName() {
		Person person = new Person();
		assertEquals("Default constructor should set name to empty string",
				"", person.getName());
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
		Person person = new Person("Bob");
		try {
			person.addMeeting(new Meeting(3, 15, 9, 11));
		} catch (TimeConflictException e) {
			fail("Adding a valid meeting should not throw: " + e.getMessage());
		}
	}

	/**
	 * Adding a second meeting that overlaps an existing one must throw
	 * {@link TimeConflictException}.  The exception message must contain the
	 * person's name so that callers can identify which attendee caused the
	 * conflict.
	 */
	@Test
	public void testAddMeeting_conflict_exceptionContainsPersonName() {
		Person person = new Person("Carol");
		try {
			person.addMeeting(new Meeting(3, 15, 9, 12));
			person.addMeeting(new Meeting(3, 15, 11, 14)); // start=11 overlaps 9-12
			fail("Should have thrown TimeConflictException for overlapping meetings");
		} catch (TimeConflictException e) {
			assertTrue("Exception message should include the person's name",
					e.getMessage().contains("Carol"));
		}
	}

	// -----------------------------------------------------------------------
	// isBusy
	// -----------------------------------------------------------------------

	/**
	 * After a meeting has been added, the person should be reported as busy
	 * during that exact time slot.
	 */
	@Test
	public void testIsBusy_afterAddMeeting_returnsTrue() {
		Person person = new Person("Dave");
		try {
			person.addMeeting(new Meeting(5, 10, 14, 16));
			assertTrue("Person should be busy in the booked slot",
					person.isBusy(5, 10, 14, 16));
		} catch (TimeConflictException e) {
			fail("Should not throw: " + e.getMessage());
		}
	}

	/**
	 * A person with no meetings at all must be reported as free for any
	 * valid time slot.
	 */
	@Test
	public void testIsBusy_emptyCalendar_returnsFalse() {
		Person person = new Person("Eve");
		try {
			assertFalse("Person with no meetings should not be busy",
					person.isBusy(5, 10, 14, 16));
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
		Person person = new Person("Frank");
		try {
			person.addMeeting(new Meeting(5, 10, 9, 11));
			assertFalse("Person should be free outside the booked slot",
					person.isBusy(5, 10, 13, 15));
		} catch (TimeConflictException e) {
			fail("Should not throw: " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------
	// getMeeting / removeMeeting
	// -----------------------------------------------------------------------

	/**
	 * {@code getMeeting} at index 0 must return the meeting that was added,
	 * with its description intact.
	 */
	@Test
	public void testGetMeeting_returnsCorrectMeeting() {
		Person person = new Person("Grace");
		try {
			Meeting meeting = new Meeting(6, 1, 10, 12);
			meeting.setDescription("One-on-one");
			person.addMeeting(meeting);
			Meeting retrieved = person.getMeeting(6, 1, 0);
			assertEquals("Retrieved meeting should have the correct description",
					"One-on-one", retrieved.getDescription());
		} catch (TimeConflictException e) {
			fail("Should not throw: " + e.getMessage());
		}
	}

	/**
	 * After {@code removeMeeting}, the previously booked slot must be reported
	 * as free.
	 */
	@Test
	public void testRemoveMeeting_meetingIsGone() {
		Person person = new Person("Henry");
		try {
			person.addMeeting(new Meeting(6, 1, 10, 12));
			person.removeMeeting(6, 1, 0);
			assertFalse("Person should be free after removing the only meeting",
					person.isBusy(6, 1, 10, 12));
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
		Person person = new Person("Iris");
		String agenda = person.printAgenda(7, 4);
		assertTrue("Day agenda should include a header with month and day",
				agenda.contains("7/4"));
	}

	/**
	 * The monthly agenda header must include the month number.
	 */
	@Test
	public void testPrintAgenda_month_containsHeader() {
		Person person = new Person("Jack");
		String agenda = person.printAgenda(7);
		assertTrue("Monthly agenda should include a header with the month number",
				agenda.contains("7"));
	}
}
