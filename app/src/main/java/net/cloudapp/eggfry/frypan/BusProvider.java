package net.cloudapp.eggfry.frypan;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by user on 2016-08-18.
 */

public final class BusProvider {
    private static final Bus ourInstance = new Bus(ThreadEnforcer.ANY);

    public static Bus getInstance() {
        return ourInstance;
    }

    private BusProvider() {
    }
}
