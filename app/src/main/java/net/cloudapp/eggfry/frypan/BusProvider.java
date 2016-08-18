package net.cloudapp.eggfry.frypan;

import com.squareup.otto.Bus;

/**
 * Created by user on 2016-08-18.
 */
public final class BusProvider {
    private static final Bus ourInstance = new Bus();

    public static Bus getInstance() {
        return ourInstance;
    }

    private BusProvider() {
    }
}
