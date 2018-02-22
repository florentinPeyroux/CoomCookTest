package com.eservices.floamnx.coomcook.interfaces;

import android.os.Bundle;
import android.support.v4.app.Fragment;
public interface FragmentManagerInterface {

    void showFragment(Fragment sFragment);

    void showFragment(Fragment sFragment, Bundle sBundle);
}