package edu.ucsb.cs.cs184.wetube;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Chat extends AbstractChat {
    private String name;
    private String message;
    private String uid;
//    private Date mTimestamp;

    public Chat() {
        // Needed for Firebase
    }

    public Chat(@Nullable String name, @Nullable String message, @NonNull String uid) {
        this.name = name;
        this.message = message;
        this.uid = uid;
    }

    @Override
    @Nullable
    public String getName() {
        return name;
    }

    @Override
    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Override
    @Nullable
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    @Override
    @NonNull
    public String getUid() {
        return uid;
    }

    @Override
    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

//    @ServerTimestamp
//    @Nullable
//    public Date getTimestamp() {
//        return mTimestamp;
//    }
//
//    public void setTimestamp(@Nullable Date timestamp) {
//        mTimestamp = timestamp;
//    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chat chat = (Chat) o;

//        return mTimestamp.equals(chat.mTimestamp)
//                &&
          return uid.equals(chat.uid)
                && (name == null ? chat.name == null : name.equals(chat.name))
                && (message == null ? chat.message == null : message.equals(chat.message));
    }

    @Override
    public int hashCode() {
        int result = name == null ? 0 : name.hashCode();
        result = 31 * result + (message == null ? 0 : message.hashCode());
        result = 31 * result + uid.hashCode();
//        result = 31 * result + mTimestamp.hashCode();
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "Chat{" +
                "mName='" + name + '\'' +
                ", message='" + message + '\'' +
                ", uid='" + uid + '\'' +
//                ", mTimestamp=" + mTimestamp +
                '}';
    }


}
