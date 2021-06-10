package com.bitcoinfetcher.services;

import java.awt.Color;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.function.LineFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.Regression;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

public class DrawChart extends ApplicationFrame {

  private static final long serialVersionUID = 1L;

  TimeSeriesCollection inputData;
  JFreeChart chart;

  public DrawChart(TimeSeriesCollection dataset) {
    super("Linear Regression");
    inputData = dataset;
    this.chart = createChart(inputData);

    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new java.awt.Dimension(900, 600));
    setContentPane(chartPanel);
  }

  public JFreeChart createChart(TimeSeriesCollection inputData) {
    // Create the chart using the data read from the prices.txt file
    JFreeChart chart = ChartFactory.createTimeSeriesChart(
        "Bitcoin Price Prediction", "Day", "Price", inputData, true, true, false);

    XYPlot plot = chart.getXYPlot();
    DateAxis axis = (DateAxis) plot.getDomainAxis();
    axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));
    plot.getRenderer().setSeriesPaint(0, Color.blue);
    return chart;
  }

  public void drawRegressionLine() {
    double regressionParameters[] = Regression.getOLSRegression(inputData,0);
    LineFunction2D linefunction2d = new LineFunction2D(
        regressionParameters[0], regressionParameters[1]);

    LocalDate startDate = LocalDate.now().minusDays(7);
    LocalDate endDate = LocalDate.now().plusDays(3);
    XYDataset dataset = DatasetUtilities.sampleFunction2D(linefunction2d,
        startDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
        endDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
        100, "Regression Line");

    XYPlot xyplot = chart.getXYPlot();
    xyplot.setDataset(1, dataset);
    XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer(
        true, false);
    xylineandshaperenderer.setSeriesPaint(0, Color.YELLOW);
    xyplot.setRenderer(1, xylineandshaperenderer);
  }
}