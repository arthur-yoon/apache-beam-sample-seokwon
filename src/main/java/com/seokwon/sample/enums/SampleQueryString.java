package com.seokwon.sample.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SampleQueryString {

    SELECT_PERSON_BOX_BATCH("SELECT distinct from, to, user_id, amount, transaction_id, paid FROM transaction;"),

    INSERT_OR_UPDATE_PERSON_BOX(
            "INSERT INTO person_box\n" +
                    "SET " +
                    "grouping_key=?, from=?, to=?, user_id=?, box_type=?, \n" +
                    "amount_set=?, amount_total=?, last_paid=?,\n" +
                    "q1=?, q2=?, q3=?, iqr=?, min=?, max=?, mean=?, sd=?, total_count=?,\n" +
                    "over_max_transaction_id_set=? \n" +
                    "ON DUPLICATE KEY UPDATE\n" +
                    "amount_set=?, amount_total=?, last_paid=?,\n" +
                    "q1=?, q2=?, q3=?, iqr=?, min=?, max=?, mean=?, sd=?, total_count=?,\n" +
                    "over_max_transaction_id_set=? ;");
    private String queryString;
}
