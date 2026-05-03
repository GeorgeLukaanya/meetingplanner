package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class CalendarTest {

    private Calendar calendar;

    @Before
    public void setUp() {
        calendar = new Calendar();
    }

    private static Meeting timedMeeting(int month, int day, int start, int end, String desc) {
        Meeting m = new Meeting(month, day, start, end);
        m.setDescription(desc);
        return m;
    }

    // -----------------------------------------------------------------------
    // addMeeting – happy path
    // -----------------------------------------------------------------------

    @Test
    public void testAddMeeting_holiday() {
        // C02 – Original test: all-day holiday uses Meeting(month, day, description)
        try {
            Meeting janan = new Meeting(2, 16, "Janan Luwum");
            calendar.addMeeting(janan);
            Boolean added = calendar.isBusy(2, 16, 0, 23);
            assertTrue("Janan Luwum Day should be marked as busy on the calendar", added);
        } catch (TimeConflictException e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testAddMeeting_validTimedMeeting() {
        // C01 – Normal meeting on a weekday slot
        try {
            calendar.addMeeting(new Meeting(3, 15, 9, 10));
            assertTrue(calendar.isBusy(3, 15, 9, 10));
        } catch (TimeConflictException e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testAddMeeting_noOverlapSameDay() {
        // C03 – Two back-to-back meetings should both succeed.
        // Meetings must have a description because addMeeting calls getDescription()
        // on existing meetings when checking for conflicts (null causes NPE — see bug test below).
        try {
            calendar.addMeeting(timedMeeting(3, 15, 9, 10, "Morning standup"));
            calendar.addMeeting(timedMeeting(3, 15, 11, 12, "Team review"));
        } catch (TimeConflictException e) {
            fail("Non-overlapping meetings should not conflict: " + e.getMessage());
        }
    }

    @Test
    public void testAddMeeting_nullDescription_secondAddThrowsNPE() {
        // Bug (unlisted): addMeeting calls toCheck.getDescription().equals(...) without
        // a null guard. When the first meeting has no description (null), adding a second
        // meeting on the same day throws NullPointerException instead of succeeding or
        // throwing TimeConflictException.
        try {
            calendar.addMeeting(new Meeting(3, 15, 9, 10)); // description = null
            calendar.addMeeting(new Meeting(3, 15, 11, 12));
            // Reaching here means the bug is fixed; both non-overlapping meetings were added.
        } catch (NullPointerException e) {
            fail("Bug: addMeeting NPEs when an existing meeting has a null description — "
                    + e.getMessage());
        } catch (TimeConflictException e) {
            fail("Non-overlapping meetings should not conflict: " + e.getMessage());
        }
    }

    @Test
    public void testAddMeeting_february28_valid() {
        // C17 – February 28 is always a valid date
        try {
            calendar.addMeeting(new Meeting(2, 28, 9, 10));
        } catch (TimeConflictException e) {
            fail("February 28 is a valid date: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // addMeeting – conflict detection
    // -----------------------------------------------------------------------

    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_overlapAtStart() throws TimeConflictException {
        // C04 – Start of second meeting falls inside first
        calendar.addMeeting(timedMeeting(3, 15, 9, 11, "First"));
        calendar.addMeeting(timedMeeting(3, 15, 10, 12, "Second"));
    }

    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_overlapAtEnd() throws TimeConflictException {
        // C05 – End of second meeting falls inside first
        calendar.addMeeting(timedMeeting(3, 15, 10, 12, "First"));
        calendar.addMeeting(timedMeeting(3, 15, 8, 11, "Second"));
    }

    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_duplicateSlot() throws TimeConflictException {
        // C06 – Identical time slot
        calendar.addMeeting(timedMeeting(3, 15, 9, 10, "Meeting A"));
        calendar.addMeeting(timedMeeting(3, 15, 9, 10, "Meeting B"));
    }

    // -----------------------------------------------------------------------
    // addMeeting – illegal date / time inputs
    // -----------------------------------------------------------------------

    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_dayZero() throws TimeConflictException {
        // C07
        calendar.addMeeting(new Meeting(3, 0, 9, 10));
    }

    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_day32() throws TimeConflictException {
        // C08
        calendar.addMeeting(new Meeting(3, 32, 9, 10));
    }

    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_monthZero() throws TimeConflictException {
        // C09
        calendar.addMeeting(new Meeting(0, 15, 9, 10));
    }

    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_month13() throws TimeConflictException {
        // C10
        calendar.addMeeting(new Meeting(13, 15, 9, 10));
    }

    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_negativeStartHour() throws TimeConflictException {
        // C11
        calendar.addMeeting(new Meeting(3, 15, -1, 10));
    }

    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_endHour24() throws TimeConflictException {
        // C12
        calendar.addMeeting(new Meeting(3, 15, 9, 24));
    }

    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_startAfterEnd() throws TimeConflictException {
        // C13
        calendar.addMeeting(new Meeting(3, 15, 12, 9));
    }

    // -----------------------------------------------------------------------
    // Bug-exposing tests (these currently FAIL on the buggy code)
    // -----------------------------------------------------------------------

    @Test
    public void testAddMeeting_december_shouldBeValid() {
        // C14 – Bug 4: checkTimes uses mMonth >= 12 so December is wrongly rejected
        try {
            calendar.addMeeting(new Meeting(12, 1, 9, 10));
        } catch (TimeConflictException e) {
            fail("Bug 4: December (month 12) is a valid month but was rejected — " + e.getMessage());
        }
    }

    @Test
    public void testAddMeeting_sameStartEndHour_shouldBeValid() {
        // C15 – Bug 5: checkTimes uses mStart >= mEnd, rejecting start == end
        try {
            calendar.addMeeting(new Meeting(3, 15, 9, 9));
        } catch (TimeConflictException e) {
            fail("Bug 5: A meeting starting and ending at the same hour should be valid — " + e.getMessage());
        }
    }

    @Test
    public void testAddMeeting_november30_shouldSucceed() {
        // C16 – Bug 3: Calendar constructor incorrectly blocks November 30
        try {
            calendar.addMeeting(new Meeting(11, 30, 9, 10));
        } catch (TimeConflictException e) {
            fail("Bug 3: November 30 is a valid day — " + e.getMessage());
        }
    }

    @Test
    public void testAddMeeting_november31_shouldThrow() {
        // C18 – November 31 does not exist; addMeeting should reject it.
        // Bug: addMeeting skips "Day does not exist" placeholder meetings in its
        // conflict check, so it currently accepts November 31 silently.
        try {
            calendar.addMeeting(new Meeting(11, 31, 9, 10));
            fail("Bug: November 31 does not exist but addMeeting accepted it without error");
        } catch (TimeConflictException e) {
            // expected correct behaviour
        }
    }

    // -----------------------------------------------------------------------
    // checkTimes – direct static method tests
    // -----------------------------------------------------------------------

    @Test
    public void testCheckTimes_december_shouldBeValid() {
        // C25 – Bug 4: December wrongly fails the month check
        try {
            Calendar.checkTimes(12, 1, 9, 10);
        } catch (TimeConflictException e) {
            fail("Bug 4: checkTimes should accept December (month 12) — " + e.getMessage());
        }
    }

    @Test
    public void testCheckTimes_sameStartEnd_shouldBeValid() {
        // C26 – Bug 5: same start/end hour wrongly rejected
        try {
            Calendar.checkTimes(3, 15, 9, 9);
        } catch (TimeConflictException e) {
            fail("Bug 5: checkTimes should allow start == end — " + e.getMessage());
        }
    }

    @Test(expected = TimeConflictException.class)
    public void testCheckTimes_startStrictlyAfterEnd_throws() throws TimeConflictException {
        Calendar.checkTimes(3, 15, 12, 9);
    }

    // -----------------------------------------------------------------------
    // isBusy
    // -----------------------------------------------------------------------

    @Test
    public void testIsBusy_free() {
        // C19
        try {
            assertFalse(calendar.isBusy(3, 15, 9, 10));
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    @Test
    public void testIsBusy_busy() {
        // C20
        try {
            calendar.addMeeting(new Meeting(3, 15, 9, 10));
            assertTrue(calendar.isBusy(3, 15, 9, 10));
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    @Test(expected = TimeConflictException.class)
    public void testIsBusy_invalidMonth_throws() throws TimeConflictException {
        // C21
        calendar.isBusy(0, 15, 9, 10);
    }

    @Test
    public void testIsBusy_november30_shouldBeFree() {
        // C22 – Bug 3: Nov 30 is incorrectly blocked on a fresh calendar
        try {
            assertFalse("Bug 3: November 30 is valid and should be free on a fresh calendar",
                    calendar.isBusy(11, 30, 9, 10));
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    @Test
    public void testIsBusy_november31_alwaysBusy() {
        // C23 – November 31 is correctly blocked by the placeholder meeting
        try {
            assertTrue("November 31 does not exist and should always appear busy",
                    calendar.isBusy(11, 31, 9, 10));
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    @Test
    public void testIsBusy_february29_alwaysBusy() {
        // C24 – February 29 blocked by placeholder meeting
        try {
            assertTrue("February 29 does not exist in a non-leap-year calendar and should be busy",
                    calendar.isBusy(2, 29, 9, 10));
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // printAgenda
    // -----------------------------------------------------------------------

    @Test
    public void testPrintAgenda_emptyMonth() {
        // C32 – Printing a month with no meetings returns a non-null string
        String agenda = calendar.printAgenda(5);
        assertNotNull(agenda);
        assertTrue(agenda.contains("5"));
    }

    @Test
    public void testPrintAgenda_emptyDay() {
        // C33
        String agenda = calendar.printAgenda(3, 15);
        assertNotNull(agenda);
        assertTrue(agenda.contains("3/15"));
    }

    @Test
    public void testPrintAgenda_dayWithMeeting() {
        // C34 – printAgenda for a day containing a fully constructed Meeting
        try {
            ArrayList<Person> attendees = new ArrayList<>();
            attendees.add(new Person("Namugga Martha"));
            Room room = new Room("LLT6A");
            Meeting m = new Meeting(3, 15, 9, 10, attendees, room, "Team standup");
            calendar.addMeeting(m);
            String agenda = calendar.printAgenda(3, 15);
            assertNotNull(agenda);
            assertTrue(agenda.contains("Team standup"));
            assertTrue(agenda.contains("LLT6A"));
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // getMeeting / removeMeeting / clearSchedule
    // -----------------------------------------------------------------------

    @Test
    public void testGetMeeting_valid() {
        // C27
        try {
            Meeting toAdd = new Meeting(3, 15, 9, 10);
            calendar.addMeeting(toAdd);
            Meeting retrieved = calendar.getMeeting(3, 15, 0);
            assertNotNull(retrieved);
            assertEquals(9, retrieved.getStartTime());
            assertEquals(10, retrieved.getEndTime());
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    @Test
    public void testRemoveMeeting_valid() {
        // C28
        try {
            calendar.addMeeting(new Meeting(3, 15, 9, 10));
            calendar.removeMeeting(3, 15, 0);
            assertFalse(calendar.isBusy(3, 15, 9, 10));
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    @Test
    public void testClearSchedule() {
        // C29
        try {
            calendar.addMeeting(timedMeeting(3, 15, 9, 10, "Morning"));
            calendar.addMeeting(timedMeeting(3, 15, 11, 12, "Afternoon"));
            calendar.clearSchedule(3, 15);
            assertFalse(calendar.isBusy(3, 15, 9, 10));
            assertFalse(calendar.isBusy(3, 15, 11, 12));
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    @Test
    public void testGetMeeting_invalidMonth_shouldThrowTimeConflictException() {
        // C30 – Bug 1: getMeeting has no date validation; currently throws
        // IndexOutOfBoundsException instead of TimeConflictException.
        // getMeeting does not declare throws TimeConflictException, so we catch Exception.
        try {
            calendar.getMeeting(-1, 15, 0);
            fail("Expected an exception for invalid month -1");
        } catch (Exception e) {
            if (!(e instanceof TimeConflictException)) {
                fail("Bug 1: Expected TimeConflictException but got "
                        + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
            // TimeConflictException — correct behaviour once bug is fixed
        }
    }

    @Test
    public void testRemoveMeeting_invalidMonth_shouldThrowTimeConflictException() {
        // C31 – Bug 1: removeMeeting has no date validation.
        try {
            calendar.removeMeeting(-1, 15, 0);
            fail("Expected an exception for invalid month -1");
        } catch (Exception e) {
            if (!(e instanceof TimeConflictException)) {
                fail("Bug 1: Expected TimeConflictException but got "
                        + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }
}
