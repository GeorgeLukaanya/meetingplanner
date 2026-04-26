package edu.sc.bse3211.meetingplanner;

import java.util.ArrayList;

/**
 * Represents a single calendar meeting.
 *
 * <p>A meeting occupies a contiguous time window on a specific month/day.
 * Hours are integers in the range 0-23.  A meeting may optionally have a
 * room, a list of attendees, and a free-text description.  All partial
 * constructors initialise the attendees list to an empty
 * {@link ArrayList} so that {@link #addAttendee} is always safe to call
 * without a prior null check.</p>
 */
public class Meeting {
	private int month;
	private int day;
	private int start;
	private int end;
	private ArrayList<Person> attendees;
	private Room room;
	private String description;

	/**
	 * Default constructor — all numeric fields default to 0, room and
	 * description are null, and the attendees list is initialised to empty.
	 */
	public Meeting(){
		this.attendees = new ArrayList<Person>();
	}

	/**
	 * Convenience constructor that blocks an entire day (start=0, end=23).
	 * Useful for marking vacation days or public holidays.
	 *
	 * @param month - The month of the meeting (1-12).
	 * @param day   - The day of the meeting (1-31).
	 */
	public Meeting(int month, int day){
		this.month=month;
		this.day=day;
		this.start=0;
		this.end=23;
		this.attendees = new ArrayList<Person>();
	}

	/**
	 * Convenience constructor that blocks an entire day with a description.
	 * Useful for labelled holidays or out-of-office entries.
	 *
	 * @param month       - The month of the meeting (1-12).
	 * @param day         - The day of the meeting (1-31).
	 * @param description - A short label for the blocked day.
	 */
	public Meeting(int month, int day, String description){
		this.month=month;
		this.day=day;
		this.start=0;
		this.end=23;
		this.description= description;
		this.attendees = new ArrayList<Person>();
	}

	/**
	 * Constructor for a time-bounded meeting without room or attendees.
	 * The attendees list is still initialised so that {@link #addAttendee}
	 * can be called immediately after construction.
	 *
	 * @param month - The month of the meeting (1-12).
	 * @param day   - The day of the meeting (1-31).
	 * @param start - The time the meeting starts (0-23).
	 * @param end   - The time the meeting ends (0-23, must be &gt; start).
	 */
	public Meeting(int month, int day, int start, int end){
		this.month=month;
		this.day=day;
		this.start=start;
		this.end=end;
		this.attendees = new ArrayList<Person>();
	}

	/**
	 * Full constructor — sets every field.
	 *
	 * @param month       - The month of the meeting (1-12).
	 * @param day         - The day of the meeting (1-31).
	 * @param start       - The time the meeting starts (0-23).
	 * @param end         - The time the meeting ends (0-23, must be &gt; start).
	 * @param attendees   - The people attending the meeting.
	 * @param room        - The room in which the meeting takes place.
	 * @param description - A description of the meeting.
	 */
	public Meeting(int month, int day, int start, int end, ArrayList<Person> attendees, Room room, String description){
		this.month=month;
		this.day=day;
		this.start=start;
		this.end=end;
		this.attendees = attendees;
		this.room = room;
		this.description = description;
	}

	/**
	 * Adds a person to the attendees list.
	 * Safe to call on any Meeting instance because all constructors initialise
	 * the attendees list.
	 *
	 * @param attendee - The person to add.
	 */
	public void addAttendee(Person attendee) {
		this.attendees.add(attendee);
	}

	/**
	 * Removes a person from the attendees list.
	 *
	 * @param attendee - The person to remove.
	 */
	public void removeAttendee(Person attendee) {
		this.attendees.remove(attendee);
	}

	/**
	 * Returns a human-readable summary of the meeting.
	 *
	 * <p>Format: {@code month/day, start - end,roomID: description\nAttending: name1,name2}</p>
	 *
	 * <p>Null-safety: if {@code room} is null the literal {@code "No Room"} is
	 * used.  If the attendees list is null or empty the "Attending:" line is
	 * emitted without any names.</p>
	 *
	 * @return A formatted string describing this meeting.
	 */
	public String toString(){
		String roomID = (room != null) ? room.getID() : "No Room";
		String info=month+"/"+day+", "+start+" - "+end+","+roomID+": "+description+"\nAttending: ";

		if(attendees != null && !attendees.isEmpty()){
			for(Person attendee : attendees){
				info=info+attendee.getName()+",";
			}
			// Remove the trailing comma after the last attendee name.
			info=info.substring(0,info.length()-1);
		}

		return info;
	}

	// -----------------------------------------------------------------------
	// Getters and Setters
	// -----------------------------------------------------------------------

	/** @return The month (1-12). */
	public int getMonth() {
		return month;
	}

	/** @param month - The month to set (1-12). */
	public void setMonth(int month) {
		this.month = month;
	}

	/** @return The day (1-31). */
	public int getDay() {
		return day;
	}

	/** @param day - The day to set (1-31). */
	public void setDay(int day) {
		this.day = day;
	}

	/** @return The start hour (0-23). */
	public int getStartTime() {
		return start;
	}

	/** @param start - The start hour to set (0-23). */
	public void setStartTime(int start) {
		this.start = start;
	}

	/** @return The end hour (0-23). */
	public int getEndTime() {
		return end;
	}

	/** @param end - The end hour to set (0-23). */
	public void setEndTime(int end) {
		this.end = end;
	}

	/** @return The list of attendees (never null after construction). */
	public ArrayList<Person> getAttendees() {
		return attendees;
	}

	/** @return The room, or {@code null} if none was set. */
	public Room getRoom() {
		return room;
	}

	/** @param room - The room to assign to this meeting. */
	public void setRoom(Room room) {
		this.room = room;
	}

	/** @return The description, or {@code null} if none was set. */
	public String getDescription() {
		return description;
	}

	/** @param description - A short label for this meeting. */
	public void setDescription(String description) {
		this.description = description;
	}
}
