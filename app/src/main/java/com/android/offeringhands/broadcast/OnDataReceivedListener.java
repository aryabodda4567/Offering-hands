package com.android.offeringhands.broadcast;

import java.util.ArrayList;
import java.util.Map;

public interface OnDataReceivedListener {
    void onDataReceived(ArrayList<Map<String, String>> arrayList);
}
