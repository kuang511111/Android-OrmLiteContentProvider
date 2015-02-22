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
package com.tojc.ormlite.android.framework.transaction.controller;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Jaken on 2014/05/25.
 * <p/>
 * NOTE: This interface has the potential to change the interface in the future.
 *
 * @since 2.0.0
 */
public interface TransactionGeneralControllerInterface extends TransactionControllerInterfaceBase {
    <T> T transaction(SQLiteDatabase db, final OnTransactionListener<T> listener);

    interface OnTransactionListener<T> {
        T onTransaction();

        void onAfterTransactionSuccessful(T result);
    }
}