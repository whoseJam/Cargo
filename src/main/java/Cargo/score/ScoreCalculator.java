package Cargo.score;

import Cargo.domain.Car;
import Cargo.domain.Location;
import Cargo.domain.Schedule;
import Cargo.domain.Standstill;
import Cargo.domain.Visit;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreCalculator
    implements EasyScoreCalculator<Schedule, HardMediumSoftLongScore> {
    @Override
    public HardMediumSoftLongScore calculateScore(Schedule solution) {
        HardMediumSoftLongScore score = HardMediumSoftLongScore.of(0, 0, 0);
        Map<Standstill, Standstill> edges = new HashMap<>();
        for (Visit visit : solution.getVisits()) {
            Standstill previous = visit.getPreviousStandstill();
            if (previous != null) edges.put(previous, visit);
        }
        long maxDist = 0;
        for (Car car : solution.getCars()) {
            Standstill cur = car;
            List<Standstill> path = new ArrayList<>();
            path.add(cur);
            while (edges.containsKey(cur)) {
                Standstill next = edges.get(cur);
                path.add(next);
                cur = next;
            }
            HardMediumSoftLongScore result;
            if (car.isDummy()) score = score.add(calculateDummyCarScore(path));
            else {
                score = score.add(result = calculateCarScore(path));
                maxDist = Math.max(maxDist, result.mediumScore());
            }
        }
        return HardMediumSoftLongScore.of(
            score.hardScore(),
            -maxDist,
            score.softScore()
        );
    }

    public HardMediumSoftLongScore calculateDummyCarScore(List<Standstill> path) {
        int notSendTarget = 0;
        for (int i = 1; i < path.size(); i++) {
            Visit visit = (Visit) path.get(i);
            if (visit.getSize() < 0) notSendTarget += visit.getSize() * 100;
        }
        return HardMediumSoftLongScore.of(notSendTarget, 0, 0);
    }

    public HardMediumSoftLongScore calculateCarScore(List<Standstill> path) {
        double load = 0;
        int correct = 0;
        double oil = 0;
        double dist = 0;
        Car car = (Car) path.get(0);
        for (int i = 1; i < path.size(); i++) {
            Location lastLocation = path.get(i - 1).getLocation();
            Location nowLocation = path.get(i).getLocation();
            dist += lastLocation.getDistanceTo(nowLocation);
            oil += load * lastLocation.getDistanceTo(nowLocation);
            Visit now = (Visit) path.get(i);
            load += now.getSize();
            if (now.getSize() > 0) {
                correct += Math.max(load - car.getCapacity(), 0);
                load = Math.min(load, car.getCapacity());
            } else {
                correct += Math.max(-load, 0);
                load = Math.max(load, 0);
            }
        }
        dist *= 1000000;
        oil = 0;
        return HardMediumSoftLongScore.of(-correct, (int) dist, (int) -oil);
    }
}
