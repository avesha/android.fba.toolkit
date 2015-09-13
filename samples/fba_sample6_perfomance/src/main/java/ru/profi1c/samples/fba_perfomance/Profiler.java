package ru.profi1c.samples.fba_perfomance;

import android.util.Pair;

import java.util.HashMap;

public class Profiler {
    private HashMap<String, Pair<Long, Boolean>> mTimers;

    public Profiler() {
        mTimers = new HashMap<>();
    }

    public void start(String key) {
        mTimers.put(key, new Pair(System.currentTimeMillis(), false));
    }

    public long stop(String key) {
        long now = System.currentTimeMillis();
        if (!mTimers.containsKey(key)) {
            throw new IllegalStateException("Profiler: error stop, key " + key + " not found!");
        }
        Pair<Long, Boolean> pair = mTimers.get(key);
        if (pair.second) {
            throw new IllegalStateException(
                    "Profiler: error stop, key " + key + " already stopped");
        }
        long time = now - pair.first;
        mTimers.put(key, new Pair(time, true));
        return time;
    }

    public void reset() {
        mTimers.clear();
    }

    public long getResultTime() {
        long allTime = 0;
        for (Pair<Long, Boolean> pair : mTimers.values()) {
            if (pair.second) {
                allTime += pair.first;
            }
        }
        return allTime;
    }
}
