package android.io.binobo.connector;

public class Configuration {

    public enum State { // TODO
        UNKNOWN_STATE,
        CONFIG_START,
        WIFI_DATA_VALID,
        WIFI_DATA_INVALID,
        TOKEN_VALID,
        TOKEN_INVALID,
        LOCAL_DATA_FOUND,
        WIFI_CONFIG_SSID,
        WIFI_CONFIG_PASSWORD,
        TOKEN,
        CALIBRATION,
        CALIBRATION_ZERO_POS,
        CALIBRATION_DONE,
        WEB_SOCKET_CONNECTING,
        WEB_SOCKET_CONNECTED,
        STORE_DATA,
        DATA_STORED,
        DONE
    }

    public static State getState (String state) { // TODO
        switch (state) {
            case "[ESP32]: Configuration starts...": return State.CONFIG_START;
            case "Use local stored config data? [y/n]: ": return State.LOCAL_DATA_FOUND;
            case "SSID:": return State.WIFI_CONFIG_SSID;
            case "Token:": return State.TOKEN;
            default: return State.UNKNOWN_STATE;
        }
    }

}
