package com.seokwon.sample.util;

import com.seokwon.sample.domain.PersonBox;
import com.seokwon.sample.domain.SamplePerson;
import com.seokwon.sample.enums.BoxType;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public interface BoxAndWhisker extends PreparedThings {
    static PersonBox box(final BoxType boxType, final List<SamplePerson> samplePersonDtoList, final String key) {
        BigDecimal whiskerByBoxType = boxType.getWhisker();
        PersonBox personBox = new PersonBox();
        TreeSet<String> usdAmountSet = new TreeSet<>();
        personBox.setKey(key);
        personBox.setFrom(samplePersonDtoList.get(ZERO).getFrom());
        personBox.setTo(samplePersonDtoList.get(ZERO).getTo());
        personBox.setUserId(samplePersonDtoList.get(ZERO).getUserId());

        BigDecimal[] personUsdAmountSum = {BigDecimal.ZERO};
        List<BigDecimal> personUsdAmounts = new ArrayList<>();

        samplePersonDtoList.forEach(samplePerson -> {
            usdAmountSet.add(samplePerson.getAmount().toPlainString());
            personUsdAmountSum[ZERO] = personUsdAmountSum[ZERO].add(samplePerson.getAmount());
            personUsdAmounts.add(samplePerson.getAmount());
        });
        personBox.setAmountSet(usdAmountSet);
        personBox.setAmountTotal(personUsdAmountSum[ZERO]);
        personBox.setLastCompleted(samplePersonDtoList.stream().map(SamplePerson::getCompleted).max(java.sql.Timestamp::compareTo).get());

        int size = personUsdAmounts.size();
        int q1 = size / 4; // 1분위번째 인덱스 구함
        personBox.setTotalCount(size);
        Collections.sort(personUsdAmounts);
        personBox.setQ1(personUsdAmounts.get(q1));  // 중앙값 아래애서의 중앙값 (1분위번째의 값)
        personBox.setQ2(personUsdAmounts.get(2 * q1));  // 중앙값 (2분위번째의 값)
        personBox.setQ3(personUsdAmounts.get(3 * q1));  // 중앙값 위에서의 중앙값 (3분위번째의 값)
        BigDecimal iqr = personBox.getQ3().subtract(personBox.getQ1()); // Q3 - Q1, 클수록 분포가 높음
        BigDecimal whisker = iqr.multiply(whiskerByBoxType);    // (WHISKER x iqr)
        personBox.setIqr(iqr);
        personBox.setMax(personBox.getQ3().add(whisker));   // Q1 - (whisker x iqr), 아웃라이어 제외한 최소값
        personBox.setMin(personBox.getQ1().subtract(whisker));     // Q3 + (1.5 x iqr), 아웃라이어 제외한 최대값

        Mean mean = new Mean();
        StandardDeviation sd = new StandardDeviation(false);

        // min, max 이내의 값들로 평균과 표준편차 구함.
        double[] doubles = personUsdAmounts.stream()
                .filter(personUsdAmount -> personBox.getMin().doubleValue() <= personUsdAmount.doubleValue() && personUsdAmount.doubleValue() <= personBox.getMax().doubleValue())
                .mapToDouble(BigDecimal::doubleValue).toArray();
        personBox.setMean(doubles.length == ZERO ? BigDecimal.ZERO : BigDecimal.valueOf(mean.evaluate(doubles)));
        personBox.setSd(doubles.length == ZERO ? BigDecimal.ZERO : BigDecimal.valueOf(sd.evaluate(doubles)));

        TreeSet<String> overMaxTransactionIdSet = samplePersonDtoList.stream()
                .filter(samplePerson -> samplePerson.getAmount().compareTo(personBox.getMax()) > ZERO)
                .map(SamplePerson::getTransactionId)
                .collect(Collectors.toCollection(TreeSet::new));
        personBox.setOverMaxTransactionIdSet(overMaxTransactionIdSet.isEmpty() ? null : overMaxTransactionIdSet);
        return personBox;
    }
}
