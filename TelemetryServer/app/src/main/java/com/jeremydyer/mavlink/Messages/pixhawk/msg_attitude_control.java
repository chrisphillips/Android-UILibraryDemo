// MESSAGE ATTITUDE_CONTROL PACKING
package com.jeremydyer.mavlink.Messages.pixhawk;

import com.jeremydyer.mavlink.Messages.MAVLinkMessage;
import com.jeremydyer.mavlink.Messages.MAVLinkPacket;
import com.jeremydyer.mavlink.Messages.MAVLinkPayload;

/**
* 
*/
public class msg_attitude_control extends MAVLinkMessage{

	public static final int MAVLINK_MSG_ID_ATTITUDE_CONTROL = 200;
	public static final int MAVLINK_MSG_LENGTH = 21;
	private static final long serialVersionUID = MAVLINK_MSG_ID_ATTITUDE_CONTROL;
	

 	/**
	* roll
	*/
	public float roll; 
 	/**
	* pitch
	*/
	public float pitch; 
 	/**
	* yaw
	*/
	public float yaw; 
 	/**
	* thrust
	*/
	public float thrust; 
 	/**
	* The system to be controlled
	*/
	public byte target; 
 	/**
	* roll control enabled auto:0, manual:1
	*/
	public byte roll_manual; 
 	/**
	* pitch auto:0, manual:1
	*/
	public byte pitch_manual; 
 	/**
	* yaw auto:0, manual:1
	*/
	public byte yaw_manual; 
 	/**
	* thrust auto:0, manual:1
	*/
	public byte thrust_manual; 

	/**
	 * Generates the payload for a mavlink message for a message of this type
	 * @return
	 */
	public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_ATTITUDE_CONTROL;
		packet.payload.putFloat(roll);
		packet.payload.putFloat(pitch);
		packet.payload.putFloat(yaw);
		packet.payload.putFloat(thrust);
		packet.payload.putByte(target);
		packet.payload.putByte(roll_manual);
		packet.payload.putByte(pitch_manual);
		packet.payload.putByte(yaw_manual);
		packet.payload.putByte(thrust_manual);
		return packet;		
	}

    /**
     * Decode a attitude_control message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
	    roll = payload.getFloat();
	    pitch = payload.getFloat();
	    yaw = payload.getFloat();
	    thrust = payload.getFloat();
	    target = payload.getByte();
	    roll_manual = payload.getByte();
	    pitch_manual = payload.getByte();
	    yaw_manual = payload.getByte();
	    thrust_manual = payload.getByte();    
    }

     /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_attitude_control(){
    	msgid = MAVLINK_MSG_ID_ATTITUDE_CONTROL;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     * 
     */
    public msg_attitude_control(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_ATTITUDE_CONTROL;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "ATTITUDE_CONTROL");
        //Log.d("MAVLINK_MSG_ID_ATTITUDE_CONTROL", toString());
    }
    
                  
    /**
     * Returns a string with the MSG name and data
     */
    public String toString(){
    	return "MAVLINK_MSG_ID_ATTITUDE_CONTROL -"+" roll:"+roll+" pitch:"+pitch+" yaw:"+yaw+" thrust:"+thrust+" target:"+target+" roll_manual:"+roll_manual+" pitch_manual:"+pitch_manual+" yaw_manual:"+yaw_manual+" thrust_manual:"+thrust_manual+"";
    }
}
