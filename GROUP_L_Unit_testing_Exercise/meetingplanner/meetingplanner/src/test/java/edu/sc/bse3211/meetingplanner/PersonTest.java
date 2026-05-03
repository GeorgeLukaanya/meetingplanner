package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class PersonTest {

    private Person person;

    @Before
    public void setUp() {
        person = new Person("Namugga Martha");
    }

    // -----------------------------------------------------------------------
    // addMeeting
    // -----------------------------------------------------------------------

    @Test
    public void testAddMeeting_valid() {
        // P01 – A person can book a valid meeting slot
        try {
            person.addMeeting(new Meeting(3, 15, 9, 10));
            assertTrue(person.isBusy(3, 15, 9, 10));
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_conflict_throws() throws TimeConflictException {
        // P02 – Adding an overlapping meeting throws TimeConflictException.
        // Meetings need a description: addMeeting calls getDescription() on existing
        // meetings and NPEs when the description is null.
        Meeting first = new Meeting(3, 15, 9, 10);
        first.setDescription("First");
        person.addMeeting(first);
        Meeting second = new Meeting(3, 15, 9, 10);
        second.setDescription("Second");
        person.addMeeting(second);
    }

    @Test
    public void testAddMeeting_conflictMessage_containsPersonName() {
        // P02 – Exception message must identify the person causing the conflict
        try {
            Meeting first = new Meeting(3, 15, 9, 10);
            first.setDescription("First");
            person.addMeeting(first);
            Meeting second = new Meeting(3, 15, 9, 10);
            second.setDescription("Second");
            person.addMeeting(second);
            fail("Expected TimeConflictException");
        } catch (TimeConflictException e) {
            assertTrue("Exception message should contain the person's name",
                    e.getMessage().contains("Namugga Martha"));
        }
    }

    // -----------------------------------------------------------------------
    // isBusy
    // -----------------------------------------------------------------------

    @Test
    public void testIsBusy_freshPerson_isFree() {
        // P03 – A fresh Person has no meetings
        try {
            assertFalse(person.isBusy(3, 15, 9, 10));
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    @Test
    public void testIsBusy_afterAddMeeting_isTrue() {
        try {
            person.addMeeting(new Meeting(4, 10, 13, 15));
            assertTrue(person.isBusy(4, 10, 13, 15));
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // printAgenda
    // -----------------------------------------------------------------------

    @Test
    public void testPrintAgenda_month_returnsNonNull() {
        // P04
        String agenda = person.printAgenda(3);
        assertNotNull(agenda);
        assertTrue(agenda.contains("3"));
    }

    @Test
    public void testPrintAgenda_day_returnsNonNull() {
        String agenda = person.printAgenda(3, 15);
        assertNotNull(agenda);
    }

    // -----------------------------------------------------------------------
    // getMeeting / removeMeeting
    // -----------------------------------------------------------------------

    @Test
    public void testGetMeeting_returnsCorrectMeeting() {
        // P05
        try {
            person.addMeeting(new Meeting(3, 15, 9, 10));
            Meeting retrieved = person.getMeeting(3, 15, 0);
            assertNotNull(retrieved);
            assertEquals(9, retrieved.getStartTime());
            assertEquals(10, retrieved.getEndTime());
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    @Test
    public void testRemoveMeeting_slotBecomesFree() {
        // P06
        try {
            person.addMeeting(new Meeting(3, 15, 9, 10));
            person.removeMeeting(3, 15, 0);
            assertFalse(person.isBusy(3, 15, 9, 10));
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    @Test
    public void testDefaultConstructor_nameIsEmpty() {
        Person p = new Person();
        assertEquals("", p.getName());
    }

    @Test
    public void testNamedConstructor_nameIsSet() {
        assertEquals("Namugga Martha", person.getName());
    }
}
