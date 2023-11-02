package TimeTable;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PlanningSolution
public class TimeTable {
    @ValueRangeProvider
    @ProblemFactCollectionProperty
    private List<Timeslot> timeslotList;

    @ValueRangeProvider
    @ProblemFactCollectionProperty
    private List<Room> roomList;

    @PlanningEntityCollectionProperty
    private List<Lesson> lessonList;

    @PlanningScore
    private HardSoftScore score;

    private TimeTable() {
    }

    public TimeTable(List<Timeslot> timeslotList, List<Room> roomList, List<Lesson> lessonList) {
        this.timeslotList = timeslotList;
        this.roomList = roomList;
        this.lessonList = lessonList;
    }

    public List<Timeslot> getTimeslotList() {
        return timeslotList;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public List<Lesson> getLessonList() {
        return lessonList;
    }

    public HardSoftScore getScore() {
        return score;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Map<Timeslot, Map<Room, List<Lesson>>> lessonMap = lessonList.stream()
            .filter(lesson -> lesson.getTimeslot() != null && lesson.getRoom() != null)
            .collect(Collectors.groupingBy(Lesson::getTimeslot, Collectors.groupingBy(Lesson::getRoom)));
        builder.append("|            | " + roomList.stream()
            .map(room -> String.format("%-10s", room.getName())).collect(Collectors.joining(" | ")) + " |\n");
        builder.append("|" + "------------|".repeat(roomList.size() + 1)  + "\n");
        for (Timeslot timeslot : timeslotList) {
            List<List<Lesson>> cellList = roomList.stream()
                .map(room -> {
                    Map<Room, List<Lesson>> byRoomMap = lessonMap.get(timeslot);
                    if (byRoomMap == null) {
                        return Collections.<Lesson>emptyList();
                    }
                    List<Lesson> cellLessonList = byRoomMap.get(room);
                    if (cellLessonList == null) {
                        return Collections.<Lesson>emptyList();
                    }
                    return cellLessonList;
                })
                .collect(Collectors.toList());

            builder.append("| " + String.format("%-10s",
                timeslot.getDayOfWeek().toString().substring(0, 3) + " " + timeslot.getStartTime()) + " | "
                + cellList.stream().map(cellLessonList -> String.format("%-10s",
                    cellLessonList.stream().map(Lesson::getSubject).collect(Collectors.joining(", "))))
                .collect(Collectors.joining(" | "))
                + " |\n");
            builder.append("|            | "
                + cellList.stream().map(cellLessonList -> String.format("%-10s",
                    cellLessonList.stream().map(Lesson::getTeacher).collect(Collectors.joining(", "))))
                .collect(Collectors.joining(" | "))
                + " |\n");
            builder.append("|            | "
                + cellList.stream().map(cellLessonList -> String.format("%-10s",
                    cellLessonList.stream().map(Lesson::getStudentGroup).collect(Collectors.joining(", "))))
                .collect(Collectors.joining(" | "))
                + " |\n");
            builder.append("|" + "------------|".repeat(roomList.size() + 1) + "\n");
        }
        List<Lesson> unassignedLessons = lessonList.stream()
            .filter(lesson -> lesson.getTimeslot() == null || lesson.getRoom() == null)
            .collect(Collectors.toList());
        if (!unassignedLessons.isEmpty()) {
            builder.append("Unassigned lessons\n");
            for (Lesson lesson : unassignedLessons) {
                builder.append("  " + lesson.getSubject() + " - " + lesson.getTeacher() + " - " + lesson.getStudentGroup() + "\n");
            }
        }
        return new String(builder);
    }
}
