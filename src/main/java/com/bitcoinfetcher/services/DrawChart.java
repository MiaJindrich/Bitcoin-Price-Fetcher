package com.bitcoinfetcher.services;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.function.LineFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

public class DrawChart extends ApplicationFrame {

  private static final long serialVersionUID = 1L;

  XYDataset inputData;
  JFreeChart chart;

  public DrawChart(XYDataset dataset) {
    super("Linear Regression");
    inputData = dataset;
    this.chart = createChart(inputData);

    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
    setContentPane(chartPanel);
  }

  public static JFreeChart createChart(XYDataset inputData) {
    // Create the chart using the data read from the prices.txt file
    JFreeChart chart = ChartFactory.createScatterPlot(
        "Bitcoin Price Prediction", "Day", "Price", inputData,
        PlotOrientation.VERTICAL, true, true, false);

    XYPlot plot = chart.getXYPlot();
    plot.getRenderer().setSeriesPaint(0, Color.blue);
    return chart;
  }

  public void drawRegressionLine() {
    // Get the parameters 'a' and 'b' for an equation y = a + b * x,
    // fitted to the inputData using ordinary least squares regression.
    // a - regressionParameters[0], b - regressionParameters[1]
    double regressionParameters[] = Regression.getOLSRegression(inputData,
        0);

    // Prepare a line function using the found parameters
    LineFunction2D linefunction2d = new LineFunction2D(
        regressionParameters[0], regressionParameters[1]);

    // Creates a dataset by taking sample values from the line function
    XYDataset dataset = DatasetUtilities.sampleFunction2D(linefunction2d,
        1D, 15, 100, "Regression Line");

    // Draw the line dataset
    XYPlot xyplot = chart.getXYPlot();
    xyplot.setDataset(1, dataset);
    XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer(
        true, false);
    xylineandshaperenderer.setSeriesPaint(0, Color.YELLOW);
    xyplot.setRenderer(1, xylineandshaperenderer);
  }

}