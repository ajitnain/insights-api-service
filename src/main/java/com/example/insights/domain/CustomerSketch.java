package com.example.insights.domain;

import org.apache.datasketches.theta.CompactSketch;
import org.apache.datasketches.theta.SetOperationBuilder;
import org.apache.datasketches.theta.Union;
import org.apache.datasketches.theta.UpdateSketch;

import java.util.Collection;
import java.util.List;

/** Compact sketch of the customers seen in one row. */
public final class CustomerSketch {

    // 4096 entries. Exact up to about 7,700 customers, then within a few percent.
    private static final int NOMINAL_ENTRIES = 4096;

    private final CompactSketch sketch;

    private CustomerSketch(CompactSketch sketch) {
        this.sketch = sketch;
    }

    public static CustomerSketch of(Collection<String> customerIds) {
        UpdateSketch sketch = UpdateSketch.builder().setNominalEntries(NOMINAL_ENTRIES).build();
        customerIds.forEach(sketch::update);
        return new CustomerSketch(sketch.compact());
    }

    public static CustomerSketch empty() {
        return of(List.of());
    }

    public static CustomerSketch merge(Collection<CustomerSketch> parts) {
        if (parts.isEmpty()) {
            return empty();
        }
        Union union = new SetOperationBuilder().setNominalEntries(NOMINAL_ENTRIES).buildUnion();
        parts.forEach(part -> union.union(part.sketch));
        return new CustomerSketch(union.getResult());
    }

    public CustomerCount toCount() {
        long estimate = Math.round(sketch.getEstimate());
        if (estimate == 0) {
            return CustomerCount.of(0, 0);
        }
        double error = Math.abs(sketch.getUpperBound(2) - estimate) / estimate;
        return CustomerCount.of(estimate, error);
    }
}
