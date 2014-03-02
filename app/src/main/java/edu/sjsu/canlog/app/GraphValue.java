package edu.sjsu.canlog.app;
import android.os.Parcel;
import android.os.Parcelable;

public class GraphValue implements Parcelable {
    private Number mData;

    public GraphValue(Number num)
    {
        mData = num;
    }

    public Number getValue()
    {
        return mData;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(mData);
    }

    public static final Parcelable.Creator<GraphValue> CREATOR
            = new Parcelable.Creator<GraphValue>() {
        public GraphValue createFromParcel(Parcel in) {
            return new GraphValue(in);
        }

        public GraphValue[] newArray(int size) {
            return new GraphValue[size];
        }
    };

    private GraphValue(Parcel in) {
        mData = (Number) in.readValue(Number.class.getClassLoader());
    }
}