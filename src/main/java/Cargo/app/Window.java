package Cargo.app;

import Cargo.domain.Car;
import Cargo.domain.Location;
import Cargo.domain.Recipient;
import Cargo.domain.Schedule;
import Cargo.domain.Storage;
import Cargo.score.ScoreCalculator;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Window extends Application {
    private SolverManager<Schedule, Long> solverManager;
    private Map<Long, Schedule> problems = new HashMap<>();
    private Canvas canvas;

    private void putProblemById(long id, Schedule problem) {
        problems.put(id, problem);
    }

    private Schedule getProblemById(long id) {
        return problems.get(id);
    }

    private void update(Schedule schedule) {
        HardMediumSoftLongScore score = new ScoreCalculator().calculateScore(schedule);
        new Painter().paint(canvas, schedule);
        System.out.println(schedule.toJson());
        System.out.println(score);
    }

    private void solve() {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource("Cargo/cargo.xml");
        SolverManager<Schedule, Long> solverManager = SolverManager.create(
            solverConfig, new SolverManagerConfig());

        try {
            // Schedule problem = CSVInput.input("./src/testdata/testdata.csv");
            Schedule problem = new DataMaker().data(200, 4, 5, 500);
            putProblemById(1L, problem);
            solverManager.solveAndListen(1L, this::getProblemById, this::update);
        } catch (Exception e) {
            System.out.println("Error : " + e);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Cargo");
        Group root = new Group();

        primaryStage.setWidth(1200);
        primaryStage.setHeight(600);

        canvas = new Canvas(1200, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.GREY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root, 1200, 600));
        primaryStage.show();

        solve();
    }
}
