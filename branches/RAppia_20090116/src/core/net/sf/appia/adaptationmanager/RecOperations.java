package net.sf.appia.adaptationmanager;

public enum RecOperations {
    SET_VALUE {public String toString() { return "setValue"; }},
    GET_SERVICE_STATE {public String toString() { return "getServiceState"; }},
    SET_SERVICE_STATE {public String toString() { return "setServiceState"; }},
    START_SERVICE {public String toString() { return "startService"; }},
    STOP_SERVICE {public String toString() { return "stopService"; }}
}



