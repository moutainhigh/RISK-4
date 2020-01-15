package rms.alert.utils.chart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class LineChart {
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

    public void generateChart(String allRejectRate) {
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        String[] points = allRejectRate.split(":");
        for (String point:points){
            String[] data = point.split("-");
            String dateTime = data[0];
            float rejectRate = Float.parseFloat(data[1]);
            line_chart_dataset.addValue(rejectRate,"rejectRate",dateTime);
        }

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "Reject rate Lixi 2020","Time",
                "Reject rate",
                line_chart_dataset,PlotOrientation.VERTICAL,
                true,true,false);

        int width = 640;    /* Width of the image */
        int height = 480;   /* Height of the image */
        File lineChart = new File(getConfigPath()+ "/conf/image/RejectRate.jpeg" );
        try {
            ChartUtilities.saveChartAsPNG(lineChart ,lineChartObject, width ,height);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}