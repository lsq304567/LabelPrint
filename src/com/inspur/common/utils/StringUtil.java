package com.inspur.common.utils;

import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/2/9 0009.
 */
public class StringUtil {
    public static ArrayList<String> autoSplit(String contentStr, Paint p, float width)
    {
        ArrayList reList = new ArrayList();
        String[] tempSplit = contentStr.split("\\n+");

        for (int x = 0; x < tempSplit.length; ++x) {
            String content = tempSplit[x];
            if (content.length() > 0) {
                int length = content.length();
                float textWidth = p.measureText(content);
                if (textWidth <= width) {
                    reList.add(content);
                }
                else
                {
                    int start = 0; int end = 1;
                    label163: for (; start < length; );
                }

            }

        }

        return reList;
    }

    public static List<String> reAutoSplit(List<String> list) {
        if (list.size() > 4) {
            String str = (String)list.get(3);
            String newStr = str.substring(0, str.length() - 2) + "...";
            list.set(3, newStr);
        }
        return list;
    }

    public static String handleString(String msg)
    {
        if ((msg != null) && (msg.split("\\)\\(").length == 4)) {
            String[] str = msg.split("\\)\\(");
            String newMsg = str[0] + ")~(" + str[1] + ")(" + str[2] + ")~(" +
                    str[3];
            return newMsg;
        }
        return msg;
    }

    public static String subString(String msg1, Paint paint)
    {
        List list = autoSplit(msg1, paint, 340.0F);
        if (list.size() > 4)
            return ((String)list.get(0)) + ((String)list.get(1)) + "..." +
                    ((String)list.get(list.size() - 2)) + ((String)list.get(list.size() - 1));

        return msg1;
    }

    public static List<String> split(String text, int length, String encoding)
            throws Exception
    {
        List texts = new ArrayList();
        int pos = 0;
        int startInd = 0;

        for (int i = 0; (text != null) && (i < text.length()); ) {
            byte[] b = String.valueOf(text.charAt(i)).getBytes(encoding);
            if (b.length > length) {
                ++i;
                startInd = i;
                break ;
            }

            pos += b.length;
            if (pos >= length)
            {
                int endInd;
                if (pos == length)
                    endInd = ++i;
                else {
                    endInd = i;
                }

                texts.add(text.substring(startInd, endInd));
                pos = 0;
                startInd = i;
                break ; }
            label114: ++i;
        }

        if (startInd < text.length())
            texts.add(text.substring(startInd));

        return texts;
    }
}
