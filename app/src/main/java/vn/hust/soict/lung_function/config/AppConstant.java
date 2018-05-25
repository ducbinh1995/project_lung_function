package vn.hust.soict.lung_function.config;

import android.media.AudioFormat;

import java.text.SimpleDateFormat;

public class AppConstant {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public static final String KEY_ID_PATIENT_SELECTED = "KEY_ID_PATIENT_SELECTED";

    public static final String KEY_SERVER_URL = "KEY_SERVER_URL";

//    public static final int TIME_RECORD_DEFAULT = 4;
    public static final int TIME_RECORD_DEFAULT = 4000;

    public static final int RECORDER_SAMPLERATE = 8000;
    public static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    public static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    public static final int NUMBER_CHANNELS = 1;                       // using 2 byte
    public static final int BIT_PER_SAMPLE = 16;                       // using 2 byte

    public static final int BUFFER_SIZE_DEFAULT = 4096;
}
