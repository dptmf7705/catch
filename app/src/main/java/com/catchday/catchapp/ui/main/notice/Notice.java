package com.catchday.catchapp.ui.main.notice;

import java.util.Objects;

public class Notice {

    private int imageResId;

    public Notice(int imageResId) {
        this.imageResId = imageResId;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notice notice = (Notice) o;
        return imageResId == notice.imageResId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageResId);
    }
}
