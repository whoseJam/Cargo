package Cargo.app;

import Cargo.domain.Car;
import Cargo.domain.Location;
import Cargo.domain.Recipient;
import Cargo.domain.Schedule;
import Cargo.domain.Storage;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CSVInput {
    public static Schedule input(String path) throws IOException {
        File csvFile = new File(path);
        CSVReader csvReader = new CSVReader(
            new InputStreamReader(new FileInputStream(csvFile), "GBK"),
            CSVWriter.DEFAULT_SEPARATOR,
            CSVWriter.NO_QUOTE_CHARACTER);
        String[] records;

        List<Car> cars = new ArrayList<>();
        List<Storage> storages = new ArrayList<>();
        List<Recipient> recipients = new ArrayList<>();
        storages.add(new Storage(100, new Location("House1", 35, -85)));
        storages.add(new Storage(100, new Location("House2", 40, -90)));
        storages.add(new Storage(100, new Location("House3", 45, -85)));

        Random rand = new Random(42);
        int i = 1;
        while ((records = csvReader.readNext()) != null) {
            i++;
            try {
                if (rand.nextBoolean() || i % 4 != 0) continue;
                double size = Double.parseDouble(records[6]);
                double latitude = Double.parseDouble(records[7]);
                double longitude = Double.parseDouble(records[8]);
//                if (longitude >= -85) continue;

                String locationName = records[1];
                Location location = new Location(locationName, latitude, longitude);
                if (location.getDistanceTo(storages.get(0).getLocation()) >= 50) continue;
                recipients.add(new Recipient(size, location));
            } catch (Exception e) {
                System.out.println("skip this data");
            }
        }
        cars.add(new Car(1, 100, storages.get(0).getLocation()));
        cars.add(new Car(2, 100, storages.get(1).getLocation()));
        cars.add(new Car(3, 100, storages.get(2).getLocation()));
        cars.get(0).setColor(Color.FIREBRICK);
        cars.get(1).setColor(Color.AZURE);
        cars.get(2).setColor(Color.BLANCHEDALMOND);
        return new Schedule(cars, recipients, storages);
    }
}
