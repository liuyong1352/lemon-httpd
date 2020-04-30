package org.lemon.transport;


import java.io.IOException;

public class ReactorGroup {

    private Reactor[] reactors;
    private int current;

    public ReactorGroup(int nReactor) throws IOException {
        reactors = new Reactor[nReactor];
        for (int i = 0; i < nReactor; i++) {
            reactors[i] = new Reactor();
        }
    }

    public Reactor next() {
        return reactors[current++ % reactors.length];
    }

}
