package reiff.earthquake;

import io.reactivex.rxjava3.core.Single;
import reiff.earthquake.json.FeatureCollection;
import retrofit2.http.GET;

public interface EarthquakeService {

    @GET("earthquakes/feed/v1.0/summary/1.0_hour.geojson") //The file that I am requesting
    Single<FeatureCollection> oneHour();
    @GET("earthquakes/feed/v1.0/summary/significant_month.geojson")
    Single<FeatureCollection> lastThirty();
}
