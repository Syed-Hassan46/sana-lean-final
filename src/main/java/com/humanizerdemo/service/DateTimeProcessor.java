package com.humanizerdemo.service;

import java.util.Date;

public class DateTimeProcessor {

    private static final long ONE_SECOND_MS  = 1_000L;
    private static final long ONE_MINUTE_MS  = 60 * ONE_SECOND_MS;
    private static final long ONE_HOUR_MS    = 60 * ONE_MINUTE_MS;
    private static final long ONE_DAY_MS     = 24 * ONE_HOUR_MS;
    private static final long ONE_WEEK_MS    = 7  * ONE_DAY_MS;
    private static final long ONE_MONTH_MS   = 30 * ONE_DAY_MS;
    private static final long ONE_YEAR_MS    = 365 * ONE_DAY_MS;

    public String describeRelativeToNow(Date pastOrFutureDate) {
        if (pastOrFutureDate == null) {
            throw new IllegalArgumentException("pastOrFutureDate must not be null");
        }
        return describeRelativeTo(pastOrFutureDate, new Date());
    }

    public String describeRelativeTo(Date targetDate, Date referenceDate) {
        if (targetDate == null) {
            throw new IllegalArgumentException("targetDate must not be null");
        }
        if (referenceDate == null) {
            throw new IllegalArgumentException("referenceDate must not be null");
        }
        long differenceMs = referenceDate.getTime() - targetDate.getTime();
        boolean isInThePast = differenceMs >= 0;
        long absoluteDifferenceMs = Math.abs(differenceMs);

        String relativeDescription = buildRelativeDescription(absoluteDifferenceMs);

        if (absoluteDifferenceMs < ONE_SECOND_MS) {
            return "just now";
        }
        return isInThePast
            ? relativeDescription + " ago"
            : "in " + relativeDescription;
    }

    private String buildRelativeDescription(long absoluteDifferenceMs) {
        if (absoluteDifferenceMs < ONE_MINUTE_MS) {
            long seconds = absoluteDifferenceMs / ONE_SECOND_MS;
            return seconds == 1 ? "a second" : seconds + " seconds";
        }
        if (absoluteDifferenceMs < ONE_HOUR_MS) {
            long minutes = absoluteDifferenceMs / ONE_MINUTE_MS;
            return minutes == 1 ? "a minute" : minutes + " minutes";
        }
        if (absoluteDifferenceMs < ONE_DAY_MS) {
            long hours = absoluteDifferenceMs / ONE_HOUR_MS;
            return hours == 1 ? "an hour" : hours + " hours";
        }
        if (absoluteDifferenceMs < ONE_WEEK_MS) {
            long days = absoluteDifferenceMs / ONE_DAY_MS;
            return days == 1 ? "a day" : days + " days";
        }
        if (absoluteDifferenceMs < ONE_MONTH_MS) {
            long weeks = absoluteDifferenceMs / ONE_WEEK_MS;
            return weeks == 1 ? "a week" : weeks + " weeks";
        }
        if (absoluteDifferenceMs < ONE_YEAR_MS) {
            long months = absoluteDifferenceMs / ONE_MONTH_MS;
            return months == 1 ? "a month" : months + " months";
        }
        long years = absoluteDifferenceMs / ONE_YEAR_MS;
        return years == 1 ? "a year" : years + " years";
    }

    public String describeDurationInMilliseconds(long durationInMilliseconds) {
        if (durationInMilliseconds < 0) {
            throw new IllegalArgumentException("durationInMilliseconds must be zero or greater");
        }
        return buildRelativeDescription(durationInMilliseconds);
    }
}
