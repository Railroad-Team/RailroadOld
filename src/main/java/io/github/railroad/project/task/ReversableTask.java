package io.github.railroad.project.task;

import java.util.Deque;
import java.util.LinkedList;

public class ReversableTask extends Task {
    private final Deque<Step> undoSteps = new LinkedList<>();

    public ReversableTask(String name) {
        super(name);
    }

    /**
     * Start undoing from the current step until there are no steps left.
     *
     * @param currentStep The step to start undoing from.
     */
    public void undo(Step currentStep) {
        boolean reachedCurrentStep = false;
        while(!undoSteps.isEmpty()) {
            Step step = undoSteps.pollLast();
            if (step == currentStep) {
                reachedCurrentStep = true;
            }

            if(reachedCurrentStep)
                step.undo();
        }
    }

    @Override
    public void addStep(Step step) {
        super.addStep(step);
        this.undoSteps.add(step);
    }
}
