// MESSAGE WATCHDOG_HEARTBEAT PACKING
package com.jeremydyer.mavlink.Messages.pixhawk;

import com.jeremydyer.mavlink.Messages.MAVLinkMessage;
import com.jeremydyer.mavlink.Messages.MAVLinkPacket;
import com.jeremydyer.mavlink.Messages.MAVLinkPayload;

/**
* 
*/
public class msg_watchdog_heartbeat extends MAVLinkMessage{

	public static final int MAVLINK_MSG_ID_WATCHDOG_HEARTBEAT = 180;
	public static final int MAVLINK_MSG_LENGTH = 4;
	private static final long serialVersionUID = MAVLINK_MSG_ID_WATCHDOG_HEARTBEAT;
	

 	/**
	* Watchdog ID
	*/
	public short watchdog_id; 
 	/**
	* Number of processes
	*/
	public short process_count; 

	/**
	 * Generates the payload for a mavlink message for a message of this type
	 * @return
	 */
	public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_WATCHDOG_HEARTBEAT;
		packet.payload.putShort(watchdog_id);
		packet.payload.putShort(process_count);
		return packet;		
	}

    /**
     * Decode a watchdog_heartbeat message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
	    watchdog_id = payload.getShort();
	    process_count = payload.getShort();    
    }

     /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_watchdog_heartbeat(){
    	msgid = MAVLINK_MSG_ID_WATCHDOG_HEARTBEAT;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     * 
     */
    public msg_watchdog_heartbeat(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_WATCHDOG_HEARTBEAT;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "WATCHDOG_HEARTBEAT");
        //Log.d("MAVLINK_MSG_ID_WATCHDOG_HEARTBEAT", toString());
    }
    
    
    /**
     * Returns a string with the MSG name and data
     */
    public String toString(){
    	return "MAVLINK_MSG_ID_WATCHDOG_HEARTBEAT -"+" watchdog_id:"+watchdog_id+" process_count:"+process_count+"";
    }
}
