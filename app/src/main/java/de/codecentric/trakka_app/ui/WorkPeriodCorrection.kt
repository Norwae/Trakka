package de.codecentric.trakka_app.ui

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class WorkPeriodCorrection(val start: Date, val end: Date): Parcelable {
    companion object {
        @JvmField val CREATOR = object: Parcelable.Creator<WorkPeriodCorrection> {
            override fun newArray(size: Int): Array<WorkPeriodCorrection?> = arrayOfNulls(size)

            override fun createFromParcel(source: Parcel): WorkPeriodCorrection = WorkPeriodCorrection(
                Date(source.readLong()),
                Date(source.readLong())
            )
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(start.time)
        dest.writeLong(end.time)
    }

    override fun describeContents(): Int = 0
}