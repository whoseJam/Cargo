package Cargo.controller;

import Cargo.app.DataMaker;
import Cargo.bean.CargoRepository;
import Cargo.domain.Car;
import Cargo.domain.Location;
import Cargo.domain.Recipient;
import Cargo.domain.Schedule;
import Cargo.domain.Storage;
import javafx.scene.canvas.Canvas;
import org.apache.naming.EjbRef;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.xml.transform.sax.SAXResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Component
class SolveSubmission {
    public Schedule problem;
    public String token;
}

@Component
class GetSubmission {
    public String problemId;
    public String token;
}

@Component
class Response {
    public Boolean success;
    public String message;

    public Response() {
        success = false;
        message = "empty";
    }

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}

@Controller
@RequestMapping("/cargo")
public class CargoController {
    @Autowired
    private CargoRepository cargoRepository;
    @Autowired
    private SolverManager<Schedule, UUID> solverManager;

    @PostMapping("/solve/async")
    @ResponseBody
    public Response solveAsync(@RequestBody SolveSubmission submission) {
        if (!isValidToken(submission.token)) return new Response(false, "invalid token");
        try {
            Schedule problem = submission.problem;
            problem.id = UUID.randomUUID();
            cargoRepository.save(problem);
            solverManager.solveAndListen(problem.id, cargoRepository::findById, cargoRepository::save);
            return new Response(true, problem.id.toString());
        } catch (Exception e) {
            return new Response(false, e.toString());
        }
    }

    @PostMapping("/solve/stop")
    @ResponseBody
    public Response stopSolving(@RequestBody GetSubmission submission) {
        if (!isValidToken(submission.token)) return new Response(false, "invalid token");
        try {
            UUID id = UUID.fromString(submission.problemId);
            solverManager.terminateEarly(id);
            return new Response(true, "terminate successfully");
        } catch (Exception e) {
            return new Response(false, e.toString());
        }
    }

    @PostMapping("/get/solverStatus")
    @ResponseBody
    public Response getSolverStatus(@RequestBody GetSubmission submission) {
        if (!isValidToken(submission.token)) return new Response(false, "invalid token");
        UUID id = UUID.fromString(submission.problemId);
        SolverStatus status = solverManager.getSolverStatus(id);
        return new Response(true, status.toString());
    }

    @PostMapping("/get/solution")
    @ResponseBody
    public Response getAns(@RequestBody GetSubmission submission) {
        if (!isValidToken(submission.token)) return new Response(false, "invalid token");
        UUID id = UUID.fromString(submission.problemId);
        Schedule solution = cargoRepository.findById(id);
        if (solution == null) return new Response(false, "problem not found");
        return new Response(true, solution.toJson().toString());
    }

    private boolean isValidToken(String token) {
        return token.equals("FKkdfmiweo9fksF");
    }
}
