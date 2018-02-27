// MESSAGE SET_POSITION_CONTROL_OFFSET PACKING
package com.jeremydyer.mavlink.Messages.pixhawk;

import com.jeremydyer.mavlink.Messages.MAVLinkMessage;
import com.jeremydyer.mavlink.Messages.MAVLinkPacket;
import com.jeremydyer.mavlink.Messages.MAVLinkPayload;

/**
* Message sent to the MAV to set a new offset from the currently controlled position
*/
public class msg_set_position_control_offset extends MAVLinkMessage{

	public static final int MAVLINK_MSG_ID_SET_POSITION_CONTROL_OFFSET = 160;
	public static final int MAVLINK_MSG_LENGTH = 18;
	private static final long serialVersionUID = MAVLINK_MSG_ID_SET_POSITION_CONTROL_OFFSET;
	

 	/**
	* x position offset
	*/
	public float x; 
 	/**
	* y position offset
	*/
	public float y; 
 	/**
	* z position offset
	*/
	public float z; 
 	/**
	* yaw orientation offset in radians, 0 = NORTH
	*/
	public float yaw; 
 	/**
	* System ID
	*/
	public byte target_system; 
 	/**
	* Component ID
	*/
	public byte target_component; 

	/**
	 * Generates the payload for a mavlink message for a message of this type
	 * @return
	 */
	public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_SET_POSITION_CONTROL_OFFSET;
		packet.payload.putFloat(x);
		packet.payload.putFloat(y);
		packet.payload.putFloat(z);
		packet.payload.putFloat(yaw);
		packet.payload.putByte(target_system);
		packet.payload.putByte(target_component);
		return packet;		
	}

    /**
     * Decode a set_position_control_offset message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
	    x = payload.getFloat();
	    y = payload.getFloat();
	    z = payload.getFloat();
	    yaw = payload.getFloat();
	    target_system = payload.getByte();
	    target_component = payload.getByte();    
    }

     /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_set_position_control_offset(){
    	msgid = MAVLINK_MSG_ID_SET_POSITION_CONTROL_OFFSET;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     * 
     */
    public msg_set_position_control_offset(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_SET_POSITION_CONTROL_OFFSET;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "SET_POSITION_CONTROL_OFFSET");
        //Log.d("MAVLINK_MSG_ID_SET_POSITION_CONTROL_OFFSET", toString());
    }
    
            
    /**
     * Returns a string with the MSG name and data
     */
    public String toString(){
    	return "MAVLINK_MSG_ID_SET_POSITION_CONTROL_OFFSET -"+" x:"+x+" y:"+y+" z:"+z+" yaw:"+yaw+" target_system:"+target_system+" target_component:"+target_component+"";
    }
}
