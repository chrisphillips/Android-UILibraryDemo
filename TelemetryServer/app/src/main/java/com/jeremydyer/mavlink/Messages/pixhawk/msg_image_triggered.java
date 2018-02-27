// MESSAGE IMAGE_TRIGGERED PACKING
package com.jeremydyer.mavlink.Messages.pixhawk;

import com.jeremydyer.mavlink.Messages.MAVLinkMessage;
import com.jeremydyer.mavlink.Messages.MAVLinkPacket;
import com.jeremydyer.mavlink.Messages.MAVLinkPayload;

/**
* 
*/
public class msg_image_triggered extends MAVLinkMessage{

	public static final int MAVLINK_MSG_ID_IMAGE_TRIGGERED = 152;
	public static final int MAVLINK_MSG_LENGTH = 52;
	private static final long serialVersionUID = MAVLINK_MSG_ID_IMAGE_TRIGGERED;
	

 	/**
	* Timestamp
	*/
	public long timestamp; 
 	/**
	* IMU seq
	*/
	public int seq; 
 	/**
	* Roll angle in rad
	*/
	public float roll; 
 	/**
	* Pitch angle in rad
	*/
	public float pitch; 
 	/**
	* Yaw angle in rad
	*/
	public float yaw; 
 	/**
	* Local frame Z coordinate (height over ground)
	*/
	public float local_z; 
 	/**
	* GPS X coordinate
	*/
	public float lat; 
 	/**
	* GPS Y coordinate
	*/
	public float lon; 
 	/**
	* Global frame altitude
	*/
	public float alt; 
 	/**
	* Ground truth X
	*/
	public float ground_x; 
 	/**
	* Ground truth Y
	*/
	public float ground_y; 
 	/**
	* Ground truth Z
	*/
	public float ground_z; 

	/**
	 * Generates the payload for a mavlink message for a message of this type
	 * @return
	 */
	public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_IMAGE_TRIGGERED;
		packet.payload.putLong(timestamp);
		packet.payload.putInt(seq);
		packet.payload.putFloat(roll);
		packet.payload.putFloat(pitch);
		packet.payload.putFloat(yaw);
		packet.payload.putFloat(local_z);
		packet.payload.putFloat(lat);
		packet.payload.putFloat(lon);
		packet.payload.putFloat(alt);
		packet.payload.putFloat(ground_x);
		packet.payload.putFloat(ground_y);
		packet.payload.putFloat(ground_z);
		return packet;		
	}

    /**
     * Decode a image_triggered message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
	    timestamp = payload.getLong();
	    seq = payload.getInt();
	    roll = payload.getFloat();
	    pitch = payload.getFloat();
	    yaw = payload.getFloat();
	    local_z = payload.getFloat();
	    lat = payload.getFloat();
	    lon = payload.getFloat();
	    alt = payload.getFloat();
	    ground_x = payload.getFloat();
	    ground_y = payload.getFloat();
	    ground_z = payload.getFloat();    
    }

     /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_image_triggered(){
    	msgid = MAVLINK_MSG_ID_IMAGE_TRIGGERED;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     * 
     */
    public msg_image_triggered(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_IMAGE_TRIGGERED;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "IMAGE_TRIGGERED");
        //Log.d("MAVLINK_MSG_ID_IMAGE_TRIGGERED", toString());
    }
    
                        
    /**
     * Returns a string with the MSG name and data
     */
    public String toString(){
    	return "MAVLINK_MSG_ID_IMAGE_TRIGGERED -"+" timestamp:"+timestamp+" seq:"+seq+" roll:"+roll+" pitch:"+pitch+" yaw:"+yaw+" local_z:"+local_z+" lat:"+lat+" lon:"+lon+" alt:"+alt+" ground_x:"+ground_x+" ground_y:"+ground_y+" ground_z:"+ground_z+"";
    }
}
