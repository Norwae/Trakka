package de.codecentric.trakka_app.ui

import android.os.Parcel
import android.os.Parcelable
import org.joda.time.DateTime
import java.util.*

data class WorkPeriodCorrection(val start: DateTime, val end: DateTime): Parcelable {
    companion object {
        @Suppress("unused") @JvmField
        val CREATOR = object: Parcelable.Creator<WorkPeriodCorrection> {
            override fun newArray(size: Int): Array<WorkPeriodCorrection?> = arrayOfNulls(size)

            override fun createFromParcel(source: Parcel): WorkPeriodCorrection = WorkPeriodCorrection(
                DateTime(source.readLong()),
                DateTime(source.readLong())
            )
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(start.toInstant().millis)
        dest.writeLong(end.toInstant().millis)
    }

    override fun describeContents(): Int = 0
}