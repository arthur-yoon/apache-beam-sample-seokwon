package com.seokwon.sample.util;

import com.seokwon.common.ConnectionPoolProvider;
import com.seokwon.common.CustomOptions;
import com.seokwon.common.DataSourceAuthDto;
import com.seokwon.common.slack.SlackChannel;
import com.seokwon.common.slack.SlackMessage;
import com.seokwon.common.slack.SlackSender;
import com.seokwon.sample.domain.PersonBox;
import com.seokwon.sample.domain.SamplePerson;
import com.seokwon.sample.enums.BoxType;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.jdbc.JdbcIO;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.joda.time.DateTime;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

@Slf4j
public class SampleUtils implements PreparedThings, BoxAndWhisker {
    /**
     * Utility classes, which are collections of static members, are not meant to be instantiated.
     * Even abstract utility classes, which can be extended, should not have public constructors.
     * Java adds an implicit public constructor to every class which does not define at least one explicitly.
     * Hence, at least one non-public constructor should be defined.
     */
    private SampleUtils() {
        throw new IllegalStateException("Utility class");
    }


    public static String stringJoiner(TreeSet<String> stringSet) {
        return stringSet != null ? String.join(DELEMETER, stringSet) : null;
    }

    public static DataSource getDataSource(Pipeline pipeline) {
        DataSourceAuthDto dataSourceAuthDto = ConnectionPoolProvider.getDataSourceAuthDto(pipeline.getOptions().as(CustomOptions.class).getActiveProfile());
        return ConnectionPoolProvider.getInstance(dataSourceAuthDto).getDataSource();
    }


    public static PCollection<KV<String, SamplePerson>> extractBeamSamplePerson(Pipeline pipeline, String selectQuery) {
        String profile = pipeline.getOptions().as(CustomOptions.class).getActiveProfile();
        DataSourceAuthDto dataSourceAuthDto = ConnectionPoolProvider.getDataSourceAuthDto(profile);
        return pipeline.apply("Data Load", JdbcIO.<KV<String, SamplePerson>>read()
                .withDataSourceProviderFn(input -> ConnectionPoolProvider.getInstance(dataSourceAuthDto).getDataSource())
                .withQuery(selectQuery)
                .withRowMapper(SamplePerson::from)
                .withCoder(KvCoder.of(StringUtf8Coder.of(), SerializableCoder.of(SamplePerson.class))));
    }

    public static PDone loadPersonBox(PCollection<PersonBox> personBoxPCollection, String queryString, BoxType boxType) {
        String profile = personBoxPCollection.getPipeline().getOptions().as(CustomOptions.class).getActiveProfile();
        DataSourceAuthDto dataSourceAuthDto = ConnectionPoolProvider.getDataSourceAuthDto(profile);
        return
                personBoxPCollection.apply("load person_box", JdbcIO.<PersonBox>write()
                        .withDataSourceProviderFn(input -> ConnectionPoolProvider.getInstance(dataSourceAuthDto).getDataSource())
                        .withStatement(queryString)
                        .withPreparedStatementSetter((element, preparedStatement) -> {
                            int parameterIndex = 1;
                            // insert
                            preparedStatement.setString(parameterIndex++, element.getKey() + "__" + boxType.getType());
                            preparedStatement.setString(parameterIndex++, element.getFrom());
                            preparedStatement.setString(parameterIndex++, element.getTo());
                            preparedStatement.setInt(parameterIndex++, element.getUserId());
                            preparedStatement.setString(parameterIndex++, boxType.getType());

                            preparedStatement.setString(parameterIndex++, stringJoiner(element.getAmountSet()));
                            preparedStatement.setBigDecimal(parameterIndex++, element.getAmountTotal());
                            preparedStatement.setTimestamp(parameterIndex++, element.getLastCompleted());

                            // box
                            preparedStatement.setBigDecimal(parameterIndex++, element.getQ1());
                            preparedStatement.setBigDecimal(parameterIndex++, element.getQ2());
                            preparedStatement.setBigDecimal(parameterIndex++, element.getQ3());
                            preparedStatement.setBigDecimal(parameterIndex++, element.getIqr());
                            preparedStatement.setBigDecimal(parameterIndex++, element.getMin());
                            preparedStatement.setBigDecimal(parameterIndex++, element.getMax());
                            preparedStatement.setBigDecimal(parameterIndex++, element.getMean());
                            preparedStatement.setBigDecimal(parameterIndex++, element.getSd());
                            preparedStatement.setInt(parameterIndex++, element.getTotalCount());

                            preparedStatement.setString(parameterIndex++, stringJoiner(element.getOverMaxTransactionIdSet()));

                            // update

                            preparedStatement.setString(parameterIndex++, stringJoiner(element.getAmountSet()));
                            preparedStatement.setBigDecimal(parameterIndex++, element.getAmountTotal());
                            preparedStatement.setTimestamp(parameterIndex++, element.getLastCompleted());

                            // box
                            preparedStatement.setBigDecimal(parameterIndex++, element.getQ1());
                            preparedStatement.setBigDecimal(parameterIndex++, element.getQ2());
                            preparedStatement.setBigDecimal(parameterIndex++, element.getQ3());
                            preparedStatement.setBigDecimal(parameterIndex++, element.getIqr());
                            preparedStatement.setBigDecimal(parameterIndex++, element.getMin());
                            preparedStatement.setBigDecimal(parameterIndex++, element.getMax());
                            preparedStatement.setBigDecimal(parameterIndex++, element.getMean());
                            preparedStatement.setBigDecimal(parameterIndex++, element.getSd());
                            preparedStatement.setInt(parameterIndex++, element.getTotalCount());

                            preparedStatement.setString(parameterIndex, stringJoiner(element.getOverMaxTransactionIdSet()));
                        })
                );
    }

    public static void sendSlackWithUnDoneState(CustomOptions options, PipelineResult.State state, String textMessage) {
        if (!state.equals(PipelineResult.State.DONE)) {
            SlackSender slackSender = new SlackSender();
            slackSender.send(SlackChannel.BEAM_ALERT, SlackMessage.builder().username("[" + options.getActiveProfile() + "] Beam").text(textMessage + "(" + state + ") end: " + DateTime.now()).build());
        }
    }

    public static class TransformSamplePersonKvFnTotal extends DoFn<KV<String, Iterable<SamplePerson>>, KV<String, List<SamplePerson>>> {
        @ProcessElement
        public void processElement(@Element KV<String, Iterable<SamplePerson>> element, OutputReceiver<KV<String, List<SamplePerson>>> receiver) {
            List<SamplePerson> samplePersonDtoList = new ArrayList<>();
            Objects.requireNonNull(element.getValue()).forEach(samplePersonDtoList::add);
            receiver.output(KV.of(element.getKey(), samplePersonDtoList));
        }
    }

    public static class TransformSamplePersonFnTotal extends DoFn<KV<String, List<SamplePerson>>, PersonBox> {
        @ProcessElement
        public void processElement(@Element KV<String, List<SamplePerson>> element, OutputReceiver<PersonBox> receiver) {
            List<SamplePerson> samplePersonDtoList = Objects.requireNonNull(element.getValue());
            String key = element.getKey();
            if (!samplePersonDtoList.isEmpty() && samplePersonDtoList.size() > MIN_SIZE) {
                receiver.output(BoxAndWhisker.box(BoxType.TOTAL, samplePersonDtoList, key));
            }
        }
    }
}
