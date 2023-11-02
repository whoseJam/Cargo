import Cargo.domain.Car;
import Cargo.domain.Location;
import Cargo.domain.Schedule;
import Cargo.domain.Visit;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource("Cargo/cargo.xml");
        SolverFactory<Schedule> solverFactory = SolverFactory.create(solverConfig);
        SolverManager<Schedule, Long> solverManager;

        List<Location> locations = new ArrayList<>();
        List<Visit> visits = new ArrayList<>();
        locations.add(new Location("北京", 100, 100));
        locations.add(new Location("上海", 100, 200));
        locations.add(new Location("天津", 200, 100));
        locations.add(new Location("深圳", 200, 200));
        for (int i = 1; i <= 5; i++) {
            visits.add(new Visit(10, locations.get(0)));
            visits.add(new Visit(10, locations.get(1)));
            visits.add(new Visit(10, locations.get(2)));
            visits.add(new Visit(10, locations.get(3)));
        }
        locations.add(new Location("重庆物流点", 50, 50));
        locations.add(new Location("云南物流点", 200, 150));
        for (int i = 1; i <= 5; i++) visits.add(new Visit(-10, locations.get(4)));
        for (int i = 1; i <= 5; i++) visits.add(new Visit(-10, locations.get(5)));

        List<Car> cars = new ArrayList<>();
        cars.add(new Car(1, 200, locations.get(0)));
        cars.add(new Car(2, 200, locations.get(3)));

        Schedule problem = new Schedule(cars, visits);
        Solver<Schedule> solver = solverFactory.buildSolver();

        Schedule solution = solver.solve(problem);
        System.out.println(solution.toString());
    }
}