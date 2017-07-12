package goldenbrother.gbmobile.model;

/**
 * Created by haojun on 2017/7/11.
 */

public class Medical {
    private int mtrsno;
    private Patient patient;
    private String customerNo;
    private String customerName;
    private String flaborNo;
    private String flaborName;
    private String recordDate;

    public int getMtrsno() {
        return mtrsno;
    }

    public void setMtrsno(int mtrsno) {
        this.mtrsno = mtrsno;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getFlaborNo() {
        return flaborNo;
    }

    public void setFlaborNo(String flaborNo) {
        this.flaborNo = flaborNo;
    }

    public String getFlaborName() {
        return flaborName;
    }

    public void setFlaborName(String flaborName) {
        this.flaborName = flaborName;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }
}
