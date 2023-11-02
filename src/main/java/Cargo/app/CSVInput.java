package Cargo.app;

import Cargo.domain.Car;
import Cargo.domain.Location;
import Cargo.domain.Schedule;
import Cargo.domain.Visit;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVInput {
    public static Schedule input(String path) throws IOException {
        File csvFile = new File(path);
        CSVReader csvReader = new CSVReader(
            new InputStreamReader(new FileInputStream(csvFile), "GBK"),
            CSVWriter.DEFAULT_SEPARATOR,
            CSVWriter.NO_QUOTE_CHARACTER);
        String[] records;

        List<Car> cars = new ArrayList<>();
        List<Visit> visits = new ArrayList<>();
        List<Location> wareHouse = new ArrayList<>();
        wareHouse.add(new Location("House1", 35, -80));
        wareHouse.add(new Location("House2", 40, -80));
        wareHouse.add(new Location("House3", 45, -80));

        while ((records = csvReader.readNext()) != null) {
            try {
                double size = Double.parseDouble(records[6]);
                double latitude = Double.parseDouble(records[7]);
                double longitude = Double.parseDouble(records[8]);

                String locationName = records[1];
                Location location = new Location(locationName, latitude, longitude);
                visits.add(new Visit(-size, location));

                for (Location house : wareHouse) {
                    visits.add(new Visit(size, house));
                }
            } catch (Exception e) {
                System.out.println("skip this data");
            }
        }
        cars.add(new Car(1, 500, wareHouse.get(0)));
        cars.add(new Car(2, 500, wareHouse.get(1)));
        cars.add(new Car(3, 500, wareHouse.get(2)));
        cars.get(0).setColor(Color.FIREBRICK);
        cars.get(1).setColor(Color.AZURE);
        cars.get(2).setColor(Color.BLANCHEDALMOND);
        return new Schedule(cars, visits);
    }
}
