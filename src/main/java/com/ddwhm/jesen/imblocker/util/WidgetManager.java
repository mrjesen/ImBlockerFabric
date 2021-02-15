package com.ddwhm.jesen.imblocker.util;

import com.ddwhm.jesen.imblocker.ImManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class WidgetManager {
    private static final ReentrantLock lock = new ReentrantLock(true);
    private static final Map<Object, Long> widgetLifeTime = new HashMap<>();
    private static final Map<Object, Boolean> widgetStatus = new HashMap<>();

    public static void updateWidgetStatus(Object widget, boolean status) {
        lock.lock();
        widgetLifeTime.put(widget, 0L);
        widgetStatus.put(widget, status);
        updateImManager();
        lock.unlock();
    }

    public static void updateLifeTime(Object widget) {
        lock.lock();
        widgetLifeTime.put(widget, 0L);
        updateImManager();
        lock.unlock();
    }

    public static void tick() {
        lock.lock();
        Iterator<Map.Entry<Object, Long>> it = widgetLifeTime.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Object, Long> entry = it.next();
            if (entry.getValue() > 20) {
                widgetStatus.remove(entry.getKey());
                it.remove();
            }
        }
        widgetLifeTime.replaceAll((k, v) -> v + 1);
        lock.unlock();
    }

    private static void updateImManager() {
        boolean on = false;
        for (boolean v : widgetStatus.values()) {
            on |= v;
        }
        if (on) {
            ImManager.makeOn();
        } else {
            ImManager.makeOff();
        }
    }

    public static void clear() {
        lock.lock();
        widgetLifeTime.clear();
        widgetStatus.clear();
        lock.unlock();
        ImManager.makeOff();
    }
}
