package com.humanizerdemo.service;

public class ServiceRegistry {

    private final TextProcessor textProcessor;
    private final DateTimeProcessor dateTimeProcessor;

    public ServiceRegistry() {
        this.textProcessor = new TextProcessor();
        this.dateTimeProcessor = new DateTimeProcessor();
    }

    public TextProcessor getTextProcessor() {
        return textProcessor;
    }

    public DateTimeProcessor getDateTimeProcessor() {
        return dateTimeProcessor;
    }
}
