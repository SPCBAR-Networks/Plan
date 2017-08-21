package main.java.com.djrapitops.plan.queue;

import main.java.com.djrapitops.plan.Plan;

/**
 * Abstract representation of a queue setup.
 *
 * @param <T> Object this queue consumes.
 * @author Rsl1122
 */
public abstract class Setup<T> {

    private final Consumer<T>[] consumers;

    /**
     * Constructor, defines consumers.
     *
     * @param consumers Consumers for the new threads.
     */
    @SafeVarargs
    public Setup(Consumer<T>... consumers) {
        this.consumers = consumers;
    }

    public void go() {
        for (Consumer<T> consumer : consumers) {
            Plan.getInstance().getRunnableFactory().createNew(consumer).runTaskAsynchronously();
        }
    }

    public void stop() {
        for (Consumer<T> consumer : consumers) {
            consumer.stop();
        }
    }
}