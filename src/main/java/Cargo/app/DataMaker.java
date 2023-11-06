package Cargo.app;

import Cargo.domain.Car;
import Cargo.domain.Location;
import Cargo.domain.Recipient;
import Cargo.domain.Schedule;
import Cargo.domain.Storage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class DataMaker {
    private static Color[] colors = {
        Color.AZURE,
        Color.RED,
        Color.ORANGE,
        Color.GREENYELLOW,
        Color.BLUE,
        Color.PERU,
        Color.ROSYBROWN,
        Color.OLDLACE,
        Color.BURLYWOOD,
        Color.CADETBLUE,
        Color.CHARTREUSE,
        Color.CHOCOLATE,
        Color.CORAL
    };

    private Location randomLocation(String name) {
        return new Location(name, Math.random() * 100, Math.random() * 100);
    }

    private int randInt(int l, int r) {
        double rd = Math.random();
        double dist = rd * (r - l);
        int cur = (int) (l + dist);
        if (cur > r) cur = r;
        if (cur < l) cur = l;
        return cur;
    }

    public Schedule data(int nloc, int ncar, int nstore, int nrep) {
        List<Location> locations = new ArrayList<>();
        List<Car> cars = new ArrayList<>();
        List<Storage> storages = new ArrayList<>();
        List<Recipient> recipients = new ArrayList<>();
        for (int i = 0; i < nloc; i++)
            locations.add(randomLocation("loc." + i));
        for (int i = 0; i < ncar; i++) {
            cars.add(new Car(i, 500, locations.get(randInt(0, nloc - 1))));
            cars.get(i).setColor(colors[i]);
        }
        for (int i = 0; i < nstore; i++)
            storages.add(new Storage(100000, locations.get(randInt(0, nloc - 1))));
        for (int i = 0; i < nrep; i++)
            recipients.add(new Recipient(randInt(1, 5), locations.get(randInt(0, nloc - 1))));
        return new Schedule(cars, recipients, storages);
    }
}
