package com.seokwon.sample;

import com.seokwon.common.CustomOptions;
import com.seokwon.common.slack.SlackChannel;
import com.seokwon.sample.analyzer.Analyzer;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

@Slf4j
public class PersonBoxAnalyze {
    public static void main(String[] args) {
        // Beam option setting
        CustomOptions options = PipelineOptionsFactory.fromArgs(args).as(CustomOptions.class);
        Pipeline pipeline = Pipeline.create(options);
        // extract
        Analyzer.analyzerRunner(options, pipeline, SlackChannel.BEAM_ALERT, "PersonBoxAnalyze");
    }
}