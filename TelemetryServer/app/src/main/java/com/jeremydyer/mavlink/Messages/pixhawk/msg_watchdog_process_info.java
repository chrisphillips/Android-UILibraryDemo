// MESSAGE WATCHDOG_PROCESS_INFO PACKING
package com.jeremydyer.mavlink.Messages.pixhawk;

import com.jeremydyer.mavlink.Messages.MAVLinkMessage;
import com.jeremydyer.mavlink.Messages.MAVLinkPacket;
import com.jeremydyer.mavlink.Messages.MAVLinkPayload;

/**
* 
*/
public class msg_watchdog_process_info extends MAVLinkMessage{

	public static final int MAVLINK_MSG_ID_WATCHDOG_PROCESS_INFO = 181;
	public static final int MAVLINK_MSG_LENGTH = 255;
	private static final long serialVersionUID = MAVLINK_MSG_ID_WATCHDOG_PROCESS_INFO;
	

 	/**
	* Timeout (seconds)
	*/
	public int timeout; 
 	/**
	* Watchdog ID
	*/
	public short watchdog_id; 
 	/**
	* Process ID
	*/
	public short process_id; 
 	/**
	* Process name
	*/
	public byte name[] = new byte[100]; 
 	/**
	* Process arguments
	*/
	public byte arguments[] = new byte[147]; 

	/**
	 * Generates the payload for a mavlink message for a message of this type
	 * @return
	 */
	public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_WATCHDOG_PROCESS_INFO;
		packet.payload.putInt(timeout);
		packet.payload.putShort(watchdog_id);
		packet.payload.putShort(process_id);
		 for (int i = 0; i < name.length; i++) {
                        packet.payload.putByte(name[i]);
            }
		 for (int i = 0; i < arguments.length; i++) {
                        packet.payload.putByte(arguments[i]);
            }
		return packet;		
	}

    /**
     * Decode a watchdog_process_info message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
	    timeout = payload.getInt();
	    watchdog_id = payload.getShort();
	    process_id = payload.getShort();
	     for (int i = 0; i < name.length; i++) {
			name[i] = payload.getByte();
		}
	     for (int i = 0; i < arguments.length; i++) {
			arguments[i] = payload.getByte();
		}    
    }

     /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_watchdog_process_info(){
    	msgid = MAVLINK_MSG_ID_WATCHDOG_PROCESS_INFO;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     * 
     */
    public msg_watchdog_process_info(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_WATCHDOG_PROCESS_INFO;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "WATCHDOG_PROCESS_INFO");
        //Log.d("MAVLINK_MSG_ID_WATCHDOG_PROCESS_INFO", toString());
    }
    
       /**
     * Sets the buffer of this message with a string, adds the necessary padding
     */    
    public void setName(String str) {
      int len = Math.min(str.length(), 100);
      for (int i=0; i<len; i++) {
        name[i] = (byte) str.charAt(i);
      }
      for (int i=len; i<100; i++) {			// padding for the rest of the buffer
        name[i] = 0;
      }
    }
    
    /**
	 * Gets the message, formated as a string
	 */
	public String getName() {
		String result = "";
		for (int i = 0; i < 100; i++) {
			if (name[i] != 0)
				result = result + (char) name[i];
			else
				break;
		}
		return result;
		
	}  /**
     * Sets the buffer of this message with a string, adds the necessary padding
     */    
    public void setArguments(String str) {
      int len = Math.min(str.length(), 147);
      for (int i=0; i<len; i++) {
        arguments[i] = (byte) str.charAt(i);
      }
      for (int i=len; i<147; i++) {			// padding for the rest of the buffer
        arguments[i] = 0;
      }
    }
    
    /**
	 * Gets the message, formated as a string
	 */
	public String getArguments() {
		String result = "";
		for (int i = 0; i < 147; i++) {
			if (arguments[i] != 0)
				result = result + (char) arguments[i];
			else
				break;
		}
		return result;
		
	} 
    /**
     * Returns a string with the MSG name and data
     */
    public String toString(){
    	return "MAVLINK_MSG_ID_WATCHDOG_PROCESS_INFO -"+" timeout:"+timeout+" watchdog_id:"+watchdog_id+" process_id:"+process_id+" name:"+name+" arguments:"+arguments+"";
    }
}
