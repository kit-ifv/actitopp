package edu.kit.ifv.mobitopp.actitopp;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum ActivityType {
	
	WORK ('W'),
	EDUCATION ('E'),
	LEISURE ('L'),
	SHOPPING ('S'),
	TRANSPORT ('T'),
	HOME ('H'),
	UNKNOWN('x')
	;
	

	public static final Set<ActivityType> OUTOFHOMEACTIVITY;
	public static final Set<ActivityType> FULLSET;
	
	static 
	{
		OUTOFHOMEACTIVITY = Collections.unmodifiableSet(EnumSet.of(
																														ActivityType.WORK,
																														ActivityType.EDUCATION,
																														ActivityType.LEISURE,
																														ActivityType.SHOPPING,
																														ActivityType.TRANSPORT
																									));
		
		FULLSET = Collections.unmodifiableSet(EnumSet.of(
																														ActivityType.WORK,
																														ActivityType.EDUCATION,
																														ActivityType.LEISURE,
																														ActivityType.SHOPPING,
																														ActivityType.TRANSPORT,
																														ActivityType.HOME
																										));
	}
	
	
	private char charValue;
	
	private ActivityType(char charValue) 
	{
		this.charValue=charValue;	
	}
	
	public char getTypeasChar() 
	{ 
		return this.charValue; 
	}

	public static ActivityType getTypeFromChar(char charValue) 
	{
		Character tocompare = Character.toUpperCase(charValue);
		
		for (ActivityType type : EnumSet.allOf(ActivityType.class)) 
		{
			if (type.getTypeasChar() ==  tocompare) 
			{
				return type;
			}
		}
		return null;
	}
}
