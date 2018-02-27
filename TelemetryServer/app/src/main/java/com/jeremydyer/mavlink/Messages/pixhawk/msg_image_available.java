// MESSAGE IMAGE_AVAILABLE PACKING
package com.jeremydyer.mavlink.Messages.pixhawk;

import com.jeremydyer.mavlink.Messages.MAVLinkMessage;
import com.jeremydyer.mavlink.Messages.MAVLinkPacket;
import com.jeremydyer.mavlink.Messages.MAVLinkPayload;

/**
* 
*/
public class msg_image_available extends MAVLinkMessage{

	public static final int MAVLINK_MSG_ID_IMAGE_AVAILABLE = 154;
	public static final int MAVLINK_MSG_LENGTH = 92;
	private static final long serialVersionUID = MAVLINK_MSG_ID_IMAGE_AVAILABLE;
	

 	/**
	* Camera id
	*/
	public long cam_id; 
 	/**
	* Timestamp
	*/
	public long timestamp; 
 	/**
	* Until which timestamp this buffer will stay valid
	*/
	public long valid_until; 
 	/**
	* The image sequence number
	*/
	public int img_seq; 
 	/**
	* Position of the image in the buffer, starts with 0
	*/
	public int img_buf_index; 
 	/**
	* Shared memory area key
	*/
	public int key; 
 	/**
	* Exposure time, in microseconds
	*/
	public int exposure; 
 	/**
	* Camera gain
	*/
	public float gain; 
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
	* Image width
	*/
	public short width; 
 	/**
	* Image height
	*/
	public short height; 
 	/**
	* Image depth
	*/
	public short depth; 
 	/**
	* Camera # (starts with 0)
	*/
	public byte cam_no; 
 	/**
	* Image channels
	*/
	public byte channels; 

	/**
	 * Generates the payload for a mavlink message for a message of this type
	 * @return
	 */
	public MAVLinkPacket pack(){
		MAVLinkPacket packet = new MAVLinkPacket();
		packet.len = MAVLINK_MSG_LENGTH;
		packet.sysid = 255;
		packet.compid = 190;
		packet.msgid = MAVLINK_MSG_ID_IMAGE_AVAILABLE;
		packet.payload.putLong(cam_id);
		packet.payload.putLong(timestamp);
		packet.payload.putLong(valid_until);
		packet.payload.putInt(img_seq);
		packet.payload.putInt(img_buf_index);
		packet.payload.putInt(key);
		packet.payload.putInt(exposure);
		packet.payload.putFloat(gain);
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
		packet.payload.putShort(width);
		packet.payload.putShort(height);
		packet.payload.putShort(depth);
		packet.payload.putByte(cam_no);
		packet.payload.putByte(channels);
		return packet;		
	}

    /**
     * Decode a image_available message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
	    cam_id = payload.getLong();
	    timestamp = payload.getLong();
	    valid_until = payload.getLong();
	    img_seq = payload.getInt();
	    img_buf_index = payload.getInt();
	    key = payload.getInt();
	    exposure = payload.getInt();
	    gain = payload.getFloat();
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
	    width = payload.getShort();
	    height = payload.getShort();
	    depth = payload.getShort();
	    cam_no = payload.getByte();
	    channels = payload.getByte();    
    }

     /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_image_available(){
    	msgid = MAVLINK_MSG_ID_IMAGE_AVAILABLE;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     * 
     */
    public msg_image_available(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_IMAGE_AVAILABLE;
        unpack(mavLinkPacket.payload);
        //Log.d("MAVLink", "IMAGE_AVAILABLE");
        //Log.d("MAVLINK_MSG_ID_IMAGE_AVAILABLE", toString());
    }
    
                                              
    /**
     * Returns a string with the MSG name and data
     */
    public String toString(){
    	return "MAVLINK_MSG_ID_IMAGE_AVAILABLE -"+" cam_id:"+cam_id+" timestamp:"+timestamp+" valid_until:"+valid_until+" img_seq:"+img_seq+" img_buf_index:"+img_buf_index+" key:"+key+" exposure:"+exposure+" gain:"+gain+" roll:"+roll+" pitch:"+pitch+" yaw:"+yaw+" local_z:"+local_z+" lat:"+lat+" lon:"+lon+" alt:"+alt+" ground_x:"+ground_x+" ground_y:"+ground_y+" ground_z:"+ground_z+" width:"+width+" height:"+height+" depth:"+depth+" cam_no:"+cam_no+" channels:"+channels+"";
    }
}
