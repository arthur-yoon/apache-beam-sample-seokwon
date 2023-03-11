package com.seokwon.sample.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.beam.sdk.values.KV;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SamplePerson implements Serializable {
    private static final String TO = "to";
    private static final String TRANSACTION_ID = "transaction_id";
    private static final String USER_ID = "user_id";
    private static final String FROM = "from";
    private static final String COMPLETED = "completed";
    private static final String AMOUNT = "amount";
    private String key; // from-to-userId
    private String from;
    private String to;
    private int userId;
    private BigDecimal amount;
    private String transactionId;
    private Timestamp completed;

    public static KV<String, SamplePerson> from(ResultSet resultSet) throws SQLException {
        SamplePerson samplePerson = new SamplePerson(null, resultSet.getString(FROM), resultSet.getString(TO), resultSet.getInt(USER_ID), resultSet.getBigDecimal(AMOUNT), resultSet.getString(TRANSACTION_ID), resultSet.getTimestamp(COMPLETED));
        samplePerson.setKey(samplePerson.getGroupingKey());
        return KV.of(samplePerson.getKey(), samplePerson);
    }

    public String getGroupingKey() {
        return getFrom() + "-" + getTo() + "-" + getUserId();
    }
}
