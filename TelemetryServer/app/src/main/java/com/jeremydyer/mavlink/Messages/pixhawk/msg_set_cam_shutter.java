// MESSAGE SET_CAM_SHUTTER PACKING
package com.jeremydyer.mavlink.Messages.pixhawk;

import com.jeremydyer.mavlink.Messages.MAVLinkMessage;
import com.jeremydyer.mavlink.Messages.MAVLinkPacket;
import com.jeremydyer.mavlink.Messages.MAVLinkPayload;

/**
* 
*/
public class msg_set_cam_shutter extends MAVLinkMessage{

	public static final int MAVLINK_MSG_ID_SET_CAM_SHUTTER = 151;
	public static final int MAVLINK_MSG_LENGTH = 11;
	private static final long serialVersionUID = MAVLINK_MSG_ID_SET_CAM_SHUTTER;
	

 	/**
	* Camera gain
	*/
	public float gain; 
 	/**
	* Shutter interval, in microseconds
	*/
	public short interval; 
 	/**
	* Exposure time, in microseconds
	*/
	public short exposure; 
 	/**
	* Camera id
	*/
	public byte cam_no; 
 	/**
	* Camera mode: 0 = auto, 1 = manual
	*/
	public byte cam_mode; 
 	/**
	* Trigger pin, 0-3 for PtGrey FireFly
	*/
	public byte trigger_pin; 

	/**
	 * Generates the payload for a mavlink message for a message of this type
	 * @return
	 */
	public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_SET_CAM_SHUTTER;
		packet.payload.putFloat(gain);
		packet.payload.putShort(interval);
		packet.payload.putShort(exposure);
		packet.payload.putByte(cam_no);
		packet.payload.putByte(cam_mode);
		packet.payload.putByte(trigger_pin);
		return packet;		
	}

    /**
     * Decode a set_cam_shutter message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
	    gain = payload.getFloat();
	    interval = payload.getShort();
	    exposure = payload.getShort();
	    cam_no = payload.getByte();
	    cam_mode = payload.getByte();
	    trigger_pin = payload.getByte();    
    }

     /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_set_cam_shutter(){
    	msgid = MAVLINK_MSG_ID_SET_CAM_SHUTTER;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     * 
     */
    public msg_set_cam_shutter(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_SET_CAM_SHUTTER;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "SET_CAM_SHUTTER");
        //Log.d("MAVLINK_MSG_ID_SET_CAM_SHUTTER", toString());
    }
    
            
    /**
     * Returns a string with the MSG name and data
     */
    public String toString(){
    	return "MAVLINK_MSG_ID_SET_CAM_SHUTTER -"+" gain:"+gain+" interval:"+interval+" exposure:"+exposure+" cam_no:"+cam_no+" cam_mode:"+cam_mode+" trigger_pin:"+trigger_pin+"";
    }
}
