package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class RoomTest {

    private Room room;

    @Before
    public void setUp() {
        room = new Room("LLT6A");
    }

    // -----------------------------------------------------------------------
    // addMeeting
    // -----------------------------------------------------------------------

    @Test
    public void testAddMeeting_valid() {
        // R01 – A room can book a valid meeting slot
        try {
            room.addMeeting(new Meeting(3, 15, 9, 10));
            assertTrue(room.isBusy(3, 15, 9, 10));
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_conflict_throws() throws TimeConflictException {
        // R02 – Overlapping meeting in same room throws TimeConflictException.
        // Meetings need a description: addMeeting calls getDescription() on existing
        // meetings and NPEs when the description is null.
        Meeting first = new Meeting(3, 15, 9, 10);
        first.setDescription("First");
        room.addMeeting(first);
        Meeting second = new Meeting(3, 15, 9, 10);
        second.setDescription("Second");
        room.addMeeting(second);
    }

    @Test
    public void testAddMeeting_conflictMessage_containsRoomID() {
        // R02 – Exception message must identify the conflicting room
        try {
            Meeting first = new Meeting(3, 15, 9, 10);
            first.setDescription("First");
            room.addMeeting(first);
            Meeting second = new Meeting(3, 15, 9, 10);
            second.setDescription("Second");
            room.addMeeting(second);
            fail("Expected TimeConflictException");
        } catch (TimeConflictException e) {
            assertTrue("Exception message should contain the room ID",
                    e.getMessage().contains("LLT6A"));
        }
    }

    @Test
    public void testAddMeeting_noOverlapSameDay() {
        // Two non-overlapping slots should both succeed
        try {
            Meeting first = new Meeting(3, 15, 9, 10);
            first.setDescription("Morning");
            room.addMeeting(first);
            Meeting second = new Meeting(3, 15, 11, 12);
            second.setDescription("Afternoon");
            room.addMeeting(second);
        } catch (TimeConflictException e) {
            fail("Non-overlapping meetings should not conflict: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // isBusy
    // -----------------------------------------------------------------------

    @Test
    public void testIsBusy_freshRoom_isFree() {
        // R03 – A fresh Room has no meetings
        try {
            assertFalse(room.isBusy(3, 15, 9, 10));
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    @Test
    public void testIsBusy_afterAddMeeting_isTrue() {
        try {
            room.addMeeting(new Meeting(4, 10, 13, 15));
            assertTrue(room.isBusy(4, 10, 13, 15));
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // printAgenda
    // -----------------------------------------------------------------------

    @Test
    public void testPrintAgenda_emptyMonth() {
        // R04 – Printing an empty month returns a valid non-null string
        String agenda = room.printAgenda(5);
        assertNotNull(agenda);
        assertTrue(agenda.contains("5"));
    }

    @Test
    public void testPrintAgenda_emptyDay() {
        String agenda = room.printAgenda(3, 15);
        assertNotNull(agenda);
        assertTrue(agenda.contains("3/15"));
    }

    // -----------------------------------------------------------------------
    // getMeeting / removeMeeting
    // -----------------------------------------------------------------------

    @Test
    public void testGetMeeting_returnsCorrectMeeting() {
        try {
            room.addMeeting(new Meeting(3, 15, 9, 10));
            Meeting retrieved = room.getMeeting(3, 15, 0);
            assertNotNull(retrieved);
            assertEquals(9, retrieved.getStartTime());
            assertEquals(10, retrieved.getEndTime());
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    @Test
    public void testRemoveMeeting_slotBecomesFree() {
        try {
            room.addMeeting(new Meeting(3, 15, 9, 10));
            room.removeMeeting(3, 15, 0);
            assertFalse(room.isBusy(3, 15, 9, 10));
        } catch (TimeConflictException e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    @Test
    public void testDefaultConstructor_IDIsEmpty() {
        Room r = new Room();
        assertEquals("", r.getID());
    }

    @Test
    public void testNamedConstructor_IDIsSet() {
        assertEquals("LLT6A", room.getID());
    }
}
