package Cargo.bean;

import Cargo.domain.Schedule;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class CargoRepository {
    private Map<UUID, Schedule> solutions = new HashMap<>();

    public synchronized Schedule findById(UUID id) {
        return solutions.get(id);
    }

    public synchronized void save(Schedule solution) {
        solutions.put(solution.id, solution);
    }
}
