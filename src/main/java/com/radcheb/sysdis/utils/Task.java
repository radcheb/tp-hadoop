package com.radcheb.sysdis.utils;

public class Task {
    private final int index;
    private final String hostname;
    private final String inputUri;
    private final String outputUri;

    public Task(int index, String hostname, String inputUri, String outputUri) {
        this.index = index;
        this.hostname = hostname;
        this.inputUri = inputUri;
        this.outputUri = outputUri;
    }

    public int getIndex() {
        return index;
    }

    public String getHostname() {
        return hostname;
    }

    public String getInputUri() {
        return inputUri;
    }

    public String getOutputUri() {
        return outputUri;
    }

    public static class TaskBuilder {

        private final int index;
        private String hostname = null;
        private String inputUri = null;
        private String outputUri = null;

        public TaskBuilder(int index) {
            this.index = index;
        }

        public TaskBuilder setHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public TaskBuilder setInputUri(String inputUri) {
            this.inputUri = inputUri;
            return this;
        }

        public TaskBuilder setOutputUri(String outputUri) {
            this.outputUri = outputUri;
            return this;
        }

        public Task build() {
            return new Task(index, hostname, inputUri, outputUri);
        }
    }
}
