package com.erge.plugin;

import com.erge.util.Logger;

import org.gradle.BuildListener;
import org.gradle.BuildResult;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionListener;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.tasks.TaskState;

import java.util.PriorityQueue;


class TimeCostListenPlugin implements Plugin<Project> {
    private static PriorityQueue<TaskCost> pq = new PriorityQueue<>();

    @Override
    public void apply(Project project) {
        pq.clear();
        project.getGradle().addListener(new InternalTaskExecutionListener());
        project.getGradle().addBuildListener(new InternalBuildListener());
    }

    static class TaskCost implements Comparable<TaskCost> {
        Task task;
        float cost;

        TaskCost(Task task, float cost) {
            this.task = task;
            this.cost = cost;
        }

        @Override
        public int compareTo(TaskCost o) {
            return -Double.compare(this.cost, o.cost);
        }
    }

    static class InternalTaskExecutionListener implements TaskExecutionListener {
        long begin;

        @Override
        public void beforeExecute(Task task) {
            begin = System.currentTimeMillis();
        }

        @Override
        public void afterExecute(Task task, TaskState state) {
            float time = (System.currentTimeMillis() - begin) / 1000f;
            Logger.i("\t\t\t\t%.3fs", time);
            pq.add(new TaskCost(task, time));
        }
    }

    static class InternalBuildListener implements BuildListener {

        @Override
        public void buildStarted(Gradle gradle) {

        }

        @Override
        public void settingsEvaluated(Settings settings) {

        }

        @Override
        public void projectsLoaded(Gradle gradle) {

        }

        @Override
        public void projectsEvaluated(Gradle gradle) {

        }

        @Override
        public void buildFinished(BuildResult buildResult) {
            int i = 0;
            int N = 10;
            float total = 0;
            Logger.i("============================执行任务%d个,耗时Top%d============================", pq.size(), N);
            while (!pq.isEmpty() && i < N) {
                TaskCost poll = pq.poll();
                Task task = poll.task;
                Logger.i("%s:%s -> %.3fs", task.getProject().getPath(), task.getName(), poll.cost);
                i++;
                total += poll.cost;
            }
            Logger.i("Top%d耗时:%.3fs", N, total);
            Logger.i("==========================================================================");
        }
    }
}