package Cargo.controller;

import Cargo.app.DataMaker;
import Cargo.bean.CargoRepository;
import Cargo.domain.Car;
import Cargo.domain.Location;
import Cargo.domain.Recipient;
import Cargo.domain.Schedule;
import Cargo.domain.Storage;
import javafx.scene.canvas.Canvas;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping("/Cargo")
public class CargoController {
    @Autowired
    private CargoRepository cargoRepository;
    @Autowired
    private SolverManager<Schedule, UUID> solverManager;
    @Autowired
    private SolutionManager<Schedule, HardMediumSoftLongScore> solutionManager;

    @CrossOrigin({"http://127.0.0.1:5500"})
    @RequestMapping("/hello")
    @ResponseBody String hello() {
        return "hello world";
    }

    @CrossOrigin({"http://127.0.0.1:5500"})
    @RequestMapping("/solve")
    @ResponseBody
    public String solve(@RequestBody Schedule problem) {
        problem.id = UUID.randomUUID();
        SolverConfig solverConfig = SolverConfig.createFromXmlResource("Cargo/cargo.xml");
        SolverManager<Schedule, UUID> solverManager = SolverManager.create(solverConfig, new SolverManagerConfig());
        cargoRepository.save(problem);
        try {
            solverManager.solveAndListen(problem.id, cargoRepository::findById, cargoRepository::save);
            return problem.id.toString();
        } catch (Exception e) {
            return e.toString();
        }
    }

    @CrossOrigin({"http://127.0.0.1:5500"})
    @RequestMapping("/getAns")
    @ResponseBody
    public String getAns(@RequestBody String data) {
        UUID id = UUID.fromString(data);
        Schedule solution = cargoRepository.findById(id);
        return solution.toJson().toString();
    }
}
