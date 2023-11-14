package Cargo.bean;

import Cargo.domain.Schedule;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

public class SolverManagerBean {
    @Bean
    public SolverManager<Schedule, UUID> getSolverManager() {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource("Cargo/cargo.xml");
        SolverManager<Schedule, UUID> solverManager = SolverManager.create(solverConfig, new SolverManagerConfig());
        return solverManager;
    }
}
