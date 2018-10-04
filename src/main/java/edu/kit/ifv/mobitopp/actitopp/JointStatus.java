package edu.kit.ifv.mobitopp.actitopp;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum JointStatus {
	
	JOINTTRIPANDACTIVITY (1),
	JOINTACTIVITY(2),
	JOINTTRIP (3),
	NOJOINTELEMENT(4),
	UNKNOWN(-1)
	;
	

	public static final Set<JointStatus> JOINTELEMENTS;
	public static final Set<JointStatus> FULLSET;
	
	static 
	{	
		FULLSET = Collections.unmodifiableSet(EnumSet.of(
																														JointStatus.JOINTTRIPANDACTIVITY,
																														JointStatus.JOINTACTIVITY,
																														JointStatus.JOINTTRIP,
																														JointStatus.NOJOINTELEMENT
																										));
		JOINTELEMENTS = Collections.unmodifiableSet(EnumSet.of(
																														JointStatus.JOINTTRIPANDACTIVITY,
																														JointStatus.JOINTACTIVITY,
																														JointStatus.JOINTTRIP
																										));
	}
	
	
	private int intValue;
	
	private JointStatus(int intValue) 
	{
		this.intValue=intValue;	
	}
	
	public int getTypeasInt() 
	{ 
		return this.intValue; 
	}

	public static JointStatus getTypeFromInt(int intValue) 
	{
		for (JointStatus type : EnumSet.allOf(JointStatus.class)) 
		{
			if (type.getTypeasInt() ==  intValue) 
			{
				return type;
			}
		}
		return null;
	}
}
