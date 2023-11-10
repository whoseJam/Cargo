package Cargo.controller;

import Cargo.app.DataMaker;
import Cargo.domain.Car;
import Cargo.domain.Location;
import Cargo.domain.Recipient;
import Cargo.domain.Schedule;
import Cargo.domain.Storage;
import javafx.scene.canvas.Canvas;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/Cargo")
public class CargoController {
    private Map<Long, Schedule> problems = new HashMap<>();

    private void putProblemById(long id, Schedule problem) {
        problems.put(id, problem);
    }

    private Schedule getProblemById(long id) {
        return problems.get(id);
    }

    @CrossOrigin({"http://127.0.0.1:5500"})
    @RequestMapping("/hello")
    @ResponseBody String hello() {
        return "hello world";
    }

    @CrossOrigin({"http://127.0.0.1:5500"})
    @RequestMapping("/solve")
    @ResponseBody
    public String solve(@RequestBody Schedule problem) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource("Cargo/cargo.xml");
        SolverManager<Schedule, Long> solverManager = SolverManager.create(solverConfig, new SolverManagerConfig());
        try {
            SolverJob<Schedule, Long> job = solverManager.solve(1L, problem);
            Schedule solution = job.getFinalBestSolution();
            return solution.toJson().toString();
        } catch (Exception e) {
            return e.toString();
        }
    }
}
