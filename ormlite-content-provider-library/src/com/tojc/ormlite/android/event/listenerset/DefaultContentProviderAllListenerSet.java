/*
 * This file is part of the Android-OrmLiteContentProvider package.
 *
 * Copyright (c) 2012, Android-OrmLiteContentProvider Team.
 *                     Jaken Jarvis (jaken.jarvis@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted via
 * https://github.com/jakenjarvis/Android-OrmLiteContentProvider
 */
package com.tojc.ormlite.android.event.listenerset;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.tojc.ormlite.android.event.listener.OnAfterApplyBatchListener;
import com.tojc.ormlite.android.event.listener.OnAfterBulkInsertListener;
import com.tojc.ormlite.android.event.listener.OnBeforeApplyBatchListener;
import com.tojc.ormlite.android.event.listener.OnBeforeBulkInsertListener;
import com.tojc.ormlite.android.event.listener.OnBulkInsertCompletedListener;
import com.tojc.ormlite.android.event.listener.OnBulkInsertListener;
import com.tojc.ormlite.android.event.listener.OnDeleteCompletedListener;
import com.tojc.ormlite.android.event.listener.OnDeleteListener;
import com.tojc.ormlite.android.event.listener.OnInsertCompletedListener;
import com.tojc.ormlite.android.event.listener.OnInsertListener;
import com.tojc.ormlite.android.event.listener.OnQueryCompletedListener;
import com.tojc.ormlite.android.event.listener.OnQueryListener;
import com.tojc.ormlite.android.event.listener.OnUpdateCompletedListener;
import com.tojc.ormlite.android.event.listener.OnUpdateListener;

/**
 * Created by Jaken on 2014/05/05.
 */
public interface DefaultContentProviderAllListenerSet<T extends OrmLiteSqliteOpenHelper> extends
        OnQueryListener<T>,
        OnQueryCompletedListener<T>,
        OnInsertListener<T>,
        OnInsertCompletedListener<T>,
        OnDeleteListener<T>,
        OnDeleteCompletedListener<T>,
        OnUpdateListener<T>,
        OnUpdateCompletedListener<T>,
        OnBeforeBulkInsertListener<T>,
        OnBulkInsertListener<T>,
        OnAfterBulkInsertListener<T>,
        OnBulkInsertCompletedListener<T>,
        OnBeforeApplyBatchListener<T>,
        OnAfterApplyBatchListener<T> {
}
