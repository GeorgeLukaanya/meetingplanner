# GROUP_L Unit Testing Exercise

Unit tests for the **Meeting Planner** application, written with JUnit 4 and built with Maven.

## Prerequisites

- Java JDK 7 or higher
- Apache Maven 3.x

Verify your setup:
```bash
java -version
mvn -version
```

## Running the Tests

Navigate to the Maven project directory and run the tests:

```bash
cd meetingplanner/meetingplanner
mvn test
```

Maven will compile the source code, compile the tests, and execute all test classes. Results are printed to the console and saved under `target/surefire-reports/`.

### Run a specific test class

```bash
mvn test -Dtest=CalendarTest
```

Replace `CalendarTest` with any of the available test classes:

| Test Class | Class Under Test |
|---|---|
| `CalendarTest` | `Calendar` |
| `MeetingTest` | `Meeting` |
| `PersonTest` | `Person` |
| `RoomTest` | `Room` |
| `OrganizationTest` | `Organization` |

### Run a specific test method

```bash
mvn test -Dtest=CalendarTest#testMethodName
```

### Clean build before running tests

```bash
mvn clean test
```

## Project Structure

```
meetingplanner/meetingplanner/
├── pom.xml
└── src/
    ├── main/java/edu/sc/bse3211/meetingplanner/   # Source classes
    └── test/java/edu/sc/bse3211/meetingplanner/   # Test classes
```

## Test Reports

After running tests, HTML and XML reports are generated at:

```
meetingplanner/meetingplanner/target/surefire-reports/
```
