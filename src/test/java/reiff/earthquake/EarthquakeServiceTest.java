package reiff.earthquake;

import org.junit.jupiter.api.Test;
import reiff.earthquake.json.FeatureCollection;
import reiff.earthquake.json.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class EarthquakeServiceTest {

    @Test
    public void oneHour() {
        //given
        EarthquakeService service = new EarthquakeServiceFactory().getService();

        //when
        FeatureCollection collection = service.oneHour().blockingGet();

        //then
        Properties properties = collection.features[0].properties;
        assertNotNull(properties.place);
        assertNotEquals(0, properties.mag);
        assertNotEquals(0, properties.time);
    }

    @Test
    public void lastThirty() {
        //given
        EarthquakeService service = new EarthquakeServiceFactory().getService();

        //when
        FeatureCollection collection = service.lastThirty().blockingGet();

        //then
        Properties properties = collection.features[0].properties;
        assertNotNull(properties.place);
        assertNotEquals(0, properties.mag);
        assertNotEquals(0, properties.time);
    }
}