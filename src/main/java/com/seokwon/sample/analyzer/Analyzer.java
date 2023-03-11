package com.seokwon.sample.analyzer;

import com.seokwon.common.CustomOptions;
import com.seokwon.common.slack.SlackChannel;
import com.seokwon.common.slack.SlackMessage;
import com.seokwon.common.slack.SlackSender;
import com.seokwon.sample.domain.PersonBox;
import com.seokwon.sample.domain.SamplePerson;
import com.seokwon.sample.enums.BoxType;
import com.seokwon.sample.enums.SampleQueryString;
import com.seokwon.sample.util.SampleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;

import java.util.List;

@Slf4j
public class Analyzer {
    private Analyzer() {
        throw new UnsupportedOperationException();
    }

    public static void analyzerRunner(CustomOptions options, Pipeline pipeline, SlackChannel slackChannel, String analyzerName) {
        samplePersonStatsAnalysis(pipeline);
        runWithSlack(options, pipeline, slackChannel, analyzerName);
    }

    private static void runWithSlack(final CustomOptions options, final Pipeline pipeline, final SlackChannel slackChannel, final String analyzerName) {
        String username = "[" + options.getActiveProfile() + "] Beam";
        SlackSender slackSender = new SlackSender();

        try {
            PipelineResult.State state = pipeline.run().waitUntilFinish();
            SampleUtils.sendSlackWithUnDoneState(options, state, analyzerName);
        } catch (Exception exc) {
            log.error("e.getMessage : {}", exc.getMessage());
            slackSender.send(slackChannel, SlackMessage.builder().username(username + analyzerName).text(exc.getMessage()).build());
        }
    }

    public static void samplePersonStatsAnalysis(Pipeline pipeline) {
        // extract
        PCollection<KV<String, SamplePerson>> loadData = SampleUtils.extractBeamSamplePerson(pipeline, SampleQueryString.SELECT_PERSON_BOX_BATCH.getQueryString());
        // transform1 : 추출된 데이터를 키로 그룹화 변환
        PCollection<KV<String, Iterable<SamplePerson>>> groupingLoadData = loadData.apply("Grouping-1st", GroupByKey.create());
        // transform2 : 그룹화된 데이터에서 키-분석값으로 변환 후 저장
        PCollection<KV<String, List<SamplePerson>>> parsonTotal = groupingLoadData.apply(ParDo.of(new SampleUtils.TransformSamplePersonKvFnTotal()));
        PCollection<PersonBox> personBoxPCollection = parsonTotal.apply(ParDo.of(new SampleUtils.TransformSamplePersonFnTotal()));
        SampleUtils.loadPersonBox(personBoxPCollection, SampleQueryString.INSERT_OR_UPDATE_PERSON_BOX.getQueryString(), BoxType.TOTAL);
    }
}
