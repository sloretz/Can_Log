package edu.sjsu.canlog.app.backend;

import java.util.HashMap;

/**
 * Created by shane on 4/27/14.
 */
public class PrettyPID {
    private static HashMap<Integer,String> descriptions;

    protected static void _init()
    {
        if (descriptions == null)
        {
            //taken from http://en.wikipedia.org/wiki/OBD-II_PIDs
            descriptions = new HashMap<Integer, String>();
            descriptions.put(null, "ERR PrettyPID Unknown");
            descriptions.put(0x1, "Monitor status since DTCs cleared");
            descriptions.put(0x2, "Freeze DTC");
            descriptions.put(0x3, "Fuel system status");
            descriptions.put(0x4, "Calculated engine load value");
            descriptions.put(0x5, "Engine coolant temperature");
            descriptions.put(0x6, "Short term fuel % trim—Bank 1");
            descriptions.put(0x7, "Long term fuel % trim—Bank 1");
            descriptions.put(0x8, "Short term fuel % trim—Bank 2");
            descriptions.put(0x9, "Long term fuel % trim—Bank 2");
            descriptions.put(0xa, "Fuel pressure");
            descriptions.put(0xb, "Intake manifold absolute pressure");
            descriptions.put(0xc, "Engine RPM");
            descriptions.put(0xd, "Vehicle speed");
            descriptions.put(0xe, "Timing advance");
            descriptions.put(0xf, "Intake air temperature");
            descriptions.put(0x10, "MAF air flow rate");
            descriptions.put(0x11, "Throttle position");
            descriptions.put(0x12, "Commanded secondary air status");
            descriptions.put(0x13, "Oxygen sensors present");
            descriptions.put(0x14, "Bank 1, Sensor 1:" +
                    "Oxygen sensor voltage," +
                    "Short term fuel trim");
            descriptions.put(0x15, "Bank 1, Sensor 2:" +
                    "Oxygen sensor voltage," +
                    "Short term fuel trim");
            descriptions.put(0x16, "Bank 1, Sensor 3:" +
                    "Oxygen sensor voltage," +
                    "Short term fuel trim");
            descriptions.put(0x17, "Bank 1, Sensor 4:" +
                    "Oxygen sensor voltage," +
                    "Short term fuel trim");
            descriptions.put(0x18, "Bank 2, Sensor 1:" +
                    "Oxygen sensor voltage," +
                    "Short term fuel trim");
            descriptions.put(0x19, "Bank 2, Sensor 2:" +
                    "Oxygen sensor voltage," +
                    "Short term fuel trim");
            descriptions.put(0x1a, "Bank 2, Sensor 3:" +
                    "Oxygen sensor voltage," +
                    "Short term fuel trim");
            descriptions.put(0x1b, "Bank 2, Sensor 4:" +
                    "Oxygen sensor voltage," +
                    "Short term fuel trim");
            descriptions.put(0x1c, "OBD standards this vehicle conforms to");
            descriptions.put(0x1d, "Oxygen sensors present");
            descriptions.put(0x1e, "Auxiliary input status");
            descriptions.put(0x1f, "Run time since engine start");
            descriptions.put(0x20, "PIDs supported [21 - 40]");
            descriptions.put(0x21, "Distance traveled with malfunction indicator lamp (MIL) on");
            descriptions.put(0x22, "Fuel Rail Pressure (relative to manifold vacuum)");
            descriptions.put(0x23, "Fuel Rail Pressure (diesel, or gasoline direct inject)");
            descriptions.put(0x24, "O2S1_WR_lambda(1):" +
                    "Equivalence Ratio" +
                    "Voltage");
            descriptions.put(0x25, "O2S2_WR_lambda(1):" +
                    "Equivalence Ratio" +
                    "Voltage");
            descriptions.put(0x26, "O2S3_WR_lambda(1):" +
                    "Equivalence Ratio" +
                    "Voltage");
            descriptions.put(0x27, "O2S4_WR_lambda(1):" +
                    "Equivalence Ratio" +
                    "Voltage");
            descriptions.put(0x28, "O2S5_WR_lambda(1):" +
                    "Equivalence Ratio" +
                    "Voltage");
            descriptions.put(0x29, "O2S6_WR_lambda(1):" +
                    "Equivalence Ratio" +
                    "Voltage");
            descriptions.put(0x2a, "O2S7_WR_lambda(1):" +
                    "Equivalence Ratio" +
                    "Voltage");
            descriptions.put(0x2b, "O2S8_WR_lambda(1):" +
                    "Equivalence Ratio" +
                    "Voltage");
            descriptions.put(0x2c, "Commanded EGR");
            descriptions.put(0x2d, "EGR Error");
            descriptions.put(0x2e, "Commanded evaporative purge");
            descriptions.put(0x2f, "Fuel Level Input");
            descriptions.put(0x30, "# of warm-ups since codes cleared");
            descriptions.put(0x31, "Distance traveled since codes cleared");
            descriptions.put(0x32, "Evap. System Vapor Pressure");
            descriptions.put(0x33, "Barometric pressure");
            descriptions.put(0x34, "O2S1_WR_lambda(1):\n" +
                    "Equivalence Ratio\n" +
                    "Current");
            descriptions.put(0x35, "O2S2_WR_lambda(1):\n" +
                    "Equivalence Ratio\n" +
                    "Current");
            descriptions.put(0x36, "O2S3_WR_lambda(1):\n" +
                    "Equivalence Ratio\n" +
                    "Current");
            descriptions.put(0x37, "O2S4_WR_lambda(1):\n" +
                    "Equivalence Ratio\n" +
                    "Current");
            descriptions.put(0x38, "O2S5_WR_lambda(1):\n" +
                    "Equivalence Ratio\n" +
                    "Current");
            descriptions.put(0x39, "O2S6_WR_lambda(1):\n" +
                    "Equivalence Ratio\n" +
                    "Current");
            descriptions.put(0x3a, "O2S7_WR_lambda(1):\n" +
                    "Equivalence Ratio\n" +
                    "Current");
            descriptions.put(0x3b, "O2S8_WR_lambda(1):\n" +
                    "Equivalence Ratio\n" +
                    "Current");
            descriptions.put(0x3c, "Catalyst Temperature\n" +
                    "Bank 1, Sensor 1");
            descriptions.put(0x3d, "Catalyst Temperature\n" +
                    "Bank 2, Sensor 1");
            descriptions.put(0x3e, "Catalyst Temperature\n" +
                    "Bank 1, Sensor 2");
            descriptions.put(0x3f, "Catalyst Temperature\n" +
                    "Bank 2, Sensor 2");
            descriptions.put(0x40, "PIDs supported [41 - 60]");
            descriptions.put(0x41, "Monitor status this drive cycle");
            descriptions.put(0x42, "Control module voltage");
            descriptions.put(0x43, "Absolute load value");
            descriptions.put(0x44, "Command equivalence ratio");
            descriptions.put(0x45, "Relative throttle position");
            descriptions.put(0x46, "Ambient air temperature");
            descriptions.put(0x47, "Absolute throttle position B");
            descriptions.put(0x48, "Absolute throttle position C");
            descriptions.put(0x49, "Accelerator pedal position D");
            descriptions.put(0x4a, "Accelerator pedal position E");
            descriptions.put(0x4b, "Accelerator pedal position F");
            descriptions.put(0x4c, "Commanded throttle actuator");
            descriptions.put(0x4d, "Time run with MIL on");
            descriptions.put(0x4e, "Time since trouble codes cleared");
            descriptions.put(0x4f, "Maximum value for equivalence ratio, oxygen sensor voltage, oxygen sensor current, and intake manifold absolute pressure");
            descriptions.put(0x50, "Maximum value for air flow rate from mass air flow sensor");
            descriptions.put(0x51, "Fuel Type");
            descriptions.put(0x52, "Ethanol fuel %");
            descriptions.put(0x53, "Absolute Evap system Vapor Pressure");
            descriptions.put(0x54, "Evap system vapor pressure");
            descriptions.put(0x55, "Short term secondary oxygen sensor trim bank 1 and bank 3");
            descriptions.put(0x56, "Long term secondary oxygen sensor trim bank 1 and bank 3");
            descriptions.put(0x57, "Short term secondary oxygen sensor trim bank 2 and bank 4");
            descriptions.put(0x58, "Long term secondary oxygen sensor trim bank 2 and bank 4");
            descriptions.put(0x59, "Fuel rail pressure (absolute)");
            descriptions.put(0x5a, "Relative accelerator pedal position");
            descriptions.put(0x5b, "Hybrid battery pack remaining life");
            descriptions.put(0x5c, "Engine oil temperature");
            descriptions.put(0x5d, "Fuel injection timing");
            descriptions.put(0x5e, "Engine fuel rate");
            descriptions.put(0x5f, "Emission requirements to which vehicle is designed");
            descriptions.put(0x60, "PIDs supported [61 - 80]");
        }
    }

    public static Number formatData(String PID, String raw)
    {
        return formatData(toInteger(PID), raw);
    }

    public static Number formatData(Integer PID, String raw)
    {
        _init();
        //Return the right format
        String type = getType(PID);

        if (type == "int")
        {
            return Integer.valueOf(raw);
        }
        if (type == "float")
        {
            return Float.valueOf(raw);
        }
        if (type == "double")
        {
            return Double.valueOf(raw);
        }
        return 0;
    }

    public static String getType(String PID)
    {
        return getType(toInteger(PID));
    }

    public static String getType(Integer PID)
    {
        _init();
        //This should give the type for
        //ret values: "int", "double", "float"
        return "int";
    }

    public static String getDescription(String PID)
    {
        return getDescription(toInteger(PID));
    }

    public static String getDescription(Integer PID)
    {
        _init();
        return descriptions.get(PID);
    }

    public static Integer toInteger(String PID)
    {
        //x05 -> 5
        return Integer.valueOf("0"+PID);
    }

}
