package at.geise.test.springboot4test.config;

import at.geise.test.springboot4test.domain.Task;
import at.geise.test.springboot4test.service.ActivityLogService;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TaskChangeListener {

    @Autowired(required = false)
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private Environment environment;

    private static volatile boolean skipLogging = false;

    public static void setSkipLogging(boolean skip) {
        skipLogging = skip;
    }

    @PrePersist
    public void prePersist(Task task) {
        // Skip logging if explicitly disabled (e.g., during startup data loading)
        if (skipLogging) {
            return;
        }

        // Skip activity logging if in test profile
        if (isTestProfile()) {
            return;
        }

        // Log task creation - get service lazily to avoid circular dependency
        if (applicationContext != null) {
            try {
                ActivityLogService activityLogService = applicationContext.getBean(ActivityLogService.class);
                activityLogService.logTaskCreated(task, "System");
            } catch (Exception e) {
                // Silently ignore if service is not available (e.g., during tests or initialization)
            }
        }
    }

    @PostUpdate
    public void postUpdate(Task task) {
        // This method is called after the update, but we need to track what changed
        // We'll handle this in the service layer instead for better control
    }

    private boolean isTestProfile() {
        if (environment == null) {
            return false;
        }

        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("test".equals(profile)) {
                return true;
            }
        }
        return false;
    }
}

