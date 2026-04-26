package edu.sc.bse3211.meetingplanner;

import java.util.ArrayList;

/**
 * Represents a yearly calendar that stores meetings indexed by month and day.
 *
 * <p>The internal structure is a three-dimensional ArrayList accessed as
 * {@code occupied[month][day][meetingIndex]}.  Months are 1-indexed (1 = January,
 * 12 = December) and days are 1-indexed (1-31).  Hours are represented as
 * integers in the range 0-23.</p>
 *
 * <p>Days that do not exist for a given month (e.g. April 31, February 29-31) are
 * pre-populated with a sentinel {@code Meeting} whose description is
 * {@code "Day does not exist"}.  That sentinel is skipped during conflict checks
 * so it never causes a false conflict — it merely makes {@link #isBusy} return
 * {@code true} for those slots, discouraging their use.</p>
 */
public class Calendar {
	// Indexed by Month, Day
	private ArrayList<ArrayList<ArrayList<Meeting>>> occupied;

	/**
	 * Default constructor — builds a calendar and initialises every day to an
	 * empty meeting list.
	 *
	 * <p>The backing list is sized 0-13 for months and 0-31 for days so that
	 * 1-based indices work without offset arithmetic.  Slots 0 and 13 are never
	 * used.</p>
	 *
	 * <p>Sentinel {@code "Day does not exist"} meetings are inserted for every
	 * date that cannot appear in any year:
	 * <ul>
	 *   <li>February 29, 30, 31</li>
	 *   <li>April 31</li>
	 *   <li>June 31</li>
	 *   <li>September 31</li>
	 *   <li>November 31</li>
	 * </ul>
	 * Note: leap-year handling (February 29) is outside the scope of this
	 * implementation.</p>
	 */
	public Calendar(){
		occupied = new ArrayList<ArrayList<ArrayList<Meeting>>>();

		for(int i=0;i<=13;i++){
			occupied.add(new ArrayList<ArrayList<Meeting>>());
			for(int j=0;j<32;j++){
				occupied.get(i).add(new ArrayList<Meeting>());
			}
		}

		// Block calendar slots that never exist in any year.
		occupied.get(2).get(29).add(new Meeting(2,29,"Day does not exist"));
		occupied.get(2).get(30).add(new Meeting(2,30,"Day does not exist"));
		occupied.get(2).get(31).add(new Meeting(2,31,"Day does not exist"));
		occupied.get(4).get(31).add(new Meeting(4,31,"Day does not exist"));
		occupied.get(6).get(31).add(new Meeting(6,31,"Day does not exist"));
		occupied.get(9).get(31).add(new Meeting(9,31,"Day does not exist"));
		occupied.get(11).get(31).add(new Meeting(11,31,"Day does not exist"));
	}

	/**
	 * Returns whether anything is scheduled during the given time window.
	 *
	 * <p>Three overlap conditions are checked against every existing meeting on
	 * the same day:
	 * <ol>
	 *   <li>The query start time falls within an existing meeting.</li>
	 *   <li>The query end time falls within an existing meeting.</li>
	 *   <li>The query window completely spans an existing meeting.</li>
	 * </ol>
	 * If any condition is true, the method returns {@code true}.</p>
	 *
	 * @param month - The month to check (1-12)
	 * @param day   - The day to check (1-31, must be valid for the month)
	 * @param start - The start of the window to check (0-23, must be &lt; end)
	 * @param end   - The end of the window to check (0-23, must be &gt; start)
	 * @return {@code true} if the calendar has any entry in that timeframe.
	 * @throws TimeConflictException if any parameter is outside its valid range.
	 */
	public boolean isBusy(int month, int day, int start, int end) throws TimeConflictException{
		boolean busy = false;

		checkTimes(month,day,start,end);

		for(Meeting toCheck : occupied.get(month).get(day)){
			if(start >= toCheck.getStartTime() && start <= toCheck.getEndTime()){
				busy=true;
			}else if(end >= toCheck.getStartTime() && end <= toCheck.getEndTime()){
				busy=true;
			}else if(start <= toCheck.getStartTime() && end >= toCheck.getEndTime()){
				// New window completely spans an existing meeting.
				busy=true;
			}
		}
		return busy;
	}

	/**
	 * Validates a month/day/time combination and throws if any value is illegal.
	 *
	 * <p>Validation rules (applied in order):
	 * <ol>
	 *   <li>Day must be in the range 1-31.</li>
	 *   <li>Month must be in the range 1-12.</li>
	 *   <li>Day must not exceed the maximum day for the given month
	 *       (February is capped at 28; leap years are not handled).</li>
	 *   <li>Start and end times must each be in the range 0-23.</li>
	 *   <li>Start must be strictly less than end.</li>
	 * </ol></p>
	 *
	 * @param mMonth - The month of the meeting (1-12)
	 * @param mDay   - The day of the meeting (1-31)
	 * @param mStart - The time the meeting starts (0-23)
	 * @param mEnd   - The time the meeting ends (0-23, must be &gt; mStart)
	 * @throws TimeConflictException if any parameter is outside its valid range.
	 */
	public static void checkTimes(int mMonth,int mDay,int mStart, int mEnd) throws TimeConflictException{
		if(mDay< 1 || mDay > 31){
			throw new TimeConflictException("Day does not exist.");
		}

		if(mMonth < 1 || mMonth > 12){
			throw new TimeConflictException("Month does not exist.");
		}

		// Enforce month-specific day limits (e.g. February cannot have 30 days).
		int[] maxDays = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		if(mDay > maxDays[mMonth]){
			throw new TimeConflictException("Day does not exist.");
		}

		if(mStart< 0 || mStart > 23){
			throw new TimeConflictException("Illegal hour.");
		}

		if(mEnd < 0 || mEnd > 23){
			throw new TimeConflictException("Illegal hour.");
		}

		if(mStart >= mEnd){
			throw new TimeConflictException("Meeting starts before it ends.");
		}
	}

	/**
	 * Adds a meeting to this calendar.
	 *
	 * <p>The method first validates the meeting's date and times via
	 * {@link #checkTimes}, then checks for any time overlap with meetings already
	 * scheduled on the same day.  Three overlap cases are detected:
	 * <ol>
	 *   <li>The new meeting's start time falls within an existing meeting.</li>
	 *   <li>The new meeting's end time falls within an existing meeting.</li>
	 *   <li>The new meeting completely spans an existing meeting.</li>
	 * </ol>
	 * Sentinel {@code "Day does not exist"} entries are skipped during conflict
	 * detection; they exist only to mark invalid calendar slots.</p>
	 *
	 * <p>The null-safe comparison {@code !"Day does not exist".equals(...)} is
	 * used so that meetings without a description (null) are handled safely.</p>
	 *
	 * @param toAdd - The {@link Meeting} to add.
	 * @throws TimeConflictException if the date/time is invalid or overlaps with
	 *                               an existing meeting.
	 */
	public void addMeeting(Meeting toAdd) throws TimeConflictException{
		int mDay = toAdd.getDay();
		int mMonth = toAdd.getMonth();
		int mStart = toAdd.getStartTime();
		int mEnd = toAdd.getEndTime();

		checkTimes(mMonth,mDay,mStart,mEnd);

		ArrayList<Meeting> thatDay = occupied.get(mMonth).get(mDay);
		boolean booked = false;
		Meeting conflict = new Meeting();

		for(Meeting toCheck : thatDay){
			// Skip sentinel entries that mark non-existent calendar dates.
			if(!"Day does not exist".equals(toCheck.getDescription())){
				if(mStart >= toCheck.getStartTime() && mStart <= toCheck.getEndTime()){
					booked = true;
					conflict = toCheck;
				}else if(mEnd >= toCheck.getStartTime() && mEnd <= toCheck.getEndTime()){
					booked = true;
					conflict = toCheck;
				}else if(mStart <= toCheck.getStartTime() && mEnd >= toCheck.getEndTime()){
					// New meeting completely contains an existing one.
					booked = true;
					conflict = toCheck;
				}
			}
		}

		if(booked){
			throw new TimeConflictException("Overlap with another item - "+conflict.getDescription()
				+" - scheduled from "+conflict.getStartTime()+" and "+conflict.getEndTime());
		}else{
			occupied.get(mMonth).get(mDay).add(toAdd);
		}
	}

	/**
	 * Removes all meetings for a given day, including any sentinel entries.
	 *
	 * @param month - The month (1-12)
	 * @param day   - The day (1-31)
	 */
	public void clearSchedule(int month, int day){
		occupied.get(month).set(day, new ArrayList<Meeting>());
	}

	/**
	 * Returns a formatted string listing every meeting in the given month.
	 *
	 * @param month - The month to print (1-12)
	 * @return A multi-line agenda string.
	 */
	public String printAgenda(int month){
		String agenda = "Agenda for "+month+":\n";
		for(ArrayList<Meeting> toPrint : occupied.get(month)){
			for(Meeting meeting: toPrint){
				agenda = agenda+meeting.toString()+"\n";
			}
		}

		return agenda;
	}

	/**
	 * Returns a formatted string listing every meeting on the given day.
	 *
	 * @param month - The month (1-12)
	 * @param day   - The day (1-31)
	 * @return A multi-line agenda string.
	 */
	public String printAgenda(int month, int day){
		String agenda = "Agenda for "+month+"/"+day+":\n";
		for(Meeting toPrint : occupied.get(month).get(day)){
			agenda = agenda+toPrint.toString()+"\n";
		}

		return agenda;
	}

	/**
	 * Retrieves a specific meeting by position.
	 *
	 * @param month - The month (1-12)
	 * @param day   - The day (1-31)
	 * @param index - Zero-based index into the meeting list for that day.
	 * @return The {@link Meeting} at the given index.
	 */
	public Meeting getMeeting(int month, int day, int index){
		return occupied.get(month).get(day).get(index);
	}

	/**
	 * Removes a specific meeting by position.
	 *
	 * @param month - The month (1-12)
	 * @param day   - The day (1-31)
	 * @param index - Zero-based index into the meeting list for that day.
	 */
	public void removeMeeting(int month, int day, int index){
		occupied.get(month).get(day).remove(index);
	}
}
