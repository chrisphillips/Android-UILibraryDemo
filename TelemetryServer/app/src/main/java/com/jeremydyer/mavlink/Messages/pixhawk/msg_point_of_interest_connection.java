// MESSAGE POINT_OF_INTEREST_CONNECTION PACKING
package com.jeremydyer.mavlink.Messages.pixhawk;

import com.jeremydyer.mavlink.Messages.MAVLinkMessage;
import com.jeremydyer.mavlink.Messages.MAVLinkPacket;
import com.jeremydyer.mavlink.Messages.MAVLinkPayload;

/**
* Notifies the operator about the connection of two point of interests (POI). This can be anything detected by the
                system. This generic message is intented to help interfacing to generic visualizations and to display
                the POI on a map.
            
*/
public class msg_point_of_interest_connection extends MAVLinkMessage{

	public static final int MAVLINK_MSG_ID_POINT_OF_INTEREST_CONNECTION = 192;
	public static final int MAVLINK_MSG_LENGTH = 55;
	private static final long serialVersionUID = MAVLINK_MSG_ID_POINT_OF_INTEREST_CONNECTION;
	

 	/**
	* X1 Position
	*/
	public float xp1; 
 	/**
	* Y1 Position
	*/
	public float yp1; 
 	/**
	* Z1 Position
	*/
	public float zp1; 
 	/**
	* X2 Position
	*/
	public float xp2; 
 	/**
	* Y2 Position
	*/
	public float yp2; 
 	/**
	* Z2 Position
	*/
	public float zp2; 
 	/**
	* 0: no timeout, >1: timeout in seconds
	*/
	public short timeout; 
 	/**
	* 0: Notice, 1: Warning, 2: Critical, 3: Emergency, 4: Debug
	*/
	public byte type; 
 	/**
	* 0: blue, 1: yellow, 2: red, 3: orange, 4: green, 5: magenta
	*/
	public byte color; 
 	/**
	* 0: global, 1:local
	*/
	public byte coordinate_system; 
 	/**
	* POI connection name
	*/
	public byte name[] = new byte[26]; 

	/**
	 * Generates the payload for a mavlink message for a message of this type
	 * @return
	 */
	public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_POINT_OF_INTEREST_CONNECTION;
		packet.payload.putFloat(xp1);
		packet.payload.putFloat(yp1);
		packet.payload.putFloat(zp1);
		packet.payload.putFloat(xp2);
		packet.payload.putFloat(yp2);
		packet.payload.putFloat(zp2);
		packet.payload.putShort(timeout);
		packet.payload.putByte(type);
		packet.payload.putByte(color);
		packet.payload.putByte(coordinate_system);
		 for (int i = 0; i < name.length; i++) {
                        packet.payload.putByte(name[i]);
            }
		return packet;		
	}

    /**
     * Decode a point_of_interest_connection message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
	    xp1 = payload.getFloat();
	    yp1 = payload.getFloat();
	    zp1 = payload.getFloat();
	    xp2 = payload.getFloat();
	    yp2 = payload.getFloat();
	    zp2 = payload.getFloat();
	    timeout = payload.getShort();
	    type = payload.getByte();
	    color = payload.getByte();
	    coordinate_system = payload.getByte();
	     for (int i = 0; i < name.length; i++) {
			name[i] = payload.getByte();
		}    
    }

     /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_point_of_interest_connection(){
    	msgid = MAVLINK_MSG_ID_POINT_OF_INTEREST_CONNECTION;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     * 
     */
    public msg_point_of_interest_connection(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_POINT_OF_INTEREST_CONNECTION;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "POINT_OF_INTEREST_CONNECTION");
        //Log.d("MAVLINK_MSG_ID_POINT_OF_INTEREST_CONNECTION", toString());
    }
    
                     /**
     * Sets the buffer of this message with a string, adds the necessary padding
     */    
    public void setName(String str) {
      int len = Math.min(str.length(), 26);
      for (int i=0; i<len; i++) {
        name[i] = (byte) str.charAt(i);
      }
      for (int i=len; i<26; i++) {			// padding for the rest of the buffer
        name[i] = 0;
      }
    }
    
    /**
	 * Gets the message, formated as a string
	 */
	public String getName() {
		String result = "";
		for (int i = 0; i < 26; i++) {
			if (name[i] != 0)
				result = result + (char) name[i];
			else
				break;
		}
		return result;
		
	} 
    /**
     * Returns a string with the MSG name and data
     */
    public String toString(){
    	return "MAVLINK_MSG_ID_POINT_OF_INTEREST_CONNECTION -"+" xp1:"+xp1+" yp1:"+yp1+" zp1:"+zp1+" xp2:"+xp2+" yp2:"+yp2+" zp2:"+zp2+" timeout:"+timeout+" type:"+type+" color:"+color+" coordinate_system:"+coordinate_system+" name:"+name+"";
    }
}
