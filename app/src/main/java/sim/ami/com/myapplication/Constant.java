package sim.ami.com.myapplication;

import android.content.res.Configuration;

/**
 * Created by Administrator on 4/26/2016.
 */
public class Constant {

    public static final String COMMAND = "CMD";
    public static final String CMD_START_RECORD = "START_RECORD";
    public static final String CMD_UPDATE_CONFIG = "UPDATE_CONFIG";
    public static final String CMD_CUT_VIDEO = "CUT_VIDEO";
    public static final String CMD_EFFECT_VICEO = "EFFECT_VIDEO";
    public static final String CMD_REPLACE_AUDIO = "REPLACE_AUDIO";
    public static final String CMD_TIME_SCALING = "TIME_SCALING";
    public static final String EXTRA_DATA = "EXTRA_DATA";
    public static final String EXTRA_DATA_FILE_NAME = "EXTRA_DATA_FILE_NAME";

    public static final String EXTRA_SETTING_SCREEN = "Setting";

    public static final int BBX_00 = 0;
    public static final int BBX_01 = 1;
    public static final int BBX_02 = 2;
    public static final int BBX_03 = 3;
    public static final int BBX_04 = 4;

    public static final double DRP_00 = 0.08;
    public static final double DRP_01 = 0.1;
    public static final double DRP_02 = 0.125;
    public static final double DRP_03 = 0.15;
    public static final double DRP_04 = 0.175;

    public static final int[] WH_480270 = {310729,388411,485514,582617,679720};
    public static final int[] WH_640360 = {552407,690509,863136,1035763,1208390};
    public static final int[] WH_848480 = {975919,1219899,1524874,1829848,2134824};
    public static final int[] WH_19201080 = {4971663,6214579,7768224,9321869,10875514};
    public static final int[] WH_320240 = {184136,230170,287712,345254,402797};
    public static final int[] WH_400300 = {287712,359640,449550,539640,629370};
    public static final int[] WH_480360 = {414305,517882,647352,776822,906293};
    public static final int[] WH_640480 = {736543,920678,1150848,1381018,1611187};

    public static final int WIDTH_320 = 320;
    public static final int WIDTH_400 = 400;
    public static final int WIDTH_480 = 480;
    public static final int WIDTH_640 = 640;
    public static final int WIDTH_848 = 848;
    public static final int WIDTH_1080 = 1080;

    public static final int [] RESOLUTION_HEIGHT = {1920,1280,848,640,640,480,480,400,320};
    public static final int [] RESOLUTION_WIDTH = {1080,720,480,480,360,360,270,300,240};
    public static final int [] FRAME_RATE = {60,50,48,30,25,24};
    public static final int [] BIT_RATE = {0,12000000,8000000,7500000,5000000,4000000,2500000,1500000,1000000};
    public static final int [] ORIENTATION = {Configuration.ORIENTATION_UNDEFINED, Configuration.ORIENTATION_LANDSCAPE,Configuration.ORIENTATION_PORTRAIT};
    public static final int [] COUNT_DOWN_VALUE = {1,2,3,4,5,6,7,8,9,10};
    public static int getBitRateAuto(int width, int height){
        int autoBitRate = (int) (width*height*30*0.1);
        return autoBitRate;
    }

}
