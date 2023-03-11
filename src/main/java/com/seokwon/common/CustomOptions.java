package com.seokwon.common;

import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;

public interface CustomOptions extends DataflowPipelineOptions {
    @Description("activeProfile")
    @Default.String("local")
    String getActiveProfile();

    void setActiveProfile(String activeProfile);

    @Description("daily")
    @Default.Boolean(true)
    boolean getDaily();

    void setDaily(boolean daily);
}
