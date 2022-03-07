package android.io.binobo.connector;

public class Configuration {

    public enum State {
        HIT_ENTER_TO_START,
        UNKNOWN_STATE,
        CONFIG_START,
        WIFI_DATA_VALID,
        WIFI_DATA_INVALID,
        TOKEN_VALID,
        TOKEN_INVALID,
        TOKEN_VALIDATING,
        LOCAL_DATA_FOUND,
        WIFI_CONFIG_SSID,
        WIFI_CONFIG_PASSWORD,
        TOKEN,
        CALIBRATION,
        CALIBRATION_ZERO_POS,
        CALIBRATION_DONE,
        WEB_SOCKET_CONNECTING,
        WEB_SOCKET_CONNECTED,
        WEB_SOCKET_COULD_NOT_CONNECT,
        STORE_DATA,
        DATA_STORED,
        DONE
    }

    public static State getState (String state) {
        switch (state) {
            case "Hit <enter> to start configuration...": return State.HIT_ENTER_TO_START;
            case "[ESP32]: Configuration starts...": return State.CONFIG_START;
            case "Use local stored config data? [y/n]:": return State.LOCAL_DATA_FOUND;
            case "SSID:": return State.WIFI_CONFIG_SSID;
            case "Password:": return State.WIFI_CONFIG_PASSWORD;
            case "Token:": return State.TOKEN;
            case "[ESP32]: Connection successfully established!": return State.WIFI_DATA_VALID;
            case "[ESP32]: Error occurred while connecting, please try again.": return State.WIFI_DATA_INVALID;
            case "[ESP32]: Validating token...": return State.TOKEN_VALIDATING;
            case "[ESP32]: Token valid.": return State.TOKEN_VALID;
            case "[ESP32]: Token not valid, try again.": return State.TOKEN_INVALID;
            case "[ESP32]: Calibration starts...": return State.CALIBRATION;
            case "[ESP32]: Zero Position --> Waiting for verification...": return State.CALIBRATION_ZERO_POS;
            case "[ESP32]: Calibration done.": return State.CALIBRATION_DONE;
            case "[ESP32]: Connecting to Websocket Server...": return State.WEB_SOCKET_CONNECTING;
            case "[ESP32]: Connections successfully established!": return State.WEB_SOCKET_CONNECTED;
            case "[ESP32]: Couldn't connect to Websocket!": return State.WEB_SOCKET_COULD_NOT_CONNECT;
            case "Store configuration data? [y/n]:": return State.STORE_DATA;
            case "[ESP32]: Config-Data stored!": return State.DATA_STORED;
            case "[ESP32]: Configuration done! Have fun!": return State.DONE;
            default: return State.UNKNOWN_STATE;
        }
    }

}
