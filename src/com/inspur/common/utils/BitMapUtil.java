package  com.inspur.common.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BitMapUtil
{
    public static String VERTICAL = "vertical";
    public static String HORIZONTAL = "horizontal";

    public static Bitmap creatContentBitmap(List<String> contentList, int width, int height, float textSize, float startYY, float padding, float range)
    {
        Bitmap newb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newb);

        Paint paint = new Paint();
        paint.setTypeface(Typeface.SERIF);
        paint.setFakeBoldText(false);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        paint.setFilterBitmap(true);

        List newList = new ArrayList();
        float startY = startYY;

        for (int i = 0; i < contentList.size(); ++i) {
            List list = StringUtil.autoSplit((String)contentList.get(i), paint,
                    width);
            for (Iterator localIterator = list.iterator(); localIterator.hasNext(); ) { String str1 = (String)localIterator.next();
                newList.add(str1);
            }
        }
        for (int l = 0; l < newList.size(); ++l) {
            drawText(cv, (String)newList.get(l), 0F, startY, paint, 0F);
            startY = startY + textSize + padding;
        }
        cv.drawBitmap(newb, 0F, 0F, null);

        Matrix m = new Matrix();
        m.setRotate(range);
        try {
            Bitmap b2 = Bitmap.createBitmap(newb, 0, 0, newb.getWidth(),
                    newb.getHeight(), m, true);
            if (newb != b2)
            {
                newb.recycle();
                newb = b2;
            }

        }
        catch (java.lang.OutOfMemoryError b2)
        {
        }
        label290: return newb;
    }

    public static int getCellLength(List<String> contentList, int width, float textSize)
    {
        Paint paint = new Paint();
        paint.setTypeface(Typeface.SERIF);
        paint.setFakeBoldText(false);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        paint.setFilterBitmap(true);
        List newList = new ArrayList();

        for (int i = 0; i < contentList.size(); ++i) {
            List list = StringUtil.autoSplit((String)contentList.get(i), paint,
                    width);
            for (Iterator localIterator = list.iterator(); localIterator.hasNext(); ) { String str1 = (String)localIterator.next();
                newList.add(str1);
            }
        }

        return newList.size();
    }

    public static void drawText(Canvas canvas, String text, float x, float y, Paint paint, float angle)
    {
        if (angle != 0F)
            canvas.rotate(angle, x, y);

        canvas.drawText(text, x, y, paint);
        if (angle != 0F)
            canvas.rotate(-angle, x, y);
    }

    public static Bitmap combineBitmap(Bitmap foreground, Bitmap background, String fangxiang, int vertical_padding, int horizontal_padding, int fb_padding, int range)
    {
        if (background == null)
            return null;

        int fgWidth = foreground.getWidth();
        int fgHeight = foreground.getHeight();
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();

        Bitmap newmap = null;

        if (fangxiang.equals(HORIZONTAL))
            newmap = Bitmap.createBitmap(fgWidth + bgWidth + fb_padding,
                    fgHeight, Bitmap.Config.ARGB_8888);
        else if (fangxiang.equals(VERTICAL))
            newmap = Bitmap.createBitmap(fgWidth, bgHeight + fgHeight +
                    fb_padding, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(newmap);
        if (fangxiang.equals(HORIZONTAL)) {
            canvas.drawBitmap(foreground, 0F, 0F, null);
            canvas.drawBitmap(background, fgWidth + fb_padding,
                    horizontal_padding, null);
        } else if (fangxiang.equals(VERTICAL)) {
            canvas.drawBitmap(foreground, 0F, 0F, null);
            canvas.drawBitmap(background, vertical_padding, fgHeight +
                    fb_padding, null);
        }
        canvas.save(31);
        canvas.restore();

        Matrix m = new Matrix();
        m.setRotate(range);
        try {
            Bitmap  b2 = Bitmap.createBitmap(newmap, 0, 0, newmap.getWidth(),
                    newmap.getHeight(), m, true);
            if (newmap != b2){
                newmap.recycle();
                newmap = b2;
            }

        }
        catch (java.lang.OutOfMemoryError b2)
        {
        }
        label248: return newmap;
    }
}