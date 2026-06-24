package com.humanizerdemo.service;

import java.util.Date;

public class DateTimeProcessor {

    private static final long ONE_SECOND_MS = 1_000L;
    private static final long ONE_MINUTE_MS = 60 * ONE_SECOND_MS;
    private static final long ONE_HOUR_MS   = 60 * ONE_MINUTE_MS;
    private static final long ONE_DAY_MS    = 24 * ONE_HOUR_MS;
    private static final long ONE_WEEK_MS   = 7  * ONE_DAY_MS;
    private static final long ONE_MONTH_MS  = 30 * ONE_DAY_MS;
    private static final long ONE_YEAR_MS   = 365 * ONE_DAY_MS;

    // threshold table: {cutoff in ms, singular label, plural label}
    private static final Object[][] THRESHOLDS = {
        { ONE_MINUTE_MS, ONE_SECOND_MS, "a second",  "seconds" },
        { ONE_HOUR_MS,   ONE_MINUTE_MS, "a minute",  "minutes" },
        { ONE_DAY_MS,    ONE_HOUR_MS,   "an hour",   "hours"   },
        { ONE_WEEK_MS,   ONE_DAY_MS,    "a day",     "days"    },
        { ONE_MONTH_MS,  ONE_WEEK_MS,   "a week",    "weeks"   },
        { ONE_YEAR_MS,   ONE_MONTH_MS,  "a month",   "months"  },
        { Long.MAX_VALUE, ONE_YEAR_MS,  "a year",    "years"   },
    };

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
        long diffMs = referenceDate.getTime() - targetDate.getTime();
        boolean isPast = diffMs >= 0;
        long abs = Math.abs(diffMs);

        if (abs < ONE_SECOND_MS) {
            return "just now";
        }
        String desc = buildDesc(abs);
        return isPast ? desc + " ago" : "in " + desc;
    }

    // TODO: consider exposing threshold config if callers need custom granularity
    private String buildDesc(long ms) {
        for (Object[] row : THRESHOLDS) {
            long cutoff = (long) row[0];
            long unit   = (long) row[1];
            if (ms < cutoff) {
                long n = ms / unit;
                return n == 1 ? (String) row[2] : n + " " + row[3];
            }
        }
        // shouldn't reach here; last threshold is MAX_VALUE
        return "a long time";
    }

    public String describeDurationInMilliseconds(long durationMs) {
        if (durationMs < 0) {
            throw new IllegalArgumentException("durationInMilliseconds must be zero or greater");
        }
        return buildDesc(durationMs);
    }
}
