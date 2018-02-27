// MESSAGE IMAGE_TRIGGER_CONTROL PACKING
package com.jeremydyer.mavlink.Messages.pixhawk;

import com.jeremydyer.mavlink.Messages.MAVLinkMessage;
import com.jeremydyer.mavlink.Messages.MAVLinkPacket;
import com.jeremydyer.mavlink.Messages.MAVLinkPayload;

/**
* 
*/
public class msg_image_trigger_control extends MAVLinkMessage{

	public static final int MAVLINK_MSG_ID_IMAGE_TRIGGER_CONTROL = 153;
	public static final int MAVLINK_MSG_LENGTH = 1;
	private static final long serialVersionUID = MAVLINK_MSG_ID_IMAGE_TRIGGER_CONTROL;
	

 	/**
	* 0 to disable, 1 to enable
	*/
	public byte enable; 

	/**
	 * Generates the payload for a mavlink message for a message of this type
	 * @return
	 */
	public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_IMAGE_TRIGGER_CONTROL;
		packet.payload.putByte(enable);
		return packet;		
	}

    /**
     * Decode a image_trigger_control message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
	    enable = payload.getByte();    
    }

     /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_image_trigger_control(){
    	msgid = MAVLINK_MSG_ID_IMAGE_TRIGGER_CONTROL;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     * 
     */
    public msg_image_trigger_control(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_IMAGE_TRIGGER_CONTROL;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "IMAGE_TRIGGER_CONTROL");
        //Log.d("MAVLINK_MSG_ID_IMAGE_TRIGGER_CONTROL", toString());
    }
    
  
    /**
     * Returns a string with the MSG name and data
     */
    public String toString(){
    	return "MAVLINK_MSG_ID_IMAGE_TRIGGER_CONTROL -"+" enable:"+enable+"";
    }
}
