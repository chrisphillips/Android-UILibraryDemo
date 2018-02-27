// MESSAGE RAW_AUX PACKING
package com.jeremydyer.mavlink.Messages.pixhawk;

import com.jeremydyer.mavlink.Messages.MAVLinkMessage;
import com.jeremydyer.mavlink.Messages.MAVLinkPacket;
import com.jeremydyer.mavlink.Messages.MAVLinkPayload;

/**
* 
*/
public class msg_raw_aux extends MAVLinkMessage{

	public static final int MAVLINK_MSG_ID_RAW_AUX = 172;
	public static final int MAVLINK_MSG_LENGTH = 16;
	private static final long serialVersionUID = MAVLINK_MSG_ID_RAW_AUX;
	

 	/**
	* Barometric pressure (hecto Pascal)
	*/
	public int baro; 
 	/**
	* ADC1 (J405 ADC3, LPC2148 AD0.6)
	*/
	public short adc1; 
 	/**
	* ADC2 (J405 ADC5, LPC2148 AD0.2)
	*/
	public short adc2; 
 	/**
	* ADC3 (J405 ADC6, LPC2148 AD0.1)
	*/
	public short adc3; 
 	/**
	* ADC4 (J405 ADC7, LPC2148 AD1.3)
	*/
	public short adc4; 
 	/**
	* Battery voltage
	*/
	public short vbat; 
 	/**
	* Temperature (degrees celcius)
	*/
	public short temp; 

	/**
	 * Generates the payload for a mavlink message for a message of this type
	 * @return
	 */
	public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_RAW_AUX;
		packet.payload.putInt(baro);
		packet.payload.putShort(adc1);
		packet.payload.putShort(adc2);
		packet.payload.putShort(adc3);
		packet.payload.putShort(adc4);
		packet.payload.putShort(vbat);
		packet.payload.putShort(temp);
		return packet;		
	}

    /**
     * Decode a raw_aux message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
	    baro = payload.getInt();
	    adc1 = payload.getShort();
	    adc2 = payload.getShort();
	    adc3 = payload.getShort();
	    adc4 = payload.getShort();
	    vbat = payload.getShort();
	    temp = payload.getShort();    
    }

     /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_raw_aux(){
    	msgid = MAVLINK_MSG_ID_RAW_AUX;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     * 
     */
    public msg_raw_aux(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_RAW_AUX;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "RAW_AUX");
        //Log.d("MAVLINK_MSG_ID_RAW_AUX", toString());
    }
    
              
    /**
     * Returns a string with the MSG name and data
     */
    public String toString(){
    	return "MAVLINK_MSG_ID_RAW_AUX -"+" baro:"+baro+" adc1:"+adc1+" adc2:"+adc2+" adc3:"+adc3+" adc4:"+adc4+" vbat:"+vbat+" temp:"+temp+"";
    }
}
