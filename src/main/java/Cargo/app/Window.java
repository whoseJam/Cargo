package Cargo.app;

import Cargo.domain.Car;
import Cargo.domain.Location;
import Cargo.domain.Recipient;
import Cargo.domain.Schedule;
import Cargo.domain.Storage;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
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

    private void reset(Canvas canvas, Color color) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void paint(Schedule schedule) {
        new Painter().paint(canvas, schedule);
        System.out.println(schedule);
    }

    private void solve() {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource("Cargo/cargo.xml");
        SolverManager<Schedule, Long> solverManager = SolverManager.create(
            solverConfig, new SolverManagerConfig());

//        List<Location> locations = new ArrayList<>();
//        List<Recipient> recipients = new ArrayList<>();
//        List<Storage> storages = new ArrayList<>();
//        locations.add(new Location("北京", 100, 70));
//        locations.add(new Location("上海", 80, 200));
//        locations.add(new Location("天津", 200, 130));
//        locations.add(new Location("深圳", 200, 210));
//        locations.add(new Location("重庆物流点", 50, 50));
//        locations.add(new Location("云南物流点", 200, 150));
//        storages.add(new Storage(50, locations.get(0)));
//        storages.add(new Storage(50, locations.get(1)));
//        storages.add(new Storage(50, locations.get(2)));
//        storages.add(new Storage(50, locations.get(3)));
//        for (int i = 1; i <= 5; i++) recipients.add(new Recipient(10, locations.get(4)));
//        for (int i = 1; i <= 5; i++) recipients.add(new Recipient(10, locations.get(5)));
//
//        List<Car> cars = new ArrayList<>();
//        cars.add(new Car(1, 20, locations.get(0)));
//        cars.add(new Car(2, 20, locations.get(3)));
//        cars.get(0).setColor(Color.TEAL);
//        cars.get(1).setColor(Color.LAVENDER);

//        Schedule problem = new Schedule(cars, recipients, storages);
        try {
            Schedule problem = CSVInput.input("./src/testdata/testdata.csv");
            putProblemById(1L, problem);
            solverManager.solveAndListen(1L, this::getProblemById, this::paint);
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
        reset(canvas, Color.GRAY);

        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root, 1200, 600));
        primaryStage.show();

        solve();
    }
}
