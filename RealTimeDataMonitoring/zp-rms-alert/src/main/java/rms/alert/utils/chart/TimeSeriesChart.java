package rms.alert.utils.chart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class TimeSeriesChart {
    @Autowired
    private Environment environment;

    private final Logger logger = LogManager.getLogger(LineChart.class);

    public String getConfigPath() {
        try {
            return environment.getProperty("app.path").trim();
        } catch (Exception e) {
            return ".";
        }
    }

    public void generateChart(String allRejectRateTurn, String allRejectRateUser) {
        final TimeSeries series = new TimeSeries( "RejectRateTurn");
        final TimeSeries secondSeries = new TimeSeries( "RejectRateUser");

        createSeries(allRejectRateTurn, series);
        createSeries(allRejectRateUser,secondSeries);

        final XYDataset dataset=( XYDataset )new TimeSeriesCollection(series);
        JFreeChart timechart = ChartFactory.createTimeSeriesChart(
                "Reject rate Lixi 2020",
                "Time",
                "Reject rate",
                 dataset,
                true,
                true,
                false);

        final XYPlot plot = timechart.getXYPlot();
        plot.setBackgroundPaint(new Color(0xffffe0));
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        final XYDataset secondDataset = ( XYDataset )new TimeSeriesCollection(secondSeries);
        plot.setDataset(1, secondDataset); // the second dataset (datasets are zero-based numbering)
        plot.mapDatasetToDomainAxis(1, 0); // same axis, different dataset
        plot.mapDatasetToRangeAxis(1, 0); // same axis, different dataset
        final XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.BLUE);
        plot.setRenderer(1, renderer);

        int width = 640;   /* Width of the image */
        int height = 480;  /* Height of the image */
        File timeChart = new File(getConfigPath()+ "/conf/image/RejectRate.jpeg" );
        try {
            ChartUtilities.saveChartAsJPEG( timeChart, timechart, width, height );
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    private void createSeries(String allRejectRateTurn, TimeSeries series) {
        String[] points = allRejectRateTurn.split(":");
        for (String point:points){
            String[] data = point.split("-");
            String dateTime = data[0];
            double rejectRate = Double.parseDouble(data[1]);

            String[] date = dateTime.split("\\s");
            String day = date[0].split("/")[0];
            String month = date[0].split("/")[1];
            String hour = date[1].split("h")[0];
            String minute = date[1].split("h")[1];

            Minute current = new Minute(Integer.parseInt(minute),Integer.parseInt(hour),Integer.parseInt(day),Integer.parseInt(month),2020);
            try {
                series.add(current,rejectRate);
            } catch ( SeriesException e ) {
                logger.error( "Error adding to series" );
            }
        }
    }
}