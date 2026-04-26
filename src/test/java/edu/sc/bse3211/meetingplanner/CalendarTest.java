package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;

/**
 * Unit tests for {@link Calendar}.
 *
 * <h2>Test strategy</h2>
 * <p>Tests are grouped into the following areas:</p>
 * <ol>
 *   <li><b>Valid meeting addition</b> — normal-path checks confirming that
 *       well-formed meetings are stored and retrievable.</li>
 *   <li><b>Invalid month values</b> — boundary and out-of-range months.</li>
 *   <li><b>Invalid day values</b> — out-of-range days and month-specific
 *       limits (e.g. February 30).</li>
 *   <li><b>Invalid time values</b> — negative hours, hours &ge; 24, and
 *       start &ge; end.</li>
 *   <li><b>Conflict detection</b> — start overlap, end overlap, and the
 *       spanning case (new meeting completely contains an existing one).</li>
 *   <li><b>isBusy</b> — free/busy checks, including invalid inputs.</li>
 *   <li><b>clearSchedule / getMeeting / removeMeeting</b> — mutation and
 *       retrieval operations.</li>
 *   <li><b>printAgenda</b> — formatted output for a day and for a month.</li>
 * </ol>
 *
 * <p>Several tests are specifically designed as <em>regression tests</em> for
 * bugs that existed in the original code:</p>
 * <ul>
 *   <li>December (month 12) was incorrectly rejected.</li>
 *   <li>November 30 was incorrectly blocked by a sentinel entry.</li>
 *   <li>February 30 was silently accepted.</li>
 *   <li>Meetings that completely span an existing meeting were not detected.</li>
 * </ul>
 */
public class CalendarTest {

	// -----------------------------------------------------------------------
	// Happy-path: add a valid meeting and verify it is stored
	// -----------------------------------------------------------------------

	/**
	 * A straightforward meeting on March 15, 09:00-11:00 should be accepted
	 * and the slot should subsequently be reported as busy.
	 */
	@Test
	public void testAddMeeting_validMeeting() {
		Calendar calendar = new Calendar();
		try {
			Meeting meeting = new Meeting(3, 15, 9, 11);
			calendar.addMeeting(meeting);
			assertTrue("Calendar should be busy from 9-11 on March 15",
					calendar.isBusy(3, 15, 9, 11));
		} catch (TimeConflictException e) {
			fail("Should not throw exception: " + e.getMessage());
		}
	}

	/**
	 * Janan Luwum Day (February 16) is a Ugandan public holiday.
	 * Adding it as an all-day meeting via the 3-arg constructor and
	 * then querying the full day (0-23) should confirm the day is blocked.
	 */
	@Test
	public void testAddMeeting_holiday() {
		Calendar calendar = new Calendar();
		try {
			Meeting janan = new Meeting(2, 16, "Janan Luwum");
			calendar.addMeeting(janan);
			Boolean added = calendar.isBusy(2, 16, 0, 23);
			assertTrue("Janan Luwum Day should be marked as busy on the calendar", added);
		} catch (TimeConflictException e) {
			fail("Should not throw exception: " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------
	// Invalid month values
	// -----------------------------------------------------------------------

	/**
	 * Month 0 is below the valid range (1-12) and must be rejected.
	 */
	@Test(expected = TimeConflictException.class)
	public void testAddMeeting_invalidMonth_zero() throws TimeConflictException {
		Calendar calendar = new Calendar();
		calendar.addMeeting(new Meeting(0, 15, 9, 11));
	}

	/**
	 * Month 13 is above the valid range (1-12) and must be rejected.
	 */
	@Test(expected = TimeConflictException.class)
	public void testAddMeeting_invalidMonth_thirteen() throws TimeConflictException {
		Calendar calendar = new Calendar();
		calendar.addMeeting(new Meeting(13, 15, 9, 11));
	}

	/**
	 * Regression test: the original code used {@code mMonth >= 12}, which
	 * incorrectly rejected December.  The condition has been fixed to
	 * {@code mMonth > 12} so month 12 is now accepted.
	 */
	@Test
	public void testAddMeeting_validMonth_december() {
		Calendar calendar = new Calendar();
		try {
			calendar.addMeeting(new Meeting(12, 15, 9, 11));
		} catch (TimeConflictException e) {
			fail("December (month 12) is a valid month and should not throw: "
					+ e.getMessage());
		}
	}

	// -----------------------------------------------------------------------
	// Invalid day values
	// -----------------------------------------------------------------------

	/**
	 * Day 0 is below the valid range (1-31) and must be rejected.
	 */
	@Test(expected = TimeConflictException.class)
	public void testAddMeeting_invalidDay_zero() throws TimeConflictException {
		Calendar calendar = new Calendar();
		calendar.addMeeting(new Meeting(3, 0, 9, 11));
	}

	/**
	 * Day 32 is above the valid range (1-31) and must be rejected.
	 */
	@Test(expected = TimeConflictException.class)
	public void testAddMeeting_invalidDay_thirtyTwo() throws TimeConflictException {
		Calendar calendar = new Calendar();
		calendar.addMeeting(new Meeting(3, 32, 9, 11));
	}

	/**
	 * Regression test: the original {@code checkTimes} only verified that
	 * day was in the range 1-31, so February 30 was accepted silently.
	 * The fix adds per-month day-limit validation, so this now throws.
	 */
	@Test(expected = TimeConflictException.class)
	public void testAddMeeting_nonExistentDate_feb30() throws TimeConflictException {
		Calendar calendar = new Calendar();
		calendar.addMeeting(new Meeting(2, 30, 9, 11));
	}

	/**
	 * Regression test: the original constructor incorrectly placed a
	 * {@code "Day does not exist"} sentinel on November 30, which is a valid
	 * date (November has 30 days).  After the fix, adding a meeting on
	 * November 30 must succeed without throwing.
	 */
	@Test
	public void testAddMeeting_november30_validDate() {
		Calendar calendar = new Calendar();
		try {
			calendar.addMeeting(new Meeting(11, 30, 9, 11));
		} catch (TimeConflictException e) {
			fail("November 30 is a valid date and should not throw: "
					+ e.getMessage());
		}
	}

	/**
	 * Regression test: an empty November 30 must not report as busy.
	 * Before the fix, the misplaced sentinel caused {@code isBusy} to always
	 * return {@code true} for that slot.
	 */
	@Test
	public void testIsBusy_november30_emptyDay_returnsFalse() {
		Calendar calendar = new Calendar();
		try {
			assertFalse("November 30 should not be busy on an empty calendar",
					calendar.isBusy(11, 30, 9, 11));
		} catch (TimeConflictException e) {
			fail("Should not throw: " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------
	// Invalid time values
	// -----------------------------------------------------------------------

	/**
	 * A negative start hour is illegal and must throw.
	 */
	@Test(expected = TimeConflictException.class)
	public void testAddMeeting_invalidStartTime_negative() throws TimeConflictException {
		Calendar calendar = new Calendar();
		calendar.addMeeting(new Meeting(3, 15, -1, 11));
	}

	/**
	 * An end hour of 24 exceeds the valid range (0-23) and must throw.
	 */
	@Test(expected = TimeConflictException.class)
	public void testAddMeeting_invalidEndTime_tooLarge() throws TimeConflictException {
		Calendar calendar = new Calendar();
		calendar.addMeeting(new Meeting(3, 15, 9, 24));
	}

	/**
	 * A meeting where start == end has zero duration and must be rejected.
	 */
	@Test(expected = TimeConflictException.class)
	public void testAddMeeting_startEqualsEnd() throws TimeConflictException {
		Calendar calendar = new Calendar();
		calendar.addMeeting(new Meeting(3, 15, 10, 10));
	}

	/**
	 * A meeting where start &gt; end is logically impossible and must be rejected.
	 */
	@Test(expected = TimeConflictException.class)
	public void testAddMeeting_startAfterEnd() throws TimeConflictException {
		Calendar calendar = new Calendar();
		calendar.addMeeting(new Meeting(3, 15, 15, 9));
	}

	// -----------------------------------------------------------------------
	// Conflict detection
	// -----------------------------------------------------------------------

	/**
	 * Adding a second meeting whose start time falls inside an existing meeting
	 * must throw {@link TimeConflictException}.
	 * <p>Scenario: existing 09:00-13:00, new 11:00-15:00 (start at 11 is inside
	 * the existing window).</p>
	 */
	@Test(expected = TimeConflictException.class)
	public void testAddMeeting_conflict_startOverlap() throws TimeConflictException {
		Calendar calendar = new Calendar();
		calendar.addMeeting(new Meeting(3, 15, 9, 13));
		calendar.addMeeting(new Meeting(3, 15, 11, 15));
	}

	/**
	 * Adding a second meeting whose end time falls inside an existing meeting
	 * must throw {@link TimeConflictException}.
	 * <p>Scenario: existing 11:00-15:00, new 09:00-13:00 (end at 13 is inside
	 * the existing window).</p>
	 */
	@Test(expected = TimeConflictException.class)
	public void testAddMeeting_conflict_endOverlap() throws TimeConflictException {
		Calendar calendar = new Calendar();
		calendar.addMeeting(new Meeting(3, 15, 11, 15));
		calendar.addMeeting(new Meeting(3, 15, 9, 13));
	}

	/**
	 * Regression test: the original conflict check only covered start-inside and
	 * end-inside cases.  A new meeting that completely spans an existing one
	 * (new.start &le; existing.start AND new.end &ge; existing.end) was silently
	 * accepted.  After the fix this must throw.
	 * <p>Scenario: existing 11:00-13:00, new 09:00-15:00 (fully spans).</p>
	 */
	@Test(expected = TimeConflictException.class)
	public void testAddMeeting_conflict_newMeetingSpansExisting() throws TimeConflictException {
		Calendar calendar = new Calendar();
		calendar.addMeeting(new Meeting(3, 15, 11, 13)); // existing: 11-13
		calendar.addMeeting(new Meeting(3, 15, 9, 15));  // spans:    09-15
	}

	/**
	 * Two meetings on the same day that do not overlap should both be accepted,
	 * and both time slots should subsequently be reported as busy.
	 * <p>Scenario: 09:00-11:00 and 13:00-15:00 — a two-hour gap between them.</p>
	 */
	@Test
	public void testAddMeeting_nonOverlappingMeetings_bothAdded() {
		Calendar calendar = new Calendar();
		try {
			calendar.addMeeting(new Meeting(3, 15, 9, 11));
			calendar.addMeeting(new Meeting(3, 15, 13, 15));
			assertTrue("Slot 9-11 should be busy", calendar.isBusy(3, 15, 9, 11));
			assertTrue("Slot 13-15 should be busy", calendar.isBusy(3, 15, 13, 15));
		} catch (TimeConflictException e) {
			fail("Non-overlapping meetings should not conflict: " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------
	// isBusy
	// -----------------------------------------------------------------------

	/**
	 * An empty day should never be reported as busy.
	 */
	@Test
	public void testIsBusy_emptyCalendar_returnsFalse() {
		Calendar calendar = new Calendar();
		try {
			assertFalse("An empty day should not be busy",
					calendar.isBusy(3, 15, 9, 11));
		} catch (TimeConflictException e) {
			fail("Should not throw: " + e.getMessage());
		}
	}

	/**
	 * After a meeting is added, the same time slot must be reported as busy.
	 */
	@Test
	public void testIsBusy_afterAddMeeting_returnsTrue() {
		Calendar calendar = new Calendar();
		try {
			calendar.addMeeting(new Meeting(3, 15, 9, 11));
			assertTrue("Day should be busy after adding a meeting",
					calendar.isBusy(3, 15, 9, 11));
		} catch (TimeConflictException e) {
			fail("Should not throw: " + e.getMessage());
		}
	}

	/**
	 * A time slot that does not overlap the booked meeting must still be free.
	 * <p>Booked: 09:00-11:00.  Query: 13:00-15:00.</p>
	 */
	@Test
	public void testIsBusy_differentSlot_returnsFalse() {
		Calendar calendar = new Calendar();
		try {
			calendar.addMeeting(new Meeting(3, 15, 9, 11));
			assertFalse("Slot 13-15 should be free when only 9-11 is booked",
					calendar.isBusy(3, 15, 13, 15));
		} catch (TimeConflictException e) {
			fail("Should not throw: " + e.getMessage());
		}
	}

	/**
	 * Passing a negative start hour to {@code isBusy} must throw because
	 * {@code isBusy} delegates validation to {@link Calendar#checkTimes}.
	 */
	@Test(expected = TimeConflictException.class)
	public void testIsBusy_invalidTime_throws() throws TimeConflictException {
		Calendar calendar = new Calendar();
		calendar.isBusy(3, 15, -1, 11);
	}

	/**
	 * Passing month 0 to {@code isBusy} must throw.
	 */
	@Test(expected = TimeConflictException.class)
	public void testIsBusy_invalidMonth_throws() throws TimeConflictException {
		Calendar calendar = new Calendar();
		calendar.isBusy(0, 15, 9, 11);
	}

	// -----------------------------------------------------------------------
	// clearSchedule
	// -----------------------------------------------------------------------

	/**
	 * After calling {@code clearSchedule}, the previously booked slot must no
	 * longer be reported as busy.
	 */
	@Test
	public void testClearSchedule_removesAddedMeeting() {
		Calendar calendar = new Calendar();
		try {
			calendar.addMeeting(new Meeting(3, 15, 9, 11));
			calendar.clearSchedule(3, 15);
			assertFalse("Schedule should be empty after clearSchedule",
					calendar.isBusy(3, 15, 9, 11));
		} catch (TimeConflictException e) {
			fail("Should not throw: " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------
	// getMeeting / removeMeeting
	// -----------------------------------------------------------------------

	/**
	 * {@code getMeeting} at index 0 must return the only meeting that was added
	 * to that day, with all fields intact.
	 */
	@Test
	public void testGetMeeting_returnsCorrectMeeting() {
		Calendar calendar = new Calendar();
		try {
			Meeting meeting = new Meeting(3, 15, 9, 11);
			meeting.setDescription("Stand-up");
			calendar.addMeeting(meeting);
			Meeting retrieved = calendar.getMeeting(3, 15, 0);
			assertEquals("Description should match", "Stand-up", retrieved.getDescription());
			assertEquals("Start time should match", 9, retrieved.getStartTime());
			assertEquals("End time should match", 11, retrieved.getEndTime());
		} catch (TimeConflictException e) {
			fail("Should not throw: " + e.getMessage());
		}
	}

	/**
	 * After removing the only meeting from a day, the slot must be free again.
	 */
	@Test
	public void testRemoveMeeting_dayBecomesEmpty() {
		Calendar calendar = new Calendar();
		try {
			calendar.addMeeting(new Meeting(3, 15, 9, 11));
			calendar.removeMeeting(3, 15, 0);
			assertFalse("Day should be free after removing the only meeting",
					calendar.isBusy(3, 15, 9, 11));
		} catch (TimeConflictException e) {
			fail("Should not throw: " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------
	// printAgenda
	// -----------------------------------------------------------------------

	/**
	 * The day agenda must contain the description of the meeting that was added.
	 * A fully-constructed meeting (with room and attendees) is used so that
	 * {@link Meeting#toString()} does not throw.
	 */
	@Test
	public void testPrintAgenda_day_containsDescription() {
		Calendar calendar = new Calendar();
		try {
			ArrayList<Person> attendees = new ArrayList<>();
			attendees.add(new Person("Alice"));
			Room room = new Room("Room101");
			Meeting meeting = new Meeting(3, 15, 9, 11, attendees, room, "Sprint Planning");
			calendar.addMeeting(meeting);
			String agenda = calendar.printAgenda(3, 15);
			assertTrue("Day agenda should contain the meeting description",
					agenda.contains("Sprint Planning"));
		} catch (TimeConflictException e) {
			fail("Should not throw: " + e.getMessage());
		}
	}

	/**
	 * The monthly agenda must contain the description of a meeting added on
	 * any day within that month.
	 */
	@Test
	public void testPrintAgenda_month_containsDescription() {
		Calendar calendar = new Calendar();
		try {
			ArrayList<Person> attendees = new ArrayList<>();
			attendees.add(new Person("Bob"));
			Room room = new Room("Room202");
			Meeting meeting = new Meeting(3, 10, 14, 16, attendees, room, "Retrospective");
			calendar.addMeeting(meeting);
			String agenda = calendar.printAgenda(3);
			assertTrue("Monthly agenda should contain the meeting description",
					agenda.contains("Retrospective"));
		} catch (TimeConflictException e) {
			fail("Should not throw: " + e.getMessage());
		}
	}
}
