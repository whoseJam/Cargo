package Cargo.move;

import Cargo.domain.Car;
import Cargo.domain.Schedule;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;

//public class CarSwapMove extends AbstractMove<Schedule> {
//    private Car source;
//    private Car target;
//
//    public CarSwapMove(Car source, Car target) {
//        this.source = source;
//        this.target = target;
//    }
//
//    @Override
//    protected void doMoveOnGenuineVariables(ScoreDirector<Schedule> scoreDirector) {
//        scoreDirector.beforeVariableChanged(source, "computer");
//    }
//}
