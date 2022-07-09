package com.example.app_server;

import android.os.Parcel;
import android.os.Parcelable;

public class UsersModal implements Parcelable {
    private String userId;
    private String email;
    private String nickname;

    public UsersModal(String userId, String email, String nickname) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
    }
    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public UsersModal(Parcel in) {
        String[] data = new String[3];

        in.readStringArray(data);
        this.userId = data[0];
        this.email = data[1];
        this.nickname = data[2];
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] data = new String[] {
                this.userId,
                this.email,
                this.nickname
        };
        dest.writeStringArray(data);
    };

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public UsersModal createFromParcel(Parcel in) { return new UsersModal(in); }
        public UsersModal[] newArray(int size) { return new UsersModal[size]; }
    };
}
