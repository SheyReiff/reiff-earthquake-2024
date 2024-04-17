package reiff.earthquake;

import hu.akarnokd.rxjava3.swing.SwingSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import reiff.earthquake.json.Feature;
import reiff.earthquake.json.FeatureCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class EarthquakeFrame extends JFrame {

    private final JList<String> jlist = new JList<>();

    public EarthquakeFrame() {

        setTitle("EarthquakeFrame");
        setSize(300, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JRadioButton oneHourButton = new JRadioButton("One Hour");
        JRadioButton thirtyDaysButton = new JRadioButton("30 Days");

        oneHourButton.setSelected(true);
        thirtyDaysButton.setSelected(false);


        oneHourButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        thirtyDaysButton.setHorizontalTextPosition(SwingConstants.RIGHT);

        JPanel radioButtonPanel = new JPanel(new GridLayout(1, 2));
        radioButtonPanel.add(oneHourButton);
        radioButtonPanel.add(thirtyDaysButton);


        ButtonGroup group = new ButtonGroup();
        group.add(oneHourButton);
        group.add(thirtyDaysButton);

        add(jlist, BorderLayout.CENTER);
        add(radioButtonPanel, BorderLayout.NORTH);

        EarthquakeService service = new EarthquakeServiceFactory().getService();
        // Subscribe to the selected radio button's data stream
        oneHourButton.addActionListener(e -> {
            if (oneHourButton.isSelected()) {
                subscribeToData(service.oneHour());
            }
        });

        thirtyDaysButton.addActionListener(e -> {
            if (thirtyDaysButton.isSelected()) {
                subscribeToData(service.lastThirty());
            }
        });
    }

    private void subscribeToData(Single<FeatureCollection> dataStream) {
        Disposable disposable = dataStream
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .subscribe(
                        this::handleResponse,
                        Throwable::printStackTrace);
    }

    private void handleResponse(FeatureCollection response) {
        String[] listData = Arrays.stream(response.features)
                .map(feature -> feature.properties.mag + " " + feature.properties.place)
                .toList()
                .toArray(new String[0]);
        jlist.setListData(listData);
        jlist.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Ensure action only on final selection
                int selectedIndex = jlist.getSelectedIndex();
                if (selectedIndex != -1) {
                    Feature selectedFeature = response.features[selectedIndex];
                    double latitude = selectedFeature.geometry.coordinates[1];
                    double longitude = selectedFeature.geometry.coordinates[0];
                    openGoogleMaps(latitude, longitude);
                }
            }
        });
    }

    private void openGoogleMaps(double latitude, double longitude) {
        String url = String.format("https://www.google.com/maps?q=%.6f,%.6f", latitude, longitude);
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new EarthquakeFrame().setVisible(true);
    }

}