package goldenbrother.gbmobile.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Patient implements  Parcelable {
    public int id;//xutil中使用
    public String name;

    public boolean gender;

    public String date;
    public int age;
    public String mobil;
    public String id1;//身份證號
    public String jiuZhen_date;
    public String keshi;
    public String bianHao_type;
    public String biaoHao;
    public String Tel,email,address,job,contacter,interducer,note;
    public String bloodType;

    public String customerNo;
    public String flaborNo;
    public String roomID;
    public String dormID;
    public String centerDirectorID;

    public Patient(Parcel in) {
        id = in.readInt();
        name = in.readString();
        gender = in.readByte() != 0;
        date = in.readString();
        age = in.readInt();
        mobil = in.readString();
        id1 = in.readString();
        jiuZhen_date = in.readString();
        keshi = in.readString();
        bianHao_type = in.readString();
        biaoHao = in.readString();
        Tel = in.readString();
        email = in.readString();
        address = in.readString();
        job = in.readString();
        contacter = in.readString();
        interducer = in.readString();
        note = in.readString();
        bloodType = in.readString();
        customerNo = in.readString();
        flaborNo = in.readString();
        roomID = in.readString();
        dormID = in.readString();
        centerDirectorID = in.readString();
    }

    public Patient() {

    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMobil() {
        return mobil;
    }

    public void setMobil(String mobil) {
        this.mobil = mobil;
    }

    public String getId1() {
        return id1;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }

    public String getJiuZhen_date() {
        return jiuZhen_date;
    }

    public void setJiuZhen_date(String jiuZhen_date) {
        this.jiuZhen_date = jiuZhen_date;
    }

    public String getKeshi() {
        return keshi;
    }

    public void setKeshi(String keshi) {
        this.keshi = keshi;
    }

    public String getBianHao_type() {
        return bianHao_type;
    }

    public void setBianHao_type(String bianHao_type) {
        this.bianHao_type = bianHao_type;
    }

    public String getBiaoHao() {
        return biaoHao;
    }

    public void setBiaoHao(String biaoHao) {
        this.biaoHao = biaoHao;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getContacter() {
        return contacter;
    }

    public void setContacter(String contacter) {
        this.contacter = contacter;
    }

    public String getInterducer() {
        return interducer;
    }

    public void setInterducer(String interducer) {
        this.interducer = interducer;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getFlaborNo() {
        return flaborNo;
    }

    public void setFlaborNo(String flaborNo) {
        this.flaborNo = flaborNo;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getDormID() {
        return dormID;
    }

    public void setDormID(String dormID) {
        this.dormID = dormID;
    }

    public String getCenterDirectorID() {
        return centerDirectorID;
    }

    public void setCenterDirectorID(String centerDirectorID) {
        this.centerDirectorID = centerDirectorID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeByte((byte) (gender ? 1 : 0));
        dest.writeString(date);
        dest.writeInt(age);
        dest.writeString(mobil);
        dest.writeString(id1);
        dest.writeString(jiuZhen_date);
        dest.writeString(keshi);
        dest.writeString(bianHao_type);
        dest.writeString(biaoHao);
        dest.writeString(Tel);
        dest.writeString(email);
        dest.writeString(address);
        dest.writeString(job);
        dest.writeString(contacter);
        dest.writeString(interducer);
        dest.writeString(note);
        dest.writeString(bloodType);
        dest.writeString(customerNo);
        dest.writeString(flaborNo);
        dest.writeString(roomID);
        dest.writeString(dormID);
        dest.writeString(centerDirectorID);
    }
}

