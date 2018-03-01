package com.dji.telemetryserver;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.security.keystore.KeyInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import dji.common.LightbridgePIPPosition;
import dji.common.LightbridgeSecondaryVideoFormat;
import dji.common.Stick;
import dji.common.airlink.ChannelSelectionMode;
import dji.common.airlink.FrequencyInterference;
import dji.common.airlink.LightbridgeAntennaRSSI;
import dji.common.airlink.LightbridgeFrequencyBand;
import dji.common.airlink.LightbridgeSecondaryVideoDisplayMode;
import dji.common.airlink.LightbridgeSecondaryVideoOutputPort;
import dji.common.airlink.LightbridgeTransmissionMode;
import dji.common.airlink.LightbridgeUnit;
import dji.common.airlink.OcuSyncBandwidth;
import dji.common.airlink.OcuSyncWarningMessage;
import dji.common.airlink.WiFiFrequencyBand;
import dji.common.airlink.WiFiMagneticInterferenceLevel;
import dji.common.airlink.WifiChannelInterference;
import dji.common.airlink.WifiDataRate;
import dji.common.battery.AggregationState;
import dji.common.battery.BatteryCellVoltageLevel;
import dji.common.battery.BatteryOverview;
import dji.common.battery.ConnectionState;
import dji.common.battery.LowVoltageBehavior;
import dji.common.battery.WarningRecord;
import dji.common.camera.CameraRecordingState;
import dji.common.camera.CameraSSDVideoLicense;
import dji.common.camera.ExposureSettings;
import dji.common.camera.FocusAssistantSettings;
import dji.common.camera.PhotoTimeLapseSettings;
import dji.common.camera.ResolutionAndFrameRate;
import dji.common.camera.SSDCapacity;
import dji.common.camera.SSDOperationState;
import dji.common.camera.SettingsDefinitions;
import dji.common.camera.StabilizationState;
import dji.common.camera.ThermalAreaTemperatureAggregations;
import dji.common.camera.ThermalMeasurementMode;
import dji.common.camera.WhiteBalance;
import dji.common.error.DJIError;
import dji.common.flightcontroller.BatteryThresholdBehavior;
import dji.common.flightcontroller.CompassCalibrationState;
import dji.common.flightcontroller.ConnectionFailSafeBehavior;
import dji.common.flightcontroller.ControlGimbalBehavior;
import dji.common.flightcontroller.ControlMode;
import dji.common.flightcontroller.FlightMode;
import dji.common.flightcontroller.FlightOrientationMode;
import dji.common.flightcontroller.FlightWindWarning;
import dji.common.flightcontroller.GPSSignalLevel;
import dji.common.flightcontroller.GoHomeExecutionState;
import dji.common.flightcontroller.LandingGearMode;
import dji.common.flightcontroller.LandingGearState;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.common.flightcontroller.ObstacleDetectionSector;
import dji.common.flightcontroller.PositioningSolution;
import dji.common.flightcontroller.UrgentStopMotorMode;
import dji.common.flightcontroller.VisionDetectionState;
import dji.common.flightcontroller.VisionLandingProtectionState;
import dji.common.flightcontroller.VisionSystemWarning;
import dji.common.flightcontroller.imu.IMUState;
import dji.common.flightcontroller.imu.SensorState;
import dji.common.flightcontroller.simulator.InitializationData;
import dji.common.flightcontroller.simulator.SimulatorState;
import dji.common.gimbal.Attitude;
import dji.common.gimbal.BalanceTestResult;
import dji.common.gimbal.GimbalMode;
import dji.common.gimbal.MotorControlPreset;
import dji.common.gimbal.MovementSettingsProfile;
import dji.common.handheld.PowerMode;
import dji.common.handheld.RecordAndShutterButtons;
import dji.common.handheld.StickHorizontalDirection;
import dji.common.handheld.StickVerticalDirection;
import dji.common.handheld.TriggerButton;
import dji.common.handheldcontroller.ControllerMode;
import dji.common.mission.activetrack.ActiveTrackMode;
import dji.common.model.LocationCoordinate2D;
import dji.common.product.Model;
import dji.common.remotecontroller.AircraftMapping;
import dji.common.remotecontroller.AircraftMappingStyle;
import dji.common.remotecontroller.AuthorizationInfo;
import dji.common.remotecontroller.ChargeMobileMode;
import dji.common.remotecontroller.ChargeRemaining;
import dji.common.remotecontroller.Credentials;
import dji.common.remotecontroller.CustomButtonTags;
import dji.common.remotecontroller.FocusControllerState;
import dji.common.remotecontroller.GPSData;
import dji.common.remotecontroller.GimbalAxis;
import dji.common.remotecontroller.GimbalControlSpeedCoefficient;
import dji.common.remotecontroller.GimbalMapping;
import dji.common.remotecontroller.GimbalMappingStyle;
import dji.common.remotecontroller.HardwareState;
import dji.common.remotecontroller.Information;
import dji.common.remotecontroller.MasterSlaveState;
import dji.common.remotecontroller.PairingState;
import dji.common.remotecontroller.ProfessionalRC;
import dji.common.remotecontroller.RCMode;
import dji.common.remotecontroller.RequestGimbalControlResult;
import dji.common.remotecontroller.ResponseForGimbalControl;
import dji.keysdk.AirLinkKey;
import dji.keysdk.BatteryKey;
import dji.keysdk.CameraKey;
import dji.keysdk.DJIKey;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.GimbalKey;
import dji.keysdk.HandheldControllerKey;
import dji.keysdk.KeyManager;
import dji.keysdk.ProductKey;
import dji.keysdk.RemoteControllerKey;
import dji.keysdk.callback.GetCallback;
import dji.keysdk.callback.KeyListener;
import dji.sdk.sdkmanager.DJISDKManager;

import static com.dji.telemetryserver.DJIKeyedInterface.DJIKeyInfo.KeyAccessTypes.*;


/**
 * Created by chris on 2/23/2018.
 */

public class DJIKeyedInterface {

    //Wrapper around the DJIKey.
    //associates the type and keyName
    //for the log message callback.
    public static class DJIKeyInfo {

        public enum KeyAccessTypes {
            GET, SET, PUSH, ACTION
        }

        public String controllerName;//FlightController,Camera etc.
        public String keyName;//String name of the key for the message.
        public KeyAccessTypes accessType;//GET,SET,PUSH,ACTION
        public Object keyType;//not used for anything yet.
        public DJIKey key;//The key that will be passed to the listner.
        public KeyListener listener=null;//Callback object.


        //Build a key from strings.
        public DJIKeyInfo(String controllerName, String keyName, KeyAccessTypes accessType, Object keyType) {
            this.controllerName = controllerName;
            this.keyName = keyName;
            this.accessType = accessType;
            this.keyType = keyType;
            if (controllerName == "FlightController") {
                key = FlightControllerKey.create(keyName);
            } else if (controllerName == "RemoteController") {
                key = RemoteControllerKey.create(keyName);
            } else if (controllerName == "Camera") {
                key = CameraKey.create(keyName);
            } else if (controllerName == "Gimbal") {
                key = GimbalKey.create(keyName);
            } else if (controllerName == "Product") {
                key = ProductKey.create(keyName);
            } else if (controllerName == "Battery") {
                key = BatteryKey.create(keyName);
            } else if (controllerName == "Airlink") {
                key = AirLinkKey.create(keyName);
            }

        }
    }

    //Lookup by name for installed key listeners.
    private static HashMap<String,DJIKeyInfo> keyListeners= new HashMap<String, DJIKeyInfo>();

    //Convert the value that comes back frome the listeners to a string for the log message.
        //Need to add support for new types as they are found.
    public static String DJIValueToString(Object value) {
        if (value == null)
            return "null";

        if (value instanceof Float) {
            return String.valueOf((Float) value);
        } else if (value instanceof ChargeRemaining) {
            return String.valueOf(((ChargeRemaining) value).getRemainingChargeInPercent());
        } else if (value instanceof Stick) {
            Stick stick = (Stick) value;
            String msg = "" + stick.getHorizontalPosition() + "," + stick.getVerticalPosition();
            return msg;
        } else if (value instanceof HardwareState.Button) {
            HardwareState.Button button = (HardwareState.Button) value;
            String msg = "" + button.isClicked();
            return msg;
        } else if (value instanceof HardwareState.TransformationSwitch) {
            HardwareState.TransformationSwitch button = (HardwareState.TransformationSwitch) value;
            String msg = "" + button.getState();
            return msg;
        } else if (value instanceof LocationCoordinate3D) {
            LocationCoordinate3D loc = (LocationCoordinate3D) value;
            String msg = "" + loc.getLatitude()+","+loc.getLongitude()+","+loc.getAltitude();
            return msg;
        } else if (value instanceof LocationCoordinate2D) {
            LocationCoordinate2D loc = (LocationCoordinate2D) value;
            String msg = "" + loc.getLatitude()+","+loc.getLongitude();
            return msg;
        } else if (value instanceof Attitude) {
            Attitude att = (Attitude) value;
            String msg = "" + att.getPitch()+","+att.getRoll()+","+att.getYaw();
            return msg;
        } else if (value instanceof WhiteBalance) {
            WhiteBalance wb = (WhiteBalance) value;
            String msg = "" + wb.getWhiteBalancePreset();
            return msg;
        } else if (value instanceof SimulatorState) {
            SimulatorState ss = (SimulatorState) value;
            String msg = "motorsOn:" + ss.areMotorsOn()+",isFlying:"+ss.isFlying();
            return msg;
        } else {
            return value.toString();
        }
    }
    //Start listeners on important keys.
    //Logs a PUSH message each time a value changes.
    public static void initListeners()
    {
        startListeners(importantKeys);
    }
    //Listen to ALL sdk keys.
    public static void startAllListeners()
    {
        startListeners(allKeys);
    }

    //Start a set of listeners.
    private static void startListeners(DJIKeyInfo[] keys){
        if (DJISDKManager.getInstance() == null || DJISDKManager.getInstance().getKeyManager() == null) {
            TelemetryService.Log("ERROR startListeners() before SDK is ready.");
            return;//not ready yet.
        }
        KeyManager keyManager = DJISDKManager.getInstance().getKeyManager();
        for (DJIKeyInfo keyinfo : keys) {
            if (keyinfo.accessType == GET || keyinfo.accessType == PUSH) {
                String keyName = keyinfo.controllerName + "." + keyinfo.keyName;

                if(keyListeners.containsKey(keyinfo.keyName))
                {
                    continue;//already installed.
                }
                KeyListener listener = new KeyListener() {
                    @Override public void onValueChange(@Nullable Object oldValue, @Nullable Object newValue) {
                        //Format the change message
                        TelemetryService.Log("PSH:"+keyName + "=" + DJIValueToString(newValue));
                    };
                };

                //install listener callback for key.
                DJISDKManager.getInstance().getKeyManager().addListener(keyinfo.key, listener);

                //save listener in info so we can remove listener if needed.
                keyinfo.listener=listener;
                keyListeners.put(keyinfo.keyName,keyinfo);
            }
        }
    }

    //Do a get of all keys that have a listener.
    //Used to get the inital state.
    public static void doGetAll() {
        if (DJISDKManager.getInstance() == null || DJISDKManager.getInstance().getKeyManager() == null){
            TelemetryService.Log("WARN doGetAll() before SDK ready.");
            return;//not ready yet.
        }

        KeyManager keyManager = DJISDKManager.getInstance().getKeyManager();
        for (DJIKeyInfo keyinfo : keyListeners.values()) {
            if (keyinfo.accessType == GET || keyinfo.accessType == PUSH) {
                String keyName = keyinfo.controllerName + "." + keyinfo.keyName;
                Object value = keyManager.getValue(keyinfo.key);
                String valueStr = DJIValueToString(value);
                TelemetryService.Log("GET:"+keyName + "=" + valueStr);
            }
        }
    }

    static DJIKeyInfo importantKeys[] = new DJIKeyInfo[]{
            new DJIKeyInfo("FlightController", FlightControllerKey.SIMULATOR_STATE, PUSH, SimulatorState.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.IS_SIMULATOR_ACTIVE, PUSH, Boolean.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.AIRCRAFT_LOCATION, PUSH, LocationCoordinate3D.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.ALTITUDE, PUSH, Float.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.ATTITUDE_YAW, PUSH, Double.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.ATTITUDE_PITCH, PUSH, Double.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.ATTITUDE_ROLL, PUSH, Double.class),
//same as yaw?            new DJIKeyInfo("FlightController", FlightControllerKey.COMPASS_HEADING, PUSH, Float.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.VELOCITY_Y, PUSH, Float.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.VELOCITY_X, PUSH, Float.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.VELOCITY_Z, PUSH, Float.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.SATELLITE_COUNT, PUSH, Integer.class),

            new DJIKeyInfo("FlightController", FlightControllerKey.HOME_LOCATION_LATITUDE, PUSH, Double.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.HOME_POINT_ALTITUDE, PUSH, Float.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.HOME_LOCATION_LONGITUDE, PUSH, Double.class),

            new DJIKeyInfo("FlightController", FlightControllerKey.REMAINING_FLIGHT_TIME, PUSH, Integer.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.GPS_SIGNAL_LEVEL, PUSH, GPSSignalLevel.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.FLIGHT_MODE, PUSH, FlightMode.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.FLY_TIME_IN_SECONDS, PUSH, Integer.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.ULTRASONIC_HEIGHT_IN_METERS, PUSH, Float.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.GO_HOME_STATUS, PUSH, GoHomeExecutionState.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.IS_FLYING, PUSH, Boolean.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.ARE_MOTOR_ON, PUSH, Boolean.class),

            new DJIKeyInfo("FlightController", FlightControllerKey.HOME_LOCATION, GET, LocationCoordinate2D.class),
//null            new DJIKeyInfo("FlightController", FlightControllerKey.CONTROL_MODE, GET, ControlMode.class),
            new DJIKeyInfo("FlightController", FlightControllerKey.GO_HOME_HEIGHT_IN_METERS, GET, Integer.class),


            new DJIKeyInfo("RemoteController", RemoteControllerKey.GO_HOME_BUTTON, PUSH, HardwareState.Button.class),
            new DJIKeyInfo("RemoteController", RemoteControllerKey.CUSTOM_BUTTON_2, PUSH, HardwareState.Button.class),
            new DJIKeyInfo("RemoteController", RemoteControllerKey.CUSTOM_BUTTON_1, PUSH, HardwareState.Button.class),
            new DJIKeyInfo("RemoteController", RemoteControllerKey.SHUTTER_BUTTON, PUSH, HardwareState.Button.class),
            new DJIKeyInfo("RemoteController", RemoteControllerKey.LEFT_WHEEL, PUSH, Integer.class),
            new DJIKeyInfo("RemoteController", RemoteControllerKey.RIGHT_STICK_VALUE, PUSH, Stick.class),
            new DJIKeyInfo("RemoteController", RemoteControllerKey.RECORD_BUTTON, PUSH, HardwareState.Button.class),
            new DJIKeyInfo("RemoteController", RemoteControllerKey.LEFT_STICK_VALUE, PUSH, Stick.class),
            new DJIKeyInfo("RemoteController", RemoteControllerKey.PLAYBACK_BUTTON, PUSH, HardwareState.Button.class),
            new DJIKeyInfo("RemoteController", RemoteControllerKey.RIGHT_WHEEL, PUSH, HardwareState.RightWheel.class),
            new DJIKeyInfo("RemoteController", RemoteControllerKey.MODE, PUSH, RCMode.class),
            new DJIKeyInfo("RemoteController", RemoteControllerKey.FIVE_D_BUTTON, PUSH, HardwareState.FiveDButton.class),
            new DJIKeyInfo("RemoteController", RemoteControllerKey.FLIGHT_MODE_SWITCH_POSITION, PUSH, HardwareState.FlightModeSwitch.class),
            new DJIKeyInfo("RemoteController", RemoteControllerKey.CHARGE_REMAINING, PUSH, ChargeRemaining.class),
            new DJIKeyInfo("RemoteController", RemoteControllerKey.PAUSE_BUTTON, PUSH, HardwareState.Button.class),

            new DJIKeyInfo("Gimbal", GimbalKey.MODE, PUSH, GimbalMode.class),
            new DJIKeyInfo("Gimbal", GimbalKey.YAW_ANGLE_WITH_AIRCRAFT_IN_DEGREE, PUSH, Float.class),
            new DJIKeyInfo("Gimbal", GimbalKey.ATTITUDE_IN_DEGREES, PUSH, Attitude.class),

            new DJIKeyInfo("Camera", CameraKey.SHUTTER_SPEED, PUSH, SettingsDefinitions.ShutterSpeed.class),
//            new DJIKeyInfo("Camera", CameraKey.ISO_RANGE, PUSH, SettingsDefinitions.ISO[].class),
            new DJIKeyInfo("Camera", CameraKey.MODE, PUSH, SettingsDefinitions.CameraMode.class),
            new DJIKeyInfo("Camera", CameraKey.SDCARD_TOTAL_SPACE_IN_MB, PUSH, Integer.class),
//            new DJIKeyInfo("Camera", CameraKey.EXPOSURE_SETTINGS, PUSH, ExposureSettings.class),
            new DJIKeyInfo("Camera", CameraKey.CURRENT_VIDEO_RECORDING_TIME_IN_SECONDS, PUSH, Integer.class),
            new DJIKeyInfo("Camera", CameraKey.RESOLUTION_FRAME_RATE, PUSH, ResolutionAndFrameRate.class),
            new DJIKeyInfo("Camera", CameraKey.IS_RECORDING, PUSH, Boolean.class),
            new DJIKeyInfo("Camera", CameraKey.SDCARD_AVAILABLE_RECORDING_TIME_IN_SECONDS, PUSH, Integer.class),
            new DJIKeyInfo("Camera", CameraKey.FOCUS_MODE, PUSH, SettingsDefinitions.FocusMode.class),
            new DJIKeyInfo("Camera", CameraKey.SHOOT_PHOTO_MODE, PUSH, SettingsDefinitions.ShootPhotoMode.class),
            new DJIKeyInfo("Camera", CameraKey.PHOTO_FILE_FORMAT, PUSH, SettingsDefinitions.PhotoFileFormat.class),
            new DJIKeyInfo("Camera", CameraKey.SHARPNESS, PUSH, Integer.class),
            new DJIKeyInfo("Camera", CameraKey.ISO, PUSH, SettingsDefinitions.ISO.class),
            new DJIKeyInfo("Camera", CameraKey.FOCUS_STATUS, PUSH, SettingsDefinitions.FocusStatus.class),
            new DJIKeyInfo("Camera", CameraKey.DISPLAY_NAME, PUSH, String.class),
            new DJIKeyInfo("Camera", CameraKey.CONTRAST, PUSH, Integer.class),
            new DJIKeyInfo("Camera", CameraKey.SDCARD_REMAINING_SPACE_IN_MB, PUSH, Integer.class),
            new DJIKeyInfo("Camera", CameraKey.VIDEO_FILE_FORMAT, PUSH, SettingsDefinitions.VideoFileFormat.class),
            new DJIKeyInfo("Camera", CameraKey.WHITE_BALANCE, PUSH, WhiteBalance.class),
            new DJIKeyInfo("Camera", CameraKey.RECORDING_STATE, PUSH, CameraRecordingState.class),

            new DJIKeyInfo("Battery", BatteryKey.LIFETIME_REMAINING, PUSH, Integer.class),
            new DJIKeyInfo("Battery", BatteryKey.CURRENT, PUSH, Integer.class),
            new DJIKeyInfo("Battery", BatteryKey.CHARGE_REMAINING, PUSH, Integer.class),
            new DJIKeyInfo("Battery", BatteryKey.CHARGE_REMAINING_IN_PERCENT, PUSH, Integer.class),
            new DJIKeyInfo("Battery", BatteryKey.TEMPERATURE, PUSH, Float.class),
            new DJIKeyInfo("Battery", BatteryKey.VOLTAGE, PUSH, Integer.class),
            new DJIKeyInfo("Battery", BatteryKey.CELL_VOLTAGE_LEVEL, PUSH, BatteryCellVoltageLevel.class),
            new DJIKeyInfo("Battery", BatteryKey.FULL_CHARGE_CAPACITY, PUSH, Integer.class),

            new DJIKeyInfo("Product", ProductKey.MODEL_NAME, PUSH, Model.class),
            new DJIKeyInfo("Product", ProductKey.CONNECTION, PUSH, Boolean.class),

    };
        /*The following array is semi generated by running a script on the SDK documentation webpage. See bottom of file for Javascript generator
     */
        static DJIKeyInfo allKeys[] = new DJIKeyInfo[]{
                new DJIKeyInfo("FlightController", FlightControllerKey.HOTPOINT_MIN_RADIUS, GET, Float.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.HOTPOINT_MAX_ACCELERATION, GET, Float.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IMU_COUNT, GET, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.LANDING_GEAR_AUTOMATIC_MOVEMENT_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.VISION_ASSISTED_POSITIONING_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ADVANCED_GESTURE_CONTROL_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.LANDING_PROTECTION_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.PRECISION_LANDING_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.FLY_ZONE_LIMITATION_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.BINDING_STATE, GET, Object[].class),
                new DJIKeyInfo("FlightController", FlightControllerKey.QUICK_SPIN_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ACTIVE_TRACK_GPS_ASSISTANT_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.AIRCRAFT_NAME, GET, String.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ACTIVE_TRACK_CIRCULAR_SPEED, GET, Float.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.MAX_FLIGHT_HEIGHT, GET, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_VIRTUAL_STICK_CONTROL_MODE_AVAILABLE, GET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.URGENT_STOP_MOTOR_MODE, GET, UrgentStopMotorMode.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IMU_STATE, GET, IMUState.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.SMART_RETURN_TO_HOME_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.SERIAL_NUMBER, GET, String.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_FLIGHT_ASSISTANT_SUPPORTED, GET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.HOME_LOCATION, GET, LocationCoordinate2D.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.MAX_FLIGHT_RADIUS, GET, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.CONNECTION_FAIL_SAFE_BEHAVIOR, GET, ConnectionFailSafeBehavior.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ACTIVE_OBSTACLE_AVOIDANCE_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.MAX_FLIGHT_RADIUS_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.CONTROL_MODE, GET, ControlMode.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ENABLE_1860, GET, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.GO_HOME_HEIGHT_IN_METERS, GET, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.TERRAIN_FOLLOW_MODE_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.UPWARDS_AVOIDANCE_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.LEDS_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ACTIVE_TRACK_GESTURE_MODE_ENABLED, GET, Boolean.class),

                new DJIKeyInfo("FlightController", FlightControllerKey.IS_HOME_LOCATION_SET, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.VELOCITY_Y, PUSH, Float.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.VELOCITY_X, PUSH, Float.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.VELOCITY_Z, PUSH, Float.class),
//                new DJIKeyInfo("FlightController", FlightControllerKey.DETECTION_SECTORS, PUSH, ObstacleDetectionSector[].class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_SATELLITE_GLONASS_COUNT, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ACTIVE_TRACK_MODE, PUSH, ActiveTrackMode.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_VISION_POSITIONING_SENSOR_BEING_USED, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.COMPASS_HAS_ERROR, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_SATELLITE_BEIDOU_COUNT_IS_ON, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_GROUND_GLONASS_COUNT_IS_ON, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.SATELLITE_COUNT, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_LOWER_THAN_SERIOUS_BATTERY_WARNING_THRESHOLD, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_DIRECT_ANGLE, PUSH, Float.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ULTRASONIC_ERROR, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.AIRCRAFT_LOCATION_LATITUDE, PUSH, Double.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_PERFORMING_PRECISION_LANDING, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.REMAINING_FLIGHT_TIME, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.GPS_SIGNAL_LEVEL, PUSH, GPSSignalLevel.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_SATELLITE_GLONASS_COUNT_IS_ON, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.LANDING_GEAR_MODE, PUSH, LandingGearMode.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_GROUND_GLONASS_COUNT, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.FLIGHT_MODE, PUSH, FlightMode.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.TRIPOD_MODE_ENABLED, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.FLY_TIME_IN_SECONDS, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ULTRASONIC_HEIGHT_IN_METERS, PUSH, Float.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.GO_HOME_STATUS, PUSH, GoHomeExecutionState.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.LANDING_PROTECTION_STATE, PUSH, VisionLandingProtectionState.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.AIRCRAFT_LOCATION_LONGITUDE, PUSH, Double.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ATTITUDE_ROLL, PUSH, Double.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_LOWER_THAN_BATTERY_WARNING_THRESHOLD, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.COMPASS_HEADING, PUSH, Float.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_ENABLED, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_ERROR, PUSH, DJIError.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ORIENTATION_MODE, PUSH, FlightOrientationMode.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IMU_STATE_CALIBRATION_PROGRESS, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.FLIGHT_WIND_WARNING, PUSH, FlightWindWarning.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ATTITUDE_PITCH, PUSH, Double.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.AIRCRAFT_LOCATION, PUSH, LocationCoordinate3D.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_FLYING, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ALTITUDE, PUSH, Float.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_SATELLITE_GPS_COUNT_IS_ON, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_MAIN_GPS_COUNT, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_LANDING, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_GROUND_LATITUDE, PUSH, Double.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.SIMULATOR_STATE, PUSH, SimulatorState.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.POSITIONING_SOLUTION, PUSH, PositioningSolution.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.COMPASS_IS_CALIBRATING, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_AIR_ALTITUDE, PUSH, Float.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.CINEMATIC_MODE_ENABLED, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.HAS_REACHED_MAX_FLIGHT_HEIGHT, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.VIRTUAL_STICK_CONTROL_MODE_ENABLED, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_GROUND_BEIDOU_COUNT, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IMU_STATE_GYROSCOPE_STATE, PUSH, SensorState.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_MAIN_GLONASS_COUNT, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_GROUND_GPS_COUNT, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_MAIN_GPS_COUNT_IS_ON, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_AVOIDING_ACTIVE_OBSTACLE_COLLISION, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_ASCENT_LIMITED_BY_OBSTACLE, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_MAIN_BEIDOU_COUNT, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.HOME_LOCATION_LATITUDE, PUSH, Double.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ATTITUDE_YAW, PUSH, Double.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.CONTROL_GIMBAL_BEHAVIOR, PUSH, ControlGimbalBehavior.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.LOW_BATTERY_WARNING_THRESHOLD, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_GOING_HOME, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_GROUND_BEIDOU_COUNT_IS_ON, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_MAIN_BEIDOU_COUNT_IS_ON, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.LANDING_GEAR_STATUS, PUSH, LandingGearState.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.MULTI_MODE_OPEN, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_SIMULATOR_ACTIVE, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.HOME_POINT_ALTITUDE, PUSH, Float.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.HAS_REACHED_MAX_FLIGHT_RADIUS, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.NOVICE_MODE_ENABLED, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.COLLISION_AVOIDANCE_ENABLED, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.AIRCRAFT_SHOULD_GO_HOME, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_FAIL_SAFE, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_AIR_LATITUDE, PUSH, Double.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.TIME_NEEDED_TO_LAND_FROM_CURRENT_HEIGHT, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_IS_HEADING_VALID, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IMU_STATE_ACCELEROMETER_STATE, PUSH, SensorState.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_IMU_PREHEATING, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_SATELLITE_BEIDOU_COUNT, PUSH, Integer.class),
//                new DJIKeyInfo("FlightController", FlightControllerKey.VISION_DETECTION_STATE, PUSH, VisionDetectionState.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.FLIGHT_MODE_STRING, PUSH, String.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_BRAKING, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.INTELLIGENT_FLIGHT_ASSISTANT_GHAVOID_ENABLED, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.HOME_LOCATION_LONGITUDE, PUSH, Double.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.BATTERY_THRESHOLD_BEHAVIOR, PUSH, BatteryThresholdBehavior.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.CURRENT_LAND_IMMEDIATELY_BATTERY, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.BATTERY_PERCENTAGE_NEEDED_TO_GO_HOME, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.COMPASS_CALIBRATION_STATUS, PUSH, CompassCalibrationState.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_ULTRASONIC_BEING_USED, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.VISION_SYSTEM_WARNING, PUSH, VisionSystemWarning.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.TIME_NEEDED_TO_GO_HOME, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_GROUND_GPS_COUNT_IS_ON, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.GO_HOME_HEIGHT_IN_METERS, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_LANDING_CONFIRMATION_NEEDED, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ARE_MOTOR_ON, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_MAIN_GLONASS_COUNT_IS_ON, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.MAX_RADIUS_AIRCRAFT_CAN_FLY_AND_GO_HOME, PUSH, Float.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_AIR_LONGITUDE, PUSH, Double.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_GROUND_LONGITUDE, PUSH, Double.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.SERIOUS_LOW_BATTERY_WARNING_THRESHOLD, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_GROUND_ALTITUDE, PUSH, Float.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_LANDING_GEAR_MOVABLE, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RTK_SATELLITE_GPS_COUNT, PUSH, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.IS_ON_BOARD_SDK_AVAILABLE, PUSH, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.INTELLIGENT_FLIGHT_ASSISTANT_IS_USERAVOID_ENABLE, PUSH, Boolean.class),

                new DJIKeyInfo("FlightController", FlightControllerKey.ACTIVE_TRACK_MODE, SET, ActiveTrackMode.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.TRIPOD_MODE_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.LANDING_GEAR_AUTOMATIC_MOVEMENT_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ORIENTATION_MODE, SET, FlightOrientationMode.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.VISION_ASSISTED_POSITIONING_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ADVANCED_GESTURE_CONTROL_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.LANDING_PROTECTION_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.PRECISION_LANDING_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.CINEMATIC_MODE_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.VIRTUAL_STICK_CONTROL_MODE_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.FLY_ZONE_LIMITATION_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.BINDING_STATE, SET, Object[].class),
                new DJIKeyInfo("FlightController", FlightControllerKey.QUICK_SPIN_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ACTIVE_TRACK_GPS_ASSISTANT_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.AIRCRAFT_NAME, SET, String.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ACTIVE_TRACK_CIRCULAR_SPEED, SET, Float.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.MAX_FLIGHT_HEIGHT, SET, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.CONTROL_GIMBAL_BEHAVIOR, SET, ControlGimbalBehavior.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.LOW_BATTERY_WARNING_THRESHOLD, SET, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.URGENT_STOP_MOTOR_MODE, SET, UrgentStopMotorMode.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.MULTI_MODE_OPEN, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.NOVICE_MODE_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.COLLISION_AVOIDANCE_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.SMART_RETURN_TO_HOME_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.SET_ACTIVE_TRACK_CAMERA, SET, SettingsDefinitions.CameraType.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.HOME_LOCATION, SET, LocationCoordinate2D.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.MAX_FLIGHT_RADIUS, SET, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.CONNECTION_FAIL_SAFE_BEHAVIOR, SET, ConnectionFailSafeBehavior.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.INTELLIGENT_FLIGHT_ASSISTANT_GHAVOID_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ACTIVE_OBSTACLE_AVOIDANCE_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.MAX_FLIGHT_RADIUS_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.CONTROL_MODE, SET, ControlMode.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ENABLE_1860, SET, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.GO_HOME_HEIGHT_IN_METERS, SET, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.TERRAIN_FOLLOW_MODE_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.UPWARDS_AVOIDANCE_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.SERIOUS_LOW_BATTERY_WARNING_THRESHOLD, SET, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.LEDS_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ACTIVE_TRACK_GESTURE_MODE_ENABLED, SET, Boolean.class),

                new DJIKeyInfo("FlightController", FlightControllerKey.RESET_MOTOR, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.START_IMU_CALIBRATION, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.START_LANDING, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.START_SIMULATOR, ACTION, InitializationData.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.DEPLOY_LANDING_GEAR, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.STOP_SIMULATOR, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.HOME_LOCATION_USING_CURRENT_AIRCRAFT_LOCATION, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.TAKE_OFF, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.CANCEL_TAKE_OFF, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.TURN_OFF_MOTORS, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.START_IMU_CALIBRATION_WITH_ID, ACTION, Integer.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.TURN_ON_MOTORS, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.ENTER_TRANSPORT_MODE, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.CANCEL_LANDING, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.EXIT_TRANSPORT_MODE, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.LOCK_COURSE_USING_CURRENT_HEADING, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.CANCEL_GO_HOME, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.SEND_VIRTUAL_STICK_FLIGHT_CONTROL_DATA, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.START_GO_HOME, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.CONFIRM_LANDING, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.CONFIRM_SMART_RETURN_TO_HOME_REQUEST, ACTION, Boolean.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.COMPASS_START_CALIBRATION, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.RETRACT_LANDING_GEAR, ACTION, void.class),
                new DJIKeyInfo("FlightController", FlightControllerKey.SEND_DATA_TO_ON_BOARD_SDK_DEVICE, ACTION, byte[].class),
                new DJIKeyInfo("FlightController", FlightControllerKey.COMPASS_STOP_CALIBRATION, ACTION, void.class),

                new DJIKeyInfo("RemoteController", RemoteControllerKey.AVAILABLE_MASTERS, GET, Information[].class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.NAME, GET, String.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.AIRCRAFT_CUSTOM_MAPPING, GET, AircraftMapping.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.CHARGE_MOBILE_MODE, GET, ChargeMobileMode.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.PAIRING_STATE, GET, PairingState.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.LEFT_WHEEL_GIMBAL_CONTROL_AXIS, GET, GimbalAxis.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.SERIAL_NUMBER, GET, String.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.CUSTOM_GIMBAL_MAPPING, GET, GimbalMapping.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.GIMBAL_MAPPING_STYLE, GET, GimbalMappingStyle.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.SELECT_BUTTON_PROFILE_GROUP, GET, String.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.BUTTON_CONFIG, GET, ProfessionalRC.ButtonConfiguration.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.BUTTON_PROFILE_GROUPS, GET, String[].class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.MODE, GET, RCMode.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.AIRCRAFT_MAPPING_STYLE, GET, AircraftMappingStyle.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.CONNECTED_MASTER_CREDENTIALS, GET, Credentials.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.CUSTOM_BUTTON_TAGS, GET, CustomButtonTags.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.PASSWORD, GET, String.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.SELECT_BUTTON_PROFILE, GET, Integer.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.MASTER_SEARCHING_STATE, GET, Boolean.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.REQUEST_GIMBAL_CONTROL, GET, RequestGimbalControlResult.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.MASTER_LIST, GET, String[].class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.SLAVE_LIST, GET, Information[].class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.GIMBAL_CONTROL_SPEED_COEFFICIENT, GET, GimbalControlSpeedCoefficient.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.IS_MASTER_SLAVE_MODE_V2_SUPPORTED, GET, Boolean.class),

                new DJIKeyInfo("RemoteController", RemoteControllerKey.GO_HOME_BUTTON, PUSH, HardwareState.Button.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.CUSTOM_BUTTON_2, PUSH, HardwareState.Button.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.CUSTOM_BUTTON_1, PUSH, HardwareState.Button.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.FOCUS_CONTROLLER_IS_WORKING, PUSH, Boolean.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.SHUTTER_BUTTON, PUSH, HardwareState.Button.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.LEFT_WHEEL, PUSH, Integer.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.RECORD_BUTTON, PUSH, HardwareState.Button.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.LEFT_STICK_VALUE, PUSH, Stick.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.PLAYBACK_BUTTON, PUSH, HardwareState.Button.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.IS_FOCUS_CONTROLLER_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.RIGHT_WHEEL, PUSH, HardwareState.RightWheel.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.MODE, PUSH, RCMode.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.GPS_DATA, PUSH, GPSData.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.FIVE_D_BUTTON, PUSH, HardwareState.FiveDButton.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.BUTTON_EVENT_OF_PROFESSIONAL_RC, PUSH, ProfessionalRC.Event.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.FLIGHT_MODE_SWITCH_POSITION, PUSH, HardwareState.FlightModeSwitch.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.FOCUS_CONTROLLER_DIRECTION, PUSH, FocusControllerState.Direction.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.CHARGE_REMAINING, PUSH, ChargeRemaining.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.TRANSFORMATION_SWITCH, PUSH, HardwareState.TransformationSwitch.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.FOCUS_CONTROLLER_CONTROL_TYPE, PUSH, FocusControllerState.ControlType.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.RIGHT_STICK_VALUE, PUSH, Stick.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.IS_MASTER_SLAVE_MODE_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.MASTER_SLAVE_STATE, PUSH, MasterSlaveState.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.DISPLAY_NAME, PUSH, String.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.PAUSE_BUTTON, PUSH, HardwareState.Button.class),

                new DJIKeyInfo("RemoteController", RemoteControllerKey.NAME, SET, String.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.AIRCRAFT_CUSTOM_MAPPING, SET, AircraftMapping.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.CHARGE_MOBILE_MODE, SET, ChargeMobileMode.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.LEFT_WHEEL_GIMBAL_CONTROL_AXIS, SET, GimbalAxis.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.CUSTOM_GIMBAL_MAPPING, SET, GimbalMapping.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.GIMBAL_MAPPING_STYLE, SET, GimbalMappingStyle.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.SELECT_BUTTON_PROFILE_GROUP, SET, String.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.BUTTON_CONFIG, SET, ProfessionalRC.ButtonConfiguration.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.SET_MASTER_AUTH_CODE, SET, String.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.MODE, SET, RCMode.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.AIRCRAFT_MAPPING_STYLE, SET, AircraftMappingStyle.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.CUSTOM_BUTTON_TAGS, SET, CustomButtonTags.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.PASSWORD, SET, String.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.SELECT_BUTTON_PROFILE, SET, Integer.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.GIMBAL_CONTROL_SPEED_COEFFICIENT, SET, GimbalControlSpeedCoefficient.class),

                new DJIKeyInfo("RemoteController", RemoteControllerKey.CUSTOMIZE_BUTTON, ACTION, void.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.FETCH_CUSTOMIZED_ACTION_OF_BUTTON, ACTION, ProfessionalRC.CustomizableButton.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.RENAME_BUTTON_PROFILE_GROUP, ACTION, void.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.CONNECT_TO_MASTER, ACTION, Credentials.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.ADD_BUTTON_PROFILE_GROUP, ACTION, String.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.RESET_BUTTON_CONFIG, ACTION, void.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.RESPONSE_TO_REQUEST_FOR_GIMBAL_CONTROL, ACTION, ResponseForGimbalControl.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.STOP_MASTER_SEARCHING, ACTION, void.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.STOP_PAIRING, ACTION, void.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.CONNECT_TO_MASTER_WITH_ID, ACTION, AuthorizationInfo.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.GET_MASTER_AUTH_CODE, ACTION, String.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.REMOVE_BUTTON_PROFILE_GROUP, ACTION, String.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.START_PAIRING, ACTION, void.class),
                new DJIKeyInfo("RemoteController", RemoteControllerKey.START_SEARCH_MASTER, ACTION, void.class),

                new DJIKeyInfo("Gimbal", GimbalKey.YAW_SMOOTH_TRACK_DEADBAND, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_INVERTED_CONTROL_ENABLED, GET, void.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_MOTOR_CONTROL_PRE_CONTROL, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_DOWN_ENDPOINT, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_MOTOR_CONTROL_STRENGTH, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_RIGHT_ENDPOINT, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_CONTROLLER_SMOOTHING_FACTOR, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_CONTROLLER_DEADBAND, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_MOTOR_CONTROL_GYRO_FILTERING_FACTOR, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_SMOOTH_TRACK_ACCELERATION, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.ROLL_MOTOR_CONTROL_GYRO_FILTERING_FACTOR, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_MOTOR_CONTROL_STIFFNESS, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_MOTOR_CONTROL_PRE_CONTROL, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.CAPABILITIES, GET, Map.class),
                new DJIKeyInfo("Gimbal", GimbalKey.MOTOR_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_CONTROLLER_DEADBAND, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_CONTROLLER_SPEED_COEFFICIENT, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_RANGE_EXTENSION_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_SMOOTH_TRACK_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_LEFT_ENDPOINT, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.CONTROLLER_MODE, GET, ControllerMode.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_SMOOTH_TRACK_DEADBAND, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_SMOOTH_TRACK_ACCELERATION, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_SMOOTH_TRACK_SPEED, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_SMOOTH_TRACK_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_CONTROLLER_SMOOTHING_FACTOR, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.ROLL_MOTOR_CONTROL_STRENGTH, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.ROLL_MOTOR_CONTROL_PRE_CONTROL, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.ROLL_MOTOR_CONTROL_STIFFNESS, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.MOVEMENT_SETTINGS_PROFILE, GET, MovementSettingsProfile.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_INVERTED_CONTROL_ENABLED, GET, void.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_MOTOR_CONTROL_STRENGTH, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_UP_ENDPOINT, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_MOTOR_CONTROL_GYRO_FILTERING_FACTOR, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_CONTROLLER_SPEED_COEFFICIENT, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_MOTOR_CONTROL_STIFFNESS, GET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_SMOOTH_TRACK_SPEED, GET, Integer.class),

                new DJIKeyInfo("Gimbal", GimbalKey.ROLL_TEST_RESULT, PUSH, BalanceTestResult.class),
                new DJIKeyInfo("Gimbal", GimbalKey.IS_MOBILE_DEVICE_MOUNTED, PUSH, void.class),
                new DJIKeyInfo("Gimbal", GimbalKey.MODE, PUSH, GimbalMode.class),
                new DJIKeyInfo("Gimbal", GimbalKey.BALANCE_STATE, PUSH, void.class),
                new DJIKeyInfo("Gimbal", GimbalKey.IS_TESTING_BALANCE, PUSH, Boolean.class),
                new DJIKeyInfo("Gimbal", GimbalKey.IS_CALIBRATING, PUSH, Boolean.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_ANGLE_WITH_AIRCRAFT_IN_DEGREE, PUSH, Float.class),
                new DJIKeyInfo("Gimbal", GimbalKey.IS_CALIBRATION_SUCCESSFUL, PUSH, Boolean.class),
                new DJIKeyInfo("Gimbal", GimbalKey.CHARGE_REMAINING_IN_PERCENT, PUSH, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.IS_PITCH_AT_STOP, PUSH, Boolean.class),
                new DJIKeyInfo("Gimbal", GimbalKey.IS_MOTOR_OVER_LOADED, PUSH, void.class),
                new DJIKeyInfo("Gimbal", GimbalKey.ATTITUDE_IN_DEGREES, PUSH, Attitude.class),
                new DJIKeyInfo("Gimbal", GimbalKey.CALIBRATION_PROGRESS, PUSH, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.IS_ROLL_AT_STOP, PUSH, Boolean.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_TEST_RESULT, PUSH, BalanceTestResult.class),
                new DJIKeyInfo("Gimbal", GimbalKey.IS_YAW_AT_STOP, PUSH, Boolean.class),
                new DJIKeyInfo("Gimbal", GimbalKey.ROLL_FINE_TUNE_IN_DEGREES, PUSH, Float.class),
                new DJIKeyInfo("Gimbal", GimbalKey.IS_ATTITUDE_RESET, PUSH, Boolean.class),

                new DJIKeyInfo("Gimbal", GimbalKey.YAW_SMOOTH_TRACK_DEADBAND, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_INVERTED_CONTROL_ENABLED, SET, void.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_MOTOR_CONTROL_PRE_CONTROL, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_DOWN_ENDPOINT, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_MOTOR_CONTROL_STRENGTH, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_RIGHT_ENDPOINT, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_CONTROLLER_SMOOTHING_FACTOR, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_CONTROLLER_DEADBAND, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_MOTOR_CONTROL_GYRO_FILTERING_FACTOR, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_SMOOTH_TRACK_ACCELERATION, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.ROLL_MOTOR_CONTROL_GYRO_FILTERING_FACTOR, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_MOTOR_CONTROL_STIFFNESS, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.MODE, SET, GimbalMode.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_MOTOR_CONTROL_PRE_CONTROL, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.MOTOR_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_CONTROLLER_DEADBAND, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_CONTROLLER_SPEED_COEFFICIENT, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_RANGE_EXTENSION_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_SMOOTH_TRACK_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_LEFT_ENDPOINT, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.CONTROLLER_MODE, SET, ControllerMode.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_SMOOTH_TRACK_DEADBAND, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_SMOOTH_TRACK_ACCELERATION, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_SMOOTH_TRACK_SPEED, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_SMOOTH_TRACK_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_CONTROLLER_SMOOTHING_FACTOR, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.ROLL_MOTOR_CONTROL_STRENGTH, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.ROLL_MOTOR_CONTROL_PRE_CONTROL, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.ROLL_MOTOR_CONTROL_STIFFNESS, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.MOVEMENT_SETTINGS_PROFILE, SET, MovementSettingsProfile.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_INVERTED_CONTROL_ENABLED, SET, void.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_MOTOR_CONTROL_STRENGTH, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_UP_ENDPOINT, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_MOTOR_CONTROL_GYRO_FILTERING_FACTOR, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_CONTROLLER_SPEED_COEFFICIENT, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.PITCH_MOTOR_CONTROL_STIFFNESS, SET, Integer.class),
                new DJIKeyInfo("Gimbal", GimbalKey.YAW_SMOOTH_TRACK_SPEED, SET, Integer.class),

                new DJIKeyInfo("Gimbal", GimbalKey.RESET_GIMBAL, ACTION, void.class),
                new DJIKeyInfo("Gimbal", GimbalKey.ROTATE, ACTION, void.class),
                new DJIKeyInfo("Gimbal", GimbalKey.APPLY_MOTOR_CONTROL_PRESET, ACTION, MotorControlPreset.class),
                new DJIKeyInfo("Gimbal", GimbalKey.START_BALANCE_TEST, ACTION, void.class),
                new DJIKeyInfo("Gimbal", GimbalKey.RESTORE_FACTORY_SETTINGS, ACTION, void.class),
                new DJIKeyInfo("Gimbal", GimbalKey.FINE_TUNE_ROLL_IN_DEGREES, ACTION, Float.class),
                new DJIKeyInfo("Gimbal", GimbalKey.START_CALIBRATION, ACTION, void.class),
                new DJIKeyInfo("Gimbal", GimbalKey.TOGGLE_SELFIE, ACTION, void.class),

                new DJIKeyInfo("Camera", CameraKey.THERMAL_MEASUREMENT_MODE, GET, ThermalMeasurementMode.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_QUICK_VIEW_DURATION, GET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.TAP_ZOOM_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.HISTOGRAM_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_BRIGHTNESS, GET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_CONTRAST, GET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.SERIAL_NUMBER, GET, String.class),
                new DJIKeyInfo("Camera", CameraKey.VIDEO_CAPTION_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.CUSTOM_INFORMATION, GET, String.class),
                new DJIKeyInfo("Camera", CameraKey.OPTICAL_ZOOM_SPEC, GET, SettingsDefinitions.OpticalZoomSpec.class),
                new DJIKeyInfo("Camera", CameraKey.TURN_OFF_FAN_WHEN_POSSIBLE, GET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.AUDIO_RECORDING_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.IRC_ENABLE, GET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_TIME_LAPSE_SETTINGS, GET, PhotoTimeLapseSettings.class),
                new DJIKeyInfo("Camera", CameraKey.LENS_INFORMATION, GET, String.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ACE, GET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.SPOT_METERING_TARGET, GET, Point.class),
                new DJIKeyInfo("Camera", CameraKey.FIRMWARE_VERSION, GET, String.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_DDE, GET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.FOCUS_RING_VALUE_UPPER_BOUND, GET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_TEMPERATURE_DATA, GET, Float.class),
                new DJIKeyInfo("Camera", CameraKey.EI_VALUE_RANGE, GET, int[].class),
                new DJIKeyInfo("Camera", CameraKey.SATURATION, GET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.ORIENTATION, GET, SettingsDefinitions.Orientation.class),
                new DJIKeyInfo("Camera", CameraKey.AUDIO_GAIN, GET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.HD_LIVE_VIEW_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.RECOMMENDED_EI_VALUE, GET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.DEFOG_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.VIDEO_DEWARPING_ENABLED, GET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.FILE_INDEX_MODE, GET, SettingsDefinitions.FileIndexMode.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_SSO, GET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.HARDWARE_VERSION, GET, String.class),


                new DJIKeyInfo("Camera", CameraKey.SDCARD_IS_READ_ONLY, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SSD_CLIP_FILE_NAME, PUSH, SettingsDefinitions.SSDClipFileName.class),
                new DJIKeyInfo("Camera", CameraKey.RAW_PHOTO_BURST_COUNT, PUSH, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.IS_ADJUSTABLE_APERTURE_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.VIDEO_FILE_COMPRESSION_STANDARD, PUSH, SettingsDefinitions.VideoFileCompressionStandard.class),
                new DJIKeyInfo("Camera", CameraKey.IS_ADJUSTABLE_FOCAL_POINT_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SHUTTER_SPEED, PUSH, SettingsDefinitions.ShutterSpeed.class),
                new DJIKeyInfo("Camera", CameraKey.HISTOGRAM_LIGHT_VALUES, PUSH, Short[].class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_METERING_AREA, PUSH, RectF.class),
                new DJIKeyInfo("Camera", CameraKey.METERING_MODE, PUSH, SettingsDefinitions.MeteringMode.class),
                new DJIKeyInfo("Camera", CameraKey.EXPOSURE_COMPENSATION, PUSH, SettingsDefinitions.ExposureCompensation.class),
                new DJIKeyInfo("Camera", CameraKey.LED_AUTO_TURN_OFF_ENABLED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SSD_COLOR_RANGE, PUSH, SettingsDefinitions.SSDColor[].class),
                new DJIKeyInfo("Camera", CameraKey.HISTOGRAM_ENABLED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ISOTHERM_MIDDLE_VALUE, PUSH, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.SSD_OPERATION_STATE, PUSH, SSDOperationState.class),
                new DJIKeyInfo("Camera", CameraKey.ISO_RANGE, PUSH, SettingsDefinitions.ISO[].class),
                new DJIKeyInfo("Camera", CameraKey.IS_SHOOTING_SHALLOW_FOCUS_PHOTO, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_AREA_TEMPERATURE_AGGREGATIONS, PUSH, ThermalAreaTemperatureAggregations.class),
                new DJIKeyInfo("Camera", CameraKey.IS_PHOTO_QUICK_VIEW_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.CAMERA_TYPE, PUSH, SettingsDefinitions.CameraType.class),
                new DJIKeyInfo("Camera", CameraKey.MODE, PUSH, SettingsDefinitions.CameraMode.class),
                new DJIKeyInfo("Camera", CameraKey.IS_OVERHEATING, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SDCARD_IS_INVALID_FORMAT, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_AEB_COUNT, PUSH, SettingsDefinitions.PhotoAEBCount.class),
                new DJIKeyInfo("Camera", CameraKey.MECHANICAL_SHUTTER_ENABLED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_WINDOW_TEMPERATURE, PUSH, Float.class),
                new DJIKeyInfo("Camera", CameraKey.SDCARD_TOTAL_SPACE_IN_MB, PUSH, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ATMOSPHERIC_TEMPERATURE, PUSH, Float.class),
                new DJIKeyInfo("Camera", CameraKey.EXPOSURE_SETTINGS, PUSH, ExposureSettings.class),
                new DJIKeyInfo("Camera", CameraKey.VISION_STABILIZATION_STATE, PUSH, StabilizationState.class),
                new DJIKeyInfo("Camera", CameraKey.CURRENT_VIDEO_RECORDING_TIME_IN_SECONDS, PUSH, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ROI, PUSH, SettingsDefinitions.ThermalROI.class),
                new DJIKeyInfo("Camera", CameraKey.EXPOSURE_COMPENSATION_RANGE, PUSH, SettingsDefinitions.ExposureCompensation[].class),
                new DJIKeyInfo("Camera", CameraKey.RESOLUTION_FRAME_RATE, PUSH, ResolutionAndFrameRate.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_WINDOW_TRANSMISSION_COEFFICIENT, PUSH, Float.class),
                new DJIKeyInfo("Camera", CameraKey.ANTI_FLICKER_RANGE, PUSH, SettingsDefinitions.AntiFlickerFrequency[].class),
                new DJIKeyInfo("Camera", CameraKey.HAS_ERROR, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.WHITE_BALANCE_CUSTOM_COLOR_TEMPERATURE_RANGE, PUSH, int[].class),
                new DJIKeyInfo("Camera", CameraKey.SSD_VIDEO_RECORDING_ENABLED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.VIDEO_FILE_FORMAT_RANGE, PUSH, SettingsDefinitions.VideoFileFormat[].class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_SCENE, PUSH, SettingsDefinitions.ThermalScene.class),
                new DJIKeyInfo("Camera", CameraKey.PICTURE_STYLE_PRESET, PUSH, SettingsDefinitions.PictureStylePreset.class),
                new DJIKeyInfo("Camera", CameraKey.WHITE_BALANCE_PRESENT_RANGE, PUSH, SettingsDefinitions.WhiteBalancePreset[].class),
                new DJIKeyInfo("Camera", CameraKey.IS_SHOOTING_RAW_BURST_PHOTO, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SDCARD_IS_INITIALIZING, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.IS_SHOOTING_BURST_PHOTO, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ATMOSPHERIC_TRANSMISSION_COEFFICIENT, PUSH, Float.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_PALETTE, PUSH, SettingsDefinitions.ThermalPalette.class),
                new DJIKeyInfo("Camera", CameraKey.IS_INTERCHANGEABLE_LENS_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.IS_MEDIA_DOWNLOAD_MODE_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SSD_VIDEO_LICENSES, PUSH, CameraSSDVideoLicense[].class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_PANORAMA_MODE, PUSH, SettingsDefinitions.PhotoPanoramaMode.class),
                new DJIKeyInfo("Camera", CameraKey.MODE_RANGE, PUSH, SettingsDefinitions.CameraMode[].class),
                new DJIKeyInfo("Camera", CameraKey.IS_RECORDING, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.IS_VIDEO_PLAYBACK_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SDCARD_HAS_ERROR, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_ASPECT_RATIO_RANGE, PUSH, SettingsDefinitions.PhotoAspectRatio[].class),
                new DJIKeyInfo("Camera", CameraKey.DIGITAL_ZOOM_FACTOR, PUSH, Float.class),
                new DJIKeyInfo("Camera", CameraKey.ORIENTATION_RANGE, PUSH, SettingsDefinitions.Orientation[].class),
                new DJIKeyInfo("Camera", CameraKey.AUTO_AE_UNLOCK_ENABLED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.IS_SSD_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_RAW_BURST_COUNT, PUSH, SettingsDefinitions.PhotoBurstCount.class),
                new DJIKeyInfo("Camera", CameraKey.SDCARD_AVAILABLE_RECORDING_TIME_IN_SECONDS, PUSH, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.IS_SHOOTING_SINGLE_PHOTO, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.FOCUS_TARGET, PUSH, PointF.class),
                new DJIKeyInfo("Camera", CameraKey.IS_PLAYBACK_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.IS_DIGITAL_ZOOM_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.FOCUS_MODE, PUSH, SettingsDefinitions.FocusMode.class),
                new DJIKeyInfo("Camera", CameraKey.SSD_REMAINING_SPACE_IN_MB, PUSH, Long.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_FILE_FORMAT_RANGE, PUSH, SettingsDefinitions.PhotoFileFormat[].class),
                new DJIKeyInfo("Camera", CameraKey.PSEUDO_CAMERA_CAPTURE_PROGRESS, PUSH, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.SHUTTER_SPEED_RANGE, PUSH, SettingsDefinitions.ShutterSpeed[].class),
                new DJIKeyInfo("Camera", CameraKey.SDCARD_IS_FULL, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.CAMERA_COLOR_RANGE, PUSH, SettingsDefinitions.CameraColor[].class),
                new DJIKeyInfo("Camera", CameraKey.SHOOT_PHOTO_MODE, PUSH, SettingsDefinitions.ShootPhotoMode.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_FILE_FORMAT, PUSH, SettingsDefinitions.PhotoFileFormat.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_DIGITAL_ZOOM_FACTOR, PUSH, SettingsDefinitions.ThermalDigitalZoomFactor.class),
                new DJIKeyInfo("Camera", CameraKey.IS_TAP_ZOOM_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SDCARD_IS_INSERTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ISOTHERM_UPPER_VALUE, PUSH, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_SPOT_METERING_TARGET_POINT, PUSH, PointF.class),
                new DJIKeyInfo("Camera", CameraKey.APERTURE_RANGE, PUSH, SettingsDefinitions.Aperture[].class),
                new DJIKeyInfo("Camera", CameraKey.SSD_VIDEO_RESOLUTION_AND_FRAME_RATE, PUSH, ResolutionAndFrameRate.class),
                new DJIKeyInfo("Camera", CameraKey.SHARPNESS, PUSH, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_WINDOW_REFLECTION, PUSH, Float.class),
                new DJIKeyInfo("Camera", CameraKey.SDCARD_AVAILABLE_CAPTURE_COUNT, PUSH, Long.class),
                new DJIKeyInfo("Camera", CameraKey.SDCARD_IS_FORMATTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.IS_SHOOTING_PANORAMA_PHOTO, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.EI_COLOR, PUSH, SettingsDefinitions.EIColor.class),
                new DJIKeyInfo("Camera", CameraKey.IS_SHOOTING_PHOTO_ENABLED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.ISO, PUSH, SettingsDefinitions.ISO.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_SCENE_EMISSIVITY, PUSH, Float.class),
                new DJIKeyInfo("Camera", CameraKey.IS_STORING_PHOTO, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SSD_COLOR, PUSH, SettingsDefinitions.SSDColor.class),
                new DJIKeyInfo("Camera", CameraKey.IS_AUDIO_RECORDING_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.FOCUS_STATUS, PUSH, SettingsDefinitions.FocusStatus.class),
                new DJIKeyInfo("Camera", CameraKey.NDFILTER_MODE, PUSH, SettingsDefinitions.NDFilterMode.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_TEMPERATURE_DATA, PUSH, Float.class),
                new DJIKeyInfo("Camera", CameraKey.FOCUS_ASSISTANT_SETTINGS, PUSH, FocusAssistantSettings.class),
                new DJIKeyInfo("Camera", CameraKey.VIDEO_STANDARD_RANGE, PUSH, SettingsDefinitions.VideoStandard[].class),
                new DJIKeyInfo("Camera", CameraKey.DISPLAY_NAME, PUSH, String.class),
                new DJIKeyInfo("Camera", CameraKey.LENS_IS_FOCUS_ASSISTANT_WORKING, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.VIDEO_RESOLUTION_FRAME_RATE_RANGE, PUSH, ResolutionAndFrameRate[].class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_CUSTOM_EXTERNAL_SCENE_SETTINGS_PROFILE, PUSH, SettingsDefinitions.ThermalCustomExternalSceneSettingsProfile.class),
                new DJIKeyInfo("Camera", CameraKey.IS_THERMAL_CAMERA, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SSD_VIDEO_RESOLUTION_FRAME_RATE_RANGE, PUSH, ResolutionAndFrameRate[].class),
                new DJIKeyInfo("Camera", CameraKey.CONTRAST, PUSH, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_IS_FFC_MODE_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.OPTICAL_ZOOM_FOCAL_LENGTH, PUSH, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ISOTHERM_ENABLED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SDCARD_IS_FORMATTING, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SDCARD_REMAINING_SPACE_IN_MB, PUSH, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.IS_TIME_LAPSE_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SSD_TOTAL_SPACE, PUSH, SSDCapacity.class),
                new DJIKeyInfo("Camera", CameraKey.FOCUS_RING_VALUE, PUSH, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_PROFILE, PUSH, SettingsDefinitions.ThermalProfile.class),
                new DJIKeyInfo("Camera", CameraKey.ACTIVATE_SSD_VIDEO_LICENSE, PUSH, CameraSSDVideoLicense.class),
                new DJIKeyInfo("Camera", CameraKey.ANTI_FLICKER_FREQUENCY, PUSH, SettingsDefinitions.AntiFlickerFrequency.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_BURST_COUNT, PUSH, SettingsDefinitions.PhotoBurstCount.class),
                new DJIKeyInfo("Camera", CameraKey.EI_VALUE, PUSH, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_FFC_MODE, PUSH, SettingsDefinitions.ThermalFFCMode.class),
                new DJIKeyInfo("Camera", CameraKey.CAMERA_COLOR, PUSH, SettingsDefinitions.CameraColor.class),
                new DJIKeyInfo("Camera", CameraKey.IS_ND_FILTER_MODE_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.OPTICAL_ZOOM_SCALE, PUSH, Float.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_GAIN_MODE, PUSH, SettingsDefinitions.ThermalGainMode.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ISOTHERM_LOWER_VALUE, PUSH, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.VIDEO_FILE_FORMAT, PUSH, SettingsDefinitions.VideoFileFormat.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_TIME_INTERVAL_SETTINGS, PUSH, SettingsDefinitions.PhotoTimeIntervalSettings.class),
                new DJIKeyInfo("Camera", CameraKey.AE_LOCK, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SSD_IS_CONNECTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.EXPOSURE_SENSITIVITY_MODE, PUSH, SettingsDefinitions.ExposureSensitivityMode.class),
                new DJIKeyInfo("Camera", CameraKey.VISION_STABILIZATION_ENABLED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.IS_MECHANICAL_SHUTTER_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SSD_VIDEO_RESOLUTION_RANGE, PUSH, SettingsDefinitions.VideoResolution[].class),
                new DJIKeyInfo("Camera", CameraKey.APERTURE, PUSH, SettingsDefinitions.Aperture.class),
                new DJIKeyInfo("Camera", CameraKey.SSD_LEGACY_COLOR, PUSH, SettingsDefinitions.SSDLegacyColor.class),
                new DJIKeyInfo("Camera", CameraKey.WHITE_BALANCE, PUSH, WhiteBalance.class),
                new DJIKeyInfo("Camera", CameraKey.IS_EI_MODE_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.IS_SHOOTING_SINGLE_PHOTO_IN_RAW_FORMAT, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.LENS_IS_AF_SWITCH_ON, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SENSOR_CLEANING_STATE, PUSH, SettingsDefinitions.SensorCleaningState.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ISOTHERM_UNIT, PUSH, SettingsDefinitions.ThermalIsothermUnit.class),
                new DJIKeyInfo("Camera", CameraKey.TAP_ZOOM_MULTIPLIER, PUSH, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.VIDEO_STANDARD, PUSH, SettingsDefinitions.VideoStandard.class),
                new DJIKeyInfo("Camera", CameraKey.SHOOT_PHOTO_MODE_CHILD_RANGE, PUSH, int[][].class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_ASPECT_RATIO, PUSH, SettingsDefinitions.PhotoAspectRatio.class),
                new DJIKeyInfo("Camera", CameraKey.RECORDING_STATE, PUSH, CameraRecordingState.class),
                new DJIKeyInfo("Camera", CameraKey.SHOOT_PHOTO_MODE_RANGE, PUSH, SettingsDefinitions.ShootPhotoMode[].class),
                new DJIKeyInfo("Camera", CameraKey.IS_SHOOTING_PHOTO, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_BACKGROUND_TEMPERATURE, PUSH, Float.class),
                new DJIKeyInfo("Camera", CameraKey.LENS_IS_INSTALLED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SDCARD_IS_VERIFIED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.EXPOSURE_MODE, PUSH, SettingsDefinitions.ExposureMode.class),
                new DJIKeyInfo("Camera", CameraKey.IS_SHOOTING_INTERVAL_PHOTO, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.IS_OPTICAL_ZOOM_SUPPORTED, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_WINDOW_REFLECTED_TEMPERATURE, PUSH, Float.class),
                new DJIKeyInfo("Camera", CameraKey.SDCARD_IS_CONNECTED_TO_PC, PUSH, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.EXPOSURE_MODE_RANGE, PUSH, SettingsDefinitions.ExposureMode[].class),
                new DJIKeyInfo("Camera", CameraKey.SSD_AVAILABLE_RECORDING_TIME_IN_SECONDS, PUSH, Integer.class),


                new DJIKeyInfo("Camera", CameraKey.THERMAL_MEASUREMENT_MODE, SET, ThermalMeasurementMode.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_QUICK_VIEW_DURATION, SET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.SSD_CLIP_FILE_NAME, SET, SettingsDefinitions.SSDClipFileName.class),
                new DJIKeyInfo("Camera", CameraKey.VIDEO_FILE_COMPRESSION_STANDARD, SET, SettingsDefinitions.VideoFileCompressionStandard.class),
                new DJIKeyInfo("Camera", CameraKey.TAP_ZOOM_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SHUTTER_SPEED, SET, SettingsDefinitions.ShutterSpeed.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_METERING_AREA, SET, RectF.class),
                new DJIKeyInfo("Camera", CameraKey.METERING_MODE, SET, SettingsDefinitions.MeteringMode.class),
                new DJIKeyInfo("Camera", CameraKey.EXPOSURE_COMPENSATION, SET, SettingsDefinitions.ExposureCompensation.class),
                new DJIKeyInfo("Camera", CameraKey.LED_AUTO_TURN_OFF_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.HISTOGRAM_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ISOTHERM_MIDDLE_VALUE, SET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_BRIGHTNESS, SET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_CONTRAST, SET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.MODE, SET, SettingsDefinitions.CameraMode.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_AEB_COUNT, SET, SettingsDefinitions.PhotoAEBCount.class),
                new DJIKeyInfo("Camera", CameraKey.MECHANICAL_SHUTTER_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_WINDOW_TEMPERATURE, SET, Float.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ATMOSPHERIC_TEMPERATURE, SET, Float.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ROI, SET, SettingsDefinitions.ThermalROI.class),
                new DJIKeyInfo("Camera", CameraKey.VIDEO_CAPTION_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.RESOLUTION_FRAME_RATE, SET, ResolutionAndFrameRate.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_WINDOW_TRANSMISSION_COEFFICIENT, SET, Float.class),
                new DJIKeyInfo("Camera", CameraKey.CUSTOM_INFORMATION, SET, String.class),
                new DJIKeyInfo("Camera", CameraKey.TURN_OFF_FAN_WHEN_POSSIBLE, SET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.SSD_VIDEO_RECORDING_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_SCENE, SET, SettingsDefinitions.ThermalScene.class),
                new DJIKeyInfo("Camera", CameraKey.PICTURE_STYLE_PRESET, SET, SettingsDefinitions.PictureStylePreset.class),
                new DJIKeyInfo("Camera", CameraKey.AUDIO_RECORDING_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ATMOSPHERIC_TRANSMISSION_COEFFICIENT, SET, Float.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_PALETTE, SET, SettingsDefinitions.ThermalPalette.class),
                new DJIKeyInfo("Camera", CameraKey.IRC_ENABLE, SET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_PANORAMA_MODE, SET, SettingsDefinitions.PhotoPanoramaMode.class),
                new DJIKeyInfo("Camera", CameraKey.DIGITAL_ZOOM_FACTOR, SET, Float.class),
                new DJIKeyInfo("Camera", CameraKey.AUTO_AE_UNLOCK_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_RAW_BURST_COUNT, SET, SettingsDefinitions.PhotoBurstCount.class),
                new DJIKeyInfo("Camera", CameraKey.FOCUS_TARGET, SET, PointF.class),
                new DJIKeyInfo("Camera", CameraKey.FOCUS_MODE, SET, SettingsDefinitions.FocusMode.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_TIME_LAPSE_SETTINGS, SET, PhotoTimeLapseSettings.class),
                new DJIKeyInfo("Camera", CameraKey.SHOOT_PHOTO_MODE, SET, SettingsDefinitions.ShootPhotoMode.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_FILE_FORMAT, SET, SettingsDefinitions.PhotoFileFormat.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_DIGITAL_ZOOM_FACTOR, SET, SettingsDefinitions.ThermalDigitalZoomFactor.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ISOTHERM_UPPER_VALUE, SET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ACE, SET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.SPOT_METERING_TARGET, SET, Point.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_SPOT_METERING_TARGET_POINT, SET, PointF.class),
                new DJIKeyInfo("Camera", CameraKey.SSD_VIDEO_RESOLUTION_AND_FRAME_RATE, SET, ResolutionAndFrameRate.class),
                new DJIKeyInfo("Camera", CameraKey.SHARPNESS, SET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_WINDOW_REFLECTION, SET, Float.class),
                new DJIKeyInfo("Camera", CameraKey.EI_COLOR, SET, SettingsDefinitions.EIColor.class),
                new DJIKeyInfo("Camera", CameraKey.ISO, SET, SettingsDefinitions.ISO.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_DDE, SET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_SCENE_EMISSIVITY, SET, Float.class),
                new DJIKeyInfo("Camera", CameraKey.SSD_COLOR, SET, SettingsDefinitions.SSDColor.class),
                new DJIKeyInfo("Camera", CameraKey.NDFILTER_MODE, SET, SettingsDefinitions.NDFilterMode.class),
                new DJIKeyInfo("Camera", CameraKey.FOCUS_ASSISTANT_SETTINGS, SET, FocusAssistantSettings.class),
                new DJIKeyInfo("Camera", CameraKey.SATURATION, SET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_CUSTOM_EXTERNAL_SCENE_SETTINGS_PROFILE, SET, SettingsDefinitions.ThermalCustomExternalSceneSettingsProfile.class),
                new DJIKeyInfo("Camera", CameraKey.ORIENTATION, SET, SettingsDefinitions.Orientation.class),
                new DJIKeyInfo("Camera", CameraKey.AUDIO_GAIN, SET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.CONTRAST, SET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.HD_LIVE_VIEW_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.OPTICAL_ZOOM_FOCAL_LENGTH, SET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ISOTHERM_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.DEFOG_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.VIDEO_DEWARPING_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.FOCUS_RING_VALUE, SET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.ACTIVATE_SSD_VIDEO_LICENSE, SET, CameraSSDVideoLicense.class),
                new DJIKeyInfo("Camera", CameraKey.ANTI_FLICKER_FREQUENCY, SET, SettingsDefinitions.AntiFlickerFrequency.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_BURST_COUNT, SET, SettingsDefinitions.PhotoBurstCount.class),
                new DJIKeyInfo("Camera", CameraKey.EI_VALUE, SET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_FFC_MODE, SET, SettingsDefinitions.ThermalFFCMode.class),
                new DJIKeyInfo("Camera", CameraKey.CAMERA_COLOR, SET, SettingsDefinitions.CameraColor.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_GAIN_MODE, SET, SettingsDefinitions.ThermalGainMode.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ISOTHERM_LOWER_VALUE, SET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.VIDEO_FILE_FORMAT, SET, SettingsDefinitions.VideoFileFormat.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_TIME_INTERVAL_SETTINGS, SET, SettingsDefinitions.PhotoTimeIntervalSettings.class),
                new DJIKeyInfo("Camera", CameraKey.AE_LOCK, SET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.EXPOSURE_SENSITIVITY_MODE, SET, SettingsDefinitions.ExposureSensitivityMode.class),
                new DJIKeyInfo("Camera", CameraKey.VISION_STABILIZATION_ENABLED, SET, Boolean.class),
                new DJIKeyInfo("Camera", CameraKey.APERTURE, SET, SettingsDefinitions.Aperture.class),
                new DJIKeyInfo("Camera", CameraKey.SSD_LEGACY_COLOR, SET, SettingsDefinitions.SSDLegacyColor.class),
                new DJIKeyInfo("Camera", CameraKey.WHITE_BALANCE, SET, WhiteBalance.class),
                new DJIKeyInfo("Camera", CameraKey.FILE_INDEX_MODE, SET, SettingsDefinitions.FileIndexMode.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_ISOTHERM_UNIT, SET, SettingsDefinitions.ThermalIsothermUnit.class),
                new DJIKeyInfo("Camera", CameraKey.TAP_ZOOM_MULTIPLIER, SET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.VIDEO_STANDARD, SET, SettingsDefinitions.VideoStandard.class),
                new DJIKeyInfo("Camera", CameraKey.PHOTO_ASPECT_RATIO, SET, SettingsDefinitions.PhotoAspectRatio.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_SSO, SET, Integer.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_BACKGROUND_TEMPERATURE, SET, Float.class),
                new DJIKeyInfo("Camera", CameraKey.EXPOSURE_MODE, SET, SettingsDefinitions.ExposureMode.class),
                new DJIKeyInfo("Camera", CameraKey.THERMAL_WINDOW_REFLECTED_TEMPERATURE, SET, Float.class),
                new DJIKeyInfo("Camera", CameraKey.TAP_ZOOM_AT_TARGET, SET, PointF.class),

                new DJIKeyInfo("Camera", CameraKey.START_RECORD_VIDEO, ACTION, void.class),
                new DJIKeyInfo("Camera", CameraKey.FORMAT_SD_CARD, ACTION, void.class),
                new DJIKeyInfo("Camera", CameraKey.TRIGGER_THERMAL_FFC, ACTION, void.class),
                new DJIKeyInfo("Camera", CameraKey.START_SHOOT_PHOTO, ACTION, void.class),
                new DJIKeyInfo("Camera", CameraKey.STOP_SHOOT_PHOTO, ACTION, void.class),
                new DJIKeyInfo("Camera", CameraKey.STOP_CONTINUOUS_OPTICAL_ZOOM, ACTION, void.class),
                new DJIKeyInfo("Camera", CameraKey.STOP_RECORD_VIDEO, ACTION, void.class),
                new DJIKeyInfo("Camera", CameraKey.START_SENSOR_CLEANING, ACTION, void.class),
                new DJIKeyInfo("Camera", CameraKey.EXIT_SENSOR_CLEANING_MODE, ACTION, void.class),
                new DJIKeyInfo("Camera", CameraKey.LOAD_SETTINGS_FROM_PROFILE, ACTION, void.class),
                new DJIKeyInfo("Camera", CameraKey.INIT_SENSOR_CLEANING_MODE, ACTION, void.class),
                new DJIKeyInfo("Camera", CameraKey.SAVE_SETTINGS_TO_PROFILE, ACTION, void.class),
                new DJIKeyInfo("Camera", CameraKey.RESTORE_FACTORY_SETTINGS, ACTION, void.class),
                new DJIKeyInfo("Camera", CameraKey.START_CONTINUOUS_OPTICAL_ZOOM, ACTION, void.class),
                new DJIKeyInfo("Camera", CameraKey.FORMAT_SSD, ACTION, void.class),

                new DJIKeyInfo("Product", ProductKey.FIRMWARE_PACKAGE_VERSION, PUSH, String.class),
                new DJIKeyInfo("Product", ProductKey.MODEL_NAME, PUSH, Model.class),
                new DJIKeyInfo("Product", ProductKey.IS_OSMO, PUSH, Boolean.class),
                new DJIKeyInfo("Product", ProductKey.CONNECTION, PUSH, Boolean.class),

                new DJIKeyInfo("Battery", BatteryKey.SELF_DISCHARGE_IN_DAYS, GET, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.OVERVIEWS, GET, BatteryOverview[].class),
                new DJIKeyInfo("Battery", BatteryKey.AGGREGATION_STATE, GET, AggregationState.class),
                new DJIKeyInfo("Battery", BatteryKey.HIGHEST_TEMPERATURE, GET, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.LEVEL_2_CELL_VOLTAGE_BEHAVIOR, GET, LowVoltageBehavior.class),
                new DJIKeyInfo("Battery", BatteryKey.LEVEL_2_CELL_VOLTAGE_THRESHOLD, GET, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.LIFETIME_REMAINING, GET, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.IS_BATTERY_SELF_HEATING, GET, Boolean.class),
                new DJIKeyInfo("Battery", BatteryKey.LIFETIME_REMAINING, GET, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.CURRENT, GET, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.IS_CELL_DAMAGED, GET, Boolean.class),
                new DJIKeyInfo("Battery", BatteryKey.NUMBER_OF_CELLS, GET, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.IS_LOW_CELL_VOLTAGE_DETECTED, GET, Boolean.class),
                new DJIKeyInfo("Battery", BatteryKey.CHARGE_REMAINING, GET, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.CHARGE_REMAINING_IN_PERCENT, GET, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.HISTORICAL_WARNING_RECORDS, GET, WarningRecord[].class),
                new DJIKeyInfo("Battery", BatteryKey.LATEST_WARNING_RECORD, GET, WarningRecord.class),
                new DJIKeyInfo("Battery", BatteryKey.NUMBER_OF_DISCHARGES, GET, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.CELL_VOLTAGES, GET, Integer[].class),
                new DJIKeyInfo("Battery", BatteryKey.TEMPERATURE, GET, Float.class),
                new DJIKeyInfo("Battery", BatteryKey.VOLTAGE, GET, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.PAIRING_STATE, GET, PairingState.class),
                new DJIKeyInfo("Battery", BatteryKey.LEVEL_1_CELL_VOLTAGE_BEHAVIOR, GET, LowVoltageBehavior.class),
                new DJIKeyInfo("Battery", BatteryKey.IS_BIG_BATTERY, GET, Boolean.class),
                new DJIKeyInfo("Battery", BatteryKey.CONNECTION_STATE, GET, ConnectionState.class),
                new DJIKeyInfo("Battery", BatteryKey.NUMBER_OF_CONNECTED_BATTERIES, GET, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.FULL_CHARGE_CAPACITY, GET, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.IS_SMART_BATTERY, GET, Boolean.class),
                new DJIKeyInfo("Battery", BatteryKey.SERIAL_NUMBER, GET, String.class),
                new DJIKeyInfo("Battery", BatteryKey.IS_FIRMWARE_DIFFERENCE_DETECTED, GET, Boolean.class),
                new DJIKeyInfo("Battery", BatteryKey.FIRMWARE_VERSION, GET, String.class),
                new DJIKeyInfo("Battery", BatteryKey.LEVEL_1_CELL_VOLTAGE_THRESHOLD, GET, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.IS_ANY_BATTERY_DISCONNECTED, GET, Boolean.class),
                new DJIKeyInfo("Battery", BatteryKey.IS_VOLTAGE_DIFFERENCE_DETECTED, GET, Boolean.class),

                new DJIKeyInfo("Battery", BatteryKey.LIFETIME_REMAINING, PUSH, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.CURRENT, PUSH, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.CHARGE_REMAINING, PUSH, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.CHARGE_REMAINING_IN_PERCENT, PUSH, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.IS_IN_SINGLE_BATTERY_MODE, PUSH, Boolean.class),
                new DJIKeyInfo("Battery", BatteryKey.NUMBER_OF_DISCHARGES, PUSH, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.TEMPERATURE, PUSH, Float.class),
                new DJIKeyInfo("Battery", BatteryKey.VOLTAGE, PUSH, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.CELL_VOLTAGE_LEVEL, PUSH, BatteryCellVoltageLevel.class),
                new DJIKeyInfo("Battery", BatteryKey.CONNECTION_STATE, PUSH, ConnectionState.class),
                new DJIKeyInfo("Battery", BatteryKey.FULL_CHARGE_CAPACITY, PUSH, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.IS_BEING_CHARGED, PUSH, Boolean.class),

                new DJIKeyInfo("Battery", BatteryKey.SELF_DISCHARGE_IN_DAYS, SET, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.LEVEL_2_CELL_VOLTAGE_BEHAVIOR, SET, LowVoltageBehavior.class),
                new DJIKeyInfo("Battery", BatteryKey.LEVEL_2_CELL_VOLTAGE_THRESHOLD, SET, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.NUMBER_OF_CELLS, SET, Integer.class),
                new DJIKeyInfo("Battery", BatteryKey.LEVEL_1_CELL_VOLTAGE_BEHAVIOR, SET, LowVoltageBehavior.class),
                new DJIKeyInfo("Battery", BatteryKey.LEVEL_1_CELL_VOLTAGE_THRESHOLD, SET, Integer.class),

                new DJIKeyInfo("Battery", BatteryKey.PAIR_BATTERIES, ACTION, void.class),

/* All these values seem to be null. Only used on high end models?
            new DJIKeyInfo("AirLink",AirLinkKey.CHANNEL_RANGE,GET,Integer[].class),
            new DJIKeyInfo("AirLink",AirLinkKey.PASSWORD,GET,String.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_OSD_UNIT,GET,LightbridgeUnit.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_OUTPUT_PORT,GET,LightbridgeSecondaryVideoOutputPort.class),
            new DJIKeyInfo("AirLink",AirLinkKey.CHANNEL_INTERFERENCE,GET,WifiChannelInterference[].class),
            new DJIKeyInfo("AirLink",AirLinkKey.SSID,GET,String.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SUPPORTED_FREQUENCY_BANDS,GET,LightbridgeFrequencyBand[].class),
            new DJIKeyInfo("AirLink",AirLinkKey.AVAILABLE_CHANNEL_NUMBERS,GET,Integer[].class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_OSD_TOP_MARGIN,GET,Integer.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_OSD_LEFT_MARGIN,GET,Integer.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SDI_OUTPUT_FORMAT,GET,LightbridgeSecondaryVideoFormat.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_OSD_ENABLED,GET,Boolean.class),
            new DJIKeyInfo("AirLink",AirLinkKey.FREQUENCY_POINT_RSSIS,GET,FrequencyInterference[].class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_DISPLAY_MODE,GET,LightbridgeSecondaryVideoDisplayMode.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_OUTPUT_ENABLED,GET,Boolean.class),
            new DJIKeyInfo("AirLink",AirLinkKey.PIP_POSITION,GET,LightbridgePIPPosition.class),
            new DJIKeyInfo("AirLink",AirLinkKey.HDMI_OUTPUT_FORMAT,GET,LightbridgeSecondaryVideoFormat.class),
            new DJIKeyInfo("AirLink",AirLinkKey.IS_LIGHTBRIDGE_LINK_SUPPORTED,GET,Boolean.class),
            new DJIKeyInfo("AirLink",AirLinkKey.FREQUENCY_POINT_INDEX,GET,Integer.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_OSD_BOTTOM_MARGIN,GET,Integer.class),
            new DJIKeyInfo("AirLink",AirLinkKey.BANDWIDTH,GET,OcuSyncBandwidth.class),
            new DJIKeyInfo("AirLink",AirLinkKey.CHANNEL_SELECTION_MODE,GET,ChannelSelectionMode.class),
            new DJIKeyInfo("AirLink",AirLinkKey.DATA_RATE,GET,WifiDataRate.class),
            new DJIKeyInfo("AirLink",AirLinkKey.TRANSMISSION_MODE,GET,LightbridgeTransmissionMode.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_OSD_RIGHT_MARGIN,GET,Integer.class),
            new DJIKeyInfo("AirLink",AirLinkKey.BANDWIDTH_ALLOCATION_FOR_HDMI_VIDEO_INPUT_PORT,GET,Float.class),
            new DJIKeyInfo("AirLink",AirLinkKey.WIFI_FREQUENCY_BAND,GET,WiFiFrequencyBand.class),
            new DJIKeyInfo("AirLink",AirLinkKey.CHANNEL_NUMBER,GET,Integer.class),
            new DJIKeyInfo("AirLink",AirLinkKey.IS_WIFI_LINK_SUPPORTED,GET,Boolean.class),

            new DJIKeyInfo("AirLink",AirLinkKey.IS_EXT_VIDEO_INPUT_PORT_ENABLED,PUSH,Boolean.class),
            new DJIKeyInfo("AirLink",AirLinkKey.UPLINK_SIGNAL_QUALITY,PUSH,Integer.class),
            new DJIKeyInfo("AirLink",AirLinkKey.WARNING_MESSAGES,PUSH,OcuSyncWarningMessage[].class),
            new DJIKeyInfo("AirLink",AirLinkKey.REMOTE_CONTROLLER_ANTENNA_RSSI,PUSH,LightbridgeAntennaRSSI.class),
            new DJIKeyInfo("AirLink",AirLinkKey.AIRCRAFT_ANTENNA_RSSI,PUSH,LightbridgeAntennaRSSI.class),
            new DJIKeyInfo("AirLink",AirLinkKey.MAGNETIC_INTERFERENCE,PUSH,WiFiMagneticInterferenceLevel.class),
            new DJIKeyInfo("AirLink",AirLinkKey.BANDWIDTH_ALLOCATION_FOR_LEFT_CAMERA,PUSH,Float.class),
            new DJIKeyInfo("AirLink",AirLinkKey.FREQUENCY_POINT_INDEX_VALID_RANGE,PUSH,Integer[].class),
            new DJIKeyInfo("AirLink",AirLinkKey.DOWNLINK_SIGNAL_QUALITY,PUSH,Integer.class),
            new DJIKeyInfo("AirLink",AirLinkKey.BANDWIDTH_ALLOCATION_FOR_LB_VIDEO_INPUT_PORT,PUSH,Float.class),
            new DJIKeyInfo("AirLink",AirLinkKey.LB_FREQUENCY_BAND,PUSH,LightbridgeFrequencyBand.class),
            new DJIKeyInfo("AirLink",AirLinkKey.DYNAMIC_DATA_RATE,PUSH,Float.class),

            new DJIKeyInfo("AirLink",AirLinkKey.IS_EXT_VIDEO_INPUT_PORT_ENABLED,SET,Boolean.class),
            new DJIKeyInfo("AirLink",AirLinkKey.PASSWORD,SET,String.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_OSD_UNIT,SET,LightbridgeUnit.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_OUTPUT_PORT,SET,LightbridgeSecondaryVideoOutputPort.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SSID,SET,String.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_OSD_TOP_MARGIN,SET,Integer.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_OSD_LEFT_MARGIN,SET,Integer.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SDI_OUTPUT_FORMAT,SET,LightbridgeSecondaryVideoFormat.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_OSD_ENABLED,SET,Boolean.class),
            new DJIKeyInfo("AirLink",AirLinkKey.BANDWIDTH_ALLOCATION_FOR_LEFT_CAMERA,SET,Float.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_DISPLAY_MODE,SET,LightbridgeSecondaryVideoDisplayMode.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_OUTPUT_ENABLED,SET,Boolean.class),
            new DJIKeyInfo("AirLink",AirLinkKey.PIP_POSITION,SET,LightbridgePIPPosition.class),
            new DJIKeyInfo("AirLink",AirLinkKey.HDMI_OUTPUT_FORMAT,SET,LightbridgeSecondaryVideoFormat.class),
            new DJIKeyInfo("AirLink",AirLinkKey.BANDWIDTH_ALLOCATION_FOR_LB_VIDEO_INPUT_PORT,SET,Float.class),
            new DJIKeyInfo("AirLink",AirLinkKey.LB_FREQUENCY_BAND,SET,LightbridgeFrequencyBand.class),
            new DJIKeyInfo("AirLink",AirLinkKey.FREQUENCY_POINT_INDEX,SET,Integer.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_OSD_BOTTOM_MARGIN,SET,Integer.class),
            new DJIKeyInfo("AirLink",AirLinkKey.BANDWIDTH,SET,OcuSyncBandwidth.class),
            new DJIKeyInfo("AirLink",AirLinkKey.CHANNEL_SELECTION_MODE,SET,ChannelSelectionMode.class),
            new DJIKeyInfo("AirLink",AirLinkKey.DATA_RATE,SET,WifiDataRate.class),
            new DJIKeyInfo("AirLink",AirLinkKey.TRANSMISSION_MODE,SET,LightbridgeTransmissionMode.class),
            new DJIKeyInfo("AirLink",AirLinkKey.SECONDARY_VIDEO_OSD_RIGHT_MARGIN,SET,Integer.class),
            new DJIKeyInfo("AirLink",AirLinkKey.BANDWIDTH_ALLOCATION_FOR_HDMI_VIDEO_INPUT_PORT,SET,Float.class),
            new DJIKeyInfo("AirLink",AirLinkKey.WIFI_FREQUENCY_BAND,SET,WiFiFrequencyBand.class),
            new DJIKeyInfo("AirLink",AirLinkKey.CHANNEL_NUMBER,SET,Integer.class),

            new DJIKeyInfo("AirLink", AirLinkKey.REBOOT,ACTION,void.class),
*/


        };


}
    /*The above array is semi generated by running this script on the SDK documentation webpage.
     */
     /*Using this javascript.
            $(".key_table_title_string").remove()
            var base = $("p code:eq(0)").text();
            var names= $(".key_table_key_string")
            var acc= $(".key_table_key_string").parent().parent().next().find("td:eq(1)")
            var types = $(".key_table_key_string").parent().parent().next().next().find("td:eq(1)")

            var both = names.toArray().map(function(e, i) {
              return [e, acc.toArray()[i],types.toArray()[i]];
            });
            var gets=[]
            var pushes=[]
            var actions=[]
            var sets=[]

            for(var i=0;i<both.length;i++)
            {
                var t =$(both[i][2]).text().trim();
                if(t==="-")
                    t="void";
                var at = $(both[i][1]).text().trim();


                //sort by access type.
                if(at.indexOf("GET")>-1)
                    gets.push('\n new DJIKeyInfo("'+base+'",'+base.trim()+'.'+$(both[i][0]).text().trim()+',GET,'+t+'.class)')
                if(at.indexOf("PUSH")>-1)
                    pushes.push('\n new DJIKeyInfo("'+base+'",'+base.trim()+'.'+$(both[i][0]).text().trim()+',PUSH,'+t+'.class)')
                if(at.indexOf("SET")>-1)
                    sets.push('\n new DJIKeyInfo("'+base+'",'+base.trim()+'.'+$(both[i][0]).text().trim()+',SET,'+t+'.class)')
                if(at.indexOf("ACTION")>-1)
                    actions.push('\n new DJIKeyInfo("'+base+'",'+base.trim()+'.'+$(both[i][0]).text().trim()+',ACTION,'+t+'.class)')

                //console.log('new DJIKeyInfo("'+base+'",'+base.trim()+'.'+$(both[i][0]).text().trim()+',new KeyAccessTypes[]{'+$(both[i][1]).text().trim()+'},'+t+'.class),');
            }
            console.log(gets.toString())
            console.log(pushes.toString())
            console.log(sets.toString())
            console.log(actions.toString());

     */
