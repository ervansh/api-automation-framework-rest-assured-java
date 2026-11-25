package com.ervansh.apiautomation.listeners;

import org.testng.IExecutionListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.time.Instant;
import java.util.logging.Logger;

/** Lifecycle reporting. Avoids logging request bodies, credentials, or bearer tokens. */
public final class TestExecutionListener implements ITestListener, IExecutionListener {
    private static final Logger LOGGER = Logger.getLogger(TestExecutionListener.class.getName());

    @Override
    public void onExecutionStart() {
        LOGGER.info(() -> "API test execution started at " + Instant.now());
    }

    @Override
    public void onExecutionFinish() {
        LOGGER.info(() -> "API test execution finished at " + Instant.now());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOGGER.info(() -> "PASS " + qualifiedName(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Throwable failure = result.getThrowable();
        LOGGER.severe(() -> "FAIL " + qualifiedName(result) + " - "
                + (failure == null ? "No exception details" : failure.getClass().getSimpleName()
                + ": " + failure.getMessage()));
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOGGER.warning(() -> "SKIP " + qualifiedName(result));
    }

    private String qualifiedName(ITestResult result) {
        return result.getTestClass().getName() + "#" + result.getMethod().getMethodName();
    }
}
