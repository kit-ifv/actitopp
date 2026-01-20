# actiTopp

actiTopp is a model to generate week activity schedules. The implementation is developed at the [Institute for Transport Studies](http://www.ifv.kit.edu) at the Karlsruhe Institute of Technology. actiTopp is part of our travel demand model [mobiTopp](http://www.ifv.kit.edu/359.php) but can also be used separately.

More information about the model itself can be found [here](https://trid.trb.org/View/1437316).

## Project Overview

actiTopp is a Kotlin-based activity scheduling model that generates weekly activity patterns for individuals based on their demographic characteristics. The model uses a multi-step approach to:

1. Generate a week routine with the number of days for different activity types
2. Create a mobility structure with specific activities assigned to days
3. Allocate time budgets for different activities
4. Generate detailed mobility plans with activity timing and durations

## Package Structure

The project is organized into the following main packages:

- **enums**: Contains enumeration classes for activity types, area types, employment status, and gender
- **changes**: Handles modifications to activity patterns
- **mobilityplan**: Manages the creation and manipulation of mobility plans
- **mobilitystructure**: Defines structures for representing mobility patterns
- **modernization**: Contains classes for the modernized version of the model
- **plandurations**: Handles the planning of activity durations
- **steps**: Defines the sequential steps in the activity generation process
- **timebudgets**: Manages time allocation for different activities
- **tourstarttimes**: Handles the determination of when tours start
- **weekroutine**: Manages weekly routines of activities

## Usage

To start using actiTopp, [ExampleActiTopp](https://github.com/ifv-mobitopp/actitopp/blob/master/src/main/java/edu/kit/ifv/mobitopp/actitopp/demo/ExampleActiTopp.java) shows how a person is created and how a week activity schedule can be generated or how multiple persons can be created using a CSVInputReader.

## Input Variables

The following input variables for actiTopp are expected in the following order and using the following coding:

1. Indexnumber for a person, freely selectable
2. number of children 0-10 years old in the household
3. number of children under 18 in the household
4. age of the person in years
5. main occupation status of the person
	Coding:
	1 - full-time occupied
	2 - half-time occupied
	3 - not occupied
	4 - student (school or university)
	5 - worker in vocational program 
	6 - housewife, househusband
	7 - retired person / pensioner
6. gender type
	Coding:
	1 - male
	2 - female
7. Area type
	Coding:
	1 - rural
	2 - provincial
	3 - cityoutskirt
	4 - metropolitan
	5 - conurbation
8. number of cars in the household
9. commuting distance to work in kilometers (0 if non existing)
10. commuting distance to school/university in kilometers (0 if non existing)

## Model Workflow

The actiTopp model follows these general steps:

1. **Person and Household Creation**: Create person and household objects with demographic attributes
2. **Week Routine Generation**: Determine the number of days for different activity types
3. **Mobility Structure Creation**: Assign specific activities to days
4. **Time Budget Allocation**: Determine time budgets for different activities
5. **Mobility Plan Generation**: Create detailed plans with activity timing and durations

## Core Classes

- **ActitoppPerson**: Represents a person with demographic attributes
- **ActiToppHousehold**: Represents a household with attributes and members
- **WeekRoutine**: Represents a weekly pattern of activities
- **MobilityStructure**: Represents a structure of mobility patterns
- **TimeBudgets**: Manages time allocation for different activities
- **MobilityPlan**: Represents a detailed plan of activities and trips
