package kr.ac.solbridge.student.hyoon.dogcareproject;

import android.provider.BaseColumns;

public final class ChattingSQLite {
    private ChattingSQLite() {
    }

    public static class ChattingEntry implements BaseColumns{
        public static final String TABLE_NAME = "chatting";
        public static final String COLUMN_NAME_SENDER = "Sender";
        public static final String COLUMN_NAME_MSG = "msg";
        //public static final String COLUMN_NAME_RECEIVER = "msg";

    }
}
