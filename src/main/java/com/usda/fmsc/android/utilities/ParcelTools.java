package com.usda.fmsc.android.utilities;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelTools {
    
    public static void writeNDouble(Parcel dest, Double value) {
        if (value != null) {
            writeBool(dest, true);
            dest.writeDouble(value);
        } else {
            writeBool(dest, false);
        }
    }

    public static Double readNDouble(Parcel source) {
        return readBool(source) ? source.readDouble() : null;
    }


    public static void writeNFloat(Parcel dest, Float value) {
        if (value != null) {
            writeBool(dest, true);
            dest.writeFloat(value);
        } else {
            writeBool(dest, false);
        }
    }

    public static Float readNFloat(Parcel source) {
        return readBool(source) ? source.readFloat() : null;
    }
    

    public static void writeNInt(Parcel dest, Integer value) {
        if (value != null) {
            writeBool(dest, true);
            dest.writeInt(value);
        } else {
            writeBool(dest, false);
        }
    }

    public static Integer readNInt(Parcel source) {
        return readBool(source) ? source.readInt() : null;
    }


    public static void writeBool(Parcel dest, boolean value) {
        dest.writeByte((byte)(value ? 1 : 0));
    }
    
    public static boolean readBool(Parcel source) {
        return source.readByte() > 0;
    }


    public static void writeNString(Parcel dest, String value) {
        if (value != null) {
            writeBool(dest, true);
            dest.writeString(value);
        } else {
            writeBool(dest, false);
        }
    }

    public static String readNString(Parcel source) {
        return readBool(source) ? source.readString() : null;
    }


    public static void writeNParcelable(Parcel dest, Parcelable value, int flags) {
        if (value != null) {
            writeBool(dest, true);
            dest.writeParcelable(value, flags);
        } else {
            writeBool(dest, false);
        }
    }

    public static Parcelable readNParcelable(Parcel source, ClassLoader loader) {
        return readBool(source) ? source.readParcelable(loader): null;
    }
}
