package TimeTable;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class TimeTableConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
            roomConflict(constraintFactory),
            teacherConflict(constraintFactory),
            studentGroupConflict(constraintFactory)
        };
    }

    private Constraint roomConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Lesson.class)
            .join(Lesson.class,
                Joiners.equal(Lesson::getTimeslot),
                Joiners.equal(Lesson::getRoom),
                Joiners.lessThan(Lesson::getId))
            .penalize(HardSoftScore.ONE_HARD)
            .asConstraint("Room Conflict");
    }

    private Constraint teacherConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Lesson.class)
            .join(Lesson.class,
                Joiners.equal(Lesson::getTimeslot),
                Joiners.equal(Lesson::getTeacher),
                Joiners.lessThan(Lesson::getId))
            .penalize(HardSoftScore.ONE_HARD)
            .asConstraint("Teacher Conflict");
    }

    private Constraint studentGroupConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Lesson.class)
            .join(Lesson.class,
                Joiners.equal(Lesson::getTimeslot),
                Joiners.equal(Lesson::getStudentGroup),
                Joiners.lessThan(Lesson::getId))
            .penalize(HardSoftScore.ONE_HARD)
            .asConstraint("Student Group Conflict");
    }
}
