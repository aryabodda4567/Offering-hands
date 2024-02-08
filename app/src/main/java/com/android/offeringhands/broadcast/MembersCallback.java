package com.android.offeringhands.broadcast;

import java.util.ArrayList;


public interface MembersCallback {

    void onMembersLoaded(ArrayList<String> userIds);

    void onMembersError(String errorMessage);
}
