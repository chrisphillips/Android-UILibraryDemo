// MESSAGE BRIEF_FEATURE PACKING
package com.jeremydyer.mavlink.Messages.pixhawk;

import com.jeremydyer.mavlink.Messages.MAVLinkMessage;
import com.jeremydyer.mavlink.Messages.MAVLinkPacket;
import com.jeremydyer.mavlink.Messages.MAVLinkPayload;

/**
* 
*/
public class msg_brief_feature extends MAVLinkMessage{

	public static final int MAVLINK_MSG_ID_BRIEF_FEATURE = 195;
	public static final int MAVLINK_MSG_LENGTH = 53;
	private static final long serialVersionUID = MAVLINK_MSG_ID_BRIEF_FEATURE;
	

 	/**
	* x position in m
	*/
	public float x; 
 	/**
	* y position in m
	*/
	public float y; 
 	/**
	* z position in m
	*/
	public float z; 
 	/**
	* Harris operator response at this location
	*/
	public float response; 
 	/**
	* Size in pixels
	*/
	public short size; 
 	/**
	* Orientation
	*/
	public short orientation; 
 	/**
	* Orientation assignment 0: false, 1:true
	*/
	public byte orientation_assignment; 
 	/**
	* Descriptor
	*/
	public byte descriptor[] = new byte[32]; 

	/**
	 * Generates the payload for a mavlink message for a message of this type
	 * @return
	 */
	public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_BRIEF_FEATURE;
		packet.payload.putFloat(x);
		packet.payload.putFloat(y);
		packet.payload.putFloat(z);
		packet.payload.putFloat(response);
		packet.payload.putShort(size);
		packet.payload.putShort(orientation);
		packet.payload.putByte(orientation_assignment);
		 for (int i = 0; i < descriptor.length; i++) {
                        packet.payload.putByte(descriptor[i]);
            }
		return packet;		
	}

    /**
     * Decode a brief_feature message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
	    x = payload.getFloat();
	    y = payload.getFloat();
	    z = payload.getFloat();
	    response = payload.getFloat();
	    size = payload.getShort();
	    orientation = payload.getShort();
	    orientation_assignment = payload.getByte();
	     for (int i = 0; i < descriptor.length; i++) {
			descriptor[i] = payload.getByte();
		}    
    }

     /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_brief_feature(){
    	msgid = MAVLINK_MSG_ID_BRIEF_FEATURE;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     * 
     */
    public msg_brief_feature(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_BRIEF_FEATURE;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "BRIEF_FEATURE");
        //Log.d("MAVLINK_MSG_ID_BRIEF_FEATURE", toString());
    }
    
                
    /**
     * Returns a string with the MSG name and data
     */
    public String toString(){
    	return "MAVLINK_MSG_ID_BRIEF_FEATURE -"+" x:"+x+" y:"+y+" z:"+z+" response:"+response+" size:"+size+" orientation:"+orientation+" orientation_assignment:"+orientation_assignment+" descriptor:"+descriptor+"";
    }
}
