package com.mazxn.korpux.persistence;

import java.io.Serializable;

public class Entry implements Serializable {
    public String URL;
    public Integer TotalCount;
    public Integer AsTitle;
    public Integer AsHeader1;
    public Integer AsHeader2;
    public Integer AsHeader3;
    public Integer AsHeader4;
    public Integer AsHeader5;
    public Integer AsHeader6;

    public Entry(String URL, Integer TotalCount, Integer AsTitle, Integer AsHeader1, Integer AsHeader2, Integer AsHeader3, Integer AsHeader4, Integer AsHeader5, Integer AsHeader6) {
        this.URL = URL;
        this.TotalCount = TotalCount;
        this.AsTitle = AsTitle;
        this.AsHeader1 = AsHeader1;
        this.AsHeader2 = AsHeader2;
        this.AsHeader3 = AsHeader3;
        this.AsHeader4 = AsHeader4;
        this.AsHeader5 = AsHeader5;
        this.AsHeader6 = AsHeader6;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Entry)) {
            return false;
        }
        Entry e = (Entry) o;
        return e.URL.equals(this.URL) && e.TotalCount.equals(this.TotalCount) && e.AsTitle.equals(this.AsTitle) && e.AsHeader1.equals(this.AsHeader1) && e.AsHeader2.equals(this.AsHeader2) && e.AsHeader3.equals(this.AsHeader3) && e.AsHeader4.equals(this.AsHeader4) && e.AsHeader5.equals(this.AsHeader5) && e.AsHeader6.equals(this.AsHeader6);
    }
}
