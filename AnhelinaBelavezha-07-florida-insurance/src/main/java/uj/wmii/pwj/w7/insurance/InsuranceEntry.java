package uj.wmii.pwj.w7.insurance;

import java.math.BigDecimal;

public class InsuranceEntry {
    private final String country;
    private final BigDecimal tiv2011;
    private final BigDecimal tiv2012;

    public InsuranceEntry(String args) {
        String[] fields = args.split(",");
        this.country = fields[2];
        this.tiv2011 = new BigDecimal(fields[7]);
        this.tiv2012 = new BigDecimal(fields[8]);
    }

    public String getCountry() {
        return country;
    }

    public BigDecimal getTiv2011() {
        return tiv2011;
    }

    public BigDecimal getTiv2012() {
        return tiv2012;
    }
}
