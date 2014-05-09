package com.tojc.ormlite.android.framework.event.multieventobject;

import android.net.Uri;

import com.tojc.ormlite.android.framework.MatcherPattern;
import com.tojc.ormlite.android.framework.OperationParameters;
import com.tojc.ormlite.android.framework.Parameter;
import com.tojc.ormlite.android.framework.event.multieventbase.MultiEventObjectBase;

/**
 * Created by Jaken on 2014/05/06.
 */
public class OnDeleteCompletedMultiEventObject extends MultiEventObjectBase {
    private final int result;
    private final Uri uri;
    private final MatcherPattern matcherPattern;
    private final Parameter parameter;

    public OnDeleteCompletedMultiEventObject(Object source, int result, Uri uri, MatcherPattern matcherPattern, Parameter parameter) {
        super(source);
        this.result = result;
        this.uri = uri;
        this.matcherPattern = matcherPattern;
        this.parameter = parameter;
    }

    public int getResult() {
        return this.result;
    }

    public Uri getUri() {
        return this.uri;
    }

    public MatcherPattern getMatcherPattern() {
        return this.matcherPattern;
    }

    public OperationParameters.DeleteParameters getParameter() {
        return this.parameter;
    }
}
