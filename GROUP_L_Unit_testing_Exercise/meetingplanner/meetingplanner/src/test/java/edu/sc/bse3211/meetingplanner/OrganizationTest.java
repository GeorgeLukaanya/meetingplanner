package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class OrganizationTest {

    private Organization org;

    @Before
    public void setUp() {
        org = new Organization();
    }

    // -----------------------------------------------------------------------
    // getRoom
    // -----------------------------------------------------------------------

    @Test
    public void testGetRoom_validID_returnsRoom() {
        // O01 – A known room ID returns the correct Room object
        try {
            Room r = org.getRoom("LLT6A");
            assertNotNull(r);
            assertEquals("LLT6A", r.getID());
        } catch (Exception e) {
            fail("Should not throw for a valid room ID: " + e.getMessage());
        }
    }

    @Test
    public void testGetRoom_allKnownRooms_exist() {
        // O01 – All five preconfigured rooms must be accessible
        String[] knownRooms = {"LLT6A", "LLT6B", "LLT3A", "LLT2C", "LAB2"};
        for (String id : knownRooms) {
            try {
                Room r = org.getRoom(id);
                assertNotNull("Room " + id + " should exist", r);
                assertEquals(id, r.getID());
            } catch (Exception e) {
                fail("Room " + id + " should exist but threw: " + e.getMessage());
            }
        }
    }

    @Test(expected = Exception.class)
    public void testGetRoom_invalidID_throws() throws Exception {
        // O02 – An unknown room ID must throw an exception
        org.getRoom("DOES_NOT_EXIST");
    }

    // -----------------------------------------------------------------------
    // getEmployee
    // -----------------------------------------------------------------------

    @Test
    public void testGetEmployee_validName_returnsPerson() {
        // O03 – A known employee name returns the correct Person object
        try {
            Person p = org.getEmployee("Namugga Martha");
            assertNotNull(p);
            assertEquals("Namugga Martha", p.getName());
        } catch (Exception e) {
            fail("Should not throw for a valid employee name: " + e.getMessage());
        }
    }

    @Test
    public void testGetEmployee_allKnownEmployees_exist() {
        // O03 – All five preconfigured employees must be accessible
        String[] knownEmployees = {
            "Namugga Martha", "Shema Collins", "Acan Brenda",
            "Kazibwe Julius", "Kukunda Lynn"
        };
        for (String name : knownEmployees) {
            try {
                Person p = org.getEmployee(name);
                assertNotNull("Employee " + name + " should exist", p);
                assertEquals(name, p.getName());
            } catch (Exception e) {
                fail("Employee " + name + " should exist but threw: " + e.getMessage());
            }
        }
    }

    @Test(expected = Exception.class)
    public void testGetEmployee_invalidName_throws() throws Exception {
        // O04 – An unknown name must throw an exception
        org.getEmployee("Nobody Here");
    }

    // -----------------------------------------------------------------------
    // getEmployees / getRooms
    // -----------------------------------------------------------------------

    @Test
    public void testGetEmployees_returnsNonEmptyList() {
        // O05
        assertNotNull(org.getEmployees());
        assertFalse(org.getEmployees().isEmpty());
        assertEquals(5, org.getEmployees().size());
    }

    @Test
    public void testGetRooms_returnsNonEmptyList() {
        // O06
        assertNotNull(org.getRooms());
        assertFalse(org.getRooms().isEmpty());
        assertEquals(5, org.getRooms().size());
    }
}
