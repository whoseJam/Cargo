package Cargo.nearby;

import Cargo.domain.Recipient;
import Cargo.domain.Standstill;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;

public class MyNearbyDistanceMeter implements NearbyDistanceMeter<Recipient, Standstill> {
    @Override
    public double getNearbyDistance(Recipient origin, Standstill destination) {
        double distance = origin.getLocation().getDistanceTo(destination.getLocation());
        return distance;
    }
}