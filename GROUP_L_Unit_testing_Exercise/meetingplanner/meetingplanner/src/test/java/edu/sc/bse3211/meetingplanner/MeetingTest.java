package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

public class MeetingTest {

    // -----------------------------------------------------------------------
    // Constructor tests
    // -----------------------------------------------------------------------

    @Test
    public void testConstructor_monthDay() {
        // M01 – Meeting(month, day) blocks a whole day (start=0, end=23)
        Meeting m = new Meeting(3, 15);
        assertEquals(3, m.getMonth());
        assertEquals(15, m.getDay());
        assertEquals(0, m.getStartTime());
        assertEquals(23, m.getEndTime());
    }

    @Test
    public void testConstructor_monthDayDescription() {
        // M02 – Meeting(month, day, description) also blocks whole day
        Meeting m = new Meeting(3, 15, "Vacation");
        assertEquals(3, m.getMonth());
        assertEquals(15, m.getDay());
        assertEquals(0, m.getStartTime());
        assertEquals(23, m.getEndTime());
        assertEquals("Vacation", m.getDescription());
    }

    @Test
    public void testConstructor_monthDayStartEnd() {
        // M03 – Meeting(month, day, start, end)
        Meeting m = new Meeting(5, 20, 9, 11);
        assertEquals(5, m.getMonth());
        assertEquals(20, m.getDay());
        assertEquals(9, m.getStartTime());
        assertEquals(11, m.getEndTime());
    }

    @Test
    public void testConstructor_full() {
        // Full constructor sets all fields
        ArrayList<Person> attendees = new ArrayList<>();
        attendees.add(new Person("Shema Collins"));
        Room room = new Room("LLT6A");
        Meeting m = new Meeting(5, 20, 9, 11, attendees, room, "Sprint review");
        assertEquals(5, m.getMonth());
        assertEquals(20, m.getDay());
        assertEquals(9, m.getStartTime());
        assertEquals(11, m.getEndTime());
        assertEquals("Sprint review", m.getDescription());
        assertEquals(room, m.getRoom());
        assertNotNull(m.getAttendees());
        assertEquals(1, m.getAttendees().size());
    }

    // -----------------------------------------------------------------------
    // Getters and setters
    // -----------------------------------------------------------------------

    @Test
    public void testGettersAndSetters() {
        // M04 – All setters update state correctly
        Meeting m = new Meeting();
        m.setMonth(7);
        m.setDay(4);
        m.setStartTime(10);
        m.setEndTime(12);
        m.setDescription("Independence");
        Room room = new Room("LAB2");
        m.setRoom(room);

        assertEquals(7, m.getMonth());
        assertEquals(4, m.getDay());
        assertEquals(10, m.getStartTime());
        assertEquals(12, m.getEndTime());
        assertEquals("Independence", m.getDescription());
        assertEquals("LAB2", m.getRoom().getID());
    }

    // -----------------------------------------------------------------------
    // toString
    // -----------------------------------------------------------------------

    @Test
    public void testToString_withFullConstructor() {
        // M05 – toString produces a readable summary when room and attendees are set
        ArrayList<Person> attendees = new ArrayList<>();
        attendees.add(new Person("Acan Brenda"));
        Room room = new Room("LLT3A");
        Meeting m = new Meeting(6, 10, 14, 16, attendees, room, "Demo day");

        String result = m.toString();
        assertNotNull(result);
        assertTrue(result.contains("6/10"));
        assertTrue(result.contains("LLT3A"));
        assertTrue(result.contains("Demo day"));
        assertTrue(result.contains("Acan Brenda"));
    }

    @Test
    public void testToString_nullRoom_throwsNPE() {
        // M05 additional – Meeting(month,day,start,end) leaves room=null;
        // toString() dereferences room.getID() causing NullPointerException.
        Meeting m = new Meeting(3, 15, 9, 10);
        try {
            m.toString();
            fail("Expected NullPointerException because room is not initialised");
        } catch (NullPointerException e) {
            // Expected — this is a known bug in Meeting
        }
    }

    // -----------------------------------------------------------------------
    // addAttendee / removeAttendee
    // -----------------------------------------------------------------------

    @Test
    public void testAddAttendee_defaultConstructor_throwsNPE() {
        // M06 – Default constructor does not initialise the attendees list;
        // calling addAttendee() throws NullPointerException.
        Meeting m = new Meeting();
        try {
            m.addAttendee(new Person("Kukunda Lynn"));
            fail("Expected NullPointerException — attendees list is not initialised");
        } catch (NullPointerException e) {
            // Expected — this is a bug (unintialised attendees)
        }
    }

    @Test
    public void testRemoveAttendee_withFullConstructor() {
        // removeAttendee works when the attendees list is properly initialised
        ArrayList<Person> attendees = new ArrayList<>();
        Person p = new Person("Kazibwe Julius");
        attendees.add(p);
        Room room = new Room("LLT6B");
        Meeting m = new Meeting(3, 15, 9, 10, attendees, room, "Review");

        m.removeAttendee(p);
        assertTrue(m.getAttendees().isEmpty());
    }
}
