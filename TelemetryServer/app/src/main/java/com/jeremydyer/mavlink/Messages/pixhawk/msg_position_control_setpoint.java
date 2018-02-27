// MESSAGE POSITION_CONTROL_SETPOINT PACKING
package com.jeremydyer.mavlink.Messages.pixhawk;

import com.jeremydyer.mavlink.Messages.MAVLinkMessage;
import com.jeremydyer.mavlink.Messages.MAVLinkPacket;
import com.jeremydyer.mavlink.Messages.MAVLinkPayload;

/**
* 
*/
public class msg_position_control_setpoint extends MAVLinkMessage{

	public static final int MAVLINK_MSG_ID_POSITION_CONTROL_SETPOINT = 170;
	public static final int MAVLINK_MSG_LENGTH = 18;
	private static final long serialVersionUID = MAVLINK_MSG_ID_POSITION_CONTROL_SETPOINT;
	

 	/**
	* x position
	*/
	public float x; 
 	/**
	* y position
	*/
	public float y; 
 	/**
	* z position
	*/
	public float z; 
 	/**
	* yaw orientation in radians, 0 = NORTH
	*/
	public float yaw; 
 	/**
	* ID of waypoint, 0 for plain position
	*/
	public short id; 

	/**
	 * Generates the payload for a mavlink message for a message of this type
	 * @return
	 */
	public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_POSITION_CONTROL_SETPOINT;
		packet.payload.putFloat(x);
		packet.payload.putFloat(y);
		packet.payload.putFloat(z);
		packet.payload.putFloat(yaw);
		packet.payload.putShort(id);
		return packet;		
	}

    /**
     * Decode a position_control_setpoint message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
	    x = payload.getFloat();
	    y = payload.getFloat();
	    z = payload.getFloat();
	    yaw = payload.getFloat();
	    id = payload.getShort();    
    }

     /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_position_control_setpoint(){
    	msgid = MAVLINK_MSG_ID_POSITION_CONTROL_SETPOINT;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     * 
     */
    public msg_position_control_setpoint(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_POSITION_CONTROL_SETPOINT;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "POSITION_CONTROL_SETPOINT");
        //Log.d("MAVLINK_MSG_ID_POSITION_CONTROL_SETPOINT", toString());
    }
    
          
    /**
     * Returns a string with the MSG name and data
     */
    public String toString(){
    	return "MAVLINK_MSG_ID_POSITION_CONTROL_SETPOINT -"+" x:"+x+" y:"+y+" z:"+z+" yaw:"+yaw+" id:"+id+"";
    }
}
