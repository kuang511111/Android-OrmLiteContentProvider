package com.tojc.ormlite.android.framework.event.listener;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.tojc.ormlite.android.framework.MatcherPattern;
import com.tojc.ormlite.android.framework.OperationParameters.UpdateParameters;
import com.tojc.ormlite.android.framework.event.listenerbase.ContentProviderEventListenerInterfaceBase;

/**
 * Created by Jaken on 2014/05/05.
 */
public interface OnUpdateListener<T extends OrmLiteSqliteOpenHelper> extends ContentProviderEventListenerInterfaceBase {
    /**
     * You implement this method. At the timing of update() method, which calls the onUpdate().
     *
     * @param helper    This is a helper object. It is the same as one that can be retrieved by
     *                  this.getHelper().
     * @param db        This is a SQLiteDatabase object. Return the object obtained by
     *                  helper.getWritableDatabase().
     * @param target    It is MatcherPattern objects that match to evaluate Uri by UriMatcher. You can
     *                  access information in the tables and columns, ContentUri, MimeType etc.
     * @param parameter Arguments passed to the update() method.
     * @return Please set value to be returned in the original update() method.
     */
    int onUpdate(T helper, SQLiteDatabase db, MatcherPattern target, UpdateParameters parameter);
}
