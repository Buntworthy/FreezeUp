package com.cutsquash.freezeup;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by Justin on 28/11/2015.
 */
public class FullTestSuite extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(FullTestSuite.class)
                .includeAllPackagesUnderHere().build();
    }

    public FullTestSuite() {
        super();
    }
}
