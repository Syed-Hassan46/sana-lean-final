package com.humanizerdemo.service;

import com.github.mfornos.humanize.Humanize;

import java.util.Date;

public class DateTimeProcessor {

    public String describeRelativeToNow(Date pastOrFutureDate) {
        if (pastOrFutureDate == null) {
            throw new IllegalArgumentException("pastOrFutureDate must not be null");
        }
        return Humanize.naturalTime(pastOrFutureDate);
    }

    public String describeRelativeTo(Date targetDate, Date referenceDate) {
        if (targetDate == null) {
            throw new IllegalArgumentException("targetDate must not be null");
        }
        if (referenceDate == null) {
            throw new IllegalArgumentException("referenceDate must not be null");
        }
        return Humanize.naturalTime(referenceDate, targetDate);
    }

    public String describeDurationInMilliseconds(long durationInMilliseconds) {
        if (durationInMilliseconds < 0) {
            throw new IllegalArgumentException("durationInMilliseconds must be zero or greater");
        }
        return Humanize.duration(durationInMilliseconds / 1000.0);
    }
}
