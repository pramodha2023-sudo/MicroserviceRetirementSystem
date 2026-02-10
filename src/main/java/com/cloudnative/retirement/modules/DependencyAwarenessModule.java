package com.cloudnative.retirement.modules;

import com.cloudnative.retirement.model.Microservice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dependency Awareness Module
 * Checks whether dependent services exist and whether safe retirement is possible.
 * Maintains a dependency graph and prevents retirement of critical services.
 */
public class DependencyAwarenessModule {
    private static final Logger logger = LoggerFactory.getLogger(DependencyAwarenessModule.class);

    private static final int DEPENDENCY_THRESHOLD = 2; // Critical if this many or more dependents

    private final Map<String, Set<String>> dependencyGraph; // service -> list of dependents

    public DependencyAwarenessModule() {
        this.dependencyGraph = new ConcurrentHashMap<>();
    }

    /**
     * Registers a dependency: serviceA depends on serviceB
     */
    public void registerDependency(String dependentService, String providingService) {
        dependencyGraph.computeIfAbsent(providingService, k -> Collections.synchronizedSet(new HashSet<>()))
                       .add(dependentService);
        logger.debug("Registered dependency: {} depends on {}", dependentService, providingService);
    }

    /**
     * Unregisters a dependency when a dependent service retires
     */
    public void unregisterDependency(String dependentService, String providingService) {
        Set<String> dependents = dependencyGraph.get(providingService);
        if (dependents != null) {
            dependents.remove(dependentService);
        }
        logger.debug("Unregistered dependency: {} no longer depends on {}", dependentService, providingService);
    }

    /**
     * Checks if a service can be safely retired.
     * Safe if: no dependents OR dependents can be notified and rerouted.
     */
    public boolean canSafelyRetire(Microservice service) {
        Set<String> dependents = dependencyGraph.getOrDefault(service.getServiceId(), Collections.emptySet());
        
        // Update service's dependent count for metrics
        service.setDependentServiceCount(dependents.size());

        if (dependents.isEmpty()) {
            logger.info("Service {} has no dependents - safe to retire", service.getServiceId());
            return true;
        }

        if (dependents.size() >= DEPENDENCY_THRESHOLD) {
            logger.warn("Service {} has {} critical dependents - cannot retire safely", 
                       service.getServiceId(), dependents.size());
            return false;
        }

        // Low number of dependents - can notify and gracefully transition
        logger.info("Service {} has {} non-critical dependents - retirement possible with notification", 
                   service.getServiceId(), dependents.size());
        notifyDependents(service.getServiceId(), dependents);
        return true;
    }

    /**
     * Notifies dependent services of upcoming retirement and allows them to prepare.
     */
    private void notifyDependents(String serviceId, Set<String> dependents) {
        logger.info("Notifying {} dependent services of {} retirement", dependents.size(), serviceId);
        for (String dependent : dependents) {
            logger.debug("Notifying dependent service: {}", dependent);
            // In a real system, this would send events/messages to dependent services
            // They could then reroute requests or start using fallback services
        }
    }

    /**
     * Gets all services that depend on the given service.
     */
    public Set<String> getDependents(String serviceId) {
        return new HashSet<>(dependencyGraph.getOrDefault(serviceId, Collections.emptySet()));
    }

    /**
     * Gets dependency graph statistics.
     */
    public String getDependencyStats() {
        int totalServices = dependencyGraph.size();
        int totalDependencies = dependencyGraph.values().stream()
                                               .mapToInt(Set::size)
                                               .sum();
        
        int criticalServices = (int) dependencyGraph.values().stream()
                                                   .filter(deps -> deps.size() >= DEPENDENCY_THRESHOLD)
                                                   .count();

        return String.format("Dependency Stats - Services: %d, Total Dependencies: %d, Critical Services: %d",
                           totalServices, totalDependencies, criticalServices);
    }

    /**
     * Clears all dependencies for a retired service.
     */
    public void clearDependenciesForRetiredService(String serviceId) {
        dependencyGraph.remove(serviceId);
        // Also remove from all dependents
        dependencyGraph.values().forEach(deps -> deps.remove(serviceId));
        logger.info("Cleared all dependencies for retired service {}", serviceId);
    }
}
