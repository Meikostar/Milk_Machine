package com.canplay.milk.view.OscliiChart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwan on 2016/9/26.
 */
public class OscliiChart extends View {

    // 坐标单位
    private String[] xLabel;
    private String[] yLabel;
    // 曲线数据
    private ArrayList<OscliiPoint> dataList;
    private List<Integer> colorList;
    private boolean showValue;
    // 默认边距
    private int margin = 20;
    // 距离左边偏移量
    private int marginX = 30;
    // 原点坐标
    private int xPoint;
    private int yPoint;
    // X,Y轴的单位长度
    private int xScale;
    private int yScale;
    // 画笔
    private Paint paintAxes;
    private Paint paintCoordinate;
    private Paint paintTable;
    private Paint paintCurve;

    public OscliiChart(Context context, String[] xLabel, String[] yLabel,
                       ArrayList<OscliiPoint> dataList) {
        super(context);
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.dataList = dataList;
    }

    public OscliiChart(Context context) {
        super(context);
    }

    /**
     * 初始化数据值和画笔
     */
    public void init() {
        xPoint = margin + marginX;
        yPoint = this.getHeight() - margin;
        xScale = (this.getWidth() - 2 * margin - marginX) / (xLabel.length - 1);
        yScale = (this.getHeight() - 2 * margin) / (yLabel.length - 1);

        paintAxes = new Paint();
        paintAxes.setStyle(Paint.Style.STROKE);
        paintAxes.setAntiAlias(true);
        paintAxes.setDither(true);
        paintAxes.setColor(Color.parseColor("#666666"));
        paintAxes.setStrokeWidth(4);

        paintCoordinate = new Paint();
        paintCoordinate.setStyle(Paint.Style.STROKE);
        paintCoordinate.setDither(true);
        paintCoordinate.setAntiAlias(true);
        paintCoordinate.setColor(Color.parseColor("#666666"));
        paintCoordinate.setTextSize(15);

        paintTable = new Paint();
        paintTable.setStyle(Paint.Style.STROKE);
        paintTable.setAntiAlias(true);
        paintTable.setDither(true);
        paintTable.setColor(Color.parseColor("#F6F6F6"));
        paintTable.setStrokeWidth(2);

        paintCurve = new Paint();
        paintCurve.setStyle(Paint.Style.STROKE);
        paintCurve.setDither(true);
        paintCurve.setAntiAlias(true);
        paintCurve.setStrokeWidth(3);

    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawColor(ContextCompat.getColor(getContext(), R.color.color1));
        init();
        drawTable(canvas, paintTable);
        drawAxesLine(canvas, paintAxes);
        drawCoordinate(canvas, paintCoordinate);

            drawCurve(canvas, paintCurve, dataList, Color.RED);
//            if (showValue) {
//                drawValue(canvas, paintRectF, dataList.get(i), colorList.get(i));
//            }
    }

    /**
     * 绘制坐标轴
     */
    private void drawAxesLine(Canvas canvas, Paint paint) {
        // X
        canvas.drawLine(xPoint, yPoint, this.getWidth() - margin / 6, yPoint, paint);
        canvas.drawLine(this.getWidth() - margin / 6, yPoint, this.getWidth() - margin / 2, yPoint - margin / 3, paint);
        canvas.drawLine(this.getWidth() - margin / 6, yPoint, this.getWidth() - margin / 2, yPoint + margin / 3, paint);

        // Y
        canvas.drawLine(xPoint, yPoint, xPoint, margin / 6, paint);
        canvas.drawLine(xPoint, margin / 6, xPoint - margin / 3, margin / 2, paint);
        canvas.drawLine(xPoint, margin / 6, xPoint + margin / 3, margin / 2, paint);
    }

    /**
     * 绘制表格
     */
    private void drawTable(Canvas canvas, Paint paint) {
        Path path = new Path();
        // 横向线
        for (int i = 1; (yPoint - i * yScale) >= margin; i++) {
            int startX = xPoint;
            int startY = yPoint - i * yScale;
            int stopX = xPoint + (xLabel.length - 1) * xScale;
            path.moveTo(startX, startY);
            path.lineTo(stopX, startY);
            canvas.drawPath(path, paint);
        }

        // 纵向线
        for (int i = 1; i * xScale <= (this.getWidth() - margin); i++) {
            int startX = xPoint + i * xScale;
            int startY = yPoint;
            int stopY = yPoint - (yLabel.length - 1) * yScale;
            path.moveTo(startX, startY);
            path.lineTo(startX, stopY);
            canvas.drawPath(path, paint);
        }
    }

    /**
     * 绘制刻度
     */
    private void drawCoordinate(Canvas canvas, Paint paint) {
        // X轴坐标
        for (int i = 0; i <= (xLabel.length - 1); i++) {
            paint.setTextAlign(Paint.Align.CENTER);
            int startX = xPoint + i * xScale;
            canvas.drawText(xLabel[i], startX, this.getHeight() - margin / 6, paint);
        }

        // Y轴坐标
        for (int i = 0; i <= (yLabel.length - 1); i++) {
            paint.setTextAlign(Paint.Align.LEFT);
            int startY = yPoint - i * yScale;
            int offsetX;
            switch (yLabel[i].length()) {
                case 1:
                    offsetX = 28;
                    break;

                case 2:
                    offsetX = 20;
                    break;

                case 3:
                    offsetX = 12;
                    break;

                case 4:
                    offsetX = 5;
                    break;

                default:
                    offsetX = 0;
                    break;
            }
            int offsetY;
            if (i == 0) {
                offsetY = 0;
            } else {
                offsetY = margin / 5;
            }
            // x默认是字符串的左边在屏幕的位置，y默认是字符串是字符串的baseline在屏幕上的位置
            canvas.drawText(yLabel[i], margin / 4 + offsetX, startY + offsetY, paint);
        }
    }

    /**
     * 绘制曲线
     */
    private void drawCurve(Canvas canvas, Paint paint, ArrayList<OscliiPoint> points, int color) {
        paint.setColor(Color.BLUE);
        Path path = new Path();
        for (int i = 0; i < points.size(); i++) {
            if (i == 0) {
                path.moveTo(toX(points.get(i).x), toY(points.get(i).y));
            } else {
                path.lineTo(toX(points.get(i).x), toY(points.get(i).y));
            }
//
//            if (i == xLabel.length - 1) {
//                path.lineTo(xPoint + i * xScale, toY(data[i]));
//            }
        }
        canvas.drawPath(path, paint);
    }





    /**
     * 数据按比例转坐标
     */
    private float toY(float num) {
        float y;
        try {
            float a = (float) num / 10.0f;
            y = yPoint - a * yScale;
        } catch (Exception e) {
            return 0;
        }
        return y;
    }

    private float toX(float num) {
        float x = 0;
        try {
            float a = num/10.f;
            x = xPoint+a*xScale;
        }catch (Exception e){
            return 0;
        }
        return x;
    }

}
