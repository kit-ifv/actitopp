# actiTopp

actiTopp is a model to generate week activity schedules. The implementation is developed at the [Institute for Transport Studies](http://www.ifv.kit.edu) at the Karlsruhe Institute of Technology. actiTopp is part of our travel demand model [mobiTopp](http://www.ifv.kit.edu/359.php) but can also be used seperately.

More information about the model itself can be found [here](https://trid.trb.org/View/1437316).

# Usage

To start using actiTopp, [ExampleActiTopp](https://github.com/ifv-mobitopp/actitopp/blob/master/src/main/java/edu/kit/ifv/mobitopp/actitopp/demo/ExampleActiTopp.java) shows how a person is created and how a week activity schedule can be generated or how multiple persons can be created using a CSVInputReader.

# Input Variables

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
7. Raumtyp
	Coding:
	1 - rural
	2 - provincial
	3 - cityoutskirt
	4 - metropolitan
	5 - conurbation
8. number of cars in the household
9. commuting distance to work in kilometers (0 if non existing)
10. commuting distance to school/university in kilometers (0 if non existing)

