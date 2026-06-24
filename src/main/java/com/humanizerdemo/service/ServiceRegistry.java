package com.humanizerdemo.service;

// Simple locator — could be replaced with DI if the project grows
public class ServiceRegistry {

    private final TextProcessor text;
    private final DateTimeProcessor dateTime;

    public ServiceRegistry() {
        this.text     = new TextProcessor();
        this.dateTime = new DateTimeProcessor();
    }

    public TextProcessor getTextProcessor() { return text; }
    public DateTimeProcessor getDateTimeProcessor() { return dateTime; }
}
