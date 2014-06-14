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
package com.tojc.ormlite.android.framework;

import android.content.ContentProvider;
import android.content.UriMatcher;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.tojc.ormlite.android.OrmLiteBaseContentProvider;
import com.tojc.ormlite.android.annotation.info.ContentMimeTypeVndInfo;
import com.tojc.ormlite.android.annotation.info.ContentUriInfo;
import com.tojc.ormlite.android.framework.MimeTypeVnd.SubType;
import com.tojc.ormlite.android.framework.fragment.ContentProviderFragmentInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Before ContentProvider instance is created, you need to register a pattern to UriMatcher.
 * MatcherController will help the registration process.
 *
 * @author Jaken
 */
public class MatcherController {
    private boolean initialized = false;

    private ContentProvider contentProvider = null;

    private UriMatcher matcher = null;
    private Map<Class<?>, TableInfo> tables = null;
    private List<MatcherPattern> matcherPatterns = null;
    private Map<String, ContentProviderFragmentInterface<?, ?>> contentProviderFragments;

    private TableInfo lastAddTableInfo = null;
    // NOTE: LIFO stack list. Support of Queue and Deque from API-Lv9...
    private LinkedList<ContentProviderFragmentInterface<?, ?>> stackFragments = null;

    /**
     * @see MatcherController#MatcherController(android.content.ContentProvider)
     * @deprecated If you are using the OrmLiteContentProviderFragment, you can not use this constructor. This is abolish in the near future.
     * Please refer to the MatcherController#MatcherController(android.content.ContentProvider).
     */
    @Deprecated
    public MatcherController() {
        this(null);
    }

    /**
     * @param contentProvider
     * @since 2.0.0
     */
    public MatcherController(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;

        this.matcher = new UriMatcher(UriMatcher.NO_MATCH);
        this.tables = new HashMap<Class<?>, TableInfo>();
        this.matcherPatterns = new ArrayList<MatcherPattern>();
        this.contentProviderFragments = new LinkedHashMap<String, ContentProviderFragmentInterface<?, ?>>();

        this.lastAddTableInfo = null;
        this.stackFragments = new LinkedList<ContentProviderFragmentInterface<?, ?>>();
    }

    /**
     * Register a class for table.
     *
     * @param tableClassType Register a class for table.
     * @return Instance of the MatcherController class.
     */
    public MatcherController add(Class<?> tableClassType) {
        this.addTableClass(tableClassType);
        return this;
    }

    /**
     * Register a class for table. And registers a pattern for UriMatcher.
     *
     * @param tableClassType Register a class for table.
     * @param subType        Contents to be registered in the pattern, specify single or multiple. This is used
     *                       in the MIME types. * ITEM : If the URI pattern is for a single row :
     *                       vnd.android.cursor.item/ * DIRECTORY : If the URI pattern is for more than one row
     *                       : vnd.android.cursor.dir/
     * @param pattern        registers a pattern for UriMatcher. Note: Must not contain the name of path here.
     *                       ex) content://com.example.app.provider/table1 : pattern = ""
     *                       content://com.example.app.provider/table1/# : pattern = "#"
     *                       content://com.example.app.provider/table1/dataset2 : pattern = "dataset2"
     * @param patternCode    UriMatcher code is returned
     * @return Instance of the MatcherController class.
     */
    public MatcherController add(Class<?> tableClassType, SubType subType, String pattern, int patternCode) {
        this.addTableClass(tableClassType);
        MatcherPattern result = this.createMatcherPattern(subType, pattern, patternCode);
        this.add(result);
        return this;
    }

    /**
     * Registers a pattern for UriMatcher. It refer to the class that was last registered from add
     * method.
     *
     * @param subType     Contents to be registered in the pattern, specify single or multiple. This is used
     *                    in the MIME types. * ITEM : If the URI pattern is for a single row :
     *                    vnd.android.cursor.item/ * DIRECTORY : If the URI pattern is for more than one row
     *                    : vnd.android.cursor.dir/
     * @param pattern     registers a pattern for UriMatcher. Note: Must not contain the name of path here.
     *                    ex) content://com.example.app.provider/table1 : pattern = ""
     *                    content://com.example.app.provider/table1/# : pattern = "#"
     *                    content://com.example.app.provider/table1/dataset2 : pattern = "dataset2"
     * @param patternCode UriMatcher code is returned
     * @return Instance of the MatcherController class.
     */
    public MatcherController add(SubType subType, String pattern, int patternCode) {
        MatcherPattern result = this.createMatcherPattern(subType, pattern, patternCode);
        this.add(result);
        return this;
    }

    /**
     * Registers a pattern for UriMatcher. To register you have to create an instance of
     * MatcherPattern.
     *
     * @param matcherPattern register MatcherPattern.
     * @return Instance of the MatcherController class.
     */
    public MatcherController add(MatcherPattern matcherPattern) {
        int patternCode = matcherPattern.getPatternCode();

        if (this.lastAddTableInfo == null) {
            throw new IllegalStateException("There is a problem with the order of function call.");
        }

        if (findMatcherPattern(patternCode) != null) {
            throw new IllegalArgumentException("patternCode has been specified already exists.");
        }

        if (this.stackFragments.size() >= 1) {
            ContentProviderFragmentInterface<?, ?> fragment = this.stackFragments.getLast();
            matcherPattern.setParentContentProviderFragment(fragment);
        } else {
            matcherPattern.setParentContentProviderFragment(null);
        }
        this.matcherPatterns.add(matcherPattern);
        return this;
    }

    /**
     * Add the ContentProviderFragment to receive the event. It corresponds to the definition nested.
     * Class Object to be added must implement OrmLiteContentProviderFragment.
     *
     * @param clazzFragment
     * @return
     */
    public <V extends ContentProviderFragmentInterface<? extends OrmLiteBaseContentProvider<?>, ? extends OrmLiteSqliteOpenHelper>> MatcherController addFragment(Class<V> clazzFragment) {
        ContentProviderFragmentInterface<?, ?> instance = this.createContentProviderFragment(clazzFragment);
        this.addFragment(instance);
        return this;
    }

    /**
     * Add the ContentProviderFragment to receive the event. It corresponds to the definition nested.
     * Object instance to be added must implement OrmLiteContentProviderFragment.
     *
     * @param fragment
     * @return
     * @since 2.0.0
     */
    public MatcherController addFragment(ContentProviderFragmentInterface<?, ?> fragment) {
        if (fragment == null) {
            throw new IllegalArgumentException("fragment is null.");
        }
        if (this.contentProvider == null) {
            // @see MatcherController#MatcherController(android.content.ContentProvider)
            throw new IllegalStateException("contentProvider is null. The constructor for MatcherController, please use the MatcherController(ContentProvider).");
        }

        String key = fragment.getKeyName();
        if ((key == null) || (key.length() <= 0)) {
            throw new IllegalStateException("key is invalid. Please check the return value of the getKeyName().");
        }

        if (this.contentProviderFragments.containsKey(key)) {
            throw new IllegalArgumentException("key has been specified already exists. Please check the return value of the getKeyName().");
        }
        this.contentProviderFragments.put(key, fragment);

        this.stackFragments.addLast(fragment);
        fragment.onFragmentInitialize(this);
        this.stackFragments.removeLast();
        return this;
    }

    /**
     * Set the DefaultContentUri. If you did not use the DefaultContentUri annotation, you must call
     * this method.
     *
     * @param authority
     * @param path
     * @return Instance of the MatcherController class.
     * @see com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri
     */
    public MatcherController setDefaultContentUri(String authority, String path) {
        if (this.lastAddTableInfo == null) {
            throw new IllegalStateException("There is a problem with the order of function call.");
        }
        this.lastAddTableInfo.setDefaultContentUriInfo(new ContentUriInfo(authority, path));
        return this;
    }

    /**
     * Set the DefaultContentMimeTypeVnd. If you did not use the DefaultContentMimeTypeVnd
     * annotation, you must call this method.
     *
     * @param name
     * @param type
     * @return Instance of the MatcherController class.
     * @see com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd
     */
    public MatcherController setDefaultContentMimeTypeVnd(String name, String type) {
        if (this.lastAddTableInfo == null) {
            throw new IllegalStateException("There is a problem with the order of function call.");
        }
        this.lastAddTableInfo.setDefaultContentMimeTypeVndInfo(new ContentMimeTypeVndInfo(name, type));
        return this;
    }

    /**
     * initialized with the contents that are registered by the add method. This method checks the
     * registration details.
     *
     * @return Instance of the MatcherController class.
     */
    public MatcherController initialize() {
        this.lastAddTableInfo = null;

        if (this.stackFragments.size() >= 1) {
            throw new IllegalStateException("There is a problem with the management of ContentProviderFragment.");
        }

        for (Map.Entry<Class<?>, TableInfo> entry : this.tables.entrySet()) {
            entry.getValue().isValid(true);
        }

        for (MatcherPattern entry : matcherPatterns) {
            entry.isValid(true);
            this.matcher.addURI(entry.getTableInfo().getDefaultContentUriInfo().getAuthority(), entry.getPathAndPatternString(), entry.getPatternCode());
            entry.initialize();
        }

        this.initialized = true;
        return this;
    }

    /**
     * This will search the MatcherPattern that are registered based on the return code UriMatcher.
     *
     * @param patternCode UriMatcher code is returned
     * @return Instance of the MatcherPattern class. if no match is found will return null.
     */
    public MatcherPattern findMatcherPattern(int patternCode) {
        MatcherPattern result = null;
        for (MatcherPattern entry : this.matcherPatterns) {
            if (entry.getPatternCode() == patternCode) {
                result = entry;
                break;
            }
        }
        return result;
    }

    private TableInfo addTableClass(Class<?> tableClassType) {
        TableInfo result = null;
        if (this.tables.containsKey(tableClassType)) {
            result = this.tables.get(tableClassType);
        } else {
            result = new TableInfo(tableClassType);
            this.tables.put(tableClassType, result);
        }

        // referenced in createMatcherPattern
        this.lastAddTableInfo = result;
        return result;
    }

    private MatcherPattern createMatcherPattern(SubType subType, String pattern, int patternCode) {
        if (this.lastAddTableInfo == null) {
            throw new IllegalStateException("There is a problem with the order of function call.");
        }
        return new MatcherPattern(this.lastAddTableInfo, subType, pattern, patternCode);
    }

    public boolean hasPreinitialized() {
        return this.initialized;
    }

    /**
     * @return Return an instance of the UriMatcher.
     */
    public UriMatcher getUriMatcher() {
        if (!this.initialized) {
            throw new IllegalStateException("Controller has not been initialized.");
        }
        return this.matcher;
    }

    /**
     * @return Return a map of tables that have been registered class.
     */
    public Map<Class<?>, TableInfo> getTables() {
        if (!this.initialized) {
            throw new IllegalStateException("Controller has not been initialized.");
        }
        return this.tables;
    }

    /**
     * @return Return an instance of the UriMatcher.
     */
    public List<MatcherPattern> getMatcherPatterns() {
        if (!this.initialized) {
            throw new IllegalStateException("Controller has not been initialized.");
        }
        return this.matcherPatterns;
    }

    /**
     * Get the content provider instance.
     *
     * @return
     * @since 2.0.0
     */
    public ContentProvider getContentProvider() {
        return contentProvider;
    }

    /**
     * ContentProviderFragment that has been added to this MatcherController.
     *
     * @return contentProviderFragments
     * @since 2.0.0
     */
    public Map<String, ContentProviderFragmentInterface<?, ?>> getContentProviderFragments() {
        return this.contentProviderFragments;
    }

    private <V extends ContentProviderFragmentInterface<? extends OrmLiteBaseContentProvider<?>, ? extends OrmLiteSqliteOpenHelper>> V createContentProviderFragment(Class<V> clazzFragment) {
        V instance;
        Exception innerException = null;
        try {
            instance = clazzFragment.newInstance();
        } catch (InstantiationException e) {
            instance = null;
            innerException = e;
        } catch (IllegalAccessException e) {
            instance = null;
            innerException = e;
        }
        if (innerException != null) {
            throw new IllegalStateException("Failed to create an instance of ContentProviderFragment.", innerException);
        }
        return instance;
    }
}
