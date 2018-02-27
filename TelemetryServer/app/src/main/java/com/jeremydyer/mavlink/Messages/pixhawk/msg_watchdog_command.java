// MESSAGE WATCHDOG_COMMAND PACKING
package com.jeremydyer.mavlink.Messages.pixhawk;

import com.jeremydyer.mavlink.Messages.MAVLinkMessage;
import com.jeremydyer.mavlink.Messages.MAVLinkPacket;
import com.jeremydyer.mavlink.Messages.MAVLinkPayload;

/**
* 
*/
public class msg_watchdog_command extends MAVLinkMessage{

	public static final int MAVLINK_MSG_ID_WATCHDOG_COMMAND = 183;
	public static final int MAVLINK_MSG_LENGTH = 6;
	private static final long serialVersionUID = MAVLINK_MSG_ID_WATCHDOG_COMMAND;
	

 	/**
	* Watchdog ID
	*/
	public short watchdog_id; 
 	/**
	* Process ID
	*/
	public short process_id; 
 	/**
	* Target system ID
	*/
	public byte target_system_id; 
 	/**
	* Command ID
	*/
	public byte command_id; 

	/**
	 * Generates the payload for a mavlink message for a message of this type
	 * @return
	 */
	public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_WATCHDOG_COMMAND;
		packet.payload.putShort(watchdog_id);
		packet.payload.putShort(process_id);
		packet.payload.putByte(target_system_id);
		packet.payload.putByte(command_id);
		return packet;		
	}

    /**
     * Decode a watchdog_command message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
	    watchdog_id = payload.getShort();
	    process_id = payload.getShort();
	    target_system_id = payload.getByte();
	    command_id = payload.getByte();    
    }

     /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_watchdog_command(){
    	msgid = MAVLINK_MSG_ID_WATCHDOG_COMMAND;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     * 
     */
    public msg_watchdog_command(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_WATCHDOG_COMMAND;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "WATCHDOG_COMMAND");
        //Log.d("MAVLINK_MSG_ID_WATCHDOG_COMMAND", toString());
    }
    
        
    /**
     * Returns a string with the MSG name and data
     */
    public String toString(){
    	return "MAVLINK_MSG_ID_WATCHDOG_COMMAND -"+" watchdog_id:"+watchdog_id+" process_id:"+process_id+" target_system_id:"+target_system_id+" command_id:"+command_id+"";
    }
}
