// MESSAGE PATTERN_DETECTED PACKING
package com.jeremydyer.mavlink.Messages.pixhawk;

import com.jeremydyer.mavlink.Messages.MAVLinkMessage;
import com.jeremydyer.mavlink.Messages.MAVLinkPacket;
import com.jeremydyer.mavlink.Messages.MAVLinkPayload;

/**
* 
*/
public class msg_pattern_detected extends MAVLinkMessage{

	public static final int MAVLINK_MSG_ID_PATTERN_DETECTED = 190;
	public static final int MAVLINK_MSG_LENGTH = 106;
	private static final long serialVersionUID = MAVLINK_MSG_ID_PATTERN_DETECTED;
	

 	/**
	* Confidence of detection
	*/
	public float confidence; 
 	/**
	* 0: Pattern, 1: Letter
	*/
	public byte type; 
 	/**
	* Pattern file name
	*/
	public byte file[] = new byte[100]; 
 	/**
	* Accepted as true detection, 0 no, 1 yes
	*/
	public byte detected; 

	/**
	 * Generates the payload for a mavlink message for a message of this type
	 * @return
	 */
	public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_PATTERN_DETECTED;
		packet.payload.putFloat(confidence);
		packet.payload.putByte(type);
		 for (int i = 0; i < file.length; i++) {
                        packet.payload.putByte(file[i]);
            }
		packet.payload.putByte(detected);
		return packet;		
	}

    /**
     * Decode a pattern_detected message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
	    confidence = payload.getFloat();
	    type = payload.getByte();
	     for (int i = 0; i < file.length; i++) {
			file[i] = payload.getByte();
		}
	    detected = payload.getByte();    
    }

     /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_pattern_detected(){
    	msgid = MAVLINK_MSG_ID_PATTERN_DETECTED;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     * 
     */
    public msg_pattern_detected(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_PATTERN_DETECTED;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "PATTERN_DETECTED");
        //Log.d("MAVLINK_MSG_ID_PATTERN_DETECTED", toString());
    }
    
     /**
     * Sets the buffer of this message with a string, adds the necessary padding
     */    
    public void setFile(String str) {
      int len = Math.min(str.length(), 100);
      for (int i=0; i<len; i++) {
        file[i] = (byte) str.charAt(i);
      }
      for (int i=len; i<100; i++) {			// padding for the rest of the buffer
        file[i] = 0;
      }
    }
    
    /**
	 * Gets the message, formated as a string
	 */
	public String getFile() {
		String result = "";
		for (int i = 0; i < 100; i++) {
			if (file[i] != 0)
				result = result + (char) file[i];
			else
				break;
		}
		return result;
		
	}   
    /**
     * Returns a string with the MSG name and data
     */
    public String toString(){
    	return "MAVLINK_MSG_ID_PATTERN_DETECTED -"+" confidence:"+confidence+" type:"+type+" file:"+file+" detected:"+detected+"";
    }
}
