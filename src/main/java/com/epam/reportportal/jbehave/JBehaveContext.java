/*
 * Copyright 2016 EPAM Systems
 *
 *
 * This file is part of EPAM Report Portal.
 * https://github.com/reportportal/agent-java-jbehave
 *
 * Report Portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Report Portal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Report Portal.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.epam.reportportal.jbehave;

import com.epam.reportportal.listeners.Statuses;
import io.reactivex.Maybe;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.Meta;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * JBehave test execution context
 *
 * @author Andrei Varabyeu
 */
public class JBehaveContext {

    private static ThreadLocal<Story> currentStory = new ThreadLocal<JBehaveContext.Story>() {
        @Override
        protected Story initialValue() {
            return new Story();
        }
    };

    private static Deque<Maybe<String>> itemsCache = new LinkedList<>();
    private static Deque<Maybe<String>> stepsCache = new LinkedList<>();

    public static Story getCurrentStory() {
        return currentStory.get();
    }

    public static void setCurrentStory(Story story) {
        currentStory.set(story);
    }

    public static Deque<Maybe<String>> getItemsCache() {
        Deque<Maybe<String>> merged = new LinkedList<>(stepsCache);
        merged.addAll(itemsCache);
        return merged;
    }

    public static class Story {

        private Maybe<String> currentStoryId;

        private Maybe<String> currentScenario;

        private Maybe<String> currentStep;
        private String currentStepStatus = Statuses.PASSED;

        private Examples examples;

        private Meta scenarioMeta;

        private Meta storyMeta;

        private Story parent;

        public boolean hasParent() {
            return null != parent;
        }

        public Story getParent() {
            return parent;
        }

        public void setParent(Story parent) {
            this.parent = parent;
        }

        /**
         * @param currentStep the currentStep to set
         */
        public void setCurrentStep(Maybe<String> currentStep) {
            if (null != currentStep) {
                stepsCache.push(currentStep);
            } else {
                stepsCache.remove(this.currentStep);
            }
            this.currentStep = stepsCache.peek();
        }

        public void setCurrentStoryId(Maybe<String> currentStoryId) {
            if (null != currentStoryId) {
                itemsCache.push(currentStoryId);
            } else {
                itemsCache.remove(this.currentStoryId);
            }
            this.currentStoryId = currentStoryId;
        }

        public Maybe<String> getCurrentStoryId() {
            return currentStoryId;
        }

        /**
         * @return the currentScenario
         */
        public Maybe<String> getCurrentScenario() {
            return currentScenario;
        }

        /**
         * @param currentScenario the currentScenario to set
         */
        public void setCurrentScenario(Maybe<String> currentScenario) {
            if (null != currentScenario) {
                itemsCache.push(currentScenario);
            } else {
                itemsCache.remove(this.currentScenario);
            }
            this.currentScenario = currentScenario;
        }

        /**
         * @return the currentStep
         */
        public Maybe<String> getCurrentStep() {
            return currentStep;
        }

        public String getCurrentStepStatus() {
            return currentStepStatus;
        }

        public void setCurrentStepStatus(String currentStepStatus) {
            this.currentStepStatus = currentStepStatus;
        }

        public void setExamples(Examples examples) {
            this.examples = examples;
        }

        public boolean hasExamples() {
            return null != examples && examples.getExamplesTable().getRowCount() > 0;
        }

        public Examples getExamples() {
            return examples;
        }

        public void setScenarioMeta(Meta scenarioMeta) {
            this.scenarioMeta = scenarioMeta;
        }

        public Meta getScenarioMeta() {
            return scenarioMeta;
        }

        public void setStoryMeta(Meta storyMeta) {
            this.storyMeta = storyMeta;
        }

        public Meta getStoryMeta() {
            return storyMeta;
        }

    }

    public static class Examples {
        private final ExamplesTable examplesTable;

        private int currentExample;

        private final List<String> steps;

        Examples(List<String> steps, ExamplesTable examplesTable) {
            this.examplesTable = examplesTable;
            this.currentExample = -1;
            this.steps = steps;
        }

        public ExamplesTable getExamplesTable() {
            return examplesTable;
        }

        public Map<String, String> getCurrentExampleParams() {
            return examplesTable.getRow(currentExample);
        }

        public int getCurrentExample() {
            return currentExample;
        }

        public List<String> getSteps() {
            return steps;
        }

        public boolean hasStep(String step) {
            return steps.contains(step);
        }

        public void setCurrentExample(int currentExample) {
            this.currentExample = currentExample;
        }

    }

}
