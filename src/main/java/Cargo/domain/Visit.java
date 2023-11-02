package Cargo.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

@PlanningEntity
public class Visit implements Standstill {
    private double size;
    private Location location;
    @PlanningVariable(graphType = PlanningVariableGraphType.CHAINED)
    private Standstill previousStandstill;

    public Visit() {
    }

    public Visit(double size, Location location) {
        this.size = size;
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public Standstill getPreviousStandstill() {
        return previousStandstill;
    }

    public double getSize() {
        return size;
    }

    @Override
    public String toString() {
        return location.toString() + "(" + size + ")";
    }
}
