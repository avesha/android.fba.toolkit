package ru.profi1c.samples.sensus.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;

import ru.profi1c.samples.sensus.Dbg;
import ru.profi1c.samples.sensus.db.CatalogSalesPoints;
import ru.profi1c.samples.sensus.db.CatalogTasks;

/*
Помощник добавления события в календарь
 */
public final class CalendarHelper {

    /**
     * Предложить добавить событие в календарь по задаче
     *
     * @param context
     * @param task
     */
    public static void promptFromTask(Context context, CatalogTasks task) {
        String location = "";
        CatalogSalesPoints point = task.salesPoint;
        if (!CatalogSalesPoints.isEmpty(point)) {
            location = point.address;
        }

        Intent intent =
                new Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI)
                                                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                                          task.dateBegin.getTime())
                                                .putExtra(CalendarContract.Events.TITLE,
                                                          task.getDescription())
                                                .putExtra(CalendarContract.Events.DESCRIPTION,
                                                          task.comment)
                                                .putExtra(CalendarContract.Events.EVENT_LOCATION,
                                                          location)
                                                .putExtra(CalendarContract.Events.AVAILABILITY,
                                                          CalendarContract.Events.AVAILABILITY_BUSY);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Dbg.printStackTrace(e);
        }

    }

    private CalendarHelper() {
    }
}
