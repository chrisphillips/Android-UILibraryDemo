/** 
*/
package com.jeremydyer.mavlink.Messages.enums;

public class CAMERA_EVENT_TYPES {
	public static final int HEARTBEAT = 0; /* Camera heartbeat, announce camera component ID at 1hz | */
	public static final int TRIGGER = 1; /* Camera image triggered | */
	public static final int DISCONNECT = 2; /* Camera connection lost | */
	public static final int ERROR = 3; /* Camera unknown error | */
	public static final int LOWBATT = 4; /* Camera battery low. Parameter p1 shows reported voltage | */
	public static final int LOWSTORE = 5; /* Camera storage low. Parameter p1 shows reported shots remaining | */
	public static final int LOWSTOREV = 6; /* Camera storage low. Parameter p1 shows reported video minutes remaining | */
	public static final int CAMERA_EVENT_TYPES_ENUM_END = 7; /*  | */
}
