package com.rs.detector.service.editing.exceptions;

public class PagesMightNotHaveBeenGeneratedException extends Exception {
    public PagesMightNotHaveBeenGeneratedException() {
        super("The pages for the given edition might not have been generated so far!");
    }
}
