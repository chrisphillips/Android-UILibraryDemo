// MESSAGE WATCHDOG_PROCESS_STATUS PACKING
package com.jeremydyer.mavlink.Messages.pixhawk;

import com.jeremydyer.mavlink.Messages.MAVLinkMessage;
import com.jeremydyer.mavlink.Messages.MAVLinkPacket;
import com.jeremydyer.mavlink.Messages.MAVLinkPayload;

/**
* 
*/
public class msg_watchdog_process_status extends MAVLinkMessage{

	public static final int MAVLINK_MSG_ID_WATCHDOG_PROCESS_STATUS = 182;
	public static final int MAVLINK_MSG_LENGTH = 12;
	private static final long serialVersionUID = MAVLINK_MSG_ID_WATCHDOG_PROCESS_STATUS;
	

 	/**
	* PID
	*/
	public int pid; 
 	/**
	* Watchdog ID
	*/
	public short watchdog_id; 
 	/**
	* Process ID
	*/
	public short process_id; 
 	/**
	* Number of crashes
	*/
	public short crashes; 
 	/**
	* Is running / finished / suspended / crashed
	*/
	public byte state; 
 	/**
	* Is muted
	*/
	public byte muted; 

	/**
	 * Generates the payload for a mavlink message for a message of this type
	 * @return
	 */
	public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_WATCHDOG_PROCESS_STATUS;
		packet.payload.putInt(pid);
		packet.payload.putShort(watchdog_id);
		packet.payload.putShort(process_id);
		packet.payload.putShort(crashes);
		packet.payload.putByte(state);
		packet.payload.putByte(muted);
		return packet;		
	}

    /**
     * Decode a watchdog_process_status message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
	    pid = payload.getInt();
	    watchdog_id = payload.getShort();
	    process_id = payload.getShort();
	    crashes = payload.getShort();
	    state = payload.getByte();
	    muted = payload.getByte();    
    }

     /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_watchdog_process_status(){
    	msgid = MAVLINK_MSG_ID_WATCHDOG_PROCESS_STATUS;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     * 
     */
    public msg_watchdog_process_status(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_WATCHDOG_PROCESS_STATUS;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "WATCHDOG_PROCESS_STATUS");
        //Log.d("MAVLINK_MSG_ID_WATCHDOG_PROCESS_STATUS", toString());
    }
    
            
    /**
     * Returns a string with the MSG name and data
     */
    public String toString(){
    	return "MAVLINK_MSG_ID_WATCHDOG_PROCESS_STATUS -"+" pid:"+pid+" watchdog_id:"+watchdog_id+" process_id:"+process_id+" crashes:"+crashes+" state:"+state+" muted:"+muted+"";
    }
}
