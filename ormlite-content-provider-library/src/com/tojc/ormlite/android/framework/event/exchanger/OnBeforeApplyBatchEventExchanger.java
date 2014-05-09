package com.tojc.ormlite.android.framework.event.exchanger;

import com.tojc.ormlite.android.framework.event.EventExchangerBase;
import com.tojc.ormlite.android.framework.event.listener.OnBeforeApplyBatchListener;
import com.tojc.ormlite.android.framework.event.listenerbase.ContentProviderEventListenerInterfaceBase;
import com.tojc.ormlite.android.framework.event.multieventlistener.OnBeforeApplyBatchMultiEventListener;
import com.tojc.ormlite.android.framework.event.multieventobject.OnBeforeApplyBatchMultiEventObject;

/**
 * Created by Jaken on 2014/05/07.
 */
public class OnBeforeApplyBatchEventExchanger extends EventExchangerBase implements OnBeforeApplyBatchMultiEventListener {
    public OnBeforeApplyBatchEventExchanger(ContentProviderEventListenerInterfaceBase forwarding) {
        super(forwarding);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBeforeApplyBatch(OnBeforeApplyBatchMultiEventObject e) {
        OnBeforeApplyBatchListener listener = (OnBeforeApplyBatchListener) this.getForwarding();
        listener.onBeforeApplyBatch(e.getHelper(), e.getSQLiteDatabase(), e.getOperations());
    }
}
