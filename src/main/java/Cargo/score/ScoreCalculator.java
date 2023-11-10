package Cargo.score;

import Cargo.domain.Car;
import Cargo.domain.Location;
import Cargo.domain.Recipient;
import Cargo.domain.Schedule;
import Cargo.domain.Standstill;
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
        for (Recipient recipient : solution.getRecipients()) {
            Standstill previous = recipient.getPreviousStandstill();
            if (previous != null) edges.put(previous, recipient);
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
            score = score.add(result = calculateCarScore(path));
            maxDist = Math.max(maxDist, result.mediumScore());
        }
        return HardMediumSoftLongScore.of(
            score.hardScore(),
            -maxDist,
            score.softScore()
        );
    }

    public HardMediumSoftLongScore calculateCarScore(List<Standstill> path) {
        double load = 0;
        int correct = 0;
        double dist = 0;
        Car car = (Car) path.get(0);
        for (int i = 1; i < path.size(); i++) {
            Location lastLocation = path.get(i - 1).getLocation();
            Location nowLocation = path.get(i).getLocation();

            Recipient now = (Recipient) path.get(i);
            if (i == 1 || ((Recipient) path.get(i - 1)).getFrom() != now.getFrom()) {
                Location storage = now.getFrom().getLocation();
                if (lastLocation == null) {
                    System.out.println(path.get(i - 1));
                    System.out.println(path.get(i));
                }
                dist += lastLocation.getDistanceTo(storage);
                dist += storage.getDistanceTo(nowLocation);
                int j = i;
                while (j + 1 < path.size()) {
                    Recipient next = (Recipient) path.get(j + 1);
                    if (now.getFrom() == next.getFrom()) j++;
                    else break;
                }
                for (int k = i; k <= j; k++) load += ((Recipient) path.get(k)).getSize();
                correct += Math.max(load - car.getCapacity(), 0);
            } else {
                dist += lastLocation.getDistanceTo(nowLocation);
            }
            load -= now.getSize();
        }
        dist *= 1000000;
        return HardMediumSoftLongScore.of(-correct, (int) dist, (int) -dist);
    }
}
