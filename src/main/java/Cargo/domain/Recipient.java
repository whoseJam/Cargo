package Cargo.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.springframework.stereotype.Component;

@Component
@PlanningEntity
public class Recipient implements Standstill {
    private double size;
    private Location location;
    @PlanningVariable(
        valueRangeProviderRefs = {"carProvider", "recipientProvider"},
        graphType = PlanningVariableGraphType.CHAINED)
    private Standstill previousStandstill;

    @PlanningVariable
    private Storage from;

    public Recipient() {
    }

    public Recipient(double size, Location location) {
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

    public Storage getFrom() {
        return from;
    }

    @Override
    public String toString() {
        return location.toString() + "(" + size + ")";
    }
}
