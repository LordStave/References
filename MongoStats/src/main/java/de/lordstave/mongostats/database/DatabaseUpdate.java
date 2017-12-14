package de.lordstave.mongostats.database;

import java.util.LinkedList;
import java.util.List;

public class DatabaseUpdate {

    private final List<Runnable> runnableList;
    private Boolean ready;

    public DatabaseUpdate() {
        this.runnableList = new LinkedList<>();
        this.ready = false;
    }

    public List<Runnable> getRunnableList() {
        return runnableList;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
        if(ready) {
            this.runnableList.forEach(Runnable::run);
        }
    }

    public void executeIfReady(Runnable runnable) {
        if(ready) {
            runnable.run();
            this.runnableList.forEach(Runnable::run);
            this.runnableList.clear();
            return;
        }
        this.runnableList.add(runnable);
    }
}
