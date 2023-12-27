package ac.uk.soton.ecs.projectalloc.evaluator.metric;

public abstract class Metric {

    public abstract String getName();

    public abstract int getPositiveCount();

    public abstract double getPositivePercent();

}
