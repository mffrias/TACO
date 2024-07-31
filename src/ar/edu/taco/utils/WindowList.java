package ar.edu.taco.utils;

import java.util.ArrayList;
import java.util.List;

public class WindowList {

    /*
    *
    * Position stored in list is from first window to last window
    *
    */
    int wTime, rangeVal, minVal, maxVal;
    float meanVal;
    List<Window> windowList;
    Window windowVals;
    public WindowList(int windowTime){
        windowList = new ArrayList<Window>();
        this.wTime = windowTime;
        this.meanVal = 0;
        this.rangeVal = 0;
        this.maxVal = 0;
        this. minVal = 0;
    }


    public void addWLVals(Window windowVals){
        windowList.add(windowVals);
    }

    public void diffMean(){
        float sum = 0;
        for(Window w: windowList){
            sum = sum + (float)w.getValDiff();
        }
        this.meanVal = sum / (float)windowList.size();
    }
    public void diffRange(){
        this.maxVal = Integer.MIN_VALUE;
        this.minVal = Integer.MAX_VALUE;

        for(Window w: windowList){
            int curVal = w.getValDiff();
            if(curVal > maxVal){
                maxVal = curVal;
            }
            if(curVal < minVal && curVal >= 0){
                minVal = curVal;
            }
        }
        this.rangeVal = this.maxVal - this.minVal;
    }

    public float getMeanVal(){
        diffMean();
        return this.meanVal;
    }
    public int getRangeVal(){
        diffRange();
        return this.rangeVal;
    }

    public int getMaxVal(){
        diffRange();
        return this.maxVal;
    }

    public int getMinVal(){
        diffRange();
        return this.minVal;
    }

    // format window values and print
    public void printVals(){

        boolean newLineFlag = false;
        int listSize = windowList.size();

        String s = "Group 1:             ";
        int spaceTL = s.length();
        // space length between values should be total length - value length

        System.out.println();
        System.out.println("--Update Time: " + wTime + "--");

        //while(!newLineFlag) {

            // print group number
            for (int i = 1; i <= listSize; i++) {
                int numLen = String.valueOf(i).length();
                String space = "             ";
                if (numLen > 1) {
                    space = space.substring(numLen - 1);

                    if (numLen % 10 == 1) {
                        newLineFlag = true;
                        break;
                    }
                }
                System.out.print("Group " + i + ":" + space);
            }
            System.out.println();

            // print first window title
            for (int i = 0; i < windowList.size(); i++) {
                System.out.print("First Window:        ");
            }
            System.out.println();

            // print first window value
            for (Window w : windowList) {
                int valA = w.getValA();
                int valLeng = String.valueOf(valA).length();
                String space = "                    ";
                if (valLeng > 1) {
                    space = space.substring(valLeng);
                }
                System.out.print(valA + space);
            }
            System.out.println();

            // print second window title
            for (int i = 0; i < windowList.size(); i++) {
                System.out.print("Last Window:         ");
            }
            System.out.println();

            // print last window value
            for (Window w : windowList) {
                int valB = w.getValB();
                int valLeng = String.valueOf(valB).length();
                String space = "                    ";
                if (valLeng > 1) {
                    space = space.substring(valLeng);
                }
                System.out.print(valB + space);
            }
            System.out.println();

            // print difference window title
            for (int i = 0; i < windowList.size(); i++) {
                System.out.print("Window Difference:   ");
            }
            System.out.println();

            // print window difference value
            for (Window w : windowList) {
                int valDiff = w.getValDiff();
                int valLeng = String.valueOf(valDiff).length();
                String space = "                    ";
                if (valLeng > 1) {
                    space = space.substring(valLeng);
                }
                System.out.print(valDiff + space);
            }
            System.out.println();
            if(newLineFlag){
                System.out.println();
                System.out.println();
                newLineFlag = false;
            }

        //}
    }
}
